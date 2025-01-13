package nosdaq.ast.schema;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class Schema {
    private final String schemaName;
    private final List<AttrSchema> attrSchemas;
    private final Set<AttrSchema> attrSchemaSet;

    public Schema(String schemaName, List<AttrSchema> attrSchemas) {
        this.schemaName = schemaName;
        this.attrSchemas = attrSchemas;
        this.attrSchemaSet = new HashSet<>(attrSchemas);
    }

    public String getSchemaName() {
        return schemaName;
    }

    public List<AttrSchema> getAttrSchemas() {
        return attrSchemas;
    }

    public Set<AttrSchema> getAttrSchemaSet() {
        return attrSchemaSet;
    }

    @Override
    public String toString() {
        return String.format("{\n%s }", attrSchemas.stream()
                .map(ap -> ap.toString())
                .collect(Collectors.joining(",\n")));
    }

    public int getDepth() {
        int depth = 0;
        for (AttrSchema attrSchema : attrSchemas) {
            depth = Math.max(depth, attrSchema.accessPath().getDepth());
        }
        return depth;
    }
}
