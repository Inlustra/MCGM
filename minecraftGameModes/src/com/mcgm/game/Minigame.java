/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game;

import com.mcgm.Plugin;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author Tom
 */
public abstract class Minigame implements Listener {

    public final Plugin p;
    public final String name;
    public final double version;
    public final String[] authors;
    public final String description;
    public int teamAmount;
    public boolean pvpEnabled;
    public int maxPlayers;
    public int gameTime;
    public Player[] winners;
    public Player[] playing;

    public Minigame(Plugin p, GameInfo f, Player... playing) {
        this.p = p;
        name = f.name();
        version = f.version();
        authors = f.authors();
        description = f.description();
        pvpEnabled = f.pvp();
        maxPlayers = f.maxPlayers();
        teamAmount = f.teamAmount();
        gameTime = f.gameTime();
        this.playing = playing;
        Misc.getMainWorld().setPVP(pvpEnabled);
        p.getServer().getPluginManager().registerEvents(this, p);
    }

    public abstract void onCountDown();

    public abstract void onTimeUp();

    public abstract void startGame();

    public abstract void onEnd();
    
    public abstract void generateGame();
    
    public abstract void onLeaveArea();

    public abstract void minigameTick(); 
}
