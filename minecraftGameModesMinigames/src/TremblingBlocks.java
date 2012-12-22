
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.WorldUtils;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Schematic;
import java.util.ArrayList;
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

@GameInfo(name = "Trembling Blocks", aliases = {"TB"}, pvp = true, authors = {"Pt + Tom"},
gameTime = 125, description = "Stand still and shoot, dont turn back",infiniteFood = true)
public class TremblingBlocks extends Minigame {

    Location area = core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlock().getRelative(0, 100, 0).getLocation();
    ArrayList<Location> spawns = new ArrayList<>();
    public HashMap<Player, Location> LastLocation = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        System.out.println("Player died!");
        callPlayerLose(event.getEntity());
        System.out.println(currentlyPlaying.size());
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
        if (e.getPlayer().getWorld() == core.getWorldManager().getMinigameWorld()) {
            Location playerLoc = e.getPlayer().getLocation();
            Location playerStandingOn = new Location(playerLoc.getWorld(), playerLoc.getBlockX(), playerLoc.getBlockY() - 1, playerLoc.getBlockZ());
            if ((LastLocation.get(e.getPlayer()).getBlock().getX() != playerStandingOn.getBlock().getX())
                    || (LastLocation.get(e.getPlayer()).getBlock().getZ() != playerStandingOn.getBlock().getZ())) {
                LastLocation.get(e.getPlayer()).getBlock().setType(Material.AIR);
                LastLocation.put(e.getPlayer(), playerStandingOn);
            }
            if (playerLoc.getY() < 130) {
                e.getPlayer().setHealth(0);
                WorldUtils.teleport(e.getPlayer(), core.getWorldManager().getMainWorld().getSpawnLocation());
            }
        }
    }

    @Override
    public void minigameTick() {
    }

    @Override
    public void generateGame() {
        Schematic sc = new Schematic("TremblingBlocks");
        spawns = sc.pasteSchematic(core.getWorldManager().getMinigameWorld().getSpawnLocation().add(0, 100, 0), true, Material.REDSTONE_TORCH_ON).get(Material.REDSTONE_TORCH_ON);
    }

    @Override
    public void onTimeUp() {
        for(Player p: currentlyPlaying) {
            p.getInventory().addItem(new ItemStack(Material.BOW),new ItemStack(Material.ARROW,99));
        }
    }

    @Override
    public void startGame() {
        for (Player p : currentlyPlaying) {
            Location teleport = spawns.get(Misc.getRandom(0, spawns.size() - 1));
            WorldUtils.teleport(p, teleport);
            LastLocation.put(p, teleport);
        }

    }

    @Override
    public void onEnd() {
    }

    @Override
    public void playerDisconnect(Player player) {
        if(currentlyPlaying.contains(player)){
            currentlyPlaying.remove(player);
        }
        if (currentlyPlaying.size() <= 1) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(this, false, currentlyPlaying.get(0)));
        }
    }
}
