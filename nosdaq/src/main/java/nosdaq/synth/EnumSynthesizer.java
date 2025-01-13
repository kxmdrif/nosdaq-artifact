package nosdaq.synth;

import nosdaq.ast.Example;
import nosdaq.ast.Program;
import nosdaq.ast.schema.AttrSchema;
import nosdaq.ast.schema.Schema;
import nosdaq.grammar.*;
import nosdaq.grammar.TreeNode;
import verify.MongoVerifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static nosdaq.trans.MongoDBTranslator.translate;

public final class EnumSynthesizer implements Synthesizer {

    @Override
    public Program synthesize(Grammar grammar, Schema inputSchema, List<Example> examples, int maxProgramSize) throws IOException {
        NonTerminal startSymbol = grammar.getStartSymbol();
        Map<NonTerminal, List<Production>> productionMap = grammar.getLhsToProductionMap();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(new TreeNode(startSymbol));
        Set<TreeNode> visitedProgram = new HashSet<>();
        int completeProgramCounter = 0;
        if (inputSchema != null) {
            productionMap = updateProductionMap(productionMap, inputSchema);
        }

        String fileName = "./build/synthesize.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        while (!queue.isEmpty()) {
            TreeNode currProgram = queue.poll();
            if (getProgramSize(currProgram) < maxProgramSize) {
                TreeNode nonTerminalNode = findANonTerminal(currProgram);
                if (nonTerminalNode == null) {
                    writer.write("Complete program no." + (completeProgramCounter++) + ", Size: " + getProgramSize(currProgram) + ", " + printTree(currProgram) + "\n");
                    Program program = Parser.parseProgram(currProgram);
                    if (program != null) {
                        writer.write(translate(program).replaceAll("\\s", "") + "\n");
                        MongoVerifier verifier = new MongoVerifier();
                        if (verifier.verify(program, inputSchema, examples)) {
                            writer.close();
                            return program;
                        }
                    }
                } else {
                    List<Production> productionsRules = productionMap.get(nonTerminalNode.getSymbol());
                    for (Production production : productionsRules) {
                        TreeNode updatedProgram = expandProgram(currProgram, nonTerminalNode, production, startSymbol);

                        // check duplicates before adding to the queue
                        if (!visitedProgram.contains(updatedProgram)) {
                            visitedProgram.add(updatedProgram);
                            queue.add(updatedProgram);
                        }
                    }
                }
            }
        }
        writer.close();
        return null;
    }

    private static int getProgramSize(TreeNode program) {
        if (program == null) {
            return 0;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(program);
        int size = 1;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            for (TreeNode child : node.getChildren()) {
                queue.add(child);
                ++size;
            }
        }

        return size;
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

    // Not implemented (include well- formed checker)
    private static boolean verify(TreeNode currProgram, List<Example> examples) {
        return false;
    }


    public static Map<NonTerminal, List<Production>> updateProductionMap(Map<NonTerminal, List<Production>> productionMap, Schema schema) {
        List<AttrSchema> attrSchemas = schema.getAttrSchemas();
        for (Map.Entry<NonTerminal, List<Production>> entry : productionMap.entrySet()) {
            NonTerminal nonTerminal = entry.getKey();
            List<Production> productionRules = new ArrayList<>(entry.getValue());
            List<Production> rulesToBeRemovedList = new ArrayList<>();
            List<Production> rulesToBeAdded = new ArrayList<>();

            for (Production production : productionRules) {
                List<Symbol> rhs = production.rhs();
                if (rhs.contains(new Terminal("attrName"))) {
                    rulesToBeRemovedList.add(production);
                    rulesToBeAdded = generateNewProductionRules(attrSchemas, rulesToBeAdded, nonTerminal, rhs);
                }
            }
            // For the current NonTerminal, add new production rules, and remove the production rules containing 'ap'
            productionRules.removeAll(rulesToBeRemovedList);
            productionRules.addAll(rulesToBeAdded);
            productionMap.put(nonTerminal, productionRules);
        }
        return productionMap;
    }

    private static List<Symbol> buildRhs(List<Symbol> rhs, String accessPath) {
        List<Symbol> newRhs = new ArrayList<>();
        for (Symbol s : rhs) {
            if (!s.getName().equals("attrName")) {
                newRhs.add(s);
            } else {
                newRhs.add(new Terminal(accessPath));
            }
        }
        return newRhs;
    }

    private static List<Production> generateNewProductionRules(List<AttrSchema> attrSchemas, List<Production> rulesToBeAdded,
                                                               NonTerminal nonTerminal, List<Symbol> rhs) {
        for (AttrSchema attrSchema : attrSchemas) {
            String ap = attrSchema.getAccessPath().toString();
            rulesToBeAdded.add(new Production(nonTerminal, buildRhs(rhs, ap)));
        }
        return rulesToBeAdded;
    }
}
