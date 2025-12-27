package jworkspace.ui.runtime.downloader;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import jworkspace.runtime.downloader.service.DownloadItem;

public class DownloadTableModel extends AbstractTableModel {

    private final List<DownloadItem> items;
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private final String[] columns = {
        "File", "Status", "Progress", "Speed (KB/s)", "ETA", "Cancel"
    };

    public DownloadTableModel(List<DownloadItem> items) {
        this.items = items;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int c) {
        return columns[c];
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Object getValueAt(int row, int col) {
        DownloadItem i = items.get(row);
        return switch (col) {
            case 0 -> i.getFileName();
            case 1 -> i.getStatus();
            case 2 -> i.getProgressPercent();
            case 3 -> String.format("%.1f", i.getSpeedKb());
            case 4 -> i.getEtaSeconds() < 0 ? "--" : (i.getEtaSeconds() / 60 + "m " + i.getEtaSeconds() % 60 + "s");
            case 5 -> "Cancel";
            default -> null;
        };
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return col == 2 ? Integer.class : String.class;
    }

    public DownloadItem getItem(int row) {
        return items.get(row);
    }

    public void removeRow(int row) {
        items.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 5;
    }

    public void updateRow(int row) {
        SwingUtilities.invokeLater(() -> fireTableRowsUpdated(row, row));
    }

    public void addItem(DownloadItem item) {
        this.items.add(item);
    }
}