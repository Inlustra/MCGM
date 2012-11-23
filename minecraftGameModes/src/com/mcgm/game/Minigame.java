/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game;

import com.mcgm.MCPartyCore;
import com.mcgm.game.event.ReceiveNameTagEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.player.Team;
import com.mcgm.utils.Misc;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 *
 * @author Tom
 */
public abstract class Minigame implements Listener {

    public final MCPartyCore core;
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
    public Team[] teams;
    public ChatColor[] teamColors = new ChatColor[]{ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
        ChatColor.DARK_PURPLE, ChatColor.AQUA, ChatColor.WHITE, ChatColor.BLACK};
    public Player[] startingPlayers;

    protected Minigame() {
        GameInfo f = this.getClass().getAnnotation(GameInfo.class);
        core = MCPartyCore.getInstance();
        name = f.name();
        credits = f.credits();
        version = f.version();
        authors = f.authors();
        description = f.description();
        pvpEnabled = f.pvp();
        maxPlayers = f.maxPlayers();
        gameTime = f.gameTime();
        playing = MCPartyCore.getInstance().getGameManager().getPlaying();
        startingPlayers = playing.toArray(new Player[playing.size()]);
        teamAmount = f.teamAmount();
        if (teamAmount > 0) {
            teams = new Team[teamAmount];
        }
        if (teamAmount != -1) {
            for (int i = 0; i < teamAmount; i++) {
                teams[i] = new Team(teamColors[i]);
            }
            int currTeam = 0;
            for (Player p : playing) {
                Misc.outPrint(currTeam + "");
                teams[currTeam].addPlayer(p);
                currTeam = currTeam++ == teamAmount ? 0 : currTeam++;
            }
        }
        core.getWorldManager().getMinigameWorld().setPVP(pvpEnabled);
        core.getServer().getPluginManager().registerEvents(this, core);

    }

    public void sendPlayingMessage(String s) {
        for (Player p : playing) {
            p.sendMessage(s);
        }
    }

    public abstract void generateGame();

    public abstract void onTimeUp();

    public abstract void startGame();

    public abstract void onEnd();

    public abstract void minigameTick();

    public abstract void playerDisconnect(Player player);

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

    public MCPartyCore getPlugin() {
        return core;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onReceiveNameTagEvent(ReceiveNameTagEvent event) {
        if (teams != null) {
            if (teams.length > 1) {
                ChatColor c = getPlayerTeam(event.getWatched()).teamColor;
                event.setTag(c + "[" + c.name() + "] " + event.getTag());
            }
        }
    }

    @EventHandler
    public void playerChangeWorld(PlayerChangedWorldEvent e) {
        Misc.refreshPlayer(e.getPlayer());
    }

    public Team getPlayerTeam(Player p) {
        for (Team m : teams) {
            if (m.getTeamPlayers().contains(p)) {
                return m;
            }
        }
        return null;
    }
}
