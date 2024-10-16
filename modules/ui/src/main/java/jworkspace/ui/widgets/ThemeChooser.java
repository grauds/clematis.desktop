package jworkspace.ui.widgets;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.metal.MetalTheme;

/**
 * Chooser for metal themes in look and feel
 * @author Anton Troshin
 */
public class ThemeChooser extends JComboBox<MetalTheme> {
    /**
     * Construct a new <code>ThemeChooser</code>.
     */
    public ThemeChooser() {
        super();
        setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus
            ) {
                Component comp = super.getListCellRendererComponent(list, value,
                    index, isSelected, cellHasFocus);
                if (value instanceof MetalTheme theme) {
                    setText(theme.getName());
                }
                return comp;
            }
        });
    }

    /**
     * Get the currently selected theme.
     *
     * @return The <code>MetalTheme</code> object corresponding to the
     * currently-selected theme.
     */
    public String getTheme() {
        if (getSelectedItem() != null) {
            return getSelectedItem().getClass().getName();
        }
        return null;
    }
}
