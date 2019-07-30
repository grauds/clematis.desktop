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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.plaf.metal.MetalButtonUI;

import com.hyperrealm.kiwi.ui.KPanel;

import jworkspace.ui.Utils;

/**
 * Class <code>jworkspace.ui.cpanel.CButtonsPane</code> is an invisible layer that actually deals with buttons and
 * performs scrolling.
 *
 * @author Anton Troshin
 */
class CButtonsPane extends KPanel implements Scrollable, PropertyChangeListener {
    /**
     * Zero insets
     */
    private static final Insets ZERO_INSETS = new Insets(0, 0, 0, 0);
    /**
     * Is rollover property.
     */
    private static final String IS_ROLLOVER = "CButtonsPane.isRollover";
    /**
     * Orientation property
     */
    private static final String ORIENTATION_PROPERTY = "ORIENTATION";
    /**
     * Metal borders
     */
    private static Border metalRolloverBorder = new CompoundBorder(
        new MetalBorders.RolloverButtonBorder(), new BasicBorders.MarginBorder());
    /**
     * Metal non rollover borders
     */
    private static Border metalNonRolloverBorder = new CompoundBorder(
        new MetalBorders.ButtonBorder(), new BasicBorders.MarginBorder());
    /**
     * Basic borders
     */
    private static Border basicRolloverBorder = new CompoundBorder(
        BorderFactory.createBevelBorder(BevelBorder.RAISED), new BasicBorders.MarginBorder());
    /**
     * Basic non rollover borders
     */
    @SuppressWarnings("MagicNumber")
    private static Border basicNonRolloverBorder = new CompoundBorder(
        BorderFactory.createEmptyBorder(3, 3, 3, 3), new BasicBorders.MarginBorder());
    /**
     * Orientation of control bar relative to
     * parent component.
     */
    private int orientation;
    /**
     * The table of borders.
     */
    private Map<JButton, Border> borderTable = new HashMap<>();
    /**
     * The table of margins
     */
    private Map<JButton, Insets> marginTable = new HashMap<>();
    /**
     * Initial state of buttons.
     */
    private boolean rolloverBorders = true;

    /**
     * ControlPanel constructor.
     */
    CButtonsPane(int orientation) {
        super();
        this.orientation = orientation;
        /*
         * Set default layout
         */
        setLayout();
        //        setBorder(new EmptyBorder(0, 3, 0, 0));
        /*
         * Listener for rollover property change
         */
        PropertyChangeListener rolloverListener = createRolloverListener();
        addPropertyChangeListener(rolloverListener);
        updateUI();
    }

    /**
     * New metal rollover listener.
     */
    private PropertyChangeListener createRolloverListener() {
        return new MetalRolloverListener();
    }

    /**
     * New container listener
     */
    protected ContainerListener createContainerListener() {
        return new MetalContainerListener();
    }

    /**
     * Adds action to this buttons pane layout
     */
    CButton addButton(Action a) {
        CButton button = Utils.createCButtonFromAction(a);
        add(button);
        return button;
    }

    /**
     * Creates control panel button from parameters and
     * add it to the button's pane layout and internal buttons
     * vector.
     */
    CButton addButton(ActionListener listener,
                      ImageIcon image, ImageIcon hoverImage,
                      String command, String toolTipText) {
        CButton button = CButton.create(listener,
            image, hoverImage,
            command, toolTipText);
        add(button);
        return button;
    }

    /**
     * Adds control button to the button's pane layout and internal buttons
     * vector.
     */
    void addButton(CButton button) {
        add(button);
    }

    public void addSeparator() {
        CSeparator sep = new CSeparator(orientation);
        addPropertyChangeListener(sep);
        add(sep);
    }

    /**
     * Returns the preferred size of the viewport for a view component.
     * For example the preferredSize of a JList component is the size
     * required to acommodate all of the cells in its list however the
     * value of preferredScrollableViewportSize is the size required for
     * JList.getVisibleRowCount() rows.   A component without any properties
     * that would effect the viewport size should just return
     * getPreferredSize() here.
     *
     * @return The preferredSize of a JViewport whose view is this Scrollable.
     * @see JViewport#getPreferredSize
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredPanelSize();
    }

    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one block
     * of rows or columns, depending on the value of orientation.
     * <p>
     * Scrolling containers, like JScrollPane, will use this method
     * each time the user requests a block scroll.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction   Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction.
     * @see JScrollBar#setBlockIncrement
     */
    @SuppressWarnings("MagicNumber")
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        return 10;
    }

    Dimension getPreferredPanelSize() {
        return getPreferredSize();
    }

    /**
     * Forbids vertical scrolling if bar is horizontally
     * oriented.
     */
    public boolean getScrollableTracksViewportHeight() {
        if (orientation == ControlPanel.Y_AXIS) {
            return getParent().getSize().height > getPreferredSize().height;
        } else {
            return true;
        }
    }

    /**
     * Forbids horizontal scrolling if bar is vertically
     * oriented.
     */
    public boolean getScrollableTracksViewportWidth() {
        if (orientation == ControlPanel.Y_AXIS) {
            return true;
        } else {
            return getParent().getSize().width > getPreferredSize().width;
        }
    }

    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one new row
     * or column, depending on the value of orientation.  Ideally,
     * components should handle a partially exposed row or column by
     * returning the distance required to completely expose the item.
     * <p>
     * Scrolling containers, like JScrollPane, will use this method
     * each time the user requests a unit scroll.
     *
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction   Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction
     * @see JScrollBar#setUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return getScrollableBlockIncrement(visibleRect, orientation, direction);
    }

    /**
     * Each time control panel will change
     * its orientation property, this will
     * handle incoming event.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof Integer
            && evt.getPropertyName().equals(ORIENTATION_PROPERTY)) {

            firePropertyChange(ORIENTATION_PROPERTY, orientation, ((Integer) evt.getNewValue()).intValue());
            orientation = (Integer) evt.getNewValue();
            setLayout();
        }
    }

    protected void setLayout() {
        BoxLayout layout;
        if (orientation == ControlPanel.X_AXIS) {
            layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        } else {
            layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        }
        setLayout(layout);
    }

    /**
     * Rollover borders support
     */
    private boolean isRolloverBorders() {
        return rolloverBorders;
    }

    /**
     * Sets rollover borders
     */
    private void setRolloverBorders(boolean rollover) {
        rolloverBorders = rollover;

        if (rolloverBorders) {
            installRolloverBorders(this);
        } else {
            installNonRolloverBorders(this);
        }
    }

    /**
     * Update UI with new borders
     */
    public void updateUI() {
        super.updateUI();
        SwingUtilities.invokeLater(() -> setRolloverBorders(isRolloverBorders()));
    }

    /**
     * Installs rollover borders
     */
    private void installRolloverBorders(JComponent c) {
        // Put rollover borders on buttons
        Component[] components = c.getComponents();

        for (Component component : components) {
            if (component instanceof JComponent) {
                ((JComponent) component).updateUI();

                setBorderToRollover(component);
            }
        }
    }

    /**
     * Installs non rollover borders
     */
    private void installNonRolloverBorders(JComponent c) {
        // Put non rollover borders on buttons
        Component[] components = c.getComponents();

        for (Component component : components) {
            if (component instanceof JComponent) {
                ((JComponent) component).updateUI();

                setBorderToNonRollover(component);
            }
        }
    }

    /**
     * Installs normal borders
     */
    protected void installNormalBorders(JComponent c) {
        // Put back the normal borders on buttons
        Component[] components = c.getComponents();

        for (Component component : components) {
            setBorderToNormal(component);
        }
    }

    /**
     * Sets rollover borders.
     */
    private void setBorderToRollover(Component c) {
        if (c instanceof JButton) {
            JButton b = (JButton) c;

            if (b.getUI() instanceof MetalButtonUI) {
                swapBorders(b, metalNonRolloverBorder, metalRolloverBorder);
            } else if (b.getUI() instanceof BasicButtonUI) {
                swapBorders(b, basicNonRolloverBorder, basicRolloverBorder);
            }
        }
    }

    /**
     * Sets borders to non rollover
     */
    private void setBorderToNonRollover(Component c) {
        if (c instanceof JButton) {
            JButton b = (JButton) c;

            if (b.getUI() instanceof MetalButtonUI) {
                swapBorders(b, metalRolloverBorder, metalNonRolloverBorder, false);
            } else if (b.getUI() instanceof BasicButtonUI) {
                swapBorders(b, basicRolloverBorder, basicNonRolloverBorder, false);
            }
        }
    }

    private void swapBorders(JButton b, Border oldBorder, Border newBorder, boolean rolloverEnabled) {

        if (b.getBorder() instanceof UIResource) {
            borderTable.put(b, b.getBorder());
        }

        if (b.getBorder() == oldBorder) {
            b.setBorder(newBorder);
        }

        if (b.getMargin() == null || b.getMargin() instanceof UIResource) {
            marginTable.put(b, b.getMargin());
            b.setMargin(ZERO_INSETS);
        }

        b.setRolloverEnabled(rolloverEnabled);
    }

    private void swapBorders(JButton b, Border nonRolloverBorder, Border rolloverBorder) {
        swapBorders(b, nonRolloverBorder, rolloverBorder, true);
    }

    private void setBorderToNormal(Component c) {
        if (c instanceof JButton) {
            JButton b = (JButton) c;
            if (b.getBorder() == metalRolloverBorder || b.getBorder() == metalNonRolloverBorder
                || b.getBorder() == basicRolloverBorder || b.getBorder() == basicNonRolloverBorder) {
                b.setBorder(borderTable.remove(b));
            }

            if (b.getMargin() == ZERO_INSETS) {
                b.setMargin(marginTable.remove(b));
            }

            b.setRolloverEnabled(false);
        }
    }

    /**
     * Metal rollover listener
     */
    protected class MetalRolloverListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();

            if (name.equals(IS_ROLLOVER)) {
                if (e.getNewValue() != null) {
                    setRolloverBorders((Boolean) e.getNewValue());
                } else {
                    setRolloverBorders(false);
                }
            }
        }
    }

    protected class MetalContainerListener implements ContainerListener {
        public void componentAdded(ContainerEvent e) {
            Component c = e.getChild();
            if (rolloverBorders) {
                setBorderToRollover(c);
            } else {
                setBorderToNonRollover(c);
            }
        }

        public void componentRemoved(ContainerEvent e) {
            Component c = e.getChild();
            setBorderToNormal(c);
        }
    }

}