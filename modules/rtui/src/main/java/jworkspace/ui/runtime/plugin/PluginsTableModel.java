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
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.hyperrealm.kiwi.plugin.Plugin;

import jworkspace.runtime.plugin.WorkspacePluginLocator;

public class PluginsTableModel extends AbstractTableModel {

    private final List<Plugin> plugins;
    private final String[] columns = {
        "Plugin", "Type", "Level", ""
    };

    public PluginsTableModel(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public int getRowCount() {
        return this.plugins.size();
    }

    public Plugin getItem(int row) {
        return this.plugins.get(row);
    }

    @Override
    public String getColumnName(int c) {
        return columns[c];
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 0 -> Plugin.class;
            case 1, 2, 3 -> String.class;
            default -> Object.class;
        };
    }

    @Override
    public int getColumnCount() {
        return this.columns.length;
    }

    public void setPlugins(List<Plugin> newData) {
        plugins.clear();
        plugins.addAll(newData);
        fireTableDataChanged();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3 && WorkspacePluginLocator.PLUGIN_LEVEL_USER.equalsIgnoreCase(
            this.plugins.get(row).getLevel()
        );
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Object getValueAt(int row, int col) {
        Plugin i = plugins.get(row);
        return switch (col) {
            case 0 -> i;
            case 1 -> i.getType();
            case 2 -> i.getLevel();
            case 3 -> "";
            default -> null;
        };
    }
}
