package jworkspace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.kohsuke.args4j.ExampleMode.ALL;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginException;

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

    @Option(name = "--name", usage = "user profile name")
    private String name;

    @Option(name = "--password", usage = "user profile password")
    private String password;

    @Argument
    private final List<String> arguments = new ArrayList<>();

    private Workspace() {}

    public static void main(String[] args) {
        new Workspace().doMain(args);
    }

    private void doMain(String[] args) {

        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
            if (arguments.isEmpty()) {
                throw new CmdLineException(parser, "No argument is given");
            }

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

        // access non-option arguments
        System.out.println("other arguments are:");

        for (String s : arguments) {
            System.out.println(s);
        }
    }

    public static void start(@NonNull String name, @NonNull String password, @NonNull Path root)
        throws ProfileOperationException, IOException, PluginException {
        /*
         * Validate profile name and password to get the configuration stored there
         */
        ProfilesManager profilesManager = ServiceLocator.getInstance().getProfilesManager();
        profilesManager.setBasePath(root);
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
         * Load plugins from profile directory
         */
        WorkspacePluginLocator pluginLocator = ServiceLocator.getInstance().getPluginLocator();
        List<Plugin> plugins = pluginLocator.loadPlugins(
            profilePath.resolve("plugins")
        );
        /*
         * Send plugins to runtime manager if they are instances of Runnable
         */
        RuntimeManager runtimeManager = ServiceLocator.getInstance().getRuntimeManager();
        for (Plugin plugin : plugins) {
            Object pluginObject = plugin.newInstance();
            if (pluginObject instanceof Runnable) {
                runtimeManager.take((Runnable) pluginObject);
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
}
