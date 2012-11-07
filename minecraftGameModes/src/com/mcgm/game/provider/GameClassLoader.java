/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcgm.game.provider;

import com.mcgm.utils.Misc;
import com.mcgm.utils.Paths;
import java.awt.AWTPermission;
import java.io.*;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tom
 */
public class GameClassLoader extends ClassLoader {

    private final ProtectionDomain domain;
    private final URL base;

    public GameClassLoader(final URL url, ClassLoader parent) {
        super(parent);
        base = url;
        final CodeSource codeSource = new CodeSource(base, (CodeSigner[]) null);
        domain = new ProtectionDomain(codeSource, getPermissions());
    }

    public void loadGames() {
        for (File f : Paths.compiledDir.listFiles()) {
            try {
                Class c = loadClass(f.getPath());
                Misc.outPrint(c.getName());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GameClassLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Permissions getPermissions() {
        final Permissions ps = new Permissions();
        ps.add(new AWTPermission("accessEventQueue"));
        ps.add(new PropertyPermission("user.home", "read"));
        ps.add(new PropertyPermission("java.vendor", "read"));
        ps.add(new PropertyPermission("java.version", "read"));
        ps.add(new PropertyPermission("os.name", "read"));
        ps.add(new PropertyPermission("os.arch", "read"));
        ps.add(new PropertyPermission("os.version", "read"));
        ps.add(new SocketPermission("*", "resolve"));
        ps.add(new FilePermission(Paths.compiledDir.getPath(), "read,write,delete"));
        ps.setReadOnly();
        return ps;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);

        if (clazz == null) {
            try {
                byte[] bytes = loadClassData(name);
                clazz = defineClass(name, bytes, 0, bytes.length, domain);
                if (resolve) {
                    resolveClass(clazz);
                }
            } catch (final Exception e) {
                clazz = super.loadClass(name, resolve);
            }
        }

        return clazz;
    }

    public byte[] loadClassData(final String name) {
        try {
            final InputStream in = getResourceAsStream(base.getFile() + name.replace('.', '/') + ".class");
            final byte[] buffer = new byte[4096];
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            int n;
            while ((n = in.read(buffer, 0, 4096)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(GameClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    @Override
    public URL getResource(final String name) {
        try {
            return new URL(base, name);
        } catch (final MalformedURLException e) {
            return null;
        }
    }

    @Override
    public InputStream getResourceAsStream(final String name) {
        try {
            return new URL(base, name).openStream();
        } catch (final IOException e) {
            return null;
        }
    }
}
