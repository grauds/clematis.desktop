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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;
import com.hyperrealm.kiwi.util.plugin.PluginLocator;
import jworkspace.installer.ApplicationDataSource;
import jworkspace.util.WorkspaceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime manager is a core component for Java Workspace to start/stop processes registered in installer
 */
public final class RuntimeManager {

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(RuntimeManager.class);

    /**
     * The list of all external processes in Java Workspace.
     */
    private Vector<JavaProcess> processes = new Vector<>();
    /**
     * Workspace plugin context defines context for all plugins, visible or not.
     */
    private WorkspacePluginContext pluginContext = null;
    /**
     * Plugin locator
     */
    private PluginLocator pluginLocator = null;

    /**
     * Default constructor.
     */
    RuntimeManager() {
        super();
    }

    /**
     * Executes program externally launching separate java process.
     *
     * @param path in workspace installer's database
     */
    private synchronized void executeExternalProcess(String path) {

        String[] args = Workspace.getInstallEngine().getInvocationArgs(path);
        String working_dir = Workspace.getInstallEngine().getWorkingDir(path);

        executeExternalProcess(args, working_dir, trimPath(path));
    }

    /**
     * Trims path to installed java application
     * if it was given with the "programs" root folder
     *
     * @param path to trim
     * @return trimmed path
     */
    private String trimPath(String path) {
        if (path.startsWith("programs/")) {
            path = path.substring("programs/".length());
        }
        return path;
    }

    /**
     * Executes program launching separate process.
     */
    public void executeExternalProcess(String[] args, String working_dir, String name) {

        String old_working_dir = System.getProperty("user.dir");

        if (args == null) {
            return;
        }

        if (working_dir == null) {
            working_dir = System.getProperty("user.dir");
        } else {
            File wd = new File(working_dir);
            if (!wd.exists()) {
                working_dir = System.getProperty("user.dir");
            }
        }

        try {
            /*
             * Try to set working directory
             */
            if (!NativeLib.setCurrentDir(working_dir)) {
                throw new IOException("Cannot set working directory"
                    + " " + working_dir);
            }
            /*
             * Create java process
             */
            JavaProcess process = new JavaProcess(args, name);
            /*
             * Add new element to process
             */
            processes.addElement(process);

        } catch (IOException | Error e) {
            WorkspaceError.exception("Cannot start application", e);
        }

        try {
            /*
             * Try to set old working directory
             */
            if (!NativeLib.setCurrentDir(old_working_dir)) {
                WorkspaceError.msg("Cannot set old working directory" + " " + old_working_dir);
            }

        } catch (Error err) {
            WorkspaceError.exception("Cannot start application", err);
        }
    }

    /**
     * Execute native command
     */
    public void executeNativeCommand(String command, String working_dir) {

        String old_working_dir = System.getProperty("user.dir");

        try {
            /*
             * Try to set working directory
             */
            if (working_dir != null) {
                if (!NativeLib.setCurrentDir(working_dir)) {
                    WorkspaceError.msg("Cannot set working directory" + " " + old_working_dir);
                }
            }
            Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            WorkspaceError.exception("Cannot execute native command", ex);
        }
        if (working_dir != null) {
            /*
             * Try to set old working directory
             */
            if (!NativeLib.setCurrentDir(old_working_dir)) {
                WorkspaceError.msg("Cannot set old working directory" + " " + old_working_dir);
            }
        }
    }

    /**
     * This method executes application, previously configured by installer. Path is an address
     * of application configuration file, relative to /programs/ folder.
     */
    public void run(String path) {
        /*
         * If application is launched from console, it has not root element prepended, that is
         * necessary for correct installation engine navigation.
         */
        if (!path.startsWith(ApplicationDataSource.ROOT)) {
            if (!path.startsWith("/")) {
                path = ApplicationDataSource.ROOT + "/" + path;
            } else {
                path = ApplicationDataSource.ROOT + path;
            }
        }
        if (Workspace.getInstallEngine().isSeparateProcess(path)) {
            executeExternalProcess(path);
        }
    }

    /**
     * Returns list of running processes in system.
     *
     * @return jworkspace.kernel.JavaProcess[]
     */
    JavaProcess[] getAllProcesses() {
        JavaProcess[] prs = new JavaProcess[processes.size()];
        processes.copyInto(prs);
        return prs;
    }

    /**
     * Finds process by name.
     */
    public Optional<JavaProcess> getByName(String name) {
        return processes.stream().filter(javaProcess -> javaProcess.getName().equals(name)).findAny();
    }

    /**
     * Removes terminated processes from the list.
     */
    public synchronized void removeTerminated() {

        JavaProcess[] temp = processes.stream().filter(JavaProcess::isAlive).toArray(JavaProcess[]::new);
        processes.clear();
        processes.addAll(Arrays.asList(temp));
    }

    /**
     * Removes terminated process.
     */
    public void remove(JavaProcess pr) {
        processes.removeElement(pr);
    }

    /**
     * Returns plugin context for the workspace
     */
    private WorkspacePluginContext getPluginContext() {
        if (pluginContext == null) {
            pluginContext = new WorkspacePluginContext();
        }
        return pluginContext;
    }

    /**
     * Returns plugin locator
     */
    private PluginLocator getPluginLocator() {
        if (pluginLocator == null) {
            pluginLocator = new PluginLocator(getPluginContext());
        }
        return pluginLocator;
    }

    /**
     * Load plugins from specified directory. This method traverses directory, with all subdirectories,
     * searches for jar file and tries to load all plugins.
     *
     * @param directory path to directory
     */
    public Plugin[] loadPlugins(String directory) {

        RuntimeManager.LOG.info(">" + "Loading plugins from " + directory);

        List<Plugin> plugins = scanPluginsDir(directory);
        Plugin[] retvalue = plugins.toArray(new Plugin[0]);

        RuntimeManager.LOG.info(">" + "Plugins from " + directory + " are loaded");

        return retvalue;
    }

    void resetPluginsCache() {
        pluginLocator = null;
    }

    private List<Plugin> scanPluginsDir(String name) {

        List<Plugin> plugins = new ArrayList<>();
        try {
            File dir = new File(name);
            if (dir.isDirectory()) {

                File[] files = dir.listFiles();
                if (files != null) {
                    /*
                     * As there is no guarantee, that files will be in alphabetical order, lets sort
                     * directories and files.
                     */
                    Arrays.sort(files, Comparator.comparing(File::getName));

                    for (File file : files) {
                        plugins.addAll(scanPluginsDir(file.getAbsolutePath()));
                    }
                }
            } else if (dir.getName().endsWith("jar")) {
                Plugin plugin = getPluginLocator().loadPlugin(dir, "Any");
                plugins.add(plugin);
            }
        } catch (PluginException ex) {
            RuntimeManager.LOG.warn("Cannot load plugins from " + name + " - " + ex.toString());
        }
        return plugins;
    }
}
