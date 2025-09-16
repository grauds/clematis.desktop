package jworkspace;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.kohsuke.args4j.ExampleMode.ALL;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.hyperrealm.kiwi.plugin.PluginException;

import jworkspace.config.ServiceLocator;
import jworkspace.runtime.RuntimeManager;
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

    public static final String VERSION = "Java Workspace 2.0.0";

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
         * Create system plugins
         */
        ServiceLocator.getInstance().getSystemPlugins().addAll(
            ServiceLocator.getInstance().getPluginLocator().loadPlugins(
                root.resolve(PLUGINS_DIRECTORY)
            )
        );
        /*
         * Start a user session
         */
        startSession(name, password, root);
    }

    public static void startSession(@NonNull String name, String password, Path root) throws ProfileOperationException {
        /*
         * Validate the profile name and password to get the configuration stored there
         */
        ProfilesManager profilesManager = ServiceLocator.getInstance().getProfilesManager();
        profilesManager.setBasePath(root);
        profilesManager.login(name, password);

        Profile currentProfile = profilesManager.getCurrentProfile();
        Path profilePath = currentProfile.getProfilePath(profilesManager.getBasePath());
        /*
         * Set the workspace user directory inside the workspace base directory to plugins context
         */
        ServiceLocator.getInstance().getPluginLocator().getContext().setUserDir(profilePath);
        /*
         * Initialize system plugins from the user directory
         */
        ServiceLocator.loadPlugins(ServiceLocator.getInstance().getSystemPlugins());
        /*
         * Load plugins from the user directory
         */
        ServiceLocator.getInstance().getUserPlugins().addAll(
            ServiceLocator.getInstance().loadPlugins(profilePath)
        );
    }

    public static void endSession() {
        if (ServiceLocator.getInstance().getProfilesManager().userLogged()) {
            /*
             * Save amd reset all user plugins
             */
            ServiceLocator.savePlugins(
                ServiceLocator.getInstance().getUserPlugins()
            );
            /*
             * Unload all user plugins
             */
            ServiceLocator.unloadPlugins(
                ServiceLocator.getInstance().getUserPlugins()
            );
            /*
             * Save and reset all system plugins
             */
            ServiceLocator.savePlugins(
                ServiceLocator.getInstance().getSystemPlugins()
            );
            /*
             * Logout from the current profile
             */
            ServiceLocator.getInstance().getProfilesManager().logout();
            /*
             * Set workspace user directory to workspace base directory
             */
            ServiceLocator.getInstance().getPluginLocator().getContext().setUserDir(
                ServiceLocator.getInstance().getProfilesManager().getBasePath()
            );
        }
    }

    public static void stop() throws IOException {
        /*
         * Stop all running threads
         */
        RuntimeManager runtimeManager = ServiceLocator.getInstance().getRuntimeManager();
        runtimeManager.yield();
        /*
         * End user session
         */
        endSession();
        /*
         * Unload system plugins
         */
        ServiceLocator.unloadPlugins(
            ServiceLocator.getInstance().getSystemPlugins()
        );
    }

    public static void exit() {

        try {
            stop();
        } catch (IOException e) {
            log.severe(e.getMessage());
        }

        System.exit(0);
    }

    public static Workspace getInstance() {
        return Workspace.InstanceHolder.WORKSPACE;
    }

    private static final class InstanceHolder {
        private static final Workspace WORKSPACE = new Workspace();
    }
}
