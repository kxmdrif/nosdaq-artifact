package nosdaq.synth_new.deduce;

import nosdaq.ast.schema.Schema;
import nosdaq.synth_new.rtype.RType;
import nosdaq.synth_new.rtype.Relation;
import nosdaq.synth_new.sketch.Sketch;
import nosdaq.synth_new.sketch.SketchStageNode;
import nosdaq.synth_new.sketch.TStage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Deprecated
/**
 * Deduce by (refinement) types.
 */
public class DeduceType {

    public static boolean deduce(Sketch sketch, RType input, RType output, List<Schema> foreignSchema) {
        return _deduce_(sketch, input, output, foreignSchema, true);
    }

    public static boolean partialDeduce(Sketch sketch, RType input, RType output, List<Schema> foreignSchema) {
        return _deduce_(sketch, input, output, foreignSchema, false);
    }

    private static boolean _deduce_(Sketch sketch, RType input, RType output, List<Schema> foreignSchema,
                                 boolean strictGroup) {
        List<TStage> tStages = new ArrayList<>();
        Sketch ptr = sketch;
        while (!ptr.isAbstractCol()) {
            SketchStageNode stageNode = (SketchStageNode) ptr;
            tStages.add(0, stageNode.getStage());
            ptr = ptr.getChildren().get(0);
        }
        Queue<RType> workList = new ArrayDeque<>();
        workList.offer(input);
        for (TStage tStage : tStages) {
            int size = workList.size();
            for (int i = 0; i < size; ++i) {
                RType rType = workList.poll();
                workList.addAll(applyOp(rType, output, tStage, foreignSchema, strictGroup));
            }
        }
        while (!workList.isEmpty()) {
            RType rType = workList.poll();
            if (rType.typeMatch(output) && deduceLength(input, rType, output)) {
                return true;
            }
        }
        return false;
    }

    private static List<RType> applyOp(RType rType, RType output, TStage tStage, List<Schema> foreignSchema, boolean strictGroup) {
        return switch (tStage) {
            case PROJECT -> rType.applyProject(output);
            case MATCH -> rType.applyMatch();
            case GROUP -> rType.applyGroup(strictGroup);
            case UNWIND -> rType.applyUnwind();
            case ADD_FIELDS -> rType.applyAddFields();
            case LOOKUP -> rType.applyLookUp(foreignSchema);
        };
    }

    private static boolean deduceLength(RType input, RType evalResult, RType output) {
        List<Integer> inLens = input.getLengths();
        List<Integer> outLens = output.getLengths();
        List<Relation> lengthRelationToFormer = evalResult.getLengthRelationToFormer();
        assert inLens.size() == outLens.size();
        for (int i = 0; i < inLens.size(); ++i) {
            boolean sat = checkLengthSAT(inLens.get(i), outLens.get(i), lengthRelationToFormer);
            if (!sat) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkLengthSAT(Integer inLen, Integer outLen, List<Relation> lengthRelationToFormer) {
        // min=null means -Inf. max=null means +Inf
        Integer min = inLen;
        Integer max = inLen;
        for (Relation relation : lengthRelationToFormer) {
            switch (relation) {
                case LE -> {
                    min = null;
                }
                case LT -> {
                    max = max == null ? null : max - 1;
                    min = null;
                }
                case GE -> {
                    max = null;
                }
                case GT -> {
                    max = null;
                    min = min == null ? null : min + 1;
                }
                case EQ -> {

                }
            }
        }
        if (min == null && max == null) {
            return true;
        } else if (min != null && max == null) {
            return outLen >= min;
        } else if (min == null) {
            return outLen <= max;
        }
        return outLen >= min && outLen <= max;
    }
}
