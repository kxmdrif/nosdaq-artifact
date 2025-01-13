package nosdaq.synth_new.rtype;

import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 1: ??: ?? (for add_fields)
 * 2: ??: Int/Double (for group, ap depth must be 1)
 * 3: ??: Object/ObjectList and ??.xx.xx: T (for lookup)
 *
 * Null means not having that kind
 * Non-null means having at least one that kind
 *
 * Strict level: 3 > 2 > 1
 */
public class UnknownPaths {
    // 1. null means not having this kind
    // (ap=null, type=null) means having this kind
    public AttrSchema bothUnknown;

    // 2 exists one type in the set is okay. no need to exist all types in the set
    public Set<Type> typeKnownTypes;

    // 3 there can be many foreign collections
    public List<Type> partialKnownType;
    public List<List<AttrSchema>> partialKnownAttrSchemas;


    public boolean hasBothUnknown() {
        return bothUnknown != null;
    }

    public boolean hasTypeKnown() {
        return typeKnownTypes != null && !typeKnownTypes.isEmpty();
    }

    public boolean hasPartialKnown() {
        return partialKnownType != null && !partialKnownType.isEmpty();
    }
    public UnknownPaths copy() {
        UnknownPaths unknownPaths = new UnknownPaths();
        unknownPaths.bothUnknown = this.bothUnknown;
        unknownPaths.typeKnownTypes = this.typeKnownTypes == null ? null : new HashSet<>(this.typeKnownTypes);
        unknownPaths.partialKnownType = this.partialKnownType == null ? null: new ArrayList<>(this.partialKnownType);
        if (this.partialKnownAttrSchemas != null) {
            unknownPaths.partialKnownAttrSchemas = new ArrayList<>();
            for (List<AttrSchema> partialKnown : this.partialKnownAttrSchemas) {
                unknownPaths.partialKnownAttrSchemas.add(new ArrayList<>(partialKnown));
            }
        } else {
            unknownPaths.partialKnownAttrSchemas = null;
        }
        return unknownPaths;
    }

    public boolean canMatch(List<AttrSchema> paths) {
        if (paths.isEmpty()) {
            return !hasBothUnknown() && !hasTypeKnown() && !hasPartialKnown();

        }
        Set<AttrSchema> matched = new HashSet<>();
        // order 3 -> 2 -> 1
        if (hasPartialKnown()) {
            for (int i = 0; i < this.partialKnownType.size(); ++i) {
                // math top level path
                boolean topMatch = false;
                for (AttrSchema path : paths) {
                    if (path.getAccessPath().getDepth() == 1
                            && (path.getType() == Type.OBJECT_ARRAY || path.getType() == Type.OBJECT)
                            && !matched.contains(path)) {
                        topMatch = true;
                        matched.add(path);
                        break;
                    }
                }
                if (!topMatch) {
                    return false;
                }
                // match sub paths
                List<AttrSchema> fAttrs = this.partialKnownAttrSchemas.get(i);
                for (AttrSchema fAttr : fAttrs) {
                    boolean subMatch = false;
                    for (AttrSchema path : paths) {
                        if (isPartial(fAttr, path) && !matched.contains(path)) {
                            subMatch = true;
                            matched.add(path);
                            break;
                        }
                    }
                    if (!subMatch) {
                        return false;
                    }
                }
            }
        }
        /*
        1. have typeKnown and have bothUnknown. one for typeKnown. others for bothUnknown
        2. have typeKnown and not have bothUnknown. all for typeKnown
        3. not have typeKnown and have bothUnknown. all for bothUnknown
        4. not have typeKnown and not have bothUnknown. match nothing
         */
        if (hasTypeKnown() && hasBothUnknown()) {
            boolean typeKnownMatch = false;
            for (AttrSchema path : paths) {
                if (this.typeKnownTypes.contains(path.getType())
                        && path.getAccessPath().getDepth() == 1
                        && !matched.contains(path)) {
                    typeKnownMatch = true;
                    matched.add(path);
                    break;
                }
            }
            if (!typeKnownMatch) { // check match count T_t = 1
                return false;
            }
            if (matched.size() == paths.size()) { // check match count T_b >= 1
                return false;
            } else {
                matched.addAll(paths);
            }
        } else if (hasTypeKnown() && !hasBothUnknown()) {
            boolean typeKnownMatch = false;
            for (AttrSchema path : paths) {
                if (this.typeKnownTypes.contains(path.getType())
                        && path.getAccessPath().getDepth() == 1
                        && !matched.contains(path)) {
                    typeKnownMatch = true;
                    matched.add(path);
                }
                if (!typeKnownMatch) { // check match count T_t >= 1
                    return false;
                }
            }
        } else if (!hasTypeKnown() && hasBothUnknown()) {
            if (matched.size() == paths.size()) { // check match count T_b >= 1
                return false;
            } else {
                matched.addAll(paths);
            }
        }
        return matched.size() == paths.size();
    }

    public static boolean isPartial(AttrSchema partial, AttrSchema whole) {
        if (partial.getType() != whole.getType()) {
            return false;
        }
        if (partial.getAccessPath().getDepth() + 1 != whole.getAccessPath().getDepth()) {
            return false;
        }
        List<String> tail = whole.getAccessPath().getFullPath().subList(1, whole.getAccessPath().getFullPath().size());
        List<String> partialFullPath = partial.getAccessPath().getFullPath();
        for (int i = 0; i < tail.size(); ++i) {
            if (!tail.get(i).equals(partialFullPath.get(i))) {
                return false;
            }
        }
        return true;
    }
}
