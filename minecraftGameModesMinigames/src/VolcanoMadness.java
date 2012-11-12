
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.WorldUtils;
import com.mcgm.utils.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        playing.remove(e.getEntity());
        if (playing.size() <= 1) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(this, false, playing.size() > 0 ? playing.get(0) : null));
        }
    }

    @Override
    public void generateGame() {
        WorldUtils.setBlocks(Material.AIR, WorldUtils.getRadiusFrom(WorldUtils.getMinigameSpawn(), 10, 10, 6));
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            Location l = new Location(plugin.getWorldManager().getMinigameWorld(), WorldUtils.getMinigameSpawn().getX() + Misc.getRandom(-4, 4),
                    WorldUtils.getMinigameSpawn().getY() - 6,
                    WorldUtils.getMinigameSpawn().getZ() + Misc.getRandom(-4, 4));
            WorldUtils.teleportSafely(p,l);
        }
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void minigameTick() {
        fireBlock(WorldUtils.getMinigameSpawn().getBlock().getRelative(BlockFace.UP, 5).getLocation());
    }

    @Override
    public void playerDisconnect(Player player) {
        playing.remove(player);
        if (playing.size() <= 1) {
            Bukkit.getPluginManager().callEvent(new GameEndEvent(this, false, playing.size() > 0 ? playing.get(0) : null));
        }
    }

    public void fireBlock(Location l) {
        FallingBlock block = plugin.getWorldManager().getMinigameWorld().spawnFallingBlock(l, Material.LAVA, (byte) 0);
        float x = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
        float y = (float) -1.3 + Misc.getRandom(0, -1);
        float z = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
        plugin.getWorldManager().getMinigameWorld().playSound(l, org.bukkit.Sound.LAVA_POP, 0.5f, 1);
        block.setVelocity(new Vector(x, y, z));
    }
}
