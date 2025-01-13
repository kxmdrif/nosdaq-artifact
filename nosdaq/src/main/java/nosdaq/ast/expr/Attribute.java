package nosdaq.ast.expr;

import java.util.Objects;

public final class Attribute extends CommonExpr {
    private final String attrName;

    public Attribute(final String attrName) {
        this.attrName = Objects.requireNonNull(attrName);
    }

    public String getAttrName() {
        return attrName;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return attrName.equals(attribute.attrName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attrName);
    }

    @Override
    public String toString() {
        return attrName;
    }
}
