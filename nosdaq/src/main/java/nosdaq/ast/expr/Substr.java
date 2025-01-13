package nosdaq.ast.expr;

import java.util.Objects;

public final class Substr extends AggregateExpr {
    private final AccessPath accessPath;
    private final ValueExpr start;
    private final ValueExpr length;

    public Substr(final AccessPath accessPath, final ValueExpr start, final ValueExpr length) {
        this.accessPath = Objects.requireNonNull(accessPath);
        this.start = Objects.requireNonNull(start);
        this.length = Objects.requireNonNull(length);
    }

    public AccessPath getAccessPath() {
        return accessPath;
    }

    public ValueExpr getStart() {
        return start;
    }

    public ValueExpr getLength() {
        return length;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Substr substr = (Substr) o;
        return accessPath.equals(substr.accessPath) && start.equals(substr.start) && length.equals(substr.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessPath, start, length);
    }

    @Override
    public String toString() {
        return String.format("substr(%s, %s, %s)", accessPath, start, length);
    }
}
