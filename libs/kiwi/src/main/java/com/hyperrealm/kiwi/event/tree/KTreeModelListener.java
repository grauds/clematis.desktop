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

package com.hyperrealm.kiwi.event.tree;

import java.util.EventListener;

/**
 * Event listener interface for <code>KTreeModelEvent</code>s.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public interface KTreeModelListener extends EventListener {
    /**
     * Invoked after a new child is added to an existing node in the
     * hierarchy.
     */

    void nodesAdded(KTreeModelEvent evt);

    /**
     * Invoked after a child is removed from an existing node in the
     * hierarchy.
     */

    void nodesRemoved(KTreeModelEvent evt);

    /**
     * Invoked after a node in the tree changes in some way.
     */

    void nodesChanged(KTreeModelEvent evt);

    /**
     * Invoked after the subtree rooted at an existing node changes its
     * structure in a way that can't be described efficiently using any of the
     * other messages in this interface.
     */

    void structureChanged(KTreeModelEvent evt);

    /**
     * Invoked after the entire tree structure has changed (typically after
     * the root node has changed).
     */

    void dataChanged(KTreeModelEvent evt);
}

