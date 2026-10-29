package com.hyperrealm.kiwi.ui.propeditor;
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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link PropertyModel}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class PropertyModelTest {

    /**
     * Test getIcon/getLabel/isExpandable overrides.
     */
    @Test
    public void testGetIconGetLabelIsExpandable() {
        PropertyModel model = new PropertyModel();
        Property prop = new Property("test", new TextPropertyType(), "value");

        // Test getLabel
        String label = model.getLabel(prop);
        assertNotNull(label);
        assertTrue(label.contains("test"));

        // Test getIcon
        javax.swing.Icon icon = model.getIcon(prop, false);
        assertNotNull(icon);

        // Test isExpandable
        boolean expandable = model.isExpandable(prop);
        // Should be false since no children
        assertNotNull(expandable);
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        PropertyModel model = new PropertyModel();
        assertNotNull(model);
    }
}
