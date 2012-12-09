
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.WorldUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
@GameInfo(name = "Hunger Games", aliases = {"HG"}, pvp = false, authors = {"Tom"},
gameTime = -1, description = "SURVIVE!", credits = 100)
public class HungerGames extends Minigame implements Listener {

    private ArrayList<Player> waiting = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        int itemId = event.getNewSlot();
        if (state.equals(PLAYER_GATHERING)) {
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

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        if (e.getBlock().getLocation().getWorld() == WorldUtils.getMinigameSpawn().getWorld()) {
            if (!state.equals(GAME_ON) && !state.equals(PLAYER_GATHERING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        currentlyPlaying.remove(e.getEntity());
        waiting.add(e.getEntity());
        e.getEntity().getInventory().clear();
        if (currentlyPlaying.size() == 1) {
            core.getServer().getPluginManager().callEvent(new GameEndEvent(this,
                    false, currentlyPlaying.toArray(new Player[currentlyPlaying.size()])));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player p = (Player) event.getDamager();
            if (playerChosenKit.get(p).equals(chameleon)) {
                if (event.getEntity() instanceof LivingEntity) {
                    Monster m = (Monster) event.getEntity();
                    p.sendMessage("You are disguised as: " + m.getType().getName());
                    Disguise mobDisguise = new Disguise(core.getDisguiseCraftAPI().newEntityID(), DisguiseType.fromString(m.getType().getName()));
                    core.getDisguiseCraftAPI().disguisePlayer(p, mobDisguise);
                } else if (event.getEntity() instanceof Player) {
                    if (core.getDisguiseCraftAPI().isDisguised((Player) event.getEntity())) {
                        core.getDisguiseCraftAPI().undisguisePlayer((Player) event.getEntity());
                        event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation(), Effect.EXTINGUISH, 31);
                        ((Player) event.getEntity()).sendMessage("You were undisguised!");
                    }
                    if (core.getDisguiseCraftAPI().isDisguised(p)) {
                        core.getDisguiseCraftAPI().undisguisePlayer(p);
                        p.getWorld().playEffect(p.getLocation(), Effect.EXTINGUISH, 31);
                        p.sendMessage("You were undisguised!");
                    }
                }
            }
        }
    }
    Kit archer = new Kit(new ItemStack(Material.BOW), "Archer", "§fThe §2Archer§f kit contains a bow and 10 arrows.", 0,
            new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 10));
    Kit woodCutter = new Kit(new ItemStack(Material.WOOD_AXE), "WoodCutter", "§fThe §2Woodcutter§f kit contains a Stone Axe.", 0,
            new ItemStack(Material.STONE_AXE));
    Kit barbarian = new Kit(new ItemStack(Material.STONE_SWORD), "Barbarian", "§fThe §2Barbarian§f kit contains a Stone Sword", 0,
            new ItemStack(Material.STONE_SWORD));
    Kit miner = new Kit(new ItemStack(Material.STONE_PICKAXE), "Miner", "§fThe §2Miner§f kit contains a Stone Pickaxe", 0,
            new ItemStack(Material.STONE_PICKAXE));
    Kit healer = new Kit(new ItemStack(373, 1, (short) 16389), "Healer", "§fThe §2Healer§f kit contains 5 splash healing potions", 0,
            new ItemStack(373, 3, (short) 16389), new ItemStack(373, 2, (short) 16421));
    Kit wolfMaster = new Kit(new ItemStack(383, 1, (short) 95), "Wolf Master", "§fThe §2Wolf Master§f kit will cost you §450 credits§f but"
            + " contains 2 Wolf eggs and 4 Bones!", 50, new ItemStack(383, 2, (short) 95), new ItemStack(383, 2, (short) 95), new ItemStack(Material.BONE, 4));
    Kit chameleon = new Kit(new ItemStack(Material.LEAVES), "Chameleon", "§fThe §2Chameleon§f kit costs §4100 credits§f and allows "
            + "you to camouflage as a mob when you hit it!", 100);

    public HungerGames() {
        kitList.add(woodCutter);
        kitList.add(barbarian);
        kitList.add(archer);
        kitList.add(healer);
        kitList.add(wolfMaster);
        kitList.add(chameleon);
    }

    @Override
    public void generateGame() {
    }

    @Override
    public void onTimeUp() {
        core.getServer().getPluginManager().callEvent(new GameEndEvent(this,
                true, currentlyPlaying.toArray(new Player[currentlyPlaying.size()])));
    }
    private ArrayList<Kit> kitList = new ArrayList<>();
    private HashMap<Player, Kit> playerChosenKit = new HashMap<>();

    @EventHandler
    public void onToggleShift(PlayerToggleSneakEvent e) {
        if (state.equals(PLAYER_GATHERING)) {
            if (e.isSneaking()) {
                if (e.getPlayer().getItemInHand().getType() != Material.AIR) {
                    Kit k = getKitForItem(e.getPlayer().getItemInHand());
                    if (k.getCost() > 0) {
                        if (core.getPlayerManager().getPlayerProperties(e.getPlayer()).getCredits() >= k.getCost()) {
                            playerChosenKit.put(e.getPlayer(), k);
                            e.getPlayer().sendMessage("You have chosen the: " + k.getName() + " kit.");
                        } else {
                            e.getPlayer().sendMessage("You don't have enough credits, type /credits to see your credits");
                        }
                    } else {
                        playerChosenKit.put(e.getPlayer(), k);
                        e.getPlayer().sendMessage("You have chosen the: " + k.getName() + " kit.");
                    }
                }
            }
        }
    }

    @Override
    public void startGame() {
        respawnAllPlayers(true);
        for (Player p : currentlyPlaying) {
            p.sendMessage("Choose your kit by sneaking!");
            for (Kit k : kitList) {
                p.getInventory().addItem(k.getItemToSwap());
            }
        }
    }

    public Kit getKitForItem(ItemStack itemId) {
        for (Kit k : kitList) {
            if (k.getItemToSwap().getTypeId() == itemId.getTypeId()) {
                return k;
            }
        }
        return null;
    }

    public void respawnAllPlayers(boolean clearinv) {
        for (Player p : currentlyPlaying) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 10));
            Location l = WorldUtils.getMinigameSpawn();
            WorldUtils.teleportSafely(p, l);
            if (clearinv) {
                p.getInventory().clear();
            }
        }
    }

    @Override
    public void onEnd() {
    }
    String state = PLAYER_GATHERING;
    static final String PLAYER_GATHERING = "2";
    static final String GAME_ON = "3";
    private int currentStateTime = 60;
    private int distance = 300;

    public void checkLocation(Player p) {
        Location loc = p.getLocation();
        if (loc.getX() > loc.getWorld().getSpawnLocation().getX() + distance) {
            loc.setX(loc.getWorld().getSpawnLocation().getX() + distance);
            WorldUtils.teleportSafely(p, loc);
        } else if (loc.getX() < loc.getWorld().getSpawnLocation().getX() - distance) {
            loc.setX(loc.getWorld().getSpawnLocation().getX() - distance);
            WorldUtils.teleportSafely(p, loc);
        }
        if (loc.getZ() > loc.getWorld().getSpawnLocation().getZ() + distance) {
            loc.setZ(loc.getWorld().getSpawnLocation().getZ() - distance);
            WorldUtils.teleportSafely(p, loc);
        } else if (loc.getZ() < loc.getWorld().getSpawnLocation().getZ() - distance) {
            loc.setZ(loc.getWorld().getSpawnLocation().getZ() - distance);
            WorldUtils.teleportSafely(p, loc);
        }
    }

    @Override
    public void minigameTick() {
        for (Player p : currentlyPlaying) {
            p.setLevel(currentStateTime);
            checkLocation(p);
        }
        switch (state) {
            case PLAYER_GATHERING:
                if (currentStateTime == 0) {
                    for (Player p : currentlyPlaying) {
                        p.sendMessage("May the odds be ever in your favour!");
                    }
                    for (Player p : currentlyPlaying) {
                        try {
                            playerChosenKit.get(p).givePlayerItems(p);
                        } catch (Exception e) {
                        }
                    }
                    core.getWorldManager().getMinigameWorld().setPVP(true);
                    currentStateTime = 900;
                    state = GAME_ON;
                }
                break;
            case GAME_ON:
                if (currentStateTime == 0) {
                    core.getServer().getPluginManager().callEvent(new GameEndEvent(this, true,
                            currentlyPlaying.toArray(new Player[currentlyPlaying.size()])));
                }
                break;
        }
        currentStateTime--;
    }

    @Override
    public void playerDisconnect(Player player) {
    }

    private class Kit {

        ItemStack itemToSwap;
        int cost;
        String name;
        String definition;
        ItemStack[] itemStacks;

        public Kit(ItemStack swaps, String kitName, String def, int kitCost, ItemStack... is) {
            itemToSwap = swaps;
            itemStacks = is;
            cost = kitCost;
            definition = def;
            name = kitName;
        }

        public ItemStack getItemToSwap() {
            return itemToSwap;
        }

        public String getName() {
            return name;
        }

        public String getDefinition() {
            return definition;
        }

        public ItemStack[] getItemStacks() {
            return itemStacks;
        }

        public int getCost() {
            return cost;
        }

        public boolean isCorrectSwap(Player p) {
            return p.getItemInHand().getTypeId() == itemToSwap.getTypeId();
        }

        public void givePlayerItems(Player p) {
            if (getCredits() > 0) {
                core.getPlayerManager().getPlayerProperties(p).setCredits(core.getPlayerManager().getPlayerProperties(p).getCredits() - getCost());
            }
            p.getInventory().clear();
            p.getInventory().addItem(itemStacks);
        }
    }
}
