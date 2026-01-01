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
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.runtime.downloader.service.DownloadItem;
import jworkspace.runtime.downloader.service.DownloadService;
import jworkspace.runtime.downloader.service.DownloadStatus;
import jworkspace.ui.runtime.RuntimeManagerWindow;

public class PluginsDownloaderPanel extends KPanel {

    private DownloadTableModel model;
    private JTable table;
    private JTextArea logArea;

    @SuppressWarnings("checkstyle:MagicNumber")
    public PluginsDownloaderPanel() {
        setLayout(new BorderLayout());
        add(createPluginsLabel(), BorderLayout.NORTH);

        KPanel c = new KPanel(new BorderLayout());
        c.add(getAddressPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(getTable()),
            new JScrollPane(getLogArea())
        );
        splitPane.setDividerLocation(300);
        c.add(splitPane, BorderLayout.CENTER);

        add(c, BorderLayout.CENTER);

        new javax.swing.Timer(300, e -> refreshSelectedLog()).start();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private JTable getTable() {
        if (table == null) {
            DownloadController downloadController = new DownloadController(
                getModel(), new DownloadService()
            );

            table = new JTable(model);
            table.setRowHeight(30);

            ProgressBarRenderer progressRenderer = new ProgressBarRenderer();
            table.getColumnModel().getColumn(2)
                .setCellRenderer(progressRenderer);
            table.getColumnModel().getColumn(5)
                .setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(5)
                .setCellEditor(new ButtonEditor(downloadController));
        }
        return table;
    }

    private DownloadTableModel getModel() {
        if (model == null) {
            model = new DownloadTableModel(new ArrayList<>());
        }
        return model;
    }

    private JTextArea getLogArea() {
        if (logArea == null) {
            logArea = new JTextArea();
            logArea.setEditable(false);
        }
        return logArea;
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

    private void refreshSelectedLog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }

        DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);
        updateLogArea(item);
    }

    private void updateLogArea(DownloadItem item) {
        List<String> log = item.getLogs();
        getLogArea().setText(String.join("\n", log));
        getLogArea().setCaretPosition(getLogArea().getDocument().getLength());
    }
}
