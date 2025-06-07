
package com.hyperrealm.kiwi.event.tree;

import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class TreeNodeMouseAdapterTest {

    private TreeNodeMouseAdapter adapter;
    private JTree mockTree;
    private MouseEvent mockEvent;
    private TreePath mockPath;

    @BeforeEach
    void setUp() {
        adapter = spy(new TreeNodeMouseAdapter());
        mockTree = mock(JTree.class);
        mockEvent = mock(MouseEvent.class);
        mockPath = mock(TreePath.class);

        when(mockEvent.getSource()).thenReturn(mockTree);
        when(mockEvent.getX()).thenReturn(10);
        when(mockEvent.getY()).thenReturn(20);
        when(mockTree.getRowForLocation(10, 20)).thenReturn(1); // Valid row
        when(mockTree.getPathForLocation(10, 20)).thenReturn(mockPath);
    }

    @Test
    void testSingleClickDispatchesToNodeClicked() {
        // Arrange
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getButton()).thenReturn(MouseEvent.BUTTON1);

        // Act
        adapter.mouseClicked(mockEvent);

        // Assert
        verify(adapter).nodeClicked(mockPath, MouseEvent.BUTTON1);
        verify(adapter, never()).nodeDoubleClicked(any(), anyInt());
    }

    @Test
    void testDoubleClickDispatchesToNodeDoubleClicked() {
        // Arrange
        when(mockEvent.getClickCount()).thenReturn(2);
        when(mockEvent.getButton()).thenReturn(MouseEvent.BUTTON2);

        // Act
        adapter.mouseClicked(mockEvent);

        // Assert
        verify(adapter).nodeDoubleClicked(mockPath, MouseEvent.BUTTON2);
        verify(adapter, never()).nodeClicked(any(), anyInt());
    }

    @Test
    void testTripleClickIgnored() {
        // Arrange
        when(mockEvent.getClickCount()).thenReturn(3);

        // Act
        adapter.mouseClicked(mockEvent);

        // Assert
        verify(adapter, never()).nodeClicked(any(), anyInt());
        verify(adapter, never()).nodeDoubleClicked(any(), anyInt());
    }

    @Test
    void testNoRowSelected() {
        // Arrange
        when(mockTree.getRowForLocation(10, 20)).thenReturn(-1); // No row selected

        // Act
        adapter.mouseClicked(mockEvent);

        // Assert
        verify(adapter, never()).nodeClicked(any(), anyInt());
        verify(adapter, never()).nodeDoubleClicked(any(), anyInt());
    }

    @Test
    void testNonTreeSourceIgnored() {
        // Arrange
        when(mockEvent.getSource()).thenReturn(new Object()); // Not a JTree

        // Act
        adapter.mouseClicked(mockEvent);

        // Assert
        verify(adapter, never()).nodeClicked(any(), anyInt());
        verify(adapter, never()).nodeDoubleClicked(any(), anyInt());
    }

    @Test
    void testDefaultNodeClickedIsNoOp() {
        // Create a real instance (not a spy)
        TreeNodeMouseAdapter realAdapter = new TreeNodeMouseAdapter();

        // Act & Assert (should not throw any exceptions)
        realAdapter.nodeClicked(mockPath, MouseEvent.BUTTON1);
    }

    @Test
    void testDefaultNodeDoubleClickedIsNoOp() {
        // Create a real instance (not a spy)
        TreeNodeMouseAdapter realAdapter = new TreeNodeMouseAdapter();

        // Act & Assert (should not throw any exceptions)
        realAdapter.nodeDoubleClicked(mockPath, MouseEvent.BUTTON1);
    }
}