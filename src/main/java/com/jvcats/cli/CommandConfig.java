package com.jvcats.cli;

/**
 * This interface defines the configuration of a command.
 * One can implement this interface to customize the behavior of a command.
 */
public interface CommandConfig {

    int INORDER_POLICY = 0;

    int MAIN_OPTION_PRIORITY = 0;

    String DEFAULT_OPTION_NAME = "_DEFAULT_";

    /**
     * Sets the name of the main option of the command. Override this method if "_DEFAULT_" will be used as the other option names.
     * The main option is the option right after the command name in the command line, and it can be omitted.
     * The default main option name is "_DEFAULT_".
     *
     * @return the name of the main option.
     */
    default String mainOptionName() {
        return DEFAULT_OPTION_NAME;
    }

    /**
     * Sets the running policy. The options take the returned value as their running priority when they are registered.
     * The running priority of main option is always 0. Setting any higher value for the running policy will cause the other options to be executed before the main option.
     * The default policy is 0, in which case all options of the command will be executed in the order of command line.
     *
     * @return the running policy.
     */
    default int runningPolicy() {
        return INORDER_POLICY;
    }

    /**
     * Sets whether the option should be registered with priority from the running policy value in descending order.
     * One should override runningPolicy() as well to adjust the priority relationship between the main option and the other options.
     *
     * @return whether the option should be registered with priority in descending order automatically.
     */
    default boolean registerWithPriority() {
        return false;
    }

}
