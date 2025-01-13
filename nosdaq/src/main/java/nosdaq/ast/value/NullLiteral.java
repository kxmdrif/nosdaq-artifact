package nosdaq.ast.value;

public class NullLiteral extends Value {
    @Override
    public <T> T accept(ValueVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "null";
    }
}
