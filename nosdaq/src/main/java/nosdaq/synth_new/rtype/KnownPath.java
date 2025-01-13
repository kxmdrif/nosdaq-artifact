package nosdaq.synth_new.rtype;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.schema.Type;

public class KnownPath {
    public AccessPath accessPath;
    public Type type;

    public KnownPath(AccessPath accessPath, Type type) {
        this.accessPath = accessPath;
        this.type = type;
    }
}
