
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import pgDev.bukkit.DisguiseCraft.Disguise;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

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
            Location teleport = plugin.getWorldManager().getMainSpawn();
            p.teleport(teleport);
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
        }
        AlphaMob = playing.get(Misc.getRandom(0, playing.size()-1));
        AlphaMob.setWalkSpeed(0.4f);
        AlphaMob.sendMessage(ChatColor.WHITE + "YOU ARE THE " + ChatColor.DARK_PURPLE + " ALPHA MOB" + ChatColor.WHITE + "!");
        Disguise AlphaDisguise = new Disguise(plugin.getDisguiseCraftAPI().newEntityID(), Disguise.MobType.Zombie);
        plugin.getDisguiseCraftAPI().disguisePlayer(AlphaMob, AlphaDisguise);
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
