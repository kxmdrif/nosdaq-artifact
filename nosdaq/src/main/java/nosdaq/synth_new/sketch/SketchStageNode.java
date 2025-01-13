package nosdaq.synth_new.sketch;

import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SketchStageNode extends Sketch{
    private final TStage stage;

    public SketchStageNode(TStage stage, List<Sketch> children) {
        this.children = children;
        this.stage = stage;
    }

    public TStage getStage() {
        return this.stage;
    }

    @Override
    public Sketch copy() {
        List<Sketch> childrenCopy = new ArrayList<>();
        for (Sketch child : this.children) {
            childrenCopy.add(child.copy());
        }
        return new SketchStageNode(this.stage, childrenCopy);
    }

    @Override
    public String toString() {
        String firstChild = children.isEmpty() ? "" : children.get(0).toString();
        return firstChild + "->" + stage.toString();
    }
}
