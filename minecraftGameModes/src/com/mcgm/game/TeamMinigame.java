/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game;

import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.event.PlayerLoseEvent;
import com.mcgm.game.event.PlayerWinEvent;
import com.mcgm.game.event.ReceiveNameTagEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.player.Team;
import com.mcgm.utils.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 *
 * @author Thomas
 */
public abstract class TeamMinigame extends Minigame {

    private int teamAmount;
    private Team[] teams;
    private ChatColor[] teamColors = new ChatColor[]{ChatColor.RED, ChatColor.BLUE,
        ChatColor.GREEN, ChatColor.YELLOW, ChatColor.DARK_PURPLE, ChatColor.AQUA,
        ChatColor.WHITE, ChatColor.BLACK};

    public TeamMinigame() {
        GameInfo f = this.getClass().getAnnotation(GameInfo.class);

        teamAmount = f.teamAmount();

        if (teamAmount > 0) {
            teams = new Team[teamAmount];
        }
        if (teamAmount != -1) {
            for (int i = 0; i < teamAmount; i++) {
                teams[i] = new Team(teamColors[i]);
            }
            int currTeam = 0;
            for (Player p : currentlyPlaying) {
                teams[currTeam].addPlayer(p);
                currTeam = currTeam++ == teamAmount ? 0 : currTeam++;
            }
        }
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

    public int getTeamAmount() {
        return teamAmount;
    }

    public Team getPlayerTeam(Player p) {
        for (Team m : teams) {
            if (m.getTeamPlayers().contains(p)) {
                return m;
            }
        }
        return null;
    }

    public Team[] getTeams() {
        return teams;
    }

    public ChatColor[] getTeamColors() {
        return teamColors;
    }

    public void callTeamWin(Team t) {
        Bukkit.getPluginManager().callEvent(new PlayerWinEvent(this, t));
        if (currentlyPlaying.size() <= 1) {
            Bukkit.getPluginManager().callEvent(new PlayerWinEvent(this, t));
        }
    }

    public void callTeamLose(Team t) {
        Bukkit.getPluginManager().callEvent(new PlayerLoseEvent(this, t));
        if (currentlyPlaying.size() <= 1) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(this, false, null));
        }
    }
}
