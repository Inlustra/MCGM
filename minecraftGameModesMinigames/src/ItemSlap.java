
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
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
@GameInfo(name = "Item Slap", aliases = {"IS"}, pvp = false, authors = {"Tom"},
gameTime = -1, description = "Much like Super smash brawl, in this game the idea is to knock your opponent off of the map! "
+ "Learn the different item effects!")
public class ItemSlap extends Minigame {

    Location[] playerSpawns;
    Location[] itemSpawns;
    HashMap<Player, Integer> playerPercent = new HashMap<>();
    HashMap<Player, Integer> playerLives = new HashMap<>();
    HashMap<Player, Player> playerLastHitter = new HashMap<>();

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        int heal = (getHeal(e.getItem().getItemStack().getData().getItemType()));
        int pp = playerPercent.get(e.getPlayer());
        if (heal > 0 && pp > 20) {
            e.getItem().remove();
            e.setCancelled(true);
            playerPercent.put(e.getPlayer(), pp - heal);
        }
        e.getPlayer().setLevel(pp);
    }

    @EventHandler
    public void onPlayerDamageFromEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            event.setCancelled(true);
            Material playerMat = ((Player) event.getDamager()).getItemInHand().getType();
            if (playerMat == Material.BAKED_POTATO) {
                event.getEntity().setVelocity(event.getDamager().getLocation().
                        getDirection().multiply(0.1 + (itemDamage(playerMat) * (playerPercent.get((Player) event.getEntity()) / 100))));
            }
        }
    }

    public int getHeal(Material m) {
        return 0;
    }

    public double itemDamage(Material m) {
        return 0;
    }

    public Material randomSpawn() {
        int rand = Misc.getRandom(0, 30);
        switch (rand) {
            case 29:
                return Material.SULPHUR;
            case 28:
            case 27:
                return Material.SNOW_BALL;
            case 26:
            case 25:
            case 24:
            case 23:
            case 22:
                return Material.GLOWSTONE_DUST;
            case 21:
            case 20:
            case 19:
                return Material.FLINT;
            default:
                return Material.BAKED_POTATO;
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity player = event.getEntity();
        if (player instanceof Player) {
            Player p = (Player) player;
            p.setLevel(playerPercent.get(p));

            Integer damage = event.getDamage();
            Integer pHealth = p.getHealth();
            if (pHealth - damage <= 0) {
                int LivesLeft = playerLives.get(p);
                if (LivesLeft == 1) {
                    playing.remove(p);
                } else {
                    event.setCancelled(true);
                    p.teleport(playerSpawns[Misc.getRandom(0, playerSpawns.length - 1)]);
                    playerLives.put(p, LivesLeft -= 1);
                    p.getInventory().clear();
                    p.setHealth(20);
                    p.setFoodLevel(20);
                    p.sendMessage(ChatColor.RED + "You Died, You have " + ChatColor.GREEN + playerLives.get(p) + ChatColor.RED + " Lives left!");
                }
            }

        }
    }
    int timeToItemSpawn = 5;

    @Override
    public void minigameTick() {
        if (itemSpawns.length != 0) {
            if (timeToItemSpawn == 0) {
                Block location = (itemSpawns[Misc.getRandom(0, itemSpawns.length - 1)]).getBlock();
                ItemStack item = new ItemStack(Material.MELON);
                Misc.getMinigameWorld().dropItem(location.getRelative(BlockFace.UP).getLocation(), item);
                timeToItemSpawn = Misc.getRandom(5, 16);
            }
            timeToItemSpawn--;
        }

    }

    @Override
    public void generateGame() {
        Misc.loadArea(new File(Paths.schematicDir.getPath() + "/SkyArena.schematic"), new Vector(Misc.getMinigameWorld().getSpawnLocation().getBlockX(),
                Misc.getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                Misc.getMinigameWorld().getSpawnLocation().getBlockZ()), Misc.MINIGAME_WORLD);
        playerSpawns = Misc.getLocations(new File(Paths.schematicDir.getPath() + "/SkyArenaDrops.schematic"), new Vector(Misc.getMinigameWorld().getSpawnLocation().getBlockX(),
                Misc.getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                Misc.getMinigameWorld().getSpawnLocation().getBlockZ()), Misc.MINIGAME_WORLD, Material.REDSTONE_TORCH_ON);
        itemSpawns = Misc.getLocations(new File(Paths.schematicDir.getPath() + "/SkyArenaDrops.schematic"), new Vector(Misc.getMinigameWorld().getSpawnLocation().getBlockX(),
                Misc.getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                Misc.getMinigameWorld().getSpawnLocation().getBlockZ()), Misc.MINIGAME_WORLD, Material.REDSTONE_WIRE);
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            Location teleport = playerSpawns[Misc.getRandom(0, playerSpawns.length - 1)];
            p.teleport(teleport);
            playerPercent.put(p, 100);
            playerLives.put(p, 10);
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
        }
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void playerDisconnect(Player player) {
    }
}
