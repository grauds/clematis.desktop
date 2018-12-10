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

import javax.swing.JComponent;

/**
 * An interface that prescribes common behavior for editor components.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public interface Editor<T> {
    /**
     * Get the object being edited.
     *
     * @return The object.
     */

    T getObject();

    /**
     * Set the object to be edited.
     *
     * @param obj The object to edit.
     */

    void setObject(T obj);

    /**
     * Clear the editor. Equivalent to calling <code>setObject(null)</code>.
     */

    void clear();

    /**
     * Get the editor component.
     */

    JComponent getEditorComponent();

    /**
     * Validate the input in the editor.
     */

    boolean validateInput();

    /**
     * Enable or disable the editor.
     */

    void setEnabled(boolean enabled);

}
