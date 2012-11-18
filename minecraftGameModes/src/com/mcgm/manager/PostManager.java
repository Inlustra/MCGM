/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.MCPartyCore;
import com.mcgm.web.Post;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @author Thomas
 */
public class PostManager {

    private MCPartyCore plugin;
    private final Object postLock = new Object();
    private Queue<Post> postList;
    private final Object immediateLock = new Object();
    private Queue<Post> immediateList;

    public PostManager(MCPartyCore p) {
        this.plugin = p;
        postList = new LinkedList<>();
        immediateList = new LinkedList<>();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                synchronized (postLock) {
                    while (!postList.isEmpty()) {
                        postList.poll().request();
                    }
                }
            }
        }, 0, 200L);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    synchronized (immediateLock) {
                        try {
                            while (!immediateList.isEmpty()) {
                                immediateList.poll().request();
                            }
                            immediateLock.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(PostManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }).start();
    }

    public void postImmediate(Post p) {
        synchronized (immediateLock) {
            immediateList.add(p);
            immediateLock.notifyAll();
        }
    }

    public void post(Post p) {
        synchronized (postLock) {
            postList.add(p);
        }
    }
}
