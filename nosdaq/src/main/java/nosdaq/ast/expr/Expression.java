package nosdaq.ast.expr;

public abstract class Expression {
    public abstract <T> T accept(ExpressionVisitor<T> visitor);

    public abstract int size();
}
