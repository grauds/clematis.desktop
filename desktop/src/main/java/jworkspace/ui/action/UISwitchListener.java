package jworkspace.ui.action;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;

/**
 * Listener for changes of UI. This class
 * should be used on any component to
 * ensure UI switch "on-fly". This is
 * nesessary for all components in
 * Java Workspace UI.
 */
public class UISwitchListener
    implements PropertyChangeListener {
    Component componentToSwitch;

    /**
     * Instance of <code>jworkspace.ui.action.UISwitchListener</code>
     * deals with one component and switches UI for it.
     * For example, this is quite useful for components outside
     * components trees.
     */
    public UISwitchListener(Component c) {
        componentToSwitch = c;
    }

    /**
     * Property change event is a change of laf at runtime.
     */
    public void propertyChange(PropertyChangeEvent e) {
        String name = e.getPropertyName();
        if (name.equals("lookAndFeel")) {
            SwingUtilities.updateComponentTreeUI(componentToSwitch);
            componentToSwitch.invalidate();
            componentToSwitch.validate();
            componentToSwitch.repaint();
        }
    }
}