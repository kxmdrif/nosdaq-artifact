package nosdaq.ast.stage;

import nosdaq.ast.expr.AccessPath;

import java.util.Objects;

public final class Unwind extends Stage {
    private final AccessPath accessPath;

    public Unwind(final AccessPath accessPath) {
        this.accessPath = Objects.requireNonNull(accessPath);
    }

    public AccessPath getAccessPath() {
        return accessPath;
    }

    @Override
    public <T> T accept(StageVisitor<T> visitor) {
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
        Unwind unwind = (Unwind) o;
        return accessPath.equals(unwind.accessPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessPath);
    }

    @Override
    public String toString() {
        return String.format("Unwind(%s)", accessPath);
    }
}
