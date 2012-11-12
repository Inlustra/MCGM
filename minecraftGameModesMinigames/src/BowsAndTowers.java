
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
@GameInfo(name = "Bows and Towers", aliases = {"BAT"}, pvp = false, authors = {"Pt"},
gameTime = -1, description = "desc", seed = "-1793484691")
public class BowsAndTowers extends Minigame {

    Location spawn = plugin.getWorldManager().getMinigameWorld().getSpawnLocation();
    HashMap<Player, Location> playerTowers = new HashMap<>();
    HashMap<Player, Double> playerStartHeight = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().getWorld() == plugin.getWorldManager().getMinigameWorld()) {
            Location playerLoc = e.getPlayer().getLocation();
            
            double distanceNeeded = 100 - playerStartHeight.get(e.getPlayer());
            double blocksMoved = playerLoc.getY() - playerStartHeight.get(e.getPlayer());

            double difference = (blocksMoved/distanceNeeded)*100;
            
            e.getPlayer().setLevel((int)difference);
            if (playerLoc.getY() > 100) {
                Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(this, false, e.getPlayer()));
            }
        }
    }
    int x = 0, y = 0, z = 0, xAvg = 0, yAvg = 0, zAvg = 0;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (playerTowers.containsKey(killer)) {
            playerTowers.put(killer, makeLayer(playerTowers.get(killer).getBlock(), Material.BRICK, true).getLocation());
            makeLayer(playerTowers.get(killer).getBlock(), Material.FENCE, false);
            killer.playSound(killer.getLocation(), Sound.BURP, 1f, 1f);
            Location teleportLocation = new Location(playerTowers.get(killer).getWorld(), playerTowers.get(killer).getX() + 0.5, playerTowers.get(killer).getY(), playerTowers.get(killer).getZ() + 0.5, killer.getLocation().getYaw(), killer.getLocation().getPitch());
            killer.teleport(teleportLocation);

            for (Player p : playing) {
                x += p.getLocation().getX();
                y += p.getLocation().getY();
                z += p.getLocation().getZ();
            }

            xAvg = x / playing.size();
            yAvg = y / playing.size();
            zAvg = z / playing.size();

            x = 0;
            y = 0;
            z = 0;
        }
    }
    int timer = 0;
    
    ArrayList<Entity> oldChickens = new ArrayList<>();
    
    @Override
    public void minigameTick() {
        timer++;
        if (timer == 5) {
            
            for(Entity e : oldChickens){
                e.setFireTicks(500);
            }
            oldChickens.clear();
            
            for (Player p : playing) {
                for (int i = 0; i < playing.size() * 5; i++) {
                    Location mobs = new Location(plugin.getWorldManager().getMinigameWorld(), xAvg + Misc.getRandom(-10, 10), yAvg + Misc.getRandom(5, 15), zAvg - Misc.getRandom(-10, 10));
                    oldChickens.add(this.plugin.getWorldManager().getMinigameWorld().spawnEntity(mobs, EntityType.CHICKEN));
                }
            }
            timer = 0;
        }
    }

    @Override
    public void generateGame() {
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            Location tower = new Location(spawn.getWorld(), spawn.getBlockX() + Misc.getRandom(-10, 10), spawn.getBlockY(), spawn.getBlockZ() + Misc.getRandom(-10, 10));
            playerTowers.put(p, tower);

            Block towerCore = playerTowers.get(p).getBlock();
            Block newTowerCore = makeLayer(towerCore, Material.BRICK, true);
            playerTowers.put(p, newTowerCore.getLocation());

            for (int i = 0; i < 10; i++) {
                playerTowers.put(p, makeLayer(playerTowers.get(p).getBlock(), Material.BRICK, true).getLocation());
                if (i == 9) {
                    makeLayer(playerTowers.get(p).getBlock(), Material.FENCE, false);
                }
            }
            Location teleportLocation = new Location(spawn.getWorld(), towerCore.getX() + 0.5, towerCore.getY(), towerCore.getZ() + 0.5);
            p.teleport(teleportLocation);
            
            playerStartHeight.put(p, (double)teleportLocation.getBlockY());
            p.setLevel(0);

            PlayerInventory inventory = p.getInventory();
            inventory.clear();
            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemStack arrow = new ItemStack(Material.ARROW, 64);
            ItemStack arrow2 = new ItemStack(Material.ARROW, 64);
            ItemStack arrow3 = new ItemStack(Material.ARROW, 64);
            ItemStack arrow4 = new ItemStack(Material.ARROW, 64);
            inventory.addItem(bow, arrow, arrow2, arrow3, arrow4);

            for (int foo = 0; foo < 20; foo++) {
                Location initalChickens = new Location(spawn.getWorld(), spawn.getBlockX() + Misc.getRandom(-10, 10), spawn.getBlockY() + 20, spawn.getBlockZ() + Misc.getRandom(-10, 10));
                plugin.getWorldManager().getMinigameWorld().spawnEntity(initalChickens, EntityType.CHICKEN);
            }

        }
    }

    public Block makeLayer(Block towerCore, Material type, boolean middle) {
        towerCore.setType(middle ? type : Material.AIR);
        towerCore.getRelative(BlockFace.NORTH).setType(type);
        towerCore.getRelative(BlockFace.SOUTH).setType(type);
        towerCore.getRelative(BlockFace.EAST).setType(type);
        towerCore.getRelative(BlockFace.WEST).setType(type);
        towerCore.getRelative(BlockFace.NORTH_EAST).setType(type);
        towerCore.getRelative(BlockFace.NORTH_WEST).setType(type);
        towerCore.getRelative(BlockFace.SOUTH_EAST).setType(type);
        towerCore.getRelative(BlockFace.SOUTH_WEST).setType(type);
        return towerCore.getRelative(BlockFace.UP);
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void playerDisconnect(Player player) {
    }
}
