package com.hyperrealm.kiwi.event.tree;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KTreeModelSupportTest {

    private final Object source = new Object();
    private final Object parentNode = new Object();
    private KTreeModelSupport support;
    private KTreeModelListener listener;

    @BeforeEach
    void setUp() {
        support = new KTreeModelSupport(source);
        listener = mock(KTreeModelListener.class);
        support.addTreeModelListener(listener);
    }

    @Test
    void testAddAndRemoveTreeModelListener() {
        // Arrange
        KTreeModelListener secondListener = mock(KTreeModelListener.class);

        // Act
        support.addTreeModelListener(secondListener);
        support.fireNodeAdded(parentNode, 0);
        support.removeTreeModelListener(secondListener);
        support.fireNodeAdded(parentNode, 1);

        // Assert
        verify(secondListener, times(1)).nodesAdded(any(KTreeModelEvent.class));
        verify(listener, times(2)).nodesAdded(any(KTreeModelEvent.class));
    }

    @Test
    void testFireNodeAdded() {
        // Act
        support.fireNodeAdded(parentNode, 5);

        // Assert
        verify(listener, times(1)).nodesAdded(argThat(evt ->
            evt.getNode() == parentNode
                && evt.getStartIndex() == 5
                && evt.getEndIndex() == 5
                && evt.getSource() == source
        ));
    }

    @Test
    void testFireNodeRemoved() {
        // Act
        support.fireNodeRemoved(parentNode, 3);

        // Assert
        verify(listener, times(1)).nodesRemoved(argThat(evt ->
            evt.getNode() == parentNode
                && evt.getStartIndex() == 3
                && evt.getEndIndex() == 3
                && evt.getSource() == source
        ));
    }

    @Test
    void testFireNodesRemoved() {
        // Act
        support.fireNodesRemoved(parentNode, 1, 5);

        // Assert
        verify(listener, times(1)).nodesRemoved(argThat(evt ->
            evt.getNode() == parentNode
                && evt.getStartIndex() == 1
                && evt.getEndIndex() == 5
                && evt.getSource() == source
        ));
    }

    @Test
    void testFireNodeChanged() {
        // Act
        support.fireNodeChanged(parentNode, 2);

        // Assert
        verify(listener, times(1)).nodesChanged(argThat(evt ->
            evt.getNode() == parentNode
                && evt.getStartIndex() == 2
                && evt.getEndIndex() == 2
                && evt.getSource() == source
        ));
    }

    @Test
    void testFireNodeStructureChanged() {
        // Act
        support.fireNodeStructureChanged(parentNode);

        // Assert
        verify(listener, times(1)).structureChanged(argThat(evt ->
            evt.getNode() == parentNode
                && evt.getStartIndex() == 0
                && evt.getEndIndex() == 0
                && evt.getSource() == source
        ));
    }

    @Test
    void testFireDataChanged() {
        // Act
        support.fireDataChanged();

        // Assert
        verify(listener, times(1)).dataChanged(argThat(evt ->
            evt.getNode() == null
                && evt.getStartIndex() == 0
                && evt.getEndIndex() == 0
                && evt.getSource() == source
        ));
    }

    @Test
    void testNoListenersCausesNoErrors() {
        // Arrange
        KTreeModelSupport emptySupport = new KTreeModelSupport(source);

        // Act & Assert (no exceptions should be thrown)
        emptySupport.fireNodeAdded(parentNode, 0);
        emptySupport.fireNodeRemoved(parentNode, 0);
        emptySupport.fireNodeChanged(parentNode, 0);
        emptySupport.fireNodeStructureChanged(parentNode);
        emptySupport.fireDataChanged();
    }
}