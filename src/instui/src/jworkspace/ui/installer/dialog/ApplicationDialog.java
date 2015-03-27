package jworkspace.ui.installer.dialog;

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
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
*/
import java.awt.*;
import javax.swing.*;
import java.util.ResourceBundle;

import jworkspace.installer.Application;
import jworkspace.kernel.Workspace;
import jworkspace.ui.installer.*;

import kiwi.ui.*;
import kiwi.ui.dialog.*;
import kiwi.util.*;
/**
 * Application configuration dialog
 */
public class ApplicationDialog extends ComponentDialog
{

  private ApplicationPanel panel;

  public ApplicationDialog(Frame parent)
  {
    super(parent, LangResource.getString("Application"), true);
    setResizable(false);
  }
  protected boolean accept()
  {
    return (panel.syncData());
  }
  protected JComponent buildDialogUI()
  {
    setComment(null);
    commentLabel.setVerticalAlignment(SwingConstants.CENTER);
    commentLabel.setHorizontalAlignment(SwingConstants.LEFT);
    commentLabel.setOpaque(true);
    commentLabel.setBackground(new Color(229, 228, 224));
    setTopIcon(new ImageIcon(new ResourceLoader(InstallerWindow.class)
                    .getResourceAsImage("images/application_header.gif")));
    panel = new ApplicationPanel();
    return (panel);
  }
  public void setData(Application data)
  {
    panel.setData(data);
  }
}
