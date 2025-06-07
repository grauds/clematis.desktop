package com.hyperrealm.kiwi.event.tree;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class KTreeModelListenerImplementationTest {

    private static class TestKTreeModelListener implements KTreeModelListener {
        private boolean nodesAddedCalled = false;
        private boolean nodesRemovedCalled = false;
        private boolean nodesChangedCalled = false;
        private boolean structureChangedCalled = false;
        private boolean dataChangedCalled = false;

        @Override
        public void nodesAdded(KTreeModelEvent evt) {
            nodesAddedCalled = true;
        }

        @Override
        public void nodesRemoved(KTreeModelEvent evt) {
            nodesRemovedCalled = true;
        }

        @Override
        public void nodesChanged(KTreeModelEvent evt) {
            nodesChangedCalled = true;
        }

        @Override
        public void structureChanged(KTreeModelEvent evt) {
            structureChangedCalled = true;
        }

        @Override
        public void dataChanged(KTreeModelEvent evt) {
            dataChangedCalled = true;
        }
    }

    @Test
    void testListenerImplementation() {
        // Arrange
        TestKTreeModelListener listener = new TestKTreeModelListener();
        KTreeModelEvent event = new KTreeModelEvent(new Object());

        // Act
        listener.nodesAdded(event);
        listener.nodesRemoved(event);
        listener.nodesChanged(event);
        listener.structureChanged(event);
        listener.dataChanged(event);

        // Assert
        assertTrue(listener.nodesAddedCalled);
        assertTrue(listener.nodesRemovedCalled);
        assertTrue(listener.nodesChangedCalled);
        assertTrue(listener.structureChangedCalled);
        assertTrue(listener.dataChangedCalled);
    }
}