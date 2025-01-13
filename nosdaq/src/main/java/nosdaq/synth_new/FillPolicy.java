package nosdaq.synth_new;

import nosdaq.RunOption;
import nosdaq.ast.Example;
import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;
import nosdaq.ast.pred.SizeIs;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.schema.Type;
import nosdaq.ast.stage.*;
import nosdaq.ast.value.*;
import nosdaq.synth_new.deduce.DeduceTypeSMT;
import nosdaq.synth_new.eval.Evaluator;
import nosdaq.synth_new.eval.PredEvaluator;
import nosdaq.synth_new.rtype.RType;
import nosdaq.synth_new.sketch.Sketch;
import nosdaq.synth_new.sketch.SketchStageNode;
import nosdaq.synth_new.sketch.TStage;
import nosdaq.utils.EXP_MODE;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

import static nosdaq.Main.EXPERIMENT;

public class FillPolicy {
    private final RunOption runOption;
    private final List<Example> examples;
    private final Schema inputSchema;
    private final Schema outputSchema;
    private final List<Schema> foreignSchema;
    private final List<String> constants;
    private final AbstractCol abstInput;
    private final AbstractCol abstOutput;

    private final ValueChecker valueChecker;
    public FillPolicy(
            RunOption runOption,
            List<Example> examples,
            Schema inputSchema,
            Schema outputSchema,
            AbstractCol abstInput,
            AbstractCol abstOutput,
            List<String> constants,
            List<Schema> foreignSchema) {
        this.runOption = runOption;
        this.examples = examples;
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
        this.constants = constants;
        this.foreignSchema = foreignSchema;
        this.abstInput = abstInput;
        this.abstOutput = abstOutput;
        this.valueChecker = new ValueChecker(examples);
    }
/*======================================================================================================================*/
    public List<List<Stage>> fill(Sketch sketch, List<TStage> leftPart) {
        if (sketch.isAbstractCol()) {
            return new ArrayList<>(List.of(new ArrayList<>()));
        }

        TStage tStage = ((SketchStageNode) sketch).getStage();
        List<TStage> newLeftPart = new ArrayList<>(leftPart);
        newLeftPart.add(0, tStage);
        List<List<Stage>> partials = fill(sketch.getChildren().get(0), newLeftPart);

        List<List<Stage>> res = new ArrayList<>();
        for (List<Stage> partial : partials) {
            List<Stage> fills = new ArrayList<>();
            List<List<Document>> partialIn = partialEval(partial);
            if (!partialDeduceType(newLeftPart, partialIn)) {
                continue;
            }
            PredEvaluator predEvaluator = new PredEvaluator(this.inputSchema, partialIn);
            switch (tStage) {
                case GROUP -> fills = fillGroup(partialIn, leftPart);
                case MATCH -> fills = fillMatch(partialIn, predEvaluator);
                case LOOKUP -> fills = fillLookup(partialIn);
                case UNWIND -> fills = fillUnwind(partialIn);
                case PROJECT -> fills = fillProject(partialIn);
                case ADD_FIELDS -> fills = fillAddFields(partialIn);
            }
            predEvaluator.destroy();

            for (Stage fill : fills) {
                List<Stage> copied = new ArrayList<>(partial);
                copied.add(fill);
                res.add(copied);
            }
        }
        return res;
    }

    private boolean partialDeduceType(List<TStage> newLeftPart, List<List<Document>> partialIn) {
        if (!checkPartialIn(partialIn)) {
            return false;
        }
        Set<AttrSchema> partialInAttrSchemaSet = SchemaExtractor.extractAttrSchemas(partialIn);
        Schema partialInSchema = new Schema("partial", new ArrayList<>(partialInAttrSchemaSet));
        AbstractCol abstPartialIn =  new AbstractCol(partialIn, partialInSchema);


        RType partialInputType = new RType(partialIn, partialInSchema);
        List<List<Document>> outputExamples = this.examples.stream().map(Example::getOutput).toList();
        RType outputType = new RType(outputExamples, this.outputSchema);
        return DeduceTypeSMT.partialDeduce(this.runOption,
                Utils.createSketch(abstPartialIn, newLeftPart), partialInputType, outputType,
                this.foreignSchema);
    }
/*======================================================================================================================*/
    private List<Stage> fillMatch(List<List<Document>> partialIn, PredEvaluator predEvaluator) {
        // todo: filter by size (size of list of a group > or < x) got from project or group
        return fillMatchBruteForce(partialIn, predEvaluator);
    }

    private List<Stage> fillMatchBruteForce(List<List<Document>> partialIn, PredEvaluator predEvaluator) {
        Set<AttrSchema> inAttrSchemaSet = SchemaExtractor.extractAttrSchemas(partialIn);
        // Assume we will never use _id and its subfields to filter.
        inAttrSchemaSet = inAttrSchemaSet.stream()
                .filter(i -> !i.getAccessPath().startsWith(new AccessPath(null, new Attribute("_id"))))
                .collect(Collectors.toSet());
        //predicates for each attr
        List<List<Predicate>> predicatesForAttrs = new ArrayList<>();
        for (AttrSchema inAttr : inAttrSchemaSet) {
            List<Predicate> predicates = new ArrayList<>();
            predicatesForEmptyValues(predicates, inAttr);
            for (String constant : constants) {
                predicatesForNumberAttr(predicates, inAttr, constant);
                predicatesForStringAttr(predicates, inAttr, constant);
                predicatesForDateAttr(predicates, inAttr, constant);
                predicatesForArrayAttr(predicates, inAttr, constant);
            }
            predicates = removeDuplicatePredicates(predicates, predEvaluator);
            // and/or combinations for the one attr
            List<Predicate> predicatesCopy = new ArrayList<>(predicates);
            for (int i = 0; i < predicatesCopy.size(); ++i) {
                for (int j = i + 1; j < predicatesCopy.size(); ++j) {
                    predicates.add(new And(predicatesCopy.get(i), predicatesCopy.get(j)));
                    predicates.add(new Or(predicatesCopy.get(i), predicatesCopy.get(j)));
                }
            }
            predicates = removeDuplicatePredicates(predicates, predEvaluator);
            predicatesForAttrs.add(predicates);
        }

        // match all the criteria. -> NNF
        Queue<Predicate> workList = new ArrayDeque<>();
        Predicate initVal = new True();
        workList.offer(initVal);
        // due to the large number of predicates in this process
        // we remove duplicates in the loop instead of doing it after the loop
        Map<String, Predicate> workMap = new HashMap<>();
        workMap.put(predEvaluator.evalPredicate(initVal), initVal);

        for (List<Predicate> predicates : predicatesForAttrs) {
            int qSize = workList.size();
            for (int i = 0; i < qSize; ++i) {
                Predicate head = workList.poll();
                // not $and predicates for current attr
                workList.offer(head);
                for (Predicate p: predicates) {
                    if (head instanceof True) {
                        checkAndAddToQueue(workMap, workList, predEvaluator, p);
                    } else {
                        checkAndAddToQueue(workMap, workList, predEvaluator, new Or(head, p));
                        checkAndAddToQueue(workMap, workList, predEvaluator, new And(head, p));
                    }
                }
            }
        }
        List<Stage> res = new ArrayList<>();
        // workMap contains the most simple predicate
        /*  workMap.values().forEach(p -> {
            res.add(new Match(p));
        });*/
        workMap.keySet().forEach(bitmap -> {
            if (!EXPERIMENT.equals(EXP_MODE.SIZE_IMPACT)) {
                // filter out predicates that filters nothing because it is meaningless.
                // (does not apply for SIZE_IMPACT experiment)
                if (bitmap != null && bitmap.contains("0")) {
                    res.add(new Match(workMap.get(bitmap)));
                }
            } else {
                if (bitmap != null) {
                    res.add(new Match(workMap.get(bitmap)));
                }
            }
        });
        return res;
    }

    private void checkAndAddToQueue(
            Map<String, Predicate> workMap,
            Queue<Predicate> workList,
            PredEvaluator predEvaluator,
            Predicate predicate) {
        String bitMap = predEvaluator.evalPredicate(predicate);
        if (workMap.containsKey(bitMap)) {
            if (workMap.get(bitMap).size() > predicate.size()) {
                workMap.put(bitMap, predicate);
            }
        } else {
            workMap.put(bitMap, predicate);
            workList.offer(predicate);
        }
    }

    private List<Predicate> removeDuplicatePredicates(List<Predicate> predicates, PredEvaluator predEvaluator) {
        Map<String, Predicate> workMap = new HashMap<>();
        for (Predicate predicate : predicates) {
            String bitMap = predEvaluator.evalPredicate(predicate);
            if (workMap.containsKey(bitMap)) {
                if (workMap.get(bitMap).size() > predicate.size()) {
                    workMap.put(bitMap, predicate);
                }
            } else {
                workMap.put(bitMap, predicate);
            }
        }
        return new ArrayList<>(workMap.values().stream().toList());
    }


    private void predicatesForEmptyValues(
            List<Predicate> predicates,
            AttrSchema inAttr) {
        predicates.add(new Exists(inAttr.accessPath()));
        predicates.add(new Not(new Exists(inAttr.accessPath())));
        predicates.add(new LogicOperator(inAttr.accessPath(),
                LogicOpcode.EQ, new ValueExpr(new NullLiteral())));
        predicates.add(new Not(new LogicOperator(inAttr.accessPath(),
                LogicOpcode.EQ, new ValueExpr(new NullLiteral()))));
    }

    private void predicatesForNumberAttr(
            List<Predicate> predicates,
            AttrSchema inAttr,
            String constant) {
        boolean bothNum = (inAttr.type() == Type.DOUBLE || inAttr.type() == Type.INT
                            || inAttr.type() == Type.DOUBLE_ARRAY || inAttr.type() == Type.INT_ARRAY)
                && Utils.isNumber(constant) && !inAttr.getAccessPath().getAttr().getAttrName().equals("_id");
        if (bothNum) {
            double consVal = Double.parseDouble(constant);
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.EQ, new ValueExpr(new FloatLiteral(consVal))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.GT, new ValueExpr(new FloatLiteral(consVal))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.GTE, new ValueExpr(new FloatLiteral(consVal))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.LT, new ValueExpr(new FloatLiteral(consVal))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.LTE, new ValueExpr(new FloatLiteral(consVal))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.NE, new ValueExpr(new FloatLiteral(consVal))));
        }
    }

    private void predicatesForStringAttr(
            List<Predicate> predicates,
            AttrSchema inAttr,
            String constant) {
        boolean bothString = (inAttr.type() == Type.STRING || inAttr.type() == Type.STRING_ARRAY)
                && !Utils.isISODate(constant);
        if (bothString) {
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.EQ, new ValueExpr(new StringLiteral(constant))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.NE, new ValueExpr(new StringLiteral(constant))));
        }
    }

    private void predicatesForDateAttr(
            List<Predicate> predicates,
            AttrSchema inAttr,
            String constant) {
        boolean bothDate = (inAttr.type() == Type.DATE || inAttr.type() == Type.DATE_ARRAY)
                && Utils.isISODate(constant);
        if (bothDate) {
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.EQ, new ValueExpr(new ISODate(constant))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.GT, new ValueExpr(new ISODate(constant))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.GTE, new ValueExpr(new ISODate(constant))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.LT, new ValueExpr(new ISODate(constant))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.LTE, new ValueExpr(new ISODate(constant))));
            predicates.add(new LogicOperator(inAttr.accessPath(),
                    LogicOpcode.NE, new ValueExpr(new ISODate(constant))));
        }
    }



    // for empty: please provide an constant "0" in the benchmark
    private void predicatesForArrayAttr(
            List<Predicate> predicates,
            AttrSchema inAttr,
            String constant) {
        boolean canFilterByArraySize = Utils.isInteger(constant)
                && Utils.isArray(inAttr);
        if (canFilterByArraySize) {
            int len = Integer.parseInt(constant);
            int lenSub1 = len - 1;
            predicates.add(new SizeIs(inAttr.accessPath(), new ValueExpr(new IntLiteral(len))));
            predicates.add(new Exists(addIndexToAp(inAttr.accessPath(), len)));
            predicates.add(new Exists(addIndexToAp(inAttr.accessPath(), lenSub1)));
            predicates.add(new Not(new Exists(addIndexToAp(inAttr.accessPath(), len))));
            predicates.add(new Not(new Exists(addIndexToAp(inAttr.accessPath(), lenSub1))));

        }

    }

    private AccessPath addIndexToAp(AccessPath accessPath, int index) {
        return new AccessPath(accessPath, new Attribute(String.valueOf(index)));
    }

/*======================================================================================================================*/
    // project(common, new) <==> addFields(new) + project(common, new)
    private List<Stage> fillProject(List<List<Document>> partialIn) {
        Set<AttrSchema> partialInAttrSchemaSet = SchemaExtractor.extractAttrSchemas(partialIn);


        Set<AttrSchema> outAttrSchemaSet = outputSchema.getAttrSchemaSet();
        List<AccessPath> outAccessPaths = outAttrSchemaSet.stream()
                .map(AttrSchema::getAccessPath).toList();

        List<AccessPath> projectAccessPaths;
        if (!checkPartialIn(partialIn)) {
            projectAccessPaths = new ArrayList<>();
        } else {
            AbstractCol abstPartialInput = new AbstractCol(partialIn,
                    new Schema("partial", new ArrayList<>(partialInAttrSchemaSet)));
            projectAccessPaths = removeHierarchy(outAccessPaths, abstPartialInput);
        }

        List<Stage> res = new ArrayList<>();
        if (!projectAccessPaths.isEmpty()) {
            res.add(new Project(projectAccessPaths, new ArrayList<>(), new ArrayList<>()));
        }
        return res;
    }

    private boolean checkPartialIn(List<List<Document>> partialIn) {
        if (partialIn == null) {
            return false;
        }
        for (List<Document> documents : partialIn) {
            if (documents == null) {
                return false;
            }
        }
        return true;
    }


    // remove hierarchy based on abstractCol
    public static List<AccessPath> removeHierarchy(List<AccessPath> accessPaths, AbstractCol abstractCol) {
        // map nodes in the abstract collection to paths that are common access paths
        Map<AbstractCol, AccessPath> abstNodeToAccessPath = new HashMap<>();
        for (AccessPath accessPath : accessPaths) {
            List<String> fullPath = accessPath.getFullPath();
            AbstractCol ptr = abstractCol;
            for (String attr : fullPath) {
                if (ptr == null) { // this means that accessPaths is not subset of partialIn
                    return new ArrayList<>();
                }
                ptr = ptr.getFields().get(attr);
            }
            abstNodeToAccessPath.put(ptr, accessPath);
        }
        Map<AbstractCol, Boolean> fullProjectMap = new HashMap<>();
        calcProjectStats(abstNodeToAccessPath, fullProjectMap, abstractCol);

        List<AccessPath> res = new ArrayList<>();

        getNoHierarchyAps(abstNodeToAccessPath, fullProjectMap, res, abstractCol);
        return res;
    }

    /**
     * @return whether the whole tree is projected
     */
    private static void calcProjectStats(
            Map<AbstractCol, AccessPath> abstNodeToAccessPath,
            Map<AbstractCol, Boolean> fullProjectMap,
            AbstractCol abstCol) {
        if (abstCol == null) {
            return;
        }
        boolean isFullProject = abstNodeToAccessPath.containsKey(abstCol);

        for (String childProp : abstCol.getFields().keySet()) {
            calcProjectStats(abstNodeToAccessPath, fullProjectMap, abstCol.getFields().get(childProp));
        }
        for (String childProp : abstCol.getFields().keySet()) {
            isFullProject = isFullProject && fullProjectMap.get(abstCol.getFields().get(childProp));
        }

        fullProjectMap.put(abstCol, isFullProject);
    }

    private static void getNoHierarchyAps(
            Map<AbstractCol, AccessPath> abstNodeToAccessPath,
            Map<AbstractCol, Boolean> fullProjectMap,
            List<AccessPath> accessPathsNoHierarchy,
            AbstractCol abstractCol
    ) {
        if (fullProjectMap.get(abstractCol)) {
            accessPathsNoHierarchy.add(abstNodeToAccessPath.get(abstractCol));
            return;
        }
        for (String childProp : abstractCol.getFields().keySet()) {
            getNoHierarchyAps(abstNodeToAccessPath, fullProjectMap, accessPathsNoHierarchy,
                    abstractCol.getFields().get(childProp));
        }
    }

/*======================================================================================================================*/
    private List<Stage> fillAddFields(List<List<Document>> partialIn) {
        // translate to project
        Set<AttrSchema> partialInAttrSchemaSet = SchemaExtractor.extractAttrSchemas(partialIn);
        Set<AttrSchema> outAttrSchemaSet = outputSchema.getAttrSchemaSet();
        Set<AttrSchema> onlyInOutAttrs = new HashSet<>();
        for (AttrSchema out : outAttrSchemaSet) {
            if (!partialInAttrSchemaSet.contains(out) && !this.abstOutput.attrInNestedArray(out)) {
                onlyInOutAttrs.add(out);
            }
        }
        AbstractCol abstPartialIn =  new AbstractCol(partialIn,
                new Schema("partial", new ArrayList<>(partialInAttrSchemaSet)));

        // just project all leve1 attrs because we project all attrs here
        List<AccessPath> inAccessPaths = partialInAttrSchemaSet.stream()
                .map(AttrSchema::getAccessPath).filter(i -> i.getDepth() == 1).toList();
//        inAccessPaths = removeHierarchy(inAccessPaths, abstPartialIn);

        List<Map.Entry<List<AccessPath>, List<Expression>>> addFields = new ArrayList<>();
        getAddFields(
                partialIn,
                addFields,
                new ArrayList<>(),
                new ArrayList<>(),
                // filter out attrs in nested array because they do not have unique vales
                new ArrayList<>(partialInAttrSchemaSet.stream()
                        .filter(i -> !abstPartialIn.attrInNestedArray(i)).toList()),
                onlyInOutAttrs.stream().toList(), 0);

        List<Stage> res = new ArrayList<>();
        for (Map.Entry<List<AccessPath>, List<Expression>> addField : addFields) {
            if (!addField.getKey().isEmpty()) {
                addField = resolveConflictsForNewFields(addField);
                res.add(new Project(
                        resolveConflictsForAddFields(inAccessPaths, addField.getKey()),
                        addField.getValue(), addField.getKey()));
            }
        }

        return res;
    }

    private List<AccessPath> resolveConflictsForAddFields(List<AccessPath> existing, List<AccessPath> newFields) {
        List<AccessPath> res = new ArrayList<>();
        for (AccessPath accessPath : existing) {
            boolean collision = false;
            for (AccessPath newField : newFields) {
                if (accessPath.startsWith(newField) || newField.startsWith(accessPath)) {
                    collision = true;
                    break;
                }
            }
            if (!collision) {
                res.add(accessPath);
            }
        }
        return res;
    }

    private Map.Entry<List<AccessPath>, List<Expression>> resolveConflictsForNewFields(
            Map.Entry<List<AccessPath>, List<Expression>> addField) {
        List<AccessPath> aps = addField.getKey();
        List<Expression> exps = addField.getValue();
        List<Integer> idxs = new ArrayList<>();
        for (int i = 0; i < aps.size(); ++i) {
            boolean covered = false;
            for (int j = 0; j < aps.size(); ++j) {
                AccessPath ai = aps.get(i);
                AccessPath aj = aps.get(j);
                if (aj.startsWith(ai) && aj.getDepth() > ai.getDepth()) {
                    covered = true;
                    break;
                }
            }
            if (!covered) {
                idxs.add(i);
            }
        }
        List<AccessPath> apsRes = new ArrayList<>();
        List<Expression> expsRes = new ArrayList<>();
        for (int idx : idxs) {
            apsRes.add(aps.get(idx));
            expsRes.add(exps.get(idx));
        }
        return Map.entry(apsRes, expsRes);
    }

    private void getAddFields(List<List<Document>> partialIn,
                              List<Map.Entry<List<AccessPath>, List<Expression>>> addFields,
                              List<AccessPath> newFields,
                              List<Expression> expressions,
                              List<AttrSchema> inputSchemas,
                              List<AttrSchema> onlyInOutSchemas,
                              int index) {
        if (index >= onlyInOutSchemas.size()) {
            addFields.add(Map.entry(newFields, expressions));
            return;
        }
        for (AttrSchema in : inputSchemas) {
            List<Expression> possibleExps = new ArrayList<>
                    (FieldGenerator.getRenameExpressions(this.valueChecker,
                            partialIn, in, onlyInOutSchemas.get(index)));
            if (!in.accessPath().toString().startsWith("_id")) {
                possibleExps.addAll(FieldGenerator.getUnaryExpressions(this.valueChecker,
                        partialIn, in, onlyInOutSchemas.get(index)));
            }
            for (Expression expression : possibleExps) {
                List<AccessPath> newFieldsCopy = new ArrayList<>(newFields);
                List<Expression> expressionsCopy = new ArrayList<>(expressions);
                newFieldsCopy.add(onlyInOutSchemas.get(index).accessPath());
                expressionsCopy.add(expression);
                getAddFields(partialIn, addFields, newFieldsCopy, expressionsCopy, inputSchemas,
                        onlyInOutSchemas, index + 1);
            }
        }

        for (int i = 0; i < inputSchemas.size(); ++i) {
            for (int j = i + 1; j < inputSchemas.size(); ++j) {
                List<Expression> possibleExps = new ArrayList<>();
                if (!inputSchemas.get(i).accessPath().toString().startsWith("_id")
                        && !inputSchemas.get(j).accessPath().toString().startsWith("_id")) {
                    possibleExps.addAll(FieldGenerator.getBinaryExpressions(this.valueChecker,
                            partialIn, inputSchemas.get(i), inputSchemas.get(j),  onlyInOutSchemas.get(index)));
                    possibleExps.addAll(FieldGenerator.addUnaryToBinary(this.valueChecker, partialIn, possibleExps));
                }
                for (Expression expression : possibleExps) {
                    List<AccessPath> newFieldsCopy = new ArrayList<>(newFields);
                    List<Expression> expressionsCopy = new ArrayList<>(expressions);
                    newFieldsCopy.add(onlyInOutSchemas.get(index).accessPath());
                    expressionsCopy.add(expression);
                    getAddFields(partialIn, addFields, newFieldsCopy, expressionsCopy, inputSchemas,
                            onlyInOutSchemas, index + 1);
                }
            }
        }
    }

/*======================================================================================================================*/
    private List<Stage> fillGroup(List<List<Document>> partialIn, List<TStage> leftPart) {
        Set<AttrSchema> partialInAttrSchemaSet = SchemaExtractor.extractAttrSchemas(partialIn);
        AbstractCol abstPartialIn =  new AbstractCol(partialIn,
                new Schema("partial", new ArrayList<>(partialInAttrSchemaSet)));

        List<AccessPath> inPaths = partialInAttrSchemaSet
                .stream()
                .filter(i -> !abstPartialIn.attrInNestedArray(i))
                .map(AttrSchema::accessPath)
                .toList();

        List<List<AccessPath>> groupKeys = getPossibleGroupKeys(inPaths);
        groupKeys = filterGroupKeys(groupKeys);

        /* NOTE: 1. new fields CANNOT be nested
         *       2. cannot do aggr functions on nested fields that are in a nested array
         *        example [{a: 1, b: [{c: 1}]}] cannot do {$sum: "$b.c"}
         *
         */

        List<AttrSchema> inputNumSchemas = partialInAttrSchemaSet.stream().filter(
                i -> (i.type() == Type.INT || i.type() == Type.DOUBLE)
                        && (!i.accessPath().getAttr().getAttrName().equals("_id"))
                        && !abstPartialIn.attrInNestedArray(i)
        ).toList();

        List<AttrSchema> outputNumSchemas = outputSchema.getAttrSchemaSet().stream().filter(
                i -> (i.type() == Type.INT || i.type() == Type.DOUBLE)
                        && (!i.accessPath().getAttr().getAttrName().equals("_id"))
                        && i.accessPath().getDepth() == 1
        ).toList();

        List<Map.Entry<List<AccessPath>, List<Expression>>> groupAggrs = new ArrayList<>();
        getGroupAggrs(groupAggrs, new ArrayList<>(), new ArrayList<>(), inputNumSchemas, outputNumSchemas, 0);
        // todo: what if group key is a number field and _id is renamed to another field after group?
        List<Stage> res = new ArrayList<>();
        for (List<AccessPath> groupKey : groupKeys) {
            List<Set<Set<Integer>>> groupDivisions = ValueChecker.divideGroup(partialIn, groupKey);
            for (Map.Entry<List<AccessPath>, List<Expression>> aggr : groupAggrs) {
                if (valueChecker.checkGroup(partialIn, groupDivisions, aggr.getKey(), aggr.getValue(),
                        hasGroup(leftPart))) {
                    res.add(new Group(groupKey, aggr.getValue(), aggr.getKey()));
                }
            }
        }
        return res;
    }

    private void getGroupAggrs(List<Map.Entry<List<AccessPath>, List<Expression>>> groups,
                               List<AccessPath> newFields,
                               List<Expression> expressions,
                               List<AttrSchema> inputNumSchemas,
                               List<AttrSchema> outputNumSchemas,
                               int index) {
        if (index >= outputNumSchemas.size()) {
            groups.add(Map.entry(newFields, expressions));
            return;
        }
        UnaryOpcode[] ops = {UnaryOpcode.MIN,
                UnaryOpcode.MAX,
                UnaryOpcode.AVG,
                UnaryOpcode.SUM};
        // No count because it is equivalent to {$sum: 1}
        for (AttrSchema in : inputNumSchemas) {
            for (UnaryOpcode op : ops) {
                List<AccessPath> newFieldsCopy = new ArrayList<>(newFields);
                List<Expression> expressionsCopy = new ArrayList<>(expressions);
                newFieldsCopy.add(outputNumSchemas.get(index).accessPath());
                expressionsCopy.add(new UnaryOperator(op, in.accessPath()));
                getGroupAggrs(groups, newFieldsCopy, expressionsCopy, inputNumSchemas, outputNumSchemas, index + 1);
            }
        }

        List<AccessPath> newFieldsCopy = new ArrayList<>(newFields);
        List<Expression> expressionsCopy = new ArrayList<>(expressions);
        newFieldsCopy.add(outputNumSchemas.get(index).accessPath());
        expressionsCopy.add(new UnaryOperator(UnaryOpcode.SUM, new ValueExpr(new IntLiteral(1))));
        getGroupAggrs(groups, newFieldsCopy, expressionsCopy, inputNumSchemas, outputNumSchemas, index + 1);
    }

    private List<List<AccessPath>> getPossibleGroupKeys(List<AccessPath> inPaths) {
        List<List<AccessPath>> res = new ArrayList<>();
        getGroupKeyLimitSize(HyperParams.GROUP_KEY_SIZE_LIMIT, res, new ArrayList<>(), 0, inPaths);
        return res;
    }

    private void getGroupKeyLimitSize(final int MAX_SIZE,
                                      List<List<AccessPath>> res,
                                      List<AccessPath> curr,
                                      int startIndex,
                                      List<AccessPath> inPaths) {
        if (curr.size() < MAX_SIZE) {
            res.add(new ArrayList<>(curr));
        } else if (curr.size() == MAX_SIZE) {
            res.add(new ArrayList<>(curr));
            return;
        } else {
            return;
        }
        for (int i = startIndex; i < inPaths.size(); ++i) {
            curr.add(inPaths.get(i));
            getGroupKeyLimitSize(MAX_SIZE, res, curr, i + 1, inPaths);
            curr.remove(curr.size() - 1);
        }
    }

    /**
     * filter out path list that contains hierarchy
     * @return filtered list
     */
    private List<List<AccessPath>> filterGroupKeys(List<List<AccessPath>> groupKeys) {
        return groupKeys.stream().filter(
                pList -> {
                    for (int i = 0; i < pList.size(); ++i) {
                        for (int j = i + 1; j < pList.size(); ++j) {
                            if (pList.get(i).startsWith(pList.get(j))
                                    || pList.get(j).startsWith(pList.get(i))) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
        ).toList();
    }

/*======================================================================================================================*/
    private List<Stage> fillUnwind(List<List<Document>> partialIn) {
        Set<AttrSchema> partialInAttrSchemaSet = SchemaExtractor.extractAttrSchemas(partialIn);
        AbstractCol abstPartialIn =  new AbstractCol(partialIn,
                new Schema("partial", new ArrayList<>(partialInAttrSchemaSet)));
        List<Stage> res = new ArrayList<>();
        for (AttrSchema in : partialInAttrSchemaSet) {
            if (isUnwindAttr(in, abstPartialIn)) {
                res.add(new Unwind(in.accessPath()));
            }
        }
        return res;
    }

    private boolean isUnwindAttr(AttrSchema in, AbstractCol abstractPartialIn) {
        return Utils.isArray(in) && !abstractPartialIn.attrInNestedArray(in);
    }
/*======================================================================================================================*/
    private List<Stage> fillLookup(List<List<Document>> partialIn) {
        Set<AttrSchema> partialInAttrSchemaSet = SchemaExtractor.extractAttrSchemas(partialIn);
        List<Stage> res = new ArrayList<>();
        for (Schema fSchema : foreignSchema) {
            ValueExpr from = new ValueExpr(new StringLiteral(fSchema.getSchemaName()));
            for (AttrSchema localField : partialInAttrSchemaSet) {
                for (AttrSchema foreignField : fSchema.getAttrSchemaSet()) {
                    if (localField.type() != foreignField.type()) {
                        continue;
                    }
                    for (AttrSchema as : outputSchema.getAttrSchemaSet()) {
                        res.add(new Lookup(localField.accessPath(), foreignField.accessPath(), from, as.accessPath()));
                    }
                }
            }
        }

        return res;
    }
/*======================================================================================================================*/
    public List<List<Document>> partialEval(List<Stage> partials) {
        return Evaluator.evaluate(this.inputSchema, partials, this.examples);
    }

    private static  boolean hasGroup(List<TStage> leftPart) {
        for (TStage tStage : leftPart) {
            if (tStage == TStage.GROUP) {
                return true;
            }
        }
        return false;
    }
}
