package jworkspace.ui.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 Anton Troshin

   This file is part of Java Workspace.

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   Authors may be contacted at:

   frenzy@ix.netcom.com
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.*;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.KPanel;
import kiwi.ui.*;

import jworkspace.kernel.*;
import jworkspace.ui.installer.dialog.*;
import jworkspace.util.*;
import jworkspace.ui.action.*;

/**
 * Tabbed viewer holds several components plus makes searches to show
 * tab with specified component.
 */
class TabbedViewer extends KPanel implements ActionListener
{
  public static final String CLOSE_TAB = "CLOSE_TAB";
  public static final String CLOSE_TABS = "CLOSE_TABS";
  public static final String CLOSE_ALL = "CLOSE_ALL";
  protected JTabbedPane tabbed_pane  = new JTabbedPane();
  protected TabMenu tabMenu = new TabMenu(this);
  /**
   * Tab menu
   */
  class TabMenu extends JPopupMenu
  {
    ActionListener listener = null;
    JMenuItem close_active_tab = null;
    JMenuItem close_tabs = null;
    JMenuItem close_all = null;

    TabMenu(ActionListener listener)
    {
      super();
      this.listener = listener;
      createItems();
      UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }
    void createItems()
    {
      close_active_tab = add(WorkspaceUtils.createMenuItem(
           listener, LangResource.getString("Close"),
           TabbedViewer.CLOSE_TAB, null));
      close_tabs = add(WorkspaceUtils.createMenuItem(
           listener, LangResource.getString("Close_Files") + "...",
           TabbedViewer.CLOSE_TABS, null));
      addSeparator();
      close_all = add(WorkspaceUtils.createMenuItem(
           listener,
           LangResource.getString("Close_All"),
           TabbedViewer.CLOSE_ALL, null));
    }
  }
  /**
   * Public constructor.
   */
  public TabbedViewer()
  {
    super();
    /**
     * Sets layout for this panel.
     */
    setLayout(new BorderLayout());
    /**
     * Adds components.
     */
    tabbed_pane.setTabPlacement(JTabbedPane.BOTTOM);
    tabbed_pane.setOpaque(false);
    add(tabbed_pane, BorderLayout.CENTER);
    /**
     * Add mouse listener
     */
    tabbed_pane.addMouseListener(new MouseAdapter()
    {
      public void mouseReleased(MouseEvent evt)
      {
        if ((evt.isControlDown() || SwingUtilities.isRightMouseButton(evt))
                && tabbed_pane.getTabCount() != 0)
        {
           int sel = tabbed_pane.getSelectedIndex();
           tabMenu.close_active_tab.setText("Close \"" +
                     tabbed_pane.getTitleAt(sel) + "\"");
           tabMenu.show(tabbed_pane, evt.getPoint().x,  evt.getPoint().y);
        }
      }
    });
  }
  /**
   * Sets blank property page.
   */
  protected JComponent getBlankPropPage()
  {
    return new KPanel();
  }
  /**
   * Action handler.
   */
  public void actionPerformed(java.awt.event.ActionEvent e)
  {
    String command = e.getActionCommand();
    if (command.equals(TabbedViewer.CLOSE_ALL))
    {
      tabbed_pane.removeAll();
      tabbed_pane.revalidate();
      tabbed_pane.repaint();
    }
    else if (command.equals(TabbedViewer.CLOSE_TABS))
    {
      CloseFilesDialog dlg =
          new CloseFilesDialog(Workspace.getUI().getFrame(), getTabNames());
      dlg.centerDialog();
      dlg.setVisible(true);
      if (!dlg.isCancelled())
      {
        String[] selected = dlg.getSelectedListData();
        for (int i = 0; i < selected.length; i++)
        {
           tabbed_pane.remove(tabbed_pane.indexOfTab(selected[i]));
        }
      }
    }
    else if (command.equals(TabbedViewer.CLOSE_TAB))
    {
      tabbed_pane.removeTabAt(tabbed_pane.getSelectedIndex());
      tabbed_pane.revalidate();
      tabbed_pane.repaint();
    }
  }

  public synchronized void addPropPage(JComponent propPage)
  {
    if (propPage == null)
      propPage = getBlankPropPage();

    if (propPage.getName() != null)
    {
       int index = propPage.getName().lastIndexOf(File.separator);

       if (index == -1)
         index = propPage.getName().lastIndexOf("/");

       String name = propPage.getName().substring(index + 1, propPage.getName().length());

       for (int i = 0; i < tabbed_pane.getTabCount(); i++)
         if (tabbed_pane.getComponentAt(i).getName() != null
              && tabbed_pane.getComponentAt(i).getName().equals(propPage.getName()))
         {
           tabbed_pane.setSelectedIndex(i);
           tabbed_pane.revalidate();
           tabbed_pane.repaint();
           return;
         }

        tabbed_pane.addTab(name, propPage);
    }
    else
    {
      tabbed_pane.addTab("untitled", propPage);
    }

    tabbed_pane.setSelectedComponent(propPage);
    tabbed_pane.revalidate();
    tabbed_pane.repaint();
  }

  /**
   * Returns the minimum size of this container.
   * @see #getPreferredSize
   */
  public Dimension getMinimumSize()
  {
    return new Dimension(10,10);
  }

  /**
   * Returns the minimum size of this container.
   * @see #getPreferredSize
   */
  public Dimension getPreferredSize()
  {
    return getMinimumSize();
  }
  /**
   * Create a list of tab names
   */
  protected String[] getTabNames()
  {
    Vector v = new Vector();
    int tabs = tabbed_pane.getTabCount();
    for (int i = 0; i < tabs; i++)
    {
      v.addElement(tabbed_pane.getTitleAt(i));
    }
    String[] names = new String[v.size()];
    for (int i = 0; i < v.size(); i++)
    {
      names[i] = (String) v.elementAt(i);
    }
    return names;
  }
  public void removeAllTabs()
  {
    tabbed_pane.removeAll();
  }
  public synchronized void addPropPage(JComponent propPage, boolean scrolls)
  {
    if (propPage == null)
      propPage = getBlankPropPage();

    if (propPage.getName() != null)
    {
       int index = propPage.getName().lastIndexOf(File.separator);

       if (index == -1)
         index = propPage.getName().lastIndexOf("/");

       String name = propPage.getName().substring(index + 1, propPage.getName().length());

       for (int i = 0; i < tabbed_pane.getTabCount(); i++)
         if (tabbed_pane.getComponentAt(i).getName() != null
              && tabbed_pane.getComponentAt(i).getName().equals(propPage.getName()))
         {
           tabbed_pane.setSelectedIndex(i);
           tabbed_pane.revalidate();
           tabbed_pane.repaint();
           return;
         }

       if (scrolls)
       {
         JScrollPane scroll = new JScrollPane(propPage);
         scroll.setName(propPage.getName());
         tabbed_pane.addTab(name, scroll);
       }
       else
       {
         tabbed_pane.addTab(name, propPage);
       }
    }
    else
    {
      tabbed_pane.addTab("untitled", propPage);
    }

    tabbed_pane.setSelectedIndex(tabbed_pane.getTabCount() - 1);
    tabbed_pane.revalidate();
    tabbed_pane.repaint();
  }
}
