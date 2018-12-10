/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui;

/**
 * This interface represents a factory that creates appropriate
 * <code>WorkspaceEditor</code>s for specified objects and classes.
 *
 * @author Mark Lindner
 */

public interface WorkspaceEditorFactory {

    /**
     * General-purpose factory method for <code>WorkspaceEditor</code>s. Returns
     * a <code>WorkspaceEditor</code> instance that is appropriate for editing
     * the specified object.
     *
     * @param obj The object to edit.
     * @see #getEditorForType
     */

    WorkspaceEditor getEditorForObject(Object obj);

    /**
     * General-purpose factory method for <code>WorkspaceEditor</code>s. Returns
     * a <code>WorkspaceEditor</code> instance that is appropriate for editing
     * an object of the specified type.
     *
     * @param clazz The type of object to edit.
     * @see #getEditorForObject
     */

    WorkspaceEditor getEditorForType(Class clazz);
}
