package nosdaq.ast.stage;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.ValueExpr;

import java.util.Objects;

public final class Lookup extends Stage {

    private final ValueExpr foreignCollection;
    private final AccessPath localField;
    private final AccessPath foreignField;
    private final AccessPath as;

    public Lookup(AccessPath localField, AccessPath foreignField, ValueExpr foreignCollection, AccessPath as) {
        this.localField = localField;
        this.foreignField = foreignField;
        this.foreignCollection = foreignCollection;
        this.as = as;
    }

    public AccessPath getLocalField() {
        return localField;
    }

    public AccessPath getForeignField() {
        return foreignField;
    }

    public ValueExpr getForeignCollection() {
        return foreignCollection;
    }

    public AccessPath getAs() {
        return as;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lookup lookup = (Lookup) o;
        return Objects.equals(localField, lookup.localField)
                && Objects.equals(foreignField, lookup.foreignField)
                && Objects.equals(foreignCollection, lookup.foreignCollection)
                && Objects.equals(as, lookup.as);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localField, foreignField, foreignCollection, as);
    }

    @Override
    public <T> T accept(StageVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 5;
    }

    @Override
    public String toString() {
        return String.format("Lookup(%s, %s, %s, %s)", foreignCollection, localField, foreignField, as);
    }
}
