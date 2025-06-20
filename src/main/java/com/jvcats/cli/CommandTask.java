package com.jvcats.cli;

import com.jvcats.cli.cmd.Argument;

import java.util.List;

@FunctionalInterface
public interface CommandTask {

    void run(List<Argument> args) throws Exception;
}
