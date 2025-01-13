package nosdaq.ast.stage;

import nosdaq.ast.expr.Expression;

import java.util.*;

public final class Sort extends Stage {
    private final List<Expression> fields;
    private final List<Expression> sortOrder;

    public Sort(final List<Expression> fields, final List<Expression> sortOrder) {
        this.fields = Collections.unmodifiableList(Objects.requireNonNull(fields));
        this.sortOrder = Collections.unmodifiableList(Objects.requireNonNull(sortOrder));
    }

    public List<Expression> getFields() {
        return fields;
    }
    public List<Expression> getSortOrder() {return sortOrder;}

    @Override
    public <T> T accept(StageVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int size() {
        int size = 1;
        for (Expression field : fields) {
            size += field.size();
        }
        for (Expression order : sortOrder) {
            size += order.size();
        }
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sort sort = (Sort) o;
        return Objects.equals(fields, sort.fields) && Objects.equals(sortOrder, sort.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields, sortOrder);
    }

    @Override
    public String toString() {
        return String.format("Sort(%s, %s)", fields, sortOrder);
    }
}
