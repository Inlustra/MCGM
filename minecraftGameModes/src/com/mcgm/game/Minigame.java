/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game;

import com.mcgm.MCPartyCore;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.event.PlayerLoseEvent;
import com.mcgm.game.event.PlayerWinEvent;
import com.mcgm.game.event.ReceiveNameTagEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.player.Team;
import com.mcgm.utils.Misc;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 *
 * @author Tom
 */
public abstract class Minigame implements Listener {

    protected final MCPartyCore core;
    private final String name;
    private final double version;
    private final String[] authors;
    private final String description;
    private boolean joinable;
    private boolean pvpEnabled;
    private boolean blocksBreakable;
    private boolean blocksPlaceable;
    private boolean infiniteFood;
    private int maxPlayers;
    private int gameTime;
    private Player[] winners;
    private int credits;
    protected CopyOnWriteArrayList<Player> currentlyPlaying;
    private CopyOnWriteArrayList<Player> startingPlayers;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (currentlyPlaying.contains(e.getPlayer())) {
            e.setCancelled(!blocksBreakable);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (currentlyPlaying.contains(e.getPlayer())) {
            e.setCancelled(!blocksPlaceable);
        }
    }

    @EventHandler
    public void foodLevelChange(FoodLevelChangeEvent e) {
        if (currentlyPlaying.contains((Player) e.getEntity())) {
            e.setCancelled(!infiniteFood);
        }
    }

    @EventHandler
    public void playerChangeWorld(PlayerChangedWorldEvent e) {
        Misc.refreshPlayer(e.getPlayer());
    }

    protected Minigame() {
        GameInfo f = this.getClass().getAnnotation(GameInfo.class);
        core = MCPartyCore.getInstance();
        name = f.name();
        credits = f.credits();
        version = f.version();
        authors = f.authors();
        description = f.description();
        pvpEnabled = f.pvp();
        blocksBreakable = f.blocksBreakable();
        blocksPlaceable = f.blocksPlaceable();
        infiniteFood = f.infiniteFood();
        maxPlayers = f.maxPlayers();
        gameTime = f.gameTime();
        joinable = false;
        currentlyPlaying = MCPartyCore.getInstance().getGameManager().getPlaying();
        startingPlayers = (CopyOnWriteArrayList<Player>) currentlyPlaying.clone();
        core.getWorldManager().getMinigameWorld().setPVP(pvpEnabled);
        core.getServer().getPluginManager().registerEvents(this, core);
    }

    public void sendPlayingMessage(String s) {
        for (Player p : currentlyPlaying) {
            p.sendMessage(s);
        }
    }

    public abstract void generateGame();

    public abstract void onTimeUp();

    public abstract void startGame();

    public abstract void onEnd();

    public abstract void minigameTick();

    public boolean isJoinable() {
        return joinable;
    }

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

    public CopyOnWriteArrayList<Player> getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public MCPartyCore getPlugin() {
        return core;
    }

    public MCPartyCore getCore() {
        return core;
    }

    public CopyOnWriteArrayList<Player> getStartingPlayers() {
        return startingPlayers;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
        if (getGameTime() > 0) {
            for (Player p : currentlyPlaying) {
                p.setLevel(getGameTime());
            }
        } else if (getGameTime() == 0) {
            onTimeUp();
            for (Player p : currentlyPlaying) {
                p.setLevel(getGameTime());
            }
        }
    }

    public void callPlayerWin(Player... p) {
        core.getGameManager().onPlayersWin(new PlayerWinEvent(this, p));
    }

    public void callPlayerLose(Player... p) {
        core.getGameManager().onPlayersLose(new PlayerLoseEvent(this, p));
    }

    public void endGame(Player... winners) {
        core.getGameManager().onGameEnd(new GameEndEvent(this, false, winners));
    }

    public void playerDisconnect(Player player) {
        callPlayerLose(player);
    }
}
