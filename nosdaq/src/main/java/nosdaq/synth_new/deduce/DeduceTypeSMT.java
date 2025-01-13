package nosdaq.synth_new.deduce;

import com.microsoft.z3.*;
import nosdaq.RunOption;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.schema.Type;
import nosdaq.synth_new.AbstractCol;
import nosdaq.synth_new.rtype.RType;
import nosdaq.synth_new.rtype.Relation;
import nosdaq.synth_new.sketch.Sketch;
import nosdaq.synth_new.sketch.SketchStageNode;
import nosdaq.synth_new.sketch.TStage;
import nosdaq.utils.EXP_MODE;

import java.util.*;

import static nosdaq.Main.EXPERIMENT;

public class DeduceTypeSMT {

    public static boolean deduce(RunOption runOption, Sketch sketch, RType input, RType output, List<Schema> foreignSchema) {
        // MAIN/SIZE_IMPACT/CUSTOM.
        // We assume in main and custom experiment, size of the benchmarks whose queries have $group will strictly decrease
        // (i.e. the examples are all `good`)
        if (EXPERIMENT == null) {
            throw new RuntimeException("Experiment type not set");
        }
        boolean strictGroup = EXPERIMENT.equals(EXP_MODE.MAIN) || EXPERIMENT.equals(EXP_MODE.CUSTOM);
        return _deduce_(runOption, sketch, input, output, foreignSchema, strictGroup);
    }

    public static boolean partialDeduce(RunOption runOption, Sketch sketch, RType input, RType output, List<Schema> foreignSchema) {
        return _deduce_(runOption, sketch, input, output, foreignSchema, false);
    }

    private static boolean _deduce_(RunOption runOption, Sketch sketch, RType input, RType output, List<Schema> foreignSchema,
                                    boolean strictGroup) {
        List<TStage> tStages = new ArrayList<>();
        Sketch ptr = sketch;
        while (!ptr.isAbstractCol()) {
            SketchStageNode stageNode = (SketchStageNode) ptr;
            tStages.add(0, stageNode.getStage());
            ptr = ptr.getChildren().get(0);
        }
        if (!deduceGroup(tStages, output)) {
            return false;
        }
        if (!deduceProject(tStages)) {
            return false;
        }
        if (!deduceAddFields(tStages)) {
            return false;
        }

        // Different modes
        if (runOption == RunOption.FULL) {
            if (!preDeduceLength(tStages, input, output, strictGroup)) {
                return false;
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
                if (rType.typeMatch(output)) {
                    return true;
                }
            }
            return false;
        } else if (runOption == RunOption.NO_LENGTH) {
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
                if (rType.typeMatch(output)) {
                    return true;
                }
            }
            return false;
        } else if (runOption == RunOption.NO_TYPE) {
            return preDeduceLength(tStages, input, output, strictGroup);
        } else {
            return true;
        }
    }

    // Pre deduce before calculating rTypes because we can know it before.
    private static boolean preDeduceLength(List<TStage> tStages, RType input, RType output, boolean strictGroup) {
        List<Relation> lengthRelationToFormer =
                tStages.stream().map(s -> RType.getLengthRelationToFormer(s, strictGroup)).toList();
        RType dummyEvalResult = new RType(lengthRelationToFormer, Collections.emptyList(), null);
        try (Context ctx = new Context()) {
            if (!deduceLength(ctx, input, dummyEvalResult, output)) {
                return false;
            }
        }
        return true;
    }

    // If there is a project, there can only exist one project and it must be the last stage
    private static boolean deduceProject(List<TStage> tStages) {
        List<Integer> projectIdxs = new ArrayList<>();
        for (int i = 0; i < tStages.size(); ++i) {
            if (tStages.get(i) == TStage.PROJECT) {
                projectIdxs.add(i);
            }
        }
        if (projectIdxs.isEmpty()) {
            return true;
        } else if (projectIdxs.size() > 1) {
            return false;
        }
        return projectIdxs.get(0) == tStages.size() - 1;
    }

    // todo: If there is an addFields and after it there exists a project.
    // This project must be exactly the next stage of this addFields?
    private static boolean deduceAddFields(List<TStage> tStages) {
        // deduce continuous addFields
        if (tStages.isEmpty() || tStages.size() == 1) {
            return true;
        }
        for (int i = 0; i < tStages.size() - 1; ++i) {
            if (tStages.get(i) == TStage.ADD_FIELDS && tStages.get(i + 1) == TStage.ADD_FIELDS) {
                return false;
            }
        }
        return true;
    }

    private static boolean deduceGroup(List<TStage> tStages, RType output) {
        return deduceTopLevelGroup(tStages, output) && deduceContinuousGroup(tStages, output);
    }

    // If the several stages are group->group or group->group->project,
    // we assume it must be counting something so the output's length must be 1 or 0
    private static boolean deduceContinuousGroup(List<TStage> tStages, RType output) {
        if (stagesEndWithWith(tStages, List.of(TStage.GROUP, TStage.GROUP))
                || stagesEndWithWith(tStages, List.of(TStage.GROUP, TStage.GROUP, TStage.ADD_FIELDS))
                || stagesEndWithWith(tStages, List.of(TStage.GROUP, TStage.GROUP, TStage.PROJECT))) {
            for (int len : output.getLengths()) {
                if (len != 0 && len != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean stagesEndWithWith(List<TStage> tStages, List<TStage> pattern) {
        if (tStages.size() < pattern.size()) {
            return false;
        }
        for (int i = 0; i < pattern.size(); ++i) {
            TStage st = tStages.get(tStages.size() - 1 - i);
            TStage sp = pattern.get(pattern.size() - 1 - i);
            if (st != sp) {
                return false;
            }
        }
        return true;
    }
    private static boolean deduceTopLevelGroup(List<TStage> tStages, RType output) {

        if (tStages.isEmpty()) {
            return true;
        }
        TStage lastStage = tStages.get(tStages.size() - 1);
        if (lastStage != TStage.GROUP) {
            return true;
        }
        AbstractCol outputTree = new AbstractCol(new ArrayList<>(), new Schema("temp", output.knownPaths));
        if (!outputTree.getFields().containsKey("_id")) {
            return false;
        }
        int height = 1;
        Queue<AbstractCol> workList = new ArrayDeque<>
                (outputTree.getFields().values().stream().filter(i -> !i.getName().equals("_id")).toList());
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

    private static boolean deduceLength(Context ctx, RType input, RType evalResult, RType output) {
        List<Integer> inLens = input.getLengths();
        List<Integer> outLens = output.getLengths();
        List<Relation> lengthRelationToFormer = evalResult.getLengthRelationToFormer();
        List<IntExpr> lens = new ArrayList<>();
        lens.add(ctx.mkIntConst("Len_T0"));
        List<BoolExpr> boolExprs = new ArrayList<>();

        for (int i = 0; i < lengthRelationToFormer.size(); ++i) {
            IntExpr inLen = lens.get(lens.size() - 1);
            IntExpr outLen = ctx.mkIntConst("Len_T" + (i + 1));
            lens.add(outLen);
            Relation relation = lengthRelationToFormer.get(i);
            switch (relation) {
                case EQ -> boolExprs.add(ctx.mkEq(outLen, inLen));
                case GE -> boolExprs.add(ctx.mkGe(outLen, inLen));
                case GT -> boolExprs.add(ctx.mkGt(outLen, inLen));
                case LE -> boolExprs.add(ctx.mkLe(outLen, inLen));
                case LT -> boolExprs.add(ctx.mkLt(outLen, inLen));
            }
        }

        assert inLens.size() == outLens.size();
        for (int i = 0; i < inLens.size(); ++i) {
            Solver solver = ctx.mkSolver();
            solver.add(ctx.mkEq(ctx.mkInt(inLens.get(i)), lens.get(0)));
            solver.add(ctx.mkEq(ctx.mkInt(outLens.get(i)), lens.get(lens.size() - 1)));
            for (BoolExpr boolExpr : boolExprs) {
                solver.add(boolExpr);
            }
            Status status = solver.check();
            if (status == Status.UNSATISFIABLE) {
                return false;
            }
        }
        return true;
    }

}
