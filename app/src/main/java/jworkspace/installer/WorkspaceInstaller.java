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
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.StringUtils;

import jworkspace.api.IWorkspaceInstaller;
import jworkspace.kernel.Workspace;
import lombok.Data;

/**
 * Install engine is one of required by kernel.
 *
 * @author Anton Troshin
 */
@Data
public class WorkspaceInstaller implements IWorkspaceInstaller {

    /**
     * File extension for configuration file.
     */
    static final String FILE_EXTENSION = ".cfg";
    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceInstaller.class);
    /**
     * JVM datasource
     */
    private DefinitionDataSource jvmData;
    /**
     * Library datasource
     */
    private DefinitionDataSource libraryData;
    /**
     * Application datasource
     */
    private DefinitionDataSource applicationData;

    /**
     * Root folder for the data
     */
    private File dataRoot;

    /**
     * Default public constructor
     */
    public WorkspaceInstaller() {
        super();
        this.dataRoot = new File(Workspace.getBasePath());
        reset();
    }

    /**
     * Public constructor with custom data root
     * @param dataRoot - a folder to store information in
     */
    public WorkspaceInstaller(File dataRoot) {
        super();
        this.dataRoot = dataRoot;
        reset();
    }

    /**
     * Load profile data.
     */
    @Override
    public void load() {

        try {
            WorkspaceInstaller.LOG.info("> Loading installer");
            /*
             * Load data sources
             */
            applicationData.getRoot().load();
            libraryData.getRoot().load();
            jvmData.getRoot().load();
            /*
             * Install default virtual machine
             */
            if (jvmData.getChildren(jvmData.getRoot()).length == 0) {
                JVM jvm = new JVM(jvmData.getRoot(), "current_jvm");

                jvm.setName("default jvm");
                jvm.setDescription("the jvm this instance of workspace is currently running");
                jvm.setPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
                jvm.setVersion(System.getProperty("java.version"));
                jvm.setArguments("-cp %c %m %a");
                jvm.save();

                jvmData.getRoot().add(jvm);
            }

            WorkspaceInstaller.LOG.info("> Installer is loaded");

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Resets initial state.
     */
    @Override
    public void reset() {
        /*
         * (Re)set data sources
         */
        applicationData = new ApplicationDataSource(new File(dataRoot, ApplicationDataSource.ROOT));
        libraryData = new LibraryDataSource(new File(dataRoot, LibraryDataSource.ROOT));
        jvmData = new JVMDataSource(new File(dataRoot, JVMDataSource.ROOT));
    }

    /**
     * Saves data state
     */
    @Override
    public void save() {
        try {
            WorkspaceInstaller.LOG.info("> Saving installer");
            /*
             * Load data sources
             */
            applicationData.getRoot().save();
            libraryData.getRoot().save();
            jvmData.getRoot().save();

            WorkspaceInstaller.LOG.info("> Installer is saved");

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Returns command line for application, found by its path. Path should be
     * divided by "/" delimiters in UNIX style, from current user's installation directories.
     * <p>
     * For example, path can be as follows "/programs/My apps/My app"
     *
     * @param path String
     * @return String
     */
    public String[] getInvocationArgs(String path) {

        DefinitionNode node = findApplicationNode(path);
        return getInvocationArgs(((Application) node));
    }

    /**
     * Load libraries from configuration file.
     */
    @Override
    public List<Library> getApplicationLibraries(Application application) {

        List<Library> libs = new LinkedList<>();
        String[] linkPaths = StringUtils.split(application.getLibraryList(), ",");
        for (String linkPath : linkPaths) {
            DefinitionNode node = libraryData.findNode(linkPath);
            if (node instanceof Library) {
                libs.add((Library) node);
            }
        }
        return libs;
    }

    /**
     * Returns command line configured to launch application.
     *
     * @param application application
     */
    @Override
    public String[] getInvocationArgs(Application application) {

        Vector<String> v = new Vector<>();

        // first get the VM information

        JVM jvmProg = (JVM) jvmData.findNode(application.getJvm());
        if (jvmProg == null) {
            return null;
        }
        v.addElement(jvmProg.getPath());

        // next, construct the classpath
        String pathSeparator = System.getProperty("path.separator");
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        sb.append('.');
        List<Library> libraries = getApplicationLibraries(application);
        for (Library library : libraries) {
            if (sb.length() > 0) {
                sb.append(pathSeparator);
            }
            sb.append(library.getPath());
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

    private DefinitionNode findApplicationNode(String path) {

        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application)) {
            throw new IllegalArgumentException(path + " is not an application");
        }

        return node;
    }

    /**
     * Get human readable name for installer
     */
    public String getName() {
        return "Java Workspace Installer v1.0.0";
    }

    /**
     * Returns working directory for new process.
     *
     * @param path String
     * @return String
     */
    public String getApplicationWorkingDir(String path) {

        DefinitionNode node = findApplicationNode(path);
        return ((Application) node).getWorkingDirectory();
    }

}
