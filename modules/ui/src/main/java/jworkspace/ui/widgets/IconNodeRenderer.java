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

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * The renderer for class IconNode.
 * @author Anton Troshin
 */
public class IconNodeRenderer extends DefaultTreeCellRenderer {

    public IconNodeRenderer() {
        super();
    }

    public Color getBackgroundSelectionColor() {
        return UIManager.getColor("selectedControl");
    }

    public Color getBorderSelectionColor() {
        return UIManager.getColor("controlDraftBorder");
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof IconNode) {
            Icon icon = ((IconNode) value).getIcon();
            if (icon == null) {
                icon = getIcon(tree, (IconNode) value);
            }
            if (icon != null) {
                setIcon(icon);
            }
        }
        return this;
    }

    private Icon getIcon(JTree tree, IconNode value) {
        Icon icon = null;

        Map icons = (Map) tree.getClientProperty("JTree.icons");
        String name = value.getIconName();
        if ((icons != null) && (name != null)) {
            icon = (Icon) icons.get(name);
        }

        return icon;
    }
}