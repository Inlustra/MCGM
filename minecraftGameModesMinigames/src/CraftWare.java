
import com.mcgm.game.Minigame;
import com.mcgm.utils.Misc;
import com.mcgm.utils.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Location;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
public class CraftWare extends Minigame {

    String objective = "";
    String[] objectiveList = {"high"};
    String[] objectiveDesc = {"In 5 seconds, the person at the lowest point on the map loses!"};

    @Override
    public void generateGame() {
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        for (Player p : playing) {
            Location tp = plugin.getWorldManager().getMinigameWorld().getSpawnLocation().add(Misc.getRandom(-10, 10), 0, Misc.getRandom(-10, 10));
            WorldUtils.teleportSafely(p, tp);
        }
        pickNewObjective();
    }

    @Override
    public void onEnd() {
    }
    int time = 0;
    int startedObj = 0;
    @Override
    public void minigameTick() {
        time++;
    }

    @Override
    public void playerDisconnect(Player player) {
    }

    public void pickNewObjective() {
        setTaskId(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int i = 2;

            @Override
            public void run() {
                if (i >= 0) {
                    i--;
                    sendPlayingMessage(i + "...");
                } else {
                    cancel();
                    int obj = Misc.getRandom(0, objectiveList.length - 1);
                    objective = objectiveList[obj];
                    sendPlayingMessage(objectiveDesc[obj]);
                    cancel();
                }
            }
        }, 20L, 20L));
    }
    private int id;

    public void setTaskId(int id) {
        this.id = id;
    }

    private void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
