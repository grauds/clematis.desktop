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

package com.hyperrealm.kiwi.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.text.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.util.*;

/** A date entry component which consists of a combination of a
 * <code>DateField</code> and a <code>DateChooser</code> in a popup.
 * The popup is activated by clicking on the button to the right of the input
 * field.
 *
 * <p><center>
 * <img src="snapshot/DateChooserField.gif"><br>
 * <i>An example DateChooserField.</i>
 * <p>
 * <img src="snapshot/DateChooserField_popup.gif"><br>
 * <i>An example DateChooserField with the popup activated.</i>
 * </center>
 *
 * @author Mark Lindner
 *
 * @since Kiwi 1.4
 *
 * @see com.hyperrealm.kiwi.ui.DateField
 * @see com.hyperrealm.kiwi.ui.DateChooser
 * @see com.hyperrealm.kiwi.ui.dialog.DateChooserDialog
 */

public class DateChooserField extends KPanel
{
  private SimpleDateFormat dateFormat;
  /** */
  protected DateField t_date;
  /** */
  protected JButton b_chooser;
  private JPopupMenu menu;
  /** */
  protected DateChooser chooser;

  /** Construct a <code>DateChooserField</code> object using the
   * default date format.
   */

  public DateChooserField()
  {
    this(null);
  }

  /** Construct a <code>DateChooserField</code> with the specified width,
   * using the default date format.
   *
   * @param width The width for the field.
   */

  public DateChooserField(int width)
  {
    this(width, null);
  }

  /** Construct a <code>DateChooserField</code> with the default width,
   * using the specified date format.
   *
   * @param format The format to display the date in.
   */
  
  public DateChooserField(String format)
  {
    this(10, format);
  }
  
  /** Construct a <code>DateChooserField</code> with the specified width and
   * date format.
   *
   * @param width The width for the field.
   * @param format The format to display the date in.
   */

  public DateChooserField(int width, String format)
  {  
    setLayout(new BorderLayout(2, 2));

    LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");
    
    t_date = new DateField(width, format);
    t_date.setOpaque(false);
    add("Center", t_date);
    
    b_chooser = new KButton(KiwiUtils.getResourceManager()
                            .getIcon("calendar_date.png"));
    b_chooser.setToolTipText(loc.getMessage("kiwi.tooltip.select_date"));
    add("East", b_chooser);
    
    menu = new JPopupMenu();
    menu.setOpaque(false);
    chooser = new DateChooser();
    chooser.setOpaque(false);

    KPanel p_popup = new KPanel(UIChangeManager.getInstance()
                                .getDefaultTexture());
    p_popup.setLayout(new GridLayout(1, 0));
    p_popup.setBorder(KiwiUtils.defaultBorder);
    p_popup.add(chooser);
    menu.add(p_popup);

    b_chooser.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          Date d = t_date.getDate();
          if(d != null)
          {
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            chooser.setSelectedDate(c);
          }
          
          menu.show(b_chooser, 0, 0);
        }
      });
    
    chooser.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          if(evt.getActionCommand().equals(DateChooser.DATE_CHANGE_CMD))
          {
            Date d = chooser.getSelectedDate().getTime();
            t_date.setDate(d);
            menu.setVisible(false);
          }
        }
      });
  }
  
  /**
   * Get the date that is currently displayed in the field.
   *
   * @return The date.
   */
  
  public Date getDate()
  {
    return(t_date.getDate());
  }

  /**
   * Set the date to be displayed in the field.
   *
   * @param date The new date.
   */

  public void setDate(Date date)
  {
    t_date.setDate(date);
  }
  
}

/* end of source file */
