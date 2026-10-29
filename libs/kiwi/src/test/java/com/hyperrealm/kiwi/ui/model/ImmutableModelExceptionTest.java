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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for {@link ImmutableModelException}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class ImmutableModelExceptionTest {

    /**
     * Test exception construction with no message.
     */
    @Test
    public void testExceptionConstructionNoMessage() {
        ImmutableModelException ex = new ImmutableModelException();
        assertNotNull(ex);
    }

    /**
     * Test exception construction with message.
     */
    @Test
    public void testExceptionConstructionWithMessage() {
        ImmutableModelException ex = new ImmutableModelException("Model is immutable");
        assertNotNull(ex);
        assertEquals("Model is immutable", ex.getMessage());
    }

    /**
     * Test that it is a RuntimeException.
     */
    @Test
    public void testIsRuntimeException() {
        ImmutableModelException ex = new ImmutableModelException();
        assertTrue(ex instanceof RuntimeException);
    }
}
