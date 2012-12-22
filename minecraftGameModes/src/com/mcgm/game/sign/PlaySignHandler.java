/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import com.mcgm.MCPartyCore;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Thomas
 */
public class PlaySignHandler extends SignHandler {
    
    public PlaySignHandler(String configText, String handlingText) {
        super(configText, handlingText);
    }
    public int currentSign = 0;
    
    @Override
    public SignTask signSet(SignChangeEvent e) {
        Sign si = (Sign) e.getBlock().getState();
        e.setCancelled(true);
        return setSignName(si, e.getLines());
    }
    
    @Override
    public SignTask onSignLoad(Sign s, String name) {
        return setSignName(s, null);
    }
    
    public SignTask setSignName(Sign s, String[] linesAdded) {
        s.setLine(0, "");
        s.setLine(1, currentSign == 0 ? ChatColor.GREEN + "Join" : ChatColor.RED + "Leave");
        s.setLine(2, "Play Queue");
        s.setLine(3, "");
        s.update(true);
        if (currentSign == 0) {
            currentSign++;
            return new SignTask() {
                @Override
                public void performTask(PlayerInteractEvent e, Sign s) {
                    MCPartyCore.getInstance().getGameManager().joinPlaying(e.getPlayer());
                }
            };
        } else {
            currentSign++;
            return new SignTask() {
                @Override
                public void performTask(PlayerInteractEvent e, Sign s) {
                    MCPartyCore.getInstance().getGameManager().stopPlaying(e.getPlayer());
                }
            };
        }
        
    }
    
    @Override
    public void onRemoveSign(Sign s) {
        currentSign--;
    }
}
