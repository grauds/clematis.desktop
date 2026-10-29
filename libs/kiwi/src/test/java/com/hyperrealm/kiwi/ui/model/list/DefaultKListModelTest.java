package com.hyperrealm.kiwi.ui.model.list;
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

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultKListModel}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class DefaultKListModelTest {

    /**
     * Test addItem.
     */
    @Test
    public void testAddItem() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        model.addItem("one");
        model.addItem("two");
        assertEquals(2, model.getItemCount());
        assertEquals("one", model.getItemAt(0));
        assertEquals("two", model.getItemAt(1));
    }

    /**
     * Test insertItemAt.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testInsertItemAt() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        model.addItem("one");
        model.addItem("three");
        model.insertItemAt("two", 1);
        assertEquals(3, model.getItemCount());
        assertEquals("one", model.getItemAt(0));
        assertEquals("two", model.getItemAt(1));
        assertEquals("three", model.getItemAt(2));
    }

    /**
     * Test removeItemAt.
     */
    @Test
    public void testRemoveItemAt() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        model.addItem("one");
        model.addItem("two");
        model.addItem("three");
        model.removeItemAt(1);
        assertEquals(2, model.getItemCount());
        assertEquals("one", model.getItemAt(0));
        assertEquals("three", model.getItemAt(1));
    }

    /**
     * Test removeItem.
     */
    @Test
    public void testRemoveItem() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        model.addItem("one");
        model.addItem("two");
        model.removeItem("two");
        assertEquals(1, model.getItemCount());
        assertEquals("one", model.getItemAt(0));
    }

    /**
     * Test clear.
     */
    @Test
    public void testClear() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        model.addItem("one");
        model.addItem("two");
        model.clear();
        assertTrue(model.isEmpty());
        assertEquals(0, model.getItemCount());
    }

    /**
     * Test sort.
     */
    @Test
    public void testSort() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        model.addItem("c");
        model.addItem("a");
        model.addItem("b");
        model.sort(Comparator.naturalOrder());
        assertEquals("a", model.getItemAt(0));
        assertEquals("b", model.getItemAt(1));
        assertEquals("c", model.getItemAt(2));
    }

    /**
     * Test isEmpty.
     */
    @Test
    public void testIsEmpty() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        assertTrue(model.isEmpty());
        model.addItem("item");
        assertFalse(model.isEmpty());
    }

    /**
     * Test indexOf.
     */
    @Test
    public void testIndexOf() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        model.addItem("one");
        model.addItem("two");
        assertEquals(0, model.indexOf("one"));
        assertEquals(1, model.indexOf("two"));
        assertEquals(-1, model.indexOf("three"));
    }

    /**
     * Test iterator.
     */
    @Test
    public void testIterator() {
        DefaultKListModel<String> model = new DefaultKListModel<>();
        model.addItem("one");
        model.addItem("two");
        int count = 0;
        for (String item : model) {
            count++;
        }
        assertEquals(2, count);
    }
}
