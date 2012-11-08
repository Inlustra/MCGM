/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.snapshots.Snapshot;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 *
 * @author Tom
 */
public class Misc {

    public static World getMainWorld() {
        return Bukkit.getWorld("world");
    }

    public static World getMinigameWorld() {
        if (Bukkit.getWorld("minigameWorld") != null) {
            Bukkit.getServer().createWorld(new WorldCreator("minigameWorld"));
        }
        return Bukkit.getWorld("minigameWorld");
    }

    public static void removeMinigameWorld() {
        File f = new File(Paths.serverDir.getPath()+"/minigameWorld");
        
    }

    public static void loadArea(final File file, final Vector origin) {
        try {
            EditSession es = new EditSession(BukkitUtil.getLocalWorld(Bukkit.getWorld("world")), 999999999);
            CuboidClipboard cc = SchematicFormat.MCEDIT.load(file);
            cc.paste(es, origin, false);
        } catch (MaxChangedBlocksException | IOException | DataException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
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
