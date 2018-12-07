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

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import com.hyperrealm.kiwi.util.BooleanHolder;

/**
 * A table cell renderer for displaying boolean values. Renders a value,
 * which may be either a <code>Boolean</code> object or a
 * <code>BooleanHolder</code> object, as a non-editable checkbox.
 *
 * @author Mark Lindner
 * @see Boolean
 * @see com.hyperrealm.kiwi.util.BooleanHolder
 */

public class BooleanCellRenderer extends AbstractCellRenderer {
    private JCheckBox renderer;

    /**
     * Construct a new <code>BooleanCellRenderer</code>
     */

    public BooleanCellRenderer() {
        renderer = new JCheckBox();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Get a reference to the renderer component.
     */

    protected JComponent getCellRenderer(JComponent component, Object value,
                                         int row, int column) {
        boolean flag;
        if (value instanceof BooleanHolder) {
            flag = ((BooleanHolder) value).getValue();
        } else if (value instanceof Boolean) {
            flag = (Boolean) value;
        } else {
            flag = false;
        }

        renderer.setSelected(flag);

        return (renderer);
    }

}
