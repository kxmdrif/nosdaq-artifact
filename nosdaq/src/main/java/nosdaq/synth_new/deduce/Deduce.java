package nosdaq.synth_new.deduce;

import nosdaq.ast.schema.Type;
import nosdaq.synth_new.AbstractCol;
import nosdaq.synth_new.sketch.Sketch;
import com.microsoft.z3.*;
import nosdaq.synth_new.sketch.SketchColNode;
import nosdaq.synth_new.sketch.SketchStageNode;
import nosdaq.synth_new.sketch.TStage;

import java.util.*;
@Deprecated
public class Deduce {
    public static boolean deduce(Sketch sketch, AbstractCol input, AbstractCol output) {
        return _deduce_(sketch, input, output, true);
    }

    public static boolean partialDeduce(Sketch sketch, AbstractCol input, AbstractCol output) {
        return _deduce_(sketch, input, output, false);
    }

    private static boolean _deduce_(Sketch sketch, AbstractCol input, AbstractCol output, boolean strictGroup) {
        final Context ctx = new Context();
        try (ctx) {
            return deduceLength(ctx, sketch, input, output, strictGroup)
                    && deduceStructure(ctx, sketch, input, output)
                    && deduceTopLevelGroup(sketch, output)
                    && deduceGroup(sketch, output);
        }
    }

    public static boolean deduceV1(Sketch sketch, AbstractCol input, AbstractCol output) {
        final Context ctx = new Context();
        try (ctx) {
            return deduceLength(ctx, sketch, input, output, true)
                    && deduceStructure(ctx, sketch, input, output);
        }
    }

    public static boolean deduceV2(Sketch sketch, AbstractCol input, AbstractCol output) {
        final Context ctx = new Context();
        try (ctx) {
            return deduceLength(ctx, sketch, input, output, true)
                    && deduceDegree(ctx, sketch, input, output);
        }
    }

    private static boolean deduceTopLevelGroup(Sketch sketch, AbstractCol output) {
        if (sketch.isAbstractCol() || sketch.isHole() ) {
            return true;
        }
        SketchStageNode stage = (SketchStageNode) sketch;
        if (stage.getStage() != TStage.GROUP) {
            return true;
        }
        if (!output.getFields().containsKey("_id")) {
            return false;
        }
        int height = 1;
        Queue<AbstractCol> workList = new ArrayDeque<>
                (output.getFields().values().stream().filter(i -> !i.getName().equals("_id")).toList());
        while (!workList.isEmpty()) {
            ++height;
            int len = workList.size();
            for (int i = 0; i < len; ++i) {
                AbstractCol abstractCol = workList.poll();
                assert abstractCol != null;
                if (abstractCol.getType() != Type.INT && abstractCol.getType() != Type.DOUBLE) {
                    return false;
                }
                workList.addAll(abstractCol.getFields().values());
            }
        }

        return height <= 2;

    }

    private static boolean deduceGroup(Sketch sketch, AbstractCol output) {
        boolean hasGroup = false;
        Sketch ptr = sketch;
        while (!(ptr instanceof SketchColNode)) {
            if ((ptr instanceof SketchStageNode) && ((SketchStageNode) ptr).getStage() == TStage.GROUP) {
                hasGroup = true;
                break;
            }
            ptr = ptr.getChildren().get(0);
        }
        boolean hasNestedFieldExceptId = false;
        Map<String, AbstractCol> topLevelFields = output.getFields();
        for (String field : topLevelFields.keySet()) {
            if (!field.equals("_id") && !topLevelFields.get(field).getFields().isEmpty()) {
                hasNestedFieldExceptId = true;
                break;
            }
        }
        return !(hasGroup && hasNestedFieldExceptId);
    }

    /**
     *
     * @param ctx
     * @param sketch
     * @param input
     * @param output
     * @param strictGroup: true -> GT for group; false -> GTE for group
     * @return
     */
    public static boolean deduceLength(Context ctx, Sketch sketch, AbstractCol input, AbstractCol output, boolean strictGroup) {
        Sketch ptr = sketch;
        List<IntExpr> lens = new ArrayList<>();
        lens.add(ctx.mkIntConst("Len_T0"));
        List<BoolExpr> relations = new ArrayList<>();
        while (!ptr.isAbstractCol()) {
            IntExpr outLen = lens.get(lens.size() - 1);
            IntExpr inLen = ctx.mkIntConst("Len_T" + lens.size());
            lens.add(inLen);
            SketchStageNode stage = (SketchStageNode) ptr;
            switch (stage.getStage()) {
                case UNWIND -> relations.add(ctx.mkLe(inLen, outLen));
                // use >= here because if a>b>c and all of them are integers and then it is not satisfiable
                case MATCH -> relations.add(ctx.mkGe(inLen, outLen));
                // avoid wasting too much time on group. If len(in) == len(out) that means that example is not good
                // and need to change the example
                case GROUP -> {
                    if (strictGroup) {
                        relations.add(ctx.mkGt(inLen, outLen));
                    } else {
                        relations.add(ctx.mkGe(inLen, outLen));
                    }
                }
                case LOOKUP, ADD_FIELDS, PROJECT -> relations.add(ctx.mkEq(inLen, outLen));
            }
            ptr = ptr.getChildren().get(0);
        }
        List<Integer> inLens = input.getLengths();
        List<Integer> outLens = output.getLengths();
        for (int i = 0; i < inLens.size(); ++i) {
            Solver solver = ctx.mkSolver();
            solver.add(ctx.mkEq(ctx.mkInt(inLens.get(i)), lens.get(lens.size() - 1)));
            solver.add(ctx.mkEq(ctx.mkInt(outLens.get(i)), lens.get(0)));
            for (BoolExpr boolExpr : relations) {
                solver.add(boolExpr);
            }
            Status status = solver.check();
            if (status == Status.UNSATISFIABLE) {
                return false;
            }
        }
        return true;
    }

    // num nodes and degree
    private static boolean deduceStructure(Context ctx, Sketch sketch, AbstractCol input, AbstractCol output) {
        return deduceRootDegree(ctx, sketch, input, output) && deduceNumNodes(ctx, sketch, input, output);
    }

    // only degree of root
    private static boolean deduceRootDegree(Context ctx, Sketch sketch, AbstractCol input, AbstractCol output) {
        Sketch ptr = sketch;
        List<IntExpr> degrees = new ArrayList<>();
        degrees.add(ctx.mkIntConst("Deg_T0"));
        List<BoolExpr> relations = new ArrayList<>();
        while (!ptr.isAbstractCol()) {
            IntExpr outDeg = degrees.get(degrees.size() - 1);
            IntExpr inDeg = ctx.mkIntConst("Deg_T" + degrees.size());
            degrees.add(inDeg);
            SketchStageNode stage = (SketchStageNode) ptr;
            // GROUP -> cannot determine
            switch (stage.getStage()) {
                case LOOKUP -> relations.add(ctx.mkEq(ctx.mkAdd(inDeg, ctx.mkInt(1)), outDeg));
                case MATCH, UNWIND -> relations.add(ctx.mkEq(inDeg, outDeg));
                case PROJECT -> relations.add(ctx.mkGe(inDeg, outDeg));
                case ADD_FIELDS -> relations.add(ctx.mkLe(inDeg, outDeg));
            }
            ptr = ptr.getChildren().get(0);
        }
        IntExpr inDeg = ctx.mkInt(input.getDegree());
        IntExpr outDeg = ctx.mkInt(output.getDegree());
        Solver solver = ctx.mkSolver();
        solver.add(ctx.mkEq(inDeg, degrees.get(degrees.size() - 1)));
        solver.add(ctx.mkEq(outDeg, degrees.get(0)));
        for (BoolExpr boolExpr : relations) {
            solver.add(boolExpr);
        }
        Status status = solver.check();
        return status != Status.UNSATISFIABLE;
    }

    private static boolean deduceNumNodes(Context ctx, Sketch sketch, AbstractCol input, AbstractCol output) {
        Sketch ptr = sketch;
        List<IntExpr> numNodes = new ArrayList<>();
        numNodes.add(ctx.mkIntConst("NumN_T0"));
        List<BoolExpr> relations = new ArrayList<>();
        while (!ptr.isAbstractCol()) {
            IntExpr outNumNode = numNodes.get(numNodes.size() - 1);
            IntExpr inNumNode = ctx.mkIntConst("NumN_T" + numNodes.size());
            numNodes.add(inNumNode);
            SketchStageNode stage = (SketchStageNode) ptr;
            // GROUP -> cannot determine
            switch (stage.getStage()) {
                case LOOKUP, ADD_FIELDS -> relations.add(ctx.mkLt(inNumNode, outNumNode));
                case MATCH, UNWIND -> relations.add(ctx.mkEq(inNumNode, outNumNode));
                case PROJECT -> relations.add(ctx.mkGt(inNumNode, outNumNode));
            }
            ptr = ptr.getChildren().get(0);
        }
        IntExpr inNumNode = ctx.mkInt(input.getSize());
        IntExpr outNumNode = ctx.mkInt(output.getSize());
        Solver solver = ctx.mkSolver();
        solver.add(ctx.mkEq(inNumNode, numNodes.get(numNodes.size() - 1)));
        solver.add(ctx.mkEq(outNumNode, numNodes.get(0)));
        for (BoolExpr boolExpr : relations) {
            solver.add(boolExpr);
        }
        Status status = solver.check();
        return status != Status.UNSATISFIABLE;
    }


    // _id is special because of GROUP
    public static boolean deduceDegree(Context ctx, Sketch sketch, AbstractCol input, AbstractCol output) {
        Map<String, Integer> inDegreeMap = getDegreeMap(input);
        Map<String, Integer> outDegreeMap = getDegreeMap(output);
        Set<String> allAttrs = new HashSet<>(inDegreeMap.keySet());
        allAttrs.addAll(outDegreeMap.keySet());
        for (String attr : allAttrs) {
            if (!deduceSingleNodeDegree(ctx, sketch, attr, inDegreeMap, outDegreeMap)) {
                return false;
            }
        }
        return true;
    }

    // add relation between existing ones and non-exist ones does not matter
    // because it is always sat
    private static boolean deduceSingleNodeDegree(
            Context ctx,
            Sketch sketch,
            String attr,
            Map<String, Integer> inDegreeMap,
            Map<String, Integer> outDegreeMap) {
        Sketch ptr = sketch;
        List<IntExpr> degrees = new ArrayList<>();
        degrees.add(ctx.mkIntConst("Deg_T0"));

        Solver solver = ctx.mkSolver();
        while (!ptr.isAbstractCol()) {
            IntExpr outDeg = degrees.get(degrees.size() - 1);
            IntExpr inDeg = ctx.mkIntConst("Deg_T" + degrees.size());
            degrees.add(inDeg);
            SketchStageNode stage = (SketchStageNode) ptr;
            TStage tStage = stage.getStage();
            if (tStage == TStage.GROUP) {
                if (!attr.equals("root") && !attr.equals("root._id")) {
                    solver.add(ctx.mkEq(outDeg, ctx.mkInt(0)));
                }
                // need eq or not?
            } else if (tStage == TStage.LOOKUP) {
                if (attr.equals("root")) {
                    solver.add(ctx.mkEq(ctx.mkAdd(inDeg, ctx.mkInt(1)), outDeg));
                } else {
                    solver.add(ctx.mkEq(inDeg, outDeg));
                }
            } else if (tStage == TStage.PROJECT){
                solver.add(ctx.mkGe(inDeg, outDeg));
            } else if (tStage == TStage.ADD_FIELDS) {
                solver.add(ctx.mkLe(inDeg, outDeg));
            } else { // MATCH, UNWIND
                solver.add(ctx.mkEq(inDeg, outDeg));
            }

            ptr = ptr.getChildren().get(0);
        }

        if (inDegreeMap.get(attr) != null) {
            IntExpr inDeg = ctx.mkInt(inDegreeMap.get(attr));
            solver.add(ctx.mkEq(inDeg, degrees.get(degrees.size() - 1)));
        }
        if (outDegreeMap.get(attr) != null) {
            IntExpr outDeg = ctx.mkInt(outDegreeMap.get(attr));
            solver.add(ctx.mkEq(outDeg, degrees.get(0)));
        }
        Status status = solver.check();
        return status != Status.UNSATISFIABLE;
    }

    private static Map<String, Integer> getDegreeMap(AbstractCol abstractCol) {
        Map<String, Integer> degreeMap = new HashMap<>();

        Queue<AbstractCol> abstractColQueue = new ArrayDeque<>();
        Queue<String> apQueue = new ArrayDeque<>();
        abstractColQueue.offer(abstractCol);
        apQueue.offer("root");
        while (!abstractColQueue.isEmpty()) {
            AbstractCol node = abstractColQueue.poll();
            String ap = apQueue.poll();
            degreeMap.put(ap, node.getDegree());
            for (AbstractCol child : node.getFields().values()) {
                abstractColQueue.offer(child);
                apQueue.offer(ap + "." + child.getName());
            }
        }
        return degreeMap;

    }

    public static boolean deducePath(Sketch sketch, AbstractCol input, AbstractCol output) {
        return false;
    }
}
