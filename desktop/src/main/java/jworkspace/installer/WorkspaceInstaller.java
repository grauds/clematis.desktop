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
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/

import java.io.File;

import javax.swing.JOptionPane;

import com.hyperrealm.kiwi.io.ConfigFile;
import com.hyperrealm.kiwi.ui.model.DefaultKTreeModel;
import com.hyperrealm.kiwi.ui.model.ExternalKTreeModel;

import jworkspace.api.InstallEngine;
import jworkspace.kernel.Workspace;
import jworkspace.util.SysUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Install engine is one of required by kernel.
 */
public class WorkspaceInstaller implements InstallEngine {

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Workspace.class);

    /**
     * Current data root - this is defined by user path.
     * <p>
     * BUG: If user path is changed, for example as a result of changing user nick
     * in User Details dialog, Installer loses connection to installation database.
     * This is of course happens because dataroot does not follow user home path.
     * <p>
     * WORKAROUND: none.
     * HINT: relogin user.
     */
    private static File dataRoot = null;

    /**
     * File extension for configuration file.
     */
    static final String FILE_EXTENSION = ".cfg";

    //static final String CK_DATADIR = "jwinstaller.datadir", CK_PROLOG = "jwinstaller.script_prolog";

    //static ConfigFile config;

    /**
     * Datasources for all types of entries.
     */
    static DefinitionDataSource jvmData, libraryData, applicationData;

    /**
     * Dynamic tree models are used by UI components
     * to build visual tree against virtual models.
     */
    static DefaultKTreeModel jvmModel, libraryModel, applicationModel;

    /**
     * Default public constructor
     */
    public WorkspaceInstaller() {
        super();
    }

    /**
     * Returns application data.
     *
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getApplicationData() {
        return applicationData;
    }

    /**
     * Returns tree model for application data.
     *
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public DefaultKTreeModel getApplicationModel() {
        return applicationModel;
    }

    /**
     * Returns command line for application, found by its path. Path should be
     * divided by "/" delimiters in UNIX style, from current user's installation directories.
     * <p>
     * For example, path can be as follows "/programs/My apps/My app"
     *
     * @param path java.lang.String
     * @return java.lang.String
     */
    public String[] getInvocationArgs(String path) {

        DefinitionNode node = findApplicationNode(path);
        if (node == null) return null;

        return ((Application) node).getInvocationArgs();
    }

    /**
     * Returns jar file for installation.
     *
     * @param path java.lang.String
     * @return java.lang.String
     */
    public String getJarFile(java.lang.String path) {

        DefinitionNode node = findApplicationNode(path);
        if (node == null) {
            return null;
        }

        return ((Application) node).getArchive();
    }

    /**
     * Returns jvm data.
     *
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getJvmData() {
        return jvmData;
    }

    /**
     * Returns tree model for jvm data.
     *
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public DefaultKTreeModel getJvmModel() {
        return jvmModel;
    }

    /**
     * Returns library data.
     *
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getLibraryData() {
        return libraryData;
    }

    /**
     * Returns tree model for library data.
     *
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public DefaultKTreeModel getLibraryModel() {
        return libraryModel;
    }

    /**
     * Returns main class for installation.
     *
     * @param path java.lang.String
     * @return java.lang.String
     */
    public String getMainClass(java.lang.String path) {

        DefinitionNode node = findApplicationNode(path);
        if (node == null) {
            return null;
        }

        return ((Application) node).getMainClass();
    }

    public DefinitionNode findApplicationNode(String path) {

        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application)) {
            JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
                    path + " is not an application",
                    "Java Workspace Installer",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }

        return node;
    }

    /**
     * Get human readable name for installer
     */
    public String getName() {
        return "Java Workspace Installer Engine (R) v0.83";
    }

    /**
     * Returns working directory for new process.
     *
     * @param path java.lang.String
     * @return java.lang.String
     */
    public java.lang.String getWorkingDir(java.lang.String path) {

        DefinitionNode node = findApplicationNode(path);
        if (node == null) {
            return null;
        }

        return ((Application) node).getWorkingDirectory();
    }

    /**
     * Returns flag, that tells Workspace to launch this application on startup.
     * Usually, this flag should set to "true" for services like network connection or system clocks.
     *
     * @param path java.lang.String
     * @return java.lang.String
     */
    @Override
    public boolean isLoadedAtStartup(java.lang.String path) {

        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application)) {
            return false;
        }

        return ((Application) node).isLoadedAtStartup();
    }

    /**
     * Returns flag, that tells Workspace to launch this application
     * in separate process. Usually, this flag should set to "true" for external java applications.
     *
     *  // TODO: provide security manager if set to false and the sandbox is enabled for a third party application
     *
     * @param path java.lang.String
     * @return java.lang.String
     */
    @Override
    public boolean isSeparateProcess(java.lang.String path) {
        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application)) return true;

        return ((Application) node).isSeparateProcess();
    }

    /**
     * Load profile data.
     */
    @Override
    public void load() {

        WorkspaceInstaller.LOG.info("> Loading installer");

        dataRoot = new File(Workspace.getUserHome());

        if (!dataRoot.exists()) {
            dataRoot.mkdirs();
            dataRoot.mkdir();
        }
        // create global data models

        applicationData = new ApplicationDataSource(new File(WorkspaceInstaller.dataRoot, ApplicationDataSource.ROOT));
        applicationModel = new ExternalKTreeModel<>(applicationData);

        libraryData = new LibraryDataSource(new File(WorkspaceInstaller.dataRoot, LibraryDataSource.ROOT));
        libraryModel = new ExternalKTreeModel<>(libraryData);

        jvmData = new JVMDataSource(new File(WorkspaceInstaller.dataRoot, JVMDataSource.ROOT));
        /*
         * Install default virtual machine
         */
        JVM jvm = new JVM(jvmData.getRoot(), "current_jvm");
        try {
            jvm.setName("default jvm");
            jvm.setDescription("the jvm this instance of workspace is currently running");
            if (SysUtil.isWindows()) {
                jvm.setPath(System.getProperty("java.home") + File.separator
                        + "bin" + File.separator + "java.exe");
            } else if (SysUtil.isSunOS()) {
                jvm.setPath(System.getProperty("java.home") + File.separator
                        + "bin" + File.separator + "java");
            }
            jvm.setVersion(System.getProperty("java.version"));
            jvm.setArguments("-cp %c %m %a");
            jvm.save();
            jvmData.getRoot().add(jvm);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        jvmModel = new ExternalKTreeModel<>(jvmData);
        /*
         * Load applications
         */
        DefinitionNode root = applicationData.getRoot();

        startupLaunch(applicationData.getChildren(root));

        WorkspaceInstaller.LOG.info("> Installer is loaded");
    }

    /**
     * Loads recursively all applications, that have launch at startup flag.
     */
    private void startupLaunch(DefinitionNode[] nodes) {

        for (DefinitionNode node : nodes) {

            if (node instanceof Application) {
                if (((Application) node).isLoadedAtStartup()) {
                    if (((Application) node).isSeparateProcess()) {
                        Workspace.getRuntimeManager().
                                executeExternalProcess(((Application) node).getInvocationArgs(),
                                        ((Application) node).getWorkingDirectory(),
                                        ((Application) node).getName());
                    }
                    /*
                     * TODO implement the sandbox for a third party application
                     */
                }
            } else {
                startupLaunch(applicationData.getChildren(node));
            }
        }
    }

    /**
     * Resets initial state.
     */
    @Override
    public void reset() {
    }

    /**
     * Saves profiles data. But there is no actually data to save, 'couse everything is already written on disk.
     */
    @Override
    public void save() {
    }
}
