package jworkspace.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LayeredDesktop extends JDesktopPane {
    public static final Integer ICON_LAYER = 0;               // bottom
    public static final Integer WINDOW_LAYER = DEFAULT_LAYER; // middle
    public static final Integer DRAG_LAYER = 400;             // top

    private final JPanel iconLayerPanel;
    private final JPanel dragLayerPanel;
    private final List<JComponent> desktopIcons = new ArrayList<>();

    // === Dragging rectangle state ===
    private boolean dragging = false;
    private Point dragStart;
    private Rectangle dragRect;

    public LayeredDesktop() {
        setLayout(null);

        // Panel for desktop icons
        iconLayerPanel = new JPanel(null);
        iconLayerPanel.setOpaque(false);
        add(iconLayerPanel, ICON_LAYER);

        // Panel for drag overlay
        dragLayerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (dragging && dragRect != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0, 120, 215, 60)); // translucent fill
                    g2.fill(dragRect);
                    g2.setColor(new Color(0, 120, 215));     // border
                    g2.draw(dragRect);
                    g2.dispose();
                }
            }
        };
        dragLayerPanel.setOpaque(false);
        add(dragLayerPanel, DRAG_LAYER);

        // Resize panels with desktop
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeLayers();
            }
        });
        resizeLayers();

        // Install drag handler
        DragHandler handler = new DragHandler();
        dragLayerPanel.addMouseListener(handler);
        dragLayerPanel.addMouseMotionListener(handler);
    }

    private void resizeLayers() {
        iconLayerPanel.setBounds(0, 0, getWidth(), getHeight());
        dragLayerPanel.setBounds(0, 0, getWidth(), getHeight());
    }

    // === Desktop icon API ===
    public void addDesktopIcon(JComponent icon) {
        desktopIcons.add(icon);
        iconLayerPanel.add(icon);
        iconLayerPanel.revalidate();
        iconLayerPanel.repaint();
    }

    private class DragHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            dragging = true;
            dragStart = e.getPoint();
            dragRect = new Rectangle(dragStart);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragging) {
                int x = Math.min(dragStart.x, e.getX());
                int y = Math.min(dragStart.y, e.getY());
                int w = Math.abs(e.getX() - dragStart.x);
                int h = Math.abs(e.getY() - dragStart.y);
                dragRect.setBounds(x, y, w, h);
                dragLayerPanel.repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragging) {
                dragging = false;
                dragLayerPanel.repaint();
                selectIconsInside(dragRect);
                dragRect = null;
            }
        }
    }

    // === Selection logic ===
    private void selectIconsInside(Rectangle selectionRect) {
        for (JComponent icon : desktopIcons) {
            Rectangle bounds = SwingUtilities.convertRectangle(
                icon.getParent(), icon.getBounds(), dragLayerPanel);

            boolean selected = selectionRect.intersects(bounds);

            if (icon instanceof AbstractButton btn) {
                btn.setSelected(selected);
            }

            // For demo: change background to show selection
            if (selected) {
                icon.setBackground(new Color(0, 120, 215, 100));
                icon.setOpaque(true);
            } else {
                icon.setOpaque(false);
            }
            icon.repaint();
        }
    }
}
