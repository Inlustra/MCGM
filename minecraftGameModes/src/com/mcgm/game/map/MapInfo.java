/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.map;

/**
 *
 * @author Thomas
 */
public @interface MapInfo {
    String name();
    String[] aliases();
    boolean teamCapable();
    boolean airBourne();
    boolean itemSpawns();
}
