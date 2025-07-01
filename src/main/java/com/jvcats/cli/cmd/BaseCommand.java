package com.jvcats.cli.cmd;

import com.jvcats.cli.ParserConfig;
import com.jvcats.cli.tree.Node;

import java.util.*;

/**
 * Base class for running commands.
 */
public class BaseCommand implements Command {
    private String name;
    private final List<RunningOption> options = new ArrayList<>();
    private final Map<Integer, Integer> priorityMap = new HashMap<>();
    private final ParserConfig parserConfig;
    private final MainCommandAdapter mainCommandAdapter;
    private List<Node> children = new ArrayList<>();
    private Node parent;

    /**
     * Using this constructor is discouraged, one should call createCommand() from CommandParser instead
     */
    public BaseCommand(String name, Map<String, List<String>> options, MainCommandAdapter mainCommandAdapter, ParserConfig parserConfig) {
        if (!mainCommandAdapter.containsKey(name)) {
            throw new IllegalArgumentException("Undefined command name: " + name);
        }
        this.name = name;
        this.parserConfig = parserConfig;
        this.mainCommandAdapter = mainCommandAdapter;
        for (Map.Entry<String, List<String>> entry : options.entrySet()) {
            addOption(entry.getKey());
            addArguments(entry.getKey(), entry.getValue().toArray(new String[0]));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        MainCommand mainCommand = mainCommandAdapter.get(name);
        if (mainCommand == null) {
            throw new IllegalArgumentException("Undefined command name: " + name);
        }
        // check for all options when the main command name is changed
        for (RunningOption option : options) {
            if (!mainCommand.getOptions().containsKey(option.getName())) {
                throw new IllegalArgumentException("Undefined option name: " + option.getName());
            }
        }
        this.name = name;
    }

    @Override
    public void addOption(String option) {
        MainCommand mainCommand = mainCommandAdapter.get(name);
        if (option == null) {
            option = mainCommand.getConfig().mainOptionName();
        }
        OptionAdapter optionAdapter = mainCommand.getOptions();
        // allow for duplicate options
        Option optionObj = optionAdapter.get(option);
        int priority = optionObj.priority();
        if (priorityMap.containsKey(priority)) {
            priorityMap.put(priority, priorityMap.get(priority) + 1);
        } else {
            priorityMap.put(priority, 1);
        }
        options.add(new RunningOption(option, priorityMap.get(priority), parserConfig, mainCommand.getConfig(), optionAdapter));
    }

    private RunningOption getOption(String option) {
        if (option == null) {
            option = mainCommandAdapter.get(name).getConfig().mainOptionName();
        }
        for (RunningOption opt : options) {
            if (opt.getName().equals(option)) {
                return opt;
            }
        }
        throw new IllegalArgumentException("Undefined option name: " + option);
    }

    @Override
    public void removeOption(String option) {
        options.remove(getOption(option));
    }

    @Override
    public void clearOptions() {
        options.clear();
    }

    @Override
    public List<String> getOptions() {
        List<String> result = new ArrayList<>();
        for (RunningOption option : options) {
            result.add(option.getName());
        }
        return result;
    }

    @Override
    public List<String> getArguments(String option) {
        return getOption(option).getArgs().stream().map(Argument::getContent).toList();
    }

    @Override
    public void addArguments(String option, String... args) {
        getOption(option).addArgument(args);
    }

    @Override
    public void removeArguments(String option, String... args) {
        List<Argument> arguments = getOption(option).getArgs();
        for (String arg : args) {
            arguments.removeIf(a -> a.getContent().equals(arg));
        }
    }

    @Override
    public void clearArguments(String option) {
        getOption(option).getArgs().clear();
    }

    @Override
    public void execute() throws Exception {
        List<RunningOption> temp = new ArrayList<>(options);
        OptionAdapter optionAdapter = mainCommandAdapter.get(name).getOptions();
        temp.sort((o1, o2) -> {
            Option option = optionAdapter.get(o1.getName());
            Option option2 = optionAdapter.get(o2.getName());
            if (option.priority() == option2.priority()) {
                return o1.getPriority() - o2.getPriority();
            }
            return option2.priority() - option.priority();
        });
        for (RunningOption option : temp) {
            optionAdapter.get(option.getName()).task().run(option.getArgs());
        }

        for (Node child : children) {
            ((Command) child).execute();
        }
    }

    @Override
    public String toString() {
        return name + options;
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
        this.parent = node;
    }

    @Override
    public Node getParent() {
        return parent;
    }
}
