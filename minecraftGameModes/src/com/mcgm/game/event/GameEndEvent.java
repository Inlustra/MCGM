/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.event;

import com.mcgm.game.Minigame;
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
