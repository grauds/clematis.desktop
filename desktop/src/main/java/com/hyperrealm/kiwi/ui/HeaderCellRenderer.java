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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.EAST_POSITION;

/**
 * A customized header table cell renderer that left-justifies the header
 * text and may optionally render an icon.
 *
 * @author Mark Lindner
 */

public class HeaderCellRenderer extends JPanel implements TableCellRenderer {

    private static final Border HEADER_BORDER = new CompoundBorder(new BevelBorder(BevelBorder.RAISED),
        new EmptyBorder(0, 3, 0, 3));

    private JLabel lText, lIcon;

    /**
     * Construct a new <code>HeaderCellRenderer</code>.
     */

    public HeaderCellRenderer() {
        setOpaque(false);
        setBorder(HEADER_BORDER);
        setLayout(new BorderLayout(0, 0));

        lText = new KLabel();
        lText.setVerticalTextPosition(SwingConstants.CENTER);
        lText.setHorizontalTextPosition(SwingConstants.LEFT);
        add(CENTER_POSITION, lText);

        lIcon = new KLabel();
        add(EAST_POSITION, lIcon);
    }

    /**
     * Set the icon for the renderer. The icon is rendered against the right
     * edge of the renderer.
     *
     * @param icon The icon.
     */

    public void setIcon(Icon icon) {
        lIcon.setIcon(icon);
    }

    /**
     * Set the alignment for the renderer's text; should be one of
     * <code>SwingConstants.LEFT</code>, <code>SwingConstants.CENTER</code>,
     * <code>SwingConstants.RIGHT</code>.
     *
     * @since Kiwi 2.4
     */

    public void setTextAlignment(int alignment) {
        lText.setHorizontalAlignment(alignment);
    }

    /**
     * Get the cell renderer component.
     */

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus, int row,
                                                   int column) {
        lText.setText(value.toString());

        return (this);
    }

}
