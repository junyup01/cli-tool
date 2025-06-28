package com.jvcats.cli.tree;

import java.util.List;

public interface Node {

    /**
     * Adds a child node to this node.
     *
     * @param node the child node to add.
     */
    void addChild(Node node);

    /**
     * Removes all children from this node and returns them as a list.
     *
     * @return a list of all children removed from this node.
     */
    List<Node> removeAllChildren();

    /**
     * Removes a child node from this node.
     *
     * @param node the child node to remove.
     */
    void removeChild(Node node);

    /**
     * Returns a list of all child nodes of this node.
     *
     * @return the child nodes.
     */
    List<Node> getChildren();

    /**
     * Returns the number of descendants of this node.
     *
     * @return the number of descendants.
     */
    default int descendantCount(){
        int count = 0;
        for (Node child : getChildren()) {
            count += child.descendantCount() + 1;
        }
        return count;
    }

    /**
     * Sets the parent of this node.
     *
     * @param node the parent node.
     */
    void setParent(Node node);

    /**
     * Returns the parent of this node.
     *
     * @return the parent node.
     */
    Node getParent();

}
