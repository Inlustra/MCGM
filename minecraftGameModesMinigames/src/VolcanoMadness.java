
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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
@GameInfo(name = "Volcano Madness", aliases = {"VM"}, pvp = false, authors = {"Tom"},
gameTime = -1, description = "!")
public class VolcanoMadness extends Minigame {

    @Override
    public void generateGame() {
        Misc.setBlocks(Material.AIR, Misc.getRadiusFrom(plugin.getWorldManager().getMinigameWorld().getSpawnLocation(), 10, 10, 6));
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            p.teleport(plugin.getWorldManager().getMinigameWorld().getSpawnLocation().getBlock().getRelative(BlockFace.NORTH, 2).getLocation());
        }
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void minigameTick() {
        fireBlock();
    }

    @Override
    public void playerDisconnect(Player player) {
    }

    public void fireBlock() {
        FallingBlock block = plugin.getWorldManager().getMinigameWorld().spawnFallingBlock(plugin.getWorldManager().getMinigameWorld().
                getSpawnLocation().getBlock().getRelative(BlockFace.UP, 5).getLocation(), Material.LAVA, (byte) 0);
        float x = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
        float y = (float) -1.3 + (float) (Math.random() * ((5 - -5) + 1));
        float z = (float) -0.3 + (float) (Math.random() * ((0.3 - -0.3) + 1));
        sendPlayingMessage("§c" + x + ", §a" + y + ", §d" + z);
        block.setVelocity(new Vector(x, y, z));
    }
}
