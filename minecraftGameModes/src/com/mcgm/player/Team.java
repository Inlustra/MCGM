/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.player;

import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Thomas
 */
public class Team implements Iterable {

    public ArrayList<Player> teamPlayers;
    public ChatColor teamColor;

    public Team(ChatColor cc) {
        this.teamColor = cc;
        teamPlayers = new ArrayList<>();
    }

    public void addPlayer(Player p) {
        if (!teamPlayers.contains(p)) {
            teamPlayers.add(p);
        }
    }

    public ArrayList<Player> getTeamPlayers() {
        return teamPlayers;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }

    public boolean playerIsInTeam(Player p) {
        return teamPlayers.contains(p);
    }

    @Override
    public Iterator iterator() {
        return teamPlayers.iterator();
    }
}
