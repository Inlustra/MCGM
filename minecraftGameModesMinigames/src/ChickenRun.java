
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.WorldUtils;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
@GameInfo(name = "Chicken Run", aliases = {"CR"}, pvp = false, authors = {"Tom"},
gameTime = 120, description = "One of you is an exploding chicken, get hit... BECOME A CHICKEN!")
public class ChickenRun extends Minigame {
    
    int WallWidth = 10;
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity player = event.getEntity();
        if (event.getEntityType() == EntityType.CHICKEN) {
            event.setCancelled(true);
            return;
        }
        if (player instanceof Player) {
            if (((Player) player) == chickenSpawner) {
                detonateCreeper((Player) player, 0L);
            }
            Player p = (Player) player;
            Integer damage = event.getDamage();
            Integer pHealth = p.getHealth();
            if (pHealth - damage <= 0) {
                Disguise AlphaDisguise = new Disguise(core.getDisguiseCraftAPI().newEntityID(), DisguiseType.Chicken);
                core.getDisguiseCraftAPI().disguisePlayer(chickenSpawner, AlphaDisguise);
                if (p != chickenSpawner) {
                    losers.add(p);
                }
                WorldUtils.teleportSafely(p, core.getWorldManager().getMinigameWorld().getSpawnLocation().add(Misc.getRandom(-15, 15), Misc.getRandom(-15, 15), Misc.getRandom(-15, 15)));
                if (losers.size() == currentlyPlaying.size() - 1) {
                    core.getServer().getPluginManager().callEvent(new GameEndEvent(this, false, chickenSpawner));
                }
                event.setCancelled(true);
            }
            
        }
    }
    private int distance = 50;
    private ArrayList<Player> losers = new ArrayList<>();
    
    @EventHandler
    public void onToggleShift(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            if (losers.contains(e.getPlayer()) || e.getPlayer() == chickenSpawner) {
                detonateCreeper(e.getPlayer(), 20L);
            }
        }
    }
    
    public void detonateCreeper(final Player p, long time) {
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.FIZZ, 1, 1);
        core.getServer().getScheduler().scheduleSyncDelayedTask(core, new Runnable() {
            @Override
            public void run() {
                p.getLocation().getWorld().createExplosion(p.getLocation(), 5);
            }
        }, time);
    }
    boolean started = false;

    public void checkLocation(Player p) {
        Location loc = p.getLocation();
        if (loc.getX() > loc.getWorld().getSpawnLocation().getX() + distance) {
            loc.setX(loc.getWorld().getSpawnLocation().getX() + distance);
            WorldUtils.teleportSafely(p, loc);
        } else if (loc.getX() < loc.getWorld().getSpawnLocation().getX() - distance) {
            loc.setX(loc.getWorld().getSpawnLocation().getX() - distance);
            WorldUtils.teleportSafely(p, loc);
        }
        if (loc.getZ() > loc.getWorld().getSpawnLocation().getZ() + distance) {
            loc.setZ(loc.getWorld().getSpawnLocation().getZ() - distance);
            WorldUtils.teleportSafely(p, loc);
        } else if (loc.getZ() < loc.getWorld().getSpawnLocation().getZ() - distance) {
            loc.setZ(loc.getWorld().getSpawnLocation().getZ() - distance);
            WorldUtils.teleportSafely(p, loc);
        }
    }
    
    @Override
    public void generateGame() {
    }
    
    @Override
    public void onTimeUp() {
        for (int i = 0; i < losers.size(); i++) {
            if (currentlyPlaying.contains(losers.get(i))) {
                currentlyPlaying.remove(i);
            }
        }
        core.getServer().getPluginManager().callEvent(new GameEndEvent(this, false, currentlyPlaying.toArray(new Player[currentlyPlaying.size()])));
    }
    Player chickenSpawner;
    
    @Override
    public void startGame() {
        started = true;
        chickenSpawner = currentlyPlaying.get(Misc.getRandom(0, currentlyPlaying.size() - 1));
        Disguise AlphaDisguise = new Disguise(core.getDisguiseCraftAPI().newEntityID(), DisguiseType.Chicken);
        core.getDisguiseCraftAPI().disguisePlayer(chickenSpawner, AlphaDisguise);
        for (Player p : currentlyPlaying) {
            WorldUtils.teleport(p, core.getWorldManager().getMinigameWorld().getSpawnLocation().add(Misc.getRandom(-15, 15), 10, Misc.getRandom(-15, 15)));
            if (p != chickenSpawner) {
                p.sendMessage(this.getDescription());
                p.getInventory().addItem(new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 60));
            } else {
                p.sendMessage("YOU ARE THE EXPLODING CHICKEN! Press the sneak key to blow up!");
            }
        }
        int chickens = currentlyPlaying.size() * 10;
        for (int i = 0; i < chickens; i--) {
            Location spawn = WorldUtils.getSafeSpawnAroundABlock(core.getWorldManager().getMinigameWorld().getSpawnLocation().add(Misc.getRandom(-15, 15), Misc.getRandom(-15, 15), Misc.getRandom(-15, 15)));
            core.getWorldManager().getMinigameWorld().spawnEntity(spawn, EntityType.CHICKEN);
        }
    }
    
    @Override
    public void onEnd() {
    }
    
    @Override
    public void minigameTick() {
//        for (Player p : playing) {
//            checkLocation(p);
//        }
    }
    
    @Override
    public void playerDisconnect(Player player) {
    }
}
