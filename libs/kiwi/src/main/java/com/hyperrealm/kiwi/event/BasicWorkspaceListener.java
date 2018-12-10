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

package com.hyperrealm.kiwi.event;

/**
 * A trivial implementation of <code>WorkspaceListener</code>, with all
 * methods implemented as no-ops.
 *
 * @author Mark Lindner
 */

public class BasicWorkspaceListener implements WorkspaceListener {

    /**
     * Construct a new <code>BasicWorkspaceListener</code>.
     */

    public BasicWorkspaceListener() {
    }

    /**
     * Handle an <i>editor selected</i> event.
     */

    public void editorSelected(WorkspaceEvent evt) {
    }

    /**
     * Handle an <i>editor deselected</i> event.
     */

    public void editorDeselected(WorkspaceEvent evt) {
    }

    /**
     * Handle an <i>editor restored</i> event.
     */

    public void editorRestored(WorkspaceEvent evt) {
    }

    /**
     * Handle an <i>editor iconified</i> event.
     */

    public void editorIconified(WorkspaceEvent evt) {
    }

    /**
     * Handle an <i>editor closed</i> event.
     */

    public void editorClosed(WorkspaceEvent evt) {
    }

    /**
     * Handle an <i>editor state changed</i> event.
     */

    public void editorStateChanged(WorkspaceEvent evt) {
    }

}
