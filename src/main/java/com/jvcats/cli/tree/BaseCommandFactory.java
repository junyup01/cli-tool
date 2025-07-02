package com.jvcats.cli.tree;

import com.jvcats.cli.ParserConfig;
import com.jvcats.cli.cmd.BaseCommand;
import com.jvcats.cli.cmd.Command;
import com.jvcats.cli.cmd.MainCommandAdapter;

import java.util.List;
import java.util.Map;

/**
 * The factory to create a base command object.
 */
public class BaseCommandFactory implements CommandFactory {

    @Override
    public Command createCommand(String commandName, Map<String, List<String>> options, MainCommandAdapter mainCommandAdapter, ParserConfig parserConfig) {
        return new BaseCommand(commandName, options, mainCommandAdapter);
    }
}
