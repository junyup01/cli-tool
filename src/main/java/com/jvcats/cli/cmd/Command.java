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

    List<String> getOptions();

    void addOption(String option);

    void removeOption(String option);

    void clearOptions();

    List<String> getArguments(String option);

    void addArguments(String option, String... args);

    void removeArguments(String option, String... args);

    void clearArguments(String option);

    void execute() throws Exception;
}
