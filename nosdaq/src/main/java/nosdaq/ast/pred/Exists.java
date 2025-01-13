package nosdaq.ast.pred;

import nosdaq.ast.expr.AccessPath;

import java.util.Objects;

public final class Exists extends Predicate {
    private final AccessPath accessPath;

    public Exists(final AccessPath accessPath) {
        this.accessPath = Objects.requireNonNull(accessPath);
    }

    public AccessPath getAccessPath() {
        return accessPath;
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exists exists = (Exists) o;
        return accessPath.equals(exists.accessPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessPath);
    }

    @Override
    public String toString() {
        return String.format("exists(%s)", accessPath);
    }
}
