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

package com.hyperrealm.kiwi.util.plugin;

import java.io.File;
import java.util.ArrayList;

import com.hyperrealm.kiwi.util.ResourceDecoder;

/**
 * A utility class for locating plugins in JAR files. This class is the
 * heart of the Kiwi plugin API.
 * <p>
 * A plugin consists of one or more classes that implement the plugin itself
 * (with one of those classes being the main or entry-point class). These
 * classes are stored in a JAR file. The JAR file Manifest must contain an
 * entry for the plugin. This entry identifies the plugin entry-point class
 * and other information about the plugin. Here is an example entry:
 * <p>
 * <pre>
 * Name: com/foo/imaging/BlurFilter.class
 * PluginName: Blur Filter
 * PluginType: Image Filter
 * PluginIcon: com/foo/imaging/icons/BlurFilter.gif
 * PluginHelpURL: http://dystance.net/filters/blur.html
 * PluginVersion: 1.1.2
 * </pre>
 * <p>
 * The <code>Name</code> field identifies the file in the JAR that is the
 * plugin entry-point class. The <code>PluginName</code> field provides a
 * textual name for the plugin, and the <code>PluginType</code> field
 * identifies the type of the plugin (the meaning of which is
 * application-dependent). These fields must be present in the entry, whereas
 * the remaining fields are optional.
 * <p>
 * The <code>PluginDescription</code> field provides a brief description of the
 * plugin (and may optionally include a copyright message or other related
 * information). <code>PluginIcon</code> identifies an icon image for the
 * plugin. Finally, <code>PluginVersion</code> specifies the version number of
 * the plugin.
 * <p>
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 1.3
 */

public class PluginLocator<T> {

    private final ArrayList<String> forbiddenPackages;

    private final ArrayList<String> restrictedPackages;

    private PluginContext context;

    private ResourceDecoder decoder;

    /**
     * Construct a new <code>PluginLocator</code> with the specified plugin
     * context.
     *
     * @param context The <code>PluginContext</code> for this plugin locator.
     */

    public PluginLocator(PluginContext context) {

        decoder = new ResourceDecoder();

        this.context = context;

        restrictedPackages = new ArrayList<String>();
        forbiddenPackages = new ArrayList<String>();

        addRestrictedPackage("java.*");
        addRestrictedPackage("javax.*");
        addRestrictedPackage("com.hyperrealm.kiwi.*");
    }

    /**
     * Add a package to the locator's list of restricted packages. Plugins are
     * allowed to access classes in restricted packages, but they are not allowed
     * to declare classes that belong to those packages.
     *
     * @param pkg The package name.
     */

    public void addRestrictedPackage(String pkg) {
        synchronized (restrictedPackages) {
            if (!restrictedPackages.contains(pkg)) {
                restrictedPackages.add(pkg);
            }
        }
    }

    /**
     * Add a package to the locator's list of forbidden packages. Plugins are
     * not allowed to access classes in forbidden packages, nor are they allowed
     * to declare classes that belong to those packages.
     *
     * @param pkg The package name.
     */

    public void addForbiddenPackage(String pkg) {
        synchronized (forbiddenPackages) {
            if (!forbiddenPackages.contains(pkg)) {
                forbiddenPackages.add(pkg);
            }
        }
    }

    /**
     * Create a Plugin object for the given plugin archive.
     *
     * @param jarFile The plugin archive.
     * @return The <code>Plugin</code>, if successfully created, or
     * <code>null</code> otherwise.
     */

    private Plugin<T> loadPlugin(String jarFile, String type)
        throws PluginException {

        return new Plugin<T>(this, jarFile, type);
    }

    /**
     * Create a Plugin object for the given plugin archive.
     *
     * @param jarFile The plugin archive.
     * @return The <code>Plugin</code>, if successfully created, or
     * <code>null</code> otherwise.
     */

    public Plugin<T> loadPlugin(File jarFile, String type) throws PluginException {
        return loadPlugin(jarFile.getAbsolutePath(), type);
    }

    /*
     */

    PluginContext getContext() {
        return context;
    }

    /*
     */

    ResourceDecoder getDecoder() {
        return decoder;
    }

    /*
     */

    PluginClassLoader createClassLoader() {
        return new PluginClassLoader(forbiddenPackages, restrictedPackages);
    }

}
