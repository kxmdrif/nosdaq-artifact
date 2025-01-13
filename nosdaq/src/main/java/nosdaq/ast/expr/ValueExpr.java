package nosdaq.ast.expr;

import nosdaq.ast.value.Value;

import java.util.Objects;

public final class ValueExpr extends CommonExpr {
    private final Value value;

    public ValueExpr(final Value value) {
        this.value = Objects.requireNonNull(value);
    }

    public Value getValue() {
        return value;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
