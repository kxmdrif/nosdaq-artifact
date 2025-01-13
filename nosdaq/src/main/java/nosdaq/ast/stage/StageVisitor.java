package nosdaq.ast.stage;

public interface StageVisitor<T> {
    T visit(Count count);
    T visit(Match match);
    T visit(Project project);
    T visit(Group group);
    T visit(Unwind unwind);
    T visit(Sort sort);
    T visit(Skip skip);
    T visit(Limit limit);
    T visit(Lookup lookup);
}
