/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import com.mcgm.MCPartyCore;
import com.mcgm.game.provider.GameDefinition;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Thomas
 */
public class MinigameSignHandler extends SignHandler {

    int currentSign = 0;

    public MinigameSignHandler(String handlingText) {
        super(handlingText);
    }

    @Override
    public void performTask(PlayerInteractEvent e, Sign s) {
        for (GameDefinition gdef : MCPartyCore.getInstance().getGameManager().getGameDefs()) {
            if (gdef.getName().toLowerCase().contains(s.getLine(1))) {
                MCPartyCore.getInstance().getGameManager().addVote(e.getPlayer(), gdef);
            }
        }
    }

    @Override
    public void signSet(SignChangeEvent e) {
        setSignName((Sign) e.getBlock().getState());
    }

    @Override
    public void onSignLoad(Sign s, String name) {
        setSignName(s);
    }

    public void setSignName(Sign s) {
        GameDefinition gdef = MCPartyCore.getInstance().getGameManager().getGameDefs().get(currentSign);
        s.setLine(0, "");
        if (gdef.getName().length() >= 14) {
            String[] string = gdef.getName().split(" ");
            s.setLine(1, string[0].trim());
            s.setLine(2, string[1].trim());
            s.setLine(3, "");
        } else {
            s.setLine(1, "");
            s.setLine(2, gdef.getName());
            s.setLine(3, "");
        }
        currentSign++;
        s.update(true);
    }

    @Override
    public void onRemoveSign(Sign s) {
        if (currentSign > 0) {
            currentSign--;
        }
    }
}
