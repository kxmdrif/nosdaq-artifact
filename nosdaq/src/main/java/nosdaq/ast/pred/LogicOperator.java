package nosdaq.ast.pred;

import nosdaq.ast.expr.Expression;

import java.util.Objects;

public final class LogicOperator extends Predicate {
    private final LogicOpcode op;
    private final Expression lhs;
    private final Expression rhs;

    public LogicOperator(final Expression lhs, final LogicOpcode op, final Expression rhs) {
        this.lhs = Objects.requireNonNull(lhs);
        this.op = Objects.requireNonNull(op);
        this.rhs = Objects.requireNonNull(rhs);
    }

    public LogicOpcode getOp() {
        return op;
    }

    public Expression getLhs() {
        return lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    public static String opToString(LogicOpcode op) {
        switch (op) {
            case EQ: return "==";
            case NE: return "!=";
            case LT: return "<";
            case LTE: return "<=";
            case GT: return ">";
            case GTE: return ">=";
            default:
                throw new RuntimeException("Unknown operator");
        }
    }

    public static String opToMongo(LogicOpcode op) {
        switch (op) {
            case EQ: return "$eq";
            case NE: return "$ne";
            case LT: return "$lt";
            case LTE: return "$lte";
            case GT: return "$gt";
            case GTE: return "$gte";
            default:
                throw new RuntimeException("Unknown operator");
        }
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 1 + lhs.size() + rhs.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogicOperator that = (LogicOperator) o;
        return op == that.op && lhs.equals(that.lhs) && rhs.equals(that.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, lhs, rhs);
    }

    @Override
    public String toString() {
        return lhs + " " + opToString(op) + " " + rhs;
    }
}
