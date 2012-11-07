/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.event;

import com.mcgm.game.Minigame;
import com.mcgm.game.area.Area;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Tom
 */
public class EnterAreaEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Minigame m;
    private Entity e;

    public EnterAreaEvent(Minigame m, Entity e) {
        this.m = m;
        this.e = e;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Area getArea() {
        return m.getArea();
    }

    public Entity getEntity() {
        return e;
    }
}
