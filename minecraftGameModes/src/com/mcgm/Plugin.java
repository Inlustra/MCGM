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
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Tom
 */
public final class Plugin extends JavaPlugin {

    private CommandManager commandManager;
    private WorldManager worldManager;
    private GameManager gameManager;
    private PostManager postManager;
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
        worldManager = new WorldManager(this);
        gameManager = new GameManager(this);
        gameManager.loadGameList();
        postManager = new PostManager(this);
        gameManager.loadManager();
        worldManager.loadWorlds(Misc.MAIN_WORLD,
                Misc.MINIGAME_WORLD);
        worldManager.loadedWorlds.get(Misc.MINIGAME_WORLD).setAutoSave(false);
        worldManager.loadedWorlds.get(Misc.MAIN_WORLD).setSpawnLocation(worldManager.getMainSpawn().getBlockX(), 
                worldManager.getMainSpawn().getBlockY(), 
                worldManager.getMainSpawn().getBlockZ());
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
}
