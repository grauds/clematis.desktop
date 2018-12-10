/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.TableColumn;

import com.hyperrealm.kiwi.ui.model.KTableColumnModel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A button and associated popup menu which allows the user to select the
 * visible columns in a table column model.
 * <p>
 * Typically, the button should be installed in the top-right corner of a
 * <code>JScrollPane</code> which is housing a <code>JTable</code>, using
 * code such as the following:
 * <p>
 * <pre>
 * TableColumnSelector tsel = ... ;
 * JScrollPane sp = ... ;
 *
 * sp.setVerticalScrollBarPolicy(
 *   ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
 * sp.setHorizontalScrollBarPolicy(
 *   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
 * sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, tsel);
 * </pre>
 * With this approach, it is necessary to force the <code>JScrollPane</code>
 * to always display a vertical scroll bar, since without it, the top-right
 * corner is not present.
 * <p>
 * The first column is considered to be the <i>primary column</i> and is the
 * one column that cannot be hidden. This ensures that at least one column is
 * always visible.
 * <p><center>
 * <img src="snapshot/TableColumnSelector.gif"><br>
 * <i>An example TableColumnSelector.</i>
 * </center>
 * <p>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.KTable
 * @see com.hyperrealm.kiwi.ui.model.KTableColumnModel
 * @since Kiwi 2.1
 */

public class TableColumnSelector extends KButton {

    private JPopupMenu menu;

    private KTableColumnModel colModel;

    private JCheckBoxMenuItem[] menuItems;

    /**
     * Construct a new <code>TableColumnSelector</code> for the given
     * table column model.
     *
     * @param colModel The <code>KTableColumnModel</code>.
     */

    public TableColumnSelector(KTableColumnModel colModel) {
        super(KiwiUtils.getResourceManager()
            .getIcon("column_select.png"));

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
        setToolTipText(loc.getMessage("kiwi.button.column_sel"));
        setFocusPainted(false);

        menu = new JPopupMenu();

        this.colModel = colModel;
        int c = colModel.getRealColumnCount();

        menuItems = new JCheckBoxMenuItem[c];

        for (int i = 1; i < c; i++) {
            TableColumn tcol = colModel.getRealColumn(i);

            menuItems[i] = new JCheckBoxMenuItem((String) tcol.getHeaderValue());
            menuItems[i].addActionListener(new ActionListener(i));
            menu.add(menuItems[i]);
        }

        addActionListener(evt -> {
            refresh();

            int mw = menu.getWidth();
            if (mw == 0) {
                mw = menu.getPreferredSize().width;
            }

            menu.show(TableColumnSelector.this, getWidth() - mw,
                getHeight());
        });
    }

    /*
     */

    private void refresh() {
        for (int i = 1; i < menuItems.length; i++) {
            menuItems[i].setState(colModel.isColumnVisible(i));
        }
    }

    /*
     */

    private class ActionListener implements java.awt.event.ActionListener {
        private int index;

        ActionListener(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent evt) {
            colModel.setColumnVisible(index, menuItems[index].getState());
        }
    }

}

