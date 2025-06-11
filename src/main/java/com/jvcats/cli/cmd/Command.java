package com.jvcats.cli.cmd;

import java.util.List;
import java.util.PriorityQueue;

/**
 * This interface represents a command that can be executed.
 */
public interface Command {

    String getName();

    PriorityQueue<RunningOption> getOptions();

    void addOption(String option, List<String> args, OptionAdapter optionAdapter);

    void execute() throws Exception;
}
