package nosdaq.ast.pred;

public class True extends Predicate {
    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public String toString() {
        return "true";
    }
}
