package jworkspace.ui.installer.actions;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner, 2002 Anton Troshin

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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import jworkspace.kernel.ResourceManager;
import jworkspace.ui.installer.*;

public class WorkbenchActions
{
 /**
  * All actions
  */
  protected Hashtable actions = new Hashtable();
  /**
   * Instance of workbench
   */
  protected Workbench wb = null;
  /**
   * Add folder to the installation tree.
   */
  public static final String addFolderActionName  = "Add Folder";
  /**
   * Delete folder or installation entry
   */
  public static final String deleteActionName  = "Delete";
  /**
   * Edit installation entry
   */
  public static final String editActionName  = "Edit Entry";
  /**
   * View documentation
   */
  public static final String viewDocActionName  = "View Documentation";
  /**
   * Add entry action name
   */
  public static final String addActionName  = "Add Entry";
 /**
  * Add folder action
  */
  public static Action addFolderAction;
  protected class AddFolderAction extends AbstractAction
  {
    public AddFolderAction ()
    {
      super(addFolderActionName);
      putValue(Action.SMALL_ICON, new ImageIcon(ResourceManager.getKiwiResourceManager().
                        getImage("folder-new.gif")));
      putValue(Action.SHORT_DESCRIPTION, "Add Folder");
    }
    public void actionPerformed (ActionEvent evt)
    {
      wb.addFolder();
    }
  }
 /**
  * Delete action
  */
  public static Action deleteAction;
  protected class DeleteAction extends AbstractAction
  {
    public DeleteAction ()
    {
      super(deleteActionName);
      putValue(Action.SMALL_ICON, new ImageIcon(ResourceManager.getKiwiResourceManager().
                                  getImage("delete.png")));
      putValue(Action.SHORT_DESCRIPTION, "Delete");
    }
    public void actionPerformed (ActionEvent evt)
    {
      wb.delete();
    }
  }
 /**
  * Add entry action
  */
  public static Action addAction;
  protected class AddAction extends AbstractAction
  {
    public AddAction ()
    {
      super(addActionName);
      putValue(Action.SMALL_ICON, new ImageIcon(ResourceManager.
                getKiwiResourceManager().getImage("plus.png")));
      putValue(Action.SHORT_DESCRIPTION, "Add Entry");
    }
    public void actionPerformed (ActionEvent evt)
    {
      wb.add();
    }
  }
 /**
  * Edit action
  */
  public static Action editAction;
  protected class EditAction extends AbstractAction
  {
    public EditAction ()
    {
      super(editActionName);
      putValue(Action.SMALL_ICON, new ImageIcon(ResourceManager.
                getKiwiResourceManager().getImage("prefs.png")));
      putValue(Action.SHORT_DESCRIPTION, "Edit");
    }
    public void actionPerformed (ActionEvent evt)
    {
      wb.edit();
    }
  }
 /**
  * Edit action
  */
  public static Action viewDocAction;
  protected class ViewDocAction extends AbstractAction
  {
    public ViewDocAction()
    {
      super(viewDocActionName);
      putValue(Action.SMALL_ICON,  new ImageIcon(ResourceManager.
                getKiwiResourceManager().getImage("document.png")));
      putValue(Action.SHORT_DESCRIPTION, "View Documentation");
    }
    public void actionPerformed (ActionEvent evt)
    {
      wb.viewDocumentation();
    }
  }
  public WorkbenchActions(Workbench wb)
  {
    super();
    this.wb = wb;
    createActions();
    enableActions(true);
  }
  public Action getAction(String name)
  {
    return  (Action) actions.get(name);
  }
  public Action[] getActions()
  {
    Enumeration e = actions.elements();
    Action[] temp = new Action[actions.size()];
    for (int i = 0; i < temp.length; i++)
    {
      temp[i] = (Action) e.nextElement();
    }
    return temp;
  }
  public void enableActions(boolean flag)
  {
     Action[] actions = getActions();
     for (int i = 0; i < actions.length; i++)
     {
        actions[i].setEnabled(flag);
     }
  }
 /**
  * Create actions
  */
  protected Hashtable createActions()
  {
    addFolderAction = new AddFolderAction();
    deleteAction = new DeleteAction();
    addAction = new AddAction();
    editAction = new EditAction();
    viewDocAction = new ViewDocAction();

    actions.put(addFolderActionName, addFolderAction);
    actions.put(deleteActionName, deleteAction);
    actions.put(addActionName, addAction);
    actions.put(editActionName, editAction);
    actions.put(viewDocActionName, viewDocAction);

    return actions;
  }
}