
import com.mcgm.game.Minigame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thomas
 */
public class VolcanoMadness extends Minigame {

    @Override
    public void generateGame() {
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
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

    public void spawnFallingBlock() {
        FallingBlock block = plugin.getWorldManager().getMinigameWorld().spawnFallingBlock(plugin.getWorldManager().getMainSpawn(), Material.LAVA, (byte) 0);
        float x = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
        float y = (float) -5 + (float) (Math.random() * ((5 - -5) + 1));
        float z = (float) -0.3 + (float) (Math.random() * ((0.3 - -0.3) + 1));
        Bukkit.broadcastMessage("§c" + x + ", §a" + y + ", §d" + z);
        block.setVelocity(new Vector(x, y, z));
    }
}
