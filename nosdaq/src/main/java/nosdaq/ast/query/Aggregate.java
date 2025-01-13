package nosdaq.ast.query;
import nosdaq.ast.stage.Stage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Aggregate extends Query {
    private final List<Stage> stages;

    public Aggregate(final List<Stage> stages) {
        this.stages = Collections.unmodifiableList(Objects.requireNonNull(stages));
    }

    public List<Stage> getStages() {
        return stages;
    }

    @Override
    public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        int size = 1;
        for (Stage stage : this.stages) {
            size += stage.size();
        }
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aggregate aggregate = (Aggregate) o;
        return stages.equals(aggregate.stages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stages);
    }

    @Override
    public String toString() {
        return String.format("Aggregate(%s)", stages);
    }
}