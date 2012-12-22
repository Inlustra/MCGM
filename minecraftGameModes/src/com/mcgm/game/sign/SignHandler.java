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
    private String configText;
    private HashMap<Sign, SignTask> tasks = new HashMap();
    
    public abstract SignTask signSet(SignChangeEvent e);
    
    public abstract SignTask onSignLoad(Sign s, String name);
    
    public SignHandler(String configText, String handlingText) {
        this.handlingText = handlingText;
        this.configText = configText;
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
        if (MCPartyConfig.getConfig().contains(configText)) {
            Set<String> names = MCPartyConfig.getConfig().getConfigurationSection(configText).getKeys(true);
            for (String name : names) {
                if (MCPartyConfig.getBoolean("Development.ShowSignLoading")) {
                    System.out.println(MCPartyConfig.getLocation(configText + "." + name));
                }
                Block b = MCPartyConfig.getLocation(configText + "." + name).getBlock();
                if (b.getType() == Material.SIGN_POST
                        || b.getType() == Material.WALL_SIGN
                        || b.getType() == Material.SIGN) {
                    Sign sign = (Sign) MCPartyConfig.getLocation(configText + "." + name).getBlock().getState();
                    addSign(name, sign, false);
                    tasks.put(sign, onSignLoad(sign, name));
                }
                
            }
        }
    }
    
    public void saveSign(String signName, Location l) {
        MCPartyConfig.addLocation(configText + "." + signName, l);
        try {
            MCPartyConfig.getConfig().save(Paths.MCPartyConfig);
        } catch (IOException ex) {
            Logger.getLogger(SignHandler.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeSign(Sign s) {
        MCPartyConfig.getConfig().set(configText + "." + getSigns().get(s), null);
        onRemoveSign(s);
        tasks.remove(s);
    }
    
    public abstract void onRemoveSign(Sign s);
    
    public HashMap<Sign, SignTask> getTasks() {
        return tasks;
    }
}
