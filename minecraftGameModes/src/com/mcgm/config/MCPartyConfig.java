/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.config;

import com.mcgm.MCPartyCore;
import com.mcgm.game.sign.SignHandler;
import com.mcgm.manager.GameManager;
import com.mcgm.utils.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/**
 *
 * @author Thomas
 */
public class MCPartyConfig {

    private static Matcher inputMatcher = Pattern.compile("~").matcher("");
    private static YamlConfiguration customConfig = YamlConfiguration.loadConfiguration(Paths.MCPartyConfig);

    public static String parse(String key, Object... inputs) {
        String value = customConfig.getString(key);
        inputMatcher.reset(value);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (inputMatcher.find()) {
            if (inputs[i] instanceof String) {
                inputMatcher.appendReplacement(sb, inputs[i].toString());
                i++;
            }
        }
        inputMatcher.appendTail(sb);
        return sb.toString().trim();
    }

    public static float getFloat(String key) {
        return (float) customConfig.getDouble(key, -1);
    }

    public static boolean getBoolean(String key) {
        return customConfig.getBoolean(key);
    }

    public static double getDouble(String key) {
        return customConfig.getDouble(key, -1);
    }

    public static int getInt(String key) {
        return customConfig.getInt(key, -1);
    }
    private static HashMap<String, Location> locationCache = new HashMap<>();

    public static Location getLocation(String key, boolean yawpitch) {
        if (locationCache.containsKey(key)) {
            return locationCache.get(key);
        } else {
            String[] locSplit = customConfig.getString(key).split(" ");
            double x = Double.parseDouble(locSplit[0]);
            double y = Double.parseDouble(locSplit[1]);
            double z = Double.parseDouble(locSplit[2]);

            float yaw = Float.parseFloat(locSplit[3]);
            float pitch = Float.parseFloat(locSplit[4]);
            Location l;
            if (yawpitch) {
                l = new Location(MCPartyCore.getInstance().getWorldManager().getMainWorld(),
                        x, y, z, yaw, pitch);
            } else {
                l = new Location(MCPartyCore.getInstance().getWorldManager().getMainWorld(),
                        x, y, z);
            }
            locationCache.put(key, l);
            return l;
        }
    }

    public static void addLocation(String key, Location l) {
        StringBuilder sb = new StringBuilder();
        sb.append(l.getBlockX()).append(" ");
        sb.append(l.getBlockY()).append(" ");
        sb.append(l.getBlockZ()).append(" ");
        sb.append(l.getYaw()).append(" ");
        sb.append(l.getPitch()).append(" ");
        customConfig.set(key, sb.toString());
    }

    public static Location getLocation(String key) {
        return getLocation(key, false);
    }

    public static void removeLocation(String key) {
        customConfig.set(key + ".X", null);
        customConfig.set(key + ".Y", null);
        customConfig.set(key + ".Z", null);
        customConfig.set(key + ".Pitch", null);
        customConfig.set(key + ".Yaw", null);
        try {
            MCPartyConfig.getConfig().save(Paths.MCPartyConfig);
        } catch (IOException ex) {
            Logger.getLogger(SignHandler.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendMessage(CommandSender cs, String key, Object... inputs) {
        cs.sendMessage(parse(key, inputs));
    }

    public static void sendMessage(CommandSender cs, String key) {
        sendMessage(cs, key, (Object) null);
    }

    public static void sendMessage(CommandSender[] cs, String key, Object... inputs) {
        for (CommandSender p : cs) {
            sendMessage(p, key, inputs);
        }
    }

    public static void sendMessage(CommandSender[] cs, String key) {
        sendMessage(cs, key, (Object) null);
    }

    public static void sendMessage(ArrayList<Player> players, String key) {
        synchronized (GameManager.playingQueueLock) {
            sendMessage(players, key, (String) null);
        }
    }

    public static void sendMessage(ArrayList<Player> players, String key, String... inputs) {
        synchronized (GameManager.playingQueueLock) {
            sendMessage(players.toArray(new CommandSender[players.size()]), key, (Object[]) inputs);
        }
    }

    public static void reloadConfig(CommandSender cs) {
        customConfig = YamlConfiguration.loadConfiguration(Paths.MCPartyConfig);
        locationCache.clear();
        if (cs != null) {
            cs.sendMessage("Reloaded Config");
        }
    }

    public static YamlConfiguration getConfig() {
        return customConfig;
    }
}
