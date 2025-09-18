package jworkspace.ui.desktop.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import jworkspace.ui.desktop.DesktopShortcut;

/**
 * The DesktopInteractionLayer class extends {@code JComponent} to provide
 * interaction and user input handling capabilities for a desktop-like graphical interface.
 * This class is intended to work in conjunction with a {@code DesktopShortcutsLayer},
 * acting as an intermediary to capture, process, and respond to events such as mouse
 * clicks, dragging, and selection via a rubber-band mechanism.
 * <p>
 * Features:
 * - Mouse click handling to manage single and multi-selection of shortcuts.
 * - Support for dragging selected shortcuts as a group.
 * - Rubber-band selection capabilities for multiple shortcuts within a rectangular area.
 * - Highlighting the rubber-band selection overlay for better visual feedback.
 * <p>
 * Key Behavioral Details:
 * - Selection of shortcuts can be toggled with control clicks.
 * - Clicking empty space clears the current selection and starts rubber-band interaction.
 * - Dragging shortcuts during a mouse drag event updates their positions relative to the drag start position.
 * - Rubber-band selection dynamically updates as the mouse is dragged to include shortcuts within the area.
 * <p>
 * Rendering:
 * - This class paints a semi-transparent rectangle during the rubber-band selection to indicate
 *   the current selection bounds.
 * <p>
 * Methods:
 * - {@code installMouseHandlers}: Registers mouse event handlers for interaction logic.
 * - {@code paintComponent}: Handles custom painting for the rubber-band selection overlay.
 */
public class DesktopInteractionLayer extends JComponent {

    private final DesktopShortcutsLayer shortcutsLayer;

    private Rectangle selectionRect;
    private Point bandStart;
    private Point dragStart;

    private final Map<DesktopShortcut, Point> initialPositions = new HashMap<>();
    private boolean draggingIcons = false;

    public DesktopInteractionLayer(DesktopShortcutsLayer layer) {
        this.shortcutsLayer = layer;
        setOpaque(false);
        installMouseHandlers();
    }

    @SuppressWarnings("checkstyle:AnonInnerLength")
    private void installMouseHandlers() {
        MouseAdapter adapter = new MouseAdapter() {
            @SuppressWarnings("checkstyle:NestedIfDepth")
            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                Component c = shortcutsLayer.getComponentAt(e.getPoint());
                if (c instanceof DesktopShortcut shortcut) {
                    if (e.isControlDown()) {
                        if (shortcutsLayer.getSelectedShortcuts().contains(shortcut)) {
                            shortcutsLayer.removeFromSelection(shortcut);
                        } else {
                            shortcutsLayer.addToSelection(shortcut);
                        }
                    } else {
                        if (!shortcutsLayer.getSelectedShortcuts().contains(shortcut)) {
                            shortcutsLayer.clearSelection();
                            shortcutsLayer.addToSelection(shortcut);
                        }
                    }

                    // prepare drag
                    dragStart = e.getPoint();
                    initialPositions.clear();
                    for (DesktopShortcut s : shortcutsLayer.getSelectedShortcuts()) {
                        initialPositions.put(s, s.getLocation());
                    }
                    draggingIcons = true;
                } else {
                    // empty space → rubber band
                    shortcutsLayer.clearSelection();
                    bandStart = e.getPoint();
                    selectionRect = new Rectangle(bandStart);
                    draggingIcons = false;
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggingIcons && dragStart != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;

                    for (Map.Entry<DesktopShortcut, Point> entry : initialPositions.entrySet()) {
                        DesktopShortcut s = entry.getKey();
                        Point startPos = entry.getValue();
                        s.setLocation(startPos.x + dx, startPos.y + dy);
                    }
                } else if (bandStart != null) {
                    selectionRect.setBounds(
                        Math.min(bandStart.x, e.getX()),
                        Math.min(bandStart.y, e.getY()),
                        Math.abs(bandStart.x - e.getX()),
                        Math.abs(bandStart.y - e.getY())
                    );

                    for (DesktopShortcut s : shortcutsLayer.getShortcuts()) {
                        boolean inside = selectionRect.intersects(s.getBounds());
                        if (inside) {
                            shortcutsLayer.addToSelection(s);
                        } else {
                            shortcutsLayer.removeFromSelection(s);
                        }
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
                bandStart = null;
                initialPositions.clear();
                selectionRect = null;
                draggingIcons = false;
                repaint();
            }
        };

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    protected void paintComponent(Graphics g) {
        if (selectionRect != null) {
            g.setColor(new Color(0,  120,  215,  50));
            g.fillRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
            g.setColor(Color.BLUE);
            g.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
        }
    }
}