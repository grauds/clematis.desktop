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

import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.hyperrealm.kiwi.util.IntegerHolder;

/**
 * A cell renderer that displays one of a set of status icons. The renderer
 * assumes that the values being rendered are objects of type
 * <code>Integer</code> or <code>com.hyperrealm.kiwi.util.IntegerHolder</code>;
 * for values of other types, the renderer displays nothing.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class StatusIconCellRenderer extends AbstractCellRenderer
    implements ListCellRenderer, TableCellRenderer {

    private Icon[] icons;

    private JLabel renderer;

    /**
     * Construct a new <code>StatusIconCellRenderer</code>.
     *
     * @param icons The set of icons that represent the various states. The array
     *              must contain at least one ICON.
     * @throws IllegalArgumentException If <code>icons</code> is
     *                                  <code>null</code> or a zero-length array.
     */

    public StatusIconCellRenderer(Icon[] icons) {
        renderer = new JLabel();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        if ((icons == null) || (icons.length < 1)) {
            throw (new IllegalArgumentException("Cannot be null or an empty array."));
        }

        this.icons = Arrays.copyOf(icons, icons.length);
    }

    /**
     * Get the actual cell renderer component.
     */

    protected JComponent getCellRenderer(JComponent component, Object value,
                                         int row, int column) {
        int val = -1;

        if (value != null) {
            if (value.getClass() == Integer.class) {
                val = (Integer) value;
            } else if (value.getClass() == IntegerHolder.class) {
                val = ((IntegerHolder) value).getValue();
            }
        }

        if ((val < 0) || (val >= icons.length)) {
            renderer.setIcon(null);
        } else {
            renderer.setIcon(icons[val]);
        }

        return (renderer);
    }

}
