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

import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JTextField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link PropertyValueEditor}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class PropertyValueEditorTest {

    /**
     * Test abstract base.
     */
    @Test
    public void testAbstractBase() {
        PropertyValueEditor editor = new PropertyValueEditor() {
            @Override
            public JComponent getEditorComponent() {
                return new JTextField();
            }
            @Override
            public void startFocus() {}
            @Override
            public void addActionListener(ActionListener l) {}
            @Override
            public void removeActionListener(ActionListener l) {}
        };
        assertNotNull(editor);
    }

    /**
     * Test setProperty/getProperty.
     */
    @Test
    public void testSetPropertyGetProperty() {
        PropertyValueEditor editor = new PropertyValueEditor() {
            @Override
            public JComponent getEditorComponent() {
                return new JTextField();
            }
            @Override
            public void startFocus() {}
            @Override
            public void addActionListener(ActionListener l) {}
            @Override
            public void removeActionListener(ActionListener l) {}
        };
        assertNull(editor.getProperty());
        Property prop = new Property("test", new TextPropertyType(), "value");
        editor.setProperty(prop);
        assertNotNull(editor.getProperty());
        assertEquals("test", editor.getProperty().getName());
    }

    /**
     * Test validateInput default returns true.
     */
    @Test
    public void testValidateInputDefault() {
        PropertyValueEditor editor = new PropertyValueEditor() {
            @Override
            public JComponent getEditorComponent() {
                return new JTextField();
            }
            @Override
            public void startFocus() {}
            @Override
            public void addActionListener(ActionListener l) {}
            @Override
            public void removeActionListener(ActionListener l) {}
        };
        assertTrue(editor.validateInput());
    }

    /**
     * Test commitInput default is no-op.
     */
    @Test
    public void testCommitInputDefault() {
        PropertyValueEditor editor = new PropertyValueEditor() {
            @Override
            public JComponent getEditorComponent() {
                return new JTextField();
            }
            @Override
            public void startFocus() {}
            @Override
            public void addActionListener(ActionListener l) {}
            @Override
            public void removeActionListener(ActionListener l) {}
        };
        // Should not throw
        editor.commitInput();
    }
}
