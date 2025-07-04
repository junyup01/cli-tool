package com.jvcats.cli;

import com.jvcats.cli.cmd.*;
import com.jvcats.cli.config.DefaultCommandConfig;
import com.jvcats.cli.config.DefaultParserConfig;
import com.jvcats.cli.tree.BaseCommandFactory;
import com.jvcats.cli.tree.CommandFactory;
import com.jvcats.cli.tree.CommandTree;

import java.util.*;

/**
 * The command parser is responsible for parsing command lines and running the appropriate command task.
 */
public class CommandParser {
    private final MainCommandAdapter mainCommands = new MainCommandAdapter();
    private final ParserConfig parserConfig;
    private final List<String> remaining = new ArrayList<>();
    private final List<List<String>> commandsParts = new ArrayList<>();
    private final Deque<Character> inQuotes = new ArrayDeque<>();
    private final CommandTree commandTree = new CommandTree();
    private final CommandFactory commandFactory;

    /**
     * Creates a new command parser with the given parser configuration.
     *
     * @param parserConfig The parser configuration, this will be used for all commands registered with this parser.
     */
    public CommandParser(ParserConfig parserConfig) {
        this(parserConfig, new BaseCommandFactory());
    }

    /**
     * Creates a new command parser with the default parser configuration.
     */
    public CommandParser() {
        this(new DefaultParserConfig(), new BaseCommandFactory());
    }

    /**
     * Creates a new command parser with the given parser configuration and command factory.
     *
     * @param parserConfig   The parser configuration, this will be used for all commands registered with this parser.
     * @param commandFactory The command factory with logic for creating commands.
     */
    public CommandParser(ParserConfig parserConfig, CommandFactory commandFactory) {
        this.parserConfig = parserConfig;
        if (!usingBlockStructure()) {
            registerNoOperationCommand();
        }
        this.commandFactory = commandFactory;
    }

    /**
     * Returns the command tree.
     *
     * @return The command tree.
     */
    public CommandTree getCommandTree() {
        return commandTree;
    }

    /**
     * Reviews if the command line was complete.
     *
     * @return True if the command line was complete, false otherwise.
     */
    public boolean isCommandComplete() {
        return remaining.isEmpty();
    }

    /**
     * Clears the remaining command line.
     */
    public void clearRemainingCommand() {
        remaining.clear();
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
        if (task == null) {
            task = (args) -> {
            };
        }
        if (option == null || option.isBlank()) {
            mainCommands.get(main).getOptions().put(config.mainOptionName(), new Option(CommandConfig.MAIN_OPTION_PRIORITY, task));
            return;
        } else if (option.equals(config.mainOptionName())) {
            throw new IllegalArgumentException("Invalid option name: " + option);
        }
        mainCommands.get(main).getOptions().put(option, new Option(mainCommands.get(main).decrementPriorityIndex(), task));
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
            mainCommands.get(main).getOptions().put(config.mainOptionName(), new Option(CommandConfig.MAIN_OPTION_PRIORITY, task));
            return;
        }
        Option option = new Option(mainCommands.get(main).decrementPriorityIndex(), task);
        for (String o : options) {
            if (o == null || o.isBlank() || o.equals(config.mainOptionName())) {
                throw new IllegalArgumentException("Invalid option name: " + o);
            }
            mainCommands.get(main).getOptions().put(o, option);
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
     * Prepares the command line for execution.
     * One can intercept the commands after calling this method to modify the command tree before execution.
     *
     * @param line The command line.
     * @throws Exception If an error occurs while preparing the command line.
     */
    public void prepare(String line) throws Exception {
        if (line == null || line.isBlank()) {
            return;
        }
        if (!remaining.isEmpty()) {
            commandsParts.add(new ArrayList<>(remaining));
            remaining.clear();
        }
        parseArgLine(line);
        for (int i = 0; i < commandsParts.size(); i++) {
            String main = commandsParts.get(i).getFirst();
            if (isBlockStart(main) || isBlockEnd(main)) {
                continue;
            }
            if (!mainCommands.containsKey(main)) {
                parserConfig.handleIllegalCommand(commandsParts.get(i).getFirst());
                // the rest of commands will be cleared
                commandsParts.clear();
                remaining.clear();
                return;
            }
            if (commandsParts.get(i).size() == 1 || !isExplicitOption(commandsParts.get(i).get(1))) {
                commandsParts.get(i).add(1, ParserConfig.FULL_OPTION_PREFIX + mainCommands.get(main).getConfig().mainOptionName());
            }
        }
        parseArgs();
    }

    /**
     * Executes the prepared commands.
     *
     * @throws Exception If an error occurs while executing the commands.
     */
    public void execute() throws Exception {
        commandTree.execute(commandTree.peek());
        commandTree.clear();
    }

    /**
     * Runs the command with the given line.
     *
     * @param line The command line.
     * @throws Exception If an error occurs while running the command.
     */
    public void runCommand(String line) throws Exception {
        prepare(line);
        execute();
    }

    /**
     * Creates a new command with the given main command name from the command factory.
     *
     * @param main The main command name.
     * @return A new command with the given main command name.
     */
    public Command createCommand(String main) {
        return commandFactory.createCommand(main, mainCommands, parserConfig);
    }

    /**
     * Checks by what character the given string is quoted.
     * One should override quotes() method to return a non-empty list to enable quoting.
     *
     * @param content The string to check.
     * @return The character or 0 if not quoted.
     */
    public char quotedBy(String content) {
        if (parserConfig.quotes().isEmpty()) {
            return 0;
        }
        if (content == null || content.length() < 2) {
            return 0;
        }
        char q = 0;
        String quotes = parserConfig.quotes();
        for (int i = 0; i < quotes.length(); i++) {
            if (content.charAt(0) == quotes.charAt(i)) {
                q = quotes.charAt(i);
                break;
            }
        }
        // it should always be true if content is an argument in a command
        if (content.charAt(content.length() - 1) == q) {
            return q;
        }
        return 0;
    }

    /**
     * Checks if the given string is a quoted string.
     *
     * @param content The string to check.
     * @return True if the string is a quoted string, false otherwise.
     */
    public boolean isQuoted(String content) {
        return quotedBy(content) != 0;
    }

    /**
     * Trims the quotes from the given string if it is quoted.
     *
     * @param content The string to trim.
     * @return The trimmed string.
     */
    public String trimQuotes(String content) {
        if (isQuoted(content)) {
            return content.substring(1, content.length() - 1);
        }
        return content;
    }

    private void parseArgs() throws Exception {
        if (!usingBlockStructure()) {
            commandTree.add(null, commandFactory.createCommand(Command.NOP_COMMAND, mainCommands, parserConfig));
        }
        Command parent = commandTree.peek();
        Command command = parent;
        for (List<String> commandParts : new ArrayList<>(commandsParts)) {
            String main = commandParts.getFirst();
            if (isBlockStart(main)) {
                parent = command;
                continue;
            } else if (isBlockEnd(main)) {
                parent = (Command) parent.getParent();
                continue;
            }
            LinkedHashMap<String, List<String>> options = new LinkedHashMap<>();
            OptionAdapter optionMap = mainCommands.get(main).getOptions();
            String key = null;
            for (int i = 1; i < commandParts.size(); i++) {
                String p = commandParts.get(i);
                if (isExplicitFullOption(p)) {
                    key = p.substring(p.lastIndexOf(ParserConfig.OPTION_PREFIX) + 1);
                    if (!optionMap.containsKey(key)) {
                        parserConfig.handleIllegalOption(key);
                        // the rest of commands will be cleared
                        commandsParts.clear();
                        remaining.clear();
                        return;
                    }
                    options.put(key, new ArrayList<>());
                } else if (isExplicitOption(p)) {
                    String keys = p.substring(1);
                    for (int j = 0; j < keys.length(); j++) {
                        key = keys.substring(j, j + 1);
                        if (!optionMap.containsKey(key)) {
                            parserConfig.handleIllegalOption(key);
                            commandsParts.clear();
                            remaining.clear();
                            return;
                        }
                        options.put(key, new ArrayList<>());
                    }
                } else if (!p.isBlank()) {
                    options.get(key).add(p);
                }
            }
            command = commandFactory.createCommand(main, options, mainCommands, parserConfig);
            commandTree.add(parent, command);
            commandsParts.remove(commandParts);
        }
    }

    private void parseArgLine(String args) {
        if (args == null || args.isBlank()) {
            return;
        }
        List<String> result = commandsParts.isEmpty() ? new ArrayList<>() : commandsParts.removeFirst();

        // allow a command to be split into multiple lines if EOS is specified
        // need to add a delimiter to the beginning of next line if handling a new token
        // or the new token will be combined with previous one
        StringBuilder currentElement = result.isEmpty() ? new StringBuilder() : new StringBuilder(result.removeLast());

        for (int i = 0; i < args.length(); i++) {
            char c = args.charAt(i);

            if (c == '\n' || c == '\r') {
                // ignore new line characters
                continue;
            }

            if (takeCareOfQuote(c, true)) {
                switchInQuotes(c);
                currentElement.append(c);
                if (inQuotes.isEmpty()) {
                    result.add(currentElement.toString().trim());
                    currentElement.setLength(0);
                }
                continue;
            }

            if (isBlockStart(c) && inQuotes.isEmpty()) {
                if (!currentElement.isEmpty()) {
                    result.add(currentElement.toString().trim());
                    currentElement.setLength(0);
                }
                if (!result.isEmpty()) {
                    commandsParts.add(result);
                    result = new ArrayList<>();
                }
                commandsParts.add(List.of(String.valueOf(c)));
                continue;
            }

            if (isBlockEnd(c) && inQuotes.isEmpty()) {
                commandsParts.add(List.of(String.valueOf(c)));
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

            if ((c == parserConfig.delimiter() || takeCareOfEOS(c, true)) && inQuotes.isEmpty()) {
                if (!currentElement.isEmpty()) {
                    result.add(currentElement.toString().trim());
                    currentElement.setLength(0);
                }
                if (takeCareOfEOS(c, true) && !result.isEmpty()) {
                    commandsParts.add(result);
                    result = new ArrayList<>();
                }
            } else if (!inQuotes.isEmpty() || c != ' ') {
                currentElement.append(c);
            }
        }

        if (!currentElement.isEmpty()) {
            result.add(currentElement.toString().trim());
        }

        if (parserConfig.endOfStatement() == ParserConfig.NO_EOS) {
            commandsParts.add(result);
        } else {
            if (!result.isEmpty()) {
                remaining.addAll(result);
            }
        }

    }

    private boolean takeCareOfQuote(char q, boolean want) {
        return !parserConfig.quotes().isEmpty() && (want == (parserConfig.quotes().indexOf(q) >= 0));
    }

    private void switchInQuotes(char q) {
        if (!inQuotes.isEmpty() && inQuotes.peek() == q) {
            inQuotes.pop();
        } else {
            inQuotes.push(q);
        }
    }

    private boolean takeCareOfEOS(char q, boolean want) {
        return parserConfig.endOfStatement() != ParserConfig.NO_EOS && (want == (parserConfig.endOfStatement() == q));
    }

    private boolean isExplicitOption(String s) {
        return s.startsWith(ParserConfig.OPTION_PREFIX);
    }

    private boolean isExplicitFullOption(String s) {
        return s.startsWith(ParserConfig.FULL_OPTION_PREFIX);
    }

    private boolean isBlockStart(String s) {
        return usingBlockStructure() && s.length() == 1 && parserConfig.blockChars().charAt(0) == s.charAt(0);
    }

    private boolean isBlockEnd(String s) {
        return usingBlockStructure() && s.length() == 1 && parserConfig.blockChars().charAt(1) == s.charAt(0);
    }

    private boolean isBlockStart(char c) {
        return usingBlockStructure() && parserConfig.blockChars().charAt(0) == c;
    }

    private boolean isBlockEnd(char c) {
        return usingBlockStructure() && parserConfig.blockChars().charAt(1) == c;
    }

    private boolean usingBlockStructure() {
        return parserConfig.blockChars().length() == 2;
    }

    private void registerNoOperationCommand() {
        register(Command.NOP_COMMAND, (args) -> {
        });
    }

}






