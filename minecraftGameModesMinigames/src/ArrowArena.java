
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author Tom
 */
@GameInfo(name = "Arrow Arena", aliases = {"AA"}, pvp = false, authors = {"Tom"}, gameTime = 65, description = "Everyone starts with a bow and arrow and arrows destroy stone! last one to survive wins!")
public class ArrowArena extends Minigame {

    Vector aboveGround;
    List<Player> playersInGame;

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = ((Arrow) event.getEntity());
            if (arrow.getShooter() instanceof Player) {
                Location blockLocation = arrow.getLocation();
                blockLocation.setY(blockLocation.getY() - 1);
                blockLocation.getBlock().setTypeId(00, true);
                arrow.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            if (playersInGame.size() == 1) {
                Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(this, false, playersInGame.get(0)));
            }
            if (playersInGame.contains((Player) event.getEntity())) {
                playersInGame.remove((Player) event.getEntity());
            }
        }
    }

    public ArrowArena(Plugin p, Player... playing) {
        super(p, ArrowArena.class.getAnnotation(GameInfo.class), playing);
        playersInGame = new ArrayList<>();
        aboveGround = new Vector(Misc.getRandom(-500, 500), 110, Misc.getRandom(-500, 500));
    }

    @Override
    public void startGame() {
        for (Player player : playing) {
            Misc.outPrint("Teleporting Players");
            playersInGame.add(player);
            player.teleport(new Location(Misc.getMainWorld(), aboveGround.getX() + Misc.getRandom(-6, 6), 113, aboveGround.getZ() + Misc.getRandom(-6, 6)));
            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemStack arrows = new ItemStack(Material.ARROW, 64);
            inventory.addItem(bow, arrows);
        }
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onCountDown() {
        Misc.loadArea(new File(Paths.schematicDir.getPath() + "/MCEdit.schematic"), aboveGround);
    }

    @Override
    public void onTimeUp() {
        for (Player player : playersInGame) {
            Location ploc = player.getLocation();
            if (ploc.getY() > 105) {
            } else {
                playersInGame.remove(player);
            }
        }
        GameEndEvent ge = new GameEndEvent(this, true, playersInGame.toArray(new Player[playersInGame.size()]));
        Bukkit.getServer().getPluginManager().callEvent(ge);
    }

    @Override
    public void generateGame() {
    }

    @Override
    public void onLeaveArea() {
    }

    @Override
    public void minigameTick() {
    }
}
