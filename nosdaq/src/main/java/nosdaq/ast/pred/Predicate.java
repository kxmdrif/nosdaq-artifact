package nosdaq.ast.pred;

public abstract class Predicate {
    public abstract <T> T accept(PredicateVisitor<T> visitor);

    public abstract int size();
}

