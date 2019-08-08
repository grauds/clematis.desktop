package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

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

import jworkspace.LangResource;
import jworkspace.api.IEngine;
import jworkspace.api.IUserProfileEngine;
import jworkspace.api.IWorkspaceListener;
import jworkspace.api.InstallEngine;
import jworkspace.api.UI;
import jworkspace.installer.WorkspaceInstaller;
import jworkspace.ui.DefaultUI;
import jworkspace.ui.WorkspaceResourceManager;
import jworkspace.users.UserProfileEngine;

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
     * Runtime manager
     */
    private static final RuntimeManager RUNTIME_MANAGER = new RuntimeManager();
    /**
     * SYSTEM PLUGINS. This is actually invisible services,
     * that are loaded by system at startup and unloaded on finish.
     * These services are for all users in system.
     * <p>
     * Each one is executed in its own thread and has its own security manager
     */
    private static final Set<Plugin> SYSTEM_PLUGINS = new HashSet<>();
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
    private static final Collection<IEngine> ENGINES = new Vector<>();
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

    private static final String PLUGINS = "plugins";

    private static final String WORKSPACE_ENGINE_LOAD_FAILED = "Workspace.engine.loadFailed";

    private static final String WORKSPACE_ENGINE_SAVE_FAILED = "Workspace.engine.saveFailed";

    private static final String EMPTY_STRING = "#\n";

    /**
     * Workspace Bean Shell interpreter instance
     */
    private static WorkspaceInterpreter workspaceInterpreter;

    /**
     * User profile engine interface
     */
    private static IUserProfileEngine profilesEngine = new UserProfileEngine();

    /**
     * Installer
     */
    private static InstallEngine installEngine = new WorkspaceInstaller();

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
     * Adds SYSTEM PLUGIN.
     */
    private static void addSystemPlugin(Plugin pl) {
        SYSTEM_PLUGINS.add(pl);
    }

    /**
     * Adds SYSTEM PLUGINS
     */
    private static void addSystemPlugins(Plugin[] pls) {
        for (Plugin pl : pls) {
            addSystemPlugin(pl);
        }
    }

    /**
     * Adds USER PLUGIN.
     */
    private static void addUserPlugin(Plugin pl) {
        USER_PLUGINS.add(pl);
    }

    /**
     * Adds USER PLUGINS
     */
    private static void addUserPlugins(Plugin[] pls) {
        for (Plugin pl : pls) {
            addUserPlugin(pl);
        }
    }

    /**
     * Save ui preferences and reset components to their original state
     */
    private static void saveAndResetUI() {

        try {
            ui.save();
            ui.reset();
        } catch (IOException ex) {
            Workspace.ui.showError(ui.getName() + WHITESPACE
                + LangResource.getString("Workspace.logOff.saveFailed"), ex);
        }
    }

    /**
     * Exits workspace.
     */
    public static void exit() throws WorkspaceException {

        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().getImage("user_exit.png"));

        if (ui.showConfirmDialog(LangResource.getString("Workspace.exit.question"),
            LangResource.getString("Workspace.exit.title"),
            icon)) {

            RuntimeManager.killAllProcesses();

            if (Workspace.getProfiles().userLogged()) {

                saveAndResetUI();
                saveEngines();

                try {
                    profilesEngine.logout();
                    profilesEngine.save();
                } catch (Exception e) {
                    Workspace.ui.showError(LangResource.getString("Workspace.logout.failure"), e);
                }
            }
            Workspace.LOG.info("> 1999 - 2019 Copyright Anton Troshin");
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
     * Deliver even
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
    public static InstallEngine getInstallEngine() {
        return installEngine;
    }

    /**
     * Returns class, implemented interface <code>jworkspace.api.IUserProfileEngine</code>
     */
    public static IUserProfileEngine getProfiles() {
        return profilesEngine;
    }

    /**
     * Get base path for workspace properties. It is commonly an operating system's user's directory
     */
    public static String getBasePath() {
        String home = System.getProperty("user.home");
        return home + File.separator + ".jworkspace" + File.separator;
    }

    public static String getUserHomePath() throws IOException {
        return getBasePath() + Workspace.getProfiles().getCurrentProfileRelativePath();
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
        return RUNTIME_MANAGER;
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

    private static void loadEngines() {
        for (IEngine engine : ENGINES) {
            try {
                engine.load();
            } catch (IOException ex) {
                String name = engine.getName();
                Workspace.ui.showError(name
                    + WHITESPACE + LangResource.getString(WORKSPACE_ENGINE_LOAD_FAILED), ex);
                Workspace.LOG.error(name
                    + WHITESPACE + LangResource.getString(WORKSPACE_ENGINE_LOAD_FAILED
                    + ex.toString()));
            }
        }
    }

    private static void saveEngines() throws WorkspaceException {
        for (IEngine engine : ENGINES) {
            try {
                engine.save();
                engine.reset();
            } catch (IOException ex) {
                String name = engine.getName();
                Workspace.LOG.error(name
                    + WHITESPACE
                    + LangResource.getString(WORKSPACE_ENGINE_SAVE_FAILED
                    + ex.toString()));
                throw new WorkspaceException(name
                    + WHITESPACE
                    + LangResource.getString(WORKSPACE_ENGINE_SAVE_FAILED), ex);
            }
        }
    }

    private static void loadUserPlugins() throws IOException {

        String fileName = Workspace.getUserHomePath() + PLUGINS;
        addUserPlugins(Workspace.getRuntimeManager().loadPlugins(fileName));
    }

    /**
     * Check for unsaved user data in gui and asks user to save data or not.
     */
    private static boolean isModified() {
        return ui.isModified();
    }

    /**
     * Starts the application.
     *
     * @param args an array of command-line arguments
     */
    public static void main(java.lang.String[] args) {

        int paramLength = args.length;

        for (String par : args) {

            if (par.equalsIgnoreCase("-locales")) {
                LangResource.printAvailableLocales();
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

    static void start(String[] args) {

        long start = System.currentTimeMillis();
        Workspace.LOG.info("> Starting" + WHITESPACE + Workspace.getVersion());

        initSystem();

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
     * Load plugins from root directory
     */
    private static void initSystem() {

        Workspace.LOG.info("> Loading " + Workspace.getVersion());
        /*
         * Add installer to engines to make it sensitive for user data
         */
        Workspace.ENGINES.add(Workspace.getInstallEngine());
        /*
         * Start interpreter
         */
        workspaceInterpreter = WorkspaceInterpreter.getInstance();
        /*
         * Add system plugins indifferent to users data
         */
        addSystemPlugins(Workspace.getRuntimeManager().loadPlugins(PLUGINS));
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
                Workspace.LOG.info(LangResource.getString("message#21"));
            }
        }
        /*
         * Login procedure with:
         *  -qlogin [profile name]
         */
        try {
            profilesEngine.load();
        } catch (IOException ex) {
            Workspace.ui.showError(LangResource.getString("Workspace.load.profilerFailure"), ex);
            System.exit(-3);
        }

        if (quickLogin != null) {
            try {
                Workspace.LOG.info("Logging in as " + quickLogin);
                profilesEngine.login(quickLogin, "");
                Workspace.LOG.info("Logged in as " + quickLogin);
            } catch (Exception ex) {
                Workspace.LOG.error("Can't log in as " + quickLogin);
                Workspace.ui.showError(LangResource.getString(WORKSPACE_LOGIN_FAILURE), ex);
                quickLogin = null;
            }
        }

        if (quickLogin == null) {
            profilesEngine.getLoginDlg().setVisible(true);
        }
        /*
         * If login failed - silently leave the basic system running
         */
        if (!profilesEngine.userLogged()) {
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
            Workspace.ui.showError(LangResource.getString("Workspace.load.abort"), e);
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
            loadEngines();
            loadUserPlugins();
            ui.load();
        } catch (IOException ex) {
            Workspace.ui.showError(ui.getName() + WHITESPACE
                + LangResource.getString(WORKSPACE_LOGIN_LOAD_FAILED), ex);
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

        if ((isModified() && ui.showConfirmDialog(LangResource.getString("Workspace.logOff.question")
                + WHITESPACE + getProfiles().getUserName() + " ?",
            LangResource.getString("Workspace.logOff.title"), icon)) || !isModified()) {

            RuntimeManager.killAllProcesses();

            saveAndResetUI();
            saveEngines();
            USER_PLUGINS.clear();

            getRuntimeManager().resetPluginsCache();

            try {
                profilesEngine.logout();
                profilesEngine.getLoginDlg().setVisible(true);
            } catch (Exception e) {
                Workspace.ui.showError(LangResource.getString(WORKSPACE_LOGIN_FAILURE), e);
                return;
            }
            try {
                loadEngines();
                loadUserPlugins();
                ui.load();
            } catch (IOException ex) {
                Workspace.ui.showError(ui.getName() + WHITESPACE
                    + LangResource.getString(WORKSPACE_LOGIN_LOAD_FAILED), ex);
            }
        }
    }
}