package nosdaq.synth_new;

import nosdaq.ast.Example;
import nosdaq.ast.expr.*;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.value.IntLiteral;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Check whether attributes in input and output can match
 */
public class ValueChecker {
    private final List<List<Document>> outputs;

    public ValueChecker(List<Example> examples) {
        this.outputs = examples.stream().map(Example::getOutput).toList();
    }

    public boolean inValIsSuperSetOfOutVal(List<List<Document>> partialIn, AttrSchema in, AttrSchema out) {
        List<Set<Object>> partialInValSetList = extractValSetList(partialIn, in.getAccessPath());
        List<Set<Object>> outValSetList = extractValSetList(outputs, out.getAccessPath());
        assert partialInValSetList.size() == outValSetList.size();
        for (int i = 0; i < partialInValSetList.size(); ++i) {
            Set<Object> partialInValSet = partialInValSetList.get(i);
            Set<Object> outValSet = outValSetList.get(i);
            if (!isSuperSetOf(partialInValSet, outValSet)) {
                return false;
            }
        }
        return true;
    }

    // can only use this if the output new fields values is directly from this group
    // If there is another group after this group it does not works
    public boolean checkGroup(List<List<Document>> partialIn,
                              List<Set<Set<Integer>>> groupDivisions,
                              List<AccessPath> newFields,
                              List<Expression> expressions,
                              boolean hasLaterGroup) {
        if (hasLaterGroup) {
            return true;
        }
        assert newFields.size() == expressions.size();
        for (int i = 0; i < newFields.size(); ++i) {
            if (!checkGroupAggrValIsSuperSetOfOutVal(partialIn, groupDivisions, expressions.get(i), newFields.get(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean checkGroupAggrValIsSuperSetOfOutVal(List<List<Document>> partialIn,
                                                       List<Set<Set<Integer>>> groupDivisions,
                                                       Expression expression,
                                                       AccessPath out) {
        assert partialIn.size() == groupDivisions.size();
        assert partialIn.size() == outputs.size();
        for (int i = 0; i < partialIn.size(); ++i) {
            List<Document> documents = partialIn.get(i);
            Set<Set<Integer>> groupDivision = groupDivisions.get(i);
            assert expression instanceof UnaryOperator;
            UnaryOpcode opcode = ((UnaryOperator) expression).getOp();
            Expression operand = ((UnaryOperator) expression).getOperand();

            Set<Object> aggrFunValSet = new HashSet<>();
            if (operand instanceof ValueExpr valueExpr) {
                assert valueExpr.getValue().equals(new IntLiteral(1));
                aggrFunValSet = groupDivision.stream().map(Set::size).collect(Collectors.toSet());
            } else if (operand instanceof AccessPath accessPath){
                if (outputs.get(i).isEmpty()) {
                    continue;
                }
                Object outVal = extractValSet(outputs.get(i), out).stream().toList().get(0);
                for (Set<Integer> group: groupDivision) {
                    List<Object> vals = getValsByIndex(documents, accessPath, group);
                    double aggrFunVal = calcAggrFun(vals, opcode);
                    // Try to make the type match without losing equality
                    if (outVal instanceof Double) {
                        aggrFunValSet.add(aggrFunVal);
                    } else if (outVal instanceof Integer) {
                        if (aggrFunVal == (int) aggrFunVal) {
                            aggrFunValSet.add((int) aggrFunVal);
                        } else {
                            aggrFunValSet.add(aggrFunVal);
                        }
                    }
                }
            }
            assert aggrFunValSet.size() <= groupDivision.size();
            if (!isSuperSetOf(aggrFunValSet, extractValSet(outputs.get(i), out))) {
                return false;
            }

        }
        return true;
    }

    // Cannot use set otherwise the aggr result will be wrong
    private static List<Object> getValsByIndex(List<Document> documents, AccessPath accessPath, Set<Integer> group) {
        List<Object> res = new ArrayList<>();
        for (int i = 0; i < documents.size(); ++i) {
            if (group.contains(i)) {
                res.add(extractVal(documents.get(i), accessPath));
            }
        }
        return res;
    }

    // calculate to double to make sure the result is right
    private static double calcAggrFun(List<Object> vals, UnaryOpcode opcode) {
        List<Double> doubleVals = vals.stream().map(i -> {
            if (i instanceof Integer integer) {
                return integer.doubleValue();
            } else if (i instanceof Double doubleV){
                return doubleV;
            }
            return 0.0;
        }).toList();
        if (opcode == UnaryOpcode.MAX) {
            return doubleVals.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
        } else if (opcode == UnaryOpcode.MIN) {
            return doubleVals.stream().mapToDouble(Double::doubleValue).min().orElseThrow();
        } else if (opcode == UnaryOpcode.SUM) {
            return doubleVals.stream().mapToDouble(Double::doubleValue).sum();
        } else if (opcode == UnaryOpcode.AVG) {
            return doubleVals.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
        }
        return 0;
    }


    // for one list<doc> the group should be something like {{0, 1}, {2}, {3,4}};
    public static List<Set<Set<Integer>>> divideGroup(List<List<Document>> partialIn, List<AccessPath> groupKey) {
        List<Set<Set<Integer>>> res = new ArrayList<>();
        for (List<Document> documents: partialIn) {
            Map<List<Object>, Set<Integer>> valListToIdx = new HashMap<>();
            for (int i = 0; i < documents.size(); ++i) {
                List<Object> valList = extractValTuple(documents.get(i), groupKey);
                if (!valListToIdx.containsKey(valList)) {
                    valListToIdx.put(valList, new HashSet<>(List.of(i)));
                } else {
                    valListToIdx.get(valList).add(i);
                }
            }
            res.add(new HashSet<>(valListToIdx.values()));
        }
        return res;
    }


    private static List<Object> extractValTuple(Document document, List<AccessPath> groupKey) {
        return groupKey.stream().map(i -> extractVal(document, i)).toList();
    }
    private static Object extractVal(Document document, AccessPath accessPath) {
        return document.getEmbedded(accessPath.getFullPath(), Object.class);
    }


    private static List<Set<Object>> extractValSetList(List<List<Document>> collectionList, AccessPath accessPath) {
        List<Set<Object>> res = new ArrayList<>();
        for (List<Document> collection : collectionList) {
            res.add(extractValSet(collection, accessPath));
        }
        return res;
    }

    private static Set<Object> extractValSet(List<Document> collection, AccessPath accessPath) {
        Set<Object> vals = new HashSet<>();
        for (Document document : collection) {
            Object val = document.getEmbedded(accessPath.getFullPath(), Object.class);
            vals.add(val);
        }
        return vals;
    }
    private static boolean isSuperSetOf(Set<Object> s1, Set<Object> s2) {
        for (Object o : s2) {
            if (!s1.contains(o)) {
                return false;
            }
        }
        return true;
    }

    public boolean canDivideAndMod(List<List<Document>> partialIn, AccessPath accessPath) {
        List<Set<Object>> valSetList = extractValSetList(partialIn, accessPath);
        for (Set<Object> valSet : valSetList) {
            if (valSet.contains(0) || valSet.contains(0.0)) {
                return false;
            }
        }
        return true;
    }
}
