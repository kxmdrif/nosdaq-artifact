package nosdaq.trans;

import nosdaq.ast.expr.*;

import static nosdaq.trans.MongoDBTranslator.isAggregateOperator;

public final class ValueExtractor implements ExpressionVisitor<Object> {
    @Override
    public Object visit(Attribute attribute) {
        return attribute.getAttrName();
    }

    @Override
    public Object visit(AccessPath accessPath) {
        if (isAggregateOperator) {
            return "$"+accessPath;
        } else {
            return accessPath.toString();
        }
    }

    @Override
    public Object visit(UnaryOperator operator) {
        return operator.accept(new MongoJavaTranslator(null));
    }

    @Override
    public Object visit(BinaryOperator operator) {
        return operator.accept(new MongoJavaTranslator(null));
    }

    @Override
    public Object visit(Size size) {
        return size.accept(new MongoJavaTranslator(null));
    }

    @Override
    public Object visit(Type type) {
        return null;
    }

    @Override
    public Object visit(Substr substr) {
        return substr.accept(new MongoJavaTranslator(null));
    }

    @Override
    public Object visit(Filter filter) {
        return null;
    }

    @Override
    public Object visit(ValueExpr valueExpr) {
        return valueExpr.getValue().accept(new MongoJavaTranslator(null));
    }
}
