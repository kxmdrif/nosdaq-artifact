package nosdaq.ast.pred;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.ValueExpr;

import java.util.Objects;

public final class In extends Predicate {
    private final AccessPath field;
    private final ValueExpr array;

    public In(final AccessPath field, final ValueExpr array) {
        this.field = Objects.requireNonNull(field);
        this.array = Objects.requireNonNull(array);
    }

    public AccessPath getField() {
        return field;
    }

    public ValueExpr getArray() {
        return array;
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        In in = (In) o;
        return field.equals(in.field) && array.equals(in.array);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, array);
    }

    @Override
    public String toString() {
        return "In(" + field + ", " + array + ")";
    }
}
