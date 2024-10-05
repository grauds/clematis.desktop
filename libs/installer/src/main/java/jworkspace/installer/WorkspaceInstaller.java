package jworkspace.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000-2024 Anton Troshin

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.ResourceManager;
import com.hyperrealm.kiwi.util.StringUtils;

import jworkspace.api.DefinitionDataSource;
import jworkspace.api.DefinitionNode;
import jworkspace.api.IWorkspaceComponent;
import lombok.Getter;
import lombok.Setter;

/**
 * Software installer workspace component
 */
@Getter
@Setter
public class WorkspaceInstaller implements IWorkspaceComponent {

    static final String JVM_ARGS_DELIMITER = " ";

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceInstaller.class);
    /**
     * Resource manager for installer resources
     */
    private static ResourceManager resourceManager = null;
    /**
     * Root folder for the data
     */
    private File dataRoot;
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
     * Public constructor with custom data root
     * @param dataRoot - a folder to store information in
     */
    public WorkspaceInstaller(File dataRoot) {
        super();
        this.dataRoot = dataRoot;
        reset();
    }

    public static synchronized ResourceManager getResourceManager() {
        if (resourceManager == null) {
            resourceManager = new ResourceManager(WorkspaceInstaller.class);
        }
        return resourceManager;
    }

    /**
     * Load profile data.
     */
    @Override
    public void load() throws IOException {

        LOG.info("> Loading installer");
        /*
         * Load data sources
         */
        applicationData.getRoot().load();
        libraryData.getRoot().load();
        jvmData.getRoot().load();
        /*
         * Register the current virtual machine as a default
         */
        if (jvmData.getChildren(jvmData.getRoot()).length == 0) {

            JVM jvm = JVM.getCurrentJvm(jvmData.getRoot());

            jvmData.getRoot().add(jvm);
        }

        LOG.info("> Installer is loaded");
    }

    /**
     * Resets initial state.
     */
    @Override
    public void reset() {
        applicationData = new ApplicationDataSource(new File(dataRoot, ApplicationDataSource.ROOT));
        libraryData = new LibraryDataSource(new File(dataRoot, LibraryDataSource.ROOT));
        jvmData = new JVMDataSource(new File(dataRoot, JVMDataSource.ROOT));
    }

    /**
     * Saves data state
     */
    @Override
    public void save() throws IOException {

        LOG.info("> Saving installer");
        /*
         * Load data sources
         */
        applicationData.getRoot().save();
        libraryData.getRoot().save();
        jvmData.getRoot().save();

        LOG.info("> Installer is saved");

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
    public String[] getInvocationArgs(Application application) {

        List<String> v = new ArrayList<>();

        // first get the VM information

        JVM jvmProg = (JVM) jvmData.findNode(application.getJvm());
        if (jvmProg == null) {
            return null;
        }
        v.add(jvmProg.getPath());

        // next, construct the classpath
        String pathSeparator = File.pathSeparator;
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        sb.append('.');
        List<Library> libraries = getApplicationLibraries(application);
        for (Library library : libraries) {
            if (!sb.isEmpty()) {
                sb.append(pathSeparator);
            }
            sb.append(library.getPath());
        }

        // append the library for the program itself to the classpath

        if (!sb.isEmpty()) {
            sb.append(pathSeparator);
        }
        sb.append(application.getArchive());
        sb.append('"');
        String classpath = sb.toString();

        // finally, construct the full command line

        StringTokenizer st = new StringTokenizer(jvmProg.getArguments(), JVM_ARGS_DELIMITER);
        while (st.hasMoreTokens()) {
            // expand special tokens

            String arg = st.nextToken();
            switch (arg) {
                case "%c":
                    v.add(classpath);
                    break;
                case "%m":
                    v.add(application.getMainClass());
                    break;
                case "%a":
                    String[] a = StringUtils.split(
                        application.getArguments(), JVM_ARGS_DELIMITER
                    );
                    v.addAll(Arrays.asList(a));
                    break;
                default:
                    v.add(arg); // other stuff copies literally
                    break;
            }
        }
        return v.toArray(new String[0]);
    }

    private DefinitionNode findApplicationNode(String path) {

        DefinitionNode node = applicationData.findNode(path);

        if (!(node instanceof Application)) {
            throw new IllegalArgumentException(path + " is not an application");
        }

        return node;
    }

    /**
     * Get human-readable name for installer
     */
    public String getName() {
        return "Software Installer";
    }
}
