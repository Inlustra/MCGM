/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.manager;

import com.mcgm.Plugin;
import com.mcgm.config.MCPartyConfig;
import com.mcgm.utils.Paths;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @author Thomas
 */
public final class FileManager {
    
    Plugin plugin;
    
    public FileManager(Plugin p) {
        this.plugin = p;
        if (MCPartyConfig.getBoolean("Development.Monitor")) {
            System.out.println("[MCPARRTAAAYYY] Starting File Monitor");
            startFileMonitor();
        }
    }
    
    public void startFileMonitor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final WatchService watchService = FileSystems.getDefault().newWatchService();
                    final Map<WatchKey, Path> keyMap = new HashMap<>();
                    final Path path = Paths.MCPartyDir.toPath();
                    keyMap.put(path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE), path);
                    WatchKey watchKey;
                    do {
                        watchKey = watchService.take();
                        final Path eventDir = keyMap.get(watchKey);
                        for (final WatchEvent<?> event : watchKey.pollEvents()) {
                            final WatchEvent.Kind kind = event.kind();
                            if (kind == StandardWatchEventKinds.OVERFLOW) {
                                continue;
                            }
                            final Path eventPath = (Path) event.context();
                            if (eventPath.toString().endsWith("yml")) {
                                MCPartyConfig.reloadConfig(Bukkit.getConsoleSender());
                            }
                        }
                    } while (watchKey.reset());
                } catch (InterruptedException | IOException ex) {
                    Logger.getLogger(MCPartyConfig.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
}
