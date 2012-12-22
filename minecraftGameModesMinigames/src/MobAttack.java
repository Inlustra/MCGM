
import com.mcgm.MCPartyCore;
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.Misc;
import com.mcgm.utils.PlayerUtils;
import com.mcgm.utils.Schematic;
import com.mcgm.utils.WorldUtils;
import com.sk89q.worldedit.MobType;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

/*
 *
 * @author Thomas
 */
@GameInfo(name = "Mob Attack", aliases = {"MA"}, pvp = true, authors = {"Tom"},
gameTime = -1, description = "BEWARE THE ALPHA MOB!", playable = true, infiniteFood = true, seed = "bt2")
public class MobAttack extends Minigame {

    Player AlphaMob;
    ArrayList<Player> losers = new ArrayList<>();
    ArrayList<Player> playersWaiting = new ArrayList<>();
    ArrayList<Location> spawns;
    ArrayList<Location> forge;
    ArrayList<Location> bows;
    Location superItem;
    ArrayList<Location> pentagram;
    ArrayList<Location> waiting;

    @Override
    public void generateGame() {
        Schematic sc = new Schematic("Manor");
        HashMap<Material, ArrayList<Location>> map = sc.pasteSchematic(
                WorldUtils.getSafeSpawnAroundABlock(
                core.getWorldManager().getMinigameWorld().getSpawnLocation().subtract(50, 2, 50)), true, Material.REDSTONE,
                Material.REDSTONE_TORCH_ON, Material.SNOW, Material.DIAMOND, Material.WOOD_PLATE);
        spawns = map.get(Material.REDSTONE_TORCH_ON);
        forge = map.get(Material.SNOW);
        bows = map.get(Material.WOOD_PLATE);
        // superItem = map.get(Material.REDSTONE_WIRE).get(0);
        sc = new Schematic("pentagram");
        map = sc.pasteSchematic(WorldUtils.getSafeSpawnAroundABlock(core.getWorldManager().getMinigameWorld().getSpawnLocation()).add(Misc.getRandom(-80, -90), 0, Misc.getRandom(-80, -90)), false, Material.NETHERRACK);
        pentagram = map.get(Material.NETHERRACK);
        sc = new Schematic("obsidianTomb");
        map = sc.pasteSchematic(core.getWorldManager().getMinigameWorld().getSpawnLocation().subtract(0, 100, 0), true, Material.ENDER_STONE);
        waiting = map.get(Material.ENDER_STONE);
    }

    @Override
    public void onTimeUp() {
    }

    @Override
    public void startGame() {
        AlphaMob = currentlyPlaying.get(Misc.getRandom(0, currentlyPlaying.size() - 1));

        for (Player p : currentlyPlaying) {
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
            if (AlphaMob == p) {
                makePlayerWait(p);
            } else {
                Location teleport = spawns.get(Misc.getRandom(0, spawns.size() - 1));
                WorldUtils.teleport(p, teleport);
            }
        }
        core.getWorldManager().getMainWorld().setStorm(true);
        core.getWorldManager().getMainWorld().setFullTime(13500);
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
            if (playersWaiting.contains(e.getPlayer())) {
                if (e.getPlayer().getItemInHand().getType() != Material.AIR) {
                    Kit k = getKitForItem(e.getPlayer().getItemInHand());
                    if (k.getCost() > 0) {
                        if (core.getPlayerManager().getCredits(e.getPlayer()) >= k.getCost()) {
                            playerKits.put(e.getPlayer(), k);
                            e.getPlayer().sendMessage("You have chosen the: " + k.getName() + " kit.");
                        } else {
                            e.getPlayer().sendMessage("You don't have enough credits, type /credits to see your credits");
                        }
                    } else {
                        playerKits.put(e.getPlayer(), k);
                        e.getPlayer().sendMessage("You have chosen the: " + k.getName() + " kit.");
                    }
                }
            } else {
                if (core.getDisguiseCraftAPI().isDisguised(e.getPlayer())) {
                    switch (core.getDisguiseCraftAPI().getDisguise(e.getPlayer()).type) {
                        case Creeper:
                            detonateCreeper(e.getPlayer(), 20L);
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
    }

    @Override
    public void onEnd() {
    }
    int time = 20;

    @Override
    public void minigameTick() {
        time--;
        if (time == -1) {
            time = 20;
            releaseWave();
        }
    }

    @Override
    public void playerDisconnect(Player player) {
        callPlayerLose(player);
    }
    HashMap<Player, Integer> intList = new HashMap<>();

    public void detonateCreeper(final Player p, long time) {
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.FIZZ, 1, 1);
        if (!intList.containsKey(p)) {
            intList.put(p, core.getServer().getScheduler().scheduleSyncDelayedTask(core, new Runnable() {
                @Override
                public void run() {
                    p.getLocation().getWorld().createExplosion(p.getLocation(), 3);
                    intList.remove(p);
                }
            }, time));
        }
    }

    public void makePlayerWait(Player player) {
        if (!(losers.contains(player) || player == AlphaMob)) {
            losers.add(player);
            if (losers.size() == currentlyPlaying.size() - 1) {
                core.getServer().getPluginManager().callEvent(new GameEndEvent(this, false, AlphaMob));
                return;
            }
        }
        if (player == AlphaMob) {
            player.sendMessage("[MCPARTY] You're the alpha mob, should you not pick a class,"
                    + " You'll be set the AlphaMob class(A little stronger than zombie),"
                    + " Use the sneak key to switch class");
        } else {
            player.sendMessage("[MCPARTY] Scroll through your items and use your"
                    + " shift key to select the kit you'd like to use!");
        }
        PlayerUtils.cleanPlayer(player, false);
        WorldUtils.teleportSafely(player, waiting.get(Misc.getRandom(0, waiting.size())));
        playerKits.remove(player);
        for (Kit k : kitList) {
            player.getInventory().addItem(k.getItemToSwap());
        }
    }

    public void releaseWave() {
        int i = 0;
        for (Player player : playersWaiting) {
            playersWaiting.remove(player);
            i++;
        }
        if (i > 0) {
            sendPlayingMessage("§d[MCPARTY] §9 " + i + (i > 1 ? "mobs have" : "mob has") + " been released!");
        }
    }

    @EventHandler
    public void onPlayerOnPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            if ((losers.contains(p) || p == AlphaMob)) {
                event.setDamage((int) (event.getDamage() * playerKits.get(p).getDamage()));
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.FIRE) {
                if ((losers.contains(player) || player == AlphaMob)) {
                    event.setCancelled(true);
                    player.setFireTicks(0);
                    return;
                }
            }
            if ((event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) && (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                if ((losers.contains(player) || player == AlphaMob)) {
                    if (core.getDisguiseCraftAPI().isDisguised(player)) {
                        if (core.getDisguiseCraftAPI().getDisguise(player).type != DisguiseType.Creeper) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
            Integer damage = event.getDamage();
            Integer pHealth = player.getHealth();
            if (pHealth - damage <= 0) {
                makePlayerWait(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        if (playersWaiting.contains(event.getPlayer())) {
            int itemId = event.getNewSlot();
            try {
                if (event.getPlayer().getInventory().getItem(itemId).getType() != Material.AIR) {
                    if (event.getNewSlot() != -1) {
                        ItemStack iStack = event.getPlayer().getInventory().getItem(itemId);
                        Kit k = getKitForItem(iStack);
                        if (k != null) {
                            event.getPlayer().sendMessage(k.getDefinition());
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }
    HashMap<Player, Kit> playerKits = new HashMap<>();
    private static ArrayList<Kit> kitList = new ArrayList<>();

    public static Kit getKitForItem(ItemStack itemId) {
        for (Kit k : kitList) {
            if (k.getItemToSwap().getTypeId() == itemId.getTypeId()) {
                return k;
            }
        }
        return null;
    }

    private class Kit {

        ItemStack itemToSwap;
        int cost;
        String name;
        String definition;
        DisguiseType disguiseType;
        double damage;
        double runSpeed;

        public Kit(ItemStack swaps, String kitName, String def, int kitCost, DisguiseType is, int RunSpeed, int Damage) {
            itemToSwap = swaps;
            disguiseType = is;
            cost = kitCost;
            definition = def;
            name = kitName;
            damage = Damage;
            runSpeed = RunSpeed;
            kitList.add(this);
        }

        public ItemStack getItemToSwap() {
            return itemToSwap;
        }

        public double getDamage() {
            return damage;
        }

        public double getRunSpeed() {
            return runSpeed;
        }

        public String getName() {
            return name;
        }

        public String getDefinition() {
            return definition;
        }

        public DisguiseType getDisguiseType() {
            return disguiseType;
        }

        public int getCost() {
            return cost;
        }

        public boolean isCorrectSwap(Player p) {
            return p.getItemInHand().getTypeId() == itemToSwap.getTypeId();
        }

        public void setPlayerDisguise(Player p) {
            p.getInventory().clear();
            if (getCost() > 0) {
                if (core.getPlayerManager().getCredits(p) >= getCost()) {
                    core.getPlayerManager().sendChange(p, "credits", -getCost());
                }
            }
            WorldUtils.teleportSafely(p, pentagram.get(Misc.getRandom(0, pentagram.size())));
            Disguise d = new Disguise(core.getDisguiseCraftAPI().newEntityID(), getDisguiseType());
            if (d.type == DisguiseType.FallingBlock) {
                MCPartyCore.getInstance().getDisguiseCraftAPI().getDisguise(p).addSingleData("blocklock");
            }
            MCPartyCore.getInstance().getDisguiseCraftAPI().disguisePlayer(p, d);

        }
    }
}
