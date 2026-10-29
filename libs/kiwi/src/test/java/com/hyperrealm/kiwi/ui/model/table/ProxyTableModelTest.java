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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link ProxyTableModel}.
 */
public class ProxyTableModelTest {

    /**
     * Test forward all TableModel methods.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    public void testForwardTableModelMethods() {
        javax.swing.table.DefaultTableModel source =
            new javax.swing.table.DefaultTableModel(
                new Object[][]{{"A", "B"}, {"C", "D"}},
                new Object[]{"Col1", "Col2"});

        ProxyTableModel proxy = new ProxyTableModel();
        proxy.setModel(source);

        assertEquals(2, proxy.getRowCount());
        assertEquals(2, proxy.getColumnCount());
        assertEquals("Col1", proxy.getColumnName(0));
        assertEquals("A", proxy.getValueAt(0, 0));
    }

    /**
     * Test getModel/setModel.
     */
    @Test
    public void testGetSetModel() {
        ProxyTableModel proxy = new ProxyTableModel();
        assertNull(proxy.getModel()); // Default is null
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel();
        proxy.setModel(model);
        assertEquals(model, proxy.getModel());
    }

    /**
     * Test with null model.
     */
    @Test
    public void testWithNullModel() {
        ProxyTableModel proxy = new ProxyTableModel();
        assertEquals(0, proxy.getRowCount());
        assertEquals(0, proxy.getColumnCount());
    }
}
