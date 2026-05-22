package jworkspace.ui.api.util;
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

import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import jworkspace.ui.api.action.ActionChangedListener;
import jworkspace.ui.api.cpanel.CButton;

public class ActionUtils {

    public static final String MENU_ICON = "MENU_ICON";

    private ActionUtils() {}

    /**
     * Create a button from action
     */
    public static CButton createCButtonFromAction(Action a) {

        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        CButton b = new CButton(icon, icon);
        b.setEnabled(a.isEnabled());
        b.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
        b.setAction(a);
        b.setText(null);
        b.addPropertyChangeListener(createActionChangeListener(b));
        return b;
    }

    public static JMenuItem createMenuItem(String name,
                                           ActionListener listener,
                                           String actionCommand,
                                           KeyStroke accelerator
    ) {
        JMenuItem menuItem = new JMenuItem(name);
        if (listener != null) {
            menuItem.addActionListener(listener);
        }
        if (actionCommand != null) {
            menuItem.setActionCommand(actionCommand);
        }
        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }
        menuItem.addPropertyChangeListener(createActionChangeListener(menuItem));
        return menuItem;
    }

    public static JMenuItem createMenuItem(String name,
                                           ActionListener listener,
                                           String actionCommand,
                                           KeyStroke accelerator,
                                           Icon icon
    ) {
        JMenuItem menuItem = createMenuItem(name, listener, actionCommand, accelerator);
        menuItem.setIcon(icon);
        menuItem.addPropertyChangeListener(createActionChangeListener(menuItem));
        return menuItem;
    }

    private static ActionChangedListener createActionChangeListener(JMenuItem b) {
        return new ActionChangedListener(b);
    }

    private static ActionChangedListener createActionChangeListener(CButton b) {
        return new ActionChangedListener(b);
    }

    /**
     * Create menu item. This method installs small ICON
     * instead of action ICON in Swing.
     */
    public static JMenuItem createMenuItem(Action a) {
        JMenuItem menuItem = new JMenuItem(a);
        menuItem.setEnabled(a.isEnabled());
        Icon icon = (Icon) a.getValue(MENU_ICON);
        menuItem.setIcon(icon);
        menuItem.addPropertyChangeListener(createActionChangeListener(menuItem));
        return menuItem;
    }

    /**
     * Create checkbox menu item.
     */
    public static JCheckBoxMenuItem createCheckboxMenuItem(Action a) {
        JCheckBoxMenuItem boxMenuItem = new JCheckBoxMenuItem(a);
        boxMenuItem.setEnabled(a.isEnabled());
        boxMenuItem.addPropertyChangeListener(createActionChangeListener(boxMenuItem));
        return boxMenuItem;
    }

    /**
     * Create radio menu item.
     */
    public static JRadioButtonMenuItem createRadioMenuItem(Action a) {
        JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(a);
        menuItem.setEnabled(a.isEnabled());
        menuItem.addPropertyChangeListener(createActionChangeListener(menuItem));
        return menuItem;
    }

    /**
     * Create button from action without text
     */
    public static JButton createButtonFromAction(Action a) {
        return createButtonFromAction(a, false);
    }

    /**
     * Create button from action
     */
    public static JButton createButtonFromAction(Action a, boolean text) {

        JButton b = new JButton((Icon) a.getValue(Action.SMALL_ICON));
        b.setAction(a);
        if (text) {
            b.setText((String) a.getValue(Action.NAME));
        } else {
            b.setText("");
        }
        b.setEnabled(a.isEnabled());
        b.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
        return b;
    }
}
