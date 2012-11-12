
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
@GameInfo(name = "Burning Buckets", aliases = {"BB"}, pvp = false, authors = {"Pt"},
gameTime = 65, description = "Work in teams to build a bridge over the lava with buckets of water, first team to capture the diamonds wins")
public class BurningBuckets extends Minigame {

    Location area = plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlock().getRelative(0, 100, 0).getLocation();
    Location[] spawns = new Location[]{area};

    @Override
    public void minigameTick() {
    }

    @Override
    public void generateGame() {
        Misc.loadArea(new File(Paths.schematicDir.getPath() + "/BurningBuckets.schematic"), new Vector(plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockX(),
                plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockZ()), Misc.MINIGAME_WORLD);
        spawns = Misc.getLocations(new File(Paths.schematicDir.getPath() + "/BurningBuckets.schematic"), new Vector(plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockX(),
                plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockZ()), Misc.MINIGAME_WORLD, Material.DIAMOND_BLOCK);
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            System.out.println(spawns.length);
            Location teleport = spawns[Misc.getRandom(1, spawns.length)];
            p.teleport(teleport);
            
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
            ItemStack bucket = new ItemStack(Material.BUCKET, 1);
            inventory.addItem(bucket);
        }
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void playerDisconnect(Player player) {
    }
}
