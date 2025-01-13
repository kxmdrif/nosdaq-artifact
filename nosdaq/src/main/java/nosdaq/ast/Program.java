package nosdaq.ast;

import nosdaq.ast.query.Query;

import java.util.Objects;

public record Program(Query query) {
    public Program(Query query) {
        this.query = Objects.requireNonNull(query);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Program) {
            Program obj = (Program) o;
            return Objects.equals(query, obj.query);
        }
        return false;
    }

    public int size() {
        return this.query.size();
    }

    @Override
    public int hashCode() {
        return Objects.hash(query);
    }

    @Override
    public String toString() {
        return query.toString();
    }
}
