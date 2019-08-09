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
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.Icon;

import com.hyperrealm.kiwi.io.ConfigFile;
//
import jworkspace.kernel.Workspace;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Application entry is a definition node, that stores
 * its data in file on disk, which is in file hierarchy
 * inside applications root directory. This class
 * also calculates proper classpath for java application
 * its presenting.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Application extends DefinitionNode {

    static final String JVM_ARGS_DELIMITER = " ";

    private static final Icon ICON = Workspace.getResourceManager().getIcon("installer/application.gif");

    private static final String CK_NAME = "application.name",
        CK_VERSION = "application.version",
        CK_ARCHIVE = "application.archive",
        CK_SOURCE = "application.source",
        CK_JVM = "application.jvm",
        CK_MAINCLASS = "application.mainclass",
        CK_ARGS = "application.arguments",
        CK_LIBS = "application.libraries",
        CK_DESCRIPTION = "application.description",
        CK_WORKINGDIR = "application.working_dir",
        CK_DOCDIR = "application.documentation_dir",
        CK_LAUNCH_AT_STARTUP = "application.launchAtStartup",
        CK_SEPARATE_PROCESS = "application.separateProcess",
        CK_SYSTEM_USER_FOLDER = "application.systemUserFolder";

    private String name;
    private String version;
    private String jvm;
    private String archive;
    private String mainClass;
    private String arguments;
    private String workingDirectory;
    private String libList = "";
    private String description;
    private String source;
    private String docs;

    private boolean launchAtStartup = false;
    private boolean separateProcess = true;
    /**
     * Set this flag to true to allow to spawn new process with virtual machine which has "user.home" property
     * set to which Java Workspace uses, rather than OS. In this case all user data of spawned application
     * will be stored in workspace user folder.
     */
    private boolean systemUserFolder = false;

    private ConfigFile config;

    /**
     * Public application constructor.
     *
     * @param parent node jworkspace.installer.DefinitionNode
     * @param file   to hold application data java.io.File
     */
    public Application(DefinitionNode parent, File file) throws IOException {
        super(parent, file);
        load();
        this.name = getNodeName();
    }

    /**
     * Public application constructor.
     *
     * @param parent node jworkspace.installer.DefinitionNode
     * @param name   of file to hold application data java.lang.String
     */
    public Application(DefinitionNode parent, String name) {
        super(parent, name + WorkspaceInstaller.FILE_EXTENSION);
        this.name = name;
    }

    /**
     * Sets path to application jar file.
     */
    public void setArchive(String archive) throws InstallationException {
        if (archive == null) {
            throw new InstallationException("Archive name is null.");
        }
        this.archive = archive;
    }

    /**
     * Returns closed ICON to represent
     * application in tree control.
     */
    public Icon getClosedIcon() {
        return ICON;
    }

    /**
     * Sets the name of jvm, which will be used with
     * this application.
     *
     * @param jvm java.lang.String
     */
    public void setJVM(String jvm) throws InstallationException {
        if (jvm == null) {
            throw new InstallationException("Jvm is null.");
        }
        this.jvm = jvm;
    }

    /**
     * Sets main class for this application. This must
     * be a fully qualified class name, for example
     * <code>java.lang.Object</code>.
     */
    public void setMainClass(String mainClass) throws InstallationException {
        if (mainClass == null) {
            throw new InstallationException("Main Class is null");
        }
        this.mainClass = mainClass;
    }

    /**
     * Sets human readable name of application.
     */
    public void setName(String name) throws InstallationException {
        if (name == null) {
            throw new InstallationException("Name is null");
        }
        this.name = name;
    }

    /**
     * Returns open ICON to represent
     * application in tree control.
     */
    public Icon getOpenIcon() {
        return ICON;
    }

    /**
     * Sets version of application. This can be useful for
     * user, as version is not recognized by installer.
     */
    public void setVersion(String version) throws InstallationException {
        if (version == null) {
            throw new InstallationException("Version is null");
        }
        this.version = version;
    }

    /**
     * Sets application working directory. This makes workspace to change working directory while launching
     * application to this path, returning afterwards to its original working directory. It is necessary
     * for spawned process, as it has to find resources, relative to working directory. This is
     * the only case, then workspace uses its native library.
     */
    public void setWorkingDirectory(String dir) throws InstallationException {
        if (dir == null) {
            throw new InstallationException("Working Directory is null");
        }
        this.workingDirectory = dir;
    }

    /**
     * Indicates that this is a leaf,
     * not a branch, as it is cannot
     * be expanded.
     */
    public boolean isExpandable() {
        return false;
    }

    /**
     * Returns whether if this application should be launched
     * at user login.
     */
    public boolean isLoadedAtStartup() {
        return launchAtStartup;
    }

    /**
     * Set this flag to true to allow workspace
     * to launch this application everytime
     * current user log into the system.
     */
    public void setLoadedAtStartup(boolean launchAtStartup) {
        this.launchAtStartup = launchAtStartup;
    }

    /**
     * Loads class data from configuration file
     */
    @SuppressWarnings("Duplicates")
    public void load() throws IOException {

        config = new ConfigFile(file, "Application Definition");
        config.load();
        name = config.getString(CK_NAME, "");
        version = config.getString(CK_VERSION, "");
        archive = config.getString(CK_ARCHIVE, "");
        source = config.getString(CK_SOURCE, "");
        jvm = config.getString(CK_JVM, "");
        mainClass = config.getString(CK_MAINCLASS, "");
        arguments = config.getString(CK_ARGS, "");
        libList = config.getString(CK_LIBS, "");
        docs = config.getString(CK_DOCDIR, "");
        description = config.getString(CK_DESCRIPTION, "");
        workingDirectory = config.getString(CK_WORKINGDIR, ".");
        launchAtStartup = config.getBoolean(CK_LAUNCH_AT_STARTUP, false);
        separateProcess = config.getBoolean(CK_SEPARATE_PROCESS, false);
        systemUserFolder = config.getBoolean(CK_SYSTEM_USER_FOLDER, false);
    }

    /**
     * Stores class data to configuration file
     */
    @SuppressWarnings("Duplicates")
    public void save() throws IOException {
        if (config == null) {
            config = new ConfigFile(file, "Application definition");
        }
        config.putString(CK_NAME, name);
        config.putString(CK_VERSION, version);
        config.putString(CK_ARCHIVE, archive);
        config.putString(CK_SOURCE, source);
        config.putString(CK_JVM, jvm);
        config.putString(CK_MAINCLASS, mainClass);
        config.putString(CK_ARGS, arguments);
        config.putString(CK_LIBS, libList);
        config.putString(CK_DOCDIR, docs);
        config.putString(CK_DESCRIPTION, description);
        config.putString(CK_WORKINGDIR, workingDirectory);
        config.putBoolean(CK_LAUNCH_AT_STARTUP, launchAtStartup);
        config.putBoolean(CK_SEPARATE_PROCESS, separateProcess);
        config.putBoolean(CK_SYSTEM_USER_FOLDER, systemUserFolder);
        config.store();
    }

    /**
     * Sets list of libraries that are nessesary for this application.
     *
     * @param libs to set to the resulting string
     */
    public void setLibraryList(Enumeration libs) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        while (libs.hasMoreElements()) {
            Library l = (Library) libs.nextElement();
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(l.getLinkString());
        }
        libList = sb.toString();
    }

    /**
     * Returns brief library info, that is used
     * in installer configuration dialogs.
     */
    public String toString() {
        return (name + JVM_ARGS_DELIMITER + version);
    }
}