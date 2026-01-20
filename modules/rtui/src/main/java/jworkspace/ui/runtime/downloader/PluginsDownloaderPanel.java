package jworkspace.ui.runtime.downloader;
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.Icon;
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
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ExceptionDialog;
import com.hyperrealm.kiwi.util.ResourceLoader;

import static jworkspace.runtime.downloader.DownloadTask.DEFAULT_PATH;
import static jworkspace.ui.WorkspaceGUI.getResourceManager;
import jworkspace.runtime.downloader.DownloadItem;
import jworkspace.runtime.downloader.DownloadItemDTO;
import jworkspace.runtime.downloader.DownloadService;
import jworkspace.runtime.downloader.DownloadStatus;
import jworkspace.runtime.plugin.WorkspacePluginContext;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.logging.LogViewerPanel;
import jworkspace.ui.runtime.RuntimeManagerWindow;
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
            DownloadController downloadController = new DownloadController(
                getModel(), new DownloadService()
            );

            this.table = new JTable(this.model);
            this.table.setRowHeight(30);

            this.table.getColumnModel().getColumn(0)
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
                        } else {
                            setText(value.toString());
                        }
                        return this;
                    }
                });

            ProgressBarRenderer progressRenderer = new ProgressBarRenderer();
            this.table.getColumnModel().getColumn(2)
                .setCellRenderer(progressRenderer);

            ButtonColumn downloadButtonColumn = getDownloadButtonColumn(downloadController);
            this.table.getColumnModel().getColumn(5).setCellRenderer(downloadButtonColumn);
            this.table.getColumnModel().getColumn(5).setCellEditor(downloadButtonColumn);

            this.table.getColumnModel().getColumn(1).setMaxWidth(90);
            this.table.getColumnModel().getColumn(2).setMaxWidth(90);
            this.table.getColumnModel().getColumn(3).setMaxWidth(90);
            this.table.getColumnModel().getColumn(4).setMaxWidth(90);
            this.table.getColumnModel().getColumn(5).setMaxWidth(90);

            this.table.getSelectionModel().addListSelectionListener(e -> switchItemLogs());
        }
        return table;
    }

    private ButtonColumn getDownloadButtonColumn(DownloadController downloadController) {

        return new ButtonColumn("Action", (row, col) -> {
            DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);

            switch (item.getStatus()) {
                case QUEUED -> downloadController.start(row);
                case DOWNLOADING, VERIFYING -> downloadController.cancel(row);
                default -> {
                    try {
                        downloadController.remove(row);
                    } catch (IOException e) {
                        ExceptionDialog exceptionDialog = new ExceptionDialog(
                            DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                            "Error removing downloaded file"
                        );
                        exceptionDialog.setException("Cannot delete file", e);
                        exceptionDialog.setVisible(true);
                    }
                }
            }

            // Refresh table so the button text updates dynamically
            table.repaint();
        }) {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
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
        urlField.addActionListener(e -> addButton.doClick());
        addButton.addActionListener(e -> {
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

    @SuppressWarnings("checkstyle:MagicNumber")
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

    private void switchItemLogs() {
        int row = table.getSelectedRow();
        getLogViewer().clear();
        if (row >= 0) {
            DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);
            List<String> logs = item.getLogs();
            logs.forEach(getLogViewer()::append);
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
