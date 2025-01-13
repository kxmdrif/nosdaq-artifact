package nosdaq.ast.query;

public interface QueryVisitor<T> {
    T visit(Find findQuery);
    T visit(Aggregate aggregateQuery);
}
