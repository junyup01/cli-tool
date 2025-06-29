package com.jvcats.cli.tree;

import com.jvcats.cli.cmd.*;

import java.util.*;

/**
 * Abstract class for basic commands.
 */
public abstract class AbstractCommand implements Command {
    protected String name;
    protected Map<String, List<String>> options;
    protected List<Node> children = new ArrayList<>();
    protected Node parent;

    public AbstractCommand(String name, Map<String, List<String>> options) {
        this.name = name;
        this.options = options;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getOptions() {
        return new ArrayList<>(options.keySet());
    }

    @Override
    public void addOption(String option) {
        options.put(option, new ArrayList<>());
    }

    @Override
    public void removeOption(String option) {
        options.remove(option);
    }

    @Override
    public void clearOptions() {
        options.clear();
    }

    @Override
    public List<String> getArguments(String option) {
        return options.get(option);
    }

    @Override
    public void addArguments(String option, String... args) {
        options.get(option).addAll(List.of(args));
    }

    @Override
    public void removeArguments(String option, String... args) {
        options.get(option).removeAll(List.of(args));
    }

    @Override
    public void clearArguments(String option) {
        options.get(option).clear();
    }

    @Override
    public void execute() throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

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
