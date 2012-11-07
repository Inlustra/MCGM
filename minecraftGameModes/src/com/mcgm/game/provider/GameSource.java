/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.provider;

import com.mcgm.game.provider.GameInfo;
import com.mcgm.manager.GameManager;
import com.mcgm.manager.GameManager;
import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import java.io.File;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tom
 */
public class GameSource {

    File[] files;

    public GameSource(File... files) {
        this.files = files;
    }

    public LinkedList<GameDefinition> list() {
        final LinkedList<GameDefinition> defs = new LinkedList<GameDefinition>();
        for (final File file : files) {
            try {
                GameClassLoader l = new GameClassLoader(file.toURI().toURL(), GameManager.class.getClassLoader());
                list(l, file, defs);
            } catch (MalformedURLException ex) {
                Logger.getLogger(GameSource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return defs;
    }

    private void list(ClassLoader loader, final File file, final LinkedList<GameDefinition> defs) {
        if (file != null) {
            System.out.println(file.getPath());

            if (file.isDirectory()) {
                for (final File item : file.listFiles()) {
                    load(loader, defs, item, "");
                }
            } else if (Misc.isJar(file)) {
//                try {
//                    URL url = file.toURI().toURL();
//                    url = new URL("jar:" + url.toExternalForm() + "!/");
//                    final ClassLoader ldr = new ScriptClassLoader(url);
//                    load(ldr, defs, new JarFile(file));
//                } catch (final IOException ignored) {
//                }
            }
        }
        for (final GameDefinition def : defs) {
            def.source = this;
        }
    }

    private static void load(ClassLoader loader, final LinkedList<GameDefinition> games, final File file, final String prefix) {

        if (file.isDirectory()) {
            if (!file.getName().startsWith(".")) {
                for (final File f : file.listFiles()) {
                    load(loader, games, f, prefix + file.getName() + ".");
                }
            }
        } else {
            String name = prefix + file.getName();
            final String ext = ".class";
            if (name.endsWith(ext) && !name.startsWith(".") && !name.contains("!") && !name.contains("$")) {
                name = name.substring(0, name.length() - ext.length());
                Class<?> clazz;
                try {

                    clazz = ((GameClassLoader) loader).loadClass(name, false);
                } catch (final Exception e) {
                    Misc.outPrintWarning(name + " is not a valid script and was ignored!");
                    e.printStackTrace();
                    return;
                } catch (final VerifyError e) {
                    Misc.outPrintWarning(name + " is not a valid script and was ignored!");
                    return;
                }
                if (clazz.isAnnotationPresent(GameInfo.class)) {
                    final GameDefinition def = new GameDefinition();
                    final GameInfo info = clazz.getAnnotation(GameInfo.class);
                    def.name = info.name();
                    def.aliases = info.aliases();
                    def.authors = info.authors();
                    def.version = info.version();
                    def.description = info.description();
                    def.clazz = clazz;
                    def.maxPlayers = info.maxPlayers();
                    def.teamAmount = info.teamAmount();
                    def.PvP = info.pvp();
                    games.add(def);
                }
            }
        }
    }
}
