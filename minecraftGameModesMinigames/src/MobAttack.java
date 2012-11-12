
import com.mcgm.game.Minigame;
import com.mcgm.utils.Misc;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
public class MobAttack extends Minigame {

    @Override
    public void generateGame() {
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            Location teleport = plugin.getWorldManager().getMainSpawn();
            p.teleport(teleport);
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
        }
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void minigameTick() {
    }

    @Override
    public void playerDisconnect(Player player) {
    }
}
