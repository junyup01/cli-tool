package com.jvcats.cli;


/**
 * This interface defines the configuration of a parser.
 * One can implement this interface to customize the behavior of a parser.
 */
public interface ParserConfig {

    String NO_QUOTE = "";

    char DELIMITER = ' ';

    char NO_EOS = '\0';

    char ESCAPE = '\\';

    String NO_BLOCK_CHARS = "";

    String OPTION_PREFIX = "-";

    String FULL_OPTION_PREFIX = "--";

    /**
     * Sets the quote characters for the command.
     * The quote character is used to enclose the value of the option if it contains delimiters or spaces.
     * The default quote characters is a string with no characters, which means no quote is used.
     *
     * @return the quote characters in a string.
     */
    default String quotes() {
        return NO_QUOTE;
    }

    /**
     * Sets the delimiter character for the command.
     * The delimiter character is used to separate the values of the option arguments.
     * The default delimiter character is a space.
     *
     * @return the delimiter character.
     */
    default char delimiter() {
        return DELIMITER;
    }

    /**
     * Sets the end of statement character for the command.
     * By setting this character, multiple commands can be executed in a single line.
     * The default end of statement character is '\0', which means no end of statement is used.
     *
     * @return the end of statement character.
     */
    default char endOfStatement() {
        return NO_EOS;
    }

    /**
     * Sets the escape character for the command.
     * The escape character is used to escape the quote character or the delimiter character in a command.
     * The default escape character is '\'.
     *
     * @return the escape character.
     */
    default char escape() {
        return ESCAPE;
    }

    /**
     * Sets the disposal for an unknown command.
     *
     * @param part the unknown command.
     */
    default void handleIllegalCommand(String part) throws Exception {
        System.out.println("Unknown command: " + part);
    }

    /**
     * Sets the disposal for an unknown option.
     *
     * @param part the unknown option.
     */
    default void handleIllegalOption(String part) throws Exception {
        System.out.println("Unknown option: " + part);
    }

    /**
     * Sets the block characters for the command tree.
     * The string contains two characters(e.g. '{' and '}'), which are used to define a block of commands.
     * The default block characters is a string with no characters, which means no block is used.
     *
     * @return the block characters in a string.
     */
    default String blockChars() {
        return NO_BLOCK_CHARS;
    }
}
