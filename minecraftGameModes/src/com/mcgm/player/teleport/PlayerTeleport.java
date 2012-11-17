/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.player.teleport;

import com.mcgm.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Thomas
 */
public class PlayerTeleport {

    private Location l;
    private Player p;

    public PlayerTeleport(Player p, Location l) {
        this.l = l;
        this.p = p;
    }

    public Location getL() {
        return l;
    }

    public Player getP() {
        return p;
    }

    public void teleport() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                p.teleport(l);
            }
        });
    }
}
