package nosdaq.ast.value;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Array extends Value {
    private final List<Value> elements;

    public Array(final List<Value> elements) {
        this.elements = Collections.unmodifiableList(Objects.requireNonNull(elements));
    }

    public List<Value> getElements() {
        return elements;
    }

    @Override
    public <T> T accept(ValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Array array = (Array) o;
        return elements.equals(array.elements);
    }


    @Override
    public String toString() {
        return elements + "";
    }
}
