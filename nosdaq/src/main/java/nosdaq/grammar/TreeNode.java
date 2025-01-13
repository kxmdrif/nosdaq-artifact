package nosdaq.grammar;

import java.util.*;

public final class TreeNode {
    private Symbol symbol;
    private List<TreeNode> children;

    public TreeNode(Symbol symbol) {
        this.symbol = Objects.requireNonNull(symbol);
        this.children = new ArrayList<>();
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public void addChild(TreeNode childNode) {
        children.add(childNode);
    }

    public int getSize() {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(this);
        int size = 1;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            queue.addAll(node.getChildren());
            size += node.getChildren().size();
        }

        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode treeNode = (TreeNode) o;
        return symbol.equals(treeNode.symbol) && children.equals(treeNode.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, children);
    }
}

