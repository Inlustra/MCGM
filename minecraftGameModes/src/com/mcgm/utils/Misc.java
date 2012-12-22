/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.reflect.FieldUtils;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.google.common.collect.Lists;
import com.mcgm.MCPartyCore;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;
import org.bukkit.entity.Player;

/**
 *
 * @author Tom
 */
public class Misc {

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    private static Field pingField;

    public static int getPlayerPing(Player player) throws IllegalAccessException {
        BukkitUnwrapper unwrapper = new BukkitUnwrapper();
        Object entity = unwrapper.unwrapItem(player);

        // Next, get the "ping" field
        if (pingField == null) {
            pingField = FuzzyReflection.fromObject(entity).getFieldByName("ping");
        }

        return (Integer) FieldUtils.readField(pingField, entity);
    }

    public static String[] addLinebreaks(String input, int maxLineLength) {
        StringTokenizer tok = new StringTokenizer(input, " ");
        ArrayList<String> stringList = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int lineLen = 0;
        int currentSplit = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            if (lineLen + word.length() > maxLineLength) {
                stringList.add(currentLine.toString());
                currentLine = new StringBuilder();
                lineLen = 0;
            }
            lineLen += word.length();
            currentLine.append(word).append(" ");
        }
        stringList.add(currentLine.toString());
        return stringList.toArray(new String[stringList.size()]);
    }

    public static void refreshPlayer(Player watched) {
        try {
            int view = MCPartyCore.getInstance().getServer().getViewDistance() * 16;
            List<Player> observers = Lists.newArrayList();

            // Get nearby observers
            for (Player observer : getPlayersWithin(watched, view)) {
                if (!observer.equals(watched) && observer.canSee(watched)) {
                    observers.add(observer);
                }
            }

            // Send a new packet
            refreshPlayer(watched, observers);
        } catch (Exception e) {
        }
    }

    /**
     * Refreshes the tag of the watched player for a list of observers.
     *
     * @param watched - the watched player.
     * @param observers - the observers that needs to be refreshed.
     */
    public static void refreshPlayer(Player watched, List<Player> observers) {

        try {
            MCPartyCore.getInstance().getProtocolManager().updateEntity(watched, observers);
        } catch (Exception e) {
        }
    }

    // Find players within a certain number of blocks
    public static List<Player> getPlayersWithin(Player player, int distance) {

        List<Player> res = Lists.newArrayList();
        int d2 = distance * distance;

        for (Player p : MCPartyCore.getInstance().getServer().getOnlinePlayers()) {
            if (p.getWorld() == player.getWorld()
                    && p.getLocation().distanceSquared(player.getLocation()) <= d2) {

                res.add(p);
            }
        }
        return res;
    }

    public static byte degreeToByte(float degree) {
        return (byte) ((int) degree * 256.0F / 360.0F);
    }

    /**
     * Used to delete a folder.
     *
     * @param file The folder to delete.
     * @return true if the folder was successfully deleted.
     */
    public static boolean deleteFolder(File file) {
        if (file.exists()) {
            boolean ret = true;
            // If the file exists, and it has more than one file in it.
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    ret = ret && deleteFolder(f);
                }
            }
            return ret && file.delete();
        } else {
            return false;
        }
    }

    /**
     * Used to delete the contents of a folder, without deleting the folder
     * itself.
     *
     * @param file The folder whose contents to delete.
     * @return true if the contents were successfully deleted
     */
    public static boolean deleteFolderContents(File file) {
        if (file.exists()) {
            boolean ret = true;
            // If the file exists, and it has more than one file in it.
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    ret = ret && deleteFolder(f);
                }
            }
            return ret;
        } else {
            return false;
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

    public static String buildPlayerString(Player[] str, String separator) {
        if (str.length >= 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length; i++) {
                sb.append(str[i].getName());
                if (i != str.length - 1) {
                    sb.append(separator);
                }
            }
            return sb.toString();
        }
        return "";
    }

    /**
     *
     * @param min The (included) lower bound of the range
     * @param max The (included) upper bound of the range
     *
     * @return The random value in the range
     */
    public static double getRandom(double min, double max) {
        return min + (Math.random() * (max - min));
    }

    public static int getRandom(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static int[] toIntArray(List<Integer> integerList) {
        int[] intArray = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            intArray[i] = integerList.get(i);
        }
        return intArray;
    }
    static Random r = new Random();

    public static int getRandomWithExclusion(int start, int end, int... exclude) {
        int random = start + r.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
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
