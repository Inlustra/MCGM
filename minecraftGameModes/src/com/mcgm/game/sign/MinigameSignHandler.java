/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import com.mcgm.MCPartyCore;
import com.mcgm.config.MCPartyConfig;
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
    public void performTask(PlayerInteractEvent e, Sign s) {
        if (s.getLine(1).equals(MCPartyConfig.parse("signs.MinigameSignHandler.play.2", (Object) null))) {
            MCPartyCore.getInstance().getGameManager().joinPlaying(e.getPlayer());
        } else if (s.getLine(1).equals(MCPartyConfig.parse("signs.MinigameSignHandler.lobby.2", (Object) null))) {
            MCPartyCore.getInstance().getGameManager().stopPlaying(e.getPlayer());
        } else if (signMap.containsKey(s)) {
            GameDefinition gd = signMap.get(s);
            MCPartyCore.getInstance().getGameManager().addVote(e.getPlayer(), gd);
        }
    }

    @Override
    public void signSet(SignChangeEvent e) {
        setSignName((Sign) e.getBlock().getState());
        e.setCancelled(true);
    }

    @Override
    public void onSignLoad(Sign s, String name) {
        setSignName(s);
    }

    public void setSignName(Sign s) {
        GameDefinition gdef = MCPartyCore.getInstance().getGameManager().getGameDefs().get(currentSign);
        if (MCPartyConfig.parse("signs.MinigameSignHandler.playstring", (Object) null).equals(s.getLine(2).toLowerCase().trim())) {
            s.setLine(0, MCPartyConfig.parse("signs.MinigameSignHandler.play.1", (Object) null));
            s.setLine(1, MCPartyConfig.parse("signs.MinigameSignHandler.play.2", (Object) null));
            s.setLine(2, MCPartyConfig.parse("signs.MinigameSignHandler.play.3", (Object) null));
            s.setLine(3, MCPartyConfig.parse("signs.MinigameSignHandler.play.4", (Object) null));
        } else if (MCPartyConfig.parse("signs.MinigameSignHandler.lobbystring", (Object) null).equals(s.getLine(2).toLowerCase().trim())) {
            s.setLine(0, MCPartyConfig.parse("signs.MinigameSignHandler.lobby.1", (Object) null));
            s.setLine(1, MCPartyConfig.parse("signs.MinigameSignHandler.lobby.2", (Object) null));
            s.setLine(2, MCPartyConfig.parse("signs.MinigameSignHandler.lobby.3", (Object) null));
            s.setLine(3, MCPartyConfig.parse("signs.MinigameSignHandler.lobby.4", (Object) null));
        } else if (gdef != null) {
            if (signMap == null) {
                signMap = new HashMap<>();
            }
            if (!signMap.containsKey(s)) {
                signMap.put(s, gdef);
            }
            s.setLine(0, "");
            if (gdef.getName().length() >= 14) {
                String[] string = gdef.getName().split(" ");
                s.setLine(1, string[0].trim());
                s.setLine(2, string[1].trim());
                s.setLine(3, "");
            } else {
                s.setLine(2, "");
                s.setLine(1, gdef.getName());
                s.setLine(3, "");
            }
            currentSign++;
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
