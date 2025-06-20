package com.jvcats.cli.cmd;

import com.jvcats.cli.ParserConfig;

/**
 * Represents a single argument in a command line.
 */
public class Argument {
    private final ParserConfig config;
    private final String content;

    public Argument(ParserConfig config, String content) {
        this.config = config;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    /**
     * Checks by what character the given string is quoted.
     * One should override quotes() method to return a non-empty list to enable quoting.
     *
     * @return The character or 0 if not quoted.
     */
    public char quotedBy() {
        if (config.quotes().isEmpty()) {
            return 0;
        }
        if (content == null || content.length() < 2) {
            return 0;
        }
        char q = 0;
        for (char c : config.quotes()) {
            if (content.charAt(0) == c) {
                q = c;
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
     * @return True if the string is a quoted string, false otherwise.
     */
    public boolean isQuoted() {
        return quotedBy() != 0;
    }

    /**
     * Trims the quotes from the given string if it is quoted.
     *
     * @return The trimmed string.
     */
    public String trimQuotes() {
        if (isQuoted()) {
            return content.substring(1, content.length() - 1);
        }
        return content;
    }
}
