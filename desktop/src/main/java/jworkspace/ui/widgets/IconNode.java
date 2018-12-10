package jworkspace.ui.widgets;

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

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Icon node is nessesary for nodes in Java Workspace trees,
 * each node allows placement of images.
 */
public class IconNode extends DefaultMutableTreeNode {
    protected Icon icon;
    protected String iconName;

    public IconNode() {
        this(null);
    }

    public IconNode(Object userObject) {
        this(userObject, true, null);
    }

    public IconNode(Object userObject, boolean allowsChildren, Icon icon) {
        super(userObject, allowsChildren);
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getIconName() {
        String str = userObject.toString();
        int index = str.lastIndexOf(".");
        if (index != -1) {
            return str.substring(++index);
        } else {
            return iconName;
        }
    }

    public void setIconName(String name) {
        iconName = name;
    }
}