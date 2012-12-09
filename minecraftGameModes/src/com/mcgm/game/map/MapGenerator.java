/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.map;

import com.mcgm.game.Minigame;
import com.sk89q.worldedit.bukkit.BukkitWorld;

/**
 *
 * @author Thomas
 */
public abstract class MapGenerator {

    public abstract void generateMap(Minigame minigame, BukkitWorld w);
}
