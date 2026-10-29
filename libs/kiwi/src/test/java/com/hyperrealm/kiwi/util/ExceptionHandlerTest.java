package com.hyperrealm.kiwi.util;
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
import org.junit.Test;

/**
 * Unit tests for {@link ExceptionHandler} interface.
 */
public class ExceptionHandlerTest {

    /**
     * Test showException - verify interface can be implemented.
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    public void testExceptionHandlerImplementation() {
        final Exception[] captured = new Exception[1];
        ExceptionHandler handler = ex -> captured[0] = ex;
        Exception testEx = new RuntimeException("test");
        handler.exceptionRaised(testEx);
        assertNotNull(captured[0]);
        assertEquals("test", captured[0].getMessage());
    }

    /**
     * Test that ExceptionHandler is a functional interface.
     */
    @Test
    public void testFunctionalInterface() {
        ExceptionHandler handler = ex -> {
            // No-op handler
        };
        handler.exceptionRaised(new Exception("dummy"));
    }
}
