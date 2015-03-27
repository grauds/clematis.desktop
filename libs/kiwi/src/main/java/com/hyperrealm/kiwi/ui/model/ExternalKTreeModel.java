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

import java.util.ArrayList;
import javax.swing.Icon;

/** An implementation of <code>KTreeModel</code> that obtains its data from
 * an external data source.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class ExternalKTreeModel<T> extends DefaultKTreeModel<T>
{
  private String columnNames[];
  private Class columnTypes[];
  
  /** The data source for this model. */
  protected TreeDataSource<T> source;


  /** Construct a new <code>DefaultKTreeModel</code> with the given
   * data source.
   *
   * @param source The data source that will provide nodes for this model.
   */
  
  public ExternalKTreeModel(TreeDataSource<T> source)
  {
    super();
    
    this.source = source;

    reload(null);
  }
  
  /** Reload the subtree rooted at the given node from the data source.
   *
   * @param node The root node of the subtree to reload, or <b>null</b> to
   * reload the entire tree.
   */

  public void reload(T node)
  {
    if(node == null)
    {
      // load metadata

      columnNames = (String[])source.getValueForProperty(
        null, TreeDataSource.COLUMN_NAMES_PROPERTY);
      columnTypes = (Class[])source.getValueForProperty(
        null, TreeDataSource.COLUMN_TYPES_PROPERTY);
      
      // load data
      
      rootNode = makeNode(source.getRoot(), null);
      preloadChildren(rootNode.getObject());
      
      support.fireDataChanged();
    }
    else
    {
      TreeNode n = nodeForObject(node);
      
      if(n != null)
      {
        n.releaseChildren();
        preloadChildren(node);
        support.fireNodeStructureChanged(node);
      }
    }
  }

  /*
   */
  
  public void setRoot(T root)
  {
    throw(new ImmutableModelException());
  }

  /*
   */

  public void removeChildren(T parent)
  {
    throw(new ImmutableModelException());
  }

  /*
   */

  public void removeChild(T parent, int index)
  {
    throw(new ImmutableModelException());
  }

  /*
   */

  public void addChild(T parent, T node)
  {
    throw(new ImmutableModelException());
  }

  /*
   */

  public void addChild(T parent, T node, int index)
  {
    throw(new ImmutableModelException());
  }

  /*
   */

  public void updateNode(T node)
  {
    throw(new ImmutableModelException());
  }

  /*
   */
  
  public void updateChildren(T parent)
  {
    throw(new ImmutableModelException());
  }  

  /*
   */

  public Icon getIcon(T node, boolean isExpanded)
  {
    return(source.getIcon(node, isExpanded));
  }

  /*
   */

  public boolean isExpandable(T node)
  {
    return(source.isExpandable(node));
  }

  /*
   */

  public String getLabel(T node)
  {
    return(source.getLabel(node));
  }

  /**
   */

  public Object getField(T item, int field)
  {
    return(source.getValueForProperty(item, columnNames[field]));
  }

  /**
   */

  public int getFieldCount()
  {
    return(columnNames.length);
  }

  /**
   */

  public String getFieldLabel(int index)
  {
    return(columnNames[index]);
  }

  /**
   */

  public Class getFieldType(T item, int index)
  {
    return(columnTypes[index]);
  }

  /*
   */

  public void releaseChildren(T parent)
  {
    TreeNode p = nodeForObject(parent);
    if(p != null)
      p.releaseChildren();
  }

  /*
   */

  protected void loadChildren(TreeNode node)
  {
    ArrayList list = new ArrayList();
    
    T children[] = source.getChildren(node.getObject());
    for(int i = 0; i < children.length; i++)
      list.add(makeNode(children[i], node)); // this is UGLY!

    node.setChildren(list);
  }  
  
}

/* end of source file */
