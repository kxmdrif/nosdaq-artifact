package nosdaq.ast.pred;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.ValueExpr;

import java.util.Objects;

// for array query. different from $size that calculates size of an array
public final class SizeIs extends Predicate {

    private final AccessPath accessPath;

    private final ValueExpr size;

    public SizeIs(AccessPath accessPath, ValueExpr size) {
        this.accessPath = Objects.requireNonNull(accessPath);
        this.size = Objects.requireNonNull(size);
    }

    public AccessPath getAccessPath() {
        return accessPath;
    }

    public ValueExpr getSize() {
        return size;
    }

    @Override
    public <T> T accept(PredicateVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public String toString() {
        return String.format("sizeIs(%s, %s)", accessPath, size);
    }
}
