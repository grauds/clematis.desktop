package jworkspace.ui.desktop;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;

import jworkspace.ui.widgets.GlassOutlinePane;

/**
 * A GlassDragPane is a specialized JLayeredPane used to provide outlining and drag functionality
 * for selected desktop icons. It overlays an interactive glass pane containing outline panes for
 * each selected icon, allowing users to move the outlines in sync with the icons.
 * <p>
 * This class handles mouse events and dynamically creates outline panes for selected icons.
 * The outline panes are visual representations that are layered above the components and
 * mimic their positions and sizes.
 */
class GlassDragPane extends JLayeredPane {

    private final List<GlassOutlinePane> outlinePanes = new ArrayList<>();

    private final List<DesktopIcon> icons = new ArrayList<>();

    private boolean active = false;

    private Point start;

    GlassDragPane() {
        super();
        addMouseListener(new GlassMouseAdapter());
        addMouseMotionListener(new GlassMouseMotionAdapter());
        setOpaque(false);
    }

    void moveOutlinePanesTo(int x, int y) {
        int counter = 0;
        for (DesktopIcon icon : icons) {
            if (icon.isSelected()) {
                GlassOutlinePane outlinePane = outlinePanes.get(counter);
                Point location = icon.getLocation();
                outlinePane.setLocation(location.x + x, location.y + y);
                counter++;
            }
        }
    }

    void activate(Point start, List<DesktopIcon> icons) {
        /*
         * Add a drag pane for each selected icon.
         */
        for (int i = 0; i < icons.size(); i++) {
            DesktopIcon icon = icons.get(i);
            if (icon.isSelected()) {
                GlassOutlinePane outlinePane = new GlassOutlinePane();

                this.setLayer(outlinePane, Integer.MAX_VALUE - i);
                outlinePane.setBounds(icon.getX(), icon.getY(), icon.getWidth(), icon.getHeight());
                add(outlinePane);

                this.outlinePanes.add(outlinePane);
                this.icons.add(icon);
            }
        }
        this.start = start;
        this.active = true;
        setVisible(true);
    }

    void deactivate() {
        this.active = false;
        setVisible(false);
        this.outlinePanes.clear();
        this.icons.clear();
    }

    /**
     * Determines if a specified point (x, y) is contained within the GlassDragPane.
     * This method checks if the pane is currently active and delegates the containment
     * check to the superclass if it is active.
     *
     * @param x the x-coordinate of the point to check
     * @param y the y-coordinate of the point to check
     * @return true if the point is within the bounds of the GlassDragPane and the pane is active,
     *         otherwise false
     */
    @Override
    public boolean contains(int x, int y) {
        return active && super.contains(x, y);
    }

    class GlassMouseAdapter extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            System.out.println("Glass mouseReleased: " + e.getX() + " " + e.getY());
            if (active) {
                Point finish = e.getLocationOnScreen();
                for (DesktopIcon icon : icons) {
                    icon.setLocation(finish.x - start.x, finish.y - start.y);
                }
                deactivate();
            }
        }

    }

    class GlassMouseMotionAdapter extends MouseMotionAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            System.out.println("Glass mouseDragged: " + e.getX() + " " + e.getY());
            if (active) {
                moveOutlinePanesTo(e.getX() - start.x, e.getY() - start.y);
            }
        }
    }

}
