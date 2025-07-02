package com.jvcats.cli.cmd;

import com.jvcats.cli.CommandConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a running option with its arguments and priority.
 */
public class RunningOption {
    private String name;
    private final List<String> args = new ArrayList<>();
    private final int priority;
    private final CommandConfig commandConfig;
    private final OptionAdapter optionAdapter;

    /**
     * Using this constructor is discouraged, one should call addOption() from BaseCommand instead
     */
    public RunningOption(String name, int priority, CommandConfig commandConfig, OptionAdapter optionAdapter) {
        // the logic for "null to main option name" is already implemented in addOption()
        if (!optionAdapter.containsKey(name)) {
            throw new IllegalArgumentException("Undefined option name: " + name);
        }
        this.name = name;
        this.priority = priority;
        this.commandConfig = commandConfig;
        this.optionAdapter = optionAdapter;
    }

    public void addArgument(String... args) {
        this.args.addAll(List.of(args));
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

    public List<String> getArgs() {
        return args;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return name + "=" + args;
    }
}
