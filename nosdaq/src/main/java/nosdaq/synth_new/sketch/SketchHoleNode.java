package nosdaq.synth_new.sketch;

import nosdaq.ast.stage.Stage;

import java.util.Collections;

public class SketchHoleNode extends Sketch {

    private final Stage stage;

    public SketchHoleNode() {
        this.children = Collections.emptyList();
        this.stage = null;
    }

    public SketchHoleNode(Stage stage) {
        this.children = Collections.emptyList();
        this.stage = stage;
    }

    // no modify, so pass reference of stage
    @Override
    public Sketch copy() {
        return new SketchHoleNode(this.stage);
    }

    @Override
    public String toString() {
        return "Hole";
    }

    public Stage getStage() {
        return this.stage;
    }
}
