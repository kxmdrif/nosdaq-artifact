package nosdaq.ast.pred;

import nosdaq.ast.expr.AccessPath;

import java.util.Objects;

// https://www.mongodb.com/docs/manual/reference/operator/query/all/
// todo: the design is wrong, need to be corrected
//  the field should be accesspath and list<value> or list<expression>
public final class AllMatch extends Predicate {
    private final AccessPath accessPath;
    private final Predicate predicate;

    public AllMatch(final AccessPath accessPath, final Predicate predicate) {
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
        AllMatch allMatch = (AllMatch) o;
        return accessPath.equals(allMatch.accessPath) && predicate.equals(allMatch.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessPath, predicate);
    }

    @Override
    public String toString() {
        return String.format("allMatch(%s, %s)", accessPath, predicate);
    }
}
