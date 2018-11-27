package jworkspace.ui.installer.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin

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

   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/
import java.awt.*;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.KPanel;
import jworkspace.ui.installer.*;
import jworkspace.ui.widgets.*;

import kiwi.ui.*;
import kiwi.ui.dialog.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * This panel offers a choice to close files in tabbed viewer
 */
public class CloseFilesDialog extends ComponentDialog implements ActionListener
{
  /**
   * Check list with files names
   */
  protected CheckList ch_list;
  /**
   * Select all button
   */
  protected KButton selectAll;
  /**
   * Select none button
   */
  protected KButton selectNone;
  public static final String SELECT_ALL = "SELECT_ALL";
  public static final String SELECT_NONE = "SELECT_NONE";
  /**
   * Public dialog constructor
   */
  public CloseFilesDialog(Frame parent, String[] fileNames)
  {
    super(parent, LangResource.getString("Close_Files"), true);
    getCheckList().setListData(fileNames, new int[0]);
  }
  /**
   * Create and return check list
   */
  public CheckList getCheckList()
  {
    if (ch_list == null)
    {
        ch_list = new CheckList();
        ch_list.setMinimumSize(new Dimension(150, 200));
    }
    return ch_list;
  }
  /**
   * Create and return select all button
   */
  public KButton getSelectAllButton()
  {
    if (selectAll == null)
    {
        selectAll = new KButton(LangResource.getString("All"));
        selectAll.setActionCommand(CloseFilesDialog.SELECT_ALL);
        selectAll.addActionListener(this);
    }
    return selectAll;
  }
  /**
   * Create and return select none button
   */
  public KButton getSelectNoneButton()
  {
    if (selectNone == null)
    {
        selectNone = new KButton(LangResource.getString("None"));
        selectNone.setActionCommand(CloseFilesDialog.SELECT_NONE);
        selectNone.addActionListener(this);
    }
    return selectNone;
  }
  /**
   * Build dialog ui
   */
  protected JComponent buildDialogUI()
  {
     setComment(null);
     KPanel holder = new KPanel();
     holder.setLayout(new BorderLayout());
     holder.add(getCheckList(), BorderLayout.CENTER);
     addButton(getSelectAllButton());
     addButton(getSelectNoneButton());
     return holder;
  }
  /**
   * Return selected entries
   */
  public String[] getSelectedListData()
  {
    return ch_list.getSelectedListData();
  }
  public void actionPerformed(ActionEvent e)
  {
    String command = e.getActionCommand();
    if (command.equals(CloseFilesDialog.SELECT_ALL))
    {
       ch_list.selectAll();
    }
    else if (command.equals(CloseFilesDialog.SELECT_NONE))
    {
       ch_list.clearAll();
    }
  }
}