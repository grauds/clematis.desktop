package com.hyperrealm.kiwi.runtime;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests for {@link Task}.
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class TaskTest {

    /**
     * Test create anonymous subclass.
     */
    @Test
    public void testCreateAnonymousSubclass() {
        Task task = new Task() {
            @Override
            public void run() {
                // No-op for test
            }
        };
        assertNotNull(task);
    }

    /**
     * Test add/remove observers.
     */
    @Test
    public void testAddRemoveObservers() {
        Task task = new Task() {
            @Override
            public void run() {
                // No-op for test
            }
        };
        ProgressObserver observer = new ProgressObserver() {
            @Override
            public void setProgress(int percent) {}
            @Override
            public void setMessage(String message) {}
            @Override
            public void setIcon(Icon icon) {}
        };
        task.addProgressObserver(observer);
        task.removeProgressObserver(observer);
        // Should not throw
    }

    /**
     * Test notifyObservers with icon.
     */
    @Test
    public void testNotifyObserversWithIcon() {
        final Icon[] received = new Icon[1];
        Task task = new Task() {
            @Override
            public void run() {}
        };
        ProgressObserver observer = new ProgressObserver() {
            @Override
            public void setProgress(int percent) {}
            @Override
            public void setMessage(String message) {}
            @Override
            public void setIcon(Icon icon) {
                received[0] = icon;
            }
        };
        task.addProgressObserver(observer);
        task.notifyObservers((Icon) null);
        assertNull(received[0]);
        task.removeProgressObserver(observer);
    }

    /**
     * Test notifyObservers with message.
     */
    @Test
    public void testNotifyObserversWithMessage() {
        final String[] received = new String[1];
        Task task = new Task() {
            @Override
            public void run() {}
        };
        ProgressObserver observer = new ProgressObserver() {
            @Override
            public void setProgress(int percent) {}
            @Override
            public void setMessage(String message) {
                received[0] = message;
            }
            @Override
            public void setIcon(Icon icon) {}
        };
        task.addProgressObserver(observer);
        task.notifyObservers("Test message");
        assertEquals("Test message", received[0]);
        task.removeProgressObserver(observer);
    }

    /**
     * Test notifyObservers with percent.
     */
    @Test
    public void testNotifyObserversWithPercent() {
        final int[] received = new int[1];
        Task task = new Task() {
            @Override
            public void run() {}
        };
        ProgressObserver observer = new ProgressObserver() {
            @Override
            public void setProgress(int percent) {
                received[0] = percent;
            }
            @Override
            public void setMessage(String message) {}
            @Override
            public void setIcon(Icon icon) {}
        };
        task.addProgressObserver(observer);
        task.notifyObservers(50);
        assertEquals(50, received[0]);
        task.removeProgressObserver(observer);
    }

    /**
     * Test percent clipping.
     */
    @Test
    public void testPercentClipping() {
        final int[] received = new int[1];
        Task task = new Task() {
            @Override
            public void run() {}
        };
        ProgressObserver observer = new ProgressObserver() {
            @Override
            public void setProgress(int percent) {
                received[0] = percent;
            }
            @Override
            public void setMessage(String message) {}
            @Override
            public void setIcon(Icon icon) {}
        };
        task.addProgressObserver(observer);
        task.notifyObservers(-10);
        assertEquals(0, received[0]);
        task.notifyObservers(150);
        assertEquals(100, received[0]);
        task.removeProgressObserver(observer);
    }
}
