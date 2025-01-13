package nosdaq.ast.stage;

import nosdaq.ast.pred.Predicate;

import java.util.Objects;

public final class Match extends Stage {
    private final Predicate predicate;

    public Match(final Predicate predicate) {
        this.predicate = Objects.requireNonNull(predicate);
    }

    public Predicate getPredicate() {
        return predicate;
    }

    @Override
    public <T> T accept(StageVisitor<T> visitor) {
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
        Match match = (Match) o;
        return Objects.equals(predicate, match.predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate);
    }

    @Override
    public String toString() {
        return String.format("Match(%s)", predicate);
    }
}
