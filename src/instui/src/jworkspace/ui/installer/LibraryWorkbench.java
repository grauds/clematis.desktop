package jworkspace.ui.installer;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2002 Anton Troshin

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
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
*/
import java.io.*;
import javax.swing.*;

import kiwi.util.*;

import jworkspace.ui.*;
import jworkspace.util.*;
import jworkspace.installer.*;
import jworkspace.kernel.*;
import jworkspace.ui.installer.dialog.*;
import jworkspace.ui.installer.actions.*;
import java.util.ResourceBundle;
/**
 * Library workbench.
 */
public class LibraryWorkbench extends Workbench
{
  /**
   * Constructor for library workbench
   */
  public LibraryWorkbench(InstallerWindow installer)
  {
    super(installer);
    icon = new ImageIcon(new ResourceLoader(InstallerWindow.class)
           .getResourceAsImage("images/library_big.png"));
    text = LangResource.getString("Libraries");
    comment = LangResource.getString("Manages_third_party1");
    layoutFullHeightContent();
  }
  /**
   * Get a reference to content tree
   */
  public JTree getContentTree()
  {
    return initContentTree(Workspace.getInstallEngine().getLibraryModel());
  }
  /**
   * Create toolbar for a specified type of content
   */
  public JToolBar getToolBar()
  {
     if (ctoolbar == null)
     {
        ctoolbar = new JToolBar();
        WorkspaceGUI gui = null;
        if (Workspace.getUI() instanceof WorkspaceGUI)
        {
          gui = (WorkspaceGUI) Workspace.getUI();
        }
        ctoolbar.setFloatable(false);

        if (gui != null)
        {
          JButton b = WorkspaceUtils.
               createButtonFromAction(WorkbenchActions.addFolderAction, false);
          ctoolbar.add(b);
          WorkbenchActions.addFolderAction.
                addPropertyChangeListener(gui.createActionChangeListener(b));

          b = WorkspaceUtils.
                     createButtonFromAction(WorkbenchActions.addAction, true);
          ctoolbar.add(b);
          WorkbenchActions.addAction.
             addPropertyChangeListener(gui.createActionChangeListener(b));

          b = WorkspaceUtils.
               createButtonFromAction(WorkbenchActions.editAction, false);
          ctoolbar.add(b);
          WorkbenchActions.editAction.
                addPropertyChangeListener(gui.createActionChangeListener(b));

          b = WorkspaceUtils.
               createButtonFromAction(WorkbenchActions.deleteAction, false);
          ctoolbar.add(b);
          WorkbenchActions.deleteAction.
                addPropertyChangeListener(gui.createActionChangeListener(b));
        }
     }
     return ctoolbar;
  }
  /**
   * Adds new library with argument node
   * as parent.
   * @param parent jworkspace.installer.DefinitionNode
   */
  public void add()
  {
    if (currentNode == null)
    {
      return;
    }
    Library lib = null;

    ImageIcon icon = new ImageIcon( Workspace.getResourceManager().
               getImage("library_big.png") );
    String name = (String) JOptionPane.showInputDialog(
          this,
          LangResource.getString("message#173"),
          LangResource.getString("New Library Configuration File"),
           JOptionPane.QUESTION_MESSAGE,
           icon , null, null);

    if(!validateInstName(currentNode, name, "library"))
    {
      return;
    }

    lib = new Library(currentNode, name);

    LibraryDialog lib_dlg = new LibraryDialog(Workspace.getUI().getFrame());
    lib_dlg.setData(lib);
    lib_dlg.centerDialog();
    lib_dlg.setVisible(true);

    if(!lib_dlg.isCancelled())
    {
      try
      {
        lib.save();
        currentNode.add(lib);
      }
      catch(IOException ex)
      {
        Workspace.logException(LangResource.getString("message#170")
                   + ex.getMessage());
        JOptionPane.showMessageDialog(this,LangResource.getString("message#170")
                             + ex.getMessage());
      }
    }
  }
  /**
   * Edit library.
   * @param parent jworkspace.installer.DefinitionNode
   */
  public void edit()
  {
    if (currentNode == null || !(currentNode instanceof Library))
    {
      return;
    }
    Library lib = (Library) currentNode;

    LibraryDialog lib_dlg = new LibraryDialog(Workspace.getUI().getFrame());
    lib_dlg.setData(lib);
    lib_dlg.centerDialog();
    lib_dlg.setVisible(true);

    if(!lib_dlg.isCancelled())
    {
      try
      {
       lib.save();
       String source = null;
       String cover = null;

       source = (lib).getSource();
       cover = (lib).getDocs();
       jarInspector.setSource(source);
      }
      catch(IOException ex)
      {
        Workspace.logException(LangResource.getString("message#170")
                   + ex.getMessage());
        JOptionPane.showMessageDialog(this,LangResource.getString("message#170")
                   + ex.getMessage());
      }
    }
  }
}
