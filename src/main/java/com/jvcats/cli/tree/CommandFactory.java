package com.jvcats.cli.tree;

import com.jvcats.cli.ParserConfig;
import com.jvcats.cli.cmd.Command;
import com.jvcats.cli.cmd.MainCommandAdapter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CommandFactory is responsible for creating a Command object.
 * One can implement their own CommandFactory to create custom Command objects.
 */
public interface CommandFactory {

    Command createCommand(String commandName, Map<String, List<String>> options, MainCommandAdapter mainCommandAdapter, ParserConfig parserConfig);

    default Command createCommand(String commandName, MainCommandAdapter mainCommandAdapter, ParserConfig parserConfig) {
        return createCommand(commandName, new LinkedHashMap<>(), mainCommandAdapter, parserConfig);
    }
}
