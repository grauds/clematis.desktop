package com.hyperrealm.kiwi.ui.model.tree.adapter;
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

import javax.swing.JTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.model.tree.KTreeModelTreeTableAdapter;

/**
 * Unit tests for {@link KTreeModelTreeTableAdapter}.
 */
public class KTreeModelTreeTableAdapterTest {

    /**
     * Test adapt to tree table model.
     */
    @Test
    public void testAdaptToTreeTableModel() {
        JTree tree = new JTree();
        KTreeModelTreeTableAdapter adapter = new KTreeModelTreeTableAdapter(tree);
        assertNotNull(adapter);
    }

    /**
     * Test getColumnCount.
     */
    @Test
    public void testGetColumnCount() {
        JTree tree = new JTree();
        KTreeModelTreeTableAdapter adapter = new KTreeModelTreeTableAdapter(tree);
        // With no model, should return 0
        assertEquals(0, adapter.getColumnCount());
    }

    /**
     * Test getRowCount.
     */
    @Test
    public void testGetRowCount() {
        JTree tree = new JTree();
        KTreeModelTreeTableAdapter adapter = new KTreeModelTreeTableAdapter(tree);
        // Should not throw
        adapter.getRowCount();
    }

    /**
     * Test getColumnName.
     */
    @Test
    public void testGetColumnName() {
        JTree tree = new JTree();
        KTreeModelTreeTableAdapter adapter = new KTreeModelTreeTableAdapter(tree);
        // With no model, should return null
        assertNull(adapter.getColumnName(0));
    }

    /**
     * Test getValueAt.
     */
    @Test
    public void testGetValueAt() {
        JTree tree = new JTree();
        KTreeModelTreeTableAdapter adapter = new KTreeModelTreeTableAdapter(tree);
        // With no model, should return null
        assertNull(adapter.getValueAt(0, 0));
    }

    /**
     * Test isCellEditable.
     */
    @Test
    public void testIsCellEditable() {
        JTree tree = new JTree();
        KTreeModelTreeTableAdapter adapter = new KTreeModelTreeTableAdapter(tree);
        // First column editable, others not
        assertTrue(adapter.isCellEditable(0, 0));
        assertFalse(adapter.isCellEditable(0, 1));
    }

    /**
     * Test dispose.
     */
    @Test
    public void testDispose() {
        JTree tree = new JTree();
        KTreeModelTreeTableAdapter adapter = new KTreeModelTreeTableAdapter(tree);
        adapter.dispose();
        // Should not throw
    }
}
