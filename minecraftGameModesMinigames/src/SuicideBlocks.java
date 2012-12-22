
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.event.PlayerWinEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.WorldUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
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
@GameInfo(name = "Suicide Blocks", aliases = {"SB"}, pvp = true, authors = {"Tom"},
gameTime = 120, description = "Be careful of the tnt!", infiniteFood = true, seed = "bt2")
public class SuicideBlocks extends Minigame {

    int WallWidth = 10;
    Disguise normalTNT = new Disguise(core.getDisguiseCraftAPI().newEntityID(), DisguiseType.FallingBlock);
    Disguise primedTNT = new Disguise(core.getDisguiseCraftAPI().newEntityID(), DisguiseType.TNTPrimed);

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlocksBreaking(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.TNT) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity player = event.getEntity();
        if (player instanceof Player) {
            if ((event.getCause() != DamageCause.ENTITY_EXPLOSION) && (event.getCause() != DamageCause.BLOCK_EXPLOSION)) {
                if (losers.contains((Player) player) || player == chickenSpawner) {
                    detonateCreeper((Player) player, 0L);
                    WorldUtils.teleportSafely((Player) player, core.getWorldManager().getMinigameWorld().getSpawnLocation().add(Misc.getRandom(-15, 15), Misc.getRandom(-15, 15), Misc.getRandom(-15, 15)));
                    event.setCancelled(true);
                    core.getDisguiseCraftAPI().disguisePlayer((Player) player, normalTNT);
                    core.getDisguiseCraftAPI().getDisguise((Player) player).addSingleData("blocklock");
                    return;
                } else {
                    event.setCancelled(true);
                }
            }
            Player p = (Player) player;
            Integer damage = event.getDamage();
            Integer pHealth = p.getHealth();
            if (pHealth - damage <= 0) {
                if (p != chickenSpawner) {
                    losers.add(p);
                    newDisguise(p, true);
                }
                WorldUtils.teleportSafely(p, core.getWorldManager().getMinigameWorld().getSpawnLocation().add(Misc.getRandom(-15, 15), Misc.getRandom(-15, 15), Misc.getRandom(-15, 15)));
                if (losers.size() == currentlyPlaying.size() - 1) {
                    callPlayerWin(chickenSpawner);
                    endGame(chickenSpawner);
                }
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    public void newDisguise(Player p, boolean message) {
        core.getDisguiseCraftAPI().disguisePlayer(p, normalTNT);
        core.getDisguiseCraftAPI().getDisguise(p).addSingleData("blocklock");
        if (message) {
            p.sendMessage("[MCGM] Go forth my minion "
                    + "and use the sneak key to use your new found powers, you must destroy " + (currentlyPlaying.size() - losers.size() - 1) + " people.");
        }
    }
    private int distance = 50;
    private ArrayList<Player> losers = new ArrayList<>();

    @EventHandler
    public void onToggleShift(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            if (losers.contains(e.getPlayer()) || e.getPlayer() == chickenSpawner) {
                core.getDisguiseCraftAPI().changePlayerDisguise((Player) e.getPlayer(), primedTNT);
                core.getDisguiseCraftAPI().getDisguise((Player) e.getPlayer()).addSingleData("blocklock");
                detonateCreeper(e.getPlayer(), 50L);
            }
        }
    }
    HashMap<Player, Integer> intList = new HashMap<>();

    public void detonateCreeper(final Player p, long time) {
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.FIZZ, 1, 1);
        if (!intList.containsKey(p)) {
            intList.put(p, core.getServer().getScheduler().scheduleSyncDelayedTask(core, new Runnable() {
                @Override
                public void run() {
                    p.getLocation().getWorld().createExplosion(p.getLocation(), 3);
                    intList.remove(p);
                }
            }, time));
        }
    }

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
        losers.add(chickenSpawner);
        callPlayerLose(losers.toArray(new Player[losers.size()]));
        Player[] p = currentlyPlaying.toArray(new Player[currentlyPlaying.size()]);
        callPlayerWin(p);
        core.getServer().getPluginManager().callEvent(new GameEndEvent(this, false, p));
    }
    Player chickenSpawner;

    @Override
    public void startGame() {
        normalTNT.setSingleData("blockID:" + Material.TNT.getId());
        chickenSpawner = currentlyPlaying.get(Misc.getRandom(0, currentlyPlaying.size() - 1));
        for (Player p : currentlyPlaying) {
            Location l = core.getWorldManager().getMinigameWorld().getSpawnLocation().add(Misc.getRandom(-15, 15), 10, Misc.getRandom(-15, 15));
            WorldUtils.teleportSafely(p, l);
            if (p != chickenSpawner) {
                p.sendMessage(this.getDescription());
                p.getInventory().addItem(new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 60));
            } else {
                newDisguise(p, true);
            }
        }
        int chickens = currentlyPlaying.size() * 10;
        for (int i = 0; i < chickens; i++) {
            Location spawn = WorldUtils.getSafeSpawnAroundABlock(core.getWorldManager().getMinigameWorld().getSpawnLocation().add(Misc.getRandom(-15, 15), 10, Misc.getRandom(-15, 15)));
            spawn.getBlock().setType(Material.TNT);
        }
    }

    @Override
    public void onEnd() {
    }

    @EventHandler
    public void onBoom(ExplosionPrimeEvent e) {
        if (e.getEntityType() == EntityType.PRIMED_TNT) {
            e.getEntity().getLocation().getBlock().setType(Material.TNT);
            e.setCancelled(true);
        }
    }

    @Override
    public void minigameTick() {
        for (Player p : currentlyPlaying) {
            checkLocation(p);
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
}
