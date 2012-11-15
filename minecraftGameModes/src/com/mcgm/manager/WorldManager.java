/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.Plugin;
import com.mcgm.utils.Misc;
import com.mcgm.utils.WorldUtils;
import com.mcgm.worlds.PlayerTeleport;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author Thomas
 */
public class WorldManager {

    private final Object teleportLock = new Object();
    private Queue<PlayerTeleport> teleportQueue = new LinkedList<>();
    public HashMap<String, World> loadedWorlds = new HashMap<>();
    private Plugin plugin;

    public World getMainWorld() {
        return loadedWorlds.get(WorldUtils.MAIN_WORLD);
    }

    public World getMinigameWorld() {
        return loadedWorlds.get(WorldUtils.MINIGAME_WORLD);
    }

    public WorldManager(Plugin p) {
        this.plugin = p;
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
                            Logger.getLogger(PostManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }).start();
    }

    public void teleport(PlayerTeleport t) {
        if (t.getL().getWorld() == null) {
            loadWorlds(t.getL().getWorld().getName());
        }
        synchronized (teleportLock) {
            teleportQueue.add(t);
            teleportLock.notifyAll();
        }
    }

    public void loadWorlds(String... worlds) {
        for (String w : worlds) {
            if (Bukkit.getWorld(w) != null) {
                if (!loadedWorlds.containsKey(w)) {
                    loadedWorlds.put(w, Bukkit.getWorld(w));
                }
                Misc.outPrintWarning("World " + w + " already loaded");
            } else {
                Misc.outPrintWarning("World " + w + " started Loading!");
                WorldCreator creator = WorldCreator.name(w);
                doLoad(creator);
            }
        }
    }

    public void doLoad(WorldCreator creator) {
        World cbworld;
        try {
            cbworld = creator.createWorld();
            loadedWorlds.put(cbworld.getName(), cbworld);
        } catch (Exception e) {
            e.printStackTrace();
            Misc.outPrintWarning("World Broken: " + creator.name());
        }
    }

    public boolean deleteWorld(String name, boolean deleteWorldFolder) {
        World world = loadedWorlds.get(name);

        try {
            if (world != null) {
                File worldFile = world.getWorldFolder();
                Misc.outPrint("deleteWorld(): worldFile: " + worldFile.getAbsolutePath());
                if (deleteWorldFolder ? Misc.deleteFolder(worldFile) : Misc.deleteFolderContents(worldFile)) {
                    Misc.outPrint("World " + name + " was DELETED.");
                    return true;
                } else {
                    Misc.outPrintWarning("World " + name + " was NOT DELETED.");
                    return false;
                }
            } else {
                Misc.outPrintWarning("World was null");
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean regenWorld(String name, boolean useNewSeed, boolean randomSeed, String seed) {
        World world = loadedWorlds.get(name);
        if (world == null) {
            Misc.outPrintWarning("Unable to regenerate world: " + name + " as it wasn't loaded");
            return false;
        }
        WorldCreator c = WorldCreator.name(name);
        List<Player> ps = world.getPlayers();

        if (useNewSeed) {
            long theSeed;

            if (randomSeed) {
                theSeed = new Random().nextLong();
            } else {
                try {
                    theSeed = Long.parseLong(seed);
                    c = c.seed(theSeed);
                } catch (NumberFormatException e) {
                    c = c.seed(seed.hashCode());
                }
            }
        }
        purgeWorld(world);
        Bukkit.unloadWorld(name, false);
        if (this.deleteWorld(name, false)) {
            this.doLoad(c);
            Location newSpawn = world.getSpawnLocation();
            // Send all players that were in the old world, BACK to it!
            for (Player p : ps) {
                p.teleport(newSpawn);
            }
            loadWorlds(name);
            return true;
        }
        return false;
    }

    public void purgeWorld(World w) {
        for (Entity e : w.getEntities()) {
            if (e instanceof Player) {
                e.teleport(WorldUtils.getMainSpawn());
            } else {
                e.remove();
            }
        }
    }
}
