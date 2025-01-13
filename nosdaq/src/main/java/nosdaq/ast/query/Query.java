package nosdaq.ast.query;

public abstract class Query {
    public abstract <T> T accept(QueryVisitor<T> visitor);

    public abstract int size();
}