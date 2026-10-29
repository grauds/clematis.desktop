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
import com.hyperrealm.kiwi.ui.model.list.KListModelAdapter;
import com.hyperrealm.kiwi.ui.model.list.KListModelListAdapter;

/**
 * Unit tests for {@link KListModelAdapter}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class KListModelAdapterTest {

    /**
     * Test adapt KListModel to ListModel.
     */
    @Test
    public void testAdaptKListModelToListModel() {
        DefaultKListModel<String> kModel = new DefaultKListModel<>();
        kModel.addItem("one");
        kModel.addItem("two");

        KListModelListAdapter<String> adapter = new KListModelListAdapter<>();
        adapter.setListModel(kModel);

        assertEquals(2, adapter.getSize());
        assertEquals("one", adapter.getElementAt(0));
        assertEquals("two", adapter.getElementAt(1));
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
     * Test switching models.
     */
    @Test
    public void testSwitchingModels() {
        KListModelListAdapter<String> adapter = new KListModelListAdapter<>();
        DefaultKListModel<String> model1 = new DefaultKListModel<>();
        model1.addItem("a");
        adapter.setListModel(model1);
        assertEquals(1, adapter.getSize());

        DefaultKListModel<String> model2 = new DefaultKListModel<>();
        model2.addItem("x");
        model2.addItem("y");
        adapter.setListModel(model2);
        assertEquals(2, adapter.getSize());
    }
}
