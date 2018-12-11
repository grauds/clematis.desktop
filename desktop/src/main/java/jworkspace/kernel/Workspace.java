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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

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

/**
 * Workspace is a core class for Java Workspace, which dispatches messages and commands from users. This class also
 * initializes and manages ENGINES, it performs such tasks as loading and saving of user settings,
 * responding to command processor and managing plugins.
 *
 * @author Anton Troshin
 */
@SuppressWarnings("unused")
public final class Workspace {

    /**
     * Java Workspace UI
     */
    static UI ui = null;

    /**
     * Version
     */
    private static final String VERSION = "Clematis 2.0";

    /**
     * Listeners for service events.
     */
    private static final Vector<IWorkspaceListener> LISTENERS = new Vector<>();

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Workspace.class);

    /**
     * The name of configuration file
     */
    private static final String CONFIG_JWCONF_CFG = "config/jwconf.cfg";
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
     * LOADED COMPONENTS. These components are not managed as
     * services, but are stored under Object keys to gain access
     * every time user needs a reference.
     */
    private static final Map<String, Object> LOADED_COMPONENTS = new HashMap<>();
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

    private static final String USER_DIR = "user.dir";

    private static final String WHITESPACE = " ";

    private static final String PLUGINS = "plugins";

    private static final String WORKSPACE_ENGINE_LOAD_FAILED = "Workspace.engine.loadFailed";

    private static final String WORKSPACE_ENGINE_SAVE_FAILED = "Workspace.engine.saveFailed";

    private static final String EMPTY_STRING = "#\n";

    /**
     * User profile engine interface
     */
    private static IUserProfileEngine profilesEngine = null;

    /**
     * User profile engine clazz name.
     */
    private static String usersAuthenticationClassName = null;

    /**
     * Installer
     */
    private static InstallEngine installEngine = null;

    /**
     * Installer clazz name
     */
    private static String installerClassName = null;

    /**
     * UI clazz name
     */
    private static String guiClassName = null;

    /**
     * Class is never instantiated
     */
    private Workspace() {
    }

    /**
     * Add ENGINE
     */
    private static void addEngine(IEngine engine) {
        ENGINES.add(engine);
    }

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
     * User login and logout procedures.
     */
    public static void changeCurrentProfile() {
        /*
         * Check if any unsaved data exists.
         */
        if (isGUIModified()) {
            JOptionPane.showMessageDialog(ui.getFrame(), "Please save data before logging out");
        } else {

            ImageIcon icon = new ImageIcon(Workspace.getResourceManager().getImage("user_change.png"));

            int result = JOptionPane.showConfirmDialog(ui.getFrame(),
                LangResource.getString("Workspace.logOff.question")
                    + WHITESPACE + getProfilesEngine().getUserName() + " ?",
                LangResource.getString("Workspace.logOff.title"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);


            if (result == JOptionPane.YES_OPTION) {

                killAllProcesses();

                saveAndResetUI();
                saveEngines();
                USER_PLUGINS.clear();
                removeAllRegisteredComponents();

                getRuntimeManager().resetPluginsCache();

                try {
                    profilesEngine.logout();
                    profilesEngine.getLoginDlg().setVisible(true);
                } catch (Exception e) {
                    Workspace.ui.showError(LangResource.getString(WORKSPACE_LOGIN_FAILURE), e);
                    return;
                }

                loadEngines();

                USER_PLUGINS.clear();

                loadUserPlugins();

                try {
                    ui.load();
                } catch (IOException ex) {
                    Workspace.ui.showError(ui.getName() + WHITESPACE
                        + LangResource.getString(WORKSPACE_LOGIN_LOAD_FAILED), ex);
                }
            }
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
    public static void exit() {

        /*
         * Check if any unsaved data exists.
         */
        if (isGUIModified()) {
            JOptionPane.showMessageDialog(ui.getFrame(), "Please save data before leaving");
            return;
        }

        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().getImage("exit.png"));

        int result = JOptionPane.showConfirmDialog(ui.getFrame(),
            LangResource.getString("Workspace.exit.question"),
            LangResource.getString("Workspace.exit.title"),
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);

        if (result == JOptionPane.YES_OPTION) {

            killAllProcesses();

            if (Workspace.getProfilesEngine().userLogged()) {

                saveAndResetUI();
                saveEngines();

                try {
                    profilesEngine.logout();
                    profilesEngine.save();
                } catch (Exception e) {
                    Workspace.ui.showError(LangResource.getString("Workspace.logout.failure"), e);
                }
            }
            Workspace.LOG.info("> 1999 - 2018 Copyright Anton Troshin");
            System.exit(0);
        }
    }

    /**
     * Add listener for service events.
     */
    private static void addListener(IWorkspaceListener l) {
        LISTENERS.add(l);
    }

    /**
     * Remove workspace listener
     */
    public static void removeListener(IWorkspaceListener l) {
        LISTENERS.removeElement(l);
    }

    /**
     * Deliver event to all LISTENERS.
     */
    public static synchronized void fireEvent(Object event, Object lparam, Object rparam) {

        for (IWorkspaceListener listener : LISTENERS) {
            listener.processEvent(event, lparam, rparam);
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
     * Register component in kernel.
     */
    public static void register(String key, Object component) {
        if (key != null && component != null) {
            LOADED_COMPONENTS.put(key, component);
        }
    }

    /**
     * Get registered component
     */
    public static Object getRegisteredComponent(String key) {
        return LOADED_COMPONENTS.get(key);
    }

    /**
     * Empty all components
     */
    private static void removeAllRegisteredComponents() {
        LOADED_COMPONENTS.clear();
    }

    /**
     * Returns class, implemented interface <code>jworkspace.api.IUserProfileEngine</code>
     */
    public static IUserProfileEngine getProfilesEngine() {
        return profilesEngine;
    }

    /**
     * Get home directory for current user.
     */
    public static String getUserHome() {

        String home = System.getProperty("user.home");
        if (!home.startsWith(System.getProperty(USER_DIR))) {
            home = System.getProperty(USER_DIR)
                + File.separator + Workspace.getProfilesEngine().getPath();
        }
        return home + File.separator;
    }

    /**
     * Kill all processes.
     */
    private static void killAllProcesses() {

        int killall;

        int pcount = Workspace.getRuntimeManager().getAllProcesses().length;
        boolean alive = false;

        for (int i = 0; i < pcount; i++) {
            if (Workspace.getRuntimeManager().getAllProcesses()[i].isAlive()) {
                alive = true;
                break;
            }
        }

        if (!alive) {
            return;
        }

        killall = JOptionPane.showConfirmDialog(ui.getFrame(),
            LangResource.getString("Workspace.killAll.question"),
            LangResource.getString("Workspace.killAll.title"),
            JOptionPane.YES_NO_OPTION);
        if (killall == JOptionPane.YES_OPTION) {
            for (int pr = 0; pr < pcount; pr++) {
                if (Workspace.getRuntimeManager().getAllProcesses()[pr] != null) {
                    Workspace.getRuntimeManager().getAllProcesses()[pr].kill();
                }
            }
        }

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

    /**
     * Locate and load SYSTEM SERVICES and PLUGINS.
     */
    private static void initSystem() {

        Class c;

        try {
            c = Class.forName(Workspace.usersAuthenticationClassName);
            if (!c.isInterface()) {
                profilesEngine = (IUserProfileEngine) c.newInstance();
            }
            Workspace.LOG.info("> User profile engine is loaded");

            c = Class.forName(Workspace.installerClassName);
            if (!c.isInterface()) {
                installEngine = (InstallEngine) c.newInstance();
                addEngine(installEngine);
            }
            Workspace.LOG.info("> Installer is loaded");

            c = Class.forName(Workspace.guiClassName);
            if (!c.isInterface()) {
                ui = (UI) c.newInstance();
                addListener(ui);
                Workspace.LOG.info("> ui is loaded");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Workspace.ui.showError(LangResource.getString("Workspace.load.abort"), e);
            System.exit(-1);
        }

        // LAUNCH SYSTEM PLUGINS FROM PLUGINS DIRECTORY

        addSystemPlugins(Workspace.getRuntimeManager().loadPlugins(PLUGINS));
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

    private static void saveEngines() {
        for (IEngine engine : ENGINES) {
            try {
                engine.save();
                engine.reset();
            } catch (IOException ex) {
                String name = engine.getName();
                Workspace.ui.showError(name
                    + WHITESPACE + LangResource.getString(WORKSPACE_ENGINE_SAVE_FAILED), ex);
                Workspace.LOG.error(name
                    + WHITESPACE + LangResource.getString(WORKSPACE_ENGINE_SAVE_FAILED
                    + ex.toString()));
            }
        }
    }

    private static void loadUserPlugins() {

        String fileName = Workspace.getUserHome() + File.separator + PLUGINS;
        addUserPlugins(Workspace.getRuntimeManager().loadPlugins(fileName));
    }

    /**
     * Check for unsaved user data in guiClassName and asks user to save data or not.
     */
    private static boolean isGUIModified() {
        if (ui.isModified()) {
            int result = JOptionPane.showConfirmDialog(ui.getFrame(),
                LangResource.getString("Workspace.guiModified.question"),
                LangResource.getString("Workspace.guiModified.title"),
                JOptionPane.YES_NO_OPTION);

            return result == JOptionPane.YES_OPTION;
        } else {
            return false;
        }
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

    public static void start(String[] args) {

        long start = System.currentTimeMillis();
        Workspace.LOG.info("> Starting" + WHITESPACE + Workspace.getVersion());
        Config cfg = new Config(getConfigHeader().toString());

        try (InputStream in = new FileInputStream(System.getProperty(USER_DIR)
            + File.separator + CONFIG_JWCONF_CFG)) {

            cfg.load(in);

        } catch (IOException e) {
            // just use defaults, don't panic
            Workspace.LOG.info("Couldn't find custom configuration file: " + CONFIG_JWCONF_CFG
                + ". Continuing with defaults");
        }

        Workspace.installerClassName = cfg.getString("install_engine",
            "jworkspace.installer.WorkspaceInstaller");

        Workspace.usersAuthenticationClassName = cfg.getString("profile_engine",
            "jworkspace.users.UserProfileEngine");

        Workspace.guiClassName = cfg.getString("ui", "jworkspace.ui.DefaultUI");

        try {
            initWorkspace(args);
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
    private static void initWorkspace(String[] args) {

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

        Workspace.LOG.info("> Loading " + Workspace.getVersion());

        /*
         * LAUNCH STARTUP SERVICES FROM JW ROOT DIRECTORY
         */
        initSystem();

        Workspace.LOG.info("> Kernel is successfully booted");
        /*
         * When ENGINES are initialized, we need to bring up ui system to promote login procedure and
         * so Java Workspace brings main frame of ui system that must be existent.
         */
        if (ui.getFrame() == null) {
            Workspace.ui.showMessage(LangResource.getString("Workspace.ui.noFrameError"));
            System.exit(-2);
        }
        /*
         * Show logo screen
         */
        Window logo = ui.getLogoScreen();
        if (logo != null) {
            logo.setVisible(true);
        }
        /*
         * Login procedure require more profound behaviour. User can specify commandline options
         * for launching Java Workspace in predefined profile. In this case user will be asked a
         * profile password only:
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
                profilesEngine.login(quickLogin, "");
            } catch (Exception ex) {
                Workspace.ui.showError(LangResource.getString(WORKSPACE_LOGIN_FAILURE), ex);
                quickLogin = null;
            }
        }
        if (quickLogin == null) {
            profilesEngine.getLoginDlg().setVisible(true);
        }

        loadEngines();
        loadUserPlugins();

        try {
            ui.load();
        } catch (IOException ex) {
            Workspace.ui.showError(ui.getName() + WHITESPACE
                + LangResource.getString(WORKSPACE_LOGIN_LOAD_FAILED), ex);
        }
        if (logo != null) {
            logo.dispose();
        }
    }

}