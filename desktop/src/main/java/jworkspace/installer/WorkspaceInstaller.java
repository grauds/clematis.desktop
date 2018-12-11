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
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.ui.model.DefaultKTreeModel;
import com.hyperrealm.kiwi.ui.model.ExternalKTreeModel;
import com.hyperrealm.kiwi.util.StringUtils;

import jworkspace.api.InstallEngine;
import jworkspace.kernel.Workspace;

/**
 * Install engine is one of required by kernel.
 *
 * @author Anton Troshin
 */
public class WorkspaceInstaller implements InstallEngine {

    /**
     * File extension for configuration file.
     */
    static final String FILE_EXTENSION = ".cfg";
    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(Workspace.class);
    /**
     * Datasources for all types of entries.
     */
    private DefinitionDataSource jvmData;

    private DefinitionDataSource libraryData;

    private DefinitionDataSource applicationData;

    /**
     * Dynamic tree models are used by UI components
     * to build visual tree against virtual models.
     */
    private DefaultKTreeModel jvmModel, libraryModel, applicationModel;
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
        if (node == null) {
            return null;
        }

        return getInvocationArgs(((Application) node));
    }

    /**
     * Load libraries from configuration file.
     */
    private Enumeration loadLibraries(Application application) {

        Vector<DefinitionNode> libs = new Vector<>();
        String[] linkPaths = StringUtils.split(application.getLibList(), ",");
        for (String linkPath : linkPaths) {
            DefinitionNode node = libraryData.findNode(linkPath);
            if (node != null) {
                libs.addElement(node);
            }
        }
        return libs.elements();
    }

    /**
     * Returns command line configured
     * to launch application.
     * @param application application
     */
    private String[] getInvocationArgs(Application application) {

        Vector<String> v = new Vector<>();

        // first get the VM information

        JVM jvmProg = (JVM) jvmData.findNode(application.getJVM());
        if (jvmProg == null) {
            return null;
        }
        v.addElement(jvmProg.getPath());

        if (!application.isSystemUserFolder()) {
            v.addElement("-Duser.home=" + System.getProperty("user.dir") + File.separator
                + Workspace.getProfilesEngine().getPath());
        }
        // next, construct the classpath

        String pathSeparator = System.getProperty("path.separator");
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        sb.append('.');
        Enumeration e = loadLibraries(application);
        while (e.hasMoreElements()) {
            Library lib = (Library) e.nextElement();
            if (sb.length() > 0) {
                sb.append(pathSeparator);
            }
            sb.append(lib.getPath());
        }

        // append the library for the program itself to the classpath

        if (sb.length() > 0) {
            sb.append(pathSeparator);
        }
        sb.append(application.getArchive());
        sb.append('"');
        String classpath = sb.toString();

        // finally, construct the full command line

        StringTokenizer st = new StringTokenizer(jvmProg.getArguments(), Application.JVM_ARGS_DELIMITER);
        while (st.hasMoreTokens()) {
            // expand special tokens

            String arg = st.nextToken();
            switch (arg) {
                case "%c":
                    v.addElement(classpath);
                    break;
                case "%m":
                    v.addElement(application.getMainClass());
                    break;
                case "%a":
                    String[] a = StringUtils.split(application.getArguments(),
                        Application.JVM_ARGS_DELIMITER);
                    for (String s : a) {
                        v.addElement(s);
                    }
                    break;
                default:
                    v.addElement(arg); // other stuff copies literally
                    break;
            }
        }
        String[] argList = new String[v.size()];
        v.copyInto(argList);
        return argList;
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
            JOptionPane.showMessageDialog(Workspace.getUi().getFrame(),
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
     * <p>
     *
     *
     * @param path java.lang.String
     * @return java.lang.String
     */
    // todo: provide security manager if set to false and the sandbox is enabled for a third party application
    @Override
    public boolean isSeparateProcess(java.lang.String path) {
        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application)) {
            return true;
        }

        return ((Application) node).isSeparateProcess();
    }

    /**
     * Load profile data.
     */
    @Override
    public void load() {

        WorkspaceInstaller.LOG.info("> Loading installer");
        /**
         * Current data root - this is defined by user path.
         * <p>
         * BUG: If user path is changed, for example as a result of changing user nick
         * in User Details dialog, Installer loses connection to installation database.
         * This is of course happens because data root does not follow user home path.
         * <p>
         * WORKAROUND: none.
         * HINT: re-login user.
         */
        File dataRoot = new File(Workspace.getUserHome());

        try {

            if (!dataRoot.exists()) {
                FileUtils.forceMkdir(dataRoot);
            }

            // create global data models
            applicationData = new ApplicationDataSource(new File(dataRoot,
                ApplicationDataSource.ROOT));
            applicationModel = new ExternalKTreeModel<>(applicationData);

            libraryData = new LibraryDataSource(new File(dataRoot, LibraryDataSource.ROOT));
            libraryModel = new ExternalKTreeModel<>(libraryData);

            jvmData = new JVMDataSource(new File(dataRoot, JVMDataSource.ROOT));
            /*
             * Install default virtual machine
             */
            JVM jvm = new JVM(jvmData.getRoot(), "current_jvm");

            jvm.setName("default jvm");
            jvm.setDescription("the jvm this instance of workspace is currently running");
            jvm.setPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
            jvm.setVersion(System.getProperty("java.version"));
            jvm.setArguments("-cp %c %m %a");
            jvm.save();
            jvmData.getRoot().add(jvm);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
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
    @SuppressWarnings("NestedIfDepth")
    private void startupLaunch(DefinitionNode[] nodes) {

        for (DefinitionNode node : nodes) {

            if (node instanceof Application) {
                if (((Application) node).isLoadedAtStartup()) {
                    if (((Application) node).isSeparateProcess()) {
                        Workspace.getRuntimeManager().
                            executeExternalProcess(
                                getInvocationArgs((Application) node),
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
