package com.jvcats.cli;

import com.jvcats.cli.cmd.MainCommand;
import com.jvcats.cli.cmd.MainCommandAdapter;
import com.jvcats.cli.cmd.Option;
import com.jvcats.cli.cmd.OptionAdapter;
import com.jvcats.cli.config.DefaultCommandConfig;
import com.jvcats.cli.config.DefaultParserConfig;

import java.util.*;

/**
 * The command parser is responsible for parsing command lines and running the appropriate command task.
 */
public class CommandParser {
    private final MainCommandAdapter mainCommands = new MainCommandAdapter();
    private final ParserConfig parserConfig;

    /**
     * Creates a new command parser with the given parser configuration.
     *
     * @param parserConfig The parser configuration, this will be used for all commands registered with this parser.
     */
    public CommandParser(ParserConfig parserConfig) {
        this.parserConfig = parserConfig;
    }

    /**
     * Creates a new command parser with the default parser configuration.
     */
    public CommandParser() {
        this.parserConfig = new DefaultParserConfig();
    }

    /**
     * Sets priority index with a value less than inorder policy.
     * Any option registered after this method call will have a lower priority compared to the main option.
     *
     * @param main The main command name.
     */
    public void makeMPPriorToLaterRegistered(String main) {
        mainCommands.get(main).setPriorityIndex(CommandConfig.MAIN_OPTION_PRIORITY - 1);
    }

    /**
     * Toggles the priority freezing. One should override registerWithPriority() method to return true for the main command before calling this method.
     * When priority freezing is enabled, any option registered after this method call will have the same priority as the previously registered option.
     * The priority freezing is disabled by default.
     *
     * @param main The main command name.
     */
    public void togglePriorityFreezing(String main) {
        if (!mainCommands.get(main).getConfig().registerWithPriority()) {
            throw new IllegalStateException("Cannot toggle priority freezing when priority is not registered automatically.");
        }
        mainCommands.get(main).togglePriorityFreezing();
    }

    /**
     * Registers a command with the given main, option name and task. If the option name is null or blank, it is set to the default option name.
     * If the task is null, it is set to an empty task.
     *
     * @param main   The main command name.
     * @param option The option name.
     * @param task   The task to run.
     */
    public void register(String main, String option, CommandTask task) {
        register(main, null, option, task);
    }

    /**
     * Registers a command with the given main, configuration, option name and task. If the option name is null or blank, it is set to the default option name.
     * If the task is null, it is set to an empty task.
     *
     * @param main          The main command name.
     * @param commandConfig The command configuration.
     * @param option        The option name.
     * @param task          The task to run.
     */
    public void register(String main, CommandConfig commandConfig, String option, CommandTask task) {
        if (!mainCommands.containsKey(main)) {
            if (commandConfig == null) {
                commandConfig = new DefaultCommandConfig();
            }
            mainCommands.put(main, new MainCommand(commandConfig));
        } else if (commandConfig != null) {
            throw new IllegalArgumentException("No need to set command configuration for existing main command: " + main);
        }
        CommandConfig config = mainCommands.get(main).getConfig();
        if (option == null || option.isBlank()) {
            option = config.mainOptionName();
        } else if (option.equals(config.mainOptionName())) {
            throw new IllegalArgumentException("Invalid option name: " + config.mainOptionName());
        }
        if (task == null) {
            task = (args) -> {
            };
        }
        if (config.registerWithPriority()) {
            mainCommands.get(main).getOptions().put(option, createOption(task, mainCommands.get(main).decrementPriorityIndex(), main, option));
        } else {
            mainCommands.get(main).getOptions().put(option, createOption(task, main, option));
        }
    }

    /**
     * Registers a command with the given main, task and option names. If the task is null, it is set to an empty task.
     * If the option names array is null or empty, it is set to the default option name.
     *
     * @param main    The main command name.
     * @param task    The task to run.
     * @param options The option names.
     */
    public void register(String main, CommandTask task, String... options) {
        register(main, null, task, options);
    }

    /**
     * Registers a command with the given main, configuration, task and option names. If the task is null, it is set to an empty task.
     * If the option names array is null or empty, it is set to the default option name.
     *
     * @param main          The main command name.
     * @param commandConfig The command configuration.
     * @param task          The task to run.
     * @param options       The option names.
     */
    public void register(String main, CommandConfig commandConfig, CommandTask task, String... options) {
        if (!mainCommands.containsKey(main)) {
            if (commandConfig == null) {
                commandConfig = new DefaultCommandConfig();
            }
            mainCommands.put(main, new MainCommand(commandConfig));
        } else if (commandConfig != null) {
            throw new IllegalArgumentException("No need to set command configuration for existing main command: " + main);
        }
        CommandConfig config = mainCommands.get(main).getConfig();
        if (task == null) {
            task = (args) -> {
            };
        }
        if (options == null || options.length == 0) {
            mainCommands.get(main).getOptions().put(config.mainOptionName(), createOption(task, main, config.mainOptionName()));
            return;
        }
        for (String option : options) {
            if (option == null || option.isBlank()) {
                option = config.mainOptionName();
            } else if (option.equals(config.mainOptionName())) {
                throw new IllegalArgumentException("Invalid option name: " + config.mainOptionName());
            }
            if (config.registerWithPriority()) {
                mainCommands.get(main).getOptions().put(option, createOption(task, mainCommands.get(main).decrementPriorityIndex(), main, option));
            } else {
                mainCommands.get(main).getOptions().put(option, createOption(task, main, option));
            }
        }
    }

    /**
     * Unregisters the command with the given main command and options.
     * If the options array is null or empty, only the main option is unregistered.
     * If the option is null or blank, it is set to the main option name.
     *
     * @param main    The main command name.
     * @param options The option names.
     */
    public void unregister(String main, String... options) {
        if (options == null || options.length == 0) {
            mainCommands.get(main).getOptions().remove(mainCommands.get(main).getConfig().mainOptionName());
            return;
        }
        for (String option : options) {
            if (option == null || option.isBlank()) {
                option = mainCommands.get(main).getConfig().mainOptionName();
            }
            mainCommands.get(main).getOptions().remove(option);
        }
    }

    /**
     * Unregisters all commands with the given main command.
     *
     * @param main The main command name.
     */
    public void unregisterAll(String main) {
        mainCommands.remove(main);
    }

    /**
     * Runs the command with the given line.
     *
     * @param line The command line.
     * @throws Exception If an error occurs while running the command.
     */
    public void runCommand(String line) throws Exception {
        if (line == null || line.isBlank()) {
            return;
        }
        List<List<String>> commandsParts = parseArgLine(line.trim());
        for (int i = 0; i < commandsParts.size(); i++) {
            String main = commandsParts.get(i).getFirst();
            if (!mainCommands.containsKey(main)) {
                parserConfig.handleIllegalCommand(commandsParts.get(i).getFirst());
                return;
            }
            if (commandsParts.get(i).size() == 1 || !isExplicitOption(commandsParts.get(i).get(1))) {
                commandsParts.get(i).add(1, ParserConfig.FULL_OPTION_PREFIX + mainCommands.get(main).getConfig().mainOptionName());
            }
        }
        parseArgs(commandsParts);
    }

    private void parseArgs(List<List<String>> commandsParts) throws Exception {
        for (List<String> commandParts : commandsParts) {
            Map<Option, List<String>> args = new LinkedHashMap<>();
            String key = null;
            String main = commandParts.getFirst();
            OptionAdapter optionMap = mainCommands.get(main).getOptions();
            for (int i = 1; i < commandParts.size(); i++) {
                String p = commandParts.get(i);
                if (isExplicitFullOption(p)) {
                    key = p.substring(p.lastIndexOf(ParserConfig.OPTION_PREFIX) + 1);
                    if (!optionMap.containsKey(key)) {
                        parserConfig.handleIllegalOption(key);
                        return;
                    }
                    args.put(optionMap.get(key), new ArrayList<>());
                } else if (isExplicitOption(p)) {
                    String keys = p.substring(1);
                    for (int j = 0; j < keys.length(); j++) {
                        key = keys.substring(j, j + 1);
                        if (!optionMap.containsKey(key)) {
                            parserConfig.handleIllegalOption(key);
                            return;
                        }
                        args.put(optionMap.get(key), new ArrayList<>());
                    }
                } else if (!p.isBlank()) {
                    args.get(optionMap.get(key)).add(p);
                }
            }
            List<Option> sortedKeys = sortOptions(args);
            for (Option k : sortedKeys) {
                k.getTask().run(args.get(k));
            }
        }

    }

    private List<List<String>> parseArgLine(String args) {
        if (args == null || args.isBlank()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        List<List<String>> results = new ArrayList<>();
        StringBuilder currentElement = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);

            if (takeCareOfQuote(c, true)) {
                inQuotes = !inQuotes;
                currentElement.append(c);
                if (!inQuotes) {
                    result.add(currentElement.toString().trim());
                    currentElement.setLength(0);
                }
                continue;
            }

            if (c == parserConfig.escape()) {
                if (i + 1 < args.length()) {
                    char t = args.charAt(i + 1);
                    if (takeCareOfQuote(t, false) && t != parserConfig.escape())
                        currentElement.append(c);
                    currentElement.append(t);
                    i++;
                }
                continue;
            }

            if ((c == parserConfig.delimiter() || takeCareOfEOS(c, true)) && !inQuotes) {
                if (!currentElement.isEmpty()) {
                    result.add(currentElement.toString().trim());
                    currentElement.setLength(0);
                }
                if (takeCareOfEOS(c, true) && !result.isEmpty()) {
                    results.add(result);
                    result = new ArrayList<>();
                }
            } else if (inQuotes || c != ' ') {
                currentElement.append(c);
            }
        }

        if (!currentElement.isEmpty()) {
            result.add(currentElement.toString().trim());
        }

        if (parserConfig.endOfStatement() == ParserConfig.NO_EOS) {
            results.add(result);
        }

        return results;
    }

    private boolean takeCareOfQuote(char q, boolean want) {
        return parserConfig.quote() != ParserConfig.NO_QUOTE && (want == (parserConfig.quote() == q));
    }

    private boolean takeCareOfEOS(char q, boolean want) {
        return parserConfig.endOfStatement() != ParserConfig.NO_EOS && (want == (parserConfig.endOfStatement() == q));
    }

    private Option createOption(CommandTask task, String main, String optionName) {
        return createOption(task, mainCommands.get(main).getConfig().runningPolicy(), main, optionName);
    }

    private Option createOption(CommandTask task, int priority, String main, String optionName) {
        int optionOrder = optionName.equals(mainCommands.get(main).getConfig().mainOptionName()) ? CommandConfig.MAIN_OPTION_PRIORITY : priority;
        Option option = new Option(optionOrder);
        option.setTask(task);
        return option;
    }

    private List<Option> sortOptions(Map<Option, List<String>> options) {
        List<Option> result = new ArrayList<>(options.keySet());
        result.sort((o1, o2) -> {
            int p1 = o1.getPriority();
            int p2 = o2.getPriority();
            return Integer.compare(p2, p1);
        });
        return result;
    }

    private boolean isExplicitOption(String s) {
        return s.startsWith(ParserConfig.OPTION_PREFIX);
    }

    private boolean isExplicitFullOption(String s) {
        return s.startsWith(ParserConfig.FULL_OPTION_PREFIX);
    }

}






