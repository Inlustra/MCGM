
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
gameTime = 65, description = "Much like Super smash brawl, in this game the idea is to knock your opponent off of the map! "
+ "Learn the different item effects!")
public class ItemSlap extends Minigame {

    Location[] playerSpawns;
    Location[] itemSpawns;
    HashMap<Player, Integer> playerPercent = new HashMap<>();
    HashMap<Player, Integer> playerLives = new HashMap<>();
    HashMap<Player, Player> playerLastHitter = new HashMap<>();
    
    @EventHandler
    public void onPlayerDamageFromEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            if(((Player)event.getDamager()).getItemInHand().getType() == Material.BAKED_POTATO) {
                event.getEntity().setVelocity(event.getDamager().getLocation().
                        getDirection().multiply
                        (1*(playerPercent.get((Player)event.getEntity())/100)));
            }
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity player = event.getEntity();
        if (player instanceof Player) {
            Player p = (Player) player;
            Integer damage = event.getDamage();
            Integer pHealth = p.getHealth();
            if (pHealth - damage <= 0) {
                int LivesLeft = playerLives.get(p);
                if (LivesLeft == 1) {
                    playing.remove(p);
                } else {
                    event.setCancelled(true);
                    p.teleport(playerSpawns[Misc.getRandom(0, playerSpawns.length-1)]);
                    playerLives.put(p, LivesLeft-=1);
                    p.getInventory().clear();
                    p.setHealth(20);
                    p.setFoodLevel(20);
                    p.sendMessage(ChatColor.RED + "You Died, You have " + ChatColor.GREEN + playerLives.get(p) + ChatColor.RED + " Lives left!");
                }
            }

        }
    }

    @Override
    public void minigameTick() {
    }

    @Override
    public void onCountDown() {
        playerSpawns = Misc.loadArea(new File(Paths.schematicDir.getPath() + "/SkyArena.schematic"), new Vector(Misc.getMinigameWorld().getSpawnLocation().getBlockX(),
                Misc.getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                Misc.getMinigameWorld().getSpawnLocation().getBlockZ()), Misc.MINIGAME_WORLD, Material.REDSTONE_TORCH_ON);
        itemSpawns = Misc.getSpawnPoints(new File(Paths.schematicDir.getPath() + "/SkyArena.schematic"), new Vector(Misc.getMinigameWorld().getSpawnLocation().getBlockX(),
                Misc.getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                Misc.getMinigameWorld().getSpawnLocation().getBlockZ()), Misc.MINIGAME_WORLD, Material.REDSTONE_WIRE);
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            Location teleport = playerSpawns[Misc.getRandom(1, playerSpawns.length)];
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
    public void generateGame() {
    }

    @Override
    public void onLeaveArea() {
    }
}
