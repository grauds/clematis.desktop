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
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/
import java.awt.*;
import javax.swing.*;

import com.hyperrealm.kiwi.util.ResourceLoader;
import jworkspace.ui.installer.*;
import jworkspace.installer.DefinitionNode;

import kiwi.ui.dialog.*;
/**
 * JVM configuration dialog
 */
public class JvmDialog extends ComponentDialog
{
  private JvmPanel panel;
  public JvmDialog(Frame parent)
  {
    super(parent, LangResource.getString("message#163"), true);
    setResizable(false);
  }
  protected boolean accept()
  {
    return(panel.syncData());
  }
  protected JComponent buildDialogUI()
  {
    setComment(null);
    commentLabel.setVerticalAlignment(SwingConstants.CENTER);
    commentLabel.setHorizontalAlignment(SwingConstants.CENTER);
    commentLabel.setOpaque(true);

    Image dimage = new ResourceLoader(InstallerWindow.class)
         .getResourceAsImage("images/jvm_header.png");
    setTopIcon(new ImageIcon(dimage));
    panel= new JvmPanel();
    return (panel);
  }
  public void setData(DefinitionNode data)
  {
    panel.setData(data);
  }
}
