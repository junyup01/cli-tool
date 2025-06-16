package com.jvcats.cli.cmd;

import com.jvcats.cli.CommandConfig;

/**
 * This class represents the main command of the CLI. It contains the options and the configuration of the command.
 */
public class MainCommand {
    private final OptionAdapter options = new OptionAdapter();
    private final CommandConfig config;
    private int priorityIndex;
    private boolean freezePriorityCount;

    public MainCommand(CommandConfig config) {
        this.config = config;
        if (config.registerWithPriority()) {
            this.priorityIndex = config.runningPolicy();
        }
    }

    public OptionAdapter getOptions() {
        return options;
    }

    public CommandConfig getConfig() {
        return config;
    }

    public void setPriorityIndex(int priorityIndex) {
        this.priorityIndex = priorityIndex;
    }

    public void togglePriorityFreezing() {
        freezePriorityCount = !freezePriorityCount;
    }

    public int decrementPriorityIndex() {
        if (config.registerWithPriority() && !freezePriorityCount) {
            priorityIndex--;
        }
        return priorityIndex;
    }
}
