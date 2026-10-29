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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.model.tree.KTreeModelTreeAdapter;

/**
 * Unit tests for {@link KTreeModelTreeAdapter}.
 */
public class KTreeModelTreeAdapterTest {

    /**
     * Test adapt KTreeModel to TreeModel.
     */
    @Test
    public void testAdaptKTreeModelToTreeModel() {
        JTree tree = new JTree();
        KTreeModelTreeAdapter adapter = new KTreeModelTreeAdapter(tree);
        assertNotNull(adapter);
    }

    /**
     * Test getRoot.
     */
    @Test
    public void testGetRoot() {
        JTree tree = new JTree();
        KTreeModelTreeAdapter adapter = new KTreeModelTreeAdapter(tree);
        // With no model set, root should be null
        assertNull(adapter.getRoot());
    }

    /**
     * Test getChildCount.
     */
    @Test
    public void testGetChildCount() {
        JTree tree = new JTree();
        KTreeModelTreeAdapter adapter = new KTreeModelTreeAdapter(tree);
        assertEquals(0, adapter.getChildCount(null));
    }

    /**
     * Test isLeaf.
     */
    @Test
    public void testIsLeaf() {
        JTree tree = new JTree();
        KTreeModelTreeAdapter adapter = new KTreeModelTreeAdapter(tree);
        // With no model, should return true
        assertTrue(adapter.isLeaf(null));
    }

    /**
     * Test getIndexOfChild.
     */
    @Test
    public void testGetIndexOfChild() {
        JTree tree = new JTree();
        KTreeModelTreeAdapter adapter = new KTreeModelTreeAdapter(tree);
        assertEquals(-1, adapter.getIndexOfChild(null, null));
    }
}
