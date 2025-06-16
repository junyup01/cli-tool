package com.jvcats.cli.cmd;

import com.jvcats.cli.CommandTask;

/**
 * This class represents an option for a command.
 */
public record Option(int priority, CommandTask task) {
}
