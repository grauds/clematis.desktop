package jworkspace.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 Anton Troshin

   This file is part of Java Workspace.

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   Authors may be contacted at:

   frenzy@ix.netcom.com
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
*/

import jworkspace.kernel.engines.InstallEngine;
import jworkspace.kernel.Workspace;
import jworkspace.util.SysUtil;
import kiwi.io.ConfigFile;
import kiwi.ui.model.DynamicTreeModel;

import javax.swing.*;

import java.io.File;

/**
 * Install engine is one of required by kernel.
 * This class implements interface
 * <code>jworkspace.kernel.engines.InstallEngine</code>.
 */
public class WorkspaceInstaller implements InstallEngine
{
    /**
     * File extension for configuration file.
     */
    public static final String FILE_EXTENSION = ".cfg";
    public static final String CK_DATADIR = "jwinstaller.datadir",
    CK_PROLOG = "jwinstaller.script_prolog";
    static ConfigFile config;
    /**
     * Current data root - this is defined by
     * user path. BUG: If user path is changed,
     * for example as a result of changing user nick
     * in User Details dialog, Installer loses connection
     * to installation database. This is of course
     * happens because dataroot does not follow
     * user home path. WORKAROUND: none.
     * HINT: relogin user.
     */
    public static File dataRoot = null;
    /**
     * Datasources for all types of entries.
     */
    static DefinitionDataSource jvmData, libraryData, applicationData;
    /**
     * Dynamic tree models are used by UI components
     * to build visual tree against virtual models.
     */
    static DynamicTreeModel jvmModel, libraryModel, applicationModel;

    /**
     * Default public constructor
     */
    public WorkspaceInstaller()
    {
        super();
    }

    /**
     * Returns application data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getApplicationData()
    {
        return applicationData;
    }

    /**
     * Returns tree model for application data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public DynamicTreeModel getApplicationModel()
    {
        return applicationModel;
    }

    /**
     * Returns command line for application,
     * found by its path. Path should be
     * divided by "/" delimiters in UNIX style,
     * from current user's installation directories.
     *
     * For example, path can be as follows "/programs/My apps/My app"
     *
     * @return java.lang.String
     * @param path java.lang.String
     */
    public String[] getInvocationArgs(String path)
    {
        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application))
        {
            JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
                            path + " is not an application", "Java Workspace Installer",
                            JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        return ((Application) node).getInvocationArgs();
    }

    /**
     * Returns jar file for installation.
     * @return java.lang.String
     * @param path java.lang.String
     */
    public java.lang.String getJarFile(java.lang.String path)
    {
        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application))
        {
            javax.swing.JOptionPane.showMessageDialog(Workspace.getUI().
                                                      getFrame(), path + " is not an application", "Java Workspace Installer",
                          javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        return ((Application) node).getArchive();
    }

    /**
     * Returns jvm data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getJvmData()
    {
        return jvmData;
    }

    /**
     * Returns tree model for jvm data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public kiwi.ui.model.DynamicTreeModel getJvmModel()
    {
        return jvmModel;
    }

    /**
     * Returns library data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getLibraryData()
    {
        return libraryData;
    }

    /**
     * Returns tree model for library data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public DynamicTreeModel getLibraryModel()
    {
        return libraryModel;
    }

    /**
     * Returns main class for installation.
     * @return java.lang.String
     * @param path java.lang.String
     */
    public java.lang.String getMainClass(java.lang.String path)
    {
        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application))
        {
            javax.swing.JOptionPane.showMessageDialog(Workspace.getUI().
                                                      getFrame(), path + " is not an application", "Java Workspace Installer",
                          javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        return ((Application) node).getMainClass();
    }

    /**
     * Get human readable name for installer
     */
    public String getName()
    {
        return "Java Workspace Installer Engine (R) v0.83";
    }

    /**
     * Returns working directory for new process.
     * @return java.lang.String
     * @param path java.lang.String
     */
    public java.lang.String getWorkingDir(java.lang.String path)
    {
        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application))
        {
            javax.swing.JOptionPane.showMessageDialog(Workspace.getUI().
                                                      getFrame(), path + " is not an application", "Java Workspace Installer",
                          javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        return ((Application) node).getWorkingDirectory();
    }

    /**
     * Returns flag, that tells Workspace
     * to launch this application
     * on startup. Usually, this flag should
     * set to "true" for services like
     * network connection or system clocks.
     *
     * @return java.lang.String
     * @param path java.lang.String
     */
    public boolean isLoadedAtStartup(java.lang.String path)
    {
        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application)) return false;

        return ((Application) node).isLoadedAtStartup();
    }

    /**
     * Returns flag, that tells Workspace
     * to launch this application
     * in separate process. Usually,
     * this flag should
     * set to "true" for external java
     * applications.
     *
     * @return java.lang.String
     * @param path java.lang.String
     */
    public boolean isSeparateProcess(java.lang.String path)
    {
        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application)) return true;

        return ((Application) node).isSeparateProcess();
    }

    /**
     * Load profile data.
     */
    public void load()
    {
        Workspace.getLogger().info("> Loading installer");

        dataRoot = new File(Workspace.getUserHome());

        if (!dataRoot.exists())
        {
            dataRoot.mkdirs();
            dataRoot.mkdir();
        }
        // create global data models

        applicationData = new ApplicationDataSource(new File(WorkspaceInstaller.dataRoot,
                                                             ApplicationDataSource.ROOT));
        applicationModel = new DynamicTreeModel(applicationData);

        libraryData = new LibraryDataSource(new File(WorkspaceInstaller.dataRoot,
                                                     LibraryDataSource.ROOT));
        libraryModel = new DynamicTreeModel(libraryData);

        jvmData = new JVMDataSource(new File(WorkspaceInstaller.dataRoot,
                                             JVMDataSource.ROOT));
        /**
         * Install default virtual machine
         */
        JVM jvm = new JVM((DefinitionNode) jvmData.getRoot(), "current_jvm");
        try
        {
            jvm.setName("default jvm");
            jvm.setDescription("the jvm this instance of workspace is currently running");
            if (SysUtil.isWindows())
            {
                jvm.setPath(System.getProperty("java.home") + File.separator
                            + "bin" + File.separator + "java.exe");
            }
            else if (SysUtil.isSunOS())
            {
                jvm.setPath(System.getProperty("java.home") + File.separator
                            + "bin" + File.separator + "java");
            }
            jvm.setVersion(System.getProperty("java.version"));
            jvm.setArguments("-cp %c %m %a");
            jvm.save();
            ((DefinitionNode) jvmData.getRoot()).add(jvm);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        jvmModel = new DynamicTreeModel(jvmData);
        /**
         * Load applications
         */
        DefinitionNode root = (DefinitionNode) applicationData.getRoot();

        startupLaunch((DefinitionNode[]) applicationData.getChildren(root));

        Workspace.getLogger().info("> Installer is loaded");
    }

    /**
     * Loads recursively all applications,
     * that have launch at startup flag.
     */
    protected void startupLaunch(DefinitionNode[] nodes)
    {
        for (int i = 0; i < nodes.length; i++)
        {
            if (nodes[i] instanceof Application)
            {
                if (((Application) nodes[i]).isLoadedAtStartup())
                {
                    if (((Application) nodes[i]).isSeparateProcess())
                    {
                        Workspace.getRuntimeManager().
                                executeExternalProcess(((Application) nodes[i]).getInvocationArgs(),
                                                       ((Application) nodes[i]).getWorkingDirectory(),
                                                       ((Application) nodes[i]).getName());
                    }
                    else
                    {
                        /**
                         * TODO implement XKernel
                         */
                    }
                }
            }
            else
            {
                startupLaunch((DefinitionNode[]) applicationData.getChildren(nodes[i]));
            }
        }
    }

    /**
     * Resets initial state.
     */
    public void reset() { }

    /**
     * Saves profiles data. But there is no actually
     * data to save, 'couse everything is already
     * written on disk.
     */
    public void save() { }
}
