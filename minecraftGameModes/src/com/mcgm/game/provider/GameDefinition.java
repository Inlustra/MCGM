/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.provider;

public class GameDefinition implements Comparable<GameDefinition> {

    public long crc32;
    public String name;
    public String[] aliases;
    public double version;
    public String description;
    public String[] authors;
    public int maxPlayers;
    public boolean teamBased;
    public int teamAmount;
    public boolean PvP;
    public String seed;
    public int votes = 0;
    public GameSource source;
    public Class clazz;

    public boolean isPvP() {
        return PvP;
    }

    public long getCrc32() {
        return crc32;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getSeed() {
        return seed;
    }

    public String getName() {
        return name;
    }

    public int getTeamAmount() {
        return teamAmount;
    }

    public boolean isTeamBased() {
        return teamBased;
    }

    public double getVersion() {
        return version;
    }

    String getAuthors() {
        final StringBuilder s = new StringBuilder(16);
        for (int i = 0; i < authors.length; i++) {
            if (i > 0) {
                s.append(i == authors.length - 1 ? " and " : ", ");
            }
            s.append(authors[i]);
        }
        return s.toString();
    }

    @Override
    public int compareTo(final GameDefinition def) {
        final int c = getName().compareToIgnoreCase(def.getName());
        return c == 0 ? Double.compare(version, def.version) : c;
    }

    @Override
    public String toString() {
        return "[" + getName() + ": V" + getVersion() + "] " + getDescription();
    }
}
