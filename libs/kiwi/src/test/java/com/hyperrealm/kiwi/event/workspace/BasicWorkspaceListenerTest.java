package com.hyperrealm.kiwi.event.workspace;

import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicWorkspaceListenerTest {

    private BasicWorkspaceListener listener;
    private WorkspaceEvent mockEvent;

    @BeforeEach
    void setUp() {
        listener = new BasicWorkspaceListener();
        mockEvent = mock(WorkspaceEvent.class);
    }

    @Test
    void testEditorSelected() {
        // Should not throw any exception
        listener.editorSelected(mockEvent);
    }

    @Test
    void testEditorDeselected() {
        // Should not throw any exception
        listener.editorDeselected(mockEvent);
    }

    @Test
    void testEditorRestored() {
        // Should not throw any exception
        listener.editorRestored(mockEvent);
    }

    @Test
    void testEditorIconified() {
        // Should not throw any exception
        listener.editorIconified(mockEvent);
    }

    @Test
    void testEditorClosed() {
        // Should not throw any exception
        listener.editorClosed(mockEvent);
    }

    @Test
    void testEditorStateChanged() {
        // Should not throw any exception
        listener.editorStateChanged(mockEvent);
    }
}