package com.hyperrealm.kiwi.logging;
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
 * Unit tests for {@link NullLoggingEndpoint}.
 */
public class NullLoggingEndpointTest {

    /**
     * Test that logMessage is a no-op.
     */
    @Test
    public void testNoOpLogMessage() {
        NullLoggingEndpoint endpoint = new NullLoggingEndpoint();
        // Should not throw
        endpoint.logMessage(Types.INFO, "test message");
        endpoint.logMessage(Types.ERROR, "error message");
    }

    /**
     * Test that close is a no-op.
     */
    @Test
    public void testNoOpClose() {
        NullLoggingEndpoint endpoint = new NullLoggingEndpoint();
        // Should not throw
        endpoint.close();
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        NullLoggingEndpoint endpoint = new NullLoggingEndpoint();
        assertNotNull(endpoint);
    }
}
