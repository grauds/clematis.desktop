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

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;

import com.hyperrealm.kiwi.event.plugin.PluginReloadEvent;
import com.hyperrealm.kiwi.event.plugin.PluginReloadListener;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A class that represents a plugin. A <code>Plugin</code> object encapsulates
 * all of the data that makes up a plugin, as specified by the plugin's
 * entry in a JAR Manifest. Instances of the plugin object itself can be
 * created with the <code>newInstance()</code> method.
 *
 * @author Mark Lindner
 * @since Kiwi 1.3
 */
@EqualsAndHashCode(callSuper = true)
public final class Plugin extends PluginDTO {

    private static final String FAILED_TO_INSTANTIATE_PLUGIN = "failed to instantiate plugin ";

    private Class<?> pluginClass = null;

    @Getter
    private Object pluginObject = null;

    @Getter
    private final Properties properties = new Properties();

    private final PluginLocator<?> locator;

    private final EventListenerList listeners = new EventListenerList();

    @Getter
    private boolean loaded = false;

    private PluginClassLoader loader;

    /**
     * Construct a new plugin. A plugin is uniquely identified by a jar file
     * and a class name. It's initially created by the PluginLocator which scans
     * a jar file for plugin entries. When it's reloaded, it reopens the jar file
     * and rescans everything.
     */
    <T extends PluginContext> Plugin(PluginLocator<T> locator, String jarFile, String type)
        throws PluginException {
        super(type, jarFile);

        this.locator = locator;
        load();
    }

    /**
     * Get the <code>PluginContext</code> for this plugin.
     *
     * @return The context.
     */
    @SuppressWarnings("unchecked")
    public <T extends PluginContext> T getContext() {
        return (T) locator.getContext();
    }

    /**
     * Load the plugin. This method attempts to load the plugin entry-point
     * class and create an instance of it. The entry-point class must have a
     * default public constructor.
     *
     * @throws PluginException If the plugin
     *                                                         could not be loaded.
     */
    @SuppressWarnings({"CyclomaticComplexity", "NestedIfDepth"})
    private void load() throws PluginException {

        Manifest mf;

        try (JarFile jar = new JarFile(new File(jarFile))) {

            mf = jar.getManifest();

            if (mf == null) {
                throw new PluginException("No manifest found");
            }

            String classFile = null;
            Attributes attrs = null;
            boolean found = false;

            Map<String, Attributes> map = mf.getEntries();

            for (String s : map.keySet()) {

                classFile = s;
                attrs = mf.getAttributes(classFile);

                if (attrs.getValue(PLUGIN_NAME) != null) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new PluginException("No plugin entry found in manifest");
            }

            className = PluginClassLoader.pathToClassName(classFile);

            // read in the attributes
            for (Object o : attrs.keySet()) {

                Attributes.Name nm = (Attributes.Name) o;
                String a = nm.toString();
                String v = attrs.getValue(nm);

                switch (a) {
                    case PLUGIN_NAME:
                        setName(v);
                        break;
                    case PLUGIN_TYPE:
                        // ANY plugin type can be used as SYSTEM or USER or else, the others' have to match
                        if (!(PluginDTO.PLUGIN_TYPE_ANY.equalsIgnoreCase(v)
                            || (getType() != null && !getType().isEmpty() && getType().equalsIgnoreCase(v)))
                        ) {
                            throw new PluginException(
                                String.format("Plugin type mismatch: %s loaded as %s", v, getType())
                            );
                        }
                        break;
                    case PLUGIN_DESCRIPTION:
                        setDescription(v);
                        break;
                    case PLUGIN_ICON:
                        setIconFile(v);
                        break;
                    case PLUGIN_VERSION:
                        setVersion(v);
                        break;
                    case PLUGIN_HELP_URL:
                        setHelpUrl(v);
                        break;
                    default:
                        properties.put(a, v);
                        break;
                }
            }

            if ((type == null) || (name == null)) {
                throw new PluginException("Invalid plugin manifest entry");
            }

            // create the classloader

            loader = locator.createClassLoader();
            loader.addJarFile(jarFile);

            // load the ICON

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
                throw new PluginException("Failed to load plugin class " + className, ex);
            }

            loaded = true;

        } catch (IOException ex) {
            throw new PluginException("Unable to read archive", ex);
        }
    }

    /* Unload the plugin.
     */

    public void reset() {
        loader = null;
        pluginClass = null;
        pluginObject = null;
        icon = null;
        loaded = false;
    }

    /**
     * Reload the plugin.
     *
     * @since Kiwi 2.0
     */
    public void reload() throws PluginException {
        reset();
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
     * @throws PluginException If a problem
     *                                                         occurs during class instantiation.
     * @since Kiwi 2.0
     */

    public Object newInstance() throws PluginException {

        if (!loaded) {
            throw (new PluginException("Plugin is not loaded!"));
        }

        if (pluginObject != null) {
            return pluginObject;
        }

        try {
            // try to find a c'tor that takes a PluginContext first
            Constructor<?> ctor = null;
            Constructor<?>[] ctors = pluginClass.getConstructors();

            for (Constructor<?> actor : ctors) {
                Class<?>[] args = actor.getParameterTypes();
                if (args.length != 1) {
                    continue;
                }

                if (PluginContext.class.isAssignableFrom(args[0])) {
                    ctor = actor;
                    break;
                }
            }
            // call a constructor with plugin context injection
            if (ctor != null) {
                pluginObject = ctor.newInstance(getContext());
            }

        } catch (Exception ex) {
            throw (new PluginException(FAILED_TO_INSTANTIATE_PLUGIN + pluginClass.getName(), ex));
        }

        if (pluginObject == null) {
            try {
                pluginObject = pluginClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw (new PluginException(FAILED_TO_INSTANTIATE_PLUGIN + pluginClass.getName(), ex));
            }
        }

        return pluginObject;
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
        return properties.getProperty(name);
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
        return properties.getProperty(name, defaultValue);
    }
}
