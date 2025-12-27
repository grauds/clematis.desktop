package jworkspace.ui.runtime.plugin;
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
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;


public class UninstallButtonRenderer extends JButton implements TableCellRenderer {

    public UninstallButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        PluginsTableModel model = (PluginsTableModel) table.getModel();
        Plugin p = model.getItem(row);
        setText("Uninstall");
        setEnabled(PluginDTO.PLUGIN_TYPE_USER.equalsIgnoreCase(p.getType()));

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        return this;
    }
}
