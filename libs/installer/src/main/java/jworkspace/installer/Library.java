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
import jworkspace.api.DefinitionNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Library entry is a definition node, that stores
 * its data in file on disk, which is in file hierarchy
 * inside libraries root directory.
 *
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class Library extends DefinitionNode {

    public static final Icon ICON = WorkspaceInstaller.getResourceManager().getIcon("installer/library.gif");

    public static final String CK_NAME = "library.name",
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
     * @param parent node jworkspace.api.DefinitionNode
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
     * @param parent node jworkspace.api.DefinitionNode
     * @param name   of file to hold library data java.lang.String
     */
    public Library(DefinitionNode parent, String name) {
        super(parent, name + DefinitionNode.FILE_EXTENSION);
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
     * Returns open ICON to represent
     * library in tree control.
     */
    public Icon getOpenIcon() {
        return (ICON);
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