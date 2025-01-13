package nosdaq.ast.pred;

import java.util.Objects;

public final class Not extends Predicate {
    private final Predicate predicate;

    public Not(final Predicate predicate) {
        this.predicate = Objects.requireNonNull(predicate);
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
        return 1 + predicate.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Not not = (Not) o;
        return predicate.equals(not.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate);
    }

    @Override
    public String toString() {
        return "Not(" + predicate + ")";
    }
}

