package nosdaq.ast.value;

public abstract class Value {
    public abstract <T> T accept(ValueVisitor<T> visitor);
}
