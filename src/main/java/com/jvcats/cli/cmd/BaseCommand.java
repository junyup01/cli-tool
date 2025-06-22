package com.jvcats.cli.cmd;

import com.jvcats.cli.ParserConfig;

import java.util.*;

/**
 * Base class for running commands.
 */
public class BaseCommand implements Command {
    private String name;
    private final List<RunningOption> options = new ArrayList<>();
    private final Map<Integer, Integer> priorityMap = new HashMap<>();
    private final ParserConfig parserConfig;
    private final MainCommandAdapter mainCommandAdapter;

    /**
     * Using this constructor is discouraged, one should call createCommand() from CommandParser instead
     */
    public BaseCommand(String name, MainCommandAdapter mainCommandAdapter, ParserConfig parserConfig) {
        if (!mainCommandAdapter.containsKey(name)) {
            throw new IllegalArgumentException("Undefined command name: " + name);
        }
        this.name = name;
        this.parserConfig = parserConfig;
        this.mainCommandAdapter = mainCommandAdapter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        MainCommand mainCommand = mainCommandAdapter.get(name);
        if (mainCommand == null) {
            throw new IllegalArgumentException("Undefined command name: " + name);
        }
        // check for all options when the main command name is changed
        for (RunningOption option : options) {
            if (!mainCommand.getOptions().containsKey(option.getName())) {
                throw new IllegalArgumentException("Undefined option name: " + option.getName());
            }
        }
        this.name = name;
    }

    @Override
    public void addOption(String option) {
        MainCommand mainCommand = mainCommandAdapter.get(name);
        if (option == null) {
            option = mainCommand.getConfig().mainOptionName();
        }
        OptionAdapter optionAdapter = mainCommand.getOptions();
        // allow for duplicate options
        Option optionObj = optionAdapter.get(option);
        int priority = optionObj.priority();
        if (priorityMap.containsKey(priority)) {
            priorityMap.put(priority, priorityMap.get(priority) + 1);
        } else {
            priorityMap.put(priority, 1);
        }
        options.add(new RunningOption(option, priorityMap.get(priority), parserConfig, mainCommand.getConfig(), optionAdapter));
    }

    @Override
    public List<RunningOption> getOptions() {
        return options;
    }

    @Override
    public RunningOption getOption(String option) {
        if (option == null) {
            option = mainCommandAdapter.get(name).getConfig().mainOptionName();
        }
        for (RunningOption opt : options) {
            if (opt.getName().equals(option)) {
                return opt;
            }
        }
        return null;
    }

    @Override
    public void removeOption(String option) {
        options.remove(getOption(option));
    }

    @Override
    public void execute() throws Exception {
        List<RunningOption> temp = new ArrayList<>(options);
        OptionAdapter optionAdapter = mainCommandAdapter.get(name).getOptions();
        temp.sort((o1, o2) -> {
            Option option = optionAdapter.get(o1.getName());
            Option option2 = optionAdapter.get(o2.getName());
            if (option.priority() == option2.priority()) {
                return o1.getPriority() - o2.getPriority();
            }
            return option2.priority() - option.priority();
        });
        for (RunningOption option : temp) {
            optionAdapter.get(option.getName()).task().run(option.getArgs());
        }
    }

    @Override
    public String toString() {
        return name + options;
    }
}
