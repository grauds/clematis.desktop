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
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.hyperrealm.kiwi.plugin.Plugin;

public class UninstallButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private final JButton button = new JButton();
    private JTable table;
    private int row;
    private final List<IPluginUninstallActionListener> listeners = new ArrayList<>();

    public UninstallButtonEditor() {
        button.addActionListener(e -> onClick());
    }

    private void onClick() {
        Plugin p = ((PluginsTableModel) table.getModel()).getItem(row);
        fireActionPerformedEvent(p);
    }

    public void addPluginUninstallActionListener(IPluginUninstallActionListener listener) {
        this.listeners.add(listener);
    }

    private void fireActionPerformedEvent(Plugin p) {
        for (IPluginUninstallActionListener l : listeners) {
            l.pluginUninstallSelected(p);
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}
