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

import com.hyperrealm.kiwi.util.ResourceLoader;
import jworkspace.ui.installer.*;


public class ApplicationActions extends WorkbenchActions
{
 /**
  * Launch action name
  */
  public static final String launchActionName  = "Launch";
 /**
  * Launch action
  */
  public static Action launchAction;
  protected class LaunchAction extends AbstractAction
  {
    public LaunchAction ()
    {
      super(launchActionName);
      putValue(Action.SMALL_ICON, new ImageIcon(new ResourceLoader(InstallerWindow.class)
                          .getResourceAsImage("images/go.png")));
      putValue(Action.SHORT_DESCRIPTION, "Launch");
    }
    public void actionPerformed (ActionEvent evt)
    {
      ((ApplicationWorkbench)wb).run();
    }
  }
  public ApplicationActions(ApplicationWorkbench apw)
  {
    super(apw);
  }
 /**
  * Create actions
  */
  protected Hashtable createActions()
  {
    actions = super.createActions();

    launchAction = new LaunchAction();
    actions.put(launchActionName, launchAction);

    return actions;
  }
}