package com.jvcats.cli.cmd;

import com.jvcats.cli.CommandTask;

/**
 * This class represents an option for a command.
 */
public class Option {
    private final int priority;
    private CommandTask task;

    public Option(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setTask(CommandTask task) {
        this.task = task;
    }

    public CommandTask getTask() {
        return task;
    }
}
