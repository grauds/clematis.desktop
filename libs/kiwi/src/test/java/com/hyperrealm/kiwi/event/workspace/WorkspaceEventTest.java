package com.hyperrealm.kiwi.event.workspace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.Test;

import com.hyperrealm.kiwi.ui.WorkspaceEditor;

class WorkspaceEventTest {

    @Test
    void testWorkspaceEventConstructionAndGetters() {
        // Arrange
        Object source = new Object();
        WorkspaceEditor mockEditor = mock(WorkspaceEditor.class);

        // Act
        WorkspaceEvent event = new WorkspaceEvent(source, mockEditor);

        // Assert
        assertNotNull(event);
        assertEquals(source, event.getSource());
        assertEquals(mockEditor, event.getEditor());
    }

    @Test
    void testWorkspaceEventWithNullEditor() {
        // Arrange
        Object source = new Object();

        // Act
        WorkspaceEvent event = new WorkspaceEvent(source, null);

        // Assert
        assertNotNull(event);
        assertEquals(source, event.getSource());
        assertNull(event.getEditor());
    }
}