package jworkspace.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-1999 Mark A. Lindner,
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

import jworkspace.api.DefinitionDataSource;
import jworkspace.api.DefinitionNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Data source for library.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class LibraryDataSource extends DefinitionDataSource {

    public static final String ROOT = "libraries";

    /**
     * Construct new library data source
     * with given file as a root.
     *
     * @param root java.io.File
     */
    public LibraryDataSource(File root) {
        super(root);
        setName(root != null ? root.getName() : LibraryDataSource.ROOT);
    }

    /**
     * Make node with hierarchical support, the node itself is a library.
     */
    protected DefinitionNode makeNode(DefinitionNode parent, File file) {
        return new Library(parent, file);
    }
}