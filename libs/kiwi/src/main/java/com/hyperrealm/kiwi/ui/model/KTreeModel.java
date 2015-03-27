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

import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.util.*;

/** This interface defines the behavior for a data model for tree data
 * structures.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public interface KTreeModel<T>
{
  /** Get the root node.
   *
   * @return The root node.
   */

  public T getRoot();

  /** Set the root node. (For immutable data models, this method may be
   * implemented as a no-op.)
   *
   * @param root The new root node.
   */
  
  public void setRoot(T root);

  /** Get a child count for a node.
   *
   * @param parent The parent node.
   *
   * @return The number of children that the given node has.
   */
  
  public int getChildCount(T parent);
  
  /** Get a child of a node.
   *
   * @param parent The parent node.
   * @param index The index of the child within the parent's list of children.
   *
   * @return The node, or <code>null</code> if no such node exists.
   */

  public T getChild(T parent, int index);

  /** Get the children of a node.
   *
   * @param parent The parent node.
   *
   * @return An iterator to the list of child nodes.
   */
  
  public Iterator<T> getChildren(T parent);

  /** Get the index of a child node in its parent's list of children.
   *
   * @param parent The parent node.
   * @param node The child node.
   * @return The index of the node, or -1 if the node is not a child of the
   * given parent.
   */

  public int getIndexOfChild(T parent, T node);

  /** Remove all of the children of a given parent node.  (For
   * immutable data models, this method may be implemented as a
   * no-op.)
   *
   * @param parent The parent node.
   */

  public void removeChildren(T parent);

  /** Remove a child node of a given parent node. (For immutable data
   * models, this method may be implemented as a no-op.)
   *
   * @param parent The parent node.
   * @param index The index of the child to remove within the parent's list of
   * children.
   */
  
  public void removeChild(T parent, int index);

  /** Add a new child node to the given parent node. The child is
   * added at the end of the parent's list of children. (For immutable
   * data models, this method may be implemented as a no-op.)
   *
   * @param parent The parent node.
   * @param node The new child node.
   */
  
  public void addChild(T parent, T node);

  /** Add a new child node to the given parent node. (For immutable
   * data models, this method may be implemented as a no-op.)
   *
   * @param parent The parent node.
   * @param node The new child node.
   * @param index The offset in the list of children at which the new node
   * should be inserted.
   */
  
  public void addChild(T parent, T node, int index);

  /** Get the parent of a node.
   *
   * @param node The child node.
   * return The parent of the specified child node.
   */

  public T getParent(T node);

  /** Determine if a node is expandable.
   *
   * @param node The node to test.
   * @return <code>true</code> if the node is expandable, <code>false</code>
   * otherwise.
   */

  public boolean isExpandable(T node);

  /** Get the icon for a node.
   *
   * @param node The node.
   * @param isExpanded The current expanded state for the node.
   * @return An icon for the node.
   */
  
  public Icon getIcon(T node, boolean isExpanded);

  /** Get the label for a node.
   *
   * @param node The node.
   * @return A string label for the node.
   */
  
  public String getLabel(T node);

  /** Get the field count for this model.
   *
   * @return The field count.
   *
   * @since Kiwi 2.3
   */

  public int getFieldCount();

  /** Get the label for the given field.
   *
   * @param index The field index.
   * @return The label for the field.
   *
   * @since Kiwi 2.3
   */

  public String getFieldLabel(int index);

  /** Get the type of the given field.
   *
   * @param index The field index.
   * @return The type of the field.
   *
   * @since Kiwi 2.3
   */
  
  public Class getFieldType(int index);

  /** Get the value for a field in the given node.
   *
   * @param node The node.
   * @param index The field index.
   * @return The value of the field.
   *
   * @since Kiwi 2.3
   */

  public Object getField(T node, int index);
  
  /** Indicate to listeners that the specified node has changed.
   *
   * @param node The node.
   */
  
  public void updateNode(T node);

  /** Indicate to listeners that the list of children of the specified node
   * has changed.
   *
   * @param parent The parent node.
   */
  
  public void updateChildren(T parent);

  /** Preload the children of a given node. This method is intended
   * for data models that are dynamically loaded from an external data
   * source; it may be desirable to instruct a model to prefetch the
   * children for a given node when that node is expanding. For models
   * where this is not desirable or not applicable, this method may be
   * implemented as a no-op.
   *
   * @param parent The node for which children should be preloaded.
   */

  public void preloadChildren(T parent);
  
  /** Release the children of a given parent node. This method is
   * intended for data models that are dynamically loaded from an
   * external data source; it may be desirable to instruct a model to
   * "forget" the subtree rooted at a given node when that node is
   * collapsed. For models where this is not desirable or not
   * applicable, the method may be implemented as a no-op.
   *
   * @param parent The root node of the subtree that should be released.
   */

  public void releaseChildren(T parent);

  /** Add a <code>KTreeModelListener</code> to this model's list of listeners.
   *
   * @param listener The listener to add.
   */
  
  public void addTreeModelListener(KTreeModelListener listener);

  /** Remove a <code>KTreeModelListener</code> from this model's list of
   * listeners.
   *
   * @param listener The listener to remove.
   */

  public void removeTreeModelListener(KTreeModelListener listener);
  
}

/* end of source file */
