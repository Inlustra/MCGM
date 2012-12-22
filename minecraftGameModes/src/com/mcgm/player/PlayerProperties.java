/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.player;

import com.mcgm.MCPartyCore;
import com.mcgm.manager.PlayerManager;
import org.bukkit.entity.Player;

/**
 *
 * @author Thomas
 */
public class PlayerProperties {

    public boolean VIP;
    public int credits;
    public int id;
    public int wins;
    public int losses;
    public Player p;

    public PlayerProperties(Player p, boolean VIP, int credits, int id, int wins, int losses) {
        this.VIP = VIP;
        this.credits = credits;
        this.id = id;
        this.wins = wins;
        this.losses = losses;
        this.p = p;
    }

}
