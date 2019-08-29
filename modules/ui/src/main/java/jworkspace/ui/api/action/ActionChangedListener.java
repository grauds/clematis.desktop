package jworkspace.ui.api.action;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * Action changed listener updates all menus and button,
 * associated with some action that changes.
 *
 * @author Anton Troshin
 */
public class ActionChangedListener implements PropertyChangeListener {

    private JMenuItem menuItem = null;

    private AbstractButton b = null;

    public ActionChangedListener(JMenuItem mi) {
        super();
        this.menuItem = mi;
    }

    public ActionChangedListener(AbstractButton b) {
        super();
        this.b = b;
    }

    public ActionChangedListener(JMenuItem mi, AbstractButton b) {
        super();
        this.menuItem = mi;
        this.b = b;
    }

    public void propertyChange(PropertyChangeEvent e) {

        String propertyName = e.getPropertyName();

        if (e.getPropertyName().equals(Action.NAME)) {
            String text = (String) e.getNewValue();
            if (menuItem != null) {
                menuItem.setText(text);
            }
            if (b != null) {
                b.setText(text);
            }
        } else if (propertyName.equals("enabled")) {
            Boolean enabledState = (Boolean) e.getNewValue();
            if (enabledState == null) {
                enabledState = true;
            }
            if (menuItem != null) {
                menuItem.setEnabled(enabledState);
            }
            if (b != null) {
                b.setEnabled(enabledState);
            }
        } else if (propertyName.equals(AbstractStateAction.SELECTED)) {
            Boolean selectedState = (Boolean) e.getNewValue();
            if (menuItem != null) {
                menuItem.setSelected(selectedState);
            }
            if (b != null) {
                b.setSelected(selectedState);
            }
        } else if (e.getPropertyName().equals(Action.SMALL_ICON)) {
            Icon icon = (Icon) e.getNewValue();
            if (menuItem != null) {
                menuItem.setIcon(icon);
            }
            if (b != null) {
                b.setIcon(icon);
            }
        }

        if (menuItem != null) {
            menuItem.repaint();
        }

        if (b != null) {
            b.repaint();
        }
    }
}

