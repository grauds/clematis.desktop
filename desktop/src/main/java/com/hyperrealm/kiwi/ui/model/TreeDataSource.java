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

package com.hyperrealm.kiwi.ui.model;

import javax.swing.Icon;

/**
 * This interface defines a data source for populating tree data structures.
 *
 * @param <T>
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public interface TreeDataSource<T> extends ModelProperties {
    /**
     * Get the root object.
     */

    T getRoot();

    /**
     * Get the children of a given node in the tree.
     *
     * @param node The node that children are being requested for.
     * @return A (possibly empty) array of children for the node.
     */

    T[] getChildren(T node);

    /**
     * Get the value of an arbitrary property for a given node.
     *
     * @param node     The node.
     * @param property The name of the property; one of the constants defined
     *                 above, or some arbitrary application-defined property.
     * @return The value of the specified property, or <code>null</code> if
     * there is no value for this property.
     */

    Object getValueForProperty(T node, String property);

    /**
     * Determine if this node is expandable.
     *
     * @param node The node.
     * @return <b>true</b> if the node is expandable, <b>false</b> otherwise.
     */

    boolean isExpandable(T node);

    /**
     * Get the icon for a node.
     *
     * @param node     The node.
     * @param expanded The current expanded state of the node.
     * @return An icon for the item.
     */

    Icon getIcon(T node, boolean expanded);

    /**
     * Get the label for a node.
     *
     * @param node The node.
     * @return A string label for the node.
     */

    String getLabel(T node);

}
