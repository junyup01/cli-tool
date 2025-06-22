package com.jvcats.cli.cmd;

import java.util.List;

/**
 * This interface represents a command that can be executed.
 */
public interface Command {

    String getName();

    void setName(String name);

    List<RunningOption> getOptions();

    RunningOption getOption(String option);

    void addOption(String option);

    void removeOption(String option);

    void execute() throws Exception;
}
