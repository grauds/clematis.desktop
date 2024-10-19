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

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * Abstract state implements action allows controlling of checkbox
 * menu items or buttons.
 *
 * @author Anton Troshin
 */
public abstract class AbstractStateAction extends AbstractAction implements StateAction {

    static final String SELECTED = "SELECTED";

    private static final String SELECTABLE = "SELECTABLE";

    public AbstractStateAction() {
        super();
        setSelectable(false);
        setSelected(true);
    }

    public AbstractStateAction(String name) {
        super(name);
        setSelectable(false);
        setSelected(true);
    }

    public AbstractStateAction(String name, Icon icon) {
        super(name, icon);
        setSelectable(false);
        setSelected(true);
    }

    public boolean isSelected() {
        return (Boolean) getValue(SELECTED);
    }

    public void setSelected(boolean selected) {
        putValue(SELECTED, selected);
    }

    public boolean isSelectable() {
        return (Boolean) getValue(SELECTABLE);
    }

    public void setSelectable(boolean selectable) {
        putValue(SELECTABLE, selectable);
    }
}