package com.hyperrealm.kiwi.ui.model.table;
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

import javax.swing.table.TableColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link KTableColumnModel}.
 */
public class KTableColumnModelTest {

    /**
     * Test column management.
     */
    @Test
    public void testColumnManagement() {
        KTableColumnModel model = new KTableColumnModel();
        TableColumn col1 = new TableColumn(0);
        col1.setHeaderValue("Column 1");
        TableColumn col2 = new TableColumn(1);
        col2.setHeaderValue("Column 2");

        model.addColumn(col1);
        model.addColumn(col2);

        assertEquals(2, model.getColumnCount());
        assertEquals(2, model.getRealColumnCount());
    }

    /**
     * Test setColumnVisible.
     */
    @Test
    public void testSetColumnVisible() {
        KTableColumnModel model = new KTableColumnModel();
        TableColumn col = new TableColumn(0);
        model.addColumn(col);

        model.setColumnVisible(0, false);
        assertEquals(0, model.getColumnCount()); // Hidden
        assertEquals(1, model.getRealColumnCount()); // Still in full list
    }

    /**
     * Test isColumnVisible.
     */
    @Test
    public void testIsColumnVisible() {
        KTableColumnModel model = new KTableColumnModel();
        TableColumn col = new TableColumn(0);
        model.addColumn(col);

        assertTrue(model.isColumnVisible(0));
        model.setColumnVisible(0, false);
        assertFalse(model.isColumnVisible(0));
    }

    /**
     * Test getRealColumn.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    public void testGetRealColumn() {
        KTableColumnModel model = new KTableColumnModel();
        TableColumn col = new TableColumn(0);
        col.setHeaderValue("Test");
        model.addColumn(col);

        TableColumn retrieved = model.getRealColumn(0);
        assertEquals("Test", retrieved.getHeaderValue());
    }

    /**
     * Test removeColumn.
     */
    @Test
    public void testRemoveColumn() {
        KTableColumnModel model = new KTableColumnModel();
        TableColumn col = new TableColumn(0);
        model.addColumn(col);
        model.removeColumn(col);
        assertEquals(0, model.getRealColumnCount());
    }
}
