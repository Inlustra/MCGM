/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import com.mcgm.MCPartyCore;
import com.mcgm.game.provider.GameDefinition;
import java.util.HashMap;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Thomas
 */
public class MinigameSignHandler extends SignHandler {

    int currentSign = 0;
    public HashMap<Sign, GameDefinition> signMap;

    public MinigameSignHandler(String handlingText) {
        super(handlingText);
    }

    @Override
    public SignTask signSet(SignChangeEvent e) {
        setSignName((Sign) e.getBlock().getState(), e.getLines());
        e.setCancelled(true);
        return setSignTask();
    }

    @Override
    public SignTask onSignLoad(Sign s, String name) {
        setSignName(s, null);
        return setSignTask();
    }

    private SignTask setSignTask() {
        return new SignTask() {
            @Override
            public void performTask(PlayerInteractEvent e, Sign s) {
                if (signMap.containsKey(s)) {
                    GameDefinition gd = signMap.get(s);
                    MCPartyCore.getInstance().getGameManager().addVote(e.getPlayer(), gd);
                }
            }
        };
    }

    public void setSignName(Sign s, String[] linesAdded) {
        if (MCPartyCore.getInstance().getGameManager().getGameDefs().size() > currentSign) {
            GameDefinition gdef = MCPartyCore.getInstance().getGameManager().getGameDefs().get(currentSign);
            if (gdef != null) {
                if (signMap == null) {
                    signMap = new HashMap<>();
                }
                if (!signMap.containsKey(s)) {
                    signMap.put(s, gdef);
                }
                s.setLine(0, "");
                if (gdef.getName().length() >= 14) {
                    String[] string = gdef.getName().split(" ");
                    s.setLine(0, "");
                    s.setLine(1, string[0].trim());
                    s.setLine(2, string[1].trim());
                    s.setLine(3, string.length >= 3 ? string[2].trim() : "");
                } else {
                    s.setLine(2, "");
                    s.setLine(1, gdef.getName());
                    s.setLine(3, "");
                }
                currentSign++;
            } else {
                s.setLine(0, "No games left!");
            }
        } else {
            s.setLine(0, "No games left!");
        }
        s.update(true);
    }

    @Override
    public void onRemoveSign(Sign s) {
        signMap.remove(s);
        if (currentSign > 0) {
            currentSign--;
        }
    }
}
