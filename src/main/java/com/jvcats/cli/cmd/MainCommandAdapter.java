package com.jvcats.cli.cmd;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to store the main commands of a parser.
 */
public class MainCommandAdapter {
    private final Map<String, MainCommand> options = new HashMap<>();

    public void put(String key, MainCommand mainCommand) {
        options.put(key, mainCommand);
    }

    public MainCommand get(String key) {
        MainCommand mainCommand = options.get(key);
        if (mainCommand == null) {
            throw new IllegalArgumentException("Main command not found: " + key);
        }
        return mainCommand;
    }

    public boolean containsKey(String key) {
        return options.containsKey(key);
    }

    public void remove(String key) {
        if (options.remove(key) == null) {
            throw new IllegalArgumentException("Main command not found: " + key);
        }
    }
}
