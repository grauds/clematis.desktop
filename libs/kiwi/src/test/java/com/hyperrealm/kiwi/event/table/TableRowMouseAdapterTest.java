package com.hyperrealm.kiwi.event.table;
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

import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

/**
 * Unit tests for {@link TableRowMouseAdapter}.
 * Tests single-click and double-click row handling in JTable.
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class TableRowMouseAdapterTest {

    /**
     * Tests that a single click on a row invokes rowClicked.
     */
    @Test
    public void testSingleClickInvokesRowClicked() {
        // Arrange
        JTable table = createTableWithRows(5);
        final int[] clickedRow = {-1};
        final int[] clickedButton = {-1};

        TableRowMouseAdapter adapter = new TableRowMouseAdapter() {
            @Override
            public void rowClicked(final int row, final int button) {
                clickedRow[0] = row;
                clickedButton[0] = button;
            }
        };

        // Create a mouse click event at row 2, left button
        MouseEvent evt = createMouseEvent(table, 50, 20, 1);

        // Act
        adapter.mouseClicked(evt);

        // Assert
        assertEquals(1, clickedRow[0]);
        assertEquals(MouseEvent.BUTTON1, clickedButton[0]);
    }

    /**
     * Tests that a double click on a row invokes rowDoubleClicked.
     */
    @Test
    public void testDoubleClickInvokesRowDoubleClicked() {
        // Arrange
        JTable table = createTableWithRows(5);
        final int[] doubleClickedRow = {-1};
        final int[] doubleClickedButton = {-1};

        TableRowMouseAdapter adapter = new TableRowMouseAdapter() {
            @Override
            public void rowDoubleClicked(final int row, final int button) {
                doubleClickedRow[0] = row;
                doubleClickedButton[0] = button;
            }
        };

        // Create a mouse double-click event at row 3, right button
        MouseEvent evt = createMouseEvent(table, 50, 40, 2);

        // Act
        adapter.mouseClicked(evt);

        // Assert
        assertEquals(2, doubleClickedRow[0]);
        assertEquals(MouseEvent.BUTTON1, doubleClickedButton[0]);
    }

    /**
     * Tests that clicking outside any row does not invoke any handler.
     */
    @Test
    public void testClickOutsideRowDoesNotInvokeHandler() {
        // Arrange
        JTable table = createTableWithRows(5);
        final boolean[] clicked = {false};

        TableRowMouseAdapter adapter = new TableRowMouseAdapter() {
            @Override
            public void rowClicked(final int row, final int button) {
                clicked[0] = true;
            }

            @Override
            public void rowDoubleClicked(final int row, final int button) {
                clicked[0] = true;
            }
        };

        // Create a mouse click event above the table (row -1)
        MouseEvent evt = createMouseEvent(table, 50, -20, 1);

        // Act
        adapter.mouseClicked(evt);

        // Assert - no handler should have been invoked
        assertFalse(clicked[0]);
    }

    /**
     * Tests that mouse events from non-JTable sources are ignored.
     */
    @Test
    public void testNonTableSourceIsIgnored() {
        // Arrange
        final boolean[] clicked = {false};

        TableRowMouseAdapter adapter = new TableRowMouseAdapter() {
            @Override
            public void rowClicked(final int row, final int button) {
                clicked[0] = true;
            }
        };

        // Create a mouse event with a non-JTable source
        JComponent nonTableSource = new JComponent() {};
        MouseEvent evt = new MouseEvent(
            nonTableSource,
            MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(),
            MouseEvent.BUTTON1_MASK,
            50, 20,
            1, false);

        // Act
        adapter.mouseClicked(evt);

        // Assert - no handler should have been invoked
        assertFalse(clicked[0]);
    }

    /**
     * Tests that triple-click (or more) does not invoke any handler.
     */
    @Test
    public void testTripleClickDoesNotInvokeHandler() {
        // Arrange
        JTable table = createTableWithRows(5);
        final boolean[] clicked = {false};

        TableRowMouseAdapter adapter = new TableRowMouseAdapter() {
            @Override
            public void rowClicked(final int row, final int button) {
                clicked[0] = true;
            }

            @Override
            public void rowDoubleClicked(final int row, final int button) {
                clicked[0] = true;
            }
        };

        // Create a mouse triple-click event
        MouseEvent evt = createMouseEvent(table, 50, 20, 3);

        // Act
        adapter.mouseClicked(evt);

        // Assert - no handler should have been invoked for triple-click
        assertFalse(clicked[0]);
    }

    /**
     * Tests that middle button clicks are correctly passed to handlers.
     */
    @Test
    public void testMiddleButtonClick() {
        // Arrange
        JTable table = createTableWithRows(5);
        final int[] clickedButton = {-1};

        TableRowMouseAdapter adapter = new TableRowMouseAdapter() {
            @Override
            public void rowClicked(final int row, final int button) {
                clickedButton[0] = button;
            }
        };

        // Create a mouse click event with middle button
        MouseEvent evt = new MouseEvent(
            table,
            MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(),
            MouseEvent.BUTTON2_MASK,
            50, 20,
            1, false);

        // Act
        adapter.mouseClicked(evt);

        // Assert
        assertEquals(MouseEvent.BUTTON2, clickedButton[0]);
    }

    /**
     * Tests that clicking on the first row (index 0) works correctly.
     */
    @Test
    public void testClickOnFirstRow() {
        // Arrange
        JTable table = createTableWithRows(5);
        final int[] clickedRow = {-1};

        TableRowMouseAdapter adapter = new TableRowMouseAdapter() {
            @Override
            public void rowClicked(final int row, final int button) {
                clickedRow[0] = row;
            }
        };

        // Create a mouse click event at the first row
        MouseEvent evt = createMouseEvent(table, 50, 5, 1);

        // Act
        adapter.mouseClicked(evt);

        // Assert
        assertEquals(0, clickedRow[0]);
    }

    /**
     * Helper method to create a JTable with the specified number of rows.
     */
    private JTable createTableWithRows(final int rowCount) {
        String[] columns = {"Column 1"};
        Object[][] data = new Object[rowCount][1];
        for (int i = 0; i < rowCount; i++) {
            data[i][0] = "Row " + i;
        }
        DefaultTableModel model = new DefaultTableModel(data, columns);
        return new JTable(model);
    }

    /**
     * Helper method to create a MouseEvent at the specified position.
     */
    private MouseEvent createMouseEvent(
        final JTable table, final int x, final int y, final int clickCount) {
        return new MouseEvent(
            table,
            MouseEvent.MOUSE_CLICKED,
            System.currentTimeMillis(),
            MouseEvent.BUTTON1_MASK,
            x, y,
            clickCount, false);
    }
}
