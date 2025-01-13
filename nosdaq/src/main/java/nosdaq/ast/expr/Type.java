package nosdaq.ast.expr;

import java.util.Objects;

public final class Type extends CommonExpr {
    private final AccessPath accessPath;

    public Type(final AccessPath accessPath) {
        this.accessPath = Objects.requireNonNull(accessPath);
    }

    public AccessPath getAccessPath() {
        return accessPath;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
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
        Type type = (Type) o;
        return accessPath.equals(type.accessPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessPath);
    }

    @Override
    public String toString() {
        return String.format("type(%s)", accessPath);
    }
}
