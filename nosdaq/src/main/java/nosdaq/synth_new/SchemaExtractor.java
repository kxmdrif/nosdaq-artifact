package nosdaq.synth_new;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.Attribute;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Type;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.stream.Collectors;

public class SchemaExtractor {

    public static Set<AttrSchema> extractAttrSchemas(List<List<Document>> partialIn) {
        if (partialIn == null || partialIn.isEmpty()) {
            return new HashSet<>();
        }
        List<Document> documents = partialIn.stream().filter(Objects::nonNull).flatMap(List::stream)
                .filter(Objects::nonNull).toList();


        Set<AttrSchema> attrSchemas = new HashSet<>();
        // process every doc because there might be empty arrays
        for (Document document : documents) {
            attrSchemas.addAll(extractSingleDoc(document));
        }
        Map<AccessPath, Integer> countMap = new HashMap<>();
        for (AttrSchema attrSchema : attrSchemas) {
            AccessPath accessPath = attrSchema.getAccessPath();
            if (countMap.containsKey(accessPath)) {
                countMap.put(accessPath, countMap.get(accessPath) + 1);
            } else {
                countMap.put(accessPath, 1);
            }
        }
        attrSchemas = attrSchemas.stream()
                .filter(i -> !(i.getType() == Type.NULL && countMap.get(i.getAccessPath()) > 1))
                .filter(i -> !(i.getType() == null && countMap.get(i.getAccessPath()) > 1))
                .collect(Collectors.toSet());
        return attrSchemas;
    }

    private static Set<AttrSchema> extractSingleDoc(Document document) {
        if (document == null) {
            return new HashSet<>();
        }

        Set<AttrSchema> res = new HashSet<>();

        Queue<AttrSchema> attrQueue = new ArrayDeque<>();
        attrQueue.offer(new AttrSchema(null, Type.OBJECT));
        // there might be null values
        Queue<Object> docQueue = new LinkedList<>();
        docQueue.offer(document);

        while (!attrQueue.isEmpty()) {
            AttrSchema attrSchema = attrQueue.poll();
            Object obj = docQueue.poll();
            // attrSchema.type() == null means undetermined type
            if (attrSchema.accessPath() != null && attrSchema.type() != null) {
                res.add(attrSchema);
            }

            if (obj instanceof Document objD) {
                for (String key : objD.keySet()) {
                    // not copy attrSchema.accessPath() here because it will not be modified
                    attrQueue.offer(new AttrSchema(new AccessPath(attrSchema.accessPath(), new Attribute(key)),
                            getType(objD.get(key))));
                    docQueue.offer(objD.get(key));
                }
            } else if (obj instanceof List<?> list) {
                for (Object item : list) {
                    if (item instanceof Document itemD) {
                        for (String key : itemD.keySet()) {
                            attrQueue.offer(new AttrSchema(new AccessPath(attrSchema.accessPath(), new Attribute(key)),
                                    getType(itemD.get(key))));
                            docQueue.offer(itemD.get(key));
                        }
                    }
                    // process all items in the array (In case that array elements do not have the same type or empty)
                    // break;
                }
            }
        }
        return res;
    }


    private static Type getType(Object obj) {
        if (obj == null) {
            return Type.NULL;
        }
        if (obj instanceof Boolean) {
            return Type.BOOLEAN;
        } else if (obj instanceof Date) {
            return Type.DATE;
        } else if (obj instanceof Double) {
            return Type.DOUBLE;
        } else if (obj instanceof Integer) {
            return Type.INT;
        } else if (obj instanceof ObjectId) {
            return Type.OBJECT_ID;
        } else if (obj instanceof String) {
            return Type.STRING;
        } else if (obj instanceof Document) {
            return Type.OBJECT;
        } else if (obj instanceof List<?> list) {
            if (list.isEmpty()) {
                return null;
            }
            Object item = list.get(0);
            if (item instanceof Boolean) {
                return Type.BOOLEAN_ARRAY;
            } else if (item instanceof Date) {
                return Type.DATE_ARRAY;
            } else if (item instanceof Double) {
                return Type.DOUBLE_ARRAY;
            } else if (item instanceof Integer) {
                return Type.INT_ARRAY;
            } else if (item instanceof String) {
                return Type.STRING_ARRAY;
            } else if (item instanceof Document || item instanceof ObjectId) {
                // treat objectID as object
                return Type.OBJECT_ARRAY;
            }
        }
        return null;
    }
}
