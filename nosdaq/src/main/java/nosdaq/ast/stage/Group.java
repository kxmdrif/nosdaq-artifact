package nosdaq.ast.stage;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.Attribute;
import nosdaq.ast.expr.Expression;

import java.util.*;

public final class Group extends Stage {
    private final List<AccessPath> groupKey;
    private final List<Expression> expressions;
    private final List<AccessPath> newFields;

    public Group(final List<AccessPath> groupKey, final List<Expression> expressions, final List<AccessPath> newFields) {
        this.groupKey = Collections.unmodifiableList(Objects.requireNonNull(groupKey));
        this.expressions = Collections.unmodifiableList(Objects.requireNonNull(expressions));
        this.newFields = Collections.unmodifiableList(Objects.requireNonNull(newFields));
    }

    public List<AccessPath> getGroupKey() {
        return groupKey;
    }
    public List<Expression> getExpressions() {return expressions;}
    public List<AccessPath> getNewFields() {return newFields;}

    @Override
    public <T> T accept(StageVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        int size = 1;
        for (AccessPath accessPath : groupKey) {
            size += accessPath.size();
        }
        for (Expression expression: expressions) {
            size += expression.size();
        }
        for (AccessPath accessPath : newFields) {
            size += accessPath.size();
        }
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(groupKey, group.groupKey) && Objects.equals(expressions, group.expressions) && Objects.equals(newFields, group.newFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupKey, expressions, newFields);
    }

    @Override
    public String toString() {
        return String.format("Group(%s, %s, %s)", groupKey, expressions, newFields);
    }
}
