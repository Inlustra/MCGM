
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;


@GameInfo(name = "Trembling Blocks", aliases = {"TB"}, pvp = true, authors = {"Pt"},
gameTime = 125, description = "Stand still and shoot, dont turn back")
public class TremblingBlocks extends Minigame {
    
    Location area = new Location(Misc.getMainWorld(), Misc.getMainWorld().getSpawnLocation().getBlockX(), Misc.getMainWorld().getSpawnLocation().getBlockY()+100, Misc.getMainWorld().getSpawnLocation().getBlockZ());

    public TremblingBlocks(Plugin p) {
        super(p, TremblingBlocks.class.getAnnotation(GameInfo.class));
    }
    
    public HashMap<Player,Location> LastLocation = new HashMap<>();
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Location playerLoc = e.getPlayer().getLocation();
        Location playerStandingOn = new Location(playerLoc.getWorld(), playerLoc.getBlockX(), playerLoc.getBlockY()-1, playerLoc.getBlockZ());
        if((LastLocation.get(e.getPlayer()).getBlock().getX() != playerStandingOn.getBlock().getX()) || (LastLocation.get(e.getPlayer()).getBlock().getZ() != playerStandingOn.getBlock().getZ())){
            System.out.println("Last Block: " + LastLocation.get(e.getPlayer()).getBlock().toString());
            System.out.println("Current Block: " + playerStandingOn.getBlock().toString());
            LastLocation.get(e.getPlayer()).getBlock().setType(Material.AIR);
            LastLocation.put(e.getPlayer(), playerStandingOn);
        }
    }

    @Override
    public void minigameTick() {
    }

    @Override
    public void onCountDown() {
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for(int i=0; i<100; i++){
            Location spawn = new Location(area.getWorld(),area.getBlockX()+Misc.getRandom(-5, 5),area.getBlockY(),area.getBlockZ()+Misc.getRandom(-5, 5));
            spawn.getBlock().setType(Material.BRICK);
        }
        for(Player p : playing){
            Location teleport = new Location(area.getWorld(), area.getBlockX(), area.getBlockY()+1, area.getBlockZ());
            p.teleport(teleport);
            LastLocation.put(p, teleport);
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
