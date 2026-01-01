package jworkspace.config;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2025 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginException;

import static jworkspace.Workspace.PLUGINS_DIRECTORY;
import jworkspace.api.EventsDispatcher;
import jworkspace.api.IWorkspaceComponent;
import jworkspace.runtime.RuntimeManager;
import jworkspace.runtime.WorkspacePluginLocator;
import jworkspace.users.ProfilesManager;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Central singleton service locator for the desktop workspace.
 * <p>
 * Provides access to core managers and services including:
 * <ul>
 *     <li>{@link ProfilesManager} — user profile handling</li>
 *     <li>{@link RuntimeManager} — manages Runnable tasks/plugins</li>
 *     <li>{@link WorkspacePluginLocator} — loads workspace plugins</li>
 *     <li>Plugin lists — system and user plugins</li>
 *     <li>{@link EventsDispatcher} — global event bus</li>
 * </ul>
 * <p>
 * This class uses the <a href="https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom">
 * Initialization-on-demand holder idiom</a> to implement a thread-safe, lazy-loaded singleton.
 * </p>
 */
@Log
@Getter
public class ServiceLocator {

    /**
     * Manages user profiles.
     */
    private final ProfilesManager profilesManager = new ProfilesManager();

    /**
     * Manages execution of Runnable tasks/plugins with a configurable thread pool.
     * <p>
     * Parameters (9, 18, 30, TimeUnit.MINUTES) configure the pool sizes and task expiry.
     * </p>
     */
    private final RuntimeManager runtimeManager = new RuntimeManager(
        9,
        18,
        30,
        TimeUnit.MINUTES
    );

    /**
     * Locator for workspace plugins.
     */
    private final WorkspacePluginLocator pluginLocator = new WorkspacePluginLocator();

    /**
     * List of system-level plugins.
     */
    private final List<Plugin> systemPlugins = Collections.synchronizedList(new ArrayList<>());

    /**
     * List of user-installed plugins.
     */
    private final List<Plugin> userPlugins = Collections.synchronizedList(new ArrayList<>());

    /**
     * Global events dispatcher for workspace components.
     */
    private final EventsDispatcher eventsDispatcher = new EventsDispatcher();

    /**
     * Private constructor prevents external instantiation.
     */
    private ServiceLocator() {}

    /**
     * Loads plugins from a given root directory and type.
     *
     * @param root the root directory of plugins
     * @param type the plugin type
     * @return list of loaded plugins
     */
    public List<Plugin> loadPlugins(Path root, String type) {

        // Use the plugin locator to load plugin descriptors from the directory
        List<Plugin> plugins = pluginLocator.loadPlugins(
            root.resolve(PLUGINS_DIRECTORY), type
        );

        // Initialize loaded plugin objects
        loadPlugins(plugins);
        return plugins;
    }

    /**
     * Initializes a list of plugin objects by creating instances and
     * performing setup.
     *
     * @param plugins the list of plugins to initialize
     */
    public static void loadPlugins(List<Plugin> plugins) {

        // Iterate all plugins and initialize
        for (Plugin plugin : plugins) {
            try {
                // Create a new plugin instance
                Object pluginObject = plugin.newInstance();

                // If the plugin implements IWorkspaceComponent, load it
                if (pluginObject instanceof IWorkspaceComponent component) {
                    component.load();
                }

                // If the plugin is Runnable, schedule it in the runtime manager
                if (pluginObject instanceof Runnable runnable) {
                    ServiceLocator.getInstance().getRuntimeManager().take(runnable);
                }

            } catch (PluginException | IOException e) {
                // Log errors during plugin initialization
                log.severe(e.getMessage());
            }
        }
    }

    /**
     * Saves a list of plugin states by invoking save/reset on
     * workspace components.
     *
     * @param plugins the list of plugins to save
     */
    public static void savePlugins(List<Plugin> plugins) {

        for (Plugin plugin : plugins) {
            Object pluginObject = plugin.getPluginObject();

            // Save and reset workspace components
            if (pluginObject instanceof IWorkspaceComponent component) {
                try {
                    component.save();
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
                component.reset();
            }
        }
    }

    /**
     * Resets and unloads a list of plugins.
     *
     * @param plugins the list of plugins to unload
     */
    public static void unloadPlugins(List<Plugin> plugins) {
        plugins.forEach(Plugin::reset); // Reset each plugin object
        plugins.clear(); // Clear the list
    }

    /**
     * Returns the singleton instance of {@link ServiceLocator}.
     *
     * @return single shared instance
     */
    public static ServiceLocator getInstance() {
        // Access InstanceHolder, triggers lazy initialization of SERVICE_LOCATOR
        return InstanceHolder.SERVICE_LOCATOR;
    }

    /**
     * Private static nested class holding the singleton instance.
     * <p>
     * This is the core of the "Initialization-on-demand holder idiom":
     * the SERVICE_LOCATOR instance is created only when this class
     * is first referenced by getInstance().
     * <ul>
     *     <li>Lazy initialization: instance created only when needed</li>
     *     <li>Thread-safe: JVM guarantees atomic class initialization</li>
     *     <li>No explicit synchronization required</li>
     * </ul>
     * </p>
     */
    private static final class InstanceHolder {
        /**
         * The singleton instance of {@link ServiceLocator}.
         */
        private static final ServiceLocator SERVICE_LOCATOR = new ServiceLocator();
    }
}

