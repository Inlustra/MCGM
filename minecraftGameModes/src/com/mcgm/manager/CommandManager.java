/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.Plugin;
import java.lang.reflect.Field;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

/**
 *
 * @author Tom
 */
public class CommandManager {

    CommandMap commandMap = null;

    public CommandManager(Plugin p) {

        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) (field.get(p.getServer().getPluginManager()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void addCommand(Command c) {
        commandMap.register("_", c);
    }

    public void removeCommands() {
        commandMap.clearCommands();
    }
}
