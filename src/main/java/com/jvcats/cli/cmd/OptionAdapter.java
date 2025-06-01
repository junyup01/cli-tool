package com.jvcats.cli.cmd;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to store the options of a main command.
 */
public class OptionAdapter {
    private final Map<String, Option> options = new HashMap<>();

    public void put(String key, Option option) {
        options.put(key, option);
    }

    public Option get(String key) {
        Option option = options.get(key);
        if (option == null) {
            throw new IllegalArgumentException("Option not found: " + key);
        }
        return option;
    }

    public boolean containsKey(String key) {
        return options.containsKey(key);
    }

    public void remove(String key) {
        if (options.remove(key) == null) {
            throw new IllegalArgumentException("Option not found: " + key);
        }
    }
}
