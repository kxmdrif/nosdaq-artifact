package nosdaq.utils;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.Attribute;
import nosdaq.ast.schema.Type;

public class Helper {
    public static AccessPath constructAp(String ap) {
        String[] accessPaths = ap.split("\\.");
        if (accessPaths.length == 1) {
            return new AccessPath(null, new Attribute(accessPaths[0]));
        }

        int index = accessPaths.length - 1;
        AccessPath accessPath = helper(accessPaths, index);
        return accessPath;
    }

    public static AccessPath helper(String[] ap, int index) {
        if (index == 0) {
            return new AccessPath(null, new Attribute(ap[index]));
        }

        return new AccessPath(helper(ap, index - 1), new Attribute(ap[index]));
    }

    public static Type getSchemaType(String type) {
        switch (type) {
            case "OBJECT_ID": return Type.OBJECT_ID;
            case "INT": return Type.INT;
            case "DOUBLE": return Type.DOUBLE;
            case "BOOLEAN": return Type.BOOLEAN;
            case "STRING": return Type.STRING;
            case "OBJECT": return Type.OBJECT;
            case "STRING_ARRAY": return Type.STRING_ARRAY;
            case "INT_ARRAY": return Type.INT_ARRAY;
            case "DOUBLE_ARRAY": return Type.DOUBLE_ARRAY;
            case "DATE_ARRAY": return Type.DATE_ARRAY;
            case "OBJECT_ARRAY": return Type.OBJECT_ARRAY;
            case "ARRAY": return Type.ARRAY;
            case "DATE": return Type.DATE;
            case "NULL": return Type.NULL;
        }
        return null;
    }
}
