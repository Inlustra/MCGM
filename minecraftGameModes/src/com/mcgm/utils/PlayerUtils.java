/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.mcgm.Plugin;
import org.bukkit.entity.Player;

/**
 *
 * @author Thomas
 */
public class PlayerUtils {

    public static void cleanPlayer(Player p, boolean teleport) {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.getInventory().clear();
        p.setWalkSpeed(0.2F);
        p.setLevel(0);
        p.setExp(0);
        Plugin.getInstance().getDisguiseCraftAPI().undisguisePlayer(p);
        if (teleport) {
            WorldUtils.teleport(p, WorldUtils.getMainSpawn());
        }
    }
}
