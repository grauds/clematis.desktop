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

package com.hyperrealm.kiwi.ui.dialog;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.ui.*;
import com.hyperrealm.kiwi.util.*;

/** A dialog that presents a list of items, one of which must be selected
 * before the dialog may be dismissed. An item may be selected either by
 * single-clicking on the item and then clicking on the <i>OK</i> button,
 * or by double-clicking on the item.
 *
 * <p><center>
 * <img src="snapshot/ItemChooserDialog.gif"><br>
 * <i>An example ItemChooserDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class ItemChooserDialog<T> extends ComponentDialog
{
  /** The <code>JList</code> used by this dialog. */
  protected JList list;
  private DefaultListModel model;
  private String selectError;
  private ListItemMouseAdapter mouseAdapter;

  /** Construct a new <code>ItemChooserDialog</code>.
   *
   * @param parent The parent window for this dialog.
   * @param title The title for the dialog.
   * @param comment The comment string for this dialog.
   */
  
  public ItemChooserDialog(Frame parent, String title, String comment)
  {
    super(parent, title, true);
    _init(comment);
  }

  /** Construct a new <code>ItemChooserDialog</code>.
   *
   * @param parent The parent window for this dialog.
   * @param title The title for the dialog.
   * @param comment The comment string for this dialog.
   *
   * @since Kiwi 1.4
   */
  
  public ItemChooserDialog(Dialog parent, String title, String comment)
  {
    super(parent, title, true);
    _init(comment);
  }
  
  /*
   */

  private void _init(String comment)
  {
    setComment(comment);

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
    selectError = loc.getMessage("kiwi.dialog.message.select_item");
  }

  /** Build the dialog UI. */
  
  protected Component buildDialogUI()
  {
    list = new JList();
    model = new DefaultListModel();
    list.setModel(model);

    mouseAdapter = new ListItemMouseAdapter()
      {
        public void itemDoubleClicked(int item, int button)
        {
          doAccept();
        }
      };

    list.addMouseListener(mouseAdapter);

    return(new KScrollPane(list));
  }

  /** Accept the input.
   *
   * @return <code>true</code> if an item is selected in the list,
   * <code>false</code> otherwise.
   */
  
  protected boolean accept()
  {
    if(list.getSelectedIndex() < 0)
    {
      DialogSet.getInstance().showMessageDialog(selectError);
      return(false);
    }

    else
      return(true);    
  }

  /** Set the list of items to be displayed by this dialog.
   *
   * @param items The items.
   *
   * @since Kiwi 1.4
   */

  public void setItems(Iterator<T> items)
  {
    model.clear();

    while(items.hasNext())
      model.addElement(items.next());

    list.setSelectedIndex(0);
  }
    
  /** Get the currently selected object in the list.
   *
   * @return The selected item, or <code>null</code> if no item is currently
   * selected.
   */
  
  public T getSelectedItem()
  {
    return((T)list.getSelectedValue());
  }
  
}

/* end of source file */
