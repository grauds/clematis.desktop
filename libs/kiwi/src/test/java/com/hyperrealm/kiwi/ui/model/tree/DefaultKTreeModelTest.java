package com.hyperrealm.kiwi.ui.model.tree;
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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultKTreeModel}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class DefaultKTreeModelTest {

    /**
     * Test setRoot.
     */
    @Test
    public void testSetRoot() {
        DefaultKTreeModel<String> model = new DefaultKTreeModel<>();
        model.setRoot("root");
        assertEquals("root", model.getRoot());
    }

    /**
     * Test addChild.
     */
    @Test
    public void testAddChild() {
        DefaultKTreeModel<String> model = new DefaultKTreeModel<>();
        model.setRoot("root");
        model.addChild("root", "child1");
        model.addChild("root", "child2");
        assertEquals(2, model.getChildCount("root"));
        assertEquals("child1", model.getChild("root", 0));
        assertEquals("child2", model.getChild("root", 1));
    }

    /**
     * Test removeChild.
     */
    @Test
    public void testRemoveChild() {
        DefaultKTreeModel<String> model = new DefaultKTreeModel<>();
        model.setRoot("root");
        model.addChild("root", "child1");
        model.addChild("root", "child2");
        model.removeChild("root", 0);
        assertEquals(1, model.getChildCount("root"));
        assertEquals("child2", model.getChild("root", 0));
    }

    /**
     * Test getParent.
     */
    @Test
    public void testGetParent() {
        DefaultKTreeModel<String> model = new DefaultKTreeModel<>();
        model.setRoot("root");
        model.addChild("root", "child1");
        assertEquals("root", model.getParent("child1"));
    }

    /**
     * Test getIndexOfChild.
     */
    @Test
    public void testGetIndexOfChild() {
        DefaultKTreeModel<String> model = new DefaultKTreeModel<>();
        model.setRoot("root");
        model.addChild("root", "child1");
        model.addChild("root", "child2");
        assertEquals(0, model.getIndexOfChild("root", "child1"));
        assertEquals(1, model.getIndexOfChild("root", "child2"));
    }

    /**
     * Test removeChildren.
     */
    @Test
    public void testRemoveChildren() {
        DefaultKTreeModel<String> model = new DefaultKTreeModel<>();
        model.setRoot("root");
        model.addChild("root", "child1");
        model.addChild("root", "child2");
        model.removeChildren("root");
        assertEquals(0, model.getChildCount("root"));
    }

    /**
     * Test isExpandable.
     */
    @Test
    public void testIsExpandable() {
        DefaultKTreeModel<String> model = new DefaultKTreeModel<>();
        model.setRoot("root");
        model.addChild("root", "child1");
        assertTrue(model.isExpandable("root"));
    }

    /**
     * Test getChildren.
     */
    @Test
    public void testGetChildren() {
        DefaultKTreeModel<String> model = new DefaultKTreeModel<>();
        model.setRoot("root");
        model.addChild("root", "child1");
        model.addChild("root", "child2");
        int count = 0;
        java.util.Iterator<String> iter = model.getChildren("root");
        while (iter.hasNext()) {
            iter.next();
            count++;
        }
        assertEquals(2, count);
    }
}
