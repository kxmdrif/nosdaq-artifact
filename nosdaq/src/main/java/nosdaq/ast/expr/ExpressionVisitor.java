package nosdaq.ast.expr;

public interface ExpressionVisitor<T> {
    T visit(Attribute attribute);
    T visit(AccessPath accessPath);
    T visit(UnaryOperator operator);
    T visit(BinaryOperator operator);
    T visit(Size size);
    T visit(Type type);
    T visit(Substr substr);
    T visit(Filter filter);
    T visit(ValueExpr valueExpr);
}
