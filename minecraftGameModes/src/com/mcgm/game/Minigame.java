/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game;

import com.mcgm.Plugin;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.manager.GameManager;
import com.mcgm.utils.Misc;
import java.util.ArrayList;
import javax.annotation.processing.SupportedAnnotationTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author Tom
 */
public abstract class Minigame implements Listener {

    public final Plugin plugin;
    public final String name;
    public final double version;
    public final String[] authors;
    public final String description;
    public int teamAmount;
    public boolean pvpEnabled;
    public int maxPlayers;
    public int gameTime;
    public Player[] winners;
    public int credits;
    public ArrayList<Player> playing;

    public Minigame(Plugin p, GameInfo f) {
        plugin = p;
        name = f.name();
        credits = f.credits();
        version = f.version();
        authors = f.authors();
        description = f.description();
        pvpEnabled = f.pvp();
        maxPlayers = f.maxPlayers();
        teamAmount = f.teamAmount();
        gameTime = f.gameTime();
        this.playing = GameManager.getInstance(p).getPlaying();
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

    public String getName() {
        return name;
    }

    public double getVersion() {
        return version;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }

    public int getTeamAmount() {
        return teamAmount;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getGameTime() {
        return gameTime;
    }

    public Player[] getWinners() {
        return winners;
    }

    public int getCredits() {
        return credits;
    }

    public ArrayList<Player> getPlaying() {
        return playing;
    }

    public Plugin getPlugin() {
        return plugin;
    }

}
