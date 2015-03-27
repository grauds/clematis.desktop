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

package com.hyperrealm.kiwi.ui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import java.net.URL;

import com.hyperrealm.kiwi.ui.model.*;

/** This class represents a general-purpose browser component for viewing a
 * hierarchically-organized collection of documents. The interface consists of
 * a split pane with a tree component in the left pane and an HTML component
 * in the right pane.
 *
 * <p><center>
 * <img src="snapshot/DocumentBrowserView.gif"><br>
 * <i>An example DocumentBrowserView.</i>
 * </center>
 *
 * @see com.hyperrealm.kiwi.ui.DocumentBrowserFrame
 *
 * @author Mark Lindner
 */

public class DocumentBrowserView extends KPanel
{
  private JTree tree = null;
  private JEditorPane html = null;
  private KTreeModelTreeAdapter adapter;
  private KTreeModel model;
  
  /** Construct a new <code>DocumentBrowserView</code>.
   *
   * @param model The tree data model for this browser.
   */

  public DocumentBrowserView(KTreeModel model)
  {
    this.model = model;
    setLayout(new GridLayout(1, 0));
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    split.setOpaque(false);
    add(split);

    tree = new JTree();
    tree.setBackground(Color.white);
    tree.setRootVisible(false);
    KScrollPane scroll = new KScrollPane(tree);

    adapter = new KTreeModelTreeAdapter(tree);
    adapter.setTreeModel(model);

    tree.setModel(adapter);
    tree.setCellRenderer(new KTreeModelTreeCellRenderer(model));
    

    split.setLeftComponent(scroll);

    html = new JEditorPane();
    html.setBackground(Color.white);
    html.setEditable(false);

    html.addHyperlinkListener(new HyperlinkListener()
      {
        public void hyperlinkUpdate(HyperlinkEvent evt)
        {
          if(evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
          {
            Object nodes[] = nodesForPath(evt.getURL().getFile());
            if(nodes != null)
            {
              expandNodes(nodes);
              showNode((DocumentDataSource.DocumentNode)nodes[nodes.length - 1]);
            }
          }
        }
      });

    scroll = new KScrollPane(html);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants
                                        .HORIZONTAL_SCROLLBAR_AS_NEEDED);
    split.setRightComponent(scroll);

    split.setDividerLocation(150);
    
    DefaultTreeSelectionModel ts = new DefaultTreeSelectionModel();
    ts.setSelectionMode(ts.SINGLE_TREE_SELECTION);

    tree.addTreeSelectionListener(new TreeSelectionListener()
      {
        public void valueChanged(TreeSelectionEvent evt)
        {
          Object item = evt.getPath().getLastPathComponent();
          if(item instanceof DocumentDataSource.DocumentNode)
          {
            DocumentDataSource.DocumentNode helpNode
              = (DocumentDataSource.DocumentNode)item;
            if(! helpNode.isExpandable())
              showNode(helpNode);
          }
        }
      });
  }
  
  /** Display a specific page in the browser.
   *
   * @param page The URL of the page to display.
   */

  private void showNode(DocumentDataSource.DocumentNode node)
  {
    try
    {
      html.setPage(node.getURL());
    }
    catch(java.io.IOException ex)
    {
      // more graceful way to handle this?
      ex.printStackTrace();
    }
  }

  /*
   */
  
  private void expandNodes(Object nodes[])
  {
    TreePath path = adapter.getPathForNode(nodes[nodes.length - 1]);
    if(path != null)
    {
      tree.scrollPathToVisible(path);
      tree.setSelectionPath(path);
    }
  }

  /*
   */

  private Object[] nodesForPath(String path)
  {
    StringTokenizer st = new StringTokenizer(path, "/");
    Object curNode = model.getRoot();
    ArrayList v = new ArrayList();

  LOOP:

    for(;;)
    {
      v.add(curNode);
      
      if(! model.isExpandable(curNode))
        break;
      
      if(!st.hasMoreTokens())
        break;

      String s = st.nextToken();

      model.preloadChildren(curNode);
      Iterator iter = model.getChildren(curNode);
      while(iter.hasNext())
      {
        Object node = iter.next();
        
        String file = ((DocumentDataSource.DocumentNode)node).getFile();

        if(s.equals(file))
        {
          curNode = node;
          continue LOOP;
        }
      }

      return(null);
    }

    if(st.hasMoreTokens())
      return(null);
    
    Object n[] = new Object[v.size()];
    return(v.toArray(n));
  }
  
}

/* end of source file */
