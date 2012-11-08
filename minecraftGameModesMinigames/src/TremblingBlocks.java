
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
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
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Location playerLoc = e.getPlayer().getLocation();
        playerLoc.getBlock().setType(Material.AIR);
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
            spawn.getBlock().setType(Material.BAKED_POTATO);
        }
        for(Player p : playing){
            p.teleport(area);
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
