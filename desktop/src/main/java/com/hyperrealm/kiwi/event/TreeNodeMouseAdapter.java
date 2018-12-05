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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * A simple extension of <code>MouseAdapter</code> for handling single-
 * and double-clicks on nodes in a <code>JTree</code>.
 * The following example illustrates how this adapter might be used:
 *
 * <p>
 * <pre>
 * tree.addMouseListener(new TreeNodeMouseAdapter()
 * {
 *   public void itemClicked(TreePath path, int button)
 *   {
 *     LOG.warn("Button " + button + " clicked on node "
 *                        path.getLastPathComponent());
 *   }
 * });
 * </pre>
 *
 * @author Mark Lindner
 */
@SuppressWarnings("unused")
public class TreeNodeMouseAdapter extends MouseAdapter {

    /**
     * Construct a new <code>TreeNodeMouseAdapter</code>.
     */

    public TreeNodeMouseAdapter() {
    }

    /**
     * Handle a mouse event. This method dispatches the mouse event to one of
     * the click handlers, based on its click count.  It is assumed that the
     * source of the event is an instance of <code>JTree</code>; if it is not,
     * the event is ignored.
     *
     * @param evt The event.
     */

    public final void mouseClicked(MouseEvent evt) {
        Object source = evt.getSource();
        if (!(source instanceof JTree)) {
            return;
        }

        JTree tree = (JTree) source;

        int selRow = tree.getRowForLocation(evt.getX(), evt.getY());
        TreePath selPath = tree.getPathForLocation(evt.getX(), evt.getY());
        if (selRow != -1) {
            switch (evt.getClickCount()) {
                case 1:
                    nodeClicked(selPath, evt.getButton());
                    break;

                case 2:
                    nodeDoubleClicked(selPath, evt.getButton());
                    break;

                default:
            }
        }
    }

    /**
     * Handle a single-click on a node.
     *
     * @param path   The path to the node that was clicked on.
     * @param button The mouse button that was clicked.
     */

    public void nodeClicked(TreePath path, int button) {
    }

    /**
     * Handle a double-click on a node.
     *
     * @param path   The path to the node that was clicked on.
     * @param button The mouse button that was clicked.
     */

    public void nodeDoubleClicked(TreePath path, int button) {
    }

}
