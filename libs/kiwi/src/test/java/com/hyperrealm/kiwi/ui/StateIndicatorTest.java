package com.hyperrealm.kiwi.ui;
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
import javax.swing.ImageIcon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link StateIndicator}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class StateIndicatorTest {

    /**
     * Test state display.
     */
    @Test
    public void testStateDisplay() {
        Icon defaultIcon = new ImageIcon();
        StateIndicator indicator = new StateIndicator(defaultIcon);
        indicator.addState("active", new ImageIcon());
        indicator.addState("inactive", new ImageIcon());
        indicator.setState("active");
        assertEquals("active", indicator.getState());
    }

    /**
     * Test clearStates.
     */
    @Test
    public void testClearStates() {
        StateIndicator indicator = new StateIndicator(new ImageIcon());
        indicator.addState("state1", new ImageIcon());
        indicator.clearStates();
        // Should fall back to default
        indicator.setState("state1");
        assertNull(indicator.getState());
    }

    /**
     * Test removeState.
     */
    @Test
    public void testRemoveState() {
        StateIndicator indicator = new StateIndicator(new ImageIcon());
        indicator.addState("state1", new ImageIcon());
        indicator.removeState("state1");
        indicator.setState("state1");
        assertNull(indicator.getState());
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        StateIndicator indicator = new StateIndicator(new ImageIcon());
        assertNotNull(indicator);
    }
}
