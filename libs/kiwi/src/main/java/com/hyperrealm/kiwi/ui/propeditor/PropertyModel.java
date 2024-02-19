/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.ui.propeditor;

import javax.swing.Icon;

import com.hyperrealm.kiwi.ui.model.tree.DefaultKTreeModel;

/**
 * A data model for the PropertyEditor component.
 *
 * @author Mark Lindner
 * @since Kiwi 2.3
 */

public class PropertyModel extends DefaultKTreeModel<Property> {
    /**
     * Construct a new <code>PropertyModel</code>.
     */

    public PropertyModel() {
    }

    /**
     *
     */

    public final Icon getIcon(Property node, boolean isExpanded) {
        return (node.getIcon());
    }

    /**
     *
     */

    public final String getLabel(Property node) {
        return (node.toString());
    }

    /**
     *
     */

    public final boolean isExpandable(Property node) {
        return (getChildCount(node) > 0);
    }

}


