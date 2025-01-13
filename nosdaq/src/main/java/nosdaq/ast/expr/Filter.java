package nosdaq.ast.expr;

import nosdaq.ast.pred.Predicate;

import java.util.Objects;

public final class Filter extends AggregateExpr {
    private final CommonExpr input;
    private final Predicate condition;
    private final ValueExpr as;
    private final ValueExpr limit;

    public Filter(final CommonExpr input, final Predicate condition, final ValueExpr as, final ValueExpr limit) {
        this.input = Objects.requireNonNull(input);
        this.condition = Objects.requireNonNull(condition);
        this.as = as;
        this.limit = limit;
    }

    public CommonExpr getInput() {
        return input;
    }

    public Predicate getCondition() {
        return condition;
    }

    public Expression getAs() {
        return as;
    }

    public ValueExpr getLimit() {
        return limit;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 1 + input.size() + condition.size() + as.size() + limit.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return input.equals(filter.input) && condition.equals(filter.condition) && as.equals(filter.as) && limit.equals(filter.limit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, condition, as, limit);
    }

    @Override
    public String toString() {
        return String.format("filter(%s, %s, %s, %s)", input, condition, as, limit);
    }
}
