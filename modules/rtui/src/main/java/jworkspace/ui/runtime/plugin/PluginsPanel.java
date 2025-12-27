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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;

import static jworkspace.ui.WorkspaceGUI.getResourceManager;
import jworkspace.ui.runtime.LangResource;
import jworkspace.ui.runtime.RuntimeManagerWindow;

public class PluginsPanel extends KPanel {

    private static final String PLUGINS = LangResource.getString("Installed Plugins");
    private final List<IPluginSelectionListener> listeners = new ArrayList<>();
    private final UninstallButtonEditor uninstallButtonEditor = new UninstallButtonEditor();
    private JTable pluginsTable = null;
    private final PluginPanel pluginPanel = new PluginPanel();

    public PluginsPanel() {
        setLayout(new BorderLayout());
        add(createPluginsLabel(), BorderLayout.NORTH);
        add(new JScrollPane(getPluginsTable()), BorderLayout.CENTER);
        add(pluginPanel, BorderLayout.SOUTH);
        this.addPluginSelectionListener(pluginPanel);
    }

    public void addPluginSelectionListener(IPluginSelectionListener l) {
        this.listeners.add(l);
    }

    public void addPluginUninstallActionListener(IPluginUninstallActionListener l) {
        this.uninstallButtonEditor.addPluginUninstallActionListener(l);
    }

    private void fireSelectionEvent(Plugin p) {
        for (IPluginSelectionListener l : listeners) {
            l.pluginSelected(p);
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private JLabel createPluginsLabel() {
        JLabel l = new JLabel();

        l.setBackground(Color.white);
        l.setOpaque(true);
        l.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
            .getResourceAsImage("images/plugin.png")));

        String sb = "<html><font color=black>"
            + PLUGINS + "</font><br><font size=\"-2\" color=black><i>"
            + "Plugins currently in the system" + "</i></font></html>";

        l.setText(sb);

        l.setForeground(Color.black);
        l.setPreferredSize(new Dimension(250, 70));
        l.setMinimumSize(l.getPreferredSize());
        l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        l.setHorizontalAlignment(JLabel.CENTER);

        return l;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public JTable getPluginsTable() {
        if (pluginsTable == null) {
            this.pluginsTable = new JTable(new PluginsTableModel(new ArrayList<>()));
            this.pluginsTable.setRowHeight(35);
            this.pluginsTable.getColumnModel().getColumn(0)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                   boolean hasFocus, int row, int column) {
                        super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column
                        );
                        if (value instanceof Plugin plugin) {
                            setText(plugin.toString());
                            Icon icon = plugin.getIcon();
                            setIcon(Objects.requireNonNullElseGet(icon,
                                () -> new ImageIcon(getResourceManager().getImage("plugin.png"))
                            ));
                        }
                        return this;
                    }
                });

            this.pluginsTable.getColumnModel().getColumn(1).setMinWidth(90);
            this.pluginsTable.getColumnModel().getColumn(1).setMaxWidth(90);

            this.pluginsTable.getColumnModel().getColumn(2).setCellRenderer(new UninstallButtonRenderer());
            this.pluginsTable.getColumnModel().getColumn(2).setCellEditor(uninstallButtonEditor);
            this.pluginsTable.getColumnModel().getColumn(2).setMinWidth(90);
            this.pluginsTable.getColumnModel().getColumn(2).setMaxWidth(120);

            this.pluginsTable.getSelectionModel().addListSelectionListener(e -> {
                int row = this.pluginsTable.getSelectedRow();
                if (row < 0) {
                    return;
                }
                Plugin pl = ((PluginsTableModel) this.pluginsTable.getModel()).getItem(row);
                fireSelectionEvent(pl);
            });
        }
        return pluginsTable;
    }

    public void setPlugins(List<Plugin> plugins) {
        ((PluginsTableModel) getPluginsTable().getModel()).setPlugins(plugins);
    }
}
