/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.MCPartyCore;
import com.mcgm.config.MCPartyConfig;
import com.mcgm.game.provider.GameDefinition;
import com.mcgm.player.PlayerProperties;
import com.mcgm.player.teleport.PlayerTeleport;
import com.mcgm.utils.Misc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

/**
 *
 * @author Thomas
 */
public class PlayerManager {

    private MCPartyCore plugin;
    private final Object teleportLock = new Object();
    private Queue<PlayerTeleport> teleportQueue = new LinkedList<>();

    public PlayerManager(final MCPartyCore plugin) {
        this.plugin = plugin;

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    synchronized (teleportLock) {
                        try {
                            while (!teleportQueue.isEmpty()) {
                                teleportQueue.poll().teleport();
                            }
                            teleportLock.wait();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }
        }).start();
        Command register = new Command("register", "register the play online", "REGISTER", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                if (args.length >= 1) {
                    JSONObject e = new JSONObject();
                    e.put("username", sender.getName());
                    e.put("password", args[0]);
                    e.put("email", args[1]);
                    plugin.getWebManager().sendData("register", e);
                } else {
                    sender.sendMessage("You didn't input this correctly.");
                }
                return true;
            }
        };
        plugin.getCommandManager().addCommand(register);
        Command credits = new Command("credits", "Display the amount of credits you have.", "CREDITS", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                if (isPlayerLoggedIn((Player) sender)) {
                    sender.sendMessage(MCPartyConfig.parse("creditCommand", getPlayerProperties((Player) sender).getCredits() + ""));
                } else {
                    sender.sendMessage(MCPartyConfig.parse("badlogin", ((Player) sender).getName()));
                }
                return true;
            }
        };
        plugin.getCommandManager().addCommand(credits);
    }

    public void teleport(PlayerTeleport t) {
        if (t.getL().getWorld() == null) {
            plugin.getWorldManager().loadWorlds(t.getL().getWorld().getName());
        }
        synchronized (teleportLock) {
            teleportQueue.add(t);
            teleportLock.notifyAll();
        }
    }
    static HashMap<Player, PlayerProperties> playerProperties = new HashMap<>();

    public void updateWeb(Player p, String string, int amount) {
        JSONObject data = new JSONObject();
        data.put("username", p.getName());
        data.put("what", string.toLowerCase());
        data.put("amount", amount > 0 ? "+" + amount : amount);
        plugin.getWebManager().sendData("change", data);
    }

    public void notifyOffline(Player p) {
        JSONObject data = new JSONObject();
        data.put("username", p.getName());
        data.put("online", 0);
        plugin.getWebManager().sendData("playerupdate", data);
    }

    public void updatePlayer(JSONObject jdata) {
        Player p = plugin.getServer().getPlayer((String) jdata.get("username"));
        if (jdata.containsKey("credits")) {
            playerProperties.get(p).setCredits(Integer.parseInt((String) jdata.get("credits")));
        } else if (jdata.containsKey("id")) {
            playerProperties.get(p).setId(Integer.parseInt((String) jdata.get("id")));
        } else if (jdata.containsKey("wins")) {
            playerProperties.get(p).setWins(Integer.parseInt((String) jdata.get("wins")));
        } else if (jdata.containsKey("losses")) {
            playerProperties.get(p).setLosses(Integer.parseInt((String) jdata.get("losses")));
        }
    }

    public void login(JSONObject jdata) {
        Player p = plugin.getServer().getPlayer((String) jdata.get("username"));
        PlayerProperties pp = new PlayerProperties(p, (Integer.parseInt((String) jdata.get("vip"))) == 1 ? true : false,
                Integer.parseInt((String) jdata.get("credits")),
                Integer.parseInt((String) jdata.get("id")),
                Integer.parseInt((String) jdata.get("wins")),
                Integer.parseInt((String) jdata.get("losses")));
        playerProperties.put(p, pp);
        p.sendMessage(MCPartyConfig.parse("login", p.getName(), (String) jdata.get("lastlogin")));
    }
    
    public boolean isPlayerLoggedIn(Player p) {
        return playerProperties.containsKey(p);
    }

    public PlayerProperties getPlayerProperties(Player p) {
        return playerProperties.get(p);
    }

    public HashMap<Player, PlayerProperties> getPlayerProperties() {
        return playerProperties;
    }
}
