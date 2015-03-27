package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

import com.hyperrealm.kiwi.util.Config;
import com.hyperrealm.kiwi.util.plugin.PluginException;
import jworkspace.LangResource;
import jworkspace.kernel.engines.IUserProfileEngine;
import jworkspace.kernel.engines.InstallEngine;
import jworkspace.kernel.engines.IEngine;
import jworkspace.kernel.engines.GUI;
import jworkspace.util.WorkspaceError;
import kiwi.util.plugin.Plugin;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

import java.util.logging.*;
/**
 * Workspace is a core class for Java Workspace,
 * which dispatches messages
 * and commands from users. This class also
 * initializes and manages engines, it performs
 * such tasks as loading and saving of user
 * settings, responding to command processor
 * and managing plugins.
 */
public final class Workspace
{
    /**
     * Listeners for service events.
     */
    private static Vector listeners = new Vector();
    /**
     * System logger (JDK 1.4)
     * Default logging level - INFO
     */
    private static Logger logger = Logger.getLogger("clematis.kernel");
    /**
     * File log writer
     */
    private static FileHandler fileHandler = null;
    /**
     * Debug stream (system.error)
     */
    static PrintStream debug;
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
    private static RuntimeManager runtimeManager = new RuntimeManager();
    /**
     * Java Workspace GUI
     */
    private static GUI gui = null;
    /**
     * Version
     */
    private static String version = "Clematis 1.0.4 RC1";
    /**
     * SYSTEM PLUGINS. This is actually invisible services,
     * that are loaded by system at startup and unloaded on finish.
     * These services are for all users in system. User specific
     * services are not listed here.
     */
    private static HashSet system_plugins = new HashSet();
    /**
     * USER PLUGINS. This is actually invisible services,
     * that are loaded by user at login or later manually and unloaded on logout.
     * These services are NOT for all users in system.
     */
    private static HashSet user_plugins = new HashSet();
    /**
     * LOADED COMPONENTS. These components are not managed as
     * services, but are stored under Object keys to gain access
     * everytime user needs a reference.
     */
    private static Hashtable loaded_components = new Hashtable();
    /**
     * Workspace ENGINES.
     */
    private static Vector engines = new Vector();
    /**
     * Resource manager.
     */
    private static ResourceManager resmgr = new ResourceManager();
    /**
     * GUI clazz name
     */
    public static String GUI = null;
    /**
     * Installer clazz name
     */
    public static String INSTALLER = null;
    /**
     * User profile engine clazz name.
     */
    public static String USER_PROFILER = null;

    /**
     * Add ENGINE
     */
    private static void addEngine(IEngine engine)
    {
        engines.addElement(engine);
    }

    /**
     * Adds SYSTEM PLUGIN.
     */
    protected static void addSystemPlugin(Plugin pl)
    {
        system_plugins.add(pl);
    }

    /**
     * Adds SYSTEM PLUGINS
     */
    protected static void addSystemPlugins(Plugin[] pls)
    {
        for (int i = 0; i < pls.length; i++)
        {
            addSystemPlugin(pls[i]);
        }
    }

    /**
     * Adds USER PLUGIN.
     */
    public static void addUserPlugin(Plugin pl)
    {
        user_plugins.add(pl);
    }

    /**
     * Adds USER PLUGINS
     */
    public static void addUserPlugins(Plugin[] pls)
    {
        for (int i = 0; i < pls.length; i++)
        {
            addUserPlugin(pls[i]);
        }
    }

    /**
     * User login and logout procedures.
     */
    public static void changeCurrentProfile()
    {
        int result = -1;
        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                                       getImage("user_change.png"));

        result = JOptionPane.showConfirmDialog(gui.getFrame(),
                                               LangResource.getString("Workspace.logOff.question") + " "
                                               + getProfilesEngine().getUserName() + " ?",
                                               LangResource.getString("Workspace.logOff.title"),
                                               JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);

        /**
         * Check if any unsaved data exists.
         */
        if (isGUIModified()) return;

        if (result == JOptionPane.YES_OPTION)
        {
            killAllProcesses();
            // SAVE USER INTERFACE
            try
            {
                gui.save();
                gui.reset();
            }
            catch (IOException ex)
            {
                WorkspaceError.exception
                        (gui.getName() + " " +
                         LangResource.getString("Workspace.logOff.saveFailed"), ex);
            }
            /**
             * SAVE AND SHUT DOWN USER PLUGINS
             */
            saveUserPlugins();
            /**
             * Allow user plugins to be garbage collected.
             */
            user_plugins = null;
            /**
             * Remove all registered components for current user.
             */
            removeAllRegisteredComponents();
            /**
             * SAVE ENGINES
             */
            saveEngines();
            /**
             * Reset plugins cache
             */
            getRuntimeManager().resetPluginsCache();

            try
            {
                profilesEngine.logout();
                profilesEngine.getLoginDlg().show();
            }
            catch (Exception e)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.login.failure"), e);
                return;
            }
            /**
             * Load engines
             */
            loadEngines();
            /**
             * Reinitialize user plugins.
             */
            user_plugins = new HashSet();
            /**
             * LAUNCH USER PLUGINS
             */
            loadUserPlugins();
            try
            {
                gui.load();
            }
            catch (IOException ex)
            {
                WorkspaceError.exception(gui.getName() + " "
                                         + LangResource.getString("Workspace.login.loadFailed"), ex);
            }
        }
    }

    /**
     * Exits workspace.
     */
    public static int exit()
    {
        int result = -1;

        ImageIcon icon = new ImageIcon(Workspace.getResourceManager().
                                       getImage("exit.png"));

        result = JOptionPane.showConfirmDialog(gui.getFrame(),
                                               LangResource.getString("Workspace.exit.question"),
                                               LangResource.getString("Workspace.exit.title"),
                                               JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
        /**
         * Check if any unsaved data exists.
         */
        if (isGUIModified()) return 0;

        if (result == JOptionPane.YES_OPTION)
        {
            killAllProcesses();
            /**
             * Save all user profile specific engines.
             */
            if (Workspace.getProfilesEngine().userLogged())
            {
                // SAVE USER INTERFACE
                try
                {
                    gui.save();
                    gui.reset();
                }
                catch (IOException ex)
                {
                    WorkspaceError.exception
                            (gui.getName() + " " +
                             LangResource.getString("Workspace.logOff.saveFailed"), ex);
                }
                /**
                 * SAVE AND SHUT DOWN USER PLUGINS
                 */
                saveUserPlugins();
                /**
                 * SAVE AND SHUT DOWN SYSTEM PLUGINS
                 */
                saveSystemPlugins();
                /**
                 * SAVE ENGINES
                 */
                saveEngines();

                try
                {
                    profilesEngine.logout();
                    profilesEngine.save();
                }
                catch (Exception e)
                {
                    WorkspaceError.exception
                            (LangResource.getString("Workspace.logout.failure"), e);
                }
            }
            getLogFileHandler().flush();
            Workspace.getLogger().info("> 1999 - 2003 Copyright Anton Troshin");
            System.exit(0);
        }
        return result;
    }

    /**
     * Add listener for service events.
     */
    public static synchronized void addListener(IWorkspaceListener l)
    {
        listeners.addElement(l);
    }

    /**
     * Remove workspace listener
     */
    public static synchronized void removeListener(IWorkspaceListener l)
    {
        listeners.removeElement(l);
    }

    /**
     * Deliver event to all listeners.
     */
    public static synchronized void fireEvent(Object event, Object lparam,
                                              Object rparam)
    {
        for (int i = 0; i < listeners.size(); i++)
        {
            ((IWorkspaceListener) listeners.elementAt(i)).processEvent(event, lparam, rparam);
        }
    }

    /**
     * Returns class, implemented interface
     * <code>jworkspace.kernel.engines.InstallEngine</code>
     */
    public static InstallEngine getInstallEngine()
    {
        return installEngine;
    }

    /**
     * Register component in kernel.
     */
    public static void register(Object key, Object component)
    {
        if (key != null && component != null)
        {
            loaded_components.put(key, component);
        }
    }

    /**
     * Get registered component
     */
    public static Object getRegisteredComponent(Object key)
    {
        return loaded_components.get(key);
    }
   /**
    * Create and return log file handler for the system
    *
    * @return log file handler
    */
    public static FileHandler getLogFileHandler()
    {
        if ( fileHandler == null )
        {
            try
            {
                fileHandler = new FileHandler("config"+ File.separator + "log.xml");
            }
            catch(IOException ex) { }
        }
        return fileHandler;
    }
    /**
     * Empty all components
     */
    public static void removeAllRegisteredComponents()
    {
        loaded_components = new Hashtable();
        System.gc();
    }
    /**
     * Return system logger
     * @return system logger
     */
    public static Logger getLogger()
    {
        return logger;
    }
    /**
     * Log entry as INFORMATION message
     * @deprecated as it does not provide info about caller class and method,
     * use getLogger().info()
     */
    public static void logInfo(String str)
    {
        Workspace.getLogger().info( str );
    }

    /**
     * Log entry as INFORMATION message
     * @deprecated as it does not provide info about caller class and method,
     * use getLogger().info()
     */
    public static void logProcessStart(String str)
    {
         Workspace.getLogger().info(str);
    }

    /**
     * Log entry as INFORMATION message
     * @deprecated as it does not provide info about caller class and method,
     * use getLogger().info()
     */
    public static void logProcessEnd(String str)
    {
        Workspace.getLogger().info(str);
    }

    /**
     * Log entry as EXCEPTION message
     * @deprecated as it does not provide info about caller class and method,
     * use getLogger().warning()
     */
    public static void logException(String str)
    {
        Workspace.getLogger().warning( str );
    }

    /**
     * Log entry as FATAL EXCEPTION message
     * @deprecated as it does not provide info about caller class and method,
     * use getLogger().logFatalException()
     */
    public static void logFatalException(String str)
    {
        Workspace.getLogger().severe( str );
    }

    /**
     * Returns class, implemented interface <code>jworkspace.kernel.engines.IUserProfileEngine</code>
     */
    public static IUserProfileEngine getProfilesEngine()
    {
        return profilesEngine;
    }

    /**
     * Get home directory for current user.
     */
    public static String getUserHome()
    {
        /**
         * Find home directory
         */
        String home = System.getProperty("user.home");
        /**
         * If home directory lies outside workspace
         * root
         */
        if (!home.startsWith(System.getProperty("user.dir")))
        {
            home = System.getProperty("user.dir") +
                    File.separator + Workspace.getProfilesEngine().getPath();
        }

        return home + File.separator;
    }

    /**
     * Kill all processes.
     */
    public static void killAllProcesses()
    {
        int killall;
        /**
         * Check if there are running processes.
         */
        int pcount = Workspace.getRuntimeManager().getAllProcesses().length;
        boolean alive = false;
        /**
         * Iterate through processes to find first alive.
         */
        for (int i = 0; i < pcount; i++)
        {
            if (Workspace.getRuntimeManager().getAllProcesses()[i].isAlive())
            {
                alive = true;
                break;
            }
        }
        /**
         * If we found alive process lets user decide leave or kill
         * all alive processes.
         */
        if (alive)
        {
            killall = JOptionPane.showConfirmDialog(gui.getFrame(),
                                                    LangResource.getString("Workspace.killAll.question"),
                                                    LangResource.getString("Workspace.killAll.title"),
                                                    JOptionPane.YES_NO_OPTION);
            if (killall == JOptionPane.YES_OPTION)
            {
                for (int pr = 0; pr < pcount; pr++)
                {
                    if (Workspace.getRuntimeManager().getAllProcesses()[pr] != null)
                        Workspace.getRuntimeManager().getAllProcesses()[pr].kill();
                }
            }
        }
    }

    /**
     *	Print a debug message on debug stream only if debugging is turned on.
     */
    public final static void debug(String s)
    {
        logger.fine( s );
    }

    /**
     * Returns resource manager for the workspace.
     * @return kiwi.util.ResourceManager
     */
    public static ResourceManager getResourceManager()
    {
        return resmgr;
    }

    /**
     * Returns class implemented interface
     * <code>jworkspace.kernel.RuntimeManager</code>
     */
    public static RuntimeManager getRuntimeManager()
    {
        return runtimeManager;
    }

    /**
     * Returns user plugins copy.
     */
    public static HashSet getUserPlugins()
    {
        return (HashSet) user_plugins.clone();
    }

    /**
     * Returns system plugins copy.
     */
    public static HashSet getSystemPlugins()
    {
        return (HashSet) system_plugins.clone();
    }

    /**
     * Returns workspace version
     */
    public static String getVersion()
    {
        return version;
    }

    /**
     * Returns class of interface <code>jworkspace.kernel.engines.GUI</code>
     */
    public static GUI getUI()
    {
        return gui;
    }

    /**
     * Find service by type of implemented interface
     * from kernel. Method is needed to get
     * reference to object, loaded dynamically
     * as service. Usually this should be called by
     * UI shells, that take advantage of service
     * functionality.
     */
    public static Object getServiceByName(String clazz_name)
    {
        Iterator it = system_plugins.iterator();
        /**
         * Seek for object here.
         */
        while (it.hasNext())
        {
            Plugin plugin = (Plugin) it.next();
            if (plugin.getClassName().equals(clazz_name))
            {
                return plugin;
            }
        }
        /**
         * If we are here, that means, that oject is
         * not found among system services. Perhaps
         * it is loaded as user service.
         */
        it = user_plugins.iterator();
        /**
         * Seek for object here.
         */
        while (it.hasNext())
        {
            Plugin plugin = (Plugin) it.next();
            if (plugin.getClassName().equals(clazz_name))
            {
                return plugin;
            }
        }
        /**
         * Finally return null
         */
        return null;
    }

    /**
     * Find service by type of implemented interface
     * from kernel. Method is needed to get
     * reference to object, loaded dynamically
     * as service. Usually this should be called by
     * UI shells, that take advantage of service
     * functionality.
     */
    public static Object getServiceByType(Class clazz)
    {
        Iterator it = system_plugins.iterator();
        /**
         * Seek for object here.
         */
        while (it.hasNext())
        {
            Plugin plugin = (Plugin) it.next();
            if (plugin.getPluginObject().equals(clazz))
            {
                return plugin;
            }
        }
        /**
         * If we are here, that means, that oject is
         * not found among system services. Perhaps
         * it is loaded as user service.
         */
        it = user_plugins.iterator();
        /**
         * Seek for object here.
         */
        while (it.hasNext())
        {
            Plugin plugin = (Plugin) it.next();
            if (plugin.getPluginObject().equals(clazz))
            {
                return plugin;
            }
        }
        /**
         * Finally return null
         */
        return null;
    }

    /**
     * Locate and load SYSTEM SERVICES and PLUGINS.
     */
    private static void initSystem()
    {
        // USER PROFILE ENGINE
        Class c = null;

        try
        {
            c = Class.forName(Workspace.USER_PROFILER);
            if (!c.isInterface())
            {
                profilesEngine = (IUserProfileEngine) c.newInstance();
            }
            Workspace.getLogger().info(">" + "User profile engine is loaded");

            c = Class.forName(Workspace.INSTALLER);
            if (!c.isInterface())
            {
                installEngine = (InstallEngine) c.newInstance();
                addEngine(installEngine);
            }
            Workspace.getLogger().info(">" + "Installer is loaded");

            c = Class.forName(Workspace.GUI);
            if (!c.isInterface())
            {
                gui = (GUI) c.newInstance();
                /**
                 * Workspace GUI is Workspace Listener
                 */
                addListener(gui);
                Workspace.getLogger().info(">" + "GUI is loaded");
            }
        }
        catch (ClassNotFoundException e)
        {
            WorkspaceError.exception(LangResource.getString("Workspace.load.abort"), e);
            System.exit(-1);
        }
        catch (InstantiationException e)
        {
            WorkspaceError.exception(LangResource.getString("Workspace.load.abort"), e);
            System.exit(-1);
        }
        catch (IllegalAccessException e)
        {
            WorkspaceError.exception(LangResource.getString("Workspace.load.abort"), e);
            System.exit(-1);
        }

        // LAUNCH SYSTEM PLUGINS FROM PLUGINS DIRECTORY

        String fileName = "plugins";
        addSystemPlugins(Workspace.getRuntimeManager().loadPlugins(fileName));
    }

    protected static void loadEngines()
    {
        for (int i = 0; i < engines.size(); i++)
        {
            try
            {
                ((IEngine) engines.elementAt(i)).load();
            }
            catch (IOException ex)
            {
                String name = ((IEngine) engines.elementAt(i)).getName();
                WorkspaceError.exception(name
                                         + " " + LangResource.getString("Workspace.engine.loadFailed"), ex);
                Workspace.getLogger().warning(name
                                       + " " + LangResource.getString("Workspace.engine.loadFailed"
                                                                      + ex.toString()));
            }
        }
    }
    /**
     * Sets logging level of kernel logger, each level adds
     * new type of logging messages
     *
     *  0 - off;
     *  1 - only severe errors
     *  2 - warnings
     *  4 - configuration strings
     *  5 - fine details
     *  6 - finer details
     *  7 - finest details
     *  8 - all messages
     *
     * @param level can be an string integer from 0 to 8
     */
    public static void setLogLevel(String level)
    {
        try
        {
            int level_int = Integer.parseInt( level );
            switch (level_int)
            {
                case 0:
                    logger.setLevel( Level.OFF );
                    break;
                case 1:
                    logger.setLevel( Level.SEVERE );
                    break;
                case 2:
                    logger.setLevel( Level.WARNING );
                    break;
                case 3:
                    logger.setLevel( Level.INFO );
                    break;
                case 4:
                    logger.setLevel( Level.CONFIG );
                    break;
                case 5:
                    logger.setLevel( Level.FINE );
                    break;
                case 6:
                    logger.setLevel( Level.FINER );
                    break;
                case 7:
                    logger.setLevel( Level.FINEST );
                    break;
                case 8:
                    logger.setLevel( Level.ALL );
                    break;
            }
        }
        catch(NumberFormatException ex)
        {
            System.out.println(">! Incorrect logging level");
            logger.setLevel( Level.SEVERE );
        }
    }
    protected static void saveEngines()
    {
        // SAVE ENGINES
        for (int i = 0; i < engines.size(); i++)
        {
            try
            {
                ((IEngine) engines.elementAt(i)).save();
                ((IEngine) engines.elementAt(i)).reset();
            }
            catch (IOException ex)
            {
                String name = ((IEngine) engines.elementAt(i)).getName();
                WorkspaceError.exception(name
                                         + " " + LangResource.getString("Workspace.engine.saveFailed"), ex);
                Workspace.getLogger().warning(name
                                       + " " + LangResource.getString("Workspace.engine.saveFailed"
                                                                      + ex.toString()));
            }
        }
    }

    /**
     * Save and dispose USER PLUGINS.
     */
    protected static void saveUserPlugins()
    {
        Iterator iter = user_plugins.iterator();
        while (iter.hasNext())
        {
            Plugin uscomp = (Plugin) iter.next();
            Workspace.getLogger().info(">" + "Saving" + " " + uscomp.getName() + "...");
            try
            {
                uscomp.dispose();
            }
            catch (PluginException ex)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.plugin.saveFailed"), ex);
            }
        }
    }

    /**
     * Save SYSTEM PLUGINS.
     */
    protected static void saveSystemPlugins()
    {
        Iterator iter = system_plugins.iterator();
        while (iter.hasNext())
        {
            Plugin uscomp = (Plugin) iter.next();
            Workspace.getLogger().info(">" + "Saving" + " " + uscomp.getName() + "...");

            try
            {
                uscomp.dispose();
            }
            catch (PluginException ex)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.plugin.saveFailed"), ex);
            }
        }
    }

    protected static void loadSystemPlugins()
    {
        Iterator iter = system_plugins.iterator();
        while (iter.hasNext())
        {
            Plugin uscomp = (Plugin) iter.next();
            Workspace.getLogger().info(">" + "Loading system plugin" + " " +
                              uscomp.getName() + "...");
            try
            {
                uscomp.load();
                Method m = uscomp.getPluginObject().getClass()
                        .getMethod("load", new Class[]{});
                m.invoke(uscomp.getPluginObject(), new Class[]{});
            }
            catch (PluginException ex)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.plugin.loadFailed"), ex);
            }
            catch (Exception ex)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.plugin.loadFailed"), ex);
            }
            catch (Error err)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.plugin.loadFailed"), err);
            }
        }
    }

    /**
     * Check for unsaved user data in GUI and asks
     * user to save data or not.
     */
    private static boolean isGUIModified()
    {
        if (gui.isModified())
        {
            int result = -1;
            result = JOptionPane.showConfirmDialog(gui.getFrame(),
                                                   LangResource.getString("Workspace.guiModified.question"),
                                                   LangResource.getString("Workspace.guiModified.title"),
                                                   JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION)
                return true;
            else
                return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * Locate and load USER PLUGINS.
     */
    protected static void loadUserPlugins()
    {
        String fileName = Workspace.getUserHome() +
                File.separator + "plugins";
        addUserPlugins(Workspace.getRuntimeManager().loadPlugins(fileName));

        Iterator iter = user_plugins.iterator();
        while (iter.hasNext())
        {
            Plugin uscomp = (Plugin) iter.next();
            Workspace.getLogger().info(">" + "Loading user plugin" +
                              " " + uscomp.getName() + "...");
            try
            {
                uscomp.load();
                Method m = uscomp.getPluginObject().getClass()
                        .getMethod("load", new Class[]{});
                m.invoke(uscomp.getPluginObject(), new Class[]{});
            }
            catch (PluginException ex)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.plugin.loadFailed"), ex);
            }
            catch (Exception ex)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.plugin.loadFailed"), ex);
            }
            catch (Error err)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.plugin.loadFailed"), err);
            }
        }
    }
    /**
     * Starts the application.
     * @param args an array of command-line arguments
     */
    public static void main(java.lang.String[] args)
    {
        try
        {
            int ver = Integer.parseInt(System.getProperty("java.version").
                                       substring(0, 3));
            if ( ver < 1.4 )
            {
                WorkspaceError.msg("Incorrect java version: " + ver,
                                   "Sorry, Clematis requires java 1.4 or better to run.");
                System.exit(-1);
            }
        }
        catch(NumberFormatException ex)
        {}

        long start = System.currentTimeMillis();
        int paramLength = args.length;

        for (int i = 0; i < paramLength; i++)
        {
            String par = args[i];
            /**
             * Print all locales and if it the only option, exit
             */
            if (par.equalsIgnoreCase("-locales"))
            {
                LangResource.printAvailableLocales();
                if (paramLength == 1)
                {
                    System.exit(0);
                }
            }
            /**
             * Print version and if it the only option, exit
             */
            else if (par.equalsIgnoreCase("-version"))
            {
                System.out.println(Workspace.getVersion());
                if (paramLength == 1)
                {
                    System.exit(0);
                }
            }
            else if (par.equalsIgnoreCase("-loglevel"))
            {
                setLogLevel( args[++i] );
            }
        }
        Workspace.getLogger().info(">" + "Starting" + " " + Workspace.getVersion());
        try
        {
          InputStream in = new FileInputStream(System.getProperty("user.dir") +
                                               File.separator + "config/jwconf.cfg");
          Config cfg = new Config(getConfigHeader().toString());
          cfg.load(in);

          Workspace.INSTALLER = cfg.getString("install_engine");
          Workspace.USER_PROFILER = cfg.getString("profile_engine");
          Workspace.GUI = cfg.getString("gui");

          initWorkspace(args);
        }
        catch (IOException e)
        {
          WorkspaceError.exception("Cannot read configuration", e);
        }
        catch (Throwable e)
        {
          Workspace.getLogger().severe("Failed to init Java Workspace " + e.toString());
          WorkspaceError.exception("Failed to init Java Workspace", e);
          System.exit(-1);
        }
        long end = System.currentTimeMillis();
        System.out.println( "Started in: " + (end - start) + " millis" );
    }
    /**
     * Get configuration header for jwconfig.cfg file.
     * @return configuration header
     */
    protected static StringBuffer getConfigHeader()
    {
      /**
       * Configuration header
       */
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
    protected static void initWorkspace(java.lang.String[] args)
    {
        int paramLength = args.length;
        /**
         * Log init
         */
        logger.addHandler( getLogFileHandler() );
        /**
         * Quick login.
         */
        String quickLogin = null;

        for (int i = 0; i < paramLength; i++)
        {
            String par = args[i];
            /**
             * If there is a quick login name.
             */
            if (par.equalsIgnoreCase("-qlogin"))
            {
                quickLogin = args[++i];
            }
            else if (par.equalsIgnoreCase("-debug"))
            {
                System.setProperty("debug", "true");
                debug = System.err;
                Workspace.getLogger().info(LangResource.getString("message#21"));
            }
        }
        /**
         * Initialize engines.
         */
        Workspace.getLogger().info(">" + "Loading" + " " + Workspace.getVersion());
        // LAUNCH STARTUP SERVICES FROM JW ROOT DIRECTORY
        initSystem();
        Workspace.getLogger().info(">" + "Kernel is successfully booted");
        /**
         * Then engines are initialized, we
         * need to bring up gui system to promote
         * login procedure and so Java Workspace
         * brings main frame of gui system,
         * that must be existent.
         */
        if (gui.getFrame() == null)
        {
            WorkspaceError.msg(LangResource.getString("Workspace.gui.noFrameError"));
            System.exit(-2);
        }
        /**
         * Show logo screen
         */
        Window logo = gui.getLogoScreen();
        logo.setVisible(true);
        /**
         * Login procedure require more profound
         * behaviour. User can specify commandline options
         * for launching Java Workspace in predefined
         * profile. In this case user will be asked a
         * profile password only.
         * This looks like this:
         * -qlogin [profile name]
         */
        try
        {
            profilesEngine.load();
        }
        catch (IOException ex)
        {
            WorkspaceError.exception
                    (LangResource.getString("Workspace.load.profilerFailure"), ex);
            System.exit(-3);
        }
        /**
         * Login.
         */
        if (quickLogin != null)
        {
            try
            {
                profilesEngine.login(quickLogin, "");
            }
            catch (Exception e)
            {
                WorkspaceError.exception
                        (LangResource.getString("Workspace.login.failure"), e);
                quickLogin = null;
            }
        }
        if (quickLogin == null)
        {
            profilesEngine.getLoginDlg().show();
        }
        // LOAD SYSTEM ENGINES AND PLUGINS
        loadEngines();
        loadSystemPlugins();

        // LOAD USER PLUGINS

        loadUserPlugins();

        // INIT GUI

        try
        {
            gui.load();
        }
        catch (IOException ex)
        {
            WorkspaceError.exception(gui.getName() + " "
                                     + LangResource.getString("Workspace.login.loadFailed"), ex);
        }
        logo.dispose();
    }
}