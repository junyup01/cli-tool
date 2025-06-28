package com.jvcats.cli.tree;

import com.jvcats.cli.cmd.Command;

import java.util.List;

/**
 * This class represents a tree of commands.
 */
public class CommandTree {
    private Command head;
    private int size;

    /**
     * Adds a command to the tree.
     *
     * @param parent the parent command to add the command to
     * @param command the command to add
     */
    public void add(Command parent, Command command) {
        if (head == null) {
            head = command;
        } else {
            parent.addChild(command);
        }
        size += command.descendantCount() + 1;
    }

    /**
     * Removes a command from the tree.
     *
     * @param command the command to remove
     */
    public void remove(Command command) {
        if (command == head) {
            clear();
        } else {
            command.setParent(null);
            command.getParent().removeChild(command);
            size -= (command.descendantCount() + 1);
        }
    }

    /**
     * Removes all children of the given command.
     *
     * @param command the command to remove all children from
     * @return the removed children
     */
    public List<Node> removeAllChildren(Command command) {
        size -= command.descendantCount();
        return command.removeAllChildren();
    }

    /**
     * Clears the tree.
     */
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Returns the number of commands in the tree.
     *
     * @return the number of commands
     */
    public int size() {
        return size;
    }

    /**
     * Returns the head of the tree, or null if the tree is empty.
     *
     * @return the head
     */
    public Command peek() {
        return head;
    }

    /**
     * Executes the given command and all its descendants in pre-order traversal.
     *
     * @param command the command to start from
     * @throws Exception if any command fails to execute
     */
    public void execute(Command command) throws Exception {
        if (command == null) {
            return;
        }
        command.execute();
        for (Node child : command.getChildren()) {
            execute((Command) child);
        }
    }

    /**
     * Returns the command with the given name, or null if not found.
     *
     * @param parent the parent command to start from
     * @param path the path to the command, relative to the parent
     * @return the command with the given name, or null if not found
     */
    public Command get(Command parent, CPath path) {
        Command current = parent;
        for (int i = 0; i < path.getNames().length; i++) {
            if (current == null) {
                return null;
            }
            int skip = path.getSkips()[i];
            for (Node child : current.getChildren()) {
                if (child instanceof Command c && c.getName().equals(path.getNames()[i])) {
                    if (skip == 0) {
                        current = c;
                        break;
                    } else {
                        skip--;
                    }
                }
            }
        }
        return current;
    }

}
