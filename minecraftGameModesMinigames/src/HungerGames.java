
import com.mcgm.Plugin;
import com.mcgm.game.Minigame;
import com.mcgm.game.provider.GameInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author Tom
 */
@GameInfo(name = "Hunger Games", aliases = {"HG"}, authors = {"JustTom"}, description = "")
public class HungerGames extends Minigame {

    public HungerGames(Plugin p) {
        super(p, HungerGames.class.getAnnotation(GameInfo.class));
    }

    @Override
    public void startGame() {
        for (Player player : p.getServer().getOnlinePlayers()) {
        }
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onCountDown() {
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void generateGame() {
    }

    @Override
    public void onLeaveArea() {
    }
}
