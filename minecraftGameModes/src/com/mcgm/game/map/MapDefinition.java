/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.map;

/**
 *
 * @author Thomas
 */
public class MapDefinition {

    private String name;
    private String[] aliases;
    private boolean teamCapable;
    private boolean airBourne;
    private boolean itemSpawns;

    public MapDefinition(String name, String[] aliases, boolean teamCapable, boolean airBourne, boolean itemSpawns) {
        this.name = name;
        this.aliases = aliases;
        this.teamCapable = teamCapable;
        this.airBourne = airBourne;
        this.itemSpawns = itemSpawns;
    }

    
}
