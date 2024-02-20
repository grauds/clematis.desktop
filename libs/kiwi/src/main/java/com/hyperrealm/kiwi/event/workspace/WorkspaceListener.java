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

package com.hyperrealm.kiwi.event.workspace;

import java.util.EventListener;

/**
 * This class represents a listener that is notified of changes in a
 * <code>WorkspaceManager</code>.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.WorkspaceManager
 * @see com.hyperrealm.kiwi.ui.WorkspaceEditor
 */
public interface WorkspaceListener extends EventListener {

    /**
     * Invoked after an editor has been selected in the workspace.
     */

    void editorSelected(WorkspaceEvent evt);

    /**
     * Invoked after an editor has been deselected in the workspace.
     */

    void editorDeselected(WorkspaceEvent evt);

    /**
     * Invoked after an editor has been restored (from an ICON) in the
     * workspace.
     */

    void editorRestored(WorkspaceEvent evt);

    /**
     * Invoked after an editor has been iconified in the workspace.
     */

    void editorIconified(WorkspaceEvent evt);

    /**
     * Invoked after an editor is closed in the workspace.
     */

    void editorClosed(WorkspaceEvent evt);

    /**
     * Invoked after an editor's state has changed in some way.
     */

    void editorStateChanged(WorkspaceEvent evt);
}
