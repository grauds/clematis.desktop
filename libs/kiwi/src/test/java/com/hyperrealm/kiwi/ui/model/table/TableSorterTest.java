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

import javax.swing.table.DefaultTableModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link TableSorter}.
 */
public class TableSorterTest {

    /**
     * Test sortByColumn.
     */
    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
    @Test
    public void testSortByColumn() {
        DefaultTableModel source =
            new DefaultTableModel(
                new Object[][]{{3, "C"}, {1, "A"}, {2, "B"}},
                new Object[]{"Num", "Letter"});

        TableSorter sorter = new TableSorter(source);
        sorter.sortByColumn(0);

        assertEquals(1, sorter.getValueAt(0, 0));
        assertEquals(2, sorter.getValueAt(1, 0));
        assertEquals(3, sorter.getValueAt(2, 0));
    }

    /**
     * Test row translation.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testRowTranslation() {
        DefaultTableModel source =
            new DefaultTableModel(
                new Object[][]{{3}, {1}, {2}},
                new Object[]{"Num"});

        TableSorter sorter = new TableSorter(source);
        sorter.sortByColumn(0);
        // After sort, row 0 in sorted view maps to row 1 in original
        int translated = sorter.getRowTranslation(0);
        assertEquals(1, translated);
    }

    /**
     * Test getSortedColumn.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testGetSortedColumn() {
        DefaultTableModel source =
            new DefaultTableModel(
                new Object[][]{{3}, {1}},
                new Object[]{"Num"});

        TableSorter sorter = new TableSorter(source);
        assertEquals(-1, sorter.getSortedColumn());
        sorter.sortByColumn(0);
        assertEquals(0, sorter.getSortedColumn());
    }

    /**
     * Test isSortedAscending.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testIsSortedAscending() {
        DefaultTableModel source =
            new DefaultTableModel(
                new Object[][]{{3}, {1}},
                new Object[]{"Num"});

        TableSorter sorter = new TableSorter(source);
        sorter.sortByColumn(0, true);
        assertTrue(sorter.isSortedAscending());
    }

    /**
     * Test constructor with model.
     */
    @Test
    public void testConstructorWithModel() {
        DefaultTableModel model =
            new DefaultTableModel();
        TableSorter sorter = new TableSorter(model);
        assertEquals(0, sorter.getRowCount());
    }
}
