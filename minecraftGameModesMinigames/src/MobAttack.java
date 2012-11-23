
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.WorldUtils;
import com.sk89q.worldedit.MobType;
import org.bukkit.Location;
import org.bukkit.event.player.*;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.PlayerInventory;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
@GameInfo(name = "Mob Attack", aliases = {"MA"}, pvp = true, authors = {"Tom"},
gameTime = -1, description = "BEWARE THE ALPHA MOB!")
public class MobAttack extends Minigame {

    Player AlphaMob;

    @Override
    public void generateGame() {
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            Location teleport = WorldUtils.getMainSpawn();
            WorldUtils.teleport(p,teleport);
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
        }
        AlphaMob = playing.get(Misc.getRandom(0, playing.size() - 1));
        AlphaMob.setWalkSpeed(0.4f);
        AlphaMob.sendMessage(ChatColor.WHITE + "YOU ARE THE " + ChatColor.DARK_PURPLE + " ALPHA MOB" + ChatColor.WHITE + "!");
        Disguise AlphaDisguise = new Disguise(core.getDisguiseCraftAPI().newEntityID(), DisguiseType.Zombie);
        core.getDisguiseCraftAPI().disguisePlayer(AlphaMob, AlphaDisguise);
    }

    public void setPlayerType(MobType e) {
    }
    
    @EventHandler
    public void onToggleShift(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            switch (core.getDisguiseCraftAPI().getDisguise(e.getPlayer()).type) {
                case Zombie:
                    detonateCreeper(e.getPlayer());
                    break;
                case Creeper:
                    detonateCreeper(e.getPlayer());
                    break;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getY() < event.getTo().getY()) {
            event.getPlayer().setVelocity(event.getPlayer().getVelocity().setY(1.5));
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

    public void detonateCreeper(final Player p) {
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.FIZZ, 1, 1);
        core.getServer().getScheduler().scheduleSyncDelayedTask(core, new Runnable() {
            @Override
            public void run() {
                p.getLocation().getWorld().createExplosion(p.getLocation(), 5);
            }
        }, 50L);
    }
}
