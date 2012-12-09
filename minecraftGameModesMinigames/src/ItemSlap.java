
import com.mcgm.game.Minigame;
import com.mcgm.game.event.GameEndEvent;
import com.mcgm.game.provider.GameInfo;
import com.mcgm.utils.WorldUtils;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
@GameInfo(name = "Item Slap", aliases = {"IS"}, pvp = true, authors = {"Tom"},
gameTime = -1, description = "Much like Super smash brawl, in this game the idea is to knock your opponent off of the map! "
+ "Learn the different item effects!")
public class ItemSlap extends Minigame {
    
    Location[] playerSpawns = new Location[]{};
    Location[] itemSpawns = new Location[]{};
    HashMap<Player, Integer> playerPercent = new HashMap<>();
    HashMap<Player, Integer> playerLives = new HashMap<>();
    HashMap<Player, Player> playerLastHitter = new HashMap<>();
    
    public void performDeath(Player p) {
        int LivesLeft = playerLives.get(p);
        if (LivesLeft == 1) {
            currentlyPlaying.remove(p);
            if (currentlyPlaying.size() == 1) {
                Bukkit.getPluginManager().callEvent(new GameEndEvent(this, false, currentlyPlaying.get(0)));
            }
        } else {
            p.setFallDistance(0f);
            WorldUtils.teleport(p, playerSpawns[Misc.getRandom(0, playerSpawns.length - 1)]);
            LivesLeft--;
            playerPercent.put(p, 100);
            showPlayerPercent(p);
            playerLives.put(p, LivesLeft);
            p.getInventory().clear();
            p.setHealth(20);
            p.setFoodLevel(20);
            p.sendMessage(ChatColor.RED + "You Died, You have " + ChatColor.GREEN + playerLives.get(p) + ChatColor.RED + " Lives left!");
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        e.getPlayer().setWalkSpeed((float) itemDetails(e.getPlayer().getItemInHand().getType())[4]);
    }
    public Material[] usedItems = new Material[]{Material.BAKED_POTATO, Material.GRILLED_PORK,
        Material.APPLE, Material.BREAD,
        Material.GOLDEN_APPLE, Material.GOLDEN_CARROT,
        Material.IRON_SWORD, Material.ANVIL,
        Material.RAW_FISH, Material.FEATHER};
    //0 = HEAL AMOUNT FOR ITEM
    //1 = ITEM DAMAGE
    //2 = KNOCKBACK VALUE
    //3 = KNOCKBACK PROTECTION
    //4 = RUN SPEED
    //5 = SPAWN PROBABILITY

    public double[] itemDetails(Material m) {
        switch (m) {
            //HEALS
            case BAKED_POTATO:
                return new double[]{50, -1, -1, -1, -1, 10};
            case GRILLED_PORK:
                return new double[]{100, -1, -1, -1, -1, 5};
            case APPLE:
                return new double[]{80, -1, -1, -1, -1, 5};
            case BREAD:
                return new double[]{90, -1, -1, -1, -1, 5};
            case GOLDEN_APPLE:
                return new double[]{900, -1, -1, -1, -1, 1};
            case GOLDEN_CARROT:
                return new double[]{900, -1, -1, -1, -1, 1};
            //NonHealing
            case AIR:
                return new double[]{-1, 10, 1, 0, 0.2, 0};
            case IRON_SWORD:
                return new double[]{-1, 25, 1.2, 0, 0.2, 6};
            case ANVIL:
                return new double[]{-1, 100, 0.3, 0.8, 0.05, 2};
            case RAW_FISH:
                return new double[]{-1, 40, 0.6, 0, 0.2, 6};
            case FEATHER:
                return new double[]{-1, 5, 2, -1, 0.3, 2};
            default:
                return null;
        }
    }
    
    public Sound itemSound(Material m) {
        switch (m) {
            //HEALS
            case BAKED_POTATO:
                return Sound.EAT;
            case GRILLED_PORK:
                return Sound.EAT;
            case APPLE:
                return Sound.EAT;
            case BREAD:
                return Sound.EAT;
            case GOLDEN_APPLE:
                return Sound.BURP;
            case GOLDEN_CARROT:
                return Sound.BURP;
            //NonHealing
            case AIR:
                return Sound.HURT;
            case IRON_SWORD:
                return Sound.SKELETON_HURT;
            case ANVIL:
                return Sound.IRONGOLEM_HIT;
            case RAW_FISH:
                return Sound.SLIME_ATTACK;
            case FEATHER:
                return Sound.BREATH;
            default:
                return null;
        }
    }
    
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        boolean canHandle = false;
        for (Material m : usedItems) {
            if (m == e.getItem().getItemStack().getType()) {
                canHandle = true;
                break;
            }
            
        }
        if (canHandle) {
            int heal = (int) itemDetails(e.getItem().getItemStack().getType())[0];
            int pp = playerPercent.get(e.getPlayer());
            boolean toCancel = true;
            if (e.getPlayer().getItemInHand().getType() == Material.AIR && heal < 0) {
                e.getItem().remove();
                e.getPlayer().getInventory().clear();
                e.getPlayer().setWalkSpeed((float) itemDetails(e.getItem().getItemStack().getType())[4]);
                toCancel = false;
            }
            if (heal != -1 && pp > 100) {
                e.getItem().remove();
                sendPlayingMessage("Picked up healing item: " + heal);
                core.getWorldManager().getMinigameWorld().playSound(e.getItem().getLocation(), itemSound(e.getItem().getItemStack().getType()), 1, 1);
                pp = playerPercent.put(e.getPlayer(), pp - heal);
                toCancel = true;
            }
            if (pp < 100) {
                pp = playerPercent.put(e.getPlayer(), 100);
            }
            e.getPlayer().setLevel(pp);
            e.setCancelled(toCancel);
        }
    }
    
    @EventHandler
    public void onPlayerDamageFromEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            event.setCancelled(true);
            Material playerMat = ((Player) event.getDamager()).getItemInHand().getType();
            double knockbackProtection = itemDetails(((Player) event.getEntity()).getItemInHand().getType())[3];
            double totalKnockback = (itemDetails(playerMat)[2] - (knockbackProtection));
            if (totalKnockback < 0) {
                totalKnockback = 0;
            }
            double knockback = 0.1 + (totalKnockback * (playerPercent.get((Player) event.getEntity()) / 100));
            event.getEntity().setVelocity(event.getDamager().getLocation().
                    getDirection().multiply(knockback));
            core.getWorldManager().getMinigameWorld().playSound(event.getDamager().getLocation(), itemSound(((Player) event.getEntity()).getItemInHand().getType()), 1, 1);
            int percent = playerPercent.get((Player) event.getEntity()) + (int) itemDetails(playerMat)[1];
            playerPercent.put((Player) event.getEntity(), percent);
            showPlayerPercent((Player) event.getEntity());
        }
    }
    public ArrayList<Material> itemProbabilityList;
    
    public Material randomSpawn() {
        return itemProbabilityList.get(Misc.getRandom(0, itemProbabilityList.size() - 1));
    }
    
    public void showPlayerPercent(Player m) {
        m.setLevel(playerPercent.get(m));
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity player = event.getEntity();
        if (player instanceof Player) {
            Player p = (Player) player;
            p.setLevel(playerPercent.get(p));
            Integer damage = event.getDamage();
            Integer pHealth = p.getHealth();
            if (pHealth - damage <= 0) {
                performDeath(p);
                event.setCancelled(true);
            }
            
        }
    }
    int timeToItemSpawn = 5;
    
    @Override
    public void minigameTick() {
        for (Player e : currentlyPlaying) {
            if (e.getLocation().getY() < 130) {
                System.out.println("calledVelocity");
                performDeath(e);
            }
        }
        if (itemSpawns.length != 0) {
            if (timeToItemSpawn == 0) {
                Block location = (itemSpawns[Misc.getRandom(0, itemSpawns.length - 1)]).getBlock();
                ItemStack item = new ItemStack(randomSpawn());
                core.getWorldManager().getMinigameWorld().dropItem(location.getRelative(BlockFace.UP).getLocation(), item);
                core.getWorldManager().getMinigameWorld().playSound(location.getLocation(), Sound.ITEM_PICKUP, 1, 1);
                timeToItemSpawn = Misc.getRandom(1, 5);
            }
            timeToItemSpawn--;
        }
    }
    
    @Override
    public void generateGame() {
        WorldUtils.loadArea(new File(Paths.schematicDir.getPath() + "/SkyArenaDrops.schematic"), new Vector(core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockX(),
                core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockZ()), WorldUtils.MINIGAME_WORLD);
        playerSpawns = WorldUtils.getLocations(new File(Paths.schematicDir.getPath() + "/SkyArenaDrops.schematic"), new Vector(core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockX(),
                core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockZ()), WorldUtils.MINIGAME_WORLD, Material.REDSTONE_TORCH_ON);
        itemSpawns = WorldUtils.getLocations(new File(Paths.schematicDir.getPath() + "/SkyArenaDrops.schematic"), new Vector(core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockX(),
                core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockY() + 100,
                core.getWorldManager().getMinigameWorld().getSpawnLocation().getBlockZ()), WorldUtils.MINIGAME_WORLD, Material.REDSTONE_WIRE);
        if (itemProbabilityList == null) {
            itemProbabilityList = new ArrayList<>();
            for (Material m : usedItems) {
                for (int i = 0; i < itemDetails(m)[5]; i++) {
                    itemProbabilityList.add(m);
                }
                
            }
        }
    }
    
    @Override
    public void onTimeUp() {
    }
    
    @Override
    public void startGame() {
        for (Player p : currentlyPlaying) {
            Location teleport = playerSpawns[Misc.getRandom(0, playerSpawns.length - 1)];
            WorldUtils.teleport(p, teleport);
            playerPercent.put(p, 100);
            playerLives.put(p, 10);
            PlayerInventory inventory = p.getInventory();
            inventory.clear();
        }
    }
    
    @Override
    public void onEnd() {
        for (Player p : core.getGameManager().getPlaying()) {
            p.setWalkSpeed(0.2f);
        }
    }
    
    @Override
    public void playerDisconnect(Player player) {
        player.setWalkSpeed(0.2f);
    }
}
