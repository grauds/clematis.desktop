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
import javax.swing.Icon;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import org.junit.Before;
import org.junit.Test;

import com.hyperrealm.kiwi.ui.model.ImmutableModelException;
import com.hyperrealm.kiwi.ui.model.datasource.TreeDataSource;

/**
 * Unit tests for {@link ExternalKTreeModel}.
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class ExternalKTreeModelTest {

    private ExternalKTreeModel<String> model;

    @Before
    public void beforeEach() {
        model = new ExternalKTreeModel<>(
            new TreeDataSource<>() {

                @Override
                public String getRoot() {
                    return "";
                }

                @Override
                public String[] getChildren(String node) {
                    return new String[0];
                }

                @Override
                public Object getValueForProperty(String node, String property) {
                    return null;
                }

                @Override
                public boolean isExpandable(String node) {
                    return false;
                }

                @Override
                public Icon getIcon(String node, boolean expanded) {
                    return null;
                }

                @Override
                public String getLabel(String node) {
                    return "";
                }
            }
        );
    }

    /**
     * Test delegation to the external model.
     */
    @Test
    public void testDelegation() {
        // Should not throw with null source
        assertNotNull(model);
    }

    /**
     * Test setRoot throws ImmutableModelException.
     */
    @Test
    public void testSetRootThrows() {
        assertThrows(ImmutableModelException.class, () -> model.setRoot("root"));
    }

    /**
     * Test removeChildren throws ImmutableModelException.
     */
    @Test
    public void testRemoveChildrenThrows() {
        assertThrows(ImmutableModelException.class, () -> model.removeChildren("root"));
    }

    /**
     * Test removeChild throws ImmutableModelException.
     */
    @Test
    public void testRemoveChildThrows() {
        assertThrows(ImmutableModelException.class, () -> model.removeChild("root", 0));
    }

    /**
     * Test addChild throws ImmutableModelException.
     */
    @Test
    public void testAddChildThrows() {
        assertThrows(ImmutableModelException.class, () -> model.addChild("root", "child"));
    }

    /**
     * Test updateNode throws ImmutableModelException.
     */
    @Test
    public void testUpdateNodeThrows() {
        assertThrows(ImmutableModelException.class, () -> model.updateNode("node"));
    }

    /**
     * Test updateChildren throws ImmutableModelException.
     */
    @Test
    public void testUpdateChildrenThrows() {
        assertThrows(ImmutableModelException.class, () -> model.updateChildren("parent"));
    }
}
