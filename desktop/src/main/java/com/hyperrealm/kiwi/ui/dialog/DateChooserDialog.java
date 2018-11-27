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
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.*;
import com.hyperrealm.kiwi.util.*;

/** A dialog window that displays a <code>DateChooser</code>.
 *
 * <p><center>
 * <img src="snapshot/DateChooserDialog.gif"><br>
 * <i>An example DateChooserDialog.</i>
 * </center>
 *
 * @see com.hyperrealm.kiwi.ui.DateChooser
 *
 * @author Mark Lindner
 */

public class DateChooserDialog extends ComponentDialog
{
  private DateChooser cal;
  private Calendar v_date = null;

  /** Construct a new <code>DateChooserDialog</code> with a default title.
   *
   * @param parent The parent window for the dialog.
   * @param modal A flag specifying whether this dialog will be modal.
   */

  public DateChooserDialog(Frame parent, boolean modal)
  {
    this(parent, "", modal);
  }

  /** Construct a new <code>DateChooserDialog</code> with a default title.
   *
   * @param parent The parent window for the dialog.
   * @param modal A flag specifying whether this dialog will be modal.
   *
   * @since Kiwi 1.4
   */

  public DateChooserDialog(Dialog parent, boolean modal)
  {
    this(parent, "", modal);
  }
  
  /** Construct a new <code>DateChooserDialog</code>.
   *
   * @param parent The parent window for the dialog.
   * @param title The title for the dialog.
   * @param modal A flag specifying whether this dialog will be modal.
   */

  public DateChooserDialog(Frame parent, String title, boolean modal)
  {
    super(parent, title, modal);
    setResizable(false);
  }

  /** Construct a new <code>DateChooserDialog</code>.
   *
   * @param parent The parent window for the dialog.
   * @param title The title for the dialog.
   * @param modal A flag specifying whether this dialog will be modal.
   *
   * @since Kiwi 1.4
   */

  public DateChooserDialog(Dialog parent, String title, boolean modal)
  {
    super(parent, title, modal);
    setResizable(false);
  }
  
  /** Build the dialog user interface. */

  protected Component buildDialogUI()
  {
    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
    
    setComment(loc.getMessage("kiwi.dialog.prompt.date_select"));
    if(getTitle().length() == 0)
      setTitle(loc.getMessage("kiwi.dialog.title.date_select"));

    return(cal = new DateChooser());
  }

  /** Show or hide the dialog. */

  public void setVisible(boolean flag)
  {
    if(flag)
      v_date = null;
    super.setVisible(flag);
  }

  /** Accept the input. Always returns <code>true</code>. */

  protected boolean accept()
  {
    v_date = cal.getSelectedDate();
    return(true);
  }

  /** Get the selected date.
   *
   * @return The date selected, as a <code>Calendar</code> object, or
   * <code>null</code> if the dialog was cancelled.
   */

  public Calendar getDate()
  {
    return(v_date);
  }

  /** Set the selected date.
   *
   * @param date The new date.
   */
  
  public void setDate(Calendar date)
  {
    cal.setSelectedDate(date);
  }

  /** Get the latest selectable date for the chooser.
   *
   * @return  The maximum selectable date, or <code>null</code> if there is no
   * maximum date currently set.
   */
  
  public Calendar getMaximumDate()
  {
    return(cal.getMaximumDate());
  }
  
  /** Set the latest selectable date for the chooser.
   *
   * @param date The (possibly <code>null</code>) maximum selectable date.
   */
   
  public void setMaximumDate(Calendar date)
  {
    cal.setMaximumDate(date);
  }

  /** Get the earliest selectable date for the chooser.
   *
   * @return  The minimum selectable date, or <code>null</code> if there is no
   * minimum date currently set.
   */
  
  public Calendar getMinimumDate()
  {
    return(cal.getMinimumDate());
  }
  
  /** Set the earliest selectable date for the chooser.
   *
   * @param date The (possibly <code>null</code>) minimum selectable date.
   */
  
  public void setMinimumDate(Calendar date)
  {
    cal.setMinimumDate(date);
  }

  /** Set the size of date cells in the calendar pane.
   *
   * @param size The width and height, in pixels, of a cell.
   *
   * @since Kiwi 2.0
   */

  public void setCellSize(int size)
  {
    cal.setCellSize(size);
    pack();
  }
  
}

/* end of source file */
