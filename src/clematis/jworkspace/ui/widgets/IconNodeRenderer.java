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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Hashtable;

/**
 * The renderer for class IconNode.
 */
public class IconNodeRenderer extends DefaultTreeCellRenderer
{
    public IconNodeRenderer()
    {
        super();
    }

    public Color getBackgroundSelectionColor()
    {
        return UIManager.getColor("selectedControl");
    }

    public Color getBorderSelectionColor()
    {
        return UIManager.getColor("controlDraftBorder");
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof IconNode)
        {
            Icon icon = ((IconNode) value).getIcon();
            if (icon == null)
            {
                Hashtable icons = (Hashtable) tree.getClientProperty("JTree.icons");
                String name = ((IconNode) value).getIconName();
                if ((icons != null) && (name != null))
                {
                    icon = (Icon) icons.get(name);
                    if (icon != null)
                    {
                        setIcon(icon);
                    }
                }
            }
            else
            {
                setIcon(icon);
            }
        }
        return this;
    }
}