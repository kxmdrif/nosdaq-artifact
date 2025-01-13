package nosdaq.synth_new.rtype;

import nosdaq.ast.expr.AccessPath;
import nosdaq.ast.expr.Attribute;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.schema.Type;
import nosdaq.synth_new.AbstractCol;
import nosdaq.synth_new.HyperParams;
import nosdaq.synth_new.Utils;
import nosdaq.synth_new.sketch.TStage;
import nosdaq.utils.Helper;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MODEL:
 * RType_Input ==> ... =Stage_i=> {Rtype_i} ==> ... ==> {RType_Final}
 * {RType_Final} should exist at least one over-approximation of RType_output
 * And it should also satisfy the predicates/relations in the refinement type
 * NOTE:
 * a < b < c is not equivalent to a < c
 * so please keep all the relations among all RTypes instead of
 * only keeping relations between RType_Input and RType_Output
 */
public class RType {
    // top level length for documents
    private List<Integer> lengths;

    // current <relation> former
    private List<Relation> lengthRelationToFormer;
    public List<AttrSchema> knownPaths;
    private UnknownPaths unknownPaths;

    // Construct input and output Rtype
    public RType(List<List<Document>> examples, Schema schema) {
        this.lengths = examples.stream().map(List::size).collect(Collectors.toList());
        this.lengthRelationToFormer = new ArrayList<>();
        this.knownPaths = schema.getAttrSchemas();
        this.unknownPaths = new UnknownPaths(); // Empty
    }

    // Construct intermediate Rtype
    public RType(List<Relation> lengthRelationToFormer, List<AttrSchema> knownPaths, UnknownPaths unknownPaths) {
        this.lengths = null;
        this.lengthRelationToFormer = lengthRelationToFormer;
        this.knownPaths = knownPaths;
        this.unknownPaths = unknownPaths;
    }

    // Construct an empty RType for workLists/Queues
    public RType() {
        this.lengths = null;
        this.lengthRelationToFormer = null;
        this.knownPaths = new ArrayList<>();
        this.unknownPaths = new UnknownPaths();
    }

    public RType shallowCopy() {
        RType rType = new RType();
        rType.lengths = this.lengths == null ? null : new ArrayList<>(this.lengths);
        rType.lengthRelationToFormer = this.lengthRelationToFormer == null ? null
                : new ArrayList<>(this.lengthRelationToFormer);
        rType.knownPaths = new ArrayList<>(this.knownPaths);
        rType.unknownPaths = this.unknownPaths.copy();
        return rType;
    }

    public List<Integer> getLengths() {
        return this.lengths;
    }

    public List<Relation> getLengthRelationToFormer() {
        return this.lengthRelationToFormer;
    }

    public List<RType> applyMatch() {
        List<AttrSchema> knownPaths = new ArrayList<>(this.knownPaths);
        UnknownPaths unknownPaths = this.unknownPaths.copy();
        List<Relation> lengthRelationToFormer = new ArrayList<>(this.lengthRelationToFormer);
        lengthRelationToFormer.add(getLengthRelationToFormer(TStage.MATCH));
        return new ArrayList<>(List.of(new RType(
                lengthRelationToFormer, knownPaths, unknownPaths
        )));
    }

    public List<RType> applyProject(RType output) {
        Set<AttrSchema> outputAttrSchemas = new HashSet<>(output.knownPaths);
        // we assume all unknown fields will be projected because calculating them is used to keep them in result;
        List<RType> applyResult = new ArrayList<>();
        RType rType = new RType();
        rType.knownPaths = new ArrayList<>(this.knownPaths.stream().filter(outputAttrSchemas::contains).toList());
        rType.unknownPaths = this.unknownPaths.copy();
        rType.lengthRelationToFormer = new ArrayList<>(this.lengthRelationToFormer);
        rType.lengthRelationToFormer.add(getLengthRelationToFormer(TStage.PROJECT));
        // If Project is not the last one, it MIGHT not be added here.
        // But other sketch which project is last stage van satisfy
        if (isValidKnownPath(rType)) {
            applyResult.add(rType);
        }

        /* avoid this 2^n possibilities to improve efficiency(we can assert project must be the last stage)
        Queue<RType> workList = new ArrayDeque<>();
        workList.offer(new RType());
        for (AttrSchema knownPath : this.knownPaths) {
            int size = workList.size();
            for (int i = 0; i < size; ++i) {
                RType rType = workList.poll();
                assert rType != null;
                RType withNewPath = rType.shallowCopy();
                withNewPath.knownPaths.add(knownPath);

                workList.offer(rType);
                workList.offer(withNewPath);
            }

        }
        // we assume all unknown fields will be projected because calculating them is used to keep them in result;
        List<RType> applyResult = new ArrayList<>();
        while (!workList.isEmpty()) {
            RType rType = workList.poll();
            if (!isValidKnownPath(rType)) {
                continue;
            }
            rType.unknownPaths = this.unknownPaths.copy();
            rType.lengthRelationToFormer = new ArrayList<>(this.lengthRelationToFormer);
            rType.lengthRelationToFormer.add(getLengthRelationToFormer(TStage.PROJECT));
            applyResult.add(rType);
        }
        */

        return applyResult;
    }

    private static boolean isValidKnownPath(RType rType) {
        return AbstractCol.canConstructValidTree(rType.knownPaths);
    }

    public List<RType> applyGroup(boolean strictGroup) {
        AbstractCol abstractCol = new AbstractCol(new ArrayList<>(), new Schema("temp", this.knownPaths));
        List<AttrSchema> inPaths = this.knownPaths.stream().filter(i -> !abstractCol.attrInNestedArray(i)).toList();
        List<List<AttrSchema>> groupKeys = filterGroupKeys(getPossibleGroupKeys(inPaths));
        List<RType> res = new ArrayList<>();
        for (List<AttrSchema> groupKey: groupKeys) {
            RType rType = new RType();
            rType.lengthRelationToFormer = new ArrayList<>(this.lengthRelationToFormer);
            rType.lengthRelationToFormer.add(getLengthRelationToFormer(TStage.GROUP, strictGroup));
            // for single groupKey. There is no nesting in _id
            // for empty groupKey. _id = null
            if (groupKey.isEmpty()) {
                rType.knownPaths.add(new AttrSchema(AccessPath.buildFromFullPath(List.of("_id")), Type.NULL));
            } else if (groupKey.size() == 1){
                rType.knownPaths.add(new AttrSchema(AccessPath.buildFromFullPath(List.of("_id")),
                        groupKey.get(0).getType()));
                // add child attrSchemas if groupKey[0] is not a primitive type
                AbstractCol ptr = abstractCol;
                for (String attrName : groupKey.get(0).getAccessPath().getFullPath()) {
                    ptr = ptr.getFields().get(attrName);
                }
                List<AttrSchema> childAttrSchemas = ptr.getAttrSchemas();
                for (AttrSchema childAttrSchema : childAttrSchemas) {
                    String fullApStr = "_id" +
                            "." + childAttrSchema.getAccessPath().toString();
                    rType.knownPaths.add(new AttrSchema(Helper.constructAp(fullApStr), childAttrSchema.getType()));
                }

            } else {
                AttrSchema idAttr = new AttrSchema(Helper.constructAp("_id"), Type.OBJECT);
                rType.knownPaths.add(idAttr);
                List<AttrSchema> groupKeyAttrs = getKnownPathsAfterGroup(groupKey, abstractCol);
                groupKeyAttrs.forEach(i -> {
                    List<String> fullPath = i.getAccessPath().getFullPath();
                    fullPath.add(0, "_id");
                    rType.knownPaths.add(new AttrSchema(AccessPath.buildFromFullPath(fullPath), i.getType()));
                });
            }
            rType.unknownPaths = new UnknownPaths();
            rType.unknownPaths.typeKnownTypes = new HashSet<>(List.of(Type.INT, Type.DOUBLE));
            if (isValidKnownPath(rType)) {
                res.add(rType);
            }

            // It can also not have aggregation fields
            RType rTypeWithoutAggr = rType.shallowCopy();
            rTypeWithoutAggr.unknownPaths.typeKnownTypes = null;
            if (isValidKnownPath(rTypeWithoutAggr)) {
                res.add(rTypeWithoutAggr);
            }
        }
        return res;
    }

    private List<AttrSchema> getKnownPathsAfterGroup(List<AttrSchema> groupKey, AbstractCol abstractCol) {
        Set<AttrSchema> attrSchemaSet = new HashSet<>();
        for (AttrSchema attrSchema : groupKey) {
            AbstractCol ptr = abstractCol;
            String apStr = "";
            for (String attrName : attrSchema.getAccessPath().getFullPath()) {
                apStr = apStr.isEmpty() ? apStr + attrName : apStr + "." + attrName;
                AbstractCol child = ptr.getFields().get(attrName);
                attrSchemaSet.add(new AttrSchema(Helper.constructAp(apStr), child.getType()));
                ptr = child;
            }
            // add all children of this node
            List<AttrSchema> childAttrSchemas = ptr.getAttrSchemas();
            for (AttrSchema childAttrSchema : childAttrSchemas) {
                String fullApStr = attrSchema.getAccessPath() +
                        "." + childAttrSchema.getAccessPath().toString();
                attrSchemaSet.add(new AttrSchema(Helper.constructAp(fullApStr), childAttrSchema.getType()));
            }
        }
        return attrSchemaSet.stream().toList();
    }

    public List<RType> applyAddFields() {
        UnknownPaths newUnknownPaths = this.unknownPaths == null ?  null : this.unknownPaths.copy();
        assert newUnknownPaths != null;
        newUnknownPaths.bothUnknown = new AttrSchema(null, null);
        RType rType = this.shallowCopy();
        rType.unknownPaths = newUnknownPaths;
        return new ArrayList<>(List.of(rType));
    }

    public List<RType> applyUnwind() {
        AbstractCol abstractCol = new AbstractCol(new ArrayList<>(), new Schema("temp", this.knownPaths));
        List<AttrSchema> unwindAttrs = this.knownPaths.stream()
                .filter(i -> Utils.isArray(i) && !abstractCol.attrInNestedArray(i)).toList();
        List<RType> res = new ArrayList<>();
        for (AttrSchema unwindAttr : unwindAttrs) {
            RType rType = this.shallowCopy();
            rType.lengthRelationToFormer.add(getLengthRelationToFormer(TStage.UNWIND, true));
            List<AttrSchema> attrs = new ArrayList<>(
                    this.knownPaths.stream().filter(i -> !i.equals(unwindAttr)).toList()
            );
            attrs.add(new AttrSchema(unwindAttr.getAccessPath(), getTypeAfterUnwind(unwindAttr.getType())));
            rType.knownPaths = attrs;
            res.add(rType);
        }
        if (this.unknownPaths.partialKnownType != null) {
            for (int i = 0; i < this.unknownPaths.partialKnownType.size(); ++i) {
                RType rType = this.shallowCopy();
                rType.lengthRelationToFormer.add(getLengthRelationToFormer(TStage.UNWIND, true));
                Type currentPartialKnownType = rType.unknownPaths.partialKnownType.get(i);
                if (currentPartialKnownType == Type.OBJECT_ARRAY) {
                    rType.unknownPaths.partialKnownType.set(i, Type.OBJECT);
                }
                res.add(rType);
            }
        }
        return res;
    }


    //todo: avoid lookup a same foreign collection for many times
    public List<RType> applyLookUp(List<Schema> foreignSchema) {
        List<RType> res = new ArrayList<>();
        for (Schema fSchema : foreignSchema) {
            RType rType = this.shallowCopy();
            rType.lengthRelationToFormer.add(getLengthRelationToFormer(TStage.LOOKUP, true));
            if (rType.unknownPaths.partialKnownType == null || rType.unknownPaths.partialKnownAttrSchemas == null) {
                rType.unknownPaths.partialKnownType = new ArrayList<>();
                rType.unknownPaths.partialKnownAttrSchemas = new ArrayList<>();
            }
            rType.unknownPaths.partialKnownType.add(Type.OBJECT_ARRAY);
            rType.unknownPaths.partialKnownAttrSchemas.add(fSchema.getAttrSchemas());
            res.add(rType);
        }
        return res;
    }

    // generally, the param rType are from output so there are no unknown paths
    public boolean typeMatch(RType rType) {
        assert rType != null;
        List<AttrSchema> paths = rType.knownPaths;
        Set<AttrSchema> knownPathSet = new HashSet<>(this.knownPaths);
        List<AttrSchema> toMatchUnknownPaths = new ArrayList<>();
        int matchKnownCount = 0;
        for (AttrSchema path : paths) {
            if (knownPathSet.contains(path)) {
                ++matchKnownCount;
            } else {
                toMatchUnknownPaths.add(path);
            }
        }
        if (matchKnownCount != knownPathSet.size()) {
            return false;
        }
        if (this.unknownPaths == null) {
            return toMatchUnknownPaths.isEmpty();
        }
        return this.unknownPaths.canMatch(toMatchUnknownPaths);
    }


    private static Relation getLengthRelationToFormer(TStage tStage) {
        return getLengthRelationToFormer(tStage, true);
    }
    public static Relation getLengthRelationToFormer(TStage tStage, boolean strictGroup) {
        return switch (tStage) {
            case PROJECT, ADD_FIELDS, LOOKUP -> Relation.EQ;
            case MATCH -> Relation.LE;
            case GROUP -> strictGroup ? Relation.LT : Relation.LE;
            case UNWIND -> Relation.GE;
        };
    }

    private static List<List<AttrSchema>> getPossibleGroupKeys(List<AttrSchema> inPaths) {
        List<List<AttrSchema>> res = new ArrayList<>();
        getGroupKeyLimitSize(HyperParams.GROUP_KEY_SIZE_LIMIT, res, new ArrayList<>(), 0, inPaths);
        return res;
    }

    private static void getGroupKeyLimitSize(final int MAX_SIZE,
                                             List<List<AttrSchema>> res,
                                             List<AttrSchema> curr,
                                             int startIndex,
                                             List<AttrSchema> inPaths) {
        if (curr.size() < MAX_SIZE) {
            res.add(new ArrayList<>(curr));
        } else if (curr.size() == MAX_SIZE) {
            res.add(new ArrayList<>(curr));
            return;
        } else {
            return;
        }
        for (int i = startIndex; i < inPaths.size(); ++i) {
            curr.add(inPaths.get(i));
            getGroupKeyLimitSize(MAX_SIZE, res, curr, i + 1, inPaths);
            curr.remove(curr.size() - 1);
        }
    }


    /**
     * filter out path list that contains hierarchy
     * @return filtered list
     */
    private static List<List<AttrSchema>> filterGroupKeys(List<List<AttrSchema>> groupKeys) {
        return groupKeys.stream().filter(
                pList -> {
                    for (int i = 0; i < pList.size(); ++i) {
                        for (int j = i + 1; j < pList.size(); ++j) {
                            if (pList.get(i).getAccessPath().startsWith(pList.get(j).getAccessPath())
                                    || pList.get(j).getAccessPath().startsWith(pList.get(i).getAccessPath())) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
        ).toList();
    }

    private static Type getTypeAfterUnwind(Type type) {
        return switch (type) {
            case OBJECT_ARRAY -> Type.OBJECT;
            case DATE_ARRAY -> Type.DATE;
            case DOUBLE_ARRAY -> Type.DOUBLE;
            case INT_ARRAY -> Type.INT;
            case STRING_ARRAY -> Type.STRING;
            case BOOLEAN_ARRAY -> Type.BOOLEAN;
            default -> null;
        };

    }
}
