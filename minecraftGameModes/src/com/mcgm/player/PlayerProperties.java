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

    private boolean VIP;
    private int credits;
    private int id;
    private int wins;
    private int losses;
    private Player p;

    public PlayerProperties(Player p, boolean VIP, int credits, int id, int wins, int losses) {
        this.VIP = VIP;
        this.credits = credits;
        this.id = id;
        this.wins = wins;
        this.losses = losses;
        this.p = p;
    }

    public void setVIP(boolean VIP) {
        this.VIP = VIP;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public boolean isVIP() {
        return VIP;
    }

    public int getCredits() {
        return credits;
    }

    public int getId() {
        return id;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}
