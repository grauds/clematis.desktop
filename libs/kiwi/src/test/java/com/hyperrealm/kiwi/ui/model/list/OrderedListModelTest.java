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
import org.junit.Test;

/**
 * Unit tests for {@link OrderedListModel}.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class OrderedListModelTest {

    /**
     * Test ordered list operations.
     */
    @Test
    public void testOrderedOperations() {
        OrderedListModel<Integer> model = new OrderedListModel<>();
        model.addElement(3);
        model.addElement(1);
        model.addElement(2);
        assertEquals(3, model.getSize());
        assertEquals(1, (int) model.getElementAt(0));
        assertEquals(2, (int) model.getElementAt(1));
        assertEquals(3, (int) model.getElementAt(2));
    }

    /**
     * Test rejects duplicates.
     */
    @Test
    public void testRejectsDuplicates() {
        OrderedListModel<Integer> model = new OrderedListModel<>();
        model.addElement(1);
        model.addElement(1);
        assertEquals(1, model.getSize());
    }

    /**
     * Test with comparator.
     */
    @Test
    public void testWithComparator() {
        Comparator<Integer> comparator = Comparator.reverseOrder();
        OrderedListModel<Integer> model = new OrderedListModel<>(null, comparator);
        model.addElement(1);
        model.addElement(3);
        model.addElement(2);
        assertEquals(3, model.getSize());
        assertEquals(3, (int) model.getElementAt(0));
        assertEquals(2, (int) model.getElementAt(1));
        assertEquals(1, (int) model.getElementAt(2));
    }

    /**
     * Test setComparator.
     */
    @Test
    public void testSetComparator() {
        OrderedListModel<Integer> model = new OrderedListModel<>();
        model.setComparator(Comparator.reverseOrder());
        model.addElement(1);
        model.addElement(3);
        assertEquals(2, model.getSize());
    }

    /**
     * Test updateItem.
     */
    @Test
    public void testUpdateItem() {
        OrderedListModel<Integer> model = new OrderedListModel<>();
        model.addElement(1);
        model.updateItem(1); // Should not throw
        assertEquals(1, model.getSize());
    }

    /**
     * Test constructor with collection.
     */
    @Test
    public void testConstructorWithCollection() {
        java.util.ArrayList<Integer> list = new java.util.ArrayList<>();
        list.add(3);
        list.add(1);
        list.add(2);
        OrderedListModel<Integer> model = new OrderedListModel<>(list, null);
        assertEquals(3, model.getSize());
        assertEquals(1, (int) model.getElementAt(0));
    }
}
