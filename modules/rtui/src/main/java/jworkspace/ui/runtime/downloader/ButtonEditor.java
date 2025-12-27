package jworkspace.ui.runtime.downloader;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import jworkspace.runtime.downloader.service.DownloadItem;
import jworkspace.runtime.downloader.service.DownloadStatus;


public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

    private final JButton button = new JButton();
    private JTable table;
    private int row;
    private final DownloadController controller;

    public ButtonEditor(DownloadController controller) {
        this.controller = controller;
        button.addActionListener(e -> onClick());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;

        DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);
        button.setText(textFor(item.getStatus()));
        return button;
    }

    private void onClick() {
        DownloadItem item = ((DownloadTableModel) table.getModel()).getItem(row);

        switch (item.getStatus()) {
            case QUEUED -> controller.start(row);
            case DOWNLOADING, VERIFYING -> controller.cancel(row);
            default -> controller.remove(row);
        }

        fireEditingStopped();
    }

    private String textFor(DownloadStatus status) {
        return switch (status) {
            case DownloadStatus.QUEUED -> "Start";
            case DownloadStatus.DOWNLOADING, DownloadStatus.VERIFYING -> "Cancel";
            default -> "Remove";
        };
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}