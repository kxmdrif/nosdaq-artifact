package nosdaq.synth_new;

import nosdaq.ast.Example;
import nosdaq.ast.expr.*;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Type;
import nosdaq.synth_new.sketch.*;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Utils {
    public static boolean isNumber(String s) {
        if (s == null) {
            return false;
        }
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isInteger(String s) {
        if (s == null) {
            return false;
        }
        return s.matches("-?\\d+");
    }

    public static boolean isISODate(String date) {
        try {
            DateTimeFormatter.ISO_DATE_TIME.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isArray(AttrSchema attrSchema) {
        if (attrSchema == null) return false;
        Type type = attrSchema.type();
        return type == Type.ARRAY
                || type == Type.DOUBLE_ARRAY
                || type == Type.INT_ARRAY
                || type == Type.OBJECT_ARRAY
                || type == Type.DATE_ARRAY
                || type == Type.STRING_ARRAY
                || type == Type.BOOLEAN_ARRAY;

    }

    public static Sketch createSketch(AbstractCol abstractInput, List<TStage> stages) {
        Sketch res = new SketchColNode(abstractInput);
        for (TStage stage : stages) {
            res = new SketchStageNode(stage, List.of(res, new SketchHoleNode()));
        }
        return res;
    }

}
