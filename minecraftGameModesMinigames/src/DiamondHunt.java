
import com.mcgm.MCPartyCore;
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.WorldUtils;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
@GameInfo(name = "Diamond Hunt", aliases = {"DH"}, pvp = false, authors = {"Pt"},
gameTime = -1, description = "First to find the diamonds wins!", infiniteFood = true)
public class DiamondHunt extends Minigame {

    ArrayList<Location> diamondLocations = new ArrayList<>();
    Location cube = core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlock().getRelative(0, 130, 0).getLocation();
    int diamondOre = 0;
    int dirtBlocks = 0;
    int stoneBlocks = 0;
    int maxDiamonds = Misc.getRandom(4, 8);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.DIAMOND_ORE) {
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
        }
    }

    public void teleportPlayerToSpawn(Player p) {
        Location teleport = new Location(cube.getWorld(), cube.getBlockX() + Misc.getRandom(1, 20), cube.getBlockY() + 1, cube.getBlockZ() + Misc.getRandom(1, 20));
        WorldUtils.teleport(p, teleport);
    }

    @Override
    public void minigameTick() {
        for (Player player : currentlyPlaying) {
            if (player.getLocation().getY() < 130) {
                teleportPlayerToSpawn(player);
                Bukkit.broadcastMessage("Clumsy " + ChatColor.GOLD + player.getName() + ChatColor.WHITE + " fell off the block of dirt didn't he.");
            }
            PlayerInventory inventory = player.getInventory();
            if (inventory.contains(Material.DIAMOND)) {
                Bukkit.broadcastMessage("Player " + ChatColor.GOLD + player.getName().toString() + ChatColor.WHITE + " wins!");
                callPlayerWin(player);
                Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(this, false, player));
            }

            for (Location l : diamondLocations) {
                player.playSound(l, Sound.ORB_PICKUP, 0.1f, 1);
            }
        }
    }

    @Override
    public void generateGame() {

        for (int i = 0; i < 20; i++) {
            for (int x = 0; x < 20; x++) {
                for (int a = 0; a < 20; a++) {
                    Location dirt = new Location(cube.getWorld(), cube.getBlockX() + i, cube.getBlockY() - a, cube.getBlockZ() + x);
                    int rand = Misc.getRandom(0, 700);
                    if (rand > 699 && diamondOre < maxDiamonds) {
                        if (dirt.getBlockY() > cube.getBlockY() - 5) {
                            core.getWorldManager().getMinigameWorld().getBlockAt(dirt).setType(Material.DIRT);
                            dirtBlocks++;
                        } else {
                            core.getWorldManager().getMinigameWorld().getBlockAt(dirt).setType(Material.DIAMOND_ORE);
                            diamondLocations.add(dirt);
                            diamondOre++;
                        }
                    } else if (a > 10 && diamondOre == 0) {
                        core.getWorldManager().getMinigameWorld().getBlockAt(dirt).setType(Material.DIAMOND_ORE);
                        diamondLocations.add(dirt);
                        diamondOre++;
                    } else {
                        if ((dirt.getBlockY() > cube.getBlockY() - 5)) {
                            if (dirt.getBlockY() > cube.getBlockY() - 1) {
                                core.getWorldManager().getMinigameWorld().getBlockAt(dirt).setType(Material.GRASS);
                            } else {
                                core.getWorldManager().getMinigameWorld().getBlockAt(dirt).setType(Material.DIRT);
                                dirtBlocks++;
                            }
                        } else {
                            if (Misc.getRandom(0, 1) == 1) {
                                core.getWorldManager().getMinigameWorld().getBlockAt(dirt).setType(Material.DIRT);
                                dirtBlocks++;
                            } else {
                                core.getWorldManager().getMinigameWorld().getBlockAt(dirt).setType(Material.STONE);
                                stoneBlocks++;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : currentlyPlaying) {
            teleportPlayerToSpawn(p);
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
            ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE, 1);
            ItemStack spade = new ItemStack(Material.DIAMOND_SPADE, 1);
            inventory.addItem(pick, spade);

            p.sendMessage("The hunt has begun!, there are " + ChatColor.AQUA + diamondOre + ChatColor.WHITE + " diamond ores");
            p.sendMessage("They are hiding in " + ChatColor.DARK_RED + dirtBlocks + ChatColor.WHITE + " dirt blocks");
            p.sendMessage("and " + ChatColor.DARK_GRAY + stoneBlocks + ChatColor.WHITE + " stone blocks");
        }
    }

    @Override
    public void onEnd() {
    }
}
