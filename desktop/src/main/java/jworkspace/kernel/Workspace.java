package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

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

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.hyperrealm.kiwi.util.Config;
import com.hyperrealm.kiwi.util.plugin.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jworkspace.LangResource;
import jworkspace.kernel.engines.GUI;
import jworkspace.kernel.engines.IEngine;
import jworkspace.kernel.engines.IUserProfileEngine;
import jworkspace.kernel.engines.InstallEngine;
import jworkspace.util.WorkspaceError;

import kiwi.util.plugin.Plugin;

/**
 * Workspace is a core class for Java Workspace, which dispatches messages and commands from users. This class also
 * initializes and manages engines, it performs such tasks as loading and saving of user settings,
 * responding to command processor and managing plugins.
 *
 * @since 1.0
 */
public final class Workspace {

    /**
     * Version
     */
    private static final String version = "Clematis 2.0";

    /**
     * Listeners for service events.
     */
    private static final Vector<IWorkspaceListener> listeners = new Vector<>();

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Workspace.class);

    /**
     * The name of configuration file
     */
    private static final String CONFIG_JWCONF_CFG = "config/jwconf.cfg";

    /**
     * User profile engine interface
     */
    private static IUserProfileEngine profilesEngine = null;

    /**
     * Installer
     */
    private static InstallEngine installEngine = null;

    /**
     * Runtime manager
     */
    private static final RuntimeManager runtimeManager = new RuntimeManager();

    /**
     * Java Workspace GUI
     */
    private static GUI gui = null;

    /**
     * SYSTEM PLUGINS. This is actually invisible services,
     * that are loaded by system at startup and unloaded on finish.
     * These services are for all users in system.
     * <p>
     * Each one is executed in its own thread and has its own security manager
     */
    private static final Set<Plugin> system_plugins = new HashSet<>();

    /**
     * USER PLUGINS. This is actually invisible services,
     * that are loaded by user at login or later manually and unloaded on logout.
     * These services are NOT for all users in system.
     * <p>
     * Each one is executed in its own thread and has its own security manager
     */
    private static final Set<Plugin> user_plugins = new HashSet<>();

    /**
     * LOADED COMPONENTS. These components are not managed as
     * services, but are stored under Object keys to gain access
     * everytime user needs a reference.
     */
    private static final Map<String, Object> loaded_components = new HashMap<>();

    /**
     * Workspace ENGINES in a synchronized list
     */
    private static final Collection<IEngine> engines = new Vector<>();

    /**
     * Resource manager.
     */
    private static final ResourceManager resmgr = new ResourceManager();

    /**
     * GUI clazz name
     */
    private static String GUI = null;

    /**
     * Installer clazz name
     */
    private static String INSTALLER = null;

    /**
     * User profile engine clazz name.
     */
    private static String USER_PROFILER = null;

    /**
     * Add ENGINE
     */
    private static void addEngine(IEngine engine) {
        engines.add(engine);
    }

    /**
     * Adds SYSTEM PLUGIN.
     */
    private static void addSystemPlugin(Plugin pl) {
        system_plugins.add(pl);
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
        user_plugins.add(pl);
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
            JOptionPane.showMessageDialog(gui.getFrame(), "Please save data before logging out");
            return;
        }

        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().getImage("user_change.png"));

        int result = JOptionPane.showConfirmDialog(gui.getFrame(),
                LangResource.getString("Workspace.logOff.question")
                        + " " + getProfilesEngine().getUserName() + " ?",
                LangResource.getString("Workspace.logOff.title"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);


        if (result == JOptionPane.YES_OPTION) {

            killAllProcesses();

            saveAndResetUI();

            saveUserPlugins();

            user_plugins.clear();

            removeAllRegisteredComponents();

            saveEngines();

            getRuntimeManager().resetPluginsCache();

            try {
                profilesEngine.logout();
                profilesEngine.getLoginDlg().setVisible(true);
            } catch (Exception e) {
                WorkspaceError.exception(LangResource.getString("Workspace.login.failure"), e);
                return;
            }

            loadEngines();

            user_plugins.clear();

            loadUserPlugins();

            try {
                gui.load();
            } catch (IOException ex) {
                WorkspaceError.exception(gui.getName() + " "
                        + LangResource.getString("Workspace.login.loadFailed"), ex);
            }
        }
    }

    /**
     * Save UI preferences and reset components to their original state
     */
    private static void saveAndResetUI() {

        try {
            gui.save();
            gui.reset();
        } catch (IOException ex) {
            WorkspaceError.exception(gui.getName() + " "
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
            JOptionPane.showMessageDialog(gui.getFrame(), "Please save data before leaving");
            return;
        }

        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                getImage("exit.png"));

        int result = JOptionPane.showConfirmDialog(gui.getFrame(),
                LangResource.getString("Workspace.exit.question"),
                LangResource.getString("Workspace.exit.title"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);

        if (result == JOptionPane.YES_OPTION) {

            killAllProcesses();

            if (Workspace.getProfilesEngine().userLogged()) {

                saveAndResetUI();

                saveUserPlugins();

                saveSystemPlugins();

                saveEngines();

                try {
                    profilesEngine.logout();
                    profilesEngine.save();
                } catch (Exception e) {
                    WorkspaceError.exception
                            (LangResource.getString("Workspace.logout.failure"), e);
                }
            }
            Workspace.LOG.info("> 1999 - 2018 Copyright Anton Troshin");
            System.exit(0);
        }
    }

    /**
     * Add listener for service events.
     */
    private static synchronized void addListener(IWorkspaceListener l) {
        listeners.add(l);
    }

    /**
     * Remove workspace listener
     */
    public static synchronized void removeListener(IWorkspaceListener l) {
        listeners.removeElement(l);
    }

    /**
     * Deliver event to all listeners.
     */
    public static synchronized void fireEvent(Object event, Object lparam, Object rparam) {

        for (IWorkspaceListener listener : listeners) {
            listener.processEvent(event, lparam, rparam);
        }
    }

    /**
     * Returns class, implemented interface
     * <code>jworkspace.kernel.engines.InstallEngine</code>
     */
    public static InstallEngine getInstallEngine() {
        return installEngine;
    }

    /**
     * Register component in kernel.
     */
    public static void register(String key, Object component) {
        if (key != null && component != null) {
            loaded_components.put(key, component);
        }
    }

    /**
     * Get registered component
     */
    public static Object getRegisteredComponent(String key) {
        return loaded_components.get(key);
    }

    /**
     * Empty all components
     */
    private static void removeAllRegisteredComponents() {
        loaded_components.clear();
        System.gc();
    }

    /**
     * Returns class, implemented interface <code>jworkspace.kernel.engines.IUserProfileEngine</code>
     */
    public static IUserProfileEngine getProfilesEngine() {
        return profilesEngine;
    }

    /**
     * Get home directory for current user.
     */
    public static String getUserHome() {

        String home = System.getProperty("user.home");
        if (!home.startsWith(System.getProperty("user.dir"))) {
            home = System.getProperty("user.dir") +
                    File.separator + Workspace.getProfilesEngine().getPath();
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

        if (alive) {
            killall = JOptionPane.showConfirmDialog(gui.getFrame(),
                    LangResource.getString("Workspace.killAll.question"),
                    LangResource.getString("Workspace.killAll.title"),
                    JOptionPane.YES_NO_OPTION);
            if (killall == JOptionPane.YES_OPTION) {
                for (int pr = 0; pr < pcount; pr++) {
                    if (Workspace.getRuntimeManager().getAllProcesses()[pr] != null)
                        Workspace.getRuntimeManager().getAllProcesses()[pr].kill();
                }
            }
        }
    }

    /**
     * Returns resource manager for the workspace.
     *
     * @return kiwi.util.ResourceManager
     */
    public static ResourceManager getResourceManager() {
        return resmgr;
    }

    /**
     * Returns class implemented interface
     * <code>jworkspace.kernel.RuntimeManager</code>
     */
    public static RuntimeManager getRuntimeManager() {
        return runtimeManager;
    }

    /**
     * Returns workspace version
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Returns class of interface <code>jworkspace.kernel.engines.GUI</code>
     */
    public static GUI getUI() {
        return gui;
    }

    /**
     * Find service by type of implemented interface from kernel. Method is needed to get
     * reference to object, loaded dynamically as service. Usually this should be called by
     * UI shells, that take advantage of service functionality.
     */
    private static Object getService(String clazz_name) {

        Iterator it = system_plugins.iterator();

        Plugin plugin = null;

        while (it.hasNext()) {
            plugin = (Plugin) it.next();
            if (plugin.getClassName().equals(clazz_name)) {
                break;
            }
        }

        it = user_plugins.iterator();

        while (it.hasNext()) {
            plugin = (Plugin) it.next();
            if (plugin.getClassName().equals(clazz_name)) {
                break;
            }
        }

        return plugin;
    }

    /**
     * Find service by type of implemented interface from kernel. Method is needed to get
     * reference to object, loaded dynamically as service. Usually this should be called by
     * UI shells, that take advantage of service functionality.
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
            c = Class.forName(Workspace.USER_PROFILER);
            if (!c.isInterface()) {
                profilesEngine = (IUserProfileEngine) c.newInstance();
            }
            Workspace.LOG.info(">" + "User profile engine is loaded");

            c = Class.forName(Workspace.INSTALLER);
            if (!c.isInterface()) {
                installEngine = (InstallEngine) c.newInstance();
                addEngine(installEngine);
            }
            Workspace.LOG.info(">" + "Installer is loaded");

            c = Class.forName(Workspace.GUI);
            if (!c.isInterface()) {
                gui = (GUI) c.newInstance();
                addListener(gui);
                Workspace.LOG.info(">" + "GUI is loaded");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            WorkspaceError.exception(LangResource.getString("Workspace.load.abort"), e);
            System.exit(-1);
        }

        // LAUNCH SYSTEM PLUGINS FROM PLUGINS DIRECTORY

        String fileName = "plugins";
        addSystemPlugins(Workspace.getRuntimeManager().loadPlugins(fileName));
    }

    private static void loadEngines() {
        for (IEngine engine : engines) {
            try {
                engine.load();
            } catch (IOException ex) {
                String name = engine.getName();
                WorkspaceError.exception(name
                        + " " + LangResource.getString("Workspace.engine.loadFailed"), ex);
                Workspace.LOG.error(name
                        + " " + LangResource.getString("Workspace.engine.loadFailed"
                        + ex.toString()));
            }
        }
    }

    private static void saveEngines() {
        for (IEngine engine : engines) {
            try {
                engine.save();
                engine.reset();
            } catch (IOException ex) {
                String name = engine.getName();
                WorkspaceError.exception(name
                        + " " + LangResource.getString("Workspace.engine.saveFailed"), ex);
                Workspace.LOG.error(name
                        + " " + LangResource.getString("Workspace.engine.saveFailed"
                        + ex.toString()));
            }
        }
    }

    private static void saveUserPlugins() {
        for (Plugin uscomp : user_plugins) {
            disposePlugin(uscomp);
        }
    }

    private static void saveSystemPlugins() {
        for (Plugin uscomp : system_plugins) {
            disposePlugin(uscomp);
        }
    }

    private static void disposePlugin(Plugin uscomp) {

        Workspace.LOG.info(">" + "Saving" + " " + uscomp.getName() + "...");
        try {
            uscomp.dispose();
        } catch (PluginException ex) {
            WorkspaceError.exception(LangResource.getString("Workspace.plugin.saveFailed"), ex);
        }
    }

    private static void loadSystemPlugins() {

        for (Plugin uscomp : system_plugins) {
            Workspace.LOG.info(">" + "Loading system plugin" + " " + uscomp.getName() + "...");
            loadPlugin(uscomp);
        }
    }

    private static void loadUserPlugins() {

        String fileName = Workspace.getUserHome() + File.separator + "plugins";
        addUserPlugins(Workspace.getRuntimeManager().loadPlugins(fileName));

        for (Plugin uscomp : user_plugins) {
            Workspace.LOG.info(">" + "Loading user plugin" + " " + uscomp.getName() + "...");
            loadPlugin(uscomp);
        }
    }

    private static void loadPlugin(Plugin uscomp) {
        try {
            uscomp.load();
            Method m = uscomp.getPluginObject().getClass().getMethod("load");
            m.invoke(uscomp.getPluginObject());
        } catch (Exception | Error ex) {
            WorkspaceError.exception(LangResource.getString("Workspace.plugin.loadFailed"), ex);
        }
    }

    /**
     * Check for unsaved user data in GUI and asks user to save data or not.
     */
    private static boolean isGUIModified() {
        if (gui.isModified()) {
            int result = JOptionPane.showConfirmDialog(gui.getFrame(),
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

        long start = System.currentTimeMillis();
        int paramLength = args.length;

        for (String par : args) {

            if (par.equalsIgnoreCase("-locales")) {
                LangResource.printAvailableLocales();
                if (paramLength == 1) {
                    System.exit(0);
                }
            } else if (par.equalsIgnoreCase("-version")) {
                System.out.println(Workspace.getVersion());
                if (paramLength == 1) {
                    System.exit(0);
                }
            } else if (par.equalsIgnoreCase("-loglevel")) {
                System.out.println("Feature is not supported in JWorkspace 2.0");
            }
        }

        Workspace.LOG.info(">" + "Starting" + " " + Workspace.getVersion());
        Config cfg = new Config(getConfigHeader().toString());
        try {
            InputStream in = new FileInputStream(System.getProperty("user.dir") +
                    File.separator + CONFIG_JWCONF_CFG);

            cfg.load(in);

        } catch (IOException e) {
            // just use defaults, don't panic
            Workspace.LOG.info("Couldn't find custom configuration file: " + CONFIG_JWCONF_CFG
                    + ". Continuing with defaults");
        }

        Workspace.INSTALLER = cfg.getString("install_engine", "jworkspace.installer.WorkspaceInstaller");
        Workspace.USER_PROFILER = cfg.getString("profile_engine", "jworkspace.users.UserProfileEngine");
        Workspace.GUI = cfg.getString("gui", "jworkspace.ui.WorkspaceGUI");

        try {
            initWorkspace(args);
        } catch (Throwable e) {
            Workspace.LOG.error("Failed to init Java Workspace " + e.toString());
            WorkspaceError.exception("Failed to init Java Workspace", e);
            System.exit(-1);
        }

        long end = System.currentTimeMillis();
        System.out.println("Started in: " + (end - start) + " millis");
    }

    /**
     * Get configuration header for jwconfig.cfg file.
     *
     * @return configuration header
     */
    private static StringBuffer getConfigHeader() {

        StringBuffer sb = new StringBuffer();
        sb.append("#\n");
        sb.append(
                "# Java Workspace kernel configuration. This file configures classes for\n");
        sb.append(
                "# three kernel engines. If any of these classes are incorrectly specified,\n");
        sb.append("# kernel will fail to load them and will use stub classes.\n");
        sb.append("#\n");

        return sb;
    }

    /**
     * This method is responsible for Workspace initialization.
     * It performs full startup procedure and logs on user.
     */
    private static void initWorkspace(java.lang.String[] args) {

        int paramLength = args.length;

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

        Workspace.LOG.info(">" + "Loading" + " " + Workspace.getVersion());

        /*
         * LAUNCH STARTUP SERVICES FROM JW ROOT DIRECTORY
         */
        initSystem();

        Workspace.LOG.info(">" + "Kernel is successfully booted");
        /*
         * When engines are initialized, we need to bring up gui system to promote login procedure and
         * so Java Workspace brings main frame of gui system that must be existent.
         */
        if (gui.getFrame() == null) {
            WorkspaceError.msg(LangResource.getString("Workspace.gui.noFrameError"));
            System.exit(-2);
        }
        /*
         * Show logo screen
         */
        Window logo = gui.getLogoScreen();
        logo.setVisible(true);
        /*
         * Login procedure require more profound behaviour. User can specify commandline options
         * for launching Java Workspace in predefined profile. In this case user will be asked a
         * profile password only:
         *  -qlogin [profile name]
         */
        try {
            profilesEngine.load();
        } catch (IOException ex) {
            WorkspaceError.exception(LangResource.getString("Workspace.load.profilerFailure"), ex);
            System.exit(-3);
        }

        if (quickLogin != null) {
            try {
                profilesEngine.login(quickLogin, "");
            } catch (Exception ex) {
                WorkspaceError.exception(LangResource.getString("Workspace.login.failure"), ex);
                quickLogin = null;
            }
        }
        if (quickLogin == null) {
            profilesEngine.getLoginDlg().setVisible(true);
        }

        loadEngines();
        loadSystemPlugins();
        loadUserPlugins();

        try {
            gui.load();
        } catch (IOException ex) {
            WorkspaceError.exception(gui.getName() + " "
                    + LangResource.getString("Workspace.login.loadFailed"), ex);
        }
        logo.dispose();
    }
}