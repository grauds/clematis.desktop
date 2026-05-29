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
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;

import static jworkspace.ui.WorkspaceGUI.getResourceManager;
import jworkspace.runtime.plugin.PluginUpdateChecker;
import jworkspace.runtime.plugin.WorkspacePluginLocator;
import jworkspace.ui.runtime.LangResource;
import jworkspace.ui.runtime.RuntimeManagerWindow;
import jworkspace.ui.runtime.plugin.reports.InstalledPluginReport;
import jworkspace.ui.runtime.plugin.reports.PluginReport;

public class PluginsPanel extends KPanel {

    private static final String PLUGINS = LangResource.getString("Installed Plugins");

    private JTable pluginsTable = null;

    private final List<IPluginSelectionListener> listeners = new ArrayList<>();

    public PluginsPanel() {

        setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

        setLayout(new BorderLayout());
        add(createPluginsLabel(), BorderLayout.NORTH);

        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitter.setOneTouchExpandable(true);
        splitter.setContinuousLayout(true);
        splitter.setLeftComponent(new JScrollPane(getPluginsTable()));

        PluginReport pluginReport = new InstalledPluginReport();
        splitter.setRightComponent(pluginReport);
        add(splitter, BorderLayout.CENTER);

        this.addPluginSelectionListener(pluginReport);
    }

    public void addPluginSelectionListener(IPluginSelectionListener l) {
        this.listeners.add(l);
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
            this.pluginsTable.getColumnModel().getColumn(0).setMinWidth(200);
            this.pluginsTable.getColumnModel().getColumn(0)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber",
                        "checkstyle:NestedIfDepth"})
                    @Override
                    public Component getTableCellRendererComponent(JTable table,
                                                                   Object value,
                                                                   boolean isSelected,
                                                                   boolean hasFocus,
                                                                   int row,
                                                                   int column
                    ) {
                        super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus, row, column
                        );
                        if (value instanceof Plugin plugin) {
                            // Append visual text metadata descriptors cleanly
                            setText(plugin.getTitle());

                            // Fallback icon assignment rules
                            Icon icon = plugin.getIcon();
                            setIcon(Objects.requireNonNullElseGet(icon,
                                () -> new ImageIcon(getResourceManager().getImage("plugin.png"))
                            ));
                        }
                        return this;
                    }
                });

            this.pluginsTable.getColumnModel().getColumn(1).setMinWidth(200);
            this.pluginsTable.getColumnModel().getColumn(1).setCellRenderer(new PluginTableCellRenderer());

            this.pluginsTable.getColumnModel().getColumn(2).setMinWidth(100);
            this.pluginsTable.getColumnModel().getColumn(2).setMaxWidth(100);

            this.pluginsTable.getColumnModel().getColumn(3).setMinWidth(60);
            this.pluginsTable.getColumnModel().getColumn(3).setMaxWidth(60);
            this.pluginsTable.getSelectionModel().addListSelectionListener(e -> {
                int row = this.pluginsTable.getSelectedRow();
                if (row < 0 || e.getValueIsAdjusting()) {
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

    private static class PluginTableCellRenderer extends DefaultTableCellRenderer {

        @SuppressWarnings({
            "checkstyle:MultipleStringLiterals",
            "checkstyle:MagicNumber",
            "checkstyle:NestedIfDepth"
        })
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column
        ) {
            super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column
            );
            if (value instanceof Plugin plugin) {
                Object o = plugin.getProperties().get(PluginUpdateChecker.PLUGIN_HAS_UPDATE);
                boolean hasUpdate = o != null ? (Boolean) o : false;

                Object del = plugin.getProperties().get(WorkspacePluginLocator.PLUGIN_DELETED);
                boolean deleted = del != null ? (Boolean) del : false;

                // Append visual text metadata descriptors cleanly
                if (hasUpdate) {
                    setText(plugin.getVersion() + " (Update Available)");
                } else if (deleted) {
                    setText(plugin.getVersion() + " (Deleted, restart required)");
                } else {
                    setText(plugin.getVersion());
                }

                // Apply prominent component styling cues to immediately capture user focus
                if (deleted) {
                    // Apply gray style for deleted components
                    setBackground(new Color(240, 240, 240)); // Muted background gray
                    setForeground(new Color(140, 140, 140)); // Muted font gray
                } else if (hasUpdate && !isSelected) {
                    // Apply prominent component styling cues to immediately capture user focus
                    setBackground(new Color(235, 247, 235)); // High contrast soft alert tint
                    setForeground(new Color(20, 110, 20));     // Clear emphasis readable font color
                } else if (!isSelected) {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
            }
            return this;
        }
    }

    private static class PluginsTableModel extends AbstractTableModel {

        private final List<Plugin> plugins;
        private final String[] columns = {
            "Plugin", "Version", "Type", "Level"
        };

        PluginsTableModel(List<Plugin> plugins) {
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
                case 0, 1 -> Plugin.class;
                case 2, 3 -> String.class;
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
            return column == 5 && WorkspacePluginLocator.PLUGIN_LEVEL_USER.equalsIgnoreCase(
                this.plugins.get(row).getLevel()
            );
        }

        @SuppressWarnings("checkstyle:MagicNumber")
        @Override
        public Object getValueAt(int row, int col) {
            Plugin i = plugins.get(row);
            return switch (col) {
                case 0, 1 -> i;
                case 2 -> i.getType();
                case 3 -> i.getLevel();
                default -> null;
            };
        }
    }
}
