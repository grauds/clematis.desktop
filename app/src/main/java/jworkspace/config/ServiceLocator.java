package jworkspace.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.hyperrealm.kiwi.plugin.Plugin;

import jworkspace.api.EventsDispatcher;
import jworkspace.runtime.RuntimeManager;
import jworkspace.runtime.WorkspacePluginLocator;
import jworkspace.users.ProfilesManager;
import lombok.Getter;


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

    public static ServiceLocator getInstance() {
        return InstanceHolder.SERVICE_LOCATOR;
    }

    private static final class InstanceHolder {
        private static final ServiceLocator SERVICE_LOCATOR = new ServiceLocator();
    }
}
