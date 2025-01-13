package nosdaq.ast.value;

import java.text.ParseException;

public interface ValueVisitor<T> {
    T visit(IntLiteral intLiteral);
    T visit(FloatLiteral floatLiteral);
    T visit(StringLiteral stringValue);
    T visit(BoolLiteral strValue);
    T visit(NullLiteral n);
    T visit(ISODate isoDate) throws ParseException;
    T visit(Array array);
}
