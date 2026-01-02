package jworkspace.ui.runtime.downloader;
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
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import jworkspace.runtime.downloader.DownloadItem;

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