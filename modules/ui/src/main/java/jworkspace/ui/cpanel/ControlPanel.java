package jworkspace.ui.cpanel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Action;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.hyperrealm.kiwi.ui.KPanel;

import jworkspace.ui.api.cpanel.CButton;
import lombok.Getter;
import lombok.Setter;

/**
 * System control panel
 *
 * @author Anton Troshin
 */
@Getter
@Setter
public class ControlPanel extends KPanel implements LayoutManager, MouseListener, MouseMotionListener, ActionListener {
    public static final String ORIENTATION_PROPERTY = "ORIENTATION";
    public static final String DRAGGING_PROPERTY = "DRAGGING";
    public static final String DRAGGED_PROPERTY = "DRAGGED";
    /**
     * Specifies that components should be laid out left to right.
     */
    static final int X_AXIS = 0;
    /**
     * Specifies that components should be laid out top to bottom.
     */
    static final int Y_AXIS = 1;
    /**
     * Orientation of the control bar relative to the parent component.
     */
    private int orientation;

    private CScrollButton upButton = null;

    private CScrollButton downButton = null;

    private final JViewport viewport = new JViewport();

    private final CButtonsPane buttonsPane;
    /**
     * Mouse pointer coordinate.
     */
    private Point pointer = new Point();
    /**
     * Is this control being dragged?
     */
    private boolean dragged = false;

    public ControlPanel() {
        this(ControlPanel.Y_AXIS);
    }

    private ControlPanel(int orientation) {
        super();
        this.orientation = orientation;

        buttonsPane = new CButtonsPane(orientation);
        addPropertyChangeListener(buttonsPane);

        createScrollButtons(this);

        firePropertyChange(ORIENTATION_PROPERTY, -1, orientation);
        addMouseListener(this);
        addMouseMotionListener(this);
        viewport.setOpaque(false);
    }

    /**
     * Handles action events triggered by user interactions. Depending on the action command,
     * the viewport's visible rectangle is scrolled either horizontally or vertically. For the
     * "INCREMENT" command, the viewport moves in one direction, and for other commands, in the
     * opposite direction. The movement is limited by the bounds of the buttons pane.
     *
     * @param e the ActionEvent containing details about the action command and its source
     */
    public void actionPerformed(ActionEvent e) {

        /*
         * Increment scrolls down and right.
         */
        int direction = 1;
        if (e.getActionCommand().equals("INCREMENT")) {
            direction = -1;
        }
        /*
         * The visible rectangle of viewport is a kind of window
         * through which subcomponents can be seen.
         */
        Rectangle visRect = viewport.getViewRect();
        /*
         * The actual size of buttons pane.
         */
        Dimension vSize = buttonsPane.getPreferredPanelSize();

        int amount;

        if (orientation == ControlPanel.X_AXIS) {
            amount = buttonsPane.getScrollableBlockIncrement(visRect, SwingConstants.HORIZONTAL, direction);
            visRect.x += direction * amount;
            if ((vSize.width - visRect.x) < visRect.width) {
                /*
                 * The top most position. |.. visRect.x ..|..... visRect.width .......|
                 *                        |................vSize.width................|
                 */
                visRect.x = Math.max(0, vSize.width - visRect.width);
            } else if (visRect.x < 0) {
                /*
                 * The bottom most position. |..... visRect.width .......|.. visRect.x ..|
                 *                             	 |................vSize.width................|
                 */
                visRect.x = 0;
            }
        } else if (orientation == ControlPanel.Y_AXIS) {
            amount = buttonsPane.getScrollableBlockIncrement(visRect, SwingConstants.VERTICAL, direction);
            visRect.y += direction * amount;
            if ((vSize.height - visRect.y) < visRect.height) {
                /*
                 * The top most position. |.. visRect.y ..|..... visRect.height .......|
                 *                        |................vSize.height................|
                 */
                visRect.y = Math.max(0, vSize.height - visRect.height);
            } else if (visRect.y < 0) {
                /*
                 * The bottom most position. |..... visRect.height .......|.. visRect.y ..|
                 *                             	 |................vSize.height................|
                 */
                visRect.y = 0;
            }
        }

        viewport.setViewPosition(visRect.getLocation());
    }

    /**
     * Adds a control button to the button's pane layout and internal buttons collection.
     */
    public void addButton(CButton button) {
        buttonsPane.addButton(button);
    }

    /**
     * Add action
     */
    public void addButton(Action a) {
        buttonsPane.addButton(a);
    }

    /**
     * Delegates adding of buttons and separators to the subcomponent of the control panel.
     */
    public void addLayoutComponent(String name, Component comp) {
        buttonsPane.add(comp);
    }

    /**
     * Creates a control panel separator from parameters and adds
     * it to the button's pane layout and internal buttons collection.
     */
    public void addSeparator() {
        buttonsPane.addSeparator();
    }

    /**
     * Adjusts scroll buttons after the pane is dragged. Also assembles the buttons pane
     * if the pane is being created.
     */
    private void createScrollButtons(ActionListener listener) {
        if (downButton == null || upButton == null) {
            setLayout(this);

            downButton = new CScrollButton(listener, false);
            upButton = new CScrollButton(listener, true);

            downButton.addActionListener(this);
            upButton.addActionListener(this);

            add(upButton);
            add(downButton);
            add(viewport);

            addPropertyChangeListener(upButton);
            addPropertyChangeListener(downButton);

            viewport.add(buttonsPane);
        }
    }

    /**
     * Sets orientation of this component.
     */
    public void setOrientation(int orientation) {
        if (orientation != ControlPanel.X_AXIS
            && orientation != ControlPanel.Y_AXIS) {
            return;
        }
        firePropertyChange(ORIENTATION_PROPERTY, this.orientation, orientation);
        this.orientation = orientation;
    }

    /**
     * Sets orientation of this component.
     */
    public void setOrientation(String orientation) {
        if (orientation.equals(BorderLayout.NORTH) || orientation.equals(BorderLayout.SOUTH)) {
            setOrientation(ControlPanel.X_AXIS);
        } else if (orientation.equals(BorderLayout.EAST) || orientation.equals(BorderLayout.WEST)) {
            setOrientation(ControlPanel.Y_AXIS);
        }

    }

    /**
     * Layouts control panel with scroll buttons and
     * button's pane.
     */
    @SuppressWarnings("MagicNumber")
    public void layoutContainer(Container parent) {
        if (orientation == ControlPanel.Y_AXIS) {
            upButton.setBounds(0, 0, getWidth(),
                upButton.getPreferredSize().height);
            downButton.setBounds(0, getHeight() - downButton.getPreferredSize().height,
                getWidth(),
                downButton.getPreferredSize().height);
            viewport.reshape(0, upButton.getPreferredSize().height + 3,
                getWidth(), getHeight() - downButton.getPreferredSize().height
                    - upButton.getPreferredSize().height - 6);
        } else if (orientation == ControlPanel.X_AXIS) {
            upButton.setBounds(0, 0, upButton.getPreferredSize().width, getHeight());
            downButton.setBounds(getWidth() - downButton.getPreferredSize().width,
                0, downButton.getPreferredSize().width,
                getHeight());
            viewport.reshape(upButton.getPreferredSize().width + 3,
                0, getWidth() - downButton.getPreferredSize().width
                    - upButton.getPreferredSize().width - 6,
                getHeight());
        }
    }

    /**
     * Calculates the minimum size dimensions for the specified
     * panel given the components in the specified parent container.
     *
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(40, 40);
    }

    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Note: this method sends property change event
     * with new point, there mouse pointer was dragged
     * relative to Control panel. Property event handler
     * should always account for it with top left coordinates
     * of control panel.
     */
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }

        Point old = new Point(pointer);
        pointer = new Point(e.getPoint());

        firePropertyChange(DRAGGED_PROPERTY, old, pointer);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public boolean isFocusCycleRoot() {
        return false;
    }

    public void mouseMoved(MouseEvent e) {
    }

    /**
     * The mouse-pressed event causes generic property of control bar to change. Control bar sends only NEW
     * value, as we don't use old mouse pointer coordinate while dragging.
     */
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        dragged = true;
        firePropertyChange(DRAGGING_PROPERTY, false, true);
        e.consume();
    }

    /**
     * Mouse released message handler. By this event the control panel sends a property change event
     */
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        dragged = false;
        firePropertyChange(DRAGGING_PROPERTY, true, false);
        viewport.setViewPosition(new Point(0, 0));
        e.consume();
    }

    /**
     * Calculates the preferred size dimensions for the specified
     * panel given the components in the specified parent container.
     *
     * @param parent the component to be laid out
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
        return minimumLayoutSize(parent);
    }

    /**
     * Delegates removing of buttons and separators to the button pane of the control panel.
     */
    public void removeLayoutComponent(Component comp) {
        buttonsPane.remove(comp);
    }
}