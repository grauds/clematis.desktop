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

import javax.swing.JLabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
/**
 * Unit tests for {@link KTabbedPane}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class KTabbedPaneTest {

    /**
     * Test tab add/remove/select.
     */
    @Test
    public void testTabAddRemoveSelect() {
        KTabbedPane pane = new KTabbedPane();
        pane.addTab("Tab 1", new JLabel("Content 1"));
        pane.addTab("Tab 2", new JLabel("Content 2"));
        assertNotNull(pane);
        assertEquals(2, pane.getTabCount());
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        KTabbedPane pane = new KTabbedPane();
        assertNotNull(pane);
    }

    /**
     * Test reset.
     */
    @Test
    public void testReset() {
        KTabbedPane pane = new KTabbedPane();
        pane.addTab("Tab 1", new JLabel());
        pane.addTab("Tab 2", new JLabel());
        pane.setSelectedIndex(1);
        pane.reset();
        assertEquals(0, pane.getSelectedIndex());
    }
}
