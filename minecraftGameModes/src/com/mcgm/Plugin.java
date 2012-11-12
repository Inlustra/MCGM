/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm;

import com.mcgm.manager.CommandManager;
import com.mcgm.manager.GameManager;
import com.mcgm.manager.PostManager;
import com.mcgm.manager.WorldManager;
import com.mcgm.utils.Misc;
import com.mcgm.utils.WorldUtils;
import org.bukkit.plugin.java.JavaPlugin;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

/**
 *
 * @author Tom
 */
public final class Plugin extends JavaPlugin {

    private CommandManager commandManager;
    private WorldManager worldManager;
    private GameManager gameManager;
    private PostManager postManager;
    private DisguiseCraftAPI dcAPI;

    public void setupDisguiseCraft() {
        dcAPI = DisguiseCraft.getAPI();
    }
    private static Plugin instance;

    public static Plugin getInstance() {
        synchronized (Plugin.class) {
            return instance;
        }
    }

    public Plugin() {
        synchronized (Plugin.class) {
            instance = this;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        commandManager = new CommandManager(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        setupDisguiseCraft();
        worldManager = new WorldManager(this);
        gameManager = new GameManager(this);
        gameManager.loadGameList();
        postManager = new PostManager(this);
        gameManager.loadManager();
        worldManager.loadWorlds(WorldUtils.MAIN_WORLD,
                WorldUtils.MINIGAME_WORLD);
        worldManager.loadedWorlds.get(WorldUtils.MINIGAME_WORLD).setAutoSave(false);
        worldManager.loadedWorlds.get(WorldUtils.MAIN_WORLD).setSpawnLocation(WorldUtils.getMainSpawn().getBlockX(),
                WorldUtils.getMainSpawn().getBlockY(),
                WorldUtils.getMainSpawn().getBlockZ());
    }

    @Override
    public void onDisable() {
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

    public PostManager getPostManager() {
        return postManager;
    }

    public DisguiseCraftAPI getDisguiseCraftAPI() {
        return dcAPI;
    }
    
}
