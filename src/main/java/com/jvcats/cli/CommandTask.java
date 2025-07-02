package com.jvcats.cli;

import java.util.List;

@FunctionalInterface
public interface CommandTask {

    void run(List<String> args) throws Exception;
}
