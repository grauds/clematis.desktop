package jworkspace.ui.runtime.downloader;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2025 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {

    private boolean isSelected = false;

    public ProgressBarRenderer() {
        setStringPainted(true);
        setBorder(null);
        setOpaque(true);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean selected, boolean hasFocus,
                                                   int row, int column) {
        isSelected = selected; // store selection state for painting

        if (value instanceof Integer) {
            setValue((Integer) value);
            setString(getValue() + "%");
        } else {
            setValue(0);
            setString("0%");
        }

        if (selected) {
            setBackground(table.getSelectionBackground());
            // keep bar color same
            setForeground(new Color(76, 175, 80));
        } else {
            setBackground(table.getBackground());
            setForeground(new Color(76, 175, 80));
        }

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Insets b = getInsets();
        int width = getWidth() - b.left - b.right;
        int height = getHeight() - b.top - b.bottom;

        Graphics2D g2 = (Graphics2D) g.create();

        // Draw background
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw the progress bar
        int barWidth = (int) (width * (getValue() / (double) getMaximum()));
        g2.setColor(getForeground());
        g2.fillRect(b.left, b.top, barWidth, height);

        // Draw string
        if (isStringPainted()) {
            String text = getString();
            g2.setColor(isSelected ? Color.WHITE : Color.BLACK);

            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(text);
            int stringHeight = fm.getAscent();
            int x = (getWidth() - stringWidth) / 2;
            int y = (getHeight() + stringHeight) / 2 - 2;
            g2.drawString(text, x, y);
        }

        g2.dispose();
    }

}