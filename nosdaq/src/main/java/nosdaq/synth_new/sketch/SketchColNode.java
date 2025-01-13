package nosdaq.synth_new.sketch;

import nosdaq.synth_new.AbstractCol;

import java.util.Collections;

public class SketchColNode extends Sketch {

    private final AbstractCol abstractCol;
    public SketchColNode(AbstractCol abstractCol) {
        this.abstractCol = abstractCol;
        this.children = Collections.emptyList();
    }

    // won't modify, just return itself
    @Override
    public Sketch copy() {
        return this;
    }

    @Override
    public String toString() {
        return "AbstractInput";
    }
}
