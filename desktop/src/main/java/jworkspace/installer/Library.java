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

import javax.swing.Icon;

import com.hyperrealm.kiwi.io.ConfigFile;
//
import jworkspace.kernel.Workspace;

/**
 * Library entry is a definition node, that stores
 * its data in file on disk, which is in file hierarchy
 * inside libraries root directory.
 *
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
public class Library extends DefinitionNode {

    public static final Icon ICON = Workspace.getResourceManager()
        .getIcon("installer/library.gif");

    private static final String CK_NAME = "library.name",
        CK_VERSION = "library.version",
        CK_PATH = "library.path",
        CK_SOURCE = "library.source",
        CK_DOCDIR = "library.documentation_dir",
        CK_DESCRIPTION = "library.description";

    private static final String LIBRARY_DEFINITION = "Library Definition";

    private String name;
    private String description;
    private String version;
    private String path;
    private String source;
    private String docs;
    private ConfigFile config;

    /**
     * Public library constructor.
     *
     * @param parent node jworkspace.installer.DefinitionNode
     * @param file   to hold library data java.io.File
     */
    public Library(DefinitionNode parent, File file) throws IOException {
        super(parent, file);
        load();
        this.name = getNodeName();
    }

    /**
     * Public library constructor.
     *
     * @param parent node jworkspace.installer.DefinitionNode
     * @param name   of file to hold library data java.lang.String
     */
    public Library(DefinitionNode parent, String name) {
        super(parent, name + WorkspaceInstaller.FILE_EXTENSION);
        this.name = name;
    }

    /**
     * Returns open ICON to represent
     * library in tree control.
     */
    public Icon getClosedIcon() {
        return (ICON);
    }

    /**
     * Returns library description
     */
    public String getDescription() {
        return (description);
    }

    /**
     * Sets description of library. This is optional,
     * as installer does not recognize this.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns directory or jar file, containing
     * library documentation.
     */
    public java.lang.String getDocs() {
        return docs;
    }

    /**
     * Sets directory or jar file, containing
     * library documentation.
     */
    public void setDocs(java.lang.String docs) {
        this.docs = docs;
    }

    /**
     * Returns library name
     */
    public String getName() {
        return (name);
    }

    /**
     * Sets human readable name of library.
     */
    public void setName(String name) throws InstallationException {
        if (name == null) {
            throw new InstallationException("Name is null");
        }
        this.name = name;
    }

    /**
     * Returns open ICON to represent
     * library in tree control.
     */
    public Icon getOpenIcon() {
        return (ICON);
    }

    /**
     * Returns path to library jar or directory.
     */
    public String getPath() {
        return (path);
    }

    /**
     * Sets path to library jar file or directory.
     * This will be a part of classpath for application
     * that will choose to use this library.
     */
    public void setPath(String path) throws InstallationException {
        if (path == null) {
            throw new InstallationException("Path is null");
        }
        this.path = path;
    }

    /**
     * Returns directory or jar file, containing
     * library source.
     */
    public java.lang.String getSource() {
        return source;
    }

    /**
     * Sets directory or jar file, containing
     * library source code.
     */
    public void setSource(java.lang.String source) {
        this.source = source;
    }

    /**
     * Returns version of this library.
     */
    public String getVersion() {
        return (version);
    }

    /**
     * Sets version of library. This can be useful for
     * user, as version is not recognized by installer.
     */
    public void setVersion(String version) throws InstallationException {
        if (version == null) {
            throw new InstallationException("Version is null");
        }
        this.version = version;
    }

    /**
     * Indicates that this is a leaf,
     * not a branch, as it is cannot
     * be expanded.
     */
    public boolean isExpandable() {
        return (false);
    }

    /**
     * Loads class data from disk file
     */
    public void load() throws IOException {
        config = new ConfigFile(file, LIBRARY_DEFINITION);
        config.load();
        name = config.getString(CK_NAME, "");
        version = config.getString(CK_VERSION, "");
        path = config.getString(CK_PATH, "");
        source = config.getString(CK_SOURCE, "");
        docs = config.getString(CK_DOCDIR, "");
        description = config.getString(CK_DESCRIPTION, "");
    }

    /**
     * Stores class data to disk file
     */
    public void save() throws IOException {
        if (config == null) {
            config = new ConfigFile(file, LIBRARY_DEFINITION);
        }
        config.putString(CK_NAME, name);
        config.putString(CK_VERSION, version);
        config.putString(CK_PATH, path);
        config.putString(CK_SOURCE, source);
        config.putString(CK_DOCDIR, docs);
        config.putString(CK_DESCRIPTION, description);
        config.store();
    }

    /**
     * Returns brief library info, that is used
     * in installer configuration dialogs.
     */
    public String toString() {
        return (name + " " + version);
    }
}