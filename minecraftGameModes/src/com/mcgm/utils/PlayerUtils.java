/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.mcgm.MCPartyCore;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

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
        p.setFireTicks(0);
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.setExp(0);
        MCPartyCore.getInstance().getDisguiseCraftAPI().undisguisePlayer(p);
        if (teleport) {
            WorldUtils.teleport(p, WorldUtils.getMainSpawn());
        }
    }
}
