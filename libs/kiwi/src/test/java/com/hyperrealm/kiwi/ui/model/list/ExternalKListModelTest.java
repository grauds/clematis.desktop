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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.model.ImmutableModelException;

/**
 * Unit tests for {@link ExternalKListModel}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class ExternalKListModelTest {

    /**
     * Test delegation to external model.
     */
    @Test
    public void testDelegation() {
        ExternalKListModel<String> model = new ExternalKListModel<>(null);
        // Should not throw with null source
        assertTrue(model.isEmpty());
        assertEquals(0, model.getItemCount());
    }

    /**
     * Test addItem throws ImmutableModelException.
     */
    @Test
    public void testAddItemThrows() {
        ExternalKListModel<String> model = new ExternalKListModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.addItem("item"));
    }

    /**
     * Test insertItemAt throws ImmutableModelException.
     */
    @Test
    public void testInsertItemAtThrows() {
        ExternalKListModel<String> model = new ExternalKListModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.insertItemAt("item", 0));
    }

    /**
     * Test removeItemAt throws ImmutableModelException.
     */
    @Test
    public void testRemoveItemAtThrows() {
        ExternalKListModel<String> model = new ExternalKListModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.removeItemAt(0));
    }

    /**
     * Test removeItem throws ImmutableModelException.
     */
    @Test
    public void testRemoveItemThrows() {
        ExternalKListModel<String> model = new ExternalKListModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.removeItem("item"));
    }

    /**
     * Test updateItem throws ImmutableModelException.
     */
    @Test
    public void testUpdateItemThrows() {
        ExternalKListModel<String> model = new ExternalKListModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.updateItem("item"));
    }

    /**
     * Test updateItemAt throws ImmutableModelException.
     */
    @Test
    public void testUpdateItemAtThrows() {
        ExternalKListModel<String> model = new ExternalKListModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.updateItemAt(0));
    }
}
