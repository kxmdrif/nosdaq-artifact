package nosdaq.ast.stage;

public abstract class Stage {
    public abstract <T> T accept(StageVisitor<T> visitor);
    public abstract int size();
}
