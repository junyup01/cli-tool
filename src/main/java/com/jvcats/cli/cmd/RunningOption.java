package com.jvcats.cli.cmd;

import java.util.List;

/**
 * This class represents a running option with its arguments and priority.
 */
public record RunningOption(Option option, List<String> args, int priority) implements Comparable<RunningOption> {

    @Override
    public int compareTo(RunningOption o) {
        Option option2 = o.option();
        if (option.getPriority() == option2.getPriority()) {
            return priority - o.priority();
        }
        return option2.getPriority() - option.getPriority();
    }

}
