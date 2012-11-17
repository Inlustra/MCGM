
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.WorldUtils;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@GameInfo(name = "Trembling Blocks", aliases = {"TB"}, pvp = true, authors = {"Pt + Tom"},
gameTime = 125, description = "Stand still and shoot, dont turn back")
public class TremblingBlocks extends Minigame {

    Location area = plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlock().getRelative(0, 100, 0).getLocation();
    Location[] spawns = new Location[]{area};
    public HashMap<Player, Location> LastLocation = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        System.out.println("Player died!");
        if (playing.contains(event.getEntity())) {
            playing.remove(event.getEntity());
        }
        if (playing.size() == 1) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(this, false, playing.get(0)));
        }
        System.out.println(playing.size());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = ((Arrow) event.getEntity());
            if (arrow.getShooter() instanceof Player) {
                Location explosionLoc = arrow.getLocation();
                arrow.getWorld().createExplosion(explosionLoc, 2);
                arrow.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().getWorld() == plugin.getWorldManager().getMinigameWorld()) {
            Location playerLoc = e.getPlayer().getLocation();
            Location playerStandingOn = new Location(playerLoc.getWorld(), playerLoc.getBlockX(), playerLoc.getBlockY() - 1, playerLoc.getBlockZ());
            if ((LastLocation.get(e.getPlayer()).getBlock().getX() != playerStandingOn.getBlock().getX())
                    || (LastLocation.get(e.getPlayer()).getBlock().getZ() != playerStandingOn.getBlock().getZ())) {
                LastLocation.get(e.getPlayer()).getBlock().setType(Material.AIR);
                LastLocation.put(e.getPlayer(), playerStandingOn);
            }
            if (playerLoc.getY() < 130) {
                e.getPlayer().setHealth(0);
                WorldUtils.teleport(e.getPlayer(), plugin.getWorldManager().getMainWorld().getSpawnLocation());
            }
        }
    }

    @Override
    public void minigameTick() {
    }

    @Override
    public void generateGame() {
        WorldUtils.loadArea(new File(Paths.schematicDir.getPath() + "/SkyArena.schematic"), new Vector(plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockX(),
                plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockZ()), WorldUtils.MINIGAME_WORLD);
        spawns = WorldUtils.getLocations(new File(Paths.schematicDir.getPath() + "/SkyArenaDrops.schematic"), new Vector(plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockX(),
                plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockZ()), WorldUtils.MINIGAME_WORLD, Material.REDSTONE_TORCH_ON);
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            System.out.println(spawns.length);
            Location teleport = spawns[Misc.getRandom(1, spawns.length)];
            WorldUtils.teleport(p,teleport);
            LastLocation.put(p, teleport);
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemStack arrows = new ItemStack(Material.ARROW, 10);
            ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
            inventory.addItem(bow, arrows, sword);
        }

    }

    @Override
    public void onEnd() {
    }

    @Override
    public void playerDisconnect(Player player) {
    }
}
