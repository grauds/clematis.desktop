package jworkspace.ui.desktop.neu;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

import jworkspace.ui.desktop.plaf.DesktopInteractionLayer;
import jworkspace.ui.desktop.plaf.DesktopShortcutsLayer;
import lombok.Getter;

/**
 * The ClematisDesktopPaneUI class is a custom UI delegate for JDesktopPane
 * that extends BasicDesktopPaneUI. It provides additional functionality
 * for managing desktop interactions and shortcut handling, enhancing the
 * default desktop pane experience.
 * <p>
 * This class introduces the following components:
 * <p>
 * - DesktopShortcutsLayer: A layer added to the desktop pane for rendering
 *   and managing shortcuts as interactable components, allowing selection
 *   and arrangement of desktop-like items.
 * <p>
 * - DesktopInteractionLayer: A layer on top of the shortcut layer that
 *   handles user interactions such as dragging icons, drawing rubber band
 *   selection rectangles, and selecting/deselecting shortcuts.
 * <p>
 * Key installation behavior:
 * - Installs the shortcut and interaction layers on top of the desktop pane.
 * - Ensures the layers are resized and aligned with the desktop pane's
 *   dimensions whenever the pane is resized.
 * - Properly integrates the layers into the component hierarchy and configures
 *   their bounds.
 * <p>
 * Override Notes:
 * - The installUI method is overridden to add the shortcut and interaction
 *   layers to the desktop pane during UI installation and to set up the required
 *   component listeners for resizing behavior.
 */
public class ClematisDesktopPaneUI extends BasicDesktopPaneUI {

    @Getter
    private DesktopShortcutsLayer shortcutsLayer;

    @Getter
    private DesktopInteractionLayer interactionLayer;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);

        shortcutsLayer = new DesktopShortcutsLayer();
        desktop.add(shortcutsLayer, JLayeredPane.DEFAULT_LAYER);
        shortcutsLayer.setBounds(0, 0, desktop.getWidth(), desktop.getHeight());

        interactionLayer = new DesktopInteractionLayer(shortcutsLayer, null);
        interactionLayer.setBounds(0, 0, desktop.getWidth(), desktop.getHeight());
        desktop.add(interactionLayer, JLayeredPane.DRAG_LAYER);

        // Keep it sized with the desktop
        desktop.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                shortcutsLayer.setBounds(0, 0, desktop.getWidth(), desktop.getHeight());
                interactionLayer.setBounds(0, 0, desktop.getWidth(), desktop.getHeight());
            }
        });
    }

}
