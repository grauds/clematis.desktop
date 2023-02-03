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
import java.util.List;

import javax.swing.Icon;

import com.hyperrealm.kiwi.io.ConfigFile;
//
import jworkspace.kernel.Workspace;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

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
@EqualsAndHashCode(callSuper = false)
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
        CK_DOCDIR = "application.documentation_dir";

    private static final String APPLICATION_DEFINITION_CONFIG_HEADER = "Application Definition";

    private String name = "";
    private String version = "";
    private String jvm = "";
    private String archive = "";
    private String mainClass = "";
    private String arguments = "";
    private String workingDirectory = "";
    private String libraryList = "";
    private String description = "";
    private String source = "";
    private String docs = "";

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
    public void setArchive(@NonNull String archive) {
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
    public void setJVM(@NonNull String jvm) {
        this.jvm = jvm;
    }

    /**
     * Sets main class for this application. This must
     * be a fully qualified class name, for example
     * <code>java.lang.Object</code>.
     */
    public void setMainClass(@NonNull String mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * Sets human-readable name of application.
     */
    public void setName(@NonNull String name) {
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
    public void setVersion(@NonNull String version) {
        this.version = version;
    }

    /**
     * Sets application working directory. This makes workspace to change working directory while launching
     * application to this path, returning afterwards to its original working directory. It is necessary
     * for spawned process, as it has to find resources, relative to working directory. This is
     * the only case, then workspace uses its native library.
     */
    public void setWorkingDirectory(@NonNull String dir) {
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
     * Loads class data from configuration file
     */
    @SuppressWarnings("Duplicates")
    public void load() throws IOException {

        config = new ConfigFile(file, APPLICATION_DEFINITION_CONFIG_HEADER);
        config.load();
        name = config.getString(CK_NAME, "");
        version = config.getString(CK_VERSION, "");
        archive = config.getString(CK_ARCHIVE, "");
        source = config.getString(CK_SOURCE, "");
        jvm = config.getString(CK_JVM, "");
        mainClass = config.getString(CK_MAINCLASS, "");
        arguments = config.getString(CK_ARGS, "");
        libraryList = config.getString(CK_LIBS, "");
        docs = config.getString(CK_DOCDIR, "");
        description = config.getString(CK_DESCRIPTION, "");
        workingDirectory = config.getString(CK_WORKINGDIR, ".");
    }

    /**
     * Stores class data to configuration file
     */
    @SuppressWarnings("Duplicates")
    public void save() throws IOException {
        if (config == null) {
            config = new ConfigFile(file, APPLICATION_DEFINITION_CONFIG_HEADER);
        }
        config.putString(CK_NAME, name);
        config.putString(CK_VERSION, version);
        config.putString(CK_ARCHIVE, archive);
        config.putString(CK_SOURCE, source);
        config.putString(CK_JVM, jvm);
        config.putString(CK_MAINCLASS, mainClass);
        config.putString(CK_ARGS, arguments);
        config.putString(CK_LIBS, libraryList);
        config.putString(CK_DOCDIR, docs);
        config.putString(CK_DESCRIPTION, description);
        config.putString(CK_WORKINGDIR, workingDirectory);
        config.store();
    }

    /**
     * Sets list of libraries that are nessesary for this application.
     *
     * @param libs to set to the resulting string
     */
    public void setLibraryList(List<Library> libs) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Library l : libs) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(l.getLinkString());
        }
        libraryList = sb.toString();
    }

    /**
     * Returns brief library info, that is used
     * in installer configuration dialogs.
     */
    public String toString() {
        return (name + JVM_ARGS_DELIMITER + version);
    }
}