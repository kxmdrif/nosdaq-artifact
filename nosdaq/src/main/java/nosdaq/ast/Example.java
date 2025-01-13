package nosdaq.ast;

import org.bson.Document;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Example {

    private final List<Document> input;
    private final List<Document> output;

    private final Map<String, List<Document>> foreign;
    public Example(List<Document> input, List<Document> output) {
        this.input = Collections.unmodifiableList(Objects.requireNonNull(input));
        this.output = Collections.unmodifiableList(Objects.requireNonNull(output));
        this.foreign = null;
    }

    public Example(List<Document> input, List<Document> output, Map<String, List<Document>> foreign) {
        this.input = Collections.unmodifiableList(Objects.requireNonNull(input));
        this.output = Collections.unmodifiableList(Objects.requireNonNull(output));
        this.foreign = foreign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Example example = (Example) o;
        return Objects.equals(input, example.input) && Objects.equals(output, example.output) && Objects.equals(foreign, example.foreign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, foreign);
    }

    public List<Document> getInput() {
        return this.input;
    }

    public Map<String, List<Document>> getForeign() {
        return foreign;
    }

    public List<Document> getOutput() {
        return this.output;
    }
}
