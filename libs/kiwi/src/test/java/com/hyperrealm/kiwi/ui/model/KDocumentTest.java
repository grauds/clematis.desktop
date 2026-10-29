package com.hyperrealm.kiwi.ui.model;
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

import javax.swing.text.BadLocationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link KDocument}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KDocumentTest {

    /**
     * Test insertString with unlimited length.
     */
    @Test
    public void testInsertStringUnlimited() throws BadLocationException {
        KDocument doc = new KDocument();
        doc.insertString(0, "Hello", null);
        assertEquals("Hello", doc.getText(0, doc.getLength()));
    }

    /**
     * Test insertString with limited length.
     */
    @Test
    public void testInsertStringLimited() throws BadLocationException {
        KDocument doc = new KDocument(5);
        doc.insertString(0, "Hello", null);
        assertEquals(5, doc.getLength());
        doc.insertString(0, "World", null);
        // Should be truncated to 5 chars
        assertEquals(5, doc.getLength());
    }

    /**
     * Test truncate removes excess characters.
     */
    @Test
    public void testTruncate() throws BadLocationException {
        KDocument doc = new KDocument(3);
        doc.insertString(0, "Hello", null);
        assertEquals(3, doc.getLength());
        assertEquals("Hel", doc.getText(0, doc.getLength()));
    }

    /**
     * Test isModified/clearModified.
     */
    @Test
    public void testIsModifiedClearModified() throws BadLocationException {
        KDocument doc = new KDocument();
        assertFalse(doc.isModified());
        doc.insertString(0, "Hello", null);
        assertTrue(doc.isModified());
        doc.clearModified();
        assertFalse(doc.isModified());
    }

    /**
     * Test NO_LIMIT constant.
     */
    @Test
    public void testNoLimit() {
        assertEquals(0, KDocument.NO_LIMIT);
    }

    /**
     * Test remove operation.
     */
    @Test
    public void testRemove() throws BadLocationException {
        KDocument doc = new KDocument();
        doc.insertString(0, "Hello World", null);
        doc.remove(5, 1); // Remove space
        assertEquals("HelloWorld", doc.getText(0, doc.getLength()));
        assertTrue(doc.isModified());
    }

    /**
     * Test get/set maximum length.
     */
    @Test
    public void testGetSetMaximumLength() {
        KDocument doc = new KDocument();
        assertEquals(KDocument.NO_LIMIT, doc.getMaximumLength());
        doc.setMaximumLength(10);
        assertEquals(10, doc.getMaximumLength());
    }
}
