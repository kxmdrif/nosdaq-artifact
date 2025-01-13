package nosdaq.trans;

import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;

public final class AccessPathExtractor implements ExpressionVisitor<AccessPath>, PredicateVisitor<AccessPath> {
    @Override
    public AccessPath visit(Attribute attribute) {
        return null;
    }

    @Override
    public AccessPath visit(AccessPath accessPath) {
        return null;
    }

    @Override
    public AccessPath visit(UnaryOperator operator) {
        return null;
    }

    @Override
    public AccessPath visit(BinaryOperator operator) {
        return null;
    }

    @Override
    public AccessPath visit(Size size) {
        return size.getAccessPath();
    }

    @Override
    public AccessPath visit(Type type) {
        return type.getAccessPath();
    }

    @Override
    public AccessPath visit(Substr substr) {
        return substr.getAccessPath();
    }

    @Override
    public AccessPath visit(Filter filter) {
        return null;
    }

    @Override
    public AccessPath visit(ValueExpr valueExpr) {
        return null;
    }

    @Override
    public AccessPath visit(LogicOperator operator) {
        return null;
    }

    @Override
    public AccessPath visit(True t) {
        return null;
    }

    @Override
    public AccessPath visit(Not not) {
        return null;
    }

    @Override
    public AccessPath visit(Or or) {
        return null;
    }

    @Override
    public AccessPath visit(And and) {
        return null;
    }

    @Override
    public AccessPath visit(In in) {
        return in.getField();
    }

    @Override
    public AccessPath visit(AllMatch allMatch) {
        return allMatch.getAccessPath();
    }

    @Override
    public AccessPath visit(ElemMatch elemMatch) {
        return elemMatch.getAccessPath();
    }

    @Override
    public AccessPath visit(Exists exists) {
        return exists.getAccessPath();
    }

    @Override
    public AccessPath visit(SizeIs sizeIs) {
        return sizeIs.getAccessPath();
    }
}
