
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
@GameInfo(name = "Bows and Towers", aliases = {"BAT"}, pvp = false, authors = {"Pt"},
gameTime = 65, description = "desc")
public class BowsAndTowers extends Minigame {
    
    Location spawn = new Location(Misc.getMinigameWorld(), Misc.getMinigameWorld().getSpawnLocation().getBlockX(), Misc.getMinigameWorld().getSpawnLocation().getBlockY(), Misc.getMinigameWorld().getSpawnLocation().getBlockZ());
    int noOfPlayers = playing.size();
    
    HashMap<Player, Location> playerTowers = new HashMap<>();
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        Player killer = e.getEntity().getKiller();
        Block killerBlock = killer.getLocation().getBlock();
        killerBlock.getRelative(BlockFace.UP).setType(Material.STONE);
    }

    @Override
    public void minigameTick() {
    }
    
    @Override
    public void generateGame() {
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        ArrayList<Location> playerTowersTemp = new ArrayList<>();
        
        for (int i = 0; i < playing.size(); i++) {
            Player p = playing.get(i);
            Location newTower = new Location(spawn.getWorld(), spawn.getBlockX()+Misc.getRandom(1, 5)+i*2, spawn.getBlockY(), spawn.getBlockZ()+Misc.getRandom(1, 5)+i*2);
            if(!playerTowersTemp.contains(newTower)){
            playerTowersTemp.add(newTower);
            playerTowers.put(p, newTower);
            } else {
                i--;
            }
        }
        
        for(Location tower : playerTowersTemp){
           for (int i = 0; i < 3; i++) {
                for (int x = 0; x < 3; x++) {
                    for (int a = 0; a < 130; a++) {
                        
                        Location block = new Location(tower.getWorld(), tower.getBlockX() + i, tower.getBlockY() + a, tower.getBlockZ() - x);
                        block.getBlock().setType(Material.AIR);
            
                    }
                }
            }
            for (int i = 0; i < 3; i++) {
                for (int x = 0; x < 3; x++) {
                    for (int a = 0; a < 10; a++) {
                        
                        Location block = new Location(tower.getWorld(), tower.getBlockX() + i, tower.getBlockY() + a, tower.getBlockZ() - x);
                        block.getBlock().setType(Material.STONE);
            
                    }
                }
            }
        }
        
        for(Player p : playing){
            Location spawnPlayer = new Location(playerTowers.get(p).getWorld(), playerTowers.get(p).getBlockX()+1,playerTowers.get(p).getBlockY()+10,playerTowers.get(p).getBlockZ()-1);
            p.teleport(spawnPlayer);
            p.getLocation().getBlock().getRelative(BlockFace.NORTH).setType(Material.FENCE);
            p.getLocation().getBlock().getRelative(BlockFace.NORTH_EAST).setType(Material.FENCE);
            p.getLocation().getBlock().getRelative(BlockFace.NORTH_WEST).setType(Material.FENCE);
            p.getLocation().getBlock().getRelative(BlockFace.SOUTH).setType(Material.FENCE);
            p.getLocation().getBlock().getRelative(BlockFace.SOUTH_EAST).setType(Material.FENCE);
            p.getLocation().getBlock().getRelative(BlockFace.SOUTH_WEST).setType(Material.FENCE);
            p.getLocation().getBlock().getRelative(BlockFace.EAST).setType(Material.FENCE);
            p.getLocation().getBlock().getRelative(BlockFace.WEST).setType(Material.FENCE);
        }
        
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void playerDisconnect(Player player) {
    }
}
