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

import jworkspace.api.IWorkspaceUI;
import jworkspace.config.ServiceLocator;
import jworkspace.installer.WorkspaceInstaller;
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

    @Option(name = "--name", usage = "user profile name")
    private String name;

    @Option(name = "--password", usage = "user profile password")
    private String password;

    private Workspace() {}

    public static void main(String[] args) {
        new Workspace().doMain(args);
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
                Workspace.start(name, password,
                    Paths.get(System.getProperty("user.dir")).resolve(HOME_DIR).toString()
                );
            }
        } catch (ProfileOperationException | IOException | PluginException e) {
            throw new RuntimeException(e);
        }
    }

    public static void start(@NonNull String name, String password, @NonNull String root)
        throws ProfileOperationException, IOException, PluginException {
        /*
         * Validate profile name and password to get the configuration stored there
         */
        ProfilesManager profilesManager = ServiceLocator.getInstance().getProfilesManager();
        profilesManager.setBasePath(Paths.get(root));
        profilesManager.login(name, password);
        /*
         * Choose the current profile
         */
        Profile currentProfile = profilesManager.getCurrentProfile();
        Path profilePath = currentProfile.getProfilePath(profilesManager.getBasePath());
        /*
         * Load workspace installer from the user path
         */
        WorkspaceInstaller workspaceInstaller = ServiceLocator.getInstance().getInstaller();
        workspaceInstaller.setDataRoot(profilePath.toFile());
        workspaceInstaller.reset();
        workspaceInstaller.load();
        /*
         * Load plugins from system directory
         */
        WorkspacePluginLocator pluginLocator = ServiceLocator.getInstance().getPluginLocator();
        List<Plugin> plugins = pluginLocator.loadPlugins(
            profilesManager.getBasePath().resolve(PLUGINS_DIRECTORY)
        );
        /*
         * Create required modules
         */
        for (Plugin plugin : plugins) {
            Object pluginObject = plugin.newInstance();
            if (pluginObject instanceof IWorkspaceUI gui) {
                gui.load();
            }
        }
        /*
         * Load plugins from profile directory
         */
        List<Plugin> userPlugins = pluginLocator.loadPlugins(
            profilePath.resolve(PLUGINS_DIRECTORY)
        );
        /*
         * Send plugins to runtime manager if they are instances of Runnable
         */
        RuntimeManager runtimeManager = ServiceLocator.getInstance().getRuntimeManager();
        for (Plugin plugin : userPlugins) {
            Object pluginObject = plugin.newInstance();
            if (pluginObject instanceof Runnable runnable) {
                runtimeManager.take(runnable);
            }
        }
    }

    static void stop(@NonNull Path root) throws IOException {
        /*
         * Stop all running threads
         */
        RuntimeManager runtimeManager = ServiceLocator.getInstance().getRuntimeManager();
        runtimeManager.yield();
        /*
         * Clear up workspace installer
         */
        WorkspaceInstaller workspaceInstaller = ServiceLocator.getInstance().getInstaller();
        workspaceInstaller.save();
        workspaceInstaller.setDataRoot(root.toFile());
        workspaceInstaller.reset();
        /*
         * Logout from the current profile
         */
        ProfilesManager profilesManager = ServiceLocator.getInstance().getProfilesManager();
        profilesManager.logout();
    }

    public static void exit() {
        System.exit(0);
    }
}
