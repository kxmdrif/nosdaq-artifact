package nosdaq.ast.expr;

import java.util.Objects;

public final class UnaryOperator extends AggregateExpr {
    private final UnaryOpcode op;
    private final Expression operand;

    public UnaryOperator(final UnaryOpcode op, final Expression operand) {
        this.op = Objects.requireNonNull(op);
        this.operand = Objects.requireNonNull(operand);
    }

    public UnaryOpcode getOp() {
        return op;
    }

    public Expression getOperand() {
        return operand;
    }

    public static String opToString(UnaryOpcode op) {
        switch (op) {
            case ABS: return "abs";
            case CEIL: return "ceil";
            case MIN: return "min";
            case MAX: return "max";
            case AVG: return "avg";
            case SUM: return "sum";
            case COUNT: return "count";
            default:
                throw new RuntimeException("Unknown operator");
        }
    }

    public static String opToMongo(UnaryOpcode op) {
        switch (op) {
            case ABS: return "$abs";
            case CEIL: return "$ceil";
            case MIN: return "$min";
            case MAX: return "$max";
            case AVG: return "$avg";
            case SUM: return "$sum";
            case COUNT: return "$count";
            default:
                throw new RuntimeException("Unknown operator");
        }
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 1 + operand.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnaryOperator that = (UnaryOperator) o;
        return op == that.op && operand.equals(that.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, operand);
    }

    @Override
    public String toString() {
        return opToString(op) + "(" + operand + ")";
    }
}
