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
import static org.junit.Assert.assertNull;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.model.list.DefaultKListModel;
import com.hyperrealm.kiwi.ui.model.list.KListModelComboBoxAdapter;

/**
 * Unit tests for {@link KListModelComboBoxAdapter}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class KListModelComboBoxAdapterTest {

    /**
     * Test adapt to ComboBoxModel.
     */
    @Test
    public void testAdaptToComboBoxModel() {
        DefaultKListModel<String> kModel = new DefaultKListModel<>();
        kModel.addItem("option1");
        kModel.addItem("option2");

        KListModelComboBoxAdapter<String> adapter = new KListModelComboBoxAdapter<>(new javax.swing.JComboBox<>());
        adapter.setListModel(kModel);

        assertEquals(2, adapter.getSize());
        assertEquals("option1", adapter.getElementAt(0));
        assertEquals("option2", adapter.getElementAt(1));
    }

    /**
     * Test getSelectedItem/setSelectedItem.
     */
    @Test
    public void testGetSetSelectedItem() {
        KListModelComboBoxAdapter<String> adapter = new KListModelComboBoxAdapter<>(new javax.swing.JComboBox<>());
        assertNull(adapter.getSelectedItem());
        adapter.setSelectedItem("selected");
        assertEquals("selected", adapter.getSelectedItem());
    }
}
