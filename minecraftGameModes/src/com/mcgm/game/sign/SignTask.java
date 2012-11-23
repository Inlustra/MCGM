/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.sign;

import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Thomas
 */
public abstract class SignTask {

    public abstract void performTask(PlayerInteractEvent e, Sign s);
}
