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
import com.hyperrealm.kiwi.ui.model.list.KListModelListAdapter;

/**
 * Unit tests for {@link KListModelListAdapter}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KListModelListAdapterTest {

    /**
     * Test adapt to java.util.List.
     */
    @Test
    public void testAdaptToList() {
        DefaultKListModel<String> kModel = new DefaultKListModel<>();
        kModel.addItem("one");
        kModel.addItem("two");
        kModel.addItem("three");

        KListModelListAdapter<String> adapter = new KListModelListAdapter<>();
        adapter.setListModel(kModel);

        assertEquals(3, adapter.getSize());
        assertEquals("one", adapter.getElementAt(0));
        assertEquals("two", adapter.getElementAt(1));
        assertEquals("three", adapter.getElementAt(2));
    }

    /**
     * Test with null model.
     */
    @Test
    public void testWithNullModel() {
        KListModelListAdapter<String> adapter = new KListModelListAdapter<>();
        adapter.setListModel(null);
        assertEquals(0, adapter.getSize());
    }

    /**
     * Test listener registration.
     */
    @Test
    public void testListenerRegistration() {
        KListModelListAdapter<String> adapter = new KListModelListAdapter<>();
        javax.swing.event.ListDataListener listener =
            new javax.swing.event.ListDataListener() {
                public void intervalAdded(javax.swing.event.ListDataEvent e) {}
                public void intervalRemoved(javax.swing.event.ListDataEvent e) {}
                public void contentsChanged(javax.swing.event.ListDataEvent e) {}
            };
        adapter.addListDataListener(listener);
        adapter.removeListDataListener(listener);
        // Should not throw
    }
}
