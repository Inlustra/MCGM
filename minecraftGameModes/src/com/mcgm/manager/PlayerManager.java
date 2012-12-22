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
                if (args.length >= 2) {
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
                    sender.sendMessage(MCPartyConfig.parse("creditCommand", getCredits((Player) sender) + ""));
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

    public void sendChange(Player p, String string, int amount) {
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
            setCredits(p, Integer.parseInt((String) jdata.get("credits")));
        } else if (jdata.containsKey("id")) {
            setId(p, Integer.parseInt((String) jdata.get("id")));
        } else if (jdata.containsKey("wins")) {
            setWins(p, Integer.parseInt((String) jdata.get("wins")));
        } else if (jdata.containsKey("losses")) {
            setLosses(p, Integer.parseInt((String) jdata.get("losses")));
        }
    }

    public void login(JSONObject jdata) {
        Player p = plugin.getServer().getPlayerExact((String) jdata.get("username"));
        PlayerProperties pp = new PlayerProperties(p, (Integer.parseInt((String) jdata.get("vip"))) == 1 ? true : false,
                Integer.parseInt((String) jdata.get("credits")),
                Integer.parseInt((String) jdata.get("id")),
                Integer.parseInt((String) jdata.get("wins")),
                Integer.parseInt((String) jdata.get("losses")));
        playerProperties.put(p, pp);
        p.sendMessage(MCPartyConfig.parse("login", p.getName(), (String) jdata.get("lastlogin")));
    }

    public void loginFirst(JSONObject jdata) {
        Player p = plugin.getServer().getPlayerExact((String) jdata.get("username"));
        PlayerProperties pp = new PlayerProperties(p, (Integer.parseInt((String) jdata.get("vip"))) == 1 ? true : false,
                Integer.parseInt((String) jdata.get("credits")),
                Integer.parseInt((String) jdata.get("id")),
                Integer.parseInt((String) jdata.get("wins")),
                Integer.parseInt((String) jdata.get("losses")));
        playerProperties.put(p, pp);
        p.sendMessage(MCPartyConfig.parse("Spawn.Message", p.getName()));
        p.sendMessage(MCPartyConfig.parse("Spawn.MessageFirst", p.getName()));
    }

    public boolean isPlayerLoggedIn(Player p) {
        return playerProperties.containsKey(p);
    }

    public PlayerProperties getPlayerProperties(Player p) {
        return playerProperties.get(p);
    }

    public void removePlayer(Player p) {
        playerProperties.remove(p);
    }

    public HashMap<Player, PlayerProperties> getPlayerProperties() {
        return playerProperties;
    }

    public void setVIP(Player p, boolean VIP) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            pp.VIP = VIP;
        } else {
            System.err.println("[MCGM] Could not set player properties for setVIP: " + p.getName());
        }
    }

    public void setCredits(Player p, int credits) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            pp.credits = credits;
        } else {
            System.err.println("[MCGM] Could not set player properties for setCredits: " + p.getName());
        }
    }

    public void setId(Player p, int id) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            pp.id = id;
        } else {
            System.err.println("[MCGM] Could not set player properties for setID: " + p.getName());
        }
    }

    public void setWins(Player p, int wins) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            pp.wins = wins;
        } else {
            System.err.println("[MCGM] Could not set player properties for setWins: " + p.getName());
        }
    }

    public void setLosses(Player p, int losses) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            pp.losses = losses;
        } else {
            System.err.println("[MCGM] Could not set player properties, setLosses: " + p.getName());
        }
    }

    public boolean isVIP(Player p) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            return pp.VIP;
        } else {
            System.err.println("[MCGM] Could not get player properties, isVIP: " + p.getName());
            return false;
        }
    }

    public int getCredits(Player p) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            return pp.credits;
        } else {
            System.err.println("[MCGM] Could not get player properties, getCredits: " + p.getName());
            return -1;
        }
    }

    public int getID(Player p) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            return pp.id;
        } else {
            System.err.println("[MCGM] Could not get player properties, getId: " + p.getName());
            return -1;
        }
    }

    public int getWins(Player p) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            return pp.wins;
        } else {
            System.err.println("[MCGM] Could not get player properties, getWins: " + p.getName());
            return -1;
        }
    }

    public int getLosses(Player p) {
        PlayerProperties pp = playerProperties.get(p);
        if (pp != null) {
            return pp.losses;
        } else {
            System.err.println("[MCGM] Could not get player properties, getLosses: " + p.getName());
            return -1;
        }
    }
}
