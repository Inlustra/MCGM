/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import com.mcgm.MCPartyCore;
import com.mcgm.game.provider.GameDefinition;
import com.mcgm.utils.Misc;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
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

    public MinigameSignHandler(String configText, String handlingText) {
        super(configText, handlingText);
    }

    @Override
    public SignTask signSet(SignChangeEvent e) {
        setSignName((Sign) e.getBlock().getState());
        e.setCancelled(true);
        return setSignTask();
    }

    @Override
    public SignTask onSignLoad(Sign s, String name) {
        setSignName(s);
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

    public void updateSignVotes() {
        int queuesize = MCPartyCore.getInstance().getGameManager().getPlaying().size();
        if (signMap != null) {
            for (Sign s : signMap.keySet()) {
                int votes = signMap.get(s).votes;
                s.setLine(3, "Votes " + votes + " / " + queuesize);
                s.update();
            }
        }
    }

    public void setSignName(Sign s) {
        if (MCPartyCore.getInstance().getGameManager().getPlayableGameDefs().size() > currentSign) {
            GameDefinition gdef = MCPartyCore.getInstance().getGameManager().getPlayableGameDefs().get(currentSign);
            System.out.println(gdef.name);
            if (gdef != null) {
                if (signMap == null) {
                    signMap = new HashMap<>();
                }
                if (!signMap.containsKey(s)) {
                    signMap.put(s, gdef);
                }
                s.setLine(0, "");
                if (gdef.getName().length() >= 14) {
                    String[] string = Misc.addLinebreaks(gdef.getName(), 12);
                    s.setLine(0, StringUtils.capitalize(string[0]));
                    s.setLine(1, StringUtils.capitalize(string.length > 0 ? string[1] : ""));
                } else {
                    s.setLine(0, StringUtils.capitalize(gdef.getName()));
                    s.setLine(1, "");
                }
                s.setLine(2, "");
                s.setLine(3, "Votes 0 / 0");
                currentSign++;
            } else {
                s.setLine(0, "");
                s.setLine(1, "More coming");
                s.setLine(2, "soon!");
                s.setLine(3, "");
            }
        } else {
            s.setLine(0, "");
            s.setLine(1, "More coming");
            s.setLine(2, "soon!");
            s.setLine(3, "");
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
