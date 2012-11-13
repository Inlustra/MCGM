/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.mcgm.game.provider.GameClassLoader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tom
 */
public class Paths {

    public static final File pluginsDir = new File(new File(GameClassLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent());
    public static final File MCPartyDir;
    public static final File MCPartyConfig;
    public static final File sourceDir;
    public static final File compiledDir;
    public static final File schematicDir;
    public static final File serverDir;

    static {
        MCPartyDir = new File(pluginsDir.getPath() + File.separator + "MCParty");
        if (!MCPartyDir.exists()) {
            MCPartyDir.mkdirs();
        }
        MCPartyConfig = new File(MCPartyDir.getPath() + File.separator + "config.yml");
        if (!MCPartyDir.exists()) {
            try {
                MCPartyDir.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Paths.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sourceDir = new File(MCPartyDir.getPath() + File.separator + "MCGMSources");
        if (!sourceDir.exists()) {
            sourceDir.mkdirs();
        }
        compiledDir = new File(MCPartyDir.getPath() + File.separator + "MCGMCompiled");
        if (!compiledDir.exists()) {
            compiledDir.mkdirs();
        }
        schematicDir = new File(MCPartyDir.getPath() + File.separator + "MCGMSchematics");
        if (!schematicDir.exists()) {
            schematicDir.mkdirs();
        }
        serverDir = new File(pluginsDir.getParent());
    }
}
