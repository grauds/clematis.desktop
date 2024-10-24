package jworkspace;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.kohsuke.args4j.ExampleMode.ALL;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginException;

import jworkspace.api.IWorkspaceComponent;
import jworkspace.config.ServiceLocator;
import jworkspace.runtime.RuntimeManager;
import jworkspace.runtime.WorkspacePluginLocator;
import jworkspace.users.Profile;
import jworkspace.users.ProfileOperationException;
import jworkspace.users.ProfilesManager;
import lombok.NonNull;
import lombok.extern.java.Log;

@Log
@SuppressWarnings({
    "PMD",
    "checkstyle:hideutilityclassconstructor",
    "checkstyle:regexp"
})
public class Workspace {

    public static final String VERSION = "Java Workspace 2.0.0 SNAPSHOT";

    public static final String HOME_DIR = ".jworkspace";

    public static final String PLUGINS_DIRECTORY = "plugins";

    @Option(name = "--name", usage = "user profile name", required = true)
    private String name;

    @Option(name = "--password", usage = "user profile password")
    private String password;

    @Option(name = "--path", usage = "workspace path to store data")
    private String path = System.getProperty("user.dir");

    private Workspace() {}

    public static void main(String[] args) {
        Workspace.getInstance().doMain(args);
    }

    private void doMain(String[] args) {

        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("Workspace [options...] arguments...");

            parser.printUsage(System.err);
            System.err.println();

            System.err.println("Example: " + parser.printExample(ALL));

            return;
        }

        if (name != null) {
            System.out.println("--name is set");
        }

        if (password != null) {
            System.out.println("--password is set");
        }

        try {
            if (name != null) {
                Workspace.start(name, password, Paths.get(path).resolve(HOME_DIR));
            }
        } catch (ProfileOperationException | IOException | PluginException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(@NonNull String name, String password)
        throws ProfileOperationException, PluginException, IOException {
        Workspace.start(name, password, Paths.get(path).resolve(HOME_DIR));
    }

    public static void start(@NonNull String name, String password, @NonNull Path root)
        throws ProfileOperationException, IOException, PluginException {
        /*
         * Validate profile name and password to get the configuration stored there
         */
        ProfilesManager profilesManager = ServiceLocator.getInstance().getProfilesManager();
        profilesManager.setBasePath(root);
        profilesManager.login(name, password);

        Profile currentProfile = profilesManager.getCurrentProfile();
        Path profilePath = currentProfile.getProfilePath(profilesManager.getBasePath());
        /*
         * Set workspace user directory inside workspace base directory to plugins context
         */
        ServiceLocator.getInstance().getPluginLocator().getContext().setUserDir(profilePath);
        /*
         * Load plugins from system directory
         */
        WorkspacePluginLocator pluginLocator = ServiceLocator.getInstance().getPluginLocator();
        List<Plugin> plugins = pluginLocator.loadPlugins(
            root.resolve(PLUGINS_DIRECTORY)
        );
        ServiceLocator.getInstance().getSystemPlugins().clear();
        ServiceLocator.getInstance().getSystemPlugins().addAll(plugins);
        /*
         * Create required modules
         */
        for (Plugin plugin : plugins) {
            Object pluginObject = plugin.newInstance();
            if (pluginObject instanceof IWorkspaceComponent component) {
                component.load();
            }
        }
        /*
         * Load plugins from profile directory
         */
        List<Plugin> userPlugins = pluginLocator.loadPlugins(
            profilePath.resolve(PLUGINS_DIRECTORY)
        );
        ServiceLocator.getInstance().getUserPlugins().clear();
        ServiceLocator.getInstance().getUserPlugins().addAll(userPlugins);

        RuntimeManager runtimeManager = ServiceLocator.getInstance().getRuntimeManager();
        for (Plugin plugin : userPlugins) {
            Object pluginObject = plugin.newInstance();
            if (pluginObject instanceof IWorkspaceComponent component) {
                component.load();
            }
            /*
             * Send plugins to runtime manager if they are instances of Runnable
             */
            if (pluginObject instanceof Runnable runnable) {
                runtimeManager.take(runnable);
            }
        }
    }

    public void stop() throws IOException {
        Workspace.stop(Paths.get(path).resolve(HOME_DIR));
    }

    public static void stop(@NonNull Path root) throws IOException {
        /*
         * Stop all running threads
         */
        RuntimeManager runtimeManager = ServiceLocator.getInstance().getRuntimeManager();
        runtimeManager.yield();
        /*
         * Save and reset all system plugins
         */
        for (Plugin plugin : ServiceLocator.getInstance().getSystemPlugins()) {
            Object pluginObject = plugin.getPluginObject();
            if (pluginObject instanceof IWorkspaceComponent component) {
                component.save();
                component.reset();
            }
        }
        /*
         * Save and reset all user plugins
         */
        for (Plugin plugin : ServiceLocator.getInstance().getUserPlugins()) {
            Object pluginObject = plugin.getPluginObject();
            if (pluginObject instanceof IWorkspaceComponent component) {
                component.save();
                component.reset();
            }
        }
        /*
         * Logout from the current profile
         */
        ProfilesManager profilesManager = ServiceLocator.getInstance().getProfilesManager();
        profilesManager.logout();
    }

    public static void exit() {
        System.exit(0);
    }

    public static Workspace getInstance() {
        return Workspace.InstanceHolder.WORKSPACE;
    }

    private static final class InstanceHolder {
        private static final Workspace WORKSPACE = new Workspace();
    }
}
