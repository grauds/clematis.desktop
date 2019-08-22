package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2019 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   VERSION 2 of the License, or (at your option) any later VERSION.

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
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IConstants;
import jworkspace.api.IUserManager;
import jworkspace.api.IWorkspaceInstaller;
import jworkspace.api.IWorkspaceListener;
import jworkspace.api.UI;
import jworkspace.api.WorkspaceComponent;
import jworkspace.installer.WorkspaceInstaller;
import jworkspace.ui.DefaultUI;
import jworkspace.ui.WorkspaceResourceManager;
import jworkspace.users.Profile;
import jworkspace.users.ProfileOperationException;
import jworkspace.users.WorkspaceUserManager;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;

/**
 * Workspace is a core class for Java Workspace
 *
 * @author Anton Troshin
 */
@SuppressWarnings("unused")
public class Workspace {

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Workspace.class);

    /**
     * Base workspace path to store data
     */
    @Setter(AccessLevel.PRIVATE)
    private static Path basePath;

    /**
     * These are actually invisible services, that are loaded by system at startup and
     * unloaded on finish. These services are for all users in system and must implement WorkspaceComponent
     * interface to be plugged into workspace lifecycle.
     */
    private static Set<Plugin> systemPlugins = new HashSet<>();
    /**
     * These are actually invisible services, that are loaded by user at login or later manually
     * and unloaded on logout. These services are NOT for all users in system.
     */
    private static Set<Plugin> userPlugins = new HashSet<>();
    /**
     * Workspace components list
     */
    private static Collection<WorkspaceComponent> workspaceComponents = new Vector<>();
    /**
     * Workspace user components list
     */
    private static Collection<WorkspaceComponent> workspaceUserComponents = new Vector<>();
    /**
     * Resource manager
     */
    private static WorkspaceResourceManager resourceManager = null;
    /**
     * Listeners for service events.
     */
    private static List<IWorkspaceListener> workspaceListeners = new Vector<>();
    /**
     * Installer
     */
    private static IWorkspaceInstaller workspaceInstaller = null;
    /**
     * Java Workspace UI - a plugin extension point
     */
    private static UI ui = new DefaultUI();

    /**
     * Private constructor
     */
    private Workspace() {}

    /**
     * Starts the application.
     *
     * @param args an array of command-line arguments
     */
    public static void main(String[] args) {
        try {
            Profile candidate = new Profile();
            CmdLineParser parser = new CmdLineParser(candidate);
            parser.parseArgument(args);
            start(getBasePath(), candidate);
        } catch (IOException | ProfileOperationException | CmdLineException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Adds system plugin
     */
    public static void addSystemPlugin(Plugin pl) throws PluginException, IOException {
        addPlugin(pl, systemPlugins, workspaceComponents);
    }

    /**
     * Adds system plugins
     */
    public static void addSystemPlugins(List<Plugin> pls) {
        addPlugins(pls, systemPlugins, workspaceComponents);
    }

    /**
     * Adds user plugins
     */
    public static void addUserPlugins(List<Plugin> pls) {
        addPlugins(pls, userPlugins, workspaceUserComponents);
    }

    /**
     * Adds user plugin.
     */
    private static void addPlugin(Plugin pl,
                                     Set<Plugin> plugins,
                                     Collection<WorkspaceComponent> components)
            throws PluginException, IOException {
        if (pl != null && plugins != null && components != null) {
            plugins.add(pl);
            Object instance = pl.newInstance();
            // initialize ui
            if (instance instanceof UI) {
                Workspace.ui = (UI) instance;
            }
            // collect all components
            if (instance instanceof WorkspaceComponent) {
                WorkspaceComponent workspaceComponent = (WorkspaceComponent) instance;
                workspaceComponent.load();
                components.add(workspaceComponent);
            }
        }
    }

    /**
     * Adds user plugins
     */
    private static void addPlugins(List<Plugin> pls,
                                  Set<Plugin> plugins,
                                  Collection<WorkspaceComponent> components) {
        for (Plugin pl : pls) {
            try {
                addPlugin(pl, plugins, components);
            } catch (PluginException | IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Add listener for service events.
     */
    public static boolean addListener(IWorkspaceListener l) {
        if (l != null && !workspaceListeners.contains(l)) {
            return workspaceListeners.add(l);
        }
        return false;
    }

    /**
     * Remove workspace listener
     */
    public static boolean removeListener(IWorkspaceListener l) {
        return workspaceListeners.remove(l);
    }

    /**
     * Deliver event to all the listeners
     */
    public static synchronized void fireEvent(Integer event, Object lparam, Object rparam) {

        for (IWorkspaceListener listener : workspaceListeners) {
            if (event == listener.getCode()) {
                listener.processEvent(event, lparam, rparam);
            }
        }
    }

    /**
     * Returns class implemented interface <code>jworkspace.kernel.RuntimeManager</code>
     */
    public static RuntimeManager getRuntimeManager() {
        return RuntimeManager.getInstance();
    }

    /**
     * Returns class, implemented interface <code>jworkspace.api.IUserProfileEngine</code>
     */
    public static IUserManager getUserManager() {
        return WorkspaceUserManager.getInstance();
    }

    /**
     * Returns class, implemented interface
     * <code>jworkspace.api.InstallEngine</code>
     */
    public static synchronized IWorkspaceInstaller getWorkspaceInstaller() throws IOException {
        if (workspaceInstaller == null) {
            workspaceInstaller = new WorkspaceInstaller(
                getUserManager().ensureCurrentProfilePath(getBasePath()).toFile());
        }
        return workspaceInstaller;
    }

    /**
     * Get base path for workspace properties. It is commonly an operating system's user's directory
     */
    public static synchronized Path getBasePath() {
        if (basePath == null) {
            String home = System.getProperty("user.home");
            basePath = Paths.get(home, ".jworkspace");
        }
        return basePath;
    }

    /**
     *
     * @return path to a current user profile folder
     * @throws IOException in case current profile is null
     */
    public static Path getUserHomePath() throws IOException {
        return Workspace.getUserManager().ensureCurrentProfilePath(getBasePath());
    }

    /**
     * Returns resource manager for the workspace.
     *
     * @return kiwi.util.WorkspaceResourceManager
     */
    public static synchronized WorkspaceResourceManager getResourceManager() {
        if (resourceManager == null) {
            resourceManager = new WorkspaceResourceManager(WorkspaceResourceAnchor.class);
        }
        return resourceManager;
    }

    /**
     * Returns workspace VERSION
     */
    public static String getVersion() {
        return IConstants.VERSION;
    }

    /**
     * Returns class of interface <code>jworkspace.api.UI</code>
     */
    public static UI getUi() {
        return ui;
    }

    public static synchronized void start(@NonNull Path baseDir, @NonNull Profile candidate)
        throws IOException, ProfileOperationException {

        long start = System.currentTimeMillis();
        /*
         * Set the base path
         */
        Workspace.setBasePath(baseDir);
        /*
         * Add system plugins indifferent to users data
         */
        addSystemPlugins(new WorkspacePluginLocator().loadPlugins(Paths.get(baseDir.toAbsolutePath().toString(),
            IConstants.PLUGINS_DIRECTORY))
        );
        /*
         * Initialize user data
         */
        initUserWorkspace(candidate);

        long end = System.currentTimeMillis();
        LOG.info("Started in: " + (end - start) + " millis");
    }
    /**
     * Start a new workspace and read all the data from the given directory
     *
     * @param baseDir to store all workspace data in
     */
    public static synchronized void start(@NonNull Path baseDir, @NonNull String username, @NonNull String password)
        throws ProfileOperationException, IOException {

        start(baseDir, Profile.create(username, password, "", "", ""));
    }

    private static void initUserWorkspace(@NonNull Profile candidate)
            throws IOException, ProfileOperationException {
        /*
         * Login procedure starts here
         */
        getUserManager().login(candidate);
        /*
         * If login failed - silently leave the basic system running
         */
        if (!getUserManager().userLogged()) {
            LOG.warn("User hasn't been logged in");
            return;
        }
        /*
         * User logged past this line - start loading user components
         */
        addUserPlugins(new WorkspacePluginLocator()
            .loadPlugins(
                Paths.get(getUserManager().ensureCurrentProfilePath(getBasePath()).toString(),
                    IConstants.PLUGINS_DIRECTORY)
            )
        );
        /*
         * Load the installer database
         */
        getWorkspaceInstaller().load();
    }

    static void removeUserWorkspace() throws IOException {
        /*
           Shut down all the running processes
         */
        RuntimeManager.getInstance().killAllProcesses();
        /*
         * Save and clear all user components loaded as plugins
         */
        for (WorkspaceComponent workspaceComponent : workspaceUserComponents) {
            workspaceComponent.save();
            workspaceComponent.reset();
        }
        workspaceUserComponents.clear();

        /*
         * Remove installer
         */
        getWorkspaceInstaller().save();
        workspaceInstaller = null;
        /*
         * Logout the user
         */
        getUserManager().logout();
    }

    /**
     * User login and logout procedures.
     */
    public static void changeCurrentProfile(@NonNull String username, @NonNull String password)
            throws IOException, ProfileOperationException {
        /*
         * Logout the previous user
         */
        removeUserWorkspace();
        /*
         * Initialize user data
         */
        initUserWorkspace(Profile.create(username, password, "", "", ""));
    }

    /**
     * Exits workspace.
     */
    public static void exit() {
        /*
         * Logout the previous user
         */
        try {
            removeUserWorkspace();
            /*
             * Save and clear all system components loaded as plugins
             */
            for (WorkspaceComponent workspaceComponent : workspaceComponents) {
                workspaceComponent.save();
                workspaceComponent.reset();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        System.exit(0);

    }
}