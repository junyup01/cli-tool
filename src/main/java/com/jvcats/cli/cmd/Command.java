package com.jvcats.cli.cmd;

import com.jvcats.cli.tree.Node;

import java.util.List;

/**
 * This interface represents a command that can be executed.
 */
public interface Command extends Node {

    String NOP_COMMAND = "__NOP__";

    String getName();

    void setName(String name);

    List<RunningOption> getOptions();

    RunningOption getOption(String option);

    void addOption(String option);

    void removeOption(String option);

    void execute() throws Exception;
}
