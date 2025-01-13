package nosdaq.ast.expr;

import java.util.Objects;

public final class BinaryOperator extends AggregateExpr {
    private final BinaryOpcode op;
    private final Expression lhs;
    private final Expression rhs;

    public BinaryOperator(final BinaryOpcode op, final Expression lhs, final Expression rhs) {
        this.op = Objects.requireNonNull(op);
        this.lhs = Objects.requireNonNull(lhs);
        this.rhs = Objects.requireNonNull(rhs);
    }

    public BinaryOpcode getOp() {
        return op;
    }
    public Expression getLhs() {return lhs;}
    public Expression getRhs() {return rhs;}


    public static String opToString(BinaryOpcode op) {
        switch (op) {
            case ADD: return "+";
            case SUB: return "-";
            case MUL: return "x";
            case DIV: return "/";
            case MOD: return "%";
            default:
                throw new RuntimeException("Unknown operator");
        }
    }

    public static String opToMongo(BinaryOpcode op) {
        switch (op) {
            case ADD: return "$add";
            case SUB: return "$subtract";
            case MUL: return "$multiply";
            case DIV: return "$divide";
            case MOD: return "$mod";
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
        return 1 + lhs.size() + rhs.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryOperator that = (BinaryOperator) o;
        return op == that.op && lhs.equals(that.lhs) && rhs.equals(that.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, lhs, rhs);
    }

    @Override
    public String toString() {
        return lhs + opToString(op) + rhs;
    }
}
