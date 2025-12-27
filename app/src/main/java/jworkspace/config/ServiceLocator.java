package jworkspace.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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

@Log
@Getter
public class ServiceLocator {

    private final ProfilesManager profilesManager = new ProfilesManager();

    private final RuntimeManager runtimeManager = new RuntimeManager(
        9,
        18,
        30,
        TimeUnit.MINUTES
    );

    private final WorkspacePluginLocator pluginLocator = new WorkspacePluginLocator();

    private final List<Plugin> systemPlugins = new ArrayList<>();

    private final List<Plugin> userPlugins = new ArrayList<>();

    private final EventsDispatcher eventsDispatcher = new EventsDispatcher();

    private ServiceLocator() {}

    public List<Plugin> loadPlugins(Path root, String type) {

        List<Plugin> plugins = pluginLocator.loadPlugins(
            root.resolve(PLUGINS_DIRECTORY), type
        );
        loadPlugins(plugins);
        return plugins;
    }

    public static void loadPlugins(List<Plugin> plugins) {

        // Create required modules
        for (Plugin plugin : plugins) {
            try {
                Object pluginObject = plugin.newInstance();
                // Load method initializes a plugin object
                if (pluginObject instanceof IWorkspaceComponent component) {
                    component.load();
                }
                // Send plugins to the runtime manager if they are instances of Runnable
                if (pluginObject instanceof Runnable runnable) {
                    ServiceLocator.getInstance().getRuntimeManager().take(runnable);
                }
            } catch (PluginException | IOException e) {
                log.severe(e.getMessage());
            }
        }
    }

    public static void savePlugins(List<Plugin> plugins) {

        for (Plugin plugin : plugins) {
            Object pluginObject = plugin.getPluginObject();
            // Unload method saves and resets plugin
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

    public static void unloadPlugins(List<Plugin> plugins) {
        plugins.forEach(Plugin::reset);
        plugins.clear();
    }

    public static ServiceLocator getInstance() {
        return InstanceHolder.SERVICE_LOCATOR;
    }

    private static final class InstanceHolder {
        private static final ServiceLocator SERVICE_LOCATOR = new ServiceLocator();
    }
}
