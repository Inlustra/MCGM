/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.MCPartyCore;
import com.mcgm.config.MCPartyConfig;
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameDefinition;
import com.mcgm.game.provider.GameSource;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import com.mcgm.utils.PlayerUtils;
import com.mcgm.utils.WebUtils;
import com.mcgm.utils.WorldUtils;
import com.mcgm.web.Post;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Tom
 */
public class GameManager implements Listener {

    private Minigame currentMinigame;
    private Minigame lobbyMinigame;
    private GameSource gameSrc;
    private List<GameDefinition> gameDefs;
    private String gameList;
    private MCPartyCore plugin;
    private ArrayList<Player> playing;
    private int voteTime = 180;

    public GameManager(final MCPartyCore p) {
        playing = new ArrayList<>();
        plugin = p;
        int taskID = p.getServer().getScheduler().scheduleSyncRepeatingTask(p, new Runnable() {
            @Override
            public void run() {
                if (currentMinigame != null) {
                    currentMinigame.gameTime--;
                    if (currentMinigame.gameTime > 0) {
                        for (Player p : playing) {
                            p.setLevel(currentMinigame.gameTime);
                        }
                    }
                    currentMinigame.minigameTick();
                } else {
                    if (playing.size() > 1) {
                        for (Player p : playing) {
                            p.setLevel(voteTime);
                        }
                        if (voteTime == 0) {
                            performCountDown(5);
                        }
                        voteTime--;
                    }
                }
            }
        }, 0, 20L);
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (playing.contains(p)) {
            playing.remove(p);
        }
        if (currentMinigame != null) {
            if (currentMinigame.getPlaying().contains(p)) {
                currentMinigame.playerDisconnect(p);
                currentMinigame.getPlaying().remove(p);
                if (currentMinigame.getPlaying().size() <= 1) {
                    PlayerUtils.cleanPlayer(p, false);
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(currentMinigame, false, (Player) null));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {

        MCPartyConfig.sendMessage(e.getPlayer(), "Spawn.Message", "" + playing.size());
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                WorldUtils.teleportSafely(e.getPlayer(), MCPartyConfig.getLocation("Spawn.Prison", true));
                Post p = new Post(WebUtils.LogonURL, (Object) "name", (Object) e.getPlayer().getName()) {
                    @Override
                    public void serverResponse(String response) {
                        e.getPlayer().sendMessage(response);
                    }
                };
                plugin.getPostManager().postImmediate(p);
            }
        }, 20L);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent end) {
        if (currentMinigame != null) {
            for (Player p : currentMinigame.startingPlayers) {
                PlayerUtils.cleanPlayer(p, p.isOnline() ? true : false);
            }
            HandlerList.unregisterAll(currentMinigame);
            currentMinigame.onEnd();
            playersVoted.clear();
            currentMinigame = null;
            voteTime = 180;
        }
    }

    public void loadManager() {
        Command spawn = new Command("spawn", "Sends player to spawn", "SPAWN", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                PlayerUtils.cleanPlayer((Player) cs, true);
                return true;
            }
        };
        Command endgame = new Command("forceend", "Forces the end of the current minigame", "WORLDTP", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(currentMinigame, false, new Player[]{}));
                return true;
            }
        };
        Command startgame = new Command("forcestart", "Forces the start of minigame", "FORCESTART", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                if (args.length != 0 && cs instanceof Player) {
                    String name = Misc.buildString(args, " ");
                    GameDefinition gameFound = getGame(name);
                    if (gameFound != null) {
                        performCountDown(5, gameFound);
                        return true;
                    }
                    MCPartyConfig.sendMessage(cs, "gameNotFound", "" + name.trim());
                } else {
                    MCPartyConfig.sendMessage(cs, "voteWrongUsage");
                }
                return true;
            }
        };
        Command lobby = new Command("lobby", "remove player from playing list", "LOBBY", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                playing.remove((Player) cs);
                MCPartyConfig.sendMessage(cs, "removePlaying");
                return true;
            }
        };
        Command play = new Command("play", "Add player to playing list", "PLAY", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                if (!playing.contains((Player) cs)) {
                    for (Player p : playing) {
                        p.sendMessage(ChatColor.GOLD + cs.getName() + ChatColor.GREEN + " is now playing!");
                    }
                    playing.add((Player) cs);
                    if (currentMinigame == null) {
                        if (playing.size() > 1) {
                            MCPartyConfig.sendMessage(cs, "votesMessage");
                            cs.sendMessage(gameList);
                        } else {
                            MCPartyConfig.sendMessage(cs, "notEnoughPlayers");
                        }
                    } else {
                        MCPartyConfig.sendMessage(cs, "inProgress", currentMinigame.getName());
                    }
                } else {
                    MCPartyConfig.sendMessage(cs, "alreadyPlaying");
                }
                return true;
            }
        };
        Command list = new Command("games", "Simple list command", "LISTING", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                cs.sendMessage(gameList);
                return true;
            }
        };
        Command reload = new Command("rlgames", "Simple list command", "LISTING", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                loadGameList(cs);
                return true;
            }
        };
        Command vote = new Command("vote", "Simple vote command", "VOTING", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                if (playing.contains((Player) cs)) {
                    if (currentMinigame != null) {
                        cs.sendMessage("§cA minigame is currently in play, please wait for it to end");
                        return true;
                    }
                    if (args.length != 0 && cs instanceof Player) {
                        if (playing.size() > 1) {
                            String name = Misc.buildString(args, " ");
                            GameDefinition gameFound = getGame(name);
                            if (gameFound != null) {
                                addVote((Player) cs, gameFound);
                                return true;
                            }
                            MCPartyConfig.sendMessage(cs, "gameNotFound", name.trim());
                        } else {
                            MCPartyConfig.sendMessage(cs, "notEnoughPlayers");
                        }
                    } else {
                        MCPartyConfig.sendMessage(cs, "voteWrongUsage");
                    }
                } else {
                    MCPartyConfig.sendMessage(cs, "mustPlay");
                }
                return true;
            }
        };
        Command define = new Command("define", "Give the player the game description", "DEFINE", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                if (args.length != 0 && sender instanceof Player) {
                    String name = Misc.buildString(args, " ");
                    GameDefinition gameFound = getGame(name);
                    if (gameFound != null) {
                        sender.sendMessage(gameFound.getDescription());
                        return true;
                    }
                    MCPartyConfig.sendMessage(sender, "gameNotFound", name.trim());
                }
                return true;
            }
        };
        plugin.getCommandManager().addCommand(reload);
        plugin.getCommandManager().addCommand(list);
        plugin.getCommandManager().addCommand(vote);
        plugin.getCommandManager().addCommand(spawn);
        plugin.getCommandManager().addCommand(define);
        plugin.getCommandManager().addCommand(play);
        plugin.getCommandManager().addCommand(endgame);
        plugin.getCommandManager().addCommand(lobby);
        plugin.getCommandManager().addCommand(startgame);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    List<Player> playersVoted = new ArrayList<>();

    public void addVote(Player p, GameDefinition gdef) {
        if (!playersVoted.contains(p)) {
            playersVoted.add(p);
            gdef.votes++;
            MCPartyConfig.sendMessage(p, "addVote", gdef.getName());
            MCPartyConfig.sendMessage(playing, "voteCounts", gdef.getName(), gdef.votes + "", playing.size() + "");
            if (playing.size() == playersVoted.size() && playing.size() > 1) {
                performCountDown(5);
            }
        } else {
            MCPartyConfig.sendMessage(p, "alreadyVoted", gdef.getName());
        }

    }

    public void performCountDown(final int time) {
        performCountDown(time, null);
    }

    public void performCountDown(final int time, final GameDefinition game) {

        GameDefinition gameToRun = game;
        if (game == null) {
            GameDefinition highestVoted = gameDefs.get(0);
            for (GameDefinition gdef : gameDefs) {
                if (gdef.votes > highestVoted.votes) {
                    highestVoted = gdef;
                }
            }
            gameToRun = highestVoted;
        }

        MCPartyConfig.sendMessage(playing, "chosenGame", gameToRun.getName());

        plugin.getWorldManager().regenWorld(WorldUtils.MINIGAME_WORLD, true, "".equals(gameToRun.getSeed()) ? true : false, "" + gameToRun.getSeed());

        try {
            currentMinigame = ((Minigame) gameToRun.clazz.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (GameDefinition gdef : gameDefs) {
            gdef.votes = 0;
        }
        final GameDefinition g = gameToRun;
        currentMinigame.generateGame();
        setTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int i = 6;

            @Override
            public void run() {
                if (i > 0) {
                    i--;
                    currentMinigame.sendPlayingMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "" + i + "...");
                } else {
                    if (currentMinigame != null) {
                        currentMinigame.startGame();
                    }
                    cancel();
                }
            }
        }, 0, 20L));
    }
    private int id;

    public void setTaskId(int id) {
        this.id = id;
    }

    private void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }

    public void loadGameList(CommandSender cs) {
        gameSrc = new GameSource(Paths.compiledDir);
        gameDefs = gameSrc.list();
        StringBuilder sb = new StringBuilder();
        for (GameDefinition def : gameDefs) {
            sb.append(def.getName()).append("(").append(Misc.buildString(def.aliases, ",")).append(") ");
        }
        gameList = sb.toString();
        if (cs != null) {
            cs.sendMessage("Reloaded games: " + gameList);
        }
    }

    public GameDefinition getGame(String name) {
        for (GameDefinition def : gameDefs) {
            for (String alias : def.aliases) {
                if (alias.trim().toLowerCase().equals(name.trim().toLowerCase())) {
                    return def;
                }
            }
            if (def.getName().toLowerCase().equals(name.trim().toLowerCase())) {
                return def;
            }
        }
        return null;
    }

    public ArrayList<Player> getPlaying() {
        return playing;
    }

    public MCPartyCore getPlugin() {
        return plugin;
    }

    public GameSource getGameSrc() {
        return gameSrc;
    }

    public List<GameDefinition> getGameDefs() {
        return gameDefs;
    }
    
    
}
