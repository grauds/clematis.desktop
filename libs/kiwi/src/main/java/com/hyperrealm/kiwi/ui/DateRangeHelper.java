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
import java.util.*;

/** A helper class for coordinating two <code>DateChooser</code>s for the
 * purpose of entering a date range. <code>DateRangeHelper</code> constrains
 * two <code>DateChooser</code>s in such a way that only a valid date range
 * may be selected using the two choosers. To this end, an end date must be
 * entered that is on or after the start date. The date range selection can
 * be further constrained by specifing a minimum and maximum date; in this
 * case input is limited so that the selected date range will fall within
 * the constrained range.
 * 
 * @author Mark Lindner
 */

public class DateRangeHelper implements ActionListener
{
  private DateChooser startChooser, endChooser;

  /** Construct a new <code>DateRangeHelper</code> for the specified start and
   * end date choosers.
   *
   * @param startChooser The <code>DateChooser</code> for selecting a start
   * date.
   * @param endChooser The <code>DateChooser</code> for selecting an end
   * date.
   */
  
  public DateRangeHelper(DateChooser startChooser, DateChooser endChooser)
  {
    this.startChooser = startChooser;
    this.endChooser = endChooser;

    startChooser.addActionListener(this);
    endChooser.addActionListener(this);
  }

  /** Set the minimum selectable date for this date range.
   *
   * @param date The minimum date.
   */
  
  public void setMinimumDate(Calendar date)
  {
    startChooser.setMinimumDate(date);
    endChooser.setMinimumDate(date);
  }

  /** Set the maximum selectable date for this date range.
   *
   * @param date The maximum date.
   */

  public void setMaximumDate(Calendar date)
  {
    startChooser.setMaximumDate(date);
    endChooser.setMaximumDate(date);
  }

  /** Get the currently selected start date.
   *
   * @return The start date.
   */
  
  public Calendar getStartDate()
  {
    return(startChooser.getSelectedDate());
  }

  /** Get the currently selected end date.
   *
   * @return The end date.
   */
  
  public Calendar getEndDate()
  {
    return(endChooser.getSelectedDate());
  }

  /** Set the start date.
   *
   * @param date The new end date.
   */
  
  public void setStartDate(Calendar date)
  {
    startChooser.setSelectedDate(date);
  }

  /** Set the end date.
   *
   * @param date The new end date.
   */
  
  public void setEndDate(Calendar date)
  {
    endChooser.setSelectedDate(date);
  }

  /** Handle events. This method is public as an implementation side-effect. */
  
  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();

    if(o == startChooser)
      endChooser.setMinimumDate(startChooser.getSelectedDate());

    else if(o == endChooser)
      startChooser.setMaximumDate(endChooser.getSelectedDate());
  }   

}

/* end of source file */
