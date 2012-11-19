/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import com.mcgm.config.MCPartyConfig;
import com.mcgm.utils.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Thomas
 */
public abstract class SignHandler {

    private HashMap<Sign, String> handlingSigns;
    private String handlingText;

    public abstract void performTask(PlayerInteractEvent e, Sign s);

    public abstract void signSet(SignChangeEvent e);

    public abstract void onSignLoad(Sign s, String name);

    public SignHandler(String handlingText) {
        this.handlingText = handlingText;
        this.handlingSigns = new HashMap<>();
        loadConfig();
    }

    public void addSign(String signName, Sign s, boolean save) {
        handlingSigns.put(s, signName);
        if (save) {
            saveSign(signName, s.getBlock().getLocation());
        }
    }

    public HashMap<Sign, String> getSigns() {
        return handlingSigns;
    }

    public String getHandlingText() {
        return handlingText;
    }

    private void loadConfig() {
        if (MCPartyConfig.getConfig().contains("signs." + this.getClass().getSimpleName())) {
            Set<String> names = MCPartyConfig.getConfig().getConfigurationSection("signs." + this.getClass().getSimpleName()).getKeys(true);
            for (String name : names) {
                Block b = MCPartyConfig.getLocation("signs." + this.getClass().getSimpleName() + "." + name).getBlock();
                if (b.getType() == Material.SIGN_POST
                        || b.getType() == Material.WALL_SIGN
                        || b.getType() == Material.SIGN) {
                    addSign(name, (Sign) MCPartyConfig.getLocation("signs." + this.getClass().getSimpleName() + "." + name).getBlock().getState(), false);
                    onSignLoad((Sign) MCPartyConfig.getLocation("signs." + this.getClass().getSimpleName() + "." + name).getBlock().getState(), name);
                }
            }
        }
    }

    public void saveSign(String signName, Location l) {
        MCPartyConfig.addLocation("signs." + this.getClass().getSimpleName() + "." + signName, l);
        try {
            MCPartyConfig.getConfig().save(Paths.MCPartyConfig);
        } catch (IOException ex) {
            Logger.getLogger(SignHandler.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeSign(Sign s) {
        MCPartyConfig.getConfig().set("signs." + this.getClass().getSimpleName() + "." + getSigns().get(s), null);
        onRemoveSign(s);
    }

    public abstract void onRemoveSign(Sign s);
}
