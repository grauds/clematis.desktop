package com.hyperrealm.kiwi.event.workspace;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hyperrealm.kiwi.ui.WorkspaceEditor;

class WorkspaceEventIntegrationTest {

    private WorkspaceListener listener;
    private WorkspaceEditor editor;
    private Object source;

    @BeforeEach
    void setUp() {
        listener = spy(new BasicWorkspaceListener());
        editor = mock(WorkspaceEditor.class);
        source = new Object();
    }

    @Test
    void testWorkspaceEventFlow() {
        // Create event
        WorkspaceEvent event = new WorkspaceEvent(source, editor);

        // Test all listener methods
        listener.editorSelected(event);
        listener.editorDeselected(event);
        listener.editorRestored(event);
        listener.editorIconified(event);
        listener.editorClosed(event);
        listener.editorStateChanged(event);

        // Verify all methods were called exactly once
        verify(listener).editorSelected(event);
        verify(listener).editorDeselected(event);
        verify(listener).editorRestored(event);
        verify(listener).editorIconified(event);
        verify(listener).editorClosed(event);
        verify(listener).editorStateChanged(event);
    }

    @Test
    void testMultipleListenersNotification() {
        WorkspaceListener listener2 = spy(new BasicWorkspaceListener());
        WorkspaceEvent event = new WorkspaceEvent(source, editor);

        // Notify both listeners
        listener.editorSelected(event);
        listener2.editorSelected(event);

        // Verify both listeners received the event
        verify(listener).editorSelected(event);
        verify(listener2).editorSelected(event);
    }
}
