package nosdaq.synth_new.sketch;

import com.mongodb.lang.NonNull;
import nosdaq.ast.Program;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.stage.Stage;
import nosdaq.synth_new.deduce.DeduceTypeSMT;

import java.util.ArrayList;
import java.util.List;

public abstract class Sketch implements Comparable<Sketch> {

    @NonNull
    protected List<Sketch> children;

    // number of stages
    public int size() {
        return getTStages().size();
    }

    public List<Sketch> getChildren() {
        return this.children;
    }

    @Override
    public int compareTo(Sketch o) {
        int thisSize = reCalcSize(this);
        int thatSize = reCalcSize(o);
        return thisSize - thatSize;
    }

    // xxx->addFields->project and xxx->addFields->project->match should have priority
    // in the gap (originalSize - 1, originalSize). To make it an integer we multiply originalSize by 2.
    // But xxx->addFields->project->match will be rejected by deduction
    // so instead, we assign xxx->addFields->match->project because it can contain
    // all the cases xxx->addFields->project->match have
    private static int reCalcSize(Sketch s) {
        int size = 2 * s.size();
        List<TStage> stages = s.getTStages();
        if (DeduceTypeSMT.stagesEndWithWith(stages, List.of(TStage.ADD_FIELDS, TStage.PROJECT))
            || DeduceTypeSMT.stagesEndWithWith(stages, List.of(TStage.ADD_FIELDS, TStage.MATCH, TStage.PROJECT))) {
            size = size - 1;
        }
        // Do not be too small otherwise filling too long sketches will consume much time
        return size;
    }

    public abstract Sketch copy();

    public boolean isHole() {
        return this instanceof SketchHoleNode;
    }

    public boolean isAbstractCol() {
        return this instanceof SketchColNode;
    }

    public boolean isStage() {
        return this instanceof SketchStageNode;
    }

    public Program getProgram() {
        List<Stage> stages = new ArrayList<>();
        Sketch ptr = this;
        while (!ptr.isAbstractCol()) {
            SketchHoleNode hole = (SketchHoleNode) ptr.children.get(1);
            stages.add(0, hole.getStage());
        }
        return new Program(new Aggregate(stages));
    }

    public List<TStage> getTStages() {
        List<TStage> tStages = new ArrayList<>();
        Sketch ptr = this;
        while (!ptr.isAbstractCol()) {
            SketchStageNode stageNode = (SketchStageNode) ptr;
            tStages.add(0, stageNode.getStage());
            ptr = ptr.getChildren().get(0);
        }
        return tStages;
    }

}
