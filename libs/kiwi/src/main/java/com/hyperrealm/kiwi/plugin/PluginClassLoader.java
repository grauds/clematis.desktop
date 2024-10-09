/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.java.Log;

/**
 * The internal class loader for plugins.
 * <p>
 * The plugin will be loaded in its own namespace. It will have access to all
 * classes in its own namespace, AND access to any class that belongs to any
 * of the _restrictedPackages_ list, BUT NOT access to any class that belongs
 * to any of the _forbiddenPackages_ list.
 * <p>
 * - If asked to load a class that is from a restricted package, the class
 * loader delegates to the system class loader. Packages "java.*' and 'javax.*'
 * are automatically added to the restricted package list. This prevents the
 * plugin from loading classes into those packages, or replacing system classes
 * with its own in its namespace.
 * <p>
 * - If asked to load a class that is from a forbidden package, the class
 * loader throws a ClassNotFoundException.
 * <p>
 * - If asked to load any other class other than those listed above, the class
 * loader attempts to load the class by searching for it in the registered JAR
 * files.
 *
 * @author Mark Lindner
 */
@Log
public class PluginClassLoader extends ClassLoader {

    private static final int PATH_EXTRA_LENGTH = 6;

    private final ArrayList<String> jars;

    private final ArrayList<String> forbiddenPackages;

    private final ArrayList<String> restrictedPackages;

    /*
     */

    PluginClassLoader(ArrayList<String> forbiddenPackages,
                      ArrayList<String> restrictedPackages) {
        this(forbiddenPackages, restrictedPackages, getSystemClassLoader());
    }

    /*
     */

    PluginClassLoader(ArrayList<String> forbiddenPackages,
                      ArrayList<String> restrictedPackages,
                      ClassLoader parent) {
        super(parent);
        this.forbiddenPackages = forbiddenPackages;
        this.restrictedPackages = restrictedPackages;

        jars = new ArrayList<>();
    }


    /*
     */

    private static String classNameToPath(String className) {
        return (className.replace('.', '/') + ".class");
    }

    static String pathToClassName(String path) {
        return (path.substring(0, path.length() - PATH_EXTRA_LENGTH).replace('/', '.'));
    }

    /*
     */

    void addJarFile(String file) {
        synchronized (jars) {
            if ((file != null) && !jars.contains(file)) {
                jars.add(file);
            }
        }
    }

    /**
     *
     */
    @SuppressWarnings({"NestedIfDepth", "ReturnCount", "checkstyle:CyclomaticComplexity"})
    public synchronized Class<?> loadClass(String className, boolean resolve)
        throws ClassNotFoundException {

        Class<?> result;

        // extract package name

        int ind = className.lastIndexOf('.');
        if (ind < 0) {
            throw new ClassNotFoundException("Won't support classes in unnamed packages");
        }

        String classPackage = className.substring(0, ind);

        // first check our cache

        result = findLoadedClass(className);

        // not in cache...

        if (result == null) {

            // try to load it from the system class loader first, but only if it's
            // not from a forbidden package

            if (!isForbiddenPackage(classPackage)) {

                try {
                    return findSystemClass(className);
                } catch (ClassNotFoundException ex) {
                    /* ignore & continue */
                }

                try {
                    if (getParent() != null) {
                        return getParent().loadClass(className);
                    }
                } catch (ClassNotFoundException ex) {
                    /* ignore & continue */
                }

                // no luck, so scan through JAR files looking for the class, but only
                // if it's not a restricted class (or a forbidden class)

                if (!isRestrictedPackage(classPackage)) {
                    String path = classNameToPath(className);

                    for (String jarPath : jars) {

                        try (JarFile jar = new JarFile(new File(jarPath))) {

                            JarEntry entry = (JarEntry) jar.getEntry(path);
                            if (entry == null) {
                                continue; // move on to next JAR file
                            }

                            // found it! so let's load it...

                            int r, size = 0;
                            byte[] b = null;

                            try (BufferedInputStream ins = new BufferedInputStream(jar.getInputStream(entry))){

                                size = (int) entry.getSize();

                                b = new byte[size];
                                r = ins.read(b, 0, size);
                            } catch (IOException ex) {
                                r = -1;
                            }

                            if (r != size) {
                                throw (new ClassFormatError(className));
                            }

                            result = defineClass(className, b, 0, size);
                            break;
                        } catch (IOException ex) {
                            /* ignore error, & continue */
                            log.log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }
        }

        if (result == null) {
            throw new ClassNotFoundException(className);
        }

        if (resolve) {
            resolveClass(result);
        }

        return (result);
    }

    private boolean isForbiddenPackage(String packageName) {
        return findPackage(packageName, forbiddenPackages);
    }

    private boolean isRestrictedPackage(String packageName) {
        return findPackage(packageName, restrictedPackages);
    }

    /**
     * Convert a class name to its corresponding JAR entry name
     */
    private synchronized boolean findPackage(String packageName,
                                             ArrayList<String> packageList) {
        boolean ret = false;

        for (String pkg : packageList) {

            if (pkg.endsWith(".*")) {
                if (packageName.startsWith(pkg.substring(0, pkg.length() - 1))) {
                    ret = true;
                    break;
                }
            } else if (pkg.equals(packageName)) {
                ret = true;
                break;
            }
        }

        return ret;
    }

    @SuppressFBWarnings("OS_OPEN_STREAM")
    public synchronized InputStream getResourceAsStream(String name) {
        /* Scan through JAR files looking for the resource */

        for (String jarPath : jars) {

            try (JarFile jar = new JarFile(new File(jarPath))) {
                JarEntry entry = jar.getJarEntry(name);
                if (entry != null) {
                    try {
                        return (jar.getInputStream(entry));
                    } catch (IOException ex) {
                        /* ignore error, & continue */
                        log.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            } catch (IOException ex) {
                /* ignore error, & continue */
                log.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        return (null);
    }

}
