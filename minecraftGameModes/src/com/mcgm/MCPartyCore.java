/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.mcgm.config.MCPartyConfig;
import com.mcgm.game.sign.MinigameSignHandler;
import com.mcgm.game.sign.PlaySignHandler;
import com.mcgm.game.sign.VoteTimeHandler;
import com.mcgm.game.sign.WinnerHandler;
import com.mcgm.manager.CommandManager;
import com.mcgm.manager.FileManager;
import com.mcgm.manager.GameManager;
import com.mcgm.manager.SignManager;
import com.mcgm.manager.WebManager;
import com.mcgm.manager.WorldManager;
import com.mcgm.manager.PlayerManager;
import com.mcgm.player.TagPacketHandler;
import com.mcgm.utils.WorldUtils;
import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

/**
 *
 * @author Tom
 */
public final class MCPartyCore extends JavaPlugin {

    private CommandManager commandManager;
    private WorldManager worldManager;
    private WebManager webManager;
    private GameManager gameManager;
    private DisguiseCraftAPI disguiseCraftAPI;
    private ProtocolManager protocolManager;
    private FileManager fileManager;
    private PlayerManager playerManager;
    private SignManager signManager;
    private static MCPartyCore instance;
    private MinigameSignHandler minigameSignHandler;

    public static MCPartyCore getInstance() {
        synchronized (MCPartyCore.class) {
            return instance;
        }
    }

    public MCPartyCore() {
        synchronized (MCPartyCore.class) {
            instance = this;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        commandManager = new CommandManager(this);
        Command rlConfig = new Command("reloadConfig", "remove player from playing list", "LOBBY", new ArrayList<String>()) {
            @Override
            public boolean execute(CommandSender cs, String string, String[] args) {
                MCPartyConfig.reloadConfig(cs);
                return true;
            }
        };
        commandManager.addCommand(rlConfig);
        fileManager = new FileManager(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        disguiseCraftAPI = DisguiseCraft.getAPI();
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new TagPacketHandler(this, getServer().getPluginManager()));
        worldManager = new WorldManager(this);
        worldManager.loadWorlds(WorldUtils.MAIN_WORLD,
                WorldUtils.MINIGAME_WORLD);
        worldManager.loadedWorlds.get(WorldUtils.MINIGAME_WORLD).setAutoSave(false);
        worldManager.loadedWorlds.get(WorldUtils.MAIN_WORLD).setSpawnLocation(WorldUtils.getMainSpawn().getBlockX(),
                WorldUtils.getMainSpawn().getBlockY(),
                WorldUtils.getMainSpawn().getBlockZ());
        gameManager = new GameManager(this);
        gameManager.loadGameList(null);
        webManager = new WebManager(this);
        playerManager = new PlayerManager(this);
        signManager = new SignManager(this);
        gameManager.loadManager();
        signManager.addSignHandler(minigameSignHandler = new MinigameSignHandler("minigameSigns", "[MCGAME]"));
        signManager.addSignHandler(new PlaySignHandler("playSigns", "[MCPLAY]"));
        signManager.addSignHandler(new VoteTimeHandler("voteSigns", "[MCVOTE]"));
        signManager.addSignHandler(new WinnerHandler("winSigns", "[MCWIN]"));
    }

    @Override
    public void onDisable() {
    }

    public MinigameSignHandler getMinigameSignHandler() {
        return minigameSignHandler;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public DisguiseCraftAPI getDisguiseCraftAPI() {
        return disguiseCraftAPI;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public WebManager getWebManager() {
        return webManager;
    }
}
