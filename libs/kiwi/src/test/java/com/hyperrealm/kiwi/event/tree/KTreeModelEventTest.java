package com.hyperrealm.kiwi.event.tree;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class KTreeModelEventTest {

    private final Object source = new Object();
    private final Object node = new Object();

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testConstructorWithAllParameters() {
        // Arrange
        int startIndex = 5;
        int endIndex = 10;

        // Act
        KTreeModelEvent event = new KTreeModelEvent(source, node, startIndex, endIndex);

        // Assert
        assertEquals(source, event.getSource());
        assertEquals(node, event.getNode());
        assertEquals(startIndex, event.getStartIndex());
        assertEquals(endIndex, event.getEndIndex());
    }

    @Test
    void testConstructorWithNodeOnly() {
        // Act
        KTreeModelEvent event = new KTreeModelEvent(source, node);

        // Assert
        assertEquals(source, event.getSource());
        assertEquals(node, event.getNode());
        assertEquals(0, event.getStartIndex());
        assertEquals(0, event.getEndIndex());
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    void testConstructorWithNodeAndSingleIndex() {
        // Arrange
        int index = 3;

        // Act
        KTreeModelEvent event = new KTreeModelEvent(source, node, index);

        // Assert
        assertEquals(source, event.getSource());
        assertEquals(node, event.getNode());
        assertEquals(index, event.getStartIndex());
        assertEquals(index, event.getEndIndex());
    }

    @Test
    void testConstructorWithSourceOnly() {
        // Act
        KTreeModelEvent event = new KTreeModelEvent(source);

        // Assert
        assertEquals(source, event.getSource());
        assertNull(event.getNode());
        assertEquals(0, event.getStartIndex());
        assertEquals(0, event.getEndIndex());
    }
}