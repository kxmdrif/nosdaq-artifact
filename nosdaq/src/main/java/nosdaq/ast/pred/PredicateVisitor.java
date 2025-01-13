package nosdaq.ast.pred;

public interface PredicateVisitor<T> {
    T visit(LogicOperator operator);
    T visit(True t);
    T visit(Not not);
    T visit(Or or);
    T visit(And and);
    T visit(In in);
    T visit(AllMatch allMatch);
    T visit(ElemMatch elemMatch);
    T visit(Exists exists);
    T visit(SizeIs sizeIs);

}
