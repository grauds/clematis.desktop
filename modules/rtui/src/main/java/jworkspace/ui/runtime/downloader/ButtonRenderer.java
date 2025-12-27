package jworkspace.ui.runtime.downloader;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import jworkspace.runtime.downloader.service.DownloadItem;
import jworkspace.runtime.downloader.service.DownloadStatus;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        DownloadTableModel model = (DownloadTableModel) table.getModel();
        DownloadItem item = model.getItem(row);
        setText(buttonText(item.getStatus()));
        return this;
    }

    private String buttonText(DownloadStatus status) {
        return switch (status) {
            case DownloadStatus.QUEUED -> "Start";
            case DownloadStatus.DOWNLOADING, DownloadStatus.VERIFYING -> "Cancel";
            default -> "Remove";
        };
    }
}
