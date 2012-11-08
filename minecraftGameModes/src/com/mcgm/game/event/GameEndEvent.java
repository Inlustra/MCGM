/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.event;

import com.mcgm.game.Minigame;
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

    public GameEndEvent(Minigame m, boolean timeUp, Player... winners) {
        this.winners = winners;
        this.minigame = m;
        Bukkit.broadcastMessage(ChatColor.WHITE + "(" + ChatColor.AQUA + m.name + ChatColor.WHITE + ") " + ChatColor.GOLD + "Winners: " + Misc.buildPlayerString(winners, " "));
        Bukkit.broadcastMessage("They have been awarded " + ChatColor.GREEN + m.getCredits() + ChatColor.WHITE + " credits");
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
