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

import com.hyperrealm.kiwi.ui.KPanel;
import jworkspace.util.WorkspaceUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.MetalBorders;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
/**
 *  Class <code>jworkspace.ui.cpanel.CButtonsPane</code>
 *  is an invisible layer that actually deals with buttons and
 *  performs scrolling.
 */
class CButtonsPane extends KPanel implements Scrollable, PropertyChangeListener
{
    /**
     * Orientation of control bar relative to
     * parent component.
     */
    private int orientation = ControlPanel.Y_AXIS;
    /**
     * Listener for rollover property change
     */
    protected PropertyChangeListener rolloverListener;
    /**
     * Container listener
     */
    protected ContainerListener contListener;
    /**
     * The table of borders.
     */
    private Hashtable borderTable = new Hashtable();
    /**
     * The table of margins
     */
    private Hashtable marginTable = new Hashtable();
    /**
     * Initial state of buttons.
     */
    private boolean rolloverBorders = true;
    /**
     * Is rollover property.
     */
    private static String IS_ROLLOVER = "CButtonsPane.isRollover";
    /**
     * Metal borders
     */
    private static Border metalRolloverBorder = new CompoundBorder(
            new MetalBorders.RolloverButtonBorder(), new BasicBorders.MarginBorder());

    private static Border metalNonRolloverBorder = new CompoundBorder(
            new MetalBorders.ButtonBorder(), new BasicBorders.MarginBorder());
    /**
     * Basic borders
     */
    private static Border basicRolloverBorder = new CompoundBorder(
            BorderFactory.createBevelBorder(BevelBorder.RAISED), new BasicBorders.MarginBorder());

    private static Border basicNonRolloverBorder = new CompoundBorder(
            BorderFactory.createEmptyBorder(3, 3, 3, 3), new BasicBorders.MarginBorder());

    private final static Insets insets0 = new Insets(0, 0, 0, 0);

    /**
     * Metal rollover listener
     */
    protected class MetalRolloverListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String name = e.getPropertyName();

            if (name.equals(IS_ROLLOVER))
            {
                if (e.getNewValue() != null)
                {
                    setRolloverBorders(((Boolean) e.getNewValue()).booleanValue());
                }
                else
                {
                    setRolloverBorders(false);
                }
            }
        }
    }

    protected class MetalContainerListener implements ContainerListener
    {
        public void componentAdded(ContainerEvent e)
        {
            Component c = e.getChild();
            if (rolloverBorders)
            {
                setBorderToRollover(c);
            }
            else
            {
                setBorderToNonRollover(c);
            }
        }

        public void componentRemoved(ContainerEvent e)
        {
            Component c = e.getChild();
            setBorderToNormal(c);
        }
    }
    /**
     * ControlPanel constructor.
     */
    public CButtonsPane(int orientation)
    {
        super();
        this.orientation = orientation;
        /**
         * Set default layout
         */
        setLayout();
        //        setBorder(new EmptyBorder(0, 3, 0, 0));
        /**
         * Create rollover listener
         */
        rolloverListener = createRolloverListener();
        if (rolloverListener != null)
        {
            addPropertyChangeListener(rolloverListener);
        }
        updateUI();
    }

    /**
     * New metal rollover listener.
     */
    protected PropertyChangeListener createRolloverListener()
    {
        return new MetalRolloverListener();
    }

    /**
     * New container listener
     */
    protected ContainerListener createContainerListener()
    {
        return new MetalContainerListener();
    }

    /**
     * Adds action to this buttons pane layout
     */
    public CButton addButton(Action a)
    {
        CButton button = WorkspaceUtils.createCButtonFromAction(a);
        add(button);
        return button;
    }

    /**
     * Creates control panel button from parameters and
     * add it to the button's pane layout and internal buttons
     * vector.
     */
    public CButton addButton(ActionListener listener,
                             ImageIcon image, ImageIcon hoverImage,
                             String command, String toolTipText)
    {
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
    public void addButton(CButton button)
    {
        add(button);
    }

    public void addSeparator()
    {
       CSeparator sep = new CSeparator(orientation);
       addPropertyChangeListener( sep );
       add( sep );
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
    public Dimension getPreferredScrollableViewportSize()
    {
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
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction.
     * @see JScrollBar#setBlockIncrement
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction)
    {
        return 10;//for testing purposes
    }

    protected Dimension getPreferredPanelSize()
    {
        return getPreferredSize();
    }

    /**
     * Forbids vertical scrolling if bar is horizontally
     * oriented.
     */
    public boolean getScrollableTracksViewportHeight()
    {
        if ( orientation == ControlPanel.Y_AXIS )
        {
            if (getParent().getSize().height > getPreferredSize().height)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Forbids horizontal scrolling if bar is vertically
     * oriented.
     */
    public boolean getScrollableTracksViewportWidth()
    {
        if ( orientation == ControlPanel.Y_AXIS )
        {
            return true;
        }
        else
        {
            if (getParent().getSize().width > getPreferredSize().width)
            {
                return true;
            }
            else
            {
                return false;
            }
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
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction
     * @see JScrollBar#setUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        return getScrollableBlockIncrement(visibleRect, orientation, direction);
    }

    /**
     * Each time control panel will change
     * its orientation property, this will
     * handle incoming event.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt)
    {
        if (evt.getNewValue() instanceof Integer &&
                evt.getPropertyName().equals("ORIENTATION"))
        {
            firePropertyChange("ORIENTATION", orientation,
                              ((Integer) evt.getNewValue()).intValue());

            orientation = ((Integer) evt.getNewValue()).intValue();
            setLayout();
        }
    }

    protected void setLayout()
    {
        BoxLayout layout = null;
        if ( orientation == ControlPanel.X_AXIS )
        {
          layout =  new BoxLayout( this, BoxLayout.LINE_AXIS );
        }
        else
        {
          layout =  new BoxLayout( this, BoxLayout.PAGE_AXIS );
        }
        setLayout( layout );
    }
    /**
     * Rollover borders support
     */
    public boolean isRolloverBorders()
    {
        return rolloverBorders;
    }

    /**
     * Update UI with new borders
     */
    public void updateUI()
    {
        super.updateUI();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                setRolloverBorders(isRolloverBorders());
            }
        });
    }

    /**
     * Sets rollover borders
     */
    public void setRolloverBorders(boolean rollover)
    {
        rolloverBorders = rollover;

        if (rolloverBorders)
        {
            installRolloverBorders(this);
        }
        else
        {
            installNonRolloverBorders(this);
        }
    }

    /**
     * Installs rollover borders
     */
    protected void installRolloverBorders(JComponent c)
    {
        // Put rollover borders on buttons
        Component[] components = c.getComponents();

        for (int i = 0; i < components.length; ++i)
        {
            if (components[i] instanceof JComponent)
            {
                ((JComponent) components[i]).updateUI();

                setBorderToRollover(components[i]);
            }
        }
    }

    /**
     * Installs non rollover borders
     */
    protected void installNonRolloverBorders(JComponent c)
    {
        // Put nonrollover borders on buttons
        Component[] components = c.getComponents();

        for (int i = 0; i < components.length; ++i)
        {
            if (components[i] instanceof JComponent)
            {
                ((JComponent) components[i]).updateUI();

                setBorderToNonRollover(components[i]);
            }
        }
    }

    /**
     * Installs normal borders
     */
    protected void installNormalBorders(JComponent c)
    {
        // Put back the normal borders on buttons
        Component[] components = c.getComponents();

        for (int i = 0; i < components.length; ++i)
        {
            setBorderToNormal(components[i]);
        }
    }

    /**
     * Sets rollover borders.
     */
    protected void setBorderToRollover(Component c)
    {
        if (c instanceof JButton)
        {
            JButton b = (JButton) c;

            if (b.getUI() instanceof MetalButtonUI)
            {
                if (b.getBorder() instanceof UIResource)
                {
                    borderTable.put(b, b.getBorder());
                }

                if (//b.getBorder() instanceof UIResource ||
                        b.getBorder() == metalNonRolloverBorder)
                {
                    b.setBorder(metalRolloverBorder);
                }

                if (b.getMargin() == null ||
                        b.getMargin() instanceof UIResource)
                {
                    marginTable.put(b, b.getMargin());
                    b.setMargin(insets0);
                }
                b.setRolloverEnabled(true);
            }
            else if (b.getUI() instanceof BasicButtonUI)
            {
                if (b.getBorder() instanceof UIResource)
                {
                    borderTable.put(b, b.getBorder());
                }

                if (//b.getBorder() instanceof UIResource ||
                        b.getBorder() == basicNonRolloverBorder)
                {
                    b.setBorder(basicRolloverBorder);
                }

                if (b.getMargin() == null ||
                        b.getMargin() instanceof UIResource)
                {
                    marginTable.put(b, b.getMargin());
                    b.setMargin(insets0);
                }
                b.setRolloverEnabled(true);
            }
        }
    }

    /**
     * Sets borders to non rollover
     */
    protected void setBorderToNonRollover(Component c)
    {
        if (c instanceof JButton)
        {
            JButton b = (JButton) c;

            if (b.getUI() instanceof MetalButtonUI)
            {
                if (b.getBorder() instanceof UIResource)
                {
                    borderTable.put(b, b.getBorder());
                }

                if (//b.getBorder() instanceof UIResource ||
                        b.getBorder() == metalRolloverBorder)
                {
                    b.setBorder(metalNonRolloverBorder);
                }

                if (b.getMargin() == null ||
                        b.getMargin() instanceof UIResource)
                {
                    marginTable.put(b, b.getMargin());
                    b.setMargin(insets0);
                }

                b.setRolloverEnabled(false);
            }
            else if (b.getUI() instanceof BasicButtonUI)
            {
                if (b.getBorder() instanceof UIResource)
                {
                    borderTable.put(b, b.getBorder());
                }

                if (//b.getBorder() instanceof UIResource ||
                        b.getBorder() == basicRolloverBorder)
                {
                    b.setBorder(basicNonRolloverBorder);
                }

                if (b.getMargin() == null || b.getMargin() instanceof UIResource)
                {
                    marginTable.put(b, b.getMargin());
                    b.setMargin(insets0);
                }

                b.setRolloverEnabled(false);
            }
        }
    }

    protected void setBorderToNormal(Component c)
    {
        if (c instanceof JButton)
        {
            JButton b = (JButton) c;

            if (b.getUI() instanceof MetalButtonUI)
            {
                if (b.getBorder() == metalRolloverBorder ||
                        b.getBorder() == metalNonRolloverBorder)
                {
                    b.setBorder((Border) borderTable.remove(b));
                }

                if (b.getMargin() == insets0)
                {
                    b.setMargin((Insets) marginTable.remove(b));
                }

                b.setRolloverEnabled(false);
            }
            else if (b.getUI() instanceof BasicButtonUI)
            {
                if (b.getBorder() == basicRolloverBorder
                        || b.getBorder() == basicNonRolloverBorder)
                {
                    b.setBorder((Border) borderTable.remove(b));
                }
                if (b.getMargin() == insets0)
                {
                    b.setMargin((Insets) marginTable.remove(b));
                }
                b.setRolloverEnabled(false);
            }
        }
    }

}