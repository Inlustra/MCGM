/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.mcgm.manager.GameManager;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
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
    public static String LogonURL = "http://virus78.chocolate.feralhosting.com/mcgmweb/testpost.php";
    public static Location MAIN_SPAWN = new Location(Misc.getMainWorld(), 94, 179, 163);

    public static String convertStreamToString(InputStream is)
            throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    public static void cleanPlayer(Player p, boolean teleport) {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.getInventory().clear();
        p.setWalkSpeed(0.2f);
        p.setLevel(0);
        p.setExp(0);
        if (teleport) {
            p.teleport(Misc.MAIN_SPAWN);
        }
    }

    public static World getMainWorld() {
        return GameManager.getInstance(null).getPlugin().getServer().getWorld(MAIN_WORLD);
    }

    public static World getMinigameWorld() {
        try {
            return GameManager.getInstance(null).getPlugin().getServer().getWorld(MINIGAME_WORLD);
        } catch (Exception e) {
            e.printStackTrace();
            GameManager.getInstance(null).getPlugin().getServer().createWorld(new WorldCreator(MINIGAME_WORLD));
            return Bukkit.getWorld(MINIGAME_WORLD);
        }
    }

    public static boolean minigameWorldExists() {
        return new File(Paths.serverDir.getPath() + "/" + MINIGAME_WORLD).exists();
    }

    public static void generateMinigameWorld(long seed) {
        try {
            removeMinigameWorld();
            WorldCreator c = new WorldCreator(MINIGAME_WORLD);
            if (seed != -1) {
                GameManager.getInstance(null).getPlugin().getServer().createWorld(c.seed(seed));
            } else {
                GameManager.getInstance(null).getPlugin().getServer().createWorld(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            ex.printStackTrace();
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
