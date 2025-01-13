package nosdaq.ast.schema;

public enum Type {
    // todo: in benchmarks, type of _id from $group?
    OBJECT_ID,
    INT,
    DOUBLE,
    BOOLEAN,
    STRING,
    OBJECT,
    STRING_ARRAY,
    INT_ARRAY,
    DOUBLE_ARRAY,
    DATE_ARRAY,
    OBJECT_ARRAY,
    BOOLEAN_ARRAY,
    //todo: deprecate this
    ARRAY,
    DATE,
    // for groupKey = null
    NULL
}
