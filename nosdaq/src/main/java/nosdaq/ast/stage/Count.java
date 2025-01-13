package nosdaq.ast.stage;

import nosdaq.ast.expr.ValueExpr;

import java.util.Objects;

public final class Count extends Stage {
    private final ValueExpr valueExpr;

    public Count(final ValueExpr valueExpr) {
        this.valueExpr = Objects.requireNonNull(valueExpr);
    }

    public ValueExpr getValueExpr() {
        return valueExpr;
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
        Count count = (Count) o;
        return valueExpr.equals(count.valueExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueExpr);
    }

    @Override
    public String toString() {
        return String.format("Count(%s)", valueExpr);
    }
}
