package nosdaq.synth;

import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.ast.schema.Type;
import nosdaq.grammar.*;
import org.bson.Document;
import verify.MongoVerifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static nosdaq.trans.MongoDBTranslator.translate;
import static nosdaq.utils.Helper.constructAp;

public class EnumSynthesizer2 implements Synthesizer {
    @Override
    public Program synthesize(Grammar grammar, Schema inputSchema, List<Example> examples, final int maxProgramSize) throws IOException {
        NonTerminal startSymbol = grammar.getStartSymbol();
        Map<NonTerminal, List<Production>> productionMap = grammar.getLhsToProductionMap();
        if (inputSchema != null) {
            productionMap = updateProductionMap(productionMap, inputSchema, examples);
        }

        Queue<TreeNode> queue = new LinkedList<>();
        // queue.add(new TreeNode(startSymbol));
        // Directly add find() and aggregate() to the queue
        TreeNode findNode = generateFirstFindQuery(examples, inputSchema);
        if (findNode != null) {
            queue.add(findNode);
        }
        queue.add(generateFirstAggregateQuery());

        Set<TreeNode> visitedPrograms = new HashSet<>();
        int completeProgramCounter = 0;
        String fileName = "./build/synthesize.txt";
        String possibleProgram = "./build/possible_program.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        BufferedWriter possibleProgramWriter = new BufferedWriter(new FileWriter(possibleProgram));

        while (!queue.isEmpty()) {
            TreeNode currProgram = queue.poll();
            if (isProgramValid(currProgram, inputSchema, maxProgramSize)) {
                TreeNode nonTerminalNode = findANonTerminal(currProgram);
                if (nonTerminalNode == null) {
                    writer.write("Complete program no." + (++completeProgramCounter) + ", Size: " + currProgram.getSize() + ", " + printTree(currProgram) + "\n");
                    Program program = Parser.parseProgram(currProgram);
                    if (program != null) {
                        writer.write(translate(program).replaceAll("\\s", "") + "\n");
                        MongoVerifier verifier = new MongoVerifier();
                        if (verifier.verify(program, inputSchema, examples)) {
                            possibleProgramWriter.write("Size: " + currProgram.getSize() + ", " + printTree(currProgram) + "\n");
                            possibleProgramWriter.write(translate(program) + "\n");
                            writer.close();
                            possibleProgramWriter.close();
                            return program;
                        }
                    }
                } else {
                    List<Production> productionsRules = productionMap.get(nonTerminalNode.getSymbol());
                    for (Production production : productionsRules) {
                        TreeNode updatedProgram = expandProgram(currProgram, nonTerminalNode, production, startSymbol);

                        // check duplicates before adding to the queue
                        if (!visitedPrograms.contains(updatedProgram)) {
                            visitedPrograms.add(updatedProgram);
                            queue.add(updatedProgram);
                        }
                    }
                }
            }
        }

        writer.close();
        possibleProgramWriter.close();
        return null;
    }

    private static boolean isProgramValid(TreeNode program, Schema schema, final int maxProgramSize) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(program);
        int size = 1;
        boolean isFind = false;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            switch (node.getSymbol().getName()) {
                case "not" -> {
                    String childName = node.getChildren().get(0).getSymbol().getName();
                    if (childName.equals("true") || childName.equals("not") || childName.equals("and") || childName.equals("or")) {
                        return false;
                    }
                }
                case "elemmatch", "size", "unwind" -> {
                    String childName = node.getChildren().get(0).getChildren().get(0).getSymbol().getName();
                    if (!schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(childName), Type.ARRAY))) {
                        return false;
                    }
                }
                case "limit", "skip" -> {  // limit and skip takes in positive integers
                    if (node.getChildren().get(0).getSymbol().getName().equals("int")) {
                        String childName = node.getChildren().get(0).getChildren().get(0).getSymbol().getName();
                        if (Integer.parseInt(childName) <= 0) {
                            return false;
                        }
                    }
                }
                case "lop" -> {  // size takes number
                    if (node.getChildren().get(1).getSymbol().getName().equals("size")) {
                        String childName = node.getChildren().get(2).getSymbol().getName();
                        if (!(childName.equals("E") || childName.equals("CE") || childName.equals("AE") || childName.equals("Value")
                                || childName.equals("IntLiteral") || childName.equals("int"))) {
                            return false;
                        } else if (childName.equals("int") && (Integer.parseInt(node.getChildren().get(2).getChildren().get(0).getSymbol().getName()) < 0)) {
                            return false;
                        }
                    } else if (node.getChildren().get(1).getSymbol().getName().equals("type")) { // type takes number (1-19, -1, 127) or Alias
                        String childName = node.getChildren().get(2).getSymbol().getName();
                        if (!(childName.equals("E") || childName.equals("CE") || childName.equals("AE") || childName.equals("Value")
                                || childName.equals("IntLiteral") || childName.equals("StringLiteral")
                                || childName.equals("int") || childName.equals("str"))) {
                            return false;
                        } else {
                            if (childName.equals("int")) {
                                Integer type = Integer.parseInt(node.getChildren().get(2).getChildren().get(0).getSymbol().getName());
                                List<Integer> availableInt = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, -1, 127));
                                if (!availableInt.contains(type)) {
                                    return false;
                                }
                            } else if (childName.equals("str")) {
                                String type = node.getChildren().get(2).getChildren().get(0).getSymbol().getName();
                                List<String> availableAlias = new ArrayList<>(Arrays.asList("double", "string", "object", "array", "binData", "objectId", "bool", "date",
                                        "null", "regex", "javascript", "int", "timestamp", "long", "decimal", "minKey", "maxKey"));
                                if (!availableAlias.contains(type)) {
                                    return false;
                                }
                            }
                        }
                    } else if (node.getChildren().get(1).getSymbol().getName().equals("Value")) { // type takes String or number
                        return false;
                    } else if (node.getChildren().get(2).getSymbol().getName().equals("size") ||
                            node.getChildren().get(2).getSymbol().getName().equals("type")) {
                        return false;
                    } else if (isFind && node.getChildren().get(1).getSymbol().getName().equals("ap")) {
                        if (node.getChildren().get(2).getSymbol().getName().equals("ap")) {
                            return false;
                        }
                    }
                }
                case "and", "or" -> {
                    if (node.getChildren().get(0).getSymbol().getName().equals("true") ||
                            node.getChildren().get(1).getSymbol().getName().equals("true")) {
                        return false;
                    }
                }
                case "match" -> {
                    if (node.getChildren().get(0).getSymbol().getName().equals("true")) {
                        return false;
                    }
                }
                case "find" -> isFind = true;
                case "aggregate" -> { /* Prune duplicate limit, skip, count */
                    Queue<TreeNode> traverseQueue = new LinkedList<>();
                    Set<TreeNode> visited = new HashSet<>();
                    traverseQueue.add(node.getChildren().get(0));
                    while (!traverseQueue.isEmpty()) {
                        TreeNode tempNode = traverseQueue.poll();
                        for (TreeNode child : tempNode.getChildren()) {
                            if (child.getSymbol().getName().equals("limit") || child.getSymbol().getName().equals("skip") ||
                                    child.getSymbol().getName().equals("count") ) {
                                if (visited.contains(child)) {
                                    return false;
                                }
                                visited.add(child);
                            }
                            traverseQueue.add(child);
                        }
                    }
                }
                case "sort" -> {  // sort ( empty empty ) pipeline stage causes MongoCommandException
                    if (!(!node.getChildren().get(0).getSymbol().getName().equals("empty") && !node.getChildren().get(1).getSymbol().getName().equals("empty"))) {
                        return false;
                    }
                    if (node.getChildren().get(0).getChildren().size() > 0) { // eliminate the program if the first field or sortOrder is invalid
                        String expression1 = node.getChildren().get(0).getChildren().get(0).getSymbol().getName();
                        if (expression1.equals("Value")) {
                            return false;
                        } else if (node.getChildren().get(1).getChildren().size() > 0) {
                            String sortOrder1 = node.getChildren().get(1).getChildren().get(0).getSymbol().getName();
                            List<String> invalidSortOrders = new ArrayList<>(Arrays.asList("emptyList", "trueValue", "falseValue", "isodate", "str", "null"));
                            if ((expression1.equals("emptyList") && !sortOrder1.equals("emptyList")) ||
                                    (!expression1.equals("emptyList") && sortOrder1.equals("emptyList"))) { // InputMismatchException
                                return false;
                            } else if (invalidSortOrders.contains(sortOrder1)) {
                                return false;
                            }
                        }
                    }
                }
                case "project" -> {  // project ( empty empty empty ) causes MongoCommandException
                    if (node.getChildren().get(0).getSymbol().getName().equals("empty") &&
                            node.getChildren().get(1).getSymbol().getName().equals("empty") &&
                            node.getChildren().get(2).getSymbol().getName().equals("empty")) {
                        return false;
                    } else if (!(!node.getChildren().get(1).getSymbol().getName().equals("empty") && !node.getChildren().get(2).getSymbol().getName().equals("empty"))) {
                        return false;
                    }
                }
                case "group" -> {
                    if (!(!node.getChildren().get(1).getSymbol().getName().equals("empty") && !node.getChildren().get(2).getSymbol().getName().equals("empty"))) {
                        return false;
                    }
                }
                case "bop" -> {
                    String child1Name = node.getChildren().get(0).getSymbol().getName();
                    TreeNode child2 = node.getChildren().get(1);
                    TreeNode child3 = node.getChildren().get(2);
                    if (child1Name.equals("add") || child1Name.equals("sub")) {
                        if (child2.getSymbol().getName().equals("ap")) {
                            String ap = child2.getChildren().get(0).getSymbol().getName();
                            if (!schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.INT)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.DOUBLE)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.DATE))) {
                                return false;
                            }
                        } else if (child2.getSymbol().getName().equals("Array") || child2.getSymbol().getName().equals("BoolLiteral") ||
                                child2.getSymbol().getName().equals("StringLiteral") || child2.getSymbol().getName().equals("null")) {
                            return false;
                        }

                        if (child3.getSymbol().getName().equals("ap")) {
                            String ap = child3.getChildren().get(0).getSymbol().getName();
                            if (!schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.INT)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.DOUBLE)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.DATE))) {
                                return false;
                            }
                        } else if (child3.getSymbol().getName().equals("Array") || child3.getSymbol().getName().equals("BoolLiteral") ||
                                child3.getSymbol().getName().equals("StringLiteral") || child3.getSymbol().getName().equals("null")) {
                            return false;
                        }
                    } else if (child1Name.equals("mul") || child1Name.equals("div") || child1Name.equals("mod")) {
                        if (child2.getSymbol().getName().equals("ap")) {
                            String ap = child2.getChildren().get(0).getSymbol().getName();
                            if (!schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.INT)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.DOUBLE))) {
                                return false;
                            }
                        } else if (child2.getSymbol().getName().equals("Array") || child2.getSymbol().getName().equals("BoolLiteral") ||
                                child2.getSymbol().getName().equals("StringLiteral") || child2.getSymbol().getName().equals("null") ||
                                child2.getSymbol().getName().equals("ISODate") ) {
                            return false;
                        }

                        if (child3.getSymbol().getName().equals("ap")) {
                            String ap = child3.getChildren().get(0).getSymbol().getName();
                            if (!schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.INT)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.DOUBLE))) {
                                return false;
                            }
                        } else if (child3.getSymbol().getName().equals("Array") || child3.getSymbol().getName().equals("BoolLiteral") ||
                                child3.getSymbol().getName().equals("StringLiteral") || child3.getSymbol().getName().equals("null") ||
                                child3.getSymbol().getName().equals("ISODate") ) {
                            return false;
                        }
                    }
                }
                case "uop" -> {
                    String child1Name = node.getChildren().get(0).getSymbol().getName();
                    TreeNode child2 = node.getChildren().get(1);
                    if (child1Name.equals("abs") || child1Name.equals("ceil")) {
                        if (child2.getSymbol().getName().equals("ap")) {
                            String ap = child2.getChildren().get(0).getSymbol().getName();
                            if (!schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.INT)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.DOUBLE))) {
                                return false;
                            }
                        } else if (child2.getSymbol().getName().equals("Array") || child2.getSymbol().getName().equals("BoolLiteral") ||
                                child2.getSymbol().getName().equals("StringLiteral") || child2.getSymbol().getName().equals("null") ||
                                child2.getSymbol().getName().equals("ISODate") ) {
                            return false;
                        }
                    } else if (child1Name.equals("min") || child1Name.equals("max") || child1Name.equals("avg") || child1Name.equals("sum")) {
                        if (child2.getSymbol().getName().equals("ap")) {
                            String ap = child2.getChildren().get(0).getSymbol().getName();
                            if (!schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.INT)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.DOUBLE)) ||
                                    !schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.ARRAY))) {
                                return false;
                            }
                        } else if (child2.getSymbol().getName().equals("BoolLiteral") ||
                                child2.getSymbol().getName().equals("StringLiteral") || child2.getSymbol().getName().equals("null") ||
                                child2.getSymbol().getName().equals("ISODate") ) {
                            return false;
                        }
                    }
                }
                case "filter" -> {
                    TreeNode child1 = node.getChildren().get(0);
                    if (child1.getSymbol().getName().equals("ap")) {
                        String ap = child1.getChildren().get(0).getSymbol().getName();
                        if (!schema.getAttrSchemaSet().contains(new AttrSchema(constructAp(ap), Type.ARRAY))) {
                            return false;
                        }
                    } else {
                        if (child1.getSymbol().getName().equals("BoolLiteral") || child1.getSymbol().getName().equals("FloatLiteral") ||
                                child1.getSymbol().getName().equals("IntLiteral") || child1.getSymbol().getName().equals("StringLiteral") ||
                                child1.getSymbol().getName().equals("null") || child1.getSymbol().getName().equals("ISODate")) {
                            return false;
                        }
                    }
                }
                case "count" -> { // count ( str(  ) ) -> false
                    if (node.getChildren().size() > 0) {
                        TreeNode child = node.getChildren().get(0);
                        if (child.getSymbol().getName().equals("str")) {
                            if (child.getChildren().size() > 0 && child.getChildren().get(0).getSymbol().getName().equals("")) {
                                return false;
                            }
                        }
                    }
                }
            }

            queue.addAll(node.getChildren());
            size += node.getChildren().size();
            if (size > maxProgramSize) {
                return false;
            }
        }

        return true;
    }

    // Find the uppermost and left-most non-terminal node from the current program
    private static TreeNode findANonTerminal(TreeNode program) {
        if (program.getSymbol() instanceof NonTerminal) {
            return program;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(program);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            for (TreeNode child : node.getChildren()) {
                if (child.getSymbol() instanceof NonTerminal) {
                    return child;
                } else {
                    queue.add(child);
                }
            }
        }

        return null; // all nodes are terminal symbols
    }

    // Process the rhs (of the production rule) and generate a TreeNode (to replace the NonTerminal node)
    public static TreeNode generateNewTree(List<Symbol> rhs) {
        TreeNode newTree = new TreeNode(rhs.get(0));
        if (rhs.size() > 1) {
            int rhsIndex = 1;
            boolean addAsChild = false;
            while (rhsIndex < rhs.size()) { // process all the symbols on the rhs
                Symbol s = rhs.get(rhsIndex);
                if (s.getName().equals("(")) { // add the Symbols after "(" as the children
                    addAsChild = true;
                }

                if (addAsChild && rhsIndex < (rhs.size() - 1)) {
                    s = rhs.get(rhsIndex + 1);
                    if (!s.getName().equals(")")) {
                        TreeNode childNode = new TreeNode(s);
                        if (s.getName().equals("ap")) {
                            rhsIndex += 2;
                            Symbol attrName = rhs.get(rhsIndex + 1);
                            childNode.addChild(new TreeNode(attrName));
                        }
                        newTree.addChild(childNode);
                    }
                }
                ++rhsIndex;
            }
        }

        return newTree;
    }

    // Expand the current program using the given production rule, and return the updated program
    private static TreeNode expandProgram(TreeNode currProgram, TreeNode nonTerminalNode, Production production, NonTerminal startSymbol) {
        List<Symbol> rhs = production.rhs();
        TreeNode newTreeNode = generateNewTree(rhs);

        if (nonTerminalNode.getSymbol().equals(startSymbol)) {
            return newTreeNode;
        }

        return updateTree(currProgram, nonTerminalNode, newTreeNode);
    }

    // Copy the tree while replacing the processing nonTerminal symbol with new TreeNode
    private static TreeNode updateTree(TreeNode currProgram, TreeNode nonTerminal, TreeNode newTreeNode) {
        if (currProgram == null) {
            return null;
        }

        List<TreeNode> children = new ArrayList<>();
        for (TreeNode child : currProgram.getChildren()) {
            if (child != nonTerminal) {
                children.add(updateTree(child, nonTerminal, newTreeNode));
            } else {
                children.add(newTreeNode);
            }
        }

        TreeNode updatedTree = new TreeNode(currProgram.getSymbol());
        updatedTree.setChildren(children);
        return updatedTree;
    }

    public static String printTree(TreeNode treeNode) {
        List<String> res = new ArrayList<>();
        if (treeNode == null) {
            return res.stream().collect(Collectors.joining(" "));
        }
        printTreeHelper(treeNode, res);
        return res.stream().collect(Collectors.joining(" "));
    }

    private static void printTreeHelper(TreeNode treeNode, List<String> res) {
        if (treeNode == null) {
            return;
        }

        res.add(treeNode.getSymbol().getName());
        if (!treeNode.getChildren().isEmpty()) {
            res.add("(");
            for (TreeNode node : treeNode.getChildren()) {
                printTreeHelper(node, res);
            }
            res.add(")");
        }
    }

    // Update production map according to attribute names in Schema
    public static Map<NonTerminal, List<Production>> updateProductionMap(Map<NonTerminal, List<Production>> productionMap, Schema schema, List<Example> examples) {
        List<AttrSchema> attrSchemas = schema.getAttrSchemas();
        for (Map.Entry<NonTerminal, List<Production>> entry : productionMap.entrySet()) {
            NonTerminal lhs = entry.getKey();
            List<Production> productionRules = new ArrayList<>(entry.getValue());
            List<Production> rulesToBeRemoved = new ArrayList<>();
            List<Production> rulesToBeAdded = new ArrayList<>();

            for (Production production : productionRules) {
                List<Symbol> rhs = production.rhs();
                if (rhs.contains(new Terminal("attrName")) || rhs.contains(new Terminal("newAttrName"))) {
                    rulesToBeRemoved.add(production);
                    rulesToBeAdded = generateNewProductionRules(attrSchemas, rulesToBeAdded, lhs, rhs, examples);
                }
            }

            // For the current lhs, remove the production rules containing 'ap' and add new production rules
            productionRules.removeAll(rulesToBeRemoved);
            productionRules.addAll(rulesToBeAdded);
            productionMap.put(lhs, productionRules);
        }

        return productionMap;
    }

    private static List<Production> generateNewProductionRules(List<AttrSchema> attrSchemas, List<Production> rulesToBeAdded,
                                                               NonTerminal lhs, List<Symbol> rhs, List<Example> examples) {
        Set<String> distinctKeys = new HashSet<>();
        for (Example example : examples) {
            for (Document document : example.getOutput()) {
                distinctKeys.addAll(document.keySet());
            }
        }

        for (AttrSchema attrSchema : attrSchemas) {
            String accessPath = attrSchema.getAccessPath().toString();
            if (distinctKeys.contains(accessPath)) {
                distinctKeys.remove(accessPath);
            }
        }

        Set<Production> rulesToBeAddedSet = new HashSet<>(rulesToBeAdded);
        for (AttrSchema attrSchema : attrSchemas) {
            String accessPath = attrSchema.getAccessPath().toString();
            List<Symbol> newRhs = new ArrayList<>();
            for (Symbol s : rhs) {
                if (!s.getName().equals("attrName") && !s.getName().equals("newAttrName")) {
                    newRhs.add(s);
                } else if (s.getName().equals("attrName")) {
                    newRhs.add(new Terminal(accessPath));
                } else {
                    for (String newAccessPath : distinctKeys) {
                        newRhs.add(new Terminal(newAccessPath));
                    }
                }
            }

            rulesToBeAddedSet.add(new Production(lhs, newRhs));
        }

        return new ArrayList<>(rulesToBeAddedSet);
    }

    /* Directly update Projection List based on output */
    TreeNode generateFirstFindQuery(List<Example> examples, Schema schema) {
        Set<String> attrSet = new HashSet<>();
        for (AttrSchema attrSchema : schema.getAttrSchemas()) {
            attrSet.add(attrSchema.getAccessPath().toString());
        }

        TreeNode findNode = new TreeNode(new Terminal("find"));
        findNode.addChild(new TreeNode(new NonTerminal("P")));

        Set<String> distinctKeys = new HashSet<>();
        for (Example example : examples) {
            for (Document document : example.getOutput()) {
                distinctKeys.addAll(document.keySet());
            }
        }

        /* If there are rename operation, return null */
        for (String key : distinctKeys) {
            if (!attrSet.contains(key)) {
                return null;
            }
        }

        List<String> keys = new ArrayList<>(distinctKeys);
        TreeNode node;
        if (distinctKeys.size() == 0) {
            node = new TreeNode(new Terminal("empty"));
        } else {
            node = new TreeNode(new Terminal("cons"));
        }

        TreeNode projNode = node;
        for (int i = 0; i < distinctKeys.size(); ++i) {
            if (i != 0) {
                node = node.getChildren().get(1);
            }

            TreeNode childNode = new TreeNode(new Terminal("ap"));
            childNode.addChild(new TreeNode(new Terminal(keys.get(i))));
            node.addChild(childNode);
            if (i != distinctKeys.size() - 1) {
                node.addChild(new TreeNode(new Terminal("cons")));
            } else {
                node.addChild(new TreeNode(new Terminal("empty")));
            }
        }

        findNode.addChild(projNode);

        return findNode;
    }

    TreeNode generateFirstAggregateQuery() {
        TreeNode aggregateNode = new TreeNode(new Terminal("aggregate"));
        aggregateNode.addChild(new TreeNode(new NonTerminal("SL")));

        return aggregateNode;
    }
}
