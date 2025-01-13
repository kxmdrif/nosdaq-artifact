package nosdaq.trans;

import nosdaq.ast.Program;
import nosdaq.ast.expr.*;
import nosdaq.ast.pred.*;
import nosdaq.ast.stage.*;
import nosdaq.ast.value.*;
import nosdaq.ast.query.Aggregate;
import nosdaq.ast.query.Find;
import nosdaq.ast.query.QueryVisitor;

import java.util.stream.Collectors;

import static nosdaq.ast.pred.LogicOperator.opToString;
import static nosdaq.ast.expr.BinaryOperator.opToString;
import static nosdaq.ast.expr.UnaryOperator.opToString;

public final class DSLTranslator implements
        QueryVisitor<String>, PredicateVisitor<String>, ExpressionVisitor<String>, ValueVisitor<String>, StageVisitor<String> {

    public static String translate(Program program) {
        DSLTranslator translator = new DSLTranslator();
        return program.query().accept(translator);
    }

    @Override
    public String visit(Find findQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("find(");
        builder.append(findQuery.getPredicate().accept(this));
        builder.append(", [");
        builder.append(findQuery.getExpressions().stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", ")));
        builder.append("])");
        return builder.toString();
    }

    @Override
    public String visit(Aggregate aggregateQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("aggregate([")
                .append(aggregateQuery.getStages().stream()
                        .map(stage -> stage.accept(this))
                        .collect(Collectors.joining(", ")))
                .append("])");
        return builder.toString();
    }

    /**
     *  Overriding PredicateVisitor methods
     */
    @Override
    public String visit(LogicOperator operator) {
        StringBuilder builder = new StringBuilder();
        builder.append(operator.getLhs().accept(this));
        builder.append(" " + opToString(operator.getOp()) + " ");
        builder.append(operator.getRhs().accept(this));
        return builder.toString();
    }

    @Override
    public String visit(True t) {
        return t.toString();
    }

    @Override
    public String visit(Not not) {
        StringBuilder builder = new StringBuilder();
        builder.append("not(")
                .append(not.getPredicate().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(Or or) {
        StringBuilder builder = new StringBuilder();
        builder.append("(")
                .append(or.getLeft().accept(this))
                .append(" or ")
                .append(or.getRight().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(And and) {
        StringBuilder builder = new StringBuilder();
        builder.append("(")
                .append(and.getLeft().accept(this))
                .append(" and ")
                .append(and.getRight().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(In in) {
        StringBuilder builder = new StringBuilder();
        builder.append("in(")
                .append(in.getField().accept(this))
                .append(", ")
                .append(in.getArray().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(AllMatch allMatch) {
        StringBuilder builder = new StringBuilder();
        builder.append("allMatch(")
                .append(allMatch.getAccessPath().accept(this))
                .append(", ")
                .append(allMatch.getPredicate().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(ElemMatch elemMatch) {
        StringBuilder builder = new StringBuilder();
        builder.append("elemMatch(")
                .append(elemMatch.getAccessPath().accept(this))
                .append(", ")
                .append(elemMatch.getPredicate().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(Exists exists) {
        StringBuilder builder = new StringBuilder();
        builder.append("exists(")
                .append(exists.getAccessPath().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(SizeIs sizeIs) {
        StringBuilder builder = new StringBuilder();
        builder.append("sizeIs(")
                .append(sizeIs.getAccessPath().accept(this))
                .append(", ")
                .append(sizeIs.getSize().accept(this))
                .append(")");
        return builder.toString();
    }

    /**
     * Overriding ExpressionVisitor methods
     */
    @Override
    public String visit(UnaryOperator operator) {
        StringBuilder builder = new StringBuilder();
        builder.append(opToString(operator.getOp()) + "(")
                .append(operator.getOperand().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(BinaryOperator operator) {
        StringBuilder builder = new StringBuilder();
        builder.append(operator.getLhs().accept(this))
                .append(" " + opToString(operator.getOp()) + " ")
                .append(operator.getRhs().accept(this));
        return builder.toString();
    }

    @Override
    public String visit(Type type) {
        StringBuilder builder = new StringBuilder();
        builder.append("type(")
                .append(type.getAccessPath().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(Size size) {
        StringBuilder builder = new StringBuilder();
        builder.append("size(");
        builder.append(size.getAccessPath().accept(this));
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visit(Substr substr) {
        StringBuilder builder = new StringBuilder();
        builder.append("substr(")
                .append(substr.getAccessPath().accept(this) + ", ")
                .append(substr.getStart().accept(this) + ", ")
                .append(substr.getLength().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(Filter filter) {
        StringBuilder builder = new StringBuilder();
        builder.append("filter(")
                .append(filter.getInput().accept(this) + ", ")
                .append(filter.getCondition().accept(this) + ", ")
                .append(filter.getAs().accept(this) + ", ")
                .append(filter.getLimit().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(ValueExpr valueExpr) {
        return valueExpr.getValue().accept(this);
    }

    /**
     * Overriding Value visitor methods
     */
    @Override
    public String visit(IntLiteral intLiteral) {
        return intLiteral.toString();
    }

    @Override
    public String visit(FloatLiteral floatLiteral) {
        return floatLiteral.toString();
    }

    @Override
    public String visit(StringLiteral strValue) {
        return strValue.getValue();
    }

    @Override
    public String visit(BoolLiteral boolLiteral) {
        return boolLiteral.toString();
    }

    @Override
    public String visit(NullLiteral n) {
        return n.toString();
    }

    @Override
    public String visit(ISODate isoDate) {
        return isoDate.toString();
    }

    @Override
    public String visit(Array array) {
        return array.toString();
    }

    @Override
    public String visit(Attribute attribute) {
        return attribute.getAttrName();
    }

    @Override
    public String visit(AccessPath accessPath) {
        return accessPath.toString();
    }

    /**
     * Overriding StageVisitor methods
     */
    @Override
    public String visit(Count count) {
        StringBuilder builder = new StringBuilder();
        builder.append("count(")
                .append(count.getValueExpr().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(Match match) {
        StringBuilder builder = new StringBuilder();
        builder.append("match(")
                .append(match.getPredicate().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(Project project) {
        StringBuilder builder = new StringBuilder();
        builder.append("project(");
        builder.append("[" + project.getAccessPaths().stream()
                .map(attr -> attr.accept(this))
                .collect(Collectors.joining(", ")) + "], ");
        builder.append("[" + project.getExpressions().stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", ")) + "], ");
        builder.append("[" + project.getNewFields().stream()
                .map(attr -> attr.accept(this))
                .collect(Collectors.joining(", ")) + "]");
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String visit(Group group) {
        StringBuilder builder = new StringBuilder();
        builder.append("group(");
        builder.append("[" + group.getGroupKey().stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", ")) + "], ");
        builder.append("[" + group.getExpressions().stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", ")) + "], ");
        builder.append("[" + group.getNewFields().stream()
                .map(attr -> attr.accept(this))
                .collect(Collectors.joining(", ")) + "])");
        return builder.toString();
    }

    @Override
    public String visit(Unwind unwind) {
        StringBuilder builder = new StringBuilder();
        builder.append("unwind(")
                .append(unwind.getAccessPath().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(Lookup lookup) {
        StringBuilder builder = new StringBuilder();
        builder.append("Lookup(");
        builder.append(lookup.getForeignCollection().accept(this)).append(", ");
        builder.append(lookup.getLocalField().accept(this)).append(", ");
        builder.append(lookup.getForeignField().accept(this)).append(", ");
        builder.append(lookup.getAs().accept(this)).append(")");
        return builder.toString();
    }

    @Override
    public String visit(Sort sort) {
        StringBuilder builder = new StringBuilder();
        builder.append("sort(");
        builder.append("[" + sort.getFields().stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", ")) + "], ");
        builder.append("[" + sort.getSortOrder().stream()
                .map(expr -> expr.accept(this))
                .collect(Collectors.joining(", ")) + "])");
        return builder.toString();
    }

    @Override
    public String visit(Skip skip) {
        StringBuilder builder = new StringBuilder();
        builder.append("skip(")
                .append(skip.getValueExpr().accept(this))
                .append(")");
        return builder.toString();
    }

    @Override
    public String visit(Limit limit) {
        StringBuilder builder = new StringBuilder();
        builder.append("limit(");
        builder.append(limit.getValueExpr().accept(this));
        builder.append(")");
        return builder.toString();
    }
}
