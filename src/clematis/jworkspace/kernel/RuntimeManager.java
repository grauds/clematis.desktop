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
   tysinsh@comail.ru
  ----------------------------------------------------------------------------
 */

import jworkspace.installer.ApplicationDataSource;
import jworkspace.util.WorkspaceError;
import jworkspace.util.sort.QuickSort;

import kiwi.util.plugin.Plugin;
import kiwi.util.plugin.PluginException;
import kiwi.util.plugin.PluginLocator;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Runtime manager is a core component for
 * Java Workspace. This engine enables
 * managing of processes and threads inside
 * system kernel.
 */
public final class RuntimeManager
{
    /**
     * The list of all external processes in
     * Java Workspace.
     */
    private Vector processes = new Vector();
    /**
     * Workspace plugin context defines context for all
     * plugins, visible or not.
     */
    protected WorkspacePluginContext pluginContext = null;
    /**
     * Plugin locator
     */
    private PluginLocator pluginLocator = null;
    /**
     * Default public constructor.
     */
    protected RuntimeManager()
    {
        super();
    }
//************************************* PROCESSES ***********************************
    /**
     * Executes program externally launching separate java process.
     * @param path in workspace installer's database
     */
    public void executeExternalProcess(String path)
    {
        String old_working_dir = System.getProperty("user.dir");
        String args[] = Workspace.getInstallEngine().getInvocationArgs(path);

        if (args == null)
        {
            return;
        }

        String working_dir = Workspace.getInstallEngine().getWorkingDir(path);
        /**
         * Prevent wrong working directories
         */
        //**************
        if (working_dir == null)
        {
            working_dir = System.getProperty("user.dir");
        }
        else
        {
            /**
             * Additionally check if directory is valid
             */
            File wd = new File(working_dir);
            if (!wd.exists())
            {
                working_dir = System.getProperty("user.dir");
            }
        }
        //****************

        try
        {
            /**
             * Try to set working directory
             */
            if (!NativeLib.setCurrentDir(working_dir))
            {
                throw new IOException("Cannot set working directory" + " " + working_dir);
            }
            /**
             * Trim path
             */
            path = trimPath( path );
            /**
             * Create java process
             */
            JavaProcess process = new JavaProcess(args, path);
            /**
             * Add new element to process
             */
            processes.addElement(process);
        }
        catch (IOException e)
        {
            WorkspaceError.exception("Cannot start application", e);
        }
        catch (Error err)
        {
            WorkspaceError.exception("Cannot start application", err);
        }
        try
        {
            /**
             * Try to set old working directory
             */
            if (!NativeLib.setCurrentDir(old_working_dir))
            {
                WorkspaceError.msg("Cannot set old working directory" + " "
                                   + old_working_dir);
            }
        }
        catch (Error err)
        {
            WorkspaceError.exception("Cannot set old working directory", err);
        }
    }

    /**
     * Trims path to installed java application
     * if it was given with the "programs" root folder
     * @param path
     * @return trimmed path
     */
    private String trimPath(String path)
    {
        if ( path.startsWith("programs/"))
        {
            path = path.substring("programs/".length(), path.length());
        }
        return path;
    }

    /**
     * Executes program launching separate process.
     */
    public void executeExternalProcess(String[] args, String working_dir, String name)
    {
        String old_working_dir = System.getProperty("user.dir");

        if (args == null || working_dir == null)
        {
            return;
        }

        try
        {
            /**
             * Try to set working directory
             */
            if (!NativeLib.setCurrentDir(working_dir))
            {
                throw new IOException("Cannot set working directory"
                                      + " " + working_dir);
            }
            /**
             * Create java process
             */
            JavaProcess process = new JavaProcess(args, name);
            /**
             * Add new element to process
             */
            processes.addElement(process);
        }
        catch (IOException e)
        {
            WorkspaceError.exception("Cannot start application", e);
        }
        catch (Error err)
        {
            WorkspaceError.exception("Cannot start application", err);
            return;
        }
        try
        {
            /**
             * Try to set old working directory
             */
            if (!NativeLib.setCurrentDir(old_working_dir))
            {
                WorkspaceError.msg("Cannot set old working directory" + " "
                                   + old_working_dir);
            }
        }
        catch (Error err)
        {
            WorkspaceError.exception("Cannot start application", err);
        }
    }

    /**
     * Execute native command
     */
    public void executeNativeCommand(String command, String working_dir) throws
            IOException
    {
        String old_working_dir = System.getProperty("user.dir");

        try
        {
            /**
             * Try to set working directory
             */
            if (working_dir != null)
            {
                if (!NativeLib.setCurrentDir(working_dir))
                {
                    WorkspaceError.msg("Cannot set working directory" + " "
                                       + old_working_dir);
                }
            }
            Runtime.getRuntime().exec(command);
        }
        catch (IOException ex)
        {
            WorkspaceError.exception("Cannot execute native command", ex);
        }
        if (working_dir != null)
        {
            /**
             * Try to set old working directory
             */
            if (!NativeLib.setCurrentDir(old_working_dir))
            {
                WorkspaceError.msg("Cannot set old working directory" + " "
                                   + old_working_dir);
            }
        }
    }

     /**
     * This method executes application, previously
     * configured by installer. Path is an address
     * of application configuration file, relative to
     * /programs/ folder.
     */
    public void run(String path)
    {
        /**
         * If application is launched from console,
         * it has not root element prepended, that is
         * nessesary for correct installation engine
         * navigation.
         */
        if (!path.startsWith(ApplicationDataSource.ROOT))
        {
            if (!path.startsWith("/"))
            {
                path = ApplicationDataSource.ROOT + "/" + path;
            }
            else
            {
                path = ApplicationDataSource.ROOT + path;
            }
        }
        if (Workspace.getInstallEngine().isSeparateProcess(path))
        {
            executeExternalProcess(path);
        }
    }

    /**
     * Returns list of running processes in system.
     * @return jworkspace.kernel.JavaProcess[]
     */
    public JavaProcess[] getAllProcesses()
    {
        JavaProcess[] prs = new JavaProcess[processes.size()];
        processes.copyInto(prs);
        return prs;
    }

    /**
     * Finds process by name.
     */
    public JavaProcess getByName(String name)
    {
        for (int i = 0; i < processes.size(); i++)
        {
            if (((JavaProcess) processes.elementAt(i)).getName().equals(name))
            {
                return (JavaProcess) processes.elementAt(i);
            }
        }
        return null;
    }

    /**
     * Removes terminated processes from the list.
     */
    public void removeTerminated()
    {
        Vector temp = new Vector();

        for (int i = 0; i < processes.size(); i++)
        {
            if (((JavaProcess) processes.elementAt(i)).isAlive())
            {
                temp.addElement(processes.elementAt(i));
            }
        }

        processes = (Vector) temp.clone();
    }

    /**
     * Removes terminated process.
     */
    public void remove(JavaProcess pr)
    {
        processes.removeElement(pr);
    }
//************************************* PLUGINS *************************************
    /**
     * Returns plugin context for the workspace
     */
    public WorkspacePluginContext getPluginContext()
    {
        if (pluginContext == null)
        {
            pluginContext = new WorkspacePluginContext();
        }
        return pluginContext;
    }

    /**
     * Returns plugin locator
     */
    public PluginLocator getPluginLocator()
    {
        if (pluginLocator == null)
        {
            pluginLocator = new PluginLocator(getPluginContext());
            pluginLocator.setDisplayAvailable(true);
        }
        return pluginLocator;
    }

    /**
     * Load plugins from specified directory. This
     * method traverses directory, with all
     * subdirectories, searches for jar file and tries to
     * load all plugins.
     *
     * @param directory path to directory
     */
    public Plugin[] loadPlugins(String directory)
    {
        Workspace.getLogger().info(">" + "Loading plugins from " + directory);
        Vector plugins = scanPluginsDir(directory);
        Plugin[] retvalue = new Plugin[plugins.size()];
        for (int i = 0; i < plugins.size(); i++)
        {
            retvalue[i] = (Plugin) plugins.elementAt(i);
        }
        Workspace.getLogger().info(">" + "Plugins from " + directory + " are loaded");
        return retvalue;
    }

    public void resetPluginsCache()
    {
        pluginLocator = null;
    }

    private Vector scanPluginsDir(String file_name)
    {
        Vector plugins = new Vector();
        try
        {
            File file = new File(file_name);
            if (file.isDirectory())
            {
                File[] files = file.listFiles();
                /**
                 * As there is no guarantee, that files will
                 * be in alphabetical order, lets sort
                 * directories and files.
                 */
                Vector sfiles = new Vector();
                for (int i = 0; i < files.length; i++)
                {
                    sfiles.addElement(files[i]);
                }
                /**
                 * Sorting
                 */
                new QuickSort(true).sort(sfiles);
                for (int i = 0; i < files.length; i++)
                {
                    files[i] = (File) sfiles.elementAt(i);
                }
                if (files != null)
                {
                    for (int i = 0; i < files.length; i++)
                    {
                        plugins.addAll(scanPluginsDir(files[i].getAbsolutePath()));
                    }
                }
            }
            else if (file.getName().endsWith("jar"))
            {
                Enumeration en = getPluginLocator().findPlugins(file.getAbsolutePath());
                while (en.hasMoreElements())
                {
                    plugins.addElement(en.nextElement());
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            Workspace.getLogger().warning("Cannot load plugins. Directory path " +
                                   file_name + " is not found.");
        }
        catch (IOException ex)
        {
            Workspace.getLogger().warning("Cannot load plugins from " +
                                   file_name + " - "
                                   + ex.toString());
        }
        catch (PluginException ex)
        {
            Workspace.getLogger().warning("Cannot load plugins from " +
                                   file_name + " - "
                                   + ex.toString());
        }
        return plugins;
    }
}
