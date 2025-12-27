package jworkspace.ui.runtime.downloader;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jworkspace.runtime.downloader.service.DownloadItem;
import jworkspace.runtime.downloader.service.DownloadService;
import jworkspace.runtime.downloader.service.DownloadStatus;

public class DownloadManagerDialog extends JDialog {

    final DownloadController downloadController;
    final JTable table;
    final JTextArea logArea;

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:ReturnCount"})
    public DownloadManagerDialog(Frame owner) {
        super(owner, "Swing Download Manager", true);

        // Top panel for URL input
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JTextField urlField = new JTextField();
        JButton addButton = new JButton("Add URL");

        inputPanel.add(urlField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        DownloadTableModel model = new DownloadTableModel(new ArrayList<>());
        downloadController = new DownloadController(model, new DownloadService());

        table = new JTable(model);
        table.setRowHeight(30);

        ProgressBarRenderer progressRenderer = new ProgressBarRenderer();
        table.getColumnModel().getColumn(2).setCellRenderer(progressRenderer);

        table.getColumnModel().getColumn(5)
            .setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5)
            .setCellEditor(new ButtonEditor(downloadController));

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(table),
            logScroll
        );
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }
            updateLogArea(model.getItem(row));
        });

        JButton start = new JButton("Start All");
        start.addActionListener(e -> startDownloads());
        add(start, BorderLayout.SOUTH);

        setSize(900, 400);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                downloadController.shutdown();
            }
        });

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

        new javax.swing.Timer(300, e -> refreshSelectedLog()).start();
    }

    private void startDownloads() {
        downloadController.start();
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
        logArea.setText(String.join("\n", log));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    @SuppressWarnings("checkstyle:InnerTypeLast")
    public static void main(String[] args) {
        DownloadManagerDialog dialog = new DownloadManagerDialog(null);
        dialog.setVisible(true);
    }
}
