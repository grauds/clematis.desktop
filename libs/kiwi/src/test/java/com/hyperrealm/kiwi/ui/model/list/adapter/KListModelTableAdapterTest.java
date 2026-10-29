package com.hyperrealm.kiwi.ui.model.list.adapter;
/* ----------------------------------------------------------------------------\
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
   ----------------------------------------------------------------------------\
*/

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.model.list.DefaultKListModel;
import com.hyperrealm.kiwi.ui.model.list.KListModelTableAdapter;

/**
 * Unit tests for {@link KListModelTableAdapter}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class KListModelTableAdapterTest {

    /**
     * Test adapt to TableModel.
     */
    @Test
    public void testAdaptToTableModel() {
        DefaultKListModel<String> kModel = new DefaultKListModel<>();
        kModel.addItem("one");
        kModel.addItem("two");

        KListModelTableAdapter<String> adapter = new KListModelTableAdapter<>(kModel);

        assertEquals(2, adapter.getRowCount());
        assertEquals(1, adapter.getColumnCount());
        assertEquals("one", adapter.getValueAt(0, 0));
        assertEquals("two", adapter.getValueAt(1, 0));
    }

    /**
     * Test with null model.
     */
    @Test
    public void testWithNullModel() {
        KListModelTableAdapter<String> adapter = new KListModelTableAdapter<>();
        adapter.setListModel(null);
        assertEquals(0, adapter.getRowCount());
    }

    /**
     * Test getColumnName.
     */
    @Test
    public void testGetColumnName() {
        KListModelTableAdapter<String> adapter = new KListModelTableAdapter<>();
        assertEquals("Item", adapter.getColumnName(0));
    }

    /**
     * Test getColumnClass.
     */
    @Test
    public void testGetColumnClass() {
        KListModelTableAdapter<String> adapter = new KListModelTableAdapter<>();
        assertEquals(String.class, adapter.getColumnClass(0));
    }
}
