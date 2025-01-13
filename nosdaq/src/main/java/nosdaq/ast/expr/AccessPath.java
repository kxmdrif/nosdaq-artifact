package nosdaq.ast.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AccessPath extends CommonExpr {
    private final AccessPath base;
    private final Attribute attr;

    public AccessPath(final AccessPath base, final Attribute attr) {
        this.base = base;
        this.attr = Objects.requireNonNull(attr);
    }

    public static AccessPath buildFromFullPath(List<String> fullPath) {
        AccessPath accessPath = null;
        for (String attr : fullPath) {
            accessPath = new AccessPath(accessPath, new Attribute(attr));
        }
        return accessPath;
    }

    public AccessPath getBase() {
        return base;
    }

    public Attribute getAttr() {
        return attr;
    }

    public List<String> getFullPath() {
        List<String> fullPath = new ArrayList<>();
        AccessPath curr = this;
        while (curr != null) {
            fullPath.add(0, curr.getAttr().getAttrName());
            curr = curr.base;
        }
        return fullPath;
    }

    public boolean startsWith(AccessPath accessPath) {
        if (this.getDepth() < accessPath.getDepth()) {
            return false;
        }
        AccessPath ptr = this;
        int diff = this.getDepth() - accessPath.getDepth();
        while (diff > 0) {
            ptr = ptr.base;
            --diff;
        }
        return ptr.equals(accessPath);
    }

    public int getDepth() {
        int depth = 0;
        AccessPath curr = this;
        while (curr != null) {
            ++depth;
            curr = curr.base;
        }
        return depth;
    }


    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, attr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessPath that = (AccessPath) o;
        return Objects.equals(base, that.base) && Objects.equals(attr, that.attr);
    }

    @Override
    public String toString() {
        return base == null ? attr.toString() : base + "." + attr;
    }
}
