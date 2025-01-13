package nosdaq.synth_new;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.schema.Type;
import nosdaq.utils.Helper;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

public class AbstractCol {
    private String name;
    private Map<String, AbstractCol> fields;
    private Type type;

    /**
     * for list type
     * length for each example
     */
    private List<Integer> lengths;

    public AbstractCol(List<List<Document>> examples, Schema schema) {
        this.name = "root";
        this.lengths = examples.stream().map(List::size).collect(Collectors.toList());
        this.type = Type.OBJECT_ARRAY;
        this.fields = new HashMap<>();

        List<AttrSchema> attrSchemas = schema.getAttrSchemas();
        for (AttrSchema attrSchema : attrSchemas) {
            addPath(attrSchema);
        }
    }

    private void addPath(AttrSchema attrSchema) {
        Type attrType = attrSchema.getType();
        List<String> fullPath = attrSchema.getAccessPath().getFullPath();
        AbstractCol ptr = this;
        for (String attr : fullPath) {
            if (!ptr.fields.containsKey(attr)) {
                // middle node, need to set type after loop ends
                AbstractCol node = new AbstractCol(attr);
                ptr.fields.put(attr, node);
            }
            ptr = ptr.fields.get(attr);
        }
        ptr.type = attrType;
    }

    private AbstractCol(String name) {
        this.name = name;
        this.fields = new HashMap<>();
        this.type = null;
        this.lengths = null;
    }

    public boolean attrInNestedArray(AttrSchema attrSchema) {
        AccessPath ap = attrSchema.accessPath();
        AbstractCol ptr = this;
        for (String attrName : ap.getFullPath()) {
            if (ptr != this && ptr.isArray()) {
                return true;
            }
            ptr = ptr.getFields().get(attrName);
        }
        return false;
    }


    private boolean isArray() {
        return type == Type.ARRAY
                || type == Type.DOUBLE_ARRAY
                || type == Type.INT_ARRAY
                || type == Type.OBJECT_ARRAY
                || type == Type.DATE_ARRAY
                || type == Type.STRING_ARRAY
                || type == Type.BOOLEAN_ARRAY;
    }


    public List<Integer> getLengths() {
        return this.lengths;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, AbstractCol> getFields() {
        return this.fields;
    }

    public int getDegree() {
        return fields.size();
    }

    public int getSize() {
        int size = 1;
        for (AbstractCol child : this.fields.values()) {
            size += child.getSize();
        }
        return size;
    }

    public Type getType() {
        return type;
    }

    // get all attrSchemas(nodes) except root
    public List<AttrSchema> getAttrSchemas() {
        List<AttrSchema> res = new ArrayList<>();
        Queue<AbstractCol> workList = new ArrayDeque<>();
        workList.offer(this);
        Queue<String> apStrQueue = new ArrayDeque<>();
        apStrQueue.offer("");
        while (!workList.isEmpty()) {
            AbstractCol node = workList.poll();
            String apStr = apStrQueue.poll();
            assert apStr != null;
            if (!apStr.isEmpty()) {
                res.add(new AttrSchema(Helper.constructAp(apStr), node.type));
            }
            for (String attr : node.getFields().keySet()) {
                workList.offer(node.getFields().get(attr));
                apStrQueue.offer(apStr.isEmpty() ? attr : apStr + "." + attr);
            }
        }
        return res;
    }

    // Check whether the attrSchema list can build a valid tree
    // It is used to check abstract result and cannot be used to check params (i.e. group key)
    public static boolean canConstructValidTree(List<AttrSchema> attrSchemas) {
        if (attrSchemas.isEmpty()) {
            return true;
        }
        AbstractCol abstractCol = new AbstractCol(new ArrayList<>(), new Schema("temp", attrSchemas));
        Queue<AbstractCol> workList = new ArrayDeque<>();
        workList.offer(abstractCol);
        while (!workList.isEmpty()) {
            AbstractCol node = workList.poll();
            // A valid abstractCol must satisfy
            // 1. all nodes must have a type
            // 2. leaf node cannot be an object or object_array (except leaf is root)
            if (node.getType() == null) {
                return false;
            }
            if (node.getDegree() == 0 &&
                    (node.getType() == Type.OBJECT_ARRAY || node.getType() == Type.OBJECT)) {
                return false;
            }
            for (AbstractCol child : node.getFields().values()) {
                workList.offer(child);
            }
        }
        return true;
    }
}
