/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.worlds;

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
        p.teleport(l);
    }
}
