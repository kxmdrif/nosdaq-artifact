package nosdaq.synth_new;

import nosdaq.ast.expr.*;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Type;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// We can ensure the in and out are not in nested array
public class FieldGenerator {
    // $size need to be considered sep
    public static List<Expression> getUnaryExpressions(
            ValueChecker valueChecker,
            List<List<Document>> partialIn,
            AttrSchema in,
            AttrSchema out) {
        Type inType = in.type();
        Type outType = out.type();
        if (in.getAccessPath().getAttr().getAttrName().equals("_id")
                || out.getAccessPath().getAttr().getAttrName().equals("_id")) {
            return Collections.emptyList();
        }
        if (outType != Type.INT && outType != Type.DOUBLE) {
            return Collections.emptyList();
        }

        if ((inType == Type.INT || inType == Type.DOUBLE)) {
            return List.of(
                    new UnaryOperator(UnaryOpcode.ABS, in.accessPath()),
                    new UnaryOperator(UnaryOpcode.CEIL, in.accessPath())
            );
        } else if (inType == Type.OBJECT_ARRAY) {
            return List.of(
                    new Size(in.accessPath())
            );
        } else if (inType == Type.STRING_ARRAY || inType == Type.DATE_ARRAY) {
            return List.of(
                    new Size(in.accessPath()),
                    new UnaryOperator(UnaryOpcode.MAX, in.accessPath()),
                    new UnaryOperator(UnaryOpcode.MIN, in.accessPath())
            );
        } else if ((inType == Type.INT_ARRAY || inType == Type.DOUBLE_ARRAY)) {
            return List.of(
                    new Size(in.accessPath()),
                    new UnaryOperator(UnaryOpcode.MAX, in.accessPath()),
                    new UnaryOperator(UnaryOpcode.MIN, in.accessPath()),
                    new UnaryOperator(UnaryOpcode.SUM, in.accessPath()),
                    new UnaryOperator(UnaryOpcode.AVG, in.accessPath())
            );
        }

        return Collections.emptyList();
    }

    public static List<Expression> getRenameExpressions(
            ValueChecker valueChecker,
            List<List<Document>> partialIn,
            AttrSchema in,
            AttrSchema out) {
        if (in.type() != out.type()) {
            return Collections.emptyList();
        }
        if (!valueChecker.inValIsSuperSetOfOutVal(partialIn, in, out)) {
            return Collections.emptyList();
        }
        return List.of(in.accessPath());
    }

    public static List<Expression> getBinaryExpressions(
            ValueChecker valueChecker,
            List<List<Document>> partialIn,
            AttrSchema in1,
            AttrSchema in2,
            AttrSchema out) {
        if (in1.getAccessPath().getAttr().getAttrName().equals("_id")
                || in2.getAccessPath().getAttr().getAttrName().equals("_id")
                || out.getAccessPath().getAttr().getAttrName().equals("_id")) {
            return Collections.emptyList();
        }
        if (in1.getType() != Type.INT && in1.getType() != Type.DOUBLE) {
            return Collections.emptyList();
        }
        if (in2.getType() != Type.INT && in2.getType() != Type.DOUBLE) {
            return Collections.emptyList();
        }
        if (out.getType() != Type.INT && out.getType() != Type.DOUBLE) {
            return Collections.emptyList();
        }
        List<Expression> res = new ArrayList<>(List.of(
                new BinaryOperator(BinaryOpcode.ADD, in1.accessPath(), in2.accessPath()),
                new BinaryOperator(BinaryOpcode.SUB, in1.accessPath(), in2.accessPath()),
                new BinaryOperator(BinaryOpcode.SUB, in2.accessPath(), in1.accessPath()),
                new BinaryOperator(BinaryOpcode.MUL, in1.accessPath(), in2.accessPath())
        ));
        if (valueChecker.canDivideAndMod(partialIn, in2.accessPath())) {
            res.add(new BinaryOperator(BinaryOpcode.DIV, in1.accessPath(), in2.accessPath()));
            res.add(new BinaryOperator(BinaryOpcode.MOD, in1.accessPath(), in2.accessPath()));
        }
        if (valueChecker.canDivideAndMod(partialIn, in1.accessPath())) {
            res.add(new BinaryOperator(BinaryOpcode.DIV, in2.accessPath(), in1.accessPath()));
            res.add(new BinaryOperator(BinaryOpcode.MOD, in2.accessPath(), in1.accessPath()));
        }
        return res;
    }

    public static List<Expression> addUnaryToBinary(ValueChecker valueChecker,
                                                    List<List<Document>> partialIn,
                                                    List<Expression> binaryExps) {
        List<Expression> res = new ArrayList<>();
        for (Expression binary : binaryExps) {
            res.add(new UnaryOperator(UnaryOpcode.ABS, binary));
        }
        return res;
    }
}
