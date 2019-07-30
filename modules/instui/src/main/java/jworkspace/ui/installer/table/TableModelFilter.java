package jworkspace.ui.installer.table;

import javax.swing.table.TableModel;

/**
 *
 */
public interface TableModelFilter {

    boolean filter(TableModel tableModel, int row);

    void addTableModelFilterListener(TableModelFilterListener listener);

    void removeTableModelFilterListener(TableModelFilterListener listener);
}
