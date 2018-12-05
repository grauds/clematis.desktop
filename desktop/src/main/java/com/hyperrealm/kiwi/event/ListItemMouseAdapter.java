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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

/**
 * A simple extension of <code>MouseAdapter</code> for handling single-
 * and double-clicks on items in a <code>JList</code>.
 * <p>
 * The following example illustrates how this adapter might be used:
 * <p>
 * <pre>
 * list.addMouseListener(new ListItemMouseAdapter()
 * {
 *   public void itemClicked(int item, int button)
 *   {
 *      LOG.warn("Button " + button + " clicked on item #" + item);
 *   }
 * });
 * </pre>
 *
 * @author Mark Lindner
 * @since Kiwi 1.4
 */
@SuppressWarnings("unused")
public class ListItemMouseAdapter extends MouseAdapter {

    /**
     * Construct a new <code>ListItemMouseAdapter</code>.
     */
    protected ListItemMouseAdapter() {
    }

    /**
     * Handle a mouse event. This method dispatches the mouse event to one of
     * the click handlers, based on its click count.  It is assumed that the
     * source of the event is an instance of <code>JList</code>; if it is not,
     * the event is ignored.
     *
     * @param evt The event.
     */

    public final void mouseClicked(MouseEvent evt) {

        Object source = evt.getSource();
        if (!(source instanceof JList)) {
            return;
        }

        JList list = (JList) source;

        int selRow = list.locationToIndex(new Point(evt.getX(), evt.getY()));
        if (selRow != -1) {
            switch (evt.getClickCount()) {
                case 1:
                    itemClicked(selRow, evt.getButton());
                    break;

                case 2:
                    itemDoubleClicked(selRow, evt.getButton());
                    break;

                default:
            }
        }
    }

    /**
     * Handle a single-click on an item.
     *
     * @param item   The index of the item that was clicked on.
     * @param button The mouse button that was clicked.
     */

    public void itemClicked(int item, int button) {
    }

    /**
     * Handle a double-click on an item.
     *
     * @param item   The index of the item that was clicked on.
     * @param button The mouse button that was clicked.
     */

    public void itemDoubleClicked(int item, int button) {
    }

}
