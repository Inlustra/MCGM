/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.event;

import com.mcgm.game.Minigame;
import com.mcgm.player.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Thomas
 */
public class PlayerWinEvent extends Event {

    private Minigame minigame;
    private Team team;
    private Player[] players;

    public PlayerWinEvent(Minigame minigame, Team t) {
        this.minigame = minigame;
        this.players = t.getTeamPlayers().toArray(new Player[t.getTeamPlayers().size()]);
        this.team = t;
    }

    public PlayerWinEvent(Minigame minigame, Player... players) {
        this.minigame = minigame;
        this.players = players;
        this.team = null;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Minigame getMinigame() {
        return minigame;
    }

    public Team getTeam() {
        return team;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
