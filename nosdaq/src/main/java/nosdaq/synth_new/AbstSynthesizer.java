package nosdaq.synth_new;

import jdk.jshell.execution.Util;
import nosdaq.RunOption;
import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.Expression;
import nosdaq.ast.pred.Exists;
import nosdaq.ast.pred.Not;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.schema.Type;
import nosdaq.ast.stage.Match;
import nosdaq.ast.stage.Project;
import nosdaq.ast.stage.Stage;
import nosdaq.synth_new.deduce.DeduceTypeSMT;
import nosdaq.synth_new.rtype.RType;
import nosdaq.synth_new.sketch.*;
import nosdaq.trans.DSLTranslator;
import nosdaq.trans.MongoDBTranslator;
import nosdaq.utils.EXP_MODE;
import nosdaq.utils.Helper;
import org.bson.Document;
import verify.MongoVerifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

import static nosdaq.Main.EXPERIMENT;
import static nosdaq.Main.getOutputFolder;

public class AbstSynthesizer implements ISynthesizer {
    private RunOption runOption;
    private Schema inputSchema;
    private Schema outputSchema;
    private List<Schema> foreignSchema;
    private List<Example> examples;
    private List<String> constants;
    private List<TStage> stagePriority;
    private AbstractCol abstInput;
    private AbstractCol abstOutput;
    private FillPolicy fillPolicy;

    private void doInit(
            RunOption runOption,
            Schema inputSchema,
            Schema outputSchema,
            List<Example> examples,
            List<String> constants,
            List<Schema> foreignSchema) {
        this.runOption = runOption;
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
        this.foreignSchema = foreignSchema;
        this.examples = examples;
        this.constants = constants;
        this.abstInput = new AbstractCol(examples.stream().map(Example::getInput)
                .collect(Collectors.toList()), inputSchema);
        this.abstOutput = new AbstractCol(examples.stream().map(Example::getOutput)
                .collect(Collectors.toList()), outputSchema);
        this.stagePriority = SketchPolicy.analyzePriority(abstInput, abstOutput, foreignSchema);
        this.fillPolicy = new FillPolicy(runOption, examples,
                inputSchema, outputSchema,
                this.abstInput, this.abstOutput,
                constants, foreignSchema);
    }


    @Override
    public Program synthesize(
            RunOption runOption,
            Schema inputSchema,
            Schema outputSchema,
            List<Example> examples,
            List<String> constants,
            List<Schema> foreignSchema) {

        doInit(runOption, inputSchema, outputSchema, examples, constants, foreignSchema);
        PriorityQueue<Sketch> workList = new PriorityQueue<>();

        writeBenchStats();
        long numSketches = 0;
        long numCompletedPrograms = 0;
        long startTime = System.currentTimeMillis();

        if (EXPERIMENT.equals(EXP_MODE.SIZE_IMPACT)) {
            // Special case for bad examples in size impact experiment. If all outputs are `[]` return Match(false)
            if (this.outputSchema.getAttrSchemaSet().isEmpty()) {
                Program program = new Program(
                        new Aggregate(List.of(new Match(new Not(new Exists(Helper.constructAp("_id")))))));
                if (verify(program)) {
                    writeResult(program, 2, 1, System.currentTimeMillis() - startTime);
                    return program;
                }
            }
        }

        workList.offer(new SketchColNode(abstInput));
        while (!workList.isEmpty()) {
            Sketch sketch = workList.poll();
            numSketches += 1;
            if (deduceType(sketch)) {
                List<Program> programs = fillSketch(sketch);
                numCompletedPrograms += programs.size();
                for (Program program : programs) {
                    if (verify(program)) {
                        program = simplify(program);
                        assert verify(program);
                        writeResult(program, numSketches, numCompletedPrograms, System.currentTimeMillis() - startTime);
                        return program;
                    }
                }
            }
            workList.addAll(refine(sketch));
        }
        return null;
    }


    private List<Program> fillSketch(Sketch sketch) {
        if (!checkFillSketchForMatch(sketch)) {
            return new ArrayList<>();
        }

        List<List<Stage>> programs = fillPolicy.fill(sketch, new ArrayList<>());
        return programs.stream()
                .map(p -> new Program(new Aggregate(p)))
                .collect(Collectors.toList());
    }


    private boolean checkFillSketchForMatch(Sketch sketch) {
        boolean hasMatch = false;
        Sketch ptr = sketch;
        while (!ptr.isAbstractCol()) {
            TStage tStage = ((SketchStageNode) ptr).getStage();
            if (tStage == TStage.MATCH) {
                hasMatch = true;
                break;
            }
            ptr = ptr.getChildren().get(0);
        }
        return hasMatch || this.constants.isEmpty();
    }


    private boolean deduceType(Sketch sketch) {
//        System.out.println(sketch);
        List<List<Document>> inputExamples = this.examples.stream().map(Example::getInput).toList();
        List<List<Document>> outputExamples = this.examples.stream().map(Example::getOutput).toList();
        RType inputType = new RType(inputExamples, inputSchema);
        RType outputType = new RType(outputExamples, outputSchema);
        return DeduceTypeSMT.deduce(this.runOption, sketch, inputType, outputType, this.foreignSchema);
    }

    private boolean verify(Program program) {
//        System.out.println(MongoDBTranslator.translate(program));
        MongoVerifier verifier = new MongoVerifier();
        return verifier.verify(program, inputSchema, examples);
    }

    /**
     *
     * @param sketch
     * @return
     * If deduce fails and is due to size cannot satisfy
     * add match/unwind/group accordingly
     * If number of nodes or degree change, add unwind/project/addFields/lookup
     * accordingly
     *
     * It has been done with stagePriority
     */
    private List<Sketch> refine(Sketch sketch) {
        List<Sketch> result = new ArrayList<>();
        if (sketch.isAbstractCol()) {
            for (TStage stage : stagePriority) {
                result.add(new SketchStageNode(stage,
                        Arrays.asList(sketch, new SketchHoleNode())));
            }
            return result;
        }

        for (TStage stage : stagePriority) {
            Sketch newSketch = sketch.copy();
            Sketch parent = newSketch;
            while (!parent.getChildren().get(0).isAbstractCol()) {
                parent = parent.getChildren().get(0);
            }
            Sketch abstractCol = parent.getChildren().get(0);
            parent.getChildren()
                    .set(0, new SketchStageNode(stage,
                            Arrays.asList(abstractCol, new SketchHoleNode())));
            result.add(newSketch);
        }
        return result;
    }


    private void writeResult(Program program, long numSketches, long numCompletedPrograms, long timeMs) {

        try {
            String benchmarkName = this.inputSchema.getSchemaName();
            BufferedWriter bw = new BufferedWriter(new FileWriter(getOutputFolder(this.runOption) + "/maineval/" + benchmarkName + ".csv"));
            bw.write(benchmarkName + ", ");
            bw.write((timeMs / 1000.0) + ", ");
            bw.write(numSketches + ", ");
            bw.write(numCompletedPrograms + ", ");
            bw.write(getNumStages(program) + ", ");
            bw.write(program.size() + "");
            bw.write("\n");
            bw.close();
        } catch (Exception ignored){
        }

        try {
            String benchmarkName = this.inputSchema.getSchemaName();
            BufferedWriter bw = new BufferedWriter(new FileWriter(getOutputFolder(this.runOption) + "/programs/" + benchmarkName + ".txt"));
            bw.write("#" + benchmarkName + "\n");
            bw.write(MongoDBTranslator.translate(program) + "\n");
            bw.write(DSLTranslator.translate(program) + "\n");
            bw.write("\n");
            bw.close();
        } catch (Exception ignored) {

        }
    }

    private void writeBenchStats() {
        try {
            String benchmarkName = this.inputSchema.getSchemaName();
            BufferedWriter bw = new BufferedWriter(new FileWriter(getOutputFolder(this.runOption) + "/benchstats/" + benchmarkName + ".csv"));
            bw.write(benchmarkName + ", ");
            bw.write(inputSchema.getAttrSchemas().size() + ", ");
            bw.write(inputSchema.getDepth() + ", ");
            bw.write(getNumListAttrs() + ", ");
            bw.write(getNumNestedAttrs() + ", ");
            bw.write(examples.size() + ", ");
            bw.write(avgDocNumIn() + ", ");
            bw.write(avgDocNumOut() + ", ");
            bw.write(constants.size() + "");
            bw.write("\n");
            bw.close();
        } catch (Exception ignored){
        }
    }




    private double avgDocNumIn() {
        int sum = 0;
        for (Example example : examples) {
            sum += example.getInput().size();
        }
        return ((double) sum) / examples.size();
    }

    private double avgDocNumOut() {
        int sum = 0;
        for (Example example : examples) {
            sum += example.getOutput().size();
        }
        return ((double) sum) / examples.size();
    }

    private int getNumListAttrs() {
        int num = 0;
        for (AttrSchema attrSchema : this.inputSchema.getAttrSchemaSet()) {
            if (Utils.isArray(attrSchema)) {
                ++num;
            }
        }
        return num;
    }

    private int getNumNestedAttrs() {
        int num = 0;
        for (AttrSchema attrSchema : this.inputSchema.getAttrSchemaSet()) {
            if (attrSchema.getType() == Type.OBJECT || attrSchema.getType() == Type.OBJECT_ARRAY) {
                ++num;
            }
        }
        return num;
    }

    private int getNumStages(Program program) {
        return ((Aggregate) program.query()).getStages().size();
    }

    // merge continuous project
    public static Program simplify(Program program) {
        List<Stage> stages = ((Aggregate) program.query()).getStages();
        Stack<Stage> stack = new Stack<>();
        for (Stage stage : stages) {
            boolean canMerge = !stack.isEmpty()
                    && (stage instanceof Project)
                    && (stack.peek() instanceof Project)
                    && (((Project) stage).getNewFields().isEmpty());
            if (canMerge) {
                Project addFields = (Project) stack.pop();
                Project project = (Project) stage;
                stack.push(mergeAddFieldsAndProject(addFields, project));
            } else {
                stack.push(stage);
            }
        }
        return new Program(new Aggregate(new ArrayList<>(stack)));
    }


    public static Stage mergeAddFieldsAndProject(Project addFields, Project project) {
        List<AccessPath> accessPaths = new ArrayList<>();

        /* WRONG!!
        Set<AccessPath> existingFields = new HashSet<>(project.getAccessPaths());
        for (AccessPath accessPath : addFields.getAccessPaths()) {
            if (existingFields.contains(accessPath)) {
                accessPaths.add(accessPath);
            }
        }
        for (int i = 0; i < addFields.getNewFields().size(); ++i) {
            if (existingFields.contains(addFields.getNewFields().get(i))) {
                newFields.add(addFields.getNewFields().get(i));
                expressions.add(addFields.getExpressions().get(i));
            }
        }
        */
        // We can assert merged.accessPaths is the subset of project.accessPaths
        for (AccessPath pAccessPath : project.getAccessPaths()) {
            for (AccessPath aAccessPath : addFields.getAccessPaths()) {
                if (pAccessPath.startsWith(aAccessPath)) {
                    accessPaths.add(pAccessPath);
                    break;
                }
            }
        }
        // we can assert merged.newFields is equal to addFields.newFields
        // because we assume new generated fields must be all projected
        List<Expression> expressions = addFields.getExpressions();
        List<AccessPath> newFields = addFields.getNewFields();
        return new Project(accessPaths, expressions, newFields);
    }


}
