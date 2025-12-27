package jworkspace.ui.views;

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
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.hyperrealm.kiwi.ui.KPanel;

import jworkspace.ui.WorkspaceGUI;

/**
 * Header panel serves as information bridge for desktop navigation, displaying the name of
 * current desktop and current time.
 *
 * @author Anton Troshin
 */
public class HeaderPanel extends KPanel implements MouseListener, LayoutManager, MouseMotionListener {
    /**
     * Possible orientations of header panel.
     * HEADER - on the top of the frame.
     */
    private static final String HEADER = BorderLayout.NORTH;
    /**
     * Possible orientations of header panel.
     * FOOTER - on the bottom of the frame.
     */
    private static final String FOOTER = BorderLayout.SOUTH;
    /**
     * Header label is the only component to display info.
     */
    private final JLabel headerLabel = new JLabel();
    /**
     * Margins.
     */
    private final Insets margin = new Insets(0, 0, 0, 0);
    /**
     * Current orientation - by default HEADER.
     */
    private String orientation = FOOTER;
    /**
     * Header label is the only component to display info.
     */
    private final ClockLabel clockLabel = new ClockLabel();

    /**
     * HeaderPanel constructor. Takes title string
     * as argument.
     */
    public HeaderPanel(String title) {
        super();
        headerLabel.setIcon(new ImageIcon(WorkspaceGUI.getResourceManager().
            getImage("desktop/desktop.png")));
        setLayout(this);

        add(headerLabel);
        add(clockLabel);
        setHeaderLabelText(title);
        addMouseListener(this);
        addMouseMotionListener(this);
        setTimer();
    }

    /**
     * HeaderPanel constructor. Takes title string
     * and orientation as arguments.
     */
    public HeaderPanel(String title, String orientation) {
        super();

        if (!orientation.equals(HeaderPanel.HEADER)
            && !orientation.equals(HeaderPanel.FOOTER)) {

            throw new IllegalArgumentException("Orientation MUST be HEADER or FOOTER");
        }

        this.orientation = orientation;
        setLayout(this);

        add(headerLabel);
        add(clockLabel);

        setHeaderLabelText(title);
        addMouseListener(this);
        addMouseMotionListener(this);
        setTimer();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void setTimer() {
        DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("EEE dd MMM HH:mm:ss", Locale.ENGLISH);

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            clockLabel.setText(now.format(fmt));
            clockLabel.repaint();
        });
        timer.start();
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Returns orientation of header panel
     */
    String getOrientation() {
        return orientation;
    }

    /**
     * Sets header orientation
     */
    void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    @SuppressWarnings("MagicNumber")
    public void layoutContainer(Container parent) {
        headerLabel.setBounds(15, 0, getWidth() * 2 / 3, preferredLayoutSize(parent).height);
        clockLabel.setBounds(getWidth() - clockLabel.getPreferredSize().width - 15, 0, getWidth(),
            preferredLayoutSize(parent).height);
    }

    /**
     * Returns the minimum layout size of this component.
     */
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(margin.left + margin.right, clockLabel.getPreferredSize().height + 2);
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            Container parentContainer = getParent();

            if (orientation.equals(FOOTER)) {
                orientation = HEADER;
            } else if (orientation.equals(HEADER)) {
                orientation = FOOTER;
            }

            parentContainer.remove(this);
            parentContainer.add(this, orientation);
            parentContainer.validate();
            parentContainer.repaint();
        }
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

        firePropertyChange("MOUSE_DRAGGED",
            e.getPoint(), e.getPoint());
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Returns preferred layout size of this component.
     */
    public Dimension preferredLayoutSize(Container parent) {
        return minimumLayoutSize(parent);
    }

    public void removeLayoutComponent(Component comp) {}

    /**
     * Sets header text.
     */
    public void setHeaderLabelText(String labelText) {
        headerLabel.setText(labelText);
        validate();
        repaint();
    }

    /**
     * Clock
     */
    @SuppressWarnings("MagicNumber")
    static class ClockLabel extends JLabel {
        private ClockLabel() {
            super();
            setIcon(new ImageIcon(WorkspaceGUI.getResourceManager().getImage("clock.png")));
        }
    }
}