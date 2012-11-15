/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.config;

import com.mcgm.Plugin;
import com.mcgm.utils.Paths;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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
            Location l = yawpitch ? new Location(Plugin.getInstance().getWorldManager().getMainWorld(),
                    getInt(key + "X"), getInt(key + "Y"), getInt(key + "Z"), getInt(key + "Yaw"), getInt(key + "Pitch"))
                    : new Location(Bukkit.getWorld(key + "World"), getInt(key + "X"), getInt(key + "Y"), getInt(key + "Z"));
            locationCache.put(key, l);
            return l;
        }
    }

    public static Location getLocation(String key) {
        return getLocation(key, false);
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
        sendMessage(players, key, (String) null);
    }

    public static void sendMessage(ArrayList<Player> players, String key, String... inputs) {
        sendMessage(players.toArray(new CommandSender[players.size()]), key, (Object[]) inputs);
    }

    public static void reloadConfig(CommandSender cs) {
        customConfig = YamlConfiguration.loadConfiguration(Paths.MCPartyConfig);
        locationCache.clear();
        if (cs != null) {
            cs.sendMessage("Reloaded Config");
        }
    }
}
