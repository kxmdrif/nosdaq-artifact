package nosdaq.ast.value;

import nosdaq.ast.expr.ExpressionVisitor;

import java.util.Objects;

public final class StringLiteral extends Value {
    private final String value;

    public StringLiteral(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public <T> T accept(ValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringLiteral strValue = (StringLiteral) o;
        return Objects.equals(value, strValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return '"' + value + '"';
    }
}
