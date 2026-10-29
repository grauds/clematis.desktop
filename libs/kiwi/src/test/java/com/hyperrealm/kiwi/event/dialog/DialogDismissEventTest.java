package com.hyperrealm.kiwi.event.dialog;
/* ----------------------------------------------------------------------------
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
   ----------------------------------------------------------------------------
*/

import java.awt.Dialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link DialogDismissEvent}.
 */
public class DialogDismissEventTest {

    /**
     * Tests the two-argument constructor with source and type.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    public void testConstructorWithSourceAndType() {
        // Arrange
        Dialog mockDialog = new Dialog(null, "Test", java.awt.Dialog.ModalityType.MODELESS);

        // Act
        DialogDismissEvent event = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.OK);

        // Assert
        assertEquals(mockDialog, event.getSource());
        assertEquals(DialogDismissEvent.OK, event.getType());
        assertNull(event.getUserObject());
        assertFalse(event.isCancelled());
    }

    /**
     * Tests the three-argument constructor with source, type, and userObject.
     */
    @Test
    public void testConstructorWithSourceTypeAndUserObject() {
        // Arrange
        Dialog mockDialog = new Dialog(null, "Test", java.awt.Dialog.ModalityType.MODELESS);
        Object userObject = new Object();

        // Act
        DialogDismissEvent event = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.CANCEL, userObject);

        // Assert
        assertEquals(mockDialog, event.getSource());
        assertEquals(DialogDismissEvent.CANCEL, event.getType());
        assertEquals(userObject, event.getUserObject());
        assertTrue(event.isCancelled());
    }

    /**
     * Tests that OK dismissal type is correctly identified.
     */
    @Test
    public void testOkDismissalType() {
        // Arrange
        Dialog mockDialog = new Dialog(null, "Test", java.awt.Dialog.ModalityType.MODELESS);

        // Act
        DialogDismissEvent event = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.OK);

        // Assert
        assertEquals(DialogDismissEvent.OK, event.getType());
        assertFalse(event.isCancelled());
    }

    /**
     * Tests that CANCEL dismissal type is correctly identified.
     */
    @Test
    public void testCancelDismissalType() {
        // Arrange
        Dialog mockDialog = new Dialog(null, "Test", java.awt.Dialog.ModalityType.MODELESS);

        // Act
        DialogDismissEvent event = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.CANCEL);

        // Assert
        assertEquals(DialogDismissEvent.CANCEL, event.getType());
        assertTrue(event.isCancelled());
    }

    /**
     * Tests that isCancelled() correctly reflects the dismissal type.
     */
    @Test
    public void testIsCancelledReflectsType() {
        // Arrange
        Dialog mockDialog = new Dialog(null, "Test", java.awt.Dialog.ModalityType.MODELESS);

        // Act & Assert - OK case
        DialogDismissEvent okEvent = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.OK);
        assertFalse(okEvent.isCancelled());
        assertEquals(okEvent.isCancelled(),
            (okEvent.getType() == DialogDismissEvent.CANCEL));

        // Act & Assert - CANCEL case
        DialogDismissEvent cancelEvent = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.CANCEL);
        assertTrue(cancelEvent.isCancelled());
        assertEquals(cancelEvent.isCancelled(),
            (cancelEvent.getType() == DialogDismissEvent.CANCEL));
    }

    /**
     * Tests that getUserObject() returns null when no user object was provided.
     */
    @Test
    public void testNullUserObject() {
        // Arrange
        Dialog mockDialog = new Dialog(null, "Test", java.awt.Dialog.ModalityType.MODELESS);

        // Act
        DialogDismissEvent event = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.OK);

        // Assert
        assertNull(event.getUserObject());
    }

    /**
     * Tests that getUserObject() returns the provided user object.
     */
    @Test
    public void testUserObjectReturned() {
        // Arrange
        Dialog mockDialog = new Dialog(null, "Test", java.awt.Dialog.ModalityType.MODELESS);
        String userObject = "test data";

        // Act
        DialogDismissEvent event = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.OK, userObject);

        // Assert
        assertNotNull(event.getUserObject());
        assertEquals(userObject, event.getUserObject());
    }

    /**
     * Tests that getSource() returns the correct dialog.
     */
    @Test
    public void testSourceIsCorrectDialog() {
        // Arrange
        Dialog mockDialog = new Dialog(null, "Test", java.awt.Dialog.ModalityType.MODELESS);

        // Act
        DialogDismissEvent event = new DialogDismissEvent(
            mockDialog, DialogDismissEvent.OK);

        // Assert
        assertEquals(mockDialog, event.getSource());
    }

    /**
     * Tests that different dismissal types have different type values.
     */
    @Test
    public void testDismissalTypesAreDistinct() {
        // Assert - CANCEL and OK should have different values
        org.junit.Assert.assertNotEquals(
            Integer.valueOf(DialogDismissEvent.CANCEL),
            Integer.valueOf(DialogDismissEvent.OK));
    }
}
