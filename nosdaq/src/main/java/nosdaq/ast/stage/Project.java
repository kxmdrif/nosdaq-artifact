package nosdaq.ast.stage;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.Attribute;
import nosdaq.ast.expr.Expression;

import java.util.*;

public final class Project extends Stage {
    private final List<AccessPath> accessPaths;
    private final List<Expression> expressions;
    private final List<AccessPath> newFields;

    public Project(final List<AccessPath> accessPaths, final List<Expression> expressions, final List<AccessPath> newFields) {
        this.accessPaths = Collections.unmodifiableList(Objects.requireNonNull(accessPaths));
        this.expressions = Collections.unmodifiableList(Objects.requireNonNull(expressions));
        this.newFields = Collections.unmodifiableList(Objects.requireNonNull(newFields));
    }

    public List<AccessPath> getAccessPaths() {
        return accessPaths;
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
        for (AccessPath accessPath : accessPaths) {
            size += accessPath.size();
        }
        for (Expression expression : expressions) {
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
        Project project = (Project) o;
        return accessPaths.equals(project.accessPaths) && expressions.equals(project.expressions) && newFields.equals(project.newFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessPaths, expressions, newFields);
    }

    @Override
    public String toString() {
        return String.format("Project(%s, %s, %s)", accessPaths, expressions, newFields);
    }
}
