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

import kiwi.util.*;
import kiwi.ui.dialog.*;
/**
 * Library configuration dialog
 */
public class LibraryDialog extends ComponentDialog
{
  private LibraryPanel panel;
  public LibraryDialog(Frame parent)
  {
    super(parent, LangResource.getString("message#183"), true);
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
    commentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    commentLabel.setOpaque(true);
    commentLabel.setBackground(new Color(178, 165, 162));
    Image dimage = new ResourceLoader(InstallerWindow.class)
         .getResourceAsImage("images/lib_header.png");
    setTopIcon(new ImageIcon(dimage));
    panel = new LibraryPanel();
    return(panel);
  }
  public void setData(jworkspace.installer.DefinitionNode data)
  {
    panel.setData(data);
  }
}
