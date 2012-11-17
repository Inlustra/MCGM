/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.google.common.collect.Lists;
import com.mcgm.Plugin;
import com.mcgm.player.teleport.PlayerTeleport;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.entity.Player;

/**
 *
 * @author Thomas
 */
public class WorldUtils {

    public static String MAIN_WORLD = "world";
    public static String MINIGAME_WORLD = "minigameWorld";
    private static final Set<BlockFace> AROUND_BLOCK = EnumSet.noneOf(BlockFace.class);

    static {
        AROUND_BLOCK.add(BlockFace.NORTH);
        AROUND_BLOCK.add(BlockFace.NORTH_EAST);
        AROUND_BLOCK.add(BlockFace.EAST);
        AROUND_BLOCK.add(BlockFace.SOUTH_EAST);
        AROUND_BLOCK.add(BlockFace.SOUTH);
        AROUND_BLOCK.add(BlockFace.SOUTH_WEST);
        AROUND_BLOCK.add(BlockFace.WEST);
        AROUND_BLOCK.add(BlockFace.NORTH_WEST);
    }

    private List<Player> getPlayersWithin(Player player, int distance) {

        List<Player> res = Lists.newArrayList();
        int d2 = distance * distance;

        for (Player p : Plugin.getInstance().getServer().getOnlinePlayers()) {
            if (p.getWorld() == player.getWorld()
                    && p.getLocation().distanceSquared(player.getLocation()) <= d2) {

                res.add(p);
            }
        }
        return res;
    }

    public static Location getMainSpawn() {
        return new Location(Plugin.getInstance().getWorldManager().getMainWorld(), 94, 179, 163);
    }

    public static void teleportSafely(Player p, Location l) {
        Plugin.getInstance().getPlayerManager().teleport(new PlayerTeleport(p, getSafeSpawnAroundABlock(l)));
    }

    public static void teleport(Player p, Location l) {
        Plugin.getInstance().getPlayerManager().teleport(new PlayerTeleport(p, l));
    }

    public static Location getMinigameSpawn() {
        return Plugin.getInstance().getWorldManager().getMinigameWorld().getSpawnLocation();
    }
    /*
     * For my crappy algorithm, radius MUST be odd.
     */

    /**
     * Find a safe spawn around a location. (N,S,E,W,NE,NW,SE,SW)
     *
     * @param l Location to check around
     * @return A safe location, or none if it wasn't found.
     */
    public static Location getSafeSpawnAroundABlock(Location l) {
        if (l != null) {
            if (playerCanSpawnHereSafely(l)) {
                return l;
            }
            Iterator<BlockFace> checkblock = AROUND_BLOCK.iterator();
            while (checkblock.hasNext()) {
                final BlockFace face = checkblock.next();
                if (playerCanSpawnHereSafely(l.getBlock().getRelative(face).getLocation())) {
                    // Don't forget to center the player.
                    return l.getBlock().getRelative(face).getLocation().add(.5, 0, .5);
                }
            }
        } else {
            Misc.outPrintWarning("TELEPORT WAS NULL");
        }
        return null;
    }

    public static boolean playerCanSpawnHereSafely(Location l) {
        if (l == null) {
            // Can't safely spawn at a null location!
            return false;
        }

        World world = l.getWorld();
        Location actual = l.clone();
        Location upOne = l.clone();
        Location downOne = l.clone();
        upOne.setY(upOne.getY() + 1);
        downOne.setY(downOne.getY() - 1);

        if (isSolidBlock(world.getBlockAt(actual).getType())
                || isSolidBlock(upOne.getBlock().getType())) {
            return false;
        }

        if (downOne.getBlock().getType() == Material.LAVA || downOne.getBlock().getType() == Material.STATIONARY_LAVA) {
            return false;
        }

        if (downOne.getBlock().getType() == Material.FIRE) {
            return false;
        }

        if (isBlockAboveAir(actual)) {
            return hasTwoBlocksofWaterBelow(actual);
        }
        return true;
    }

    public static boolean hasTwoBlocksofWaterBelow(Location l) {
        if (l.getBlockY() < 0) {
            return false;
        }
        Location oneBelow = l.clone();
        oneBelow.subtract(0, 1, 0);
        if (oneBelow.getBlock().getType() == Material.WATER || oneBelow.getBlock().getType() == Material.STATIONARY_WATER) {
            Location twoBelow = oneBelow.clone();
            twoBelow.subtract(0, 1, 0);
            return (oneBelow.getBlock().getType() == Material.WATER || oneBelow.getBlock().getType() == Material.STATIONARY_WATER);
        }
        if (oneBelow.getBlock().getType() != Material.AIR) {
            return false;
        }
        return hasTwoBlocksofWaterBelow(oneBelow);
    }

    public static boolean isBlockAboveAir(Location l) {
        Location downOne = l.clone();
        downOne.setY(downOne.getY() - 1);
        return (downOne.getBlock().getType() == Material.AIR);
    }

    public static void removeWater(Location[] l) {
        for (Location loc : l) {
            if (loc.getBlock().getType() == Material.WATER) {
                loc.getBlock().setType(Material.AIR);
            }
        }
    }

    public static Location[] removeBlocksFromArray(Location[] l, Material m) {
        ArrayList<Location> good = new ArrayList<>();
        for (Location loc : l) {
            if (loc.getBlock().getType() != m) {
                good.add(loc);
            }
        }
        return good.toArray(new Location[good.size()]);
    }

    public static void removeWater(Location l, int radius, int height) {
        removeWater(getFilledCylinderAt(l, radius, height));
    }

    private boolean checkAroundSpecificDiameter(Location checkLoc, int circle) {
        // Adjust the circle to get how many blocks to step out.
        // A radius of 3 makes the block step 1
        // A radius of 5 makes the block step 2
        // A radius of 7 makes the block step 3
        // ...
        int adjustedCircle = ((circle - 1) / 2);
        checkLoc.add(adjustedCircle, 0, 0);
        if (playerCanSpawnHereSafely(checkLoc)) {
            return true;
        }
        // Now we go to the right that adjustedCircle many
        for (int i = 0; i < adjustedCircle; i++) {
            checkLoc.add(0, 0, 1);
            if (playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then down adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(-1, 0, 0);
            if (playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then left adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(0, 0, -1);
            if (playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then up Then left adjustedCircle *2
        for (int i = 0; i < adjustedCircle * 2; i++) {
            checkLoc.add(1, 0, 0);
            if (playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }

        // Then finish up by doing adjustedCircle - 1
        for (int i = 0; i < adjustedCircle - 1; i++) {
            checkLoc.add(0, 0, 1);
            if (playerCanSpawnHereSafely(checkLoc)) {
                return true;
            }
        }
        return false;
    }

    private Location checkAroundLocation(Location l, int diameter) {
        if (diameter % 2 == 0) {
            diameter += 1;
        }
        Location checkLoc = l.clone();

        // Start at 3, the min diameter around a block
        int loopcounter = 3;
        while (loopcounter <= diameter) {
            boolean foundSafeArea = checkAroundSpecificDiameter(checkLoc, loopcounter);
            // If a safe area was found:
            if (foundSafeArea) {
                // Return the checkLoc, it is the safe location.
                return checkLoc;
            }
            // Otherwise, let's reset our location
            checkLoc = l.clone();
            // And increment the radius
            loopcounter += 2;
        }
        return null;
    }

    private Location checkAboveAndBelowLocation(Location l, int tolerance, int radius) {
        // Tolerance must be an even number:
        if (tolerance % 2 != 0) {
            tolerance += 1;
        }
        // We want half of it, so we can go up and down
        tolerance /= 2;
        // For now this will just do a straight up block.
        Location locToCheck = l.clone();
        // Check the main level
        Location safe = this.checkAroundLocation(locToCheck, radius);
        if (safe != null) {
            return safe;
        }
        // We've already checked zero right above this.
        int currentLevel = 1;
        while (currentLevel <= tolerance) {
            // Check above
            locToCheck = l.clone();
            locToCheck.add(0, currentLevel, 0);
            safe = this.checkAroundLocation(locToCheck, radius);
            if (safe != null) {
                return safe;
            }

            // Check below
            locToCheck = l.clone();
            locToCheck.subtract(0, currentLevel, 0);
            safe = this.checkAroundLocation(locToCheck, radius);
            if (safe != null) {
                return safe;
            }
            currentLevel++;
        }

        return null;
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

    public static Location[] getRadiusFrom(Location l, int rx, int ry, int rz) {
        ArrayList<Location> list = new ArrayList<>();
        for (int x = -rx; x < rx * 2; x++) {
            for (int y = -ry; y < ry * 2; y++) {
                for (int z = -rz; z < rz * 2; z++) {
                    list.add(new Location(l.getWorld(), l.getX() + x, l.getY() + y, l.getZ() + z));
                }
            }
        }
        return list.toArray(new Location[list.size()]);
    }

    private static double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    private static double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }

    public static Location[] getHollowCylinderAt(Location start, int radius, int height) {
        ArrayList<Location> list = new ArrayList<>();
        for (int y = start.getBlockY(); y < start.getBlockY() + height; y++) {
            int f = 1 - radius;
            int ddF_x = 1;
            int ddF_y = -2 * radius;
            int currentX = 0;
            int currentZ = radius;
            int startX = start.getBlockX();
            int startZ = start.getBlockZ();

            list.add(new Location(start.getWorld(), startX, y, startZ + radius));
            list.add(new Location(start.getWorld(), startX, y, startZ - radius));
            list.add(new Location(start.getWorld(), startX + radius, y, startZ));
            list.add(new Location(start.getWorld(), startX - radius, y, startZ));

            while (currentX < currentZ) {
                // ddF_x == 2 * x + 1;
                // ddF_y == -2 * y;
                // f == x*x + y*y - radius*radius + 2*x - y + 1;
                if (f >= 0) {
                    currentZ--;
                    ddF_y += 2;
                    f += ddF_y;
                }
                currentX++;
                ddF_x += 2;
                f += ddF_x;
                list.add(new Location(start.getWorld(), startX + currentX, y, startZ + currentZ));
                list.add(new Location(start.getWorld(), startX - currentX, y, startZ + currentZ));
                list.add(new Location(start.getWorld(), startX + currentX, y, startZ - currentZ));
                list.add(new Location(start.getWorld(), startX - currentX, y, startZ - currentZ));
                list.add(new Location(start.getWorld(), startX + currentZ, y, startZ + currentX));
                list.add(new Location(start.getWorld(), startX - currentZ, y, startZ + currentX));
                list.add(new Location(start.getWorld(), startX + currentZ, y, startZ - currentX));
                list.add(new Location(start.getWorld(), startX - currentZ, y, startZ - currentX));
            }
        }
        return list.toArray(new Location[list.size()]);
    }

    public static Location[] getFilledCylinderAt(Location loc, int r, int height) {
        ArrayList<Location> list = new ArrayList<>();

        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        World w = loc.getWorld();
        int rSquared = r * r;

        for (int x = cx - r; x <= cx + r; x++) {
            for (int y = cy - height; y <= cy + height; y++) {
                for (int z = cz - r; z <= cz + r; z++) {
                    if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
                        list.add(new Location(w, x, y, z));
                    }
                }
            }
        }
        return list.toArray(new Location[list.size()]);
    }

    public static void setBlocks(Material t, Location... l) {
        for (Location loc : l) {
            loc.getBlock().setType(t);
        }
    }

    public static void setBlockFast(Block b, int typeId, byte data) {
        Chunk c = b.getChunk();
        net.minecraft.server.Chunk chunk = ((CraftChunk) c).getHandle();
        chunk.a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId, data);
    }

    public static void setBlockFast(Location l, int typeId, byte data) {
        setBlockFast(l.getBlock(), typeId, data);
    }

    public static void setBlocksFast(Material m, Location[] lc) {
        for (Location l : lc) {
            setBlockFast(l, m.getId(), (byte) 0);
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

    public Location[] getLocationsOfType(Location center, Material t, int rx, int ry, int rz) {
        ArrayList<Location> list = new ArrayList<>();
        for (int x = -rx; x < rx * 2; x++) {
            for (int y = -ry; y < ry * 2; y++) {
                for (int z = -rz; z < rz * 2; z++) {
                    Location l = new Location(center.getWorld(), center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (l.getBlock().getType() == t) {
                        list.add(l);
                    }
                }
            }
        }
        return list.toArray(new Location[list.size()]);
    }

    /*
     * If someone has a better way of this... Please either tell us, or submit a pull request!
     */
    private static boolean isSolidBlock(Material type) {
        switch (type) {
            case AIR:
                return false;
            case SNOW:
                return false;
            case TRAP_DOOR:
                return false;
            case TORCH:
                return false;
            case YELLOW_FLOWER:
                return false;
            case RED_ROSE:
                return false;
            case RED_MUSHROOM:
                return false;
            case BROWN_MUSHROOM:
                return false;
            case REDSTONE:
                return false;
            case REDSTONE_WIRE:
                return false;
            case RAILS:
                return false;
            case POWERED_RAIL:
                return false;
            case REDSTONE_TORCH_ON:
                return false;
            case REDSTONE_TORCH_OFF:
                return false;
            case DEAD_BUSH:
                return false;
            case SAPLING:
                return false;
            case STONE_BUTTON:
                return false;
            case LEVER:
                return false;
            case LONG_GRASS:
                return false;
            case PORTAL:
                return false;
            case STONE_PLATE:
                return false;
            case WOOD_PLATE:
                return false;
            case SEEDS:
                return false;
            case SUGAR_CANE_BLOCK:
                return false;
            case WALL_SIGN:
                return false;
            case SIGN_POST:
                return false;
            case WOODEN_DOOR:
                return false;
            case STATIONARY_WATER:
                return false;
            case WATER:
                return false;
            default:
                return true;
        }
    }
}
