package com.jvcats.cli.cmd;

import com.jvcats.cli.CommandConfig;
import com.jvcats.cli.ParserConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a running option with its arguments and priority.
 */
public class RunningOption {
    private String name;
    private final List<Argument> args = new ArrayList<>();
    private final int priority;
    private final ParserConfig parserConfig;
    private final CommandConfig commandConfig;
    private final OptionAdapter optionAdapter;

    /**
     * Using this constructor is discouraged, one should call addOption() from BaseCommand instead
     */
    public RunningOption(String name, int priority, ParserConfig parserConfig, CommandConfig commandConfig, OptionAdapter optionAdapter) {
        // the logic for "null to main option name" is already implemented in addOption()
        if (!optionAdapter.containsKey(name)) {
            throw new IllegalArgumentException("Undefined option name: " + name);
        }
        this.name = name;
        this.priority = priority;
        this.parserConfig = parserConfig;
        this.commandConfig = commandConfig;
        this.optionAdapter = optionAdapter;
    }

    public void addArgument(String... args) {
        List<Argument> arguments = new ArrayList<>();
        for (String s : args) {
            arguments.add(new Argument(parserConfig, s));
        }
        this.args.addAll(arguments);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name = commandConfig.mainOptionName();
        }
        if (!optionAdapter.containsKey(name)) {
            throw new IllegalArgumentException("Undefined option name: " + name);
        }
        this.name = name;
    }

    public List<Argument> getArgs() {
        return args;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return name + "=" + String.join(" ", args.stream().map(Argument::getContent).toList());
    }
}
