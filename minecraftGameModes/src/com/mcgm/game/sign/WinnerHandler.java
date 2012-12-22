/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import com.mcgm.MCPartyCore;
import com.mcgm.game.event.GameEndEvent;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Thomas
 */
public class WinnerHandler extends SignHandler implements Listener {

    public WinnerHandler(String configText, String handlingText) {
        super(configText, handlingText);
        MCPartyCore.getInstance().getServer().getPluginManager().registerEvents(this, MCPartyCore.getInstance());
    }

    @Override
    public SignTask signSet(SignChangeEvent e) {
        setSignName((Sign) e.getBlock().getState(), "");
        e.setCancelled(true);
        return new SignTask() {
            @Override
            public void performTask(PlayerInteractEvent e, Sign s) {
            }
        };
    }

    @Override
    public SignTask onSignLoad(Sign s, String name) {
        setSignName(s, "");
        return new SignTask() {
            @Override
            public void performTask(PlayerInteractEvent e, Sign s) {
            }
        };
    }
    String playName = "";

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        if (e.getTeam() != null) {
            playName = e.getTeam().getTeamColor() + "Team";
        } else {
            if (e.getWinners().length > 0) {
                playName = MCPartyCore.getInstance().getPlayerManager().isVIP(e.getWinners()[0])
                        ? ChatColor.GOLD + e.getWinners()[0].getName() : e.getWinners()[0].getName();
            } else {
                playName = ChatColor.GOLD + "TomFromCollege";
            }
        }
        for (Sign s : getSigns().keySet()) {
            setSignName(s, playName);
        }
    }

    public void setSignName(Sign s, String name) {
        s.setLine(0, "");
        s.setLine(1, "Latest Winner");
        s.setLine(2, name);
        s.setLine(3, "");
    }

    @Override
    public void onRemoveSign(Sign s) {
    }
}
