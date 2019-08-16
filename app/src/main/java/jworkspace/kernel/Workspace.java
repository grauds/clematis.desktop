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

import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.Config;
import com.hyperrealm.kiwi.util.plugin.Plugin;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IUserManager;
import jworkspace.api.IWorkspaceInstaller;
import jworkspace.api.IWorkspaceListener;
import jworkspace.api.UI;
import jworkspace.api.WorkspaceComponent;
import jworkspace.api.WorkspaceException;
import jworkspace.installer.WorkspaceInstaller;
import jworkspace.ui.DefaultUI;
import jworkspace.ui.WorkspaceResourceManager;
import jworkspace.users.UserManager;

/**
 * Workspace is a core class for Java Workspace
 *
 * @author Anton Troshin
 */
@SuppressWarnings("unused")
public class Workspace {

    /**
     * Version
     */
    private static final String VERSION = "Clematis 2.0";

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Workspace.class);

    /**
     * The name of configuration file
     */
    private static final String CONFIG_JWCONF_CFG = "config/jwconf.cfg";

    /**
     * Listeners for service events.
     */
    private static final List<IWorkspaceListener> LISTENERS = new Vector<>();
    /**
     * The directory where Workspace will be looking for plugins
     * todo: Make configurable
     */
    private static final String PLUGINS_DIRECTORY = "plugins";
    /**
     * SYSTEM PLUGINS. This is actually invisible services, that are loaded by system at startup and
     * unloaded on finish. These services are for all users in system and must implement WorkspaceComponent
     * interface to be plugged into workspace lifecycle.
     */
    private static final Set<Plugin<WorkspaceComponent>> SYSTEM_PLUGINS = new HashSet<>();
    /**
     * USER PLUGINS. This is actually invisible services,
     * that are loaded by user at login or later manually and unloaded on logout.
     * These services are NOT for all users in system.
     * <p>
     * Each one is executed in its own thread and has its own security manager
     */
    private static final Set<Plugin> USER_PLUGINS = new HashSet<>();
    /**
     * Workspace ENGINES in a synchronized list
     */
    private static final Collection<WorkspaceComponent> COMPONENTS = new Vector<>();
    /**
     * Resource manager.
     */
    private static final WorkspaceResourceManager RESOURCE_MANAGER = new WorkspaceResourceManager();
    /**
     * String constants
     */
    private static final String WORKSPACE_LOGIN_FAILURE = "Workspace.login.failure";

    private static final String WORKSPACE_LOGIN_LOAD_FAILED = "Workspace.login.loadFailed";

    private static final String WHITESPACE = " ";

    private static final String WORKSPACE_ENGINE_LOAD_FAILED = "Workspace.engine.loadFailed";

    private static final String WORKSPACE_ENGINE_SAVE_FAILED = "Workspace.engine.saveFailed";

    private static final String EMPTY_STRING = "#\n";

    /**
     * Installer
     */
    private static IWorkspaceInstaller workspaceInstaller = new WorkspaceInstaller();

    /**
     * Java Workspace UI, selectable by users
     */
    private static UI ui = new DefaultUI();

    /**
     * UI clazz name
     */
    private static String guiClassName = null;

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

        int paramLength = args.length;

        for (String par : args) {

            if (par.equalsIgnoreCase("-locales")) {
                WorkspaceResourceAnchor.printAvailableLocales();
                if (paramLength == 1) {
                    System.exit(0);
                }
            } else if (par.equalsIgnoreCase("-VERSION")) {
                if (paramLength == 1) {
                    System.exit(0);
                }
            }
        }

        start(args);
    }
    
    /**
     * Adds SYSTEM PLUGIN.
     */
    public static void addSystemPlugin(Plugin<WorkspaceComponent> pl) {
        SYSTEM_PLUGINS.add(pl);
    }

    /**
     * Adds SYSTEM PLUGINS
     */
    public static void addSystemPlugins(List<Plugin<WorkspaceComponent>> pls) {
        for (Plugin<WorkspaceComponent> pl : pls) {
            addSystemPlugin(pl);
        }
    }

    /**
     * Adds USER PLUGIN.
     */
    public static void addUserPlugin(Plugin pl) {
        USER_PLUGINS.add(pl);
    }

    /**
     * Adds USER PLUGINS
     */
    public static void addUserPlugins(Plugin[] pls) {
        for (Plugin pl : pls) {
            addUserPlugin(pl);
        }
    }

    /**
     * Exits workspace.
     */
    public static void exit() throws WorkspaceException {

        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().getImage("user_exit.png"));

        if (ui.showConfirmDialog(WorkspaceResourceAnchor.getString("Workspace.exit.question"),
            WorkspaceResourceAnchor.getString("Workspace.exit.title"),
            icon)) {

            RuntimeManager.killAllProcesses();

            if (Workspace.getUserManager().userLogged()) {

               // saveAndResetUI();
              //  saveEngines();

                try {
                    UserManager.getInstance().logout();
                    UserManager.getInstance().save();
                } catch (Exception e) {
                    Workspace.ui.showError(WorkspaceResourceAnchor.getString("Workspace.logout.failure"), e);
                }
            }
            System.exit(0);
        }
    }

    /**
     * Add listener for service events.
     */
    public static void addListener(IWorkspaceListener l) {
        LISTENERS.add(l);
    }

    /**
     * Remove workspace listener
     */
    public static void removeListener(IWorkspaceListener l) {
        LISTENERS.remove(l);
    }

    /**
     * Deliver event to all the listeners
     */
    public static synchronized void fireEvent(Object event, Object lparam, Object rparam) {

        for (IWorkspaceListener listener : LISTENERS) {
            if (event instanceof Integer && (Integer) event == listener.getCode()) {
                listener.processEvent(event, lparam, rparam);
            }
        }
    }

    /**
     * Returns class, implemented interface
     * <code>jworkspace.api.InstallEngine</code>
     */
    public static IWorkspaceInstaller getWorkspaceInstaller() {
        return workspaceInstaller;
    }

    /**
     * Returns class, implemented interface <code>jworkspace.api.IUserProfileEngine</code>
     */
    public static IUserManager getUserManager() {
        return UserManager.getInstance();
    }

    /**
     * Get base path for workspace properties. It is commonly an operating system's user's directory
     */
    public static String getBasePath() {
        String home = System.getProperty("user.home");
        return home + File.separator + ".jworkspace" + File.separator;
    }

    public static String getUserHomePath() throws IOException {
        return getBasePath() + Workspace.getUserManager().getCurrentProfileRelativePath();
    }

    /**
     * Returns resource manager for the workspace.
     *
     * @return kiwi.util.WorkspaceResourceManager
     */
    public static WorkspaceResourceManager getResourceManager() {
        return RESOURCE_MANAGER;
    }

    /**
     * Returns class implemented interface
     * <code>jworkspace.kernel.RuntimeManager</code>
     */
    public static RuntimeManager getRuntimeManager() {
        return RuntimeManager.getInstance();
    }

    /**
     * Returns workspace VERSION
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Returns class of interface <code>jworkspace.api.UI</code>
     */
    public static UI getUi() {
        return ui;
    }

    /**
     * Find service by type of implemented interface from kernel. Method is needed to get
     * reference to object, loaded dynamically as service. Usually this should be called by
     * ui shells, that take advantage of service functionality.
     */
    private static Object getService(String clazzName) {

        Iterator it = SYSTEM_PLUGINS.iterator();

        Plugin plugin = null;

        while (it.hasNext()) {
            plugin = (Plugin) it.next();
            if (plugin.getClassName().equals(clazzName)) {
                break;
            }
        }

        it = USER_PLUGINS.iterator();

        while (it.hasNext()) {
            plugin = (Plugin) it.next();
            if (plugin.getClassName().equals(clazzName)) {
                break;
            }
        }

        return plugin;
    }

    /**
     * Find service by type of implemented interface from kernel. Method is needed to get
     * reference to object, loaded dynamically as service. Usually this should be called by
     * ui shells, that take advantage of service functionality.
     */
    public static Object getService(Class clazz) {

        return clazz != null ? getService(clazz.getCanonicalName()) : null;
    }

    private static void loadUserPlugins() throws IOException {

     //   String fileName = Workspace.getUserHomePath() + PLUGINS;
       // addUserPlugins(WorkspacePluginLocator.loadPlugins(fileName));
    }

    /**
     * Check for unsaved user data in gui and asks user to save data or not.
     */
    private static boolean isModified() {
        return ui.isModified();
    }


    public static void start(String[] args) {

        long start = System.currentTimeMillis();
        /*
         * Workspace Bean Shell interpreter instance
         */
        WorkspaceInterpreter.getInstance();
        /*
         * Add system plugins indifferent to users data
         */
        addSystemPlugins(new WorkspacePluginLocator<WorkspaceComponent>().loadPlugins(PLUGINS_DIRECTORY));

        try {
            initUserWorkspace(args);
        } catch (Throwable e) {
            Workspace.LOG.error("Failed to init Java Workspace " + e.toString());
            Workspace.ui.showError("Failed to init Java Workspace", e);
            System.exit(-1);
        }

        long end = System.currentTimeMillis();
        LOG.info("Started in: " + (end - start) + " millis");
    }

    /**
     * Get configuration header for jwconfig.cfg file.
     *
     * @return configuration header
     */
    private static StringBuffer getConfigHeader() {

        StringBuffer sb = new StringBuffer();
        sb.append(EMPTY_STRING);
        sb.append("# Java Workspace kernel configuration. This file configures classes for\n");
        sb.append("# three kernel engines. If any of these classes are incorrectly specified,\n");
        sb.append("# kernel will resort to the default classes.\n");
        sb.append(EMPTY_STRING);

        return sb;
    }

    /**
     * This method is responsible for Workspace initialization.
     * It performs full startup procedure and logs on user.
     */
    @SuppressWarnings("MagicNumber")
    private static void initUserWorkspace(String[] args) {

        int paramLength = args != null ? args.length : 0;

        String quickLogin = null;

        for (int i = 0; i < paramLength; i++) {
            String par = args[i];
            if (par.equalsIgnoreCase("-qlogin")) {
                quickLogin = args[++i];
            } else if (par.equalsIgnoreCase("-debug")) {
                System.setProperty("debug", "true");
                Workspace.LOG.info(WorkspaceResourceAnchor.getString("message#21"));
            }
        }
        /*
         * Login procedure with:
         *  -qlogin [profile name]
         */
        try {
            getUserManager().load();
        } catch (IOException ex) {
            Workspace.ui.showError(WorkspaceResourceAnchor.getString("Workspace.load.profilerFailure"), ex);
            System.exit(-3);
        }

        if (quickLogin != null) {
            try {
                Workspace.LOG.info("Logging in as " + quickLogin);
                getUserManager().login(quickLogin, "");
                Workspace.LOG.info("Logged in as " + quickLogin);
            } catch (Exception ex) {
                Workspace.LOG.error("Can't log in as " + quickLogin);
                Workspace.ui.showError(WorkspaceResourceAnchor.getString(WORKSPACE_LOGIN_FAILURE), ex);
                quickLogin = null;
            }
        }

//        if (quickLogin == null) {
//           // profilesEngine.getLoginDlg().setVisible(true);
//        }
        /*
         * If login failed - silently leave the basic system running
         */
        if (!getUserManager().userLogged()) {
            return;
        }

        Config cfg = new Config(getConfigHeader().toString());

        try (InputStream in = new FileInputStream(getUserHomePath() + CONFIG_JWCONF_CFG)) {

            cfg.load(in);
        } catch (IOException e) {
            // just use defaults, don't panic
            Workspace.LOG.error("Couldn't find custom configuration file: "
                + CONFIG_JWCONF_CFG + ". Continuing with defaults", e);
        }
        /*
         * Either read from configuration or go with default
         */
        Workspace.guiClassName = cfg.getString("ui", "jworkspace.ui.DefaultUI");
        Workspace.LOG.info("Loading user interface from: " + Workspace.guiClassName);

        try {
            Class c = Class.forName(Workspace.guiClassName);
            if (!c.isInterface()) {
                ui = (UI) c.newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Workspace.ui.showError(WorkspaceResourceAnchor.getString("Workspace.load.abort"), e);
            System.exit(-1);
        }

        /*
         * User logged past this line - show splash screen and start loading user components
         */
        Window logo = ui.getLogoScreen();
        if (logo != null) {
            logo.setVisible(true);
        }

        try {
            workspaceInstaller = new WorkspaceInstaller(new File(getUserHomePath()));
            //loadEngines();
            loadUserPlugins();
            ui.load();
        } catch (IOException ex) {
            Workspace.ui.showError(ui.getName() + WHITESPACE
                + WorkspaceResourceAnchor.getString(WORKSPACE_LOGIN_LOAD_FAILED), ex);
        }

        if (logo != null) {
            logo.dispose();
        }
    }

    /**
     * User login and logout procedures.
     */
    public static void changeCurrentProfile() throws WorkspaceException {
        /*
         * Check if any unsaved data exists.
         */
        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().getImage("user_change.png"));

        if ((isModified() && ui.showConfirmDialog(WorkspaceResourceAnchor.getString("Workspace.logOff.question")
                + WHITESPACE + getUserManager().getUserName() + " ?",
            WorkspaceResourceAnchor.getString("Workspace.logOff.title"), icon)) || !isModified()) {

            RuntimeManager.killAllProcesses();

            //saveAndResetUI();
            //saveEngines();
            USER_PLUGINS.clear();

            try {
                getUserManager().logout();
              //  profilesEngine.getLoginDlg().setVisible(true);
            } catch (Exception e) {
                Workspace.ui.showError(WorkspaceResourceAnchor.getString(WORKSPACE_LOGIN_FAILURE), e);
                return;
            }
            try {
                //loadEngines();
                loadUserPlugins();
                ui.load();
            } catch (IOException ex) {
                Workspace.ui.showError(ui.getName() + WHITESPACE
                    + WorkspaceResourceAnchor.getString(WORKSPACE_LOGIN_LOAD_FAILED), ex);
            }
        }
    }
}