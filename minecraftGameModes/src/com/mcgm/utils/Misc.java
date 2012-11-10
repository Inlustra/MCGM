/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

/**
 *
 * @author Tom
 */
public class Misc {

    public static String MAIN_WORLD = "world";
    public static String MINIGAME_WORLD = "minigameWorld";
    public static Location MAIN_SPAWN = new Location(Misc.getMainWorld(), 94, 179, 163);

    public static World getMainWorld() {
        return Bukkit.getWorld(MAIN_WORLD);
    }

    public static World getMinigameWorld() {
        try {
            return Bukkit.getWorld(MINIGAME_WORLD);
        } catch (Exception e) {
            Bukkit.getServer().createWorld(new WorldCreator(MINIGAME_WORLD));
            return Bukkit.getWorld(MINIGAME_WORLD);
        }
    }

    public static boolean minigameWorldExists() {
        return new File(Paths.serverDir.getPath() + "/" + MINIGAME_WORLD).exists();
    }

    public static void generateMinigameWorld() {
        try {
            removeMinigameWorld();
            Bukkit.getServer().createWorld(new WorldCreator(MINIGAME_WORLD));
        } catch (Exception e) {
        }
    }

    public static void removeMinigameWorld() {
        try {
            for (Player p : getMinigameWorld().getPlayers()) {
                p.teleport(MAIN_SPAWN);
            }
            Bukkit.unloadWorld(MINIGAME_WORLD, false);
            File f = new File(Paths.serverDir.getPath() + "/" + MINIGAME_WORLD);
            delete(f);
        } catch (Exception ex) {
        }
    }

    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

    public static void loadArea(final File file, final Vector origin, String world) {
        try {
            EditSession es = new EditSession(BukkitUtil.getLocalWorld(Bukkit.getWorld(world)), 999999999);
            CuboidClipboard cc = SchematicFormat.MCEDIT.load(file);
            cc.paste(es, origin, false);
        } catch (MaxChangedBlocksException | IOException | DataException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Location[] getLocations(final File file, final Vector origin, String world, Material m) {
        ArrayList<Location> l = new ArrayList<>();
        try {
            CuboidClipboard cc = SchematicFormat.MCEDIT.load(file);
            cc.setOrigin(origin.add(cc.getOffset()));
            for (int x = (int) cc.getOrigin().getX(); x < cc.getOrigin().getX() + cc.getWidth(); x++) {
                for (int y = (int) cc.getOrigin().getY(); y < cc.getOrigin().getY() + cc.getHeight(); y++) {
                    for (int z = (int) cc.getOrigin().getZ(); z < cc.getOrigin().getZ() + cc.getLength(); z++) {
                        Location loc = new Location(Bukkit.getWorld(world), x, y, z);
                        if (loc.getBlock().getType() == m) {
                            l.add(loc);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l.toArray(new Location[l.size()]);

    }

    public static String buildString(String[] str, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i]);
            if (i != str.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static String buildPlayerString(Player[] str, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i].getName());
            if (i != str.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static int getRandom(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static void outPrint(String str) {
        System.out.println("[MCGM] " + str);
    }

    public static void outPrintWarning(String str) {
        outPrint("[Warning] " + str);
    }

    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }

    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf("/");
        int lastWindowsPos = filename.lastIndexOf("\\");
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    public static String getBaseName(String filename) {
        return removeExtension(getName(filename));
    }

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }

    public static boolean isJar(final File file) {
        return file.getName().endsWith(".jar") || file.getName().endsWith(".dat");
    }
}
