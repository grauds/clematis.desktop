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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.plugin.Plugin;

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
import jworkspace.users.ProfileOperationException;
import jworkspace.users.WorkspaceUserManager;
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
    @Setter
    private static Path basePath;

    /**
     * These are actually invisible services, that are loaded by system at startup and
     * unloaded on finish. These services are for all users in system and must implement WorkspaceComponent
     * interface to be plugged into workspace lifecycle.
     */
    private static Set<Plugin<WorkspaceComponent>> systemPlugins = new HashSet<>();
    /**
     * These are actually invisible services, that are loaded by user at login or later manually
     * and unloaded on logout. These services are NOT for all users in system.
     */
    private static Set<Plugin<WorkspaceComponent>> userPlugins = new HashSet<>();
    /**
     * Workspace components list
     */
    private static Collection<WorkspaceComponent> workspaceComponents = new Vector<>();
    /**
     * Resource manager
     */
    private static WorkspaceResourceManager resourceManager = new WorkspaceResourceManager();
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
     * todo: parse command line arguments
     *
     * @param args an array of command-line arguments
     */
    public static void main(String[] args) {
        try {
            start(getBasePath(), "root", "");
        } catch (IOException | ProfileOperationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Adds system plugin
     */
    public static void addSystemPlugin(Plugin<WorkspaceComponent> pl) {
        systemPlugins.add(pl);
    }

    /**
     * Adds system plugins
     */
    public static void addSystemPlugins(List<Plugin<WorkspaceComponent>> pls) {
        for (Plugin<WorkspaceComponent> pl : pls) {
            addSystemPlugin(pl);
        }
    }

    /**
     * Adds user plugin.
     */
    public static void addUserPlugin(Plugin<WorkspaceComponent> pl) {
        userPlugins.add(pl);
    }

    /**
     * Adds user plugins
     */
    public static void addUserPlugins(List<Plugin<WorkspaceComponent>> pls) {
        for (Plugin<WorkspaceComponent> pl : pls) {
            addUserPlugin(pl);
        }
    }

    /**
     * Exits workspace.
     */
    public static void exit() {

        RuntimeManager.getInstance().killAllProcesses();

        if (Workspace.getUserManager().userLogged()) {
            try {
                getWorkspaceInstaller().save();
                WorkspaceUserManager.getInstance().logout();
            } catch (Exception e) {
                LOG.error(WorkspaceResourceAnchor.getString("Workspace.logout.failure"), e);
            }
        }
        System.exit(0);
    }

    /**
     * Add listener for service events.
     */
    public static void addListener(IWorkspaceListener l) {
        workspaceListeners.add(l);
    }

    /**
     * Remove workspace listener
     */
    public static void removeListener(IWorkspaceListener l) {
        workspaceListeners.remove(l);
    }

    /**
     * Deliver event to all the listeners
     */
    public static synchronized void fireEvent(Object event, Object lparam, Object rparam) {

        for (IWorkspaceListener listener : workspaceListeners) {
            if (event instanceof Integer && (Integer) event == listener.getCode()) {
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
    public static WorkspaceResourceManager getResourceManager() {
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

    /**
     * Start a new workspace and read all the data from the given directory
     *
     * @param baseDir to store all workspace data in
     */
    public static synchronized void start(@NonNull Path baseDir, @NonNull String username, @NonNull String password)
        throws IOException, ProfileOperationException {

        long start = System.currentTimeMillis();

        Workspace.setBasePath(baseDir);
        /*
         * Add system plugins indifferent to users data
         */
        addSystemPlugins(new WorkspacePluginLocator<WorkspaceComponent>()
            .loadPlugins(Paths.get(baseDir + IConstants.PLUGINS_DIRECTORY))
        );

        /*
         * Login procedure starts here
         */
        getUserManager().login(username, password);

        /*
         * If login failed - silently leave the basic system running
         */
        if (!getUserManager().userLogged()) {
            LOG.warn("User hasn't been logged in");
            return;
        }

        /*
         * Initialize user data
         */
        initUserWorkspace();

        /*
         * Run Workspace Bean Shell interpreter instance
         */
        WorkspaceInterpreter.getInstance();

        long end = System.currentTimeMillis();
        LOG.info("Started in: " + (end - start) + " millis");
    }

    private static void initUserWorkspace() throws IOException {
        /*
         * User logged past this line - start loading user components
         */
        addUserPlugins(new WorkspacePluginLocator<WorkspaceComponent>()
            .loadPlugins(
                Paths.get(getUserManager().ensureCurrentProfilePath(getBasePath()).toString(),
                    IConstants.PLUGINS_DIRECTORY)
            )
        );
    }

    /**
     * User login and logout procedures.
     */
    public static void changeCurrentProfile(@NonNull String username, @NonNull String password)
        throws Exception {

        getUserManager().logout();

        workspaceInstaller = null;

        getUserManager().login(username, password);
        /*
         * Initialize user data
         */
        initUserWorkspace();
    }
}