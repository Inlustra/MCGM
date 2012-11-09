
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.sk89q.worldedit.Vector;
import java.util.ArrayList;
import org.bukkit.entity.Player;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
@GameInfo(name = "Default Minigame", aliases = {"DM"}, pvp = false, authors = {"Tom"},
gameTime = 65, description = "desc")
public class DefaultMinigame extends Minigame {

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
