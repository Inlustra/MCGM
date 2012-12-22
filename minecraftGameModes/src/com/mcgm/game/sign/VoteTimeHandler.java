/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import com.mcgm.MCPartyCore;
import com.mcgm.game.event.GameEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Thomas
 */
public class VoteTimeHandler extends SignHandler {

    public VoteTimeHandler(String configText, String handlingText) {
        super(configText, handlingText);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(MCPartyCore.getInstance(), new Runnable() {
            @Override
            public void run() {
                String timeLeft = MCPartyCore.getInstance().getGameManager().getVoteTime() + "";
                String line1 = MCPartyCore.getInstance().getGameManager().isGamePlaying()
                        ? "" : "Time until";
                String line2 = MCPartyCore.getInstance().getGameManager().isGamePlaying()
                        ? "Game currently in progress" : "game starts";
                String line3 = MCPartyCore.getInstance().getGameManager().isGamePlaying()
                        ? "§c" + "" : "§a" + timeLeft;
                String line4 = "";
                for (Sign s : getSigns().keySet()) {
                    s.setLine(0, line1);
                    s.setLine(1, line2);
                    s.setLine(2, line3);
                    s.setLine(3, line4);
                    s.update();
                }
            }
        }, 20L, 20L);
    }

    @Override
    public SignTask signSet(SignChangeEvent e) {
        return new SignTask() {
            @Override
            public void performTask(PlayerInteractEvent e, Sign s) {
            }
        };
    }

    @Override
    public SignTask onSignLoad(Sign s, String name) {
        return new SignTask() {
            @Override
            public void performTask(PlayerInteractEvent e, Sign s) {
            }
        };
    }

    @Override
    public void onRemoveSign(Sign s) {
    }
}
