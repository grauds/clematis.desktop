package jworkspace.ui.runtime.plugin;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;
import com.hyperrealm.kiwi.plugin.PluginException;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;

import static jworkspace.runtime.downloader.DownloadTask.DEFAULT_PATH;
import jworkspace.config.ServiceLocator;
import jworkspace.runtime.downloader.DownloadItem;
import jworkspace.runtime.downloader.DownloadItemDTO;
import jworkspace.runtime.downloader.DownloadService;
import jworkspace.runtime.downloader.DownloadStatus;
import jworkspace.runtime.plugin.WorkspacePluginContext;
import jworkspace.ui.ResourceAnchor;
import jworkspace.ui.WorkspaceError;
import jworkspace.ui.logging.LogViewerPanel;
import jworkspace.ui.runtime.RuntimeManagerWindow;
import jworkspace.ui.runtime.downloader.DownloadTableModel;
import jworkspace.ui.runtime.downloader.ProgressBarRenderer;
import jworkspace.ui.widgets.ButtonColumn;

public class PluginsDownloaderPanel extends KPanel {

    private static final String DOWNLOADS_CONFIG_FILE = "downloads.json";
    private final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
        .create();
    private DownloadTableModel model;
    private JTable table;
    private LogViewerPanel logViewer;
    private JLabel installerLabel;
    private final Map<Path, Plugin> pluginsCache = new HashMap<>();
    private Path selectedCompletedFile;
    private final PluginDownloadController downloadController = new PluginDownloadController(
        getModel(), new DownloadService()
    ) {
        @Override
        public void finished(int row) {
            super.finished(row);
            PluginsDownloaderPanel.this.switchInstallerLabel();
        }
    };

    @SuppressWarnings("checkstyle:MagicNumber")
    public PluginsDownloaderPanel() {
        setLayout(new BorderLayout());

        add(createPluginsLabel(), BorderLayout.NORTH);

        KPanel c = new KPanel(new BorderLayout());
        c.add(getAddressPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(getTable()),
            new JScrollPane(getLogViewer())
        );
        splitPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        splitPane.setDividerLocation(300);
        splitPane.setContinuousLayout(true);
        c.add(splitPane, BorderLayout.CENTER);

        add(c, BorderLayout.CENTER);
        add(getInstallerLabel(), BorderLayout.SOUTH);
    }

    private LogViewerPanel getLogViewer() {
        if (logViewer == null) {
            logViewer = new LogViewerPanel(getPreferredSize().width);
        }
        return logViewer;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private JTable getTable() {
        if (this.table == null) {
            this.table = new JTable(this.model);
            this.table.setRowHeight(30);

            this.table.getColumnModel().getColumn(0)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
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
                        setText(value.toString());
                        return this;
                    }
                });

            ProgressBarRenderer progressRenderer = new ProgressBarRenderer();
            this.table.getColumnModel().getColumn(2)
                .setCellRenderer(progressRenderer);

            ButtonColumn downloadButtonColumn = getDownloadButtonColumn(this.downloadController);
            this.table.getColumnModel().getColumn(5).setCellRenderer(downloadButtonColumn);
            this.table.getColumnModel().getColumn(5).setCellEditor(downloadButtonColumn);

            this.table.getColumnModel().getColumn(1).setMaxWidth(90);
            this.table.getColumnModel().getColumn(2).setMaxWidth(90);
            this.table.getColumnModel().getColumn(3).setMaxWidth(90);
            this.table.getColumnModel().getColumn(4).setMaxWidth(90);
            this.table.getColumnModel().getColumn(5).setMaxWidth(90);

            this.table.getSelectionModel().addListSelectionListener(_ -> {
                switchItemLogs();
                switchInstallerLabel();
            });
        }
        return table;
    }

    private ButtonColumn getDownloadButtonColumn(PluginDownloadController downloadController) {

        return new ButtonColumn("", (row, _) -> {
            DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);

            switch (item.getStatus()) {
                case QUEUED -> downloadController.start(row);
                case DOWNLOADING, VERIFYING -> downloadController.cancel(row);
                default -> {
                    try {
                        downloadController.remove(row);
                    } catch (Exception e) {
                        WorkspaceError.exception(ResourceAnchor.getString("Error removing downloaded file"), e);
                    }
                }
            }

            // Refresh the table so the button text updates dynamically
            table.repaint();
        }) {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column
            ) {
                DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);

                // Dynamically set button text based on DownloadStatus
                renderButton.setText(switch (item.getStatus()) {
                    case QUEUED -> "Start";
                    case DOWNLOADING, VERIFYING -> "Cancel";
                    default -> "Remove";
                });

                // Call the parent method to handle enabled/disabled state, background, cursor
                setupButtonForCell(table, renderButton, row, column, isSelected);
                installHoverCursor(table, column);
                return renderButton;
            }
        };
    }

    private DownloadTableModel getModel() {
        if (model == null) {
            model = new DownloadTableModel(new ArrayList<>());
        }
        return model;
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:ReturnCount"})
    private KPanel getAddressPanel() {
        KPanel inputPanel = new KPanel(new BorderLayout(5, 5));
        JTextField urlField = new JTextField();

        JButton addButton = new JButton("Add URL");
        urlField.addActionListener(_ -> addButton.doClick());
        addButton.addActionListener(_ -> {
            String url = urlField.getText().trim();
            if (url.isEmpty()) {
                return;
            }

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                JOptionPane.showMessageDialog(this, "Invalid URL", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DownloadItem item = new DownloadItem(url, DownloadStatus.QUEUED, 0, -1);
            model.addItem(item);
            model.fireTableDataChanged();

            // Attach log listener to update the viewer
            item.addListener(s -> logViewer.append(s));

            urlField.setText("");
        });

        inputPanel.add(urlField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        return inputPanel;
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    private JLabel createPluginsLabel() {
        JLabel l = new JLabel();

        l.setBackground(Color.white);
        l.setOpaque(true);
        l.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
            .getResourceAsImage("images/shells.png")));

        String sb = "<html><font color=black>"
            + "Plugins Downloader" + "</font><br><font size=\"-2\" color=black><i>"
            + "Install plugins from a remote location" + "</i></font></html>";

        l.setText(sb);

        l.setForeground(Color.black);
        l.setPreferredSize(new Dimension(250, 70));
        l.setMinimumSize(l.getPreferredSize());
        l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        l.setHorizontalAlignment(JLabel.CENTER);

        return l;
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private JLabel getInstallerLabel() {

        if (installerLabel == null) {
            installerLabel = new JLabel();

            installerLabel.setBackground(Color.white);
            installerLabel.setOpaque(true);
            installerLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            installerLabel.setIcon(new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
                .getResourceAsImage("images/installer_big.png")));

            String sb = "<html><font color=black>"
                + "Install a downloaded plugin" + "</font><br><font size=\"-2\" color=black><i>"
                + "Select a plugin from the list of downloaded plugins" + "</i></font></html>";

            installerLabel.setText(sb);

            installerLabel.setForeground(Color.black);
            installerLabel.setMinimumSize(installerLabel.getPreferredSize());
            installerLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            installerLabel.setHorizontalAlignment(JLabel.CENTER);

            installerLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (PluginsDownloaderPanel.this.selectedCompletedFile != null
                        && PluginsDownloaderPanel.this.pluginsCache.containsKey(
                            PluginsDownloaderPanel.this.selectedCompletedFile
                    )) {
                        PluginDownloadController.installPlugin(
                            PluginsDownloaderPanel.this.selectedCompletedFile,
                            PluginsDownloaderPanel.this.pluginsCache.get(
                                PluginsDownloaderPanel.this.selectedCompletedFile
                            )
                        );
                    }
                }
            });
        }

        return installerLabel;
    }

    private void switchItemLogs() {
        int row = table.getSelectedRow();
        getLogViewer().clear();
        if (row >= 0) {
            DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);
            List<String> logs = item.getLogs();
            logs.forEach(getLogViewer()::append);
        }
    }

    private void switchInstallerLabel() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);
            // Plugin to install
            Plugin plugin;
            try {
                if (pluginsCache.containsKey(item.getCompletedFile())) {
                    // Use the cached plugin
                    plugin = pluginsCache.get(item.getCompletedFile());
                } else {
                    // Construct the plugin from the downloaded file
                    plugin = ServiceLocator.getInstance().getPluginLocator().createPlugin(
                        item.getCompletedFile().toFile(), PluginDTO.PLUGIN_LEVEL_ANY
                    );
                    pluginsCache.putIfAbsent(item.getCompletedFile(), plugin);
                }
                String sb = "<html><font color=black>"
                    + "Install " + plugin.toString()
                    + "</font><br>"
                    + "<font size=\"-2\" color=black><i>The plugin installation will require restart</i></font></html>";

                getInstallerLabel().setText(sb);
                this.selectedCompletedFile = item.getCompletedFile();
            } catch (PluginException e) {
                String sb = "<html><font color=black>"
                    + "The selected download doesn't contain a valid plugin"
                    + "</font><br><font size=\"-2\" color=black></font></html>";

                getInstallerLabel().setText(sb);
            }
        } else {
            String sb = "<html><font color=black>"
                + "Install a downloaded plugin" + "</font><br><font size=\"-2\" color=black><i>"
                + "Select a plugin from the list of downloaded plugins" + "</i></font></html>";

            installerLabel.setText(sb);
            this.selectedCompletedFile = null;
        }
    }

    public void load(WorkspacePluginContext pluginContext) {
        Path configPath = pluginContext.getUserDir()
            .resolve(DEFAULT_PATH)
            .resolve(DOWNLOADS_CONFIG_FILE);
        if (!Files.exists(configPath)) {
            return;
        }

        try {
            String json = Files.readString(configPath, StandardCharsets.UTF_8);
            List<DownloadItemDTO> dtos = gson.fromJson(json,
                new TypeToken<List<DownloadItemDTO>>() {}.getType()
            );

            if (dtos != null) {
                for (DownloadItemDTO dto : dtos) {
                    DownloadItem item = DownloadItem.fromDTO(dto);
                    model.addItem(item);
                    item.addListener(s -> logViewer.append(s));
                }
                model.fireTableDataChanged();
            }
        } catch (IOException e) {
            /* ignore */
        }
    }

    public void save(WorkspacePluginContext pluginContext) throws IOException {
        Path configPath = pluginContext.getUserDir()
            .resolve(DEFAULT_PATH)
            .resolve(DOWNLOADS_CONFIG_FILE);

        List<DownloadItemDTO> dtos = model.getItems().stream()
            .map(DownloadItem::toDTO)
            .collect(Collectors.toList());

        String json = gson.toJson(dtos);
        Files.writeString(configPath, json, StandardCharsets.UTF_8);
    }
}
