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
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/
import java.io.*;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.KTreeTable;
import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.installer.*;
import jworkspace.kernel.*;
import jworkspace.ui.*;
import jworkspace.ui.dialog.*;
import jworkspace.util.*;
import jworkspace.ui.installer.dialog.*;
import jworkspace.ui.installer.actions.ApplicationActions;
/**
 * Application workbench.
 */
public class ApplicationWorkbench extends Workbench
{
  /**
   * Application actions
   */
  protected ApplicationActions actions = null;
  /**
   * Constructor for application workbench
   */
  public ApplicationWorkbench(InstallerWindow installer)
  {
    super(installer);
    icon = new ImageIcon(new ResourceLoader(InstallerWindow.class)
           .getResourceAsImage("images/application_big.png"));
    text = LangResource.getString("Applications");
    comment = LangResource.getString("Manages_third_party");
    this.actions = new ApplicationActions(this);
    layoutFullHeightContent();
  }
  /**
   * Get a reference to content tree
   */
  public KTreeTable getContentTree()
  {
    return initContentTree(Workspace.getInstallEngine().getApplicationModel());
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
                 createButtonFromAction(ApplicationActions.launchAction, false);
          ctoolbar.add(b);
          ApplicationActions.launchAction.
                addPropertyChangeListener(gui.createActionChangeListener(b));
          ctoolbar.addSeparator();

          b = WorkspaceUtils.
              createButtonFromAction(ApplicationActions.addFolderAction, false);
          ctoolbar.add(b);
          ApplicationActions.addFolderAction.
                addPropertyChangeListener(gui.createActionChangeListener(b));

          b = WorkspaceUtils.
                 createButtonFromAction(ApplicationActions.addAction, true);
          ctoolbar.add(b);
          ApplicationActions.addAction.
                addPropertyChangeListener(gui.createActionChangeListener(b));

          b = WorkspaceUtils.
                 createButtonFromAction(ApplicationActions.editAction, false);
          ctoolbar.add(b);
          ApplicationActions.editAction.
                addPropertyChangeListener(gui.createActionChangeListener(b));

          b = WorkspaceUtils.
                createButtonFromAction(ApplicationActions.deleteAction, false);
          ctoolbar.add(b);
          ApplicationActions.deleteAction.
                addPropertyChangeListener(gui.createActionChangeListener(b));
        }
     }
     return ctoolbar;
  }
  /**
   * Installs new application with parent node
   * as argument.
   */
  public void add()
  {
    if (currentNode == null)
    {
      return;
    }
    Application app = null;

    ImageIcon icon = new ImageIcon( Workspace.getResourceManager().
               getImage("application_big.png") );
    String name = (String) JOptionPane.showInputDialog(
          this,
          LangResource.getString("message#175"),
          LangResource.getString("New Application Configuration File"),
           JOptionPane.QUESTION_MESSAGE,
           icon , null, null);

    if(!validateInstName(currentNode, name, "application"))
    {
       return;
    }

    app = new Application(currentNode, name);

    ApplicationDialog app_dlg =
       new ApplicationDialog(Workspace.getUI().getFrame());

    app_dlg.setData(app);
    app_dlg.setVisible(true);

    if(!app_dlg.isCancelled())
    {
      try
      {
        app.save();
        currentNode.add(app);
      }
      catch(IOException ex)
      {
        Workspace.getLogger().warning(LangResource.getString("message#165")
                   + ex.getMessage());
        JOptionPane.showMessageDialog(this,LangResource.getString("message#171")
                   + ex.getMessage());
      }
    }
  }
  /**
   * Launch current application.
   */
  public void run()
  {
    if (currentNode != null)
    {
      Workspace.getRuntimeManager().run(currentNode.getLinkString());
    }
    else
    {
      ApplicationChooserDialog dlg =
              new ApplicationChooserDialog(Workspace.getUI().getFrame());
      dlg.setVisible(true);
      if (dlg.getSelectedApplication() != null)
      {
        Workspace.getRuntimeManager().
             run(dlg.getSelectedApplication().getLinkString());
      }
    }
  }
  /**
   * Edit application.
   */
  public void edit()
  {
    if (currentNode == null || !(currentNode instanceof Application))
    {
      return;
    }
    Application app = (Application) currentNode;

    ApplicationDialog app_dlg =
       new ApplicationDialog(Workspace.getUI().getFrame());

    app_dlg.setData(app);
    app_dlg.setVisible(true);

    if(!app_dlg.isCancelled())
    {
      try
      {
        app.save();
        String source = null;
        source = (app).getSource();
        jarInspector.setSource(source);
      }
      catch(IOException ex)
      {
        Workspace.getLogger().warning(LangResource.getString("message#165")
                   + ex.getMessage());
        JOptionPane.showMessageDialog
          (this,LangResource.getString("Unable_to_save") + ex.getMessage());
      }
     }
  }
}
