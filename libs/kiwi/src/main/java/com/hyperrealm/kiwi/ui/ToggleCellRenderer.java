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

import javax.swing.Icon;
import javax.swing.JComponent;

import com.hyperrealm.kiwi.util.BooleanHolder;

/**
 * A cell renderer that displays a boolean value using a ToggleIndicator.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class ToggleCellRenderer extends AbstractCellRenderer {
    private ToggleIndicator toggle;

    /**
     * Construct a new <code>ToggleTableCellRenderer</code>.
     *
     * @param icon    The ICON that represents the <b>true</b> value.
     * @param altIcon The ICON that represents the <b>false</b> value.
     */

    public ToggleCellRenderer(Icon icon, Icon altIcon) {
        toggle = new ToggleIndicator(icon, altIcon);
    }

    /**
     *
     */

    protected JComponent getCellRenderer(JComponent component, Object value,
                                         int row, int column) {
        boolean flag = false;

        if (value == null) {
            flag = false;
        } else if (value.getClass() == Boolean.class) {
            flag = (Boolean) value;
        } else if (value.getClass() == BooleanHolder.class) {
            BooleanHolder b = (BooleanHolder) value;
            flag = b.getValue();
        }

        toggle.setState(flag);

        return (toggle);
    }

}
