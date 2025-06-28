package com.jvcats.cli.tree;

/**
 * This class represents a path in the tree.
 */
public class CPath {
    private final String[] names;
    private final int[] skips;

    public CPath(String path) {
        String[] parts = path.split("/");
        names = new String[parts.length];
        skips = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            String[] split = part.split("\\[");
            if (split.length > 1) {
                names[i] = split[0];
                skips[i] = Integer.parseInt(split[1].substring(0, split[1].length() - 1));
            } else {
                names[i] = part;
                skips[i] = 0;
            }
        }
    }

    public String[] getNames() {
        return names;
    }

    public int[] getSkips() {
        return skips;
    }

}
