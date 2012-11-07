/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.utils;

import com.mcgm.game.provider.GameClassLoader;
import java.io.File;

/**
 *
 * @author Tom
 */
public class Paths {

    public static final File pluginsDir = new File(new File(GameClassLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent());
    public static final File sourceDir;
    public static final File compiledDir;
    public static final File schematicDir;
    
    static {
        sourceDir = new File(pluginsDir.getPath()+File.separator+"MCGMSources");
        if(!sourceDir.exists()) {
            sourceDir.mkdirs();
        }
        compiledDir = new File(pluginsDir.getPath()+File.separator+"MCGMCompiled");
        if(!compiledDir.exists()) {
            compiledDir.mkdirs();
        }
        schematicDir = new File(pluginsDir.getPath()+File.separator+"MCGMSchematics");
        if(!schematicDir.exists()) {
            schematicDir.mkdirs();
        }
    }
    
}
