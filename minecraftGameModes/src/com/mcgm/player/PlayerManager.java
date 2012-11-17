/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.player;

import com.mcgm.Plugin;
import com.mcgm.manager.PostManager;
import com.mcgm.manager.WorldManager;
import com.mcgm.player.teleport.PlayerTeleport;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas
 */
public class PlayerManager {

    private Plugin plugin;
    private final Object teleportLock = new Object();
    private Queue<PlayerTeleport> teleportQueue = new LinkedList<>();

    public PlayerManager(Plugin plugin) {
        this.plugin = plugin;

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    synchronized (teleportLock) {
                        try {
                            while (!teleportQueue.isEmpty()) {
                                teleportQueue.poll().teleport();
                            }
                            teleportLock.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(PostManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }).start();
    }

    public void teleport(PlayerTeleport t) {
        if (t.getL().getWorld() == null) {
            plugin.getWorldManager().loadWorlds(t.getL().getWorld().getName());
        }
        synchronized (teleportLock) {
            teleportQueue.add(t);
            teleportLock.notifyAll();
        }
    }
    
}
