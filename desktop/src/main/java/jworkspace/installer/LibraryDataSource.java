package jworkspace.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-1999 Mark A. Lindner,
          2000-2018 Anton Troshin

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

/**
 * Data source for library.
 */
class LibraryDataSource extends DefinitionDataSource {

    static final String ROOT = "libraries";

    private static String rootName = LibraryDataSource.ROOT;

    /**
     * Construct new library data source
     * with given file as a root.
     *
     * @param root java.io.File
     */
    LibraryDataSource(File root) {
        super(root);
        rootName = root.getName();
    }

    /**
     * Returns root name for libraries hierarchy.
     */
    public String getRootName() {
        return rootName;
    }

    /**
     * Make node with hierarchical support, the node
     * itself is a library.
     */
    protected DefinitionNode makeNode(DefinitionNode parent, File file)
        throws IOException {
        return (new Library(parent, file));
    }
}