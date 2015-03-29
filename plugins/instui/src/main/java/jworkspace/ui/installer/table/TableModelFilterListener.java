package jworkspace.ui.installer.table;

import java.util.EventListener;

/**
 *
 */
public interface TableModelFilterListener extends EventListener {

    void filterChanged(TableModelFilterEvent evt);
}