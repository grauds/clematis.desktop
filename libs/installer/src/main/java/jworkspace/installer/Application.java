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
import java.util.List;

import javax.swing.Icon;

import com.hyperrealm.kiwi.io.ConfigFile;

import jworkspace.api.DefinitionNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Application entry is a definition node, that stores its data in a file, which is in file hierarchy
 * inside applications root directory. This class also calculates proper classpath for java application
 * its presenting.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class Application extends DefinitionNode {

    public static final String CK_NAME = "application.name",
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

    private static final Icon ICON = WorkspaceInstaller.getResourceManager().getIcon("application.gif");

    private static final String APPLICATION_DEFINITION_CONFIG_HEADER = "Application Definition";

    private String name;

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
     * @param parent node {@link DefinitionNode
     * @param file   to hold application data java.io.File
     */
    public Application(DefinitionNode parent, File file) {
        super(parent, file);
        this.name = getNodeName();
    }

    /**
     * Public application constructor.
     *
     * @param parent node {@link DefinitionNode
     * @param name   of file to hold application data java.lang.String
     */
    public Application(DefinitionNode parent, String name) {
        super(parent, name + DefinitionNode.FILE_EXTENSION);
        this.name = name;
    }

    /**
     * Returns closed ICON to represent
     * application in tree control.
     */
    public Icon getClosedIcon() {
        return ICON;
    }

    /**
     * Returns open ICON to represent
     * application in tree control.
     */
    public Icon getOpenIcon() {
        return ICON;
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
     * Composes library list
     *
     * @param libs to set to the resulting string
     */
    public void createLibraryList(List<Library> libs) {
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
        return (name + " " + version);
    }
}