/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameDefinition;
import com.mcgm.game.provider.GameSource;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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
public class GameManager implements Listener, UncaughtExceptionHandler {

    private Minigame currentMinigame;
    private static GameManager instance;
    private GameSource gameSrc;
    private List<GameDefinition> gameDefs;
    private String gameList;
    private Plugin plugin;
    private ArrayList<Player> playing;

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
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().sendMessage(ChatColor.GREEN + "Welcome to " + ChatColor.DARK_PURPLE + "MCGM" + ChatColor.GREEN + "! We currently have: " + ChatColor.GOLD + playing.size() + ChatColor.GREEN + " playing" + ChatColor.DARK_PURPLE + " Minigames!");
    }

    @EventHandler
    public void onGameEnd(GameEndEvent end) {
        if (currentMinigame != null) {
            currentMinigame.onEnd();
            HandlerList.unregisterAll(currentMinigame);
            playersVoted.clear();
            for (Player p : currentMinigame.playing) {
                p.teleport(Misc.MAIN_SPAWN);
                p.setHealth(20);
                p.getInventory().clear();
            }
            currentMinigame = null;
            Misc.removeMinigameWorld();
            voteTime = 180;
        }
    }
    private int voteTime = 180;

    public GameManager(final Plugin p) {
        playing = new ArrayList<>();
        plugin = p;
        int taskID = p.getServer().getScheduler().scheduleAsyncRepeatingTask(p, new Runnable() {
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

    public void loadManager() {
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
                    cs.sendMessage("§cCould not find game: §2" + name.trim());
                } else {
                    cs.sendMessage("§cWrong usage, Please use as follows: §2/vote <gamename>");
                }
                return true;
            }
        };
        Command lobby = new Command("lobby", "remove player from playing list", "LOBBY", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                playing.remove((Player) cs);
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
                    if (currentMinigame == null) {
                        cs.sendMessage(ChatColor.GOLD + "Cast your votes!");
                        cs.sendMessage(gameList);
                        playing.add((Player) cs);
                    } else {
                        cs.sendMessage(ChatColor.RED + "A game is currently in progress, please wait for the game to finish.");
                        playing.add((Player) cs);
                    }
                } else {
                    cs.sendMessage(ChatColor.RED + "You're already playing!");
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
                            cs.sendMessage("§cCould not find game: §2" + name.trim());
                        } else {
                            cs.sendMessage("§cPlease wait until there are at least§2 2 players§c playing (Wait until level countdown starts)");
                        }
                    } else {
                        cs.sendMessage("§cWrong usage, Please use as follows: §2/vote <gamename>");
                    }
                } else {
                    cs.sendMessage("§c Please type /play in order to join the queue");
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
                    sender.sendMessage("§cCould not find game: §2" + name.trim());
                }
                return true;
            }
        };
        plugin.getCommandManager().addCommand(list);
        plugin.getCommandManager().addCommand(vote);
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
            p.sendMessage(ChatColor.GREEN + "Vote for: " + ChatColor.GOLD + " " + gdef.getName() + ChatColor.GREEN + " counted!");
            Bukkit.broadcastMessage(ChatColor.GOLD + gdef.getName() + ChatColor.GREEN + " votes: " + ChatColor.GOLD
                    + gdef.votes + ChatColor.GREEN + " out of " + ChatColor.GOLD + playing.size());
            if (playing.size() == playersVoted.size() && playing.size() > 1) {
                performCountDown(5);
            }
        } else {
            p.sendMessage("You've already voted!");
        }

    }

    public void performCountDown(final int time) {
        performCountDown(time, null);
    }

    public void performCountDown(final int time, final GameDefinition game) {
        Misc.generateMinigameWorld();

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

        try {
            currentMinigame = ((Minigame) gameToRun.clazz.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (GameDefinition gdef : gameDefs) {
            gdef.votes = 0;
        }
        final GameDefinition g = gameToRun;
        plugin.getServer().broadcastMessage("Starting " + g.getName());
        currentMinigame.generateGame();
        int i = 6;
        while (i-- > 0) {
            try {
                Thread.sleep(1000);
                plugin.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "" + i + "...");
            } catch (InterruptedException ex) {
                Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        currentMinigame.startGame();

    }

    public static GameManager getInstance(Plugin p) {
        synchronized (GameManager.class) {
            if (instance == null) {
                instance = new GameManager(p);
            }
        }
        return instance;
    }

    public void loadGameList() {
        gameSrc = new GameSource(Paths.compiledDir);
        gameDefs = gameSrc.list();
        StringBuilder sb = new StringBuilder();
        for (GameDefinition def : gameDefs) {
            sb.append(def.getName()).append("(").append(Misc.buildString(def.aliases, ",")).append(") ");
        }
        gameList = sb.toString();
        Misc.outPrint(gameList);
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

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Uncaught exception: " + e.getMessage());
        Location x = Bukkit.getWorld("world").getSpawnLocation();
        currentMinigame = null;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.teleport(x);
        }
    }

    public ArrayList<Player> getPlaying() {
        return playing;
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
