/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.MCPartyCore;
import com.mcgm.game.sign.SignHandler;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Thomas
 */
public class SignManager implements Listener {

    MCPartyCore plugin;
    List<SignHandler> handlers;

    public SignManager(MCPartyCore p) {
        this.plugin = p;
        handlers = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addSignHandler(SignHandler sh) {
        handlers.add(sh);
    }

    @EventHandler
    public void onChangeSign(SignChangeEvent e) {
        lines:
        for (String s : e.getLines()) {
            for (SignHandler handler : handlers) {
                System.out.println(handler.getHandlingText() + " " + s.toLowerCase());
                if (s.toLowerCase().contains(handler.getHandlingText().toLowerCase())) {
                    if (s.contains(":")) {
                        String signname = s.substring(s.indexOf(":") + 1);
                        handler.signSet(e);
                        handler.addSign(signname, (Sign) e.getBlock().getState(), true);
                        break lines;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }
        if (e.getClickedBlock().getType() == Material.SIGN_POST
                || e.getClickedBlock().getType() == Material.WALL_SIGN
                || e.getClickedBlock().getType() == Material.SIGN) {
            BlockState state = e.getClickedBlock().getState();
            if (state instanceof org.bukkit.block.Sign) {
                final Sign s = (Sign) state;
                signLoop:
                for (SignHandler handler : handlers) {
                    for (Sign s2 : handler.getSigns().keySet()) {
                        System.out.println(s.getLine(1) + " " + s2.getLine(1));
                        if (s.getLine(1).equals(s2.getLine(1))) {
                            handler.performTask(e, s);
                            break signLoop;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.SIGN_POST
                || e.getBlock().getType() == Material.WALL_SIGN
                || e.getBlock().getType() == Material.SIGN) {
            for (SignHandler handler : handlers) {
                if (handler.getSigns().containsKey((Sign) e.getBlock().getState())) {
                    handler.removeSign((Sign) e.getBlock().getState());
                }
            }
        }
    }
}
