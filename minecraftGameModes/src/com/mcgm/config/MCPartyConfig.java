/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.config;

import com.mcgm.utils.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Thomas
 */
public class MCPartyConfig {

    private static Matcher inputMatcher = Pattern.compile("([~])").matcher("");
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
        return sb.toString();
    }

    public static void sendMessage(CommandSender cs, String key, Object... inputs) {
        cs.sendMessage(parse(key, inputs));
    }

    public void sendMessage(CommandSender cs, String key) {
        sendMessage(cs, key, null);
    }

    public static void sendMessage(CommandSender[] cs, String key, Object... inputs) {
        for (CommandSender p : cs) {
            sendMessage(p, key, inputs);
        }
    }

    public static void sendMessage(CommandSender[] cs, String key) {
        sendMessage(cs, key, null);
    }

    public static void reloadConfig(CommandSender cs) {
        customConfig = YamlConfiguration.loadConfiguration(Paths.MCPartyConfig);
        if (cs != null) {
            sendMessage(cs, "Reloaded Config");
        }
    }
}
