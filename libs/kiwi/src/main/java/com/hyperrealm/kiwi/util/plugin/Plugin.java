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

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;

import com.hyperrealm.kiwi.event.PluginReloadEvent;
import com.hyperrealm.kiwi.event.PluginReloadListener;

/**
 * A class that represents a plugin. A <code>Plugin</code> object encapsulates
 * all of the data that makes up a plugin, as specified by the plugin's
 * entry in a JAR Manifest. Instances of the plugin object itself can be
 * created with the <code>newInstance()</code> method.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 1.3
 */

public final class Plugin<T> {

    private static final String FAILED_TO_INSTANTIATE_PLUGIN = "failed to instantiate plugin ";

    private boolean loaded = false;

    private String className;

    private String name;

    private String type;

    private String desc;

    private String iconFile;

    private Properties props = new Properties();

    private String version;

    private String expectedType;

    private String jarFile;

    /* stuff that must be loaded */
    private Class pluginClass = null;

    private Icon icon = null;

    private JarFile jar = null;

    private URL helpURL = null;

    /* support objects */
    private PluginClassLoader loader;

    private PluginLocator<T> locator;

    private EventListenerList listeners = new EventListenerList();

    /* Construct a new plugin. A plugin is uniquely identified by a jar file
     * and a class name. It's initially created by the PluginLocator which scans
     * a jar file for plugin entires. When it's reloaded, it reopens the jar file
     * and rescans everything. (hopefully...will this really work?)
     */

    Plugin(PluginLocator<T> locator, String jarFile, String expectedType)
        throws PluginException {

        this.locator = locator;
        this.jarFile = jarFile;
        this.expectedType = expectedType;
        load();
    }

    /**
     * Get the version number for this plugin.
     *
     * @return The version number.
     */

    public String getVersion() {
        return version;
    }

    /**
     * Get the class name for this plugin.
     *
     * @return The class name.
     */

    public String getClassName() {
        return className;
    }

    /**
     * Get the <code>PluginContext</code> for this plugin.
     *
     * @return The context.
     */

    public PluginContext getContext() {
        return locator.getContext();
    }

    /**
     * Get the name of this plugin.
     *
     * @return The name.
     */

    public String getName() {
        return name;
    }

    /**
     * Get the type of this plugin.
     *
     * @return The type.
     */

    public String getType() {
        return type;
    }

    /**
     * Get the path to the file that the plugin was loaded from.
     *
     * @since Kiwi 2.0
     */

    public String getFile() {
        return jarFile;
    }

    /**
     * Get the user-defined properties for the plugin. These correspond to the
     * user-defined key/value pairs in the Manifest entry for this plugin.
     *
     * @return A <code>Properties</code> object containing the user-defined
     * properties.
     */

    public Properties getProperties() {
        return props;
    }

    /**
     * Get a specific user-defined property for the plugin. User-defined
     * properties are those fields in the Manifest entry for the plugin that are
     * not defined and recognized by the plugin framework itself.
     *
     * @param name The property name (key).
     * @return The value of the property, or <code>null</code> if there is no
     * property with the given name.
     */

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    /**
     * Get a specific user-defined property for the plugin.
     *
     * @param name         The property name (key).
     * @param defaultValue The default value for this property.
     * @return The value of the property, or <code>defaultValue</code> if there
     * is no property with the given name.
     */

    public String getProperty(String name, String defaultValue) {
        return props.getProperty(name, defaultValue);
    }

    /**
     * Get the description of this plugin.
     *
     * @return The description, or <b>null</b> if no description is available.
     */

    public String getDescription() {
        return desc;
    }

    /**
     * Get the ICON for this plugin.
     *
     * @return The ICON, or <b>null</b> if no ICON is available.
     */

    public Icon getIcon() {
        return icon;
    }

    /**
     * Get the help URL for this plugin.
     *
     * @return The URL, or <b>null</b> if no URL is available.
     * @since Kiwi 2.0
     */

    public URL getHelpURL() {
        return helpURL;
    }

    /**
     * Determine if the plugin is loaded.
     *
     * @return <b>true</b> if the plugin is loaded and <b>false</b> otherwise.
     */

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Load the plugin. This method attempts to load the plugin entry-point
     * class and create an instance of it. The entry-point class must have a
     * default public constructor.
     *
     * @throws com.hyperrealm.kiwi.util.plugin.PluginException If the plugin
     *                                                         could not be loaded.
     */
    @SuppressWarnings("all")
    private void load() throws PluginException {

        Manifest mf;

        try {
            jar = new JarFile(new File(jarFile));

            // open the manifest and find the plugin entry

            mf = jar.getManifest();
        } catch (IOException ex) {
            throw new PluginException("Unable to read archive");
        }

        if (mf == null) {
            throw new PluginException("No manifest found");
        }

        String classFile = null;
        Attributes attrs = null;
        boolean found = false;

        Map map = mf.getEntries();
        Iterator iter = map.keySet().iterator();

        while (iter.hasNext()) {

            classFile = (String) iter.next();
            attrs = mf.getAttributes(classFile);

            if (attrs.getValue("PluginName") != null) {
                found = true;
                break;
            }
        }

        if (!found) {
            throw new PluginException("No plugin entry found in manifest");
        }

        className = PluginClassLoader.pathToClassName(classFile);

        // read in the attributes

        iter = attrs.keySet().iterator();

        while (iter.hasNext()) {

            Attributes.Name nm = (Attributes.Name) iter.next();
            String a = nm.toString();
            String v = attrs.getValue(nm);

            switch (a) {
                case "PluginName":
                    name = v;
                    break;
                case "PluginType":
                    type = v;
                    break;
                case "PluginDescription":
                    desc = v;
                    break;
                case "PluginIcon":
                    iconFile = v;
                    break;
                case "PluginVersion":
                    version = v;
                    break;
                case "PluginHelpURL":
                    try {
                        helpURL = new URL(v);
                    } catch (MalformedURLException ex) { /* ignore */ }
                    break;
                default:
                    props.put(a, v);
                    break;
            }
        }

        if ((type == null) || (name == null)) {
            try {
                jar.close();
            } catch (IOException ignored) {
            }
            throw new PluginException("Invalid plugin manifest entry");
        }

        if (!type.equals(expectedType)) {
            try {
                jar.close();
            } catch (IOException ignored) {
            }
            throw new PluginException("Plugin type mismatch");
        }

        // create the classloader

        loader = locator.createClassLoader();
        loader.addJarFile(jar);

        // load the ICON

        GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (iconFile != null && !GraphicsEnvironment.isHeadless()) {
            JarEntry entry = (JarEntry) jar.getEntry(iconFile);
            if (entry != null) {
                try {
                    InputStream in = jar.getInputStream(entry);
                    Image im = locator.getDecoder().decodeImage(in);
                    if (im != null) {
                        icon = new ImageIcon(im);
                    }
                    in.close();
                } catch (IOException ex) {
                    icon = null;
                }
            }
        }

        // load the plugin class

        try {
            pluginClass = loader.loadClass(className);
        } catch (Exception ex) {
            throw new PluginException("failed to load plugin class " + className, ex);
        }

        loaded = true;
    }

    /* Unload the plugin.
     */

    private void unload() throws IOException {
        loader = null;
        pluginClass = null;
        icon = null;
        try {
            jar.close();
        } finally {
            jar = null;
            loaded = false;
        }
    }

    /**
     * Reload the plugin.
     *
     * @since Kiwi 2.0
     */

    public void reload() throws PluginException, IOException {
        unload();
        load();
        firePluginReloaded();
    }

    /**
     * Create a new instance of the plugin object. The method attempts to
     * instantiate the plugin object by calling a constructor that takes an
     * object that implements <code>PluginContext</code> (or a subinterface
     * thereof) as its only argument. If no such constructor exists, the method
     * tries instantiate the object using the default constructor.
     *
     * @throws com.hyperrealm.kiwi.util.plugin.PluginException If a problem
     *                                                         occurs during class instantiation.
     * @since Kiwi 2.0
     */

    public T newInstance() throws PluginException {
        if (!loaded) {
            throw (new PluginException("Plugin is not loaded!"));
        }

        Object obj = null;

        try {
            // try to find a c'tor that takes a PluginContext first

            Constructor ctor = null;
            Constructor[] ctors = pluginClass.getConstructors();

            for (Constructor actor : ctors) {
                Class[] args = actor.getParameterTypes();
                if (args.length != 1) {
                    continue;
                }

                if (PluginContext.class.isAssignableFrom(args[0])) {
                    ctor = actor;
                    break;
                }
            }

            if (ctor != null) {
                obj = ctor.newInstance(getContext());
            }

        } catch (Exception ex) {
            throw (new PluginException(FAILED_TO_INSTANTIATE_PLUGIN
                + pluginClass.getName(), ex));
        }

        if (obj == null) {
            try {
                obj = pluginClass.newInstance();
            } catch (Exception ex) {
                throw (new PluginException(FAILED_TO_INSTANTIATE_PLUGIN
                    + pluginClass.getName(), ex));
            }
        }

        T instance;

        try {
            instance = (T) obj;
        } catch (ClassCastException ex) {
            throw (new PluginException("plugin class " + pluginClass.getName()
                + " is of incompatible type", ex));
        }

        return (instance);
    }

    /**
     * Add a <code>PluginReloadListener</code> to this model's list of
     * listeners.
     *
     * @param listener The listener to add.
     * @since Kiwi 2.0
     */

    public void addPluginReloadListener(PluginReloadListener listener) {
        listeners.add(PluginReloadListener.class, listener);
    }

    /**
     * Remove a <code>PluginReloadListener</code> from this model's list of
     * listeners.
     *
     * @param listener The listener to remove.
     * @since Kiwi 2.0
     */

    public void removePluginReloadListener(PluginReloadListener listener) {
        listeners.remove(PluginReloadListener.class, listener);
    }

    /**
     * Fire a <i>plugin reloaded</i> event.
     */

    private void firePluginReloaded() {

        PluginReloadEvent evt = null;

        Object[] list = listeners.getListenerList();

        for (int i = list.length - 2; i >= 0; i -= 2) {
            if (list[i] == PluginReloadListener.class) {
                // Lazily create the event:
                if (evt == null) {
                    evt = new PluginReloadEvent(this);
                }
                ((PluginReloadListener) list[i + 1]).pluginReloaded(evt);
            }
        }
    }

    /**
     * Return a string representation of this plugin, which consists of
     * its name and version.
     *
     * @since Kiwi 2.0
     */

    public String toString() {

        String n = getName();
        String v = getVersion();

        if (v != null) {
            n += " " + v;
        }

        return (n);
    }
}
