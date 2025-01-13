package nosdaq.ast.schema;

import nosdaq.ast.expr.AccessPath;

import java.util.Objects;

public record AttrSchema(AccessPath accessPath, Type type) {
    public AttrSchema(final AccessPath accessPath, final Type type) {
        // this.accessPath = Objects.requireNonNull(accessPath);
        //todo: check is it ok to be null?
        this.accessPath = accessPath;
        this.type = type;
    }

    public AccessPath getAccessPath() {
        return accessPath;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttrSchema that = (AttrSchema) o;
        return accessPath.equals(that.accessPath) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessPath, type);
    }
}
