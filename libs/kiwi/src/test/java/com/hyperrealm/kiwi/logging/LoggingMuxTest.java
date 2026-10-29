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
 * Unit tests for {@link LoggingMux}.
 */
public class LoggingMuxTest {

    /**
     * Test add/remove endpoints.
     */
    @Test
    public void testAddRemoveEndpoints() {
        LoggingMux mux = new LoggingMux();
        NullLoggingEndpoint endpoint = new NullLoggingEndpoint();
        mux.addLoggingEndpoint(endpoint);
        mux.removeLoggingEndpoint(endpoint);
        // Should not throw
    }

    /**
     * Test distribute logMessage.
     */
    @Test
    public void testDistributeLogMessage() {
        LoggingMux mux = new LoggingMux();
        NullLoggingEndpoint endpoint1 = new NullLoggingEndpoint();
        NullLoggingEndpoint endpoint2 = new NullLoggingEndpoint();
        mux.addLoggingEndpoint(endpoint1);
        mux.addLoggingEndpoint(endpoint2);
        // Should distribute to all endpoints without throwing
        mux.logMessage(Types.INFO, "test message");
        mux.logMessage(Types.ERROR, "error message");
    }

    /**
     * Test close.
     */
    @Test
    public void testClose() {
        LoggingMux mux = new LoggingMux();
        NullLoggingEndpoint endpoint = new NullLoggingEndpoint();
        mux.addLoggingEndpoint(endpoint);
        mux.close();
        // Should not throw
    }

    /**
     * Test close with closeEndpoints.
     */
    @Test
    public void testCloseWithCloseEndpoints() {
        LoggingMux mux = new LoggingMux();
        NullLoggingEndpoint endpoint = new NullLoggingEndpoint();
        mux.addLoggingEndpoint(endpoint);
        mux.close(true);
        // Should not throw
    }

    /**
     * Test removeAllLoggingEndpoints.
     */
    @Test
    public void testRemoveAllLoggingEndpoints() {
        LoggingMux mux = new LoggingMux();
        mux.addLoggingEndpoint(new NullLoggingEndpoint());
        mux.removeAllLoggingEndpoints();
        // Should not throw
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        LoggingMux mux = new LoggingMux();
        assertNotNull(mux);
    }
}
