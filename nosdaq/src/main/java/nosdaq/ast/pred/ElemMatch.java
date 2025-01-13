package nosdaq.ast.pred;

import nosdaq.ast.expr.AccessPath;

import java.util.Objects;

/**
 * https://www.mongodb.com/docs/manual/reference/operator/query/elemMatch/
 */
public final class ElemMatch extends Predicate {
    private final AccessPath accessPath;
    private final Predicate predicate;

    public ElemMatch(final AccessPath accessPath, final Predicate predicate) {
        this.accessPath = Objects.requireNonNull(accessPath);
        this.predicate = Objects.requireNonNull(predicate);
    }

    public AccessPath getAccessPath() {
        return accessPath;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 2 + predicate.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElemMatch elemMatch = (ElemMatch) o;
        return accessPath.equals(elemMatch.accessPath) && predicate.equals(elemMatch.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessPath, predicate);
    }

    @Override
    public String toString() {
        return String.format("elemMatch(%s, %s)", accessPath, predicate);
    }
}
