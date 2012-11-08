
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
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
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@GameInfo(name = "Trembling Blocks", aliases = {"TB"}, pvp = true, authors = {"Pt"},
gameTime = 125, description = "Stand still and shoot, dont turn back")
public class TremblingBlocks extends Minigame {

    Location area = new Location(Misc.getMinigameWorld(), Misc.getMinigameWorld().getSpawnLocation().getBlockX(),
            Misc.getMinigameWorld().getSpawnLocation().getBlockY() + 100,
            Misc.getMinigameWorld().getSpawnLocation().getBlockZ());

    public TremblingBlocks(Plugin p) {
        super(p, TremblingBlocks.class.getAnnotation(GameInfo.class));
    }
    public HashMap<Player, Location> LastLocation = new HashMap<>();

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
        if(e.getPlayer().getWorld() == Misc.getMinigameWorld()){
            Location playerLoc = e.getPlayer().getLocation();
            Location playerStandingOn = new Location(playerLoc.getWorld(), playerLoc.getBlockX(), playerLoc.getBlockY() - 1, playerLoc.getBlockZ());
            if ((LastLocation.get(e.getPlayer()).getBlock().getX() != playerStandingOn.getBlock().getX())
                    || (LastLocation.get(e.getPlayer()).getBlock().getZ() != playerStandingOn.getBlock().getZ())) {
                LastLocation.get(e.getPlayer()).getBlock().setType(Material.AIR);
                LastLocation.put(e.getPlayer(), playerStandingOn);
            }
            if (playerLoc.getY() < 130) {
                e.getPlayer().setHealth(0);
                e.getPlayer().teleport(Misc.getMainWorld().getSpawnLocation());
            }
        }
    }

    @Override
    public void minigameTick() {
    }

    @Override
    public void onCountDown() {
        Misc.loadArea(new File(Paths.schematicDir.getPath() + "/SkyArena.schematic"), new Vector(Misc.getMinigameWorld().getSpawnLocation().getBlockX(),
                Misc.getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                Misc.getMinigameWorld().getSpawnLocation().getBlockZ()), Misc.MINIGAME_WORLD);
    }

    @Override
    public void onTimeUp() {
    }
    @Override
    public void startGame() {
        for (int i = 0; i < 100; i++) {
            Location spawn = new Location(area.getWorld(), area.getBlockX() + Misc.getRandom(-5, 5), area.getBlockY(), area.getBlockZ() + Misc.getRandom(-5, 5));
            spawn.getBlock().setType(Material.BRICK);
        }
        for (Player p : playing) {
            Location teleport = new Location(area.getWorld(), area.getBlockX(), area.getBlockY() + 1, area.getBlockZ());
            p.teleport(teleport);
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
    public void generateGame() {
    }

    @Override
    public void onLeaveArea() {
    }
}
