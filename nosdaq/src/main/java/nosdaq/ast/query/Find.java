package nosdaq.ast.query;
import nosdaq.ast.expr.CommonExpr;
import nosdaq.ast.pred.Predicate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Find extends Query {
    private final Predicate predicate;
    private final List<CommonExpr> expressions;

    public Find(final Predicate predicate, final List<CommonExpr> expressions) {
        this.predicate = Objects.requireNonNull(predicate);
        this.expressions = Collections.unmodifiableList(Objects.requireNonNull(expressions));
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public List<CommonExpr> getExpressions() {
        return expressions;
    }

    @Override
    public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        // do not use find, return 0
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Find find = (Find) o;
        return predicate.equals(find.predicate) && expressions.equals(find.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, expressions);
    }

    @Override
    public String toString() {
        return String.format("find(%s, %s)", predicate, expressions);
    }
}
