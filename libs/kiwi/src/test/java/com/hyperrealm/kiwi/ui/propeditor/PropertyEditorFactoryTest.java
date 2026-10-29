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
import org.junit.Test;

/**
 * Unit tests for {@link PropertyEditorFactory}.
 */
public class PropertyEditorFactoryTest {

    /**
     * Test factory pattern.
     */
    @Test
    public void testFactoryPattern() {
        PropertyEditorFactory factory = new DefaultPropertyEditorFactory();
        assertNotNull(factory);
    }

    /**
     * Test createEditor for types.
     */
    @Test
    public void testCreateEditorForTypes() {
        PropertyEditorFactory factory = new DefaultPropertyEditorFactory();
        TextPropertyType textType = new TextPropertyType();
        PropertyValueEditor editor = factory.createEditor(textType);
        assertNotNull(editor);

        NumericPropertyType numType = new NumericPropertyType();
        editor = factory.createEditor(numType);
        assertNotNull(editor);

        SelectionPropertyType selType = new SelectionPropertyType();
        editor = factory.createEditor(selType);
        assertNotNull(editor);
    }

    /**
     * Test createEditor for null type.
     */
    @Test
    public void testCreateEditorForNullType() {
        PropertyEditorFactory factory = new DefaultPropertyEditorFactory();
        // Should not throw
        PropertyValueEditor editor = factory.createEditor(null);
        // May return null or default editor
    }
}
