package com.jvcats.cli.cmd;

import java.util.*;

/**
 * Base class for running commands.
 */
public class BaseCommand implements Command {
    private final String name;
    private final PriorityQueue<RunningOption> options = new PriorityQueue<>();
    private final Map<Integer, Integer> priorityMap = new HashMap<>();

    public BaseCommand(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addOption(String option, List<String> args, OptionAdapter optionAdapter) {
        Option optionObj = optionAdapter.get(option);
        int priority = optionObj.getPriority();
        if (priorityMap.containsKey(priority)) {
            priorityMap.put(priority, priorityMap.get(priority) + 1);
        } else {
            priorityMap.put(priority, 1);
        }
        options.offer(new RunningOption(optionObj, args, priorityMap.get(priority)));
    }

    @Override
    public PriorityQueue<RunningOption> getOptions() {
        return options;
    }

    @Override
    public void execute() throws Exception {
        while (!options.isEmpty()) {
            RunningOption option = options.poll();
            option.option().getTask().run(option.args());
        }
    }
}
