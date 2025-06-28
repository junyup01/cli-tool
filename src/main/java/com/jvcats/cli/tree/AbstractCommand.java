package com.jvcats.cli.tree;

import com.jvcats.cli.cmd.*;

import java.util.*;

/**
 * Abstract class for basic commands.
 */
public abstract class AbstractCommand implements Command {
    protected String name;
    protected List<Node> children = new ArrayList<>();
    protected Node parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public abstract List<RunningOption> getOptions();

    @Override
    public abstract RunningOption getOption(String option);

    @Override
    public abstract void addOption(String option);

    @Override
    public abstract void removeOption(String option);

    @Override
    public abstract void execute() throws Exception;

    @Override
    public void addChild(Node node) {
        node.setParent(this);
        children.add(node);
    }

    @Override
    public List<Node> removeAllChildren() {
        List<Node> children = this.children;
        this.children = new ArrayList<>();
        for (Node child : children) {
            child.setParent(null);
        }
        return children;
    }

    @Override
    public void removeChild(Node node) {
        children.remove(node);
        node.setParent(null);
    }

    @Override
    public List<Node> getChildren() {
        return children;
    }

    @Override
    public void setParent(Node node) {
        parent = node;
    }

    @Override
    public Node getParent() {
        return parent;
    }
}
