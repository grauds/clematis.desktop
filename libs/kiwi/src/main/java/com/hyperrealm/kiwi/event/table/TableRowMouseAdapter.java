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

package com.hyperrealm.kiwi.event.table;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

/**
 * A simple extension of <code>MouseAdapter</code> for handling single-
 * and double-clicks on rows in a <code>JTable</code>.
 *
 * <p>
 * The following example illustrates how this adapter might be used:
 * <p>
 * <pre>
 * table.addMouseListener(new TableRowMouseAdapter()
 * {
 *   public void rowClicked(int row, int button)
 *   {
 *     LOG.warn("Button " + button + " clicked on row #" + row);
 *   }
 * });
 * </pre>
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */
@SuppressWarnings("unused")
public class TableRowMouseAdapter extends MouseAdapter {

    /**
     * Construct a new <code>TableRowMouseAdapter</code>.
     */

    public TableRowMouseAdapter() {
    }

    /**
     * Handle a mouse event. This method dispatches the mouse event to one of
     * the click handlers, based on its click count.  It is assumed that the
     * source of the event is an instance of <code>JTable</code>; if it is not,
     * the event is ignored.
     *
     * @param evt The event.
     */

    public final void mouseClicked(MouseEvent evt) {
        Object source = evt.getSource();
        if (!(source instanceof JTable)) {
            return;
        }

        JTable table = (JTable) source;

        int selRow = table.rowAtPoint(new Point(evt.getX(), evt.getY()));
        if (selRow != -1) {
            switch (evt.getClickCount()) {
                case 1:
                    rowClicked(selRow, evt.getButton());
                    break;

                case 2:
                    rowDoubleClicked(selRow, evt.getButton());
                    break;

                default:
            }
        }
    }

    /**
     * Handle a single-click on a row.
     *
     * @param row    The index of the row that was clicked on.
     * @param button The mouse button that was clicked.
     */

    public void rowClicked(int row, int button) {
    }

    /**
     * Handle a double-click on a row.
     *
     * @param row    The index of the row that was clicked on.
     * @param button The mouse button that was clicked.
     */

    public void rowDoubleClicked(int row, int button) {
    }

}
