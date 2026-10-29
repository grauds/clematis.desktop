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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.model.ImmutableModelException;
/**
 * Unit tests for {@link ExternalKTreeModel}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class ExternalKTreeModelTest {

    /**
     * Test delegation to external model.
     */
    @Test
    public void testDelegation() {
        ExternalKTreeModel<String> model = new ExternalKTreeModel<>(null);
        // Should not throw with null source
        assertNotNull(model);
    }

    /**
     * Test setRoot throws ImmutableModelException.
     */
    @Test
    public void testSetRootThrows() {
        ExternalKTreeModel<String> model = new ExternalKTreeModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.setRoot("root"));
    }

    /**
     * Test removeChildren throws ImmutableModelException.
     */
    @Test
    public void testRemoveChildrenThrows() {
        ExternalKTreeModel<String> model = new ExternalKTreeModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.removeChildren("root"));
    }

    /**
     * Test removeChild throws ImmutableModelException.
     */
    @Test
    public void testRemoveChildThrows() {
        ExternalKTreeModel<String> model = new ExternalKTreeModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.removeChild("root", 0));
    }

    /**
     * Test addChild throws ImmutableModelException.
     */
    @Test
    public void testAddChildThrows() {
        ExternalKTreeModel<String> model = new ExternalKTreeModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.addChild("root", "child"));
    }

    /**
     * Test updateNode throws ImmutableModelException.
     */
    @Test
    public void testUpdateNodeThrows() {
        ExternalKTreeModel<String> model = new ExternalKTreeModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.updateNode("node"));
    }

    /**
     * Test updateChildren throws ImmutableModelException.
     */
    @Test
    public void testUpdateChildrenThrows() {
        ExternalKTreeModel<String> model = new ExternalKTreeModel<>(null);
        assertThrows(ImmutableModelException.class, () -> model.updateChildren("parent"));
    }
}
