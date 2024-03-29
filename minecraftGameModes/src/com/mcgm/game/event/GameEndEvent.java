/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.event;

import com.mcgm.config.MCPartyConfig;
import com.mcgm.game.Minigame;
import com.mcgm.player.Team;
import com.mcgm.utils.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Tom
 */
public class GameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player[] winners;
    private boolean timeUp;
    private Minigame minigame;
    private Team team = null;

    public GameEndEvent(Minigame m, boolean timeUp, Player... winners) {
        this.winners = winners;
        this.minigame = m;
    }

    public GameEndEvent(Minigame m, boolean timeUp, Team t) {
        this.team = t;
        this.winners = t.getTeamPlayers().toArray(new Player[t.getTeamPlayers().size()]);
        this.minigame = m;
    }

    public Team getTeam() {
        return team;
    }

    public Minigame getMinigame() {
        return minigame;
    }

    public Player[] getWinners() {
        return winners;
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
