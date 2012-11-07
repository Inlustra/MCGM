/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm;

import com.mcgm.manager.CommandManager;
import com.mcgm.manager.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Tom
 */
public final class Plugin extends JavaPlugin {
    
    private CommandManager commandManager;
    
    @Override
    public void onLoad() {
        super.onLoad();
        commandManager = new CommandManager(this);
        GameManager.getInstance(this).loadGameList();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        GameManager.getInstance(this).loadManager();
        Thread.setDefaultUncaughtExceptionHandler(GameManager.getInstance(this));
    }
    
    @Override
    public void onDisable() {
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
}
