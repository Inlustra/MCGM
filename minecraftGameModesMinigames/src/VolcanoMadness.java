
import com.mcgm.config.MCPartyConfig;
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.WorldUtils;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

/**
 *
 * @author Thomas
 */
@GameInfo(name = "Volcano Madness", aliases = {"VM"}, pvp = false, authors = {"Tom"},
gameTime = -1, description = "!", teamAmount = -1, infiniteFood = true)
public class VolcanoMadness extends Minigame {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        callPlayerLose(e.getEntity());
    }
    ArrayList<Location> volcanoCenter = new ArrayList<>();
    ArrayList<Location> volcanoSides = new ArrayList<>();
    ArrayList<Location> volcanoFloor = new ArrayList<>();
    int radius = 20;
    int height = 20;

    @Override
    public void generateGame() {
        volcano = WorldUtils.getMinigameSpawn().getBlock().getRelative(BlockFace.DOWN, 2).getLocation();
        volcanoBase = WorldUtils.getMinigameSpawn().getBlock().getRelative(BlockFace.DOWN, 20).getLocation();
        WorldUtils.setBlocksFast(Material.AIR, WorldUtils.getFilledCylinderAt(WorldUtils.getMinigameSpawn(), radius, height));
        WorldUtils.setBlocksFast(Material.BEDROCK, WorldUtils.getFilledCylinderAt(new Location(WorldUtils.getMinigameSpawn().getWorld(),
                WorldUtils.getMinigameSpawn().getBlockX(),
                WorldUtils.getMinigameSpawn().getBlockY() - height - 5,
                WorldUtils.getMinigameSpawn().getBlockZ()), radius + 20, 1));
        WorldUtils.setBlocksFast(Material.BEDROCK, WorldUtils.getHollowCylinderAt(new Location(WorldUtils.getMinigameSpawn().getWorld(),
                WorldUtils.getMinigameSpawn().getBlockX(),
                WorldUtils.getMinigameSpawn().getBlockY() - 30,
                WorldUtils.getMinigameSpawn().getBlockZ()), radius + 2, height + 40));
        Location[] layer1 = WorldUtils.getFilledCylinderAt(new Location(WorldUtils.getMinigameSpawn().getWorld(),
                WorldUtils.getMinigameSpawn().getBlockX(),
                WorldUtils.getMinigameSpawn().getBlockY() - 20,
                WorldUtils.getMinigameSpawn().getBlockZ()), 3, 6);
        WorldUtils.setBlocksFast(Material.OBSIDIAN, layer1);
        Location[] layer2 = WorldUtils.getFilledCylinderAt(new Location(WorldUtils.getMinigameSpawn().getWorld(),
                WorldUtils.getMinigameSpawn().getBlockX(),
                WorldUtils.getMinigameSpawn().getBlockY() - 20,
                WorldUtils.getMinigameSpawn().getBlockZ()), 4, 1);
        WorldUtils.setBlocksFast(Material.OBSIDIAN, layer2);
        Location[] layer3 = WorldUtils.getFilledCylinderAt(new Location(WorldUtils.getMinigameSpawn().getWorld(),
                WorldUtils.getMinigameSpawn().getBlockX(),
                WorldUtils.getMinigameSpawn().getBlockY() - 16,
                WorldUtils.getMinigameSpawn().getBlockZ()), 2, 8);
        WorldUtils.setBlocksFast(Material.OBSIDIAN, layer3);
        volcanoCenter.addAll(Arrays.asList(layer1));
        volcanoCenter.addAll(Arrays.asList(layer2));
        volcanoCenter.addAll(Arrays.asList(layer3));
        volcanoSides.addAll(Arrays.asList(WorldUtils.getHollowCylinderAt(new Location(WorldUtils.getMinigameSpawn().getWorld(),
                WorldUtils.getMinigameSpawn().getBlockX(),
                WorldUtils.getMinigameSpawn().getBlockY() - 30,
                WorldUtils.getMinigameSpawn().getBlockZ()), radius, height)));
        aboveGroundDrops = WorldUtils.getFilledCylinderAt(WorldUtils.getMinigameSpawn().add(0, 10, 0), radius, 1);
        WorldUtils.setBlocksFast(Material.OBSIDIAN, layer2);
        WorldUtils.removeWater(WorldUtils.getMinigameSpawn(), radius + 11, height);
        spawnLoc = WorldUtils.removeBlocksFromArray(WorldUtils.getFilledCylinderAt(WorldUtils.getMinigameSpawn().subtract(0, 16, 0), radius, 1), Material.OBSIDIAN);
    }
    Location[] aboveGroundDrops = null;
    Location[] spawnLoc = null;

    @Override
    public void onTimeUp() {
    }

    public void breakRandomVolcanoBlock(int amt) {
        for (int i = 0; i < amt; i++) {
            if (!volcanoCenter.isEmpty()) {
                Location l = volcanoCenter.get(Misc.getRandom(0, volcanoCenter.size() - 1));
                volcanoCenter.remove(l);
                l.getWorld().createExplosion(l, 4);
                l.getBlock().setType(Material.LAVA);
            }
        }
    }

    public void breakRandomSidesBlock(int amt) {
        for (int i = 0; i < amt; i++) {
            if (!volcanoSides.isEmpty()) {
                Location l = volcanoSides.get(Misc.getRandom(0, volcanoSides.size() - 1));
                volcanoSides.remove(l);
                l.getWorld().createExplosion(l, 5);
                l.getBlock().setType(Material.LAVA);
            }
        }
    }

    public void breakRandomFloorBlock(int amt) {
        for (int i = 0; i < amt; i++) {
            Location l = spawnLoc[Misc.getRandom(0, spawnLoc.length - 1)];
            l.getWorld().createExplosion(l, 5);
            l.getBlock().setType(Material.LAVA);

        }
    }

    @Override
    public void startGame() {
        for (Player p : currentlyPlaying) {
            WorldUtils.teleport(p, spawnLoc[Misc.getRandom(0, spawnLoc.length)]);
        }
        hasStarted = true;
    }

    @Override
    public void onEnd() {
    }
    public int severityTick = 0;
    public int severity = 0;
    boolean hasStarted = false;
    Location volcano = WorldUtils.getMinigameSpawn().getBlock().getRelative(BlockFace.DOWN, 2).getLocation();
    Location volcanoBase = WorldUtils.getMinigameSpawn().getBlock().getRelative(BlockFace.DOWN, 20).getLocation();

    @Override
    public void minigameTick() {
        if (hasStarted) {
            severityTick++;
            if (severityTick == 10) {
                severityTick = 0;
                severity++;
                switch (severity) {
                    case 1:
                        MCPartyConfig.sendMessage(currentlyPlaying.toArray(new Player[currentlyPlaying.size()]), "VolcanoMadness.severity" + severity);
                        break;
                    case 2:
                        breakRandomVolcanoBlock(2);
                        break;
                    case 4:
                        MCPartyConfig.sendMessage(currentlyPlaying.toArray(new Player[currentlyPlaying.size()]), "VolcanoMadness.severity" + severity);
                        break;
                    case 5:
                        breakRandomSidesBlock(3);
                        break;
                    case 8:
                        MCPartyConfig.sendMessage(currentlyPlaying.toArray(new Player[currentlyPlaying.size()]), "VolcanoMadness.severity" + severity);
                        break;
                    case 12:
                        MCPartyConfig.sendMessage(currentlyPlaying.toArray(new Player[currentlyPlaying.size()]), "VolcanoMadness.severity" + severity);
                        break;
                    case 15:
                        MCPartyConfig.sendMessage(currentlyPlaying.toArray(new Player[currentlyPlaying.size()]), "VolcanoMadness.severity" + severity);
                        break;
                    case 16:
                        Bukkit.getPluginManager().callEvent(new GameEndEvent(this, true, currentlyPlaying.toArray(new Player[currentlyPlaying.size()])));
                        break;
                    default:
                        breakRandomVolcanoBlock(2);
                        break;
                }
            }
            switch (severity) {
                case 0:
                case 1:
                    fireBlock(volcano, Material.LAVA);
                    core.getWorldManager().getMinigameWorld().playSound(volcanoBase, org.bukkit.Sound.LAVA_POP, 0.5f, 1);
                    break;
                case 2:
                    fireBlock(volcano, Material.LAVA, 2);
                    core.getWorldManager().getMinigameWorld().playSound(volcanoBase, org.bukkit.Sound.LAVA_POP, 1f, 1);
                    break;
                case 3:
                    fireBlock(volcano, Material.LAVA, 3);
                    core.getWorldManager().getMinigameWorld().playSound(volcanoBase, org.bukkit.Sound.LAVA, 1f, 1);
                    break;
                case 4:
                    fireBlock(volcano, Material.LAVA, 4);
                    core.getWorldManager().getMinigameWorld().playSound(volcanoBase, org.bukkit.Sound.LAVA, 2f, 1);
                    break;
                case 5:
                    fireBlock(volcano, Material.LAVA, 4);
                    core.getWorldManager().getMinigameWorld().playSound(volcanoBase, org.bukkit.Sound.EXPLODE, 0.5f, 1);
                    break;
                case 6:
                    fireBlock(volcano, Material.LAVA, 5);
                    core.getWorldManager().getMinigameWorld().playSound(volcanoBase, org.bukkit.Sound.EXPLODE, 1f, 1);
                    break;
                case 7:
                    fireBlock(volcano, Material.LAVA, 5);
                    core.getWorldManager().getMinigameWorld().playSound(volcanoBase, org.bukkit.Sound.EXPLODE, 1f, 1);
                    break;
                case 8:
                    fireBlock(volcano, Material.LAVA, 5);
                    breakRandomSidesBlock(2);
                    break;
                case 9:
                    breakRandomSidesBlock(2);
                    break;
                case 10:
                    breakRandomSidesBlock(4);
                    break;
                case 11:
                case 12:
                case 13:
                    breakRandomSidesBlock(4);
                    breakRandomFloorBlock(5);
                case 14:
                case 15:
                    breakRandomSidesBlock(6);
                    breakRandomFloorBlock(10);
                default:
                    breakRandomSidesBlock(6);
                    breakRandomFloorBlock(10);
                    break;
            }
        }
    }

    @Override
    public void playerDisconnect(Player player) {
        if (currentlyPlaying.contains(player)) {
            currentlyPlaying.remove(player);
        }
        if (currentlyPlaying.size() <= 1) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(this, false, currentlyPlaying.get(0)));
        }
    }

    public void dropBlocks(Location[] l, Material m) {
        for (Location loc : l) {
            core.getWorldManager().getMinigameWorld().spawnFallingBlock(loc, m, (byte) 0);
        }
    }

    public void fireBlock(Location l, Material m) {
        FallingBlock block = core.getWorldManager().getMinigameWorld().spawnFallingBlock(l, m, (byte) 0);
        float maxx = MCPartyConfig.getFloat("VolcanoMadness.fireMaxX");
        float maxy = MCPartyConfig.getFloat("VolcanoMadness.fireMaxY");
        float maxz = MCPartyConfig.getFloat("VolcanoMadness.fireMaxZ");
        float minx = MCPartyConfig.getFloat("VolcanoMadness.fireMinX");
        float miny = MCPartyConfig.getFloat("VolcanoMadness.fireMinY");
        float minz = MCPartyConfig.getFloat("VolcanoMadness.fireMinZ");
        Vector v = new Vector(Misc.getRandom(minx, maxx), Misc.getRandom(miny, maxy), Misc.getRandom(minz, maxz));
        block.setVelocity(v);
    }

    public void fireBlock(Location l, Material m, int amt) {
        for (int i = 0; i < amt; i++) {
            fireBlock(l, m);
        }
    }
}
