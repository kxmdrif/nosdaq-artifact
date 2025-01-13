package nosdaq.synth_new;

import nosdaq.ast.schema.Schema;
import nosdaq.synth_new.sketch.TStage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SketchPolicy {

    public static List<TStage> analyzePriority(AbstractCol abstInput, AbstractCol abstOutput, List<Schema> foreignSchema) {
        boolean longer = longerLength(abstInput, abstOutput);
        boolean shorter = shorterLength(abstInput, abstOutput);
        boolean fieldNameChange = fieldNameChange(abstInput, abstOutput);

        if (shorter && fieldNameChange) {
            if (foreignSchema == null) {
                return Arrays.asList(TStage.PROJECT, TStage.MATCH,
                        TStage.GROUP, TStage.ADD_FIELDS, TStage.UNWIND);
            } else {
                return Arrays.asList(TStage.PROJECT, TStage.MATCH,
                        TStage.GROUP, TStage.ADD_FIELDS, TStage.UNWIND, TStage.LOOKUP);
            }

        } else if (shorter) {
            if (foreignSchema == null) {
                return Arrays.asList(TStage.MATCH, TStage.PROJECT,
                        TStage.GROUP, TStage.ADD_FIELDS, TStage.UNWIND);
            } else {
                return Arrays.asList(TStage.MATCH, TStage.PROJECT,
                        TStage.GROUP, TStage.ADD_FIELDS, TStage.UNWIND, TStage.LOOKUP);
            }

        }

        if (longer && fieldNameChange) {
            if (foreignSchema == null) {
                return Arrays.asList(TStage.PROJECT, TStage.UNWIND,  TStage.MATCH,
                        TStage.GROUP, TStage.ADD_FIELDS);
            } else {
                return Arrays.asList(TStage.PROJECT, TStage.UNWIND,  TStage.MATCH,
                        TStage.GROUP, TStage.ADD_FIELDS, TStage.LOOKUP);
            }

        } else if (longer) {
            if (foreignSchema == null) {
                return Arrays.asList(TStage.UNWIND, TStage.PROJECT, TStage.MATCH,
                        TStage.GROUP, TStage.ADD_FIELDS);
            } else {
                return Arrays.asList(TStage.UNWIND, TStage.PROJECT, TStage.MATCH,
                        TStage.GROUP, TStage.ADD_FIELDS, TStage.LOOKUP);
            }

        }

        if (foreignSchema == null) {
            return Arrays.asList(TStage.PROJECT, TStage.ADD_FIELDS, TStage.MATCH,
                    TStage.GROUP, TStage.UNWIND);
        } else {
            return Arrays.asList(TStage.PROJECT, TStage.ADD_FIELDS, TStage.LOOKUP, TStage.MATCH,
                    TStage.GROUP, TStage.UNWIND);
        }

    }


    private static boolean longerLength(AbstractCol abstInput, AbstractCol abstOutput) {
        for (int i = 0; i < abstInput.getLengths().size(); ++i) {
            boolean longer = abstInput.getLengths().get(i) < abstOutput.getLengths().get(i);
            if (!longer) {
                return false;
            }
        }
        return true;
    }

    private static boolean shorterLength(AbstractCol abstInput, AbstractCol abstOutput) {
        for (int i = 0; i < abstInput.getLengths().size(); ++i) {
            boolean shorter = abstInput.getLengths().get(i) > abstOutput.getLengths().get(i);
            if (!shorter) {
                return false;
            }
        }
        return true;
    }

    private static boolean fieldNameChange(AbstractCol abstInput, AbstractCol abstOutput) {
        if (abstInput == null || abstOutput == null) {
            return true;
        }
        if (!Objects.equals(abstInput.getName(), abstOutput.getName())) {
            return true;
        }
        for (String key : abstInput.getFields().keySet()) {
            boolean change = fieldNameChange(
                    abstInput.getFields().get(key),
                    abstOutput.getFields().get(key));
            if (change) {
                return true;
            }
        }
        return false;
    }


}
