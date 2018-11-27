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
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import com.hyperrealm.kiwi.event.*;
import com.hyperrealm.kiwi.util.*;

/** This class represents a date chooser. The chooser allows an arbitrary date
 * to be selected by presenting a calendar with day, month and year selectors.
 * The range of selectable dates may be constrained by supplying a minimum
 * and/or maximum selectable date. The date chooser is fully locale-aware.
 *
 * <p><center>
 * <img src="snapshot/DateChooser.gif"><br>
 * <i>An example DateChooser.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class DateChooser extends KPanel implements ActionListener
{
  private KLabel l_date, l_year, l_month;
  private KButton b_lyear, b_ryear, b_lmonth, b_rmonth;
  private SimpleDateFormat datefmt = new SimpleDateFormat("E  d MMM yyyy");
  private Calendar selectedDate = null, minDate = null, maxDate = null;
  private int selectedDay, firstDay, minDay = -1, maxDay = -1, firstCell = 0;
  private JToggleButton b_days[];
  private KLabel l_days[];
  private static final int[] daysInMonth
    = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  private static final int[] daysInMonthLeap
    = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  private String months[], labels[] = new String[7];
  private static final Color weekendColor = Color.red.darker();
  private boolean clipMin = false, clipMax = false, clipAllMin = false,
    clipAllMax = false;
  private int weekendCols[] = { 0, 0 };
  private ActionSupport asupport;
  private int fontHeight;
  private ButtonGroup group;
  private static final Icon i_left
    = KiwiUtils.getResourceManager().getIcon("repeater_left.png");
  private static final Icon i_right
    = KiwiUtils.getResourceManager().getIcon("repeater_right.png");  
  
  /** The default cell size. */
  public static final int CELL_SIZE = 30;
  /** <i>Date changed</i> event command. */
  public static final String DATE_CHANGE_CMD = "dateChanged"; 
  /** <i>Month changed</i> event command. */
  public static final String MONTH_CHANGE_CMD = "monthChanged"; 
  /** <i>Year changed</i> event command. */
  public static final String YEAR_CHANGE_CMD = "yearChanged";
  
  /** Construct a new <code>DateChooser</code>. The selection will be
   * initialized to the current date.
   */
  
  public DateChooser()
  {
    this(Calendar.getInstance());
  }

  /** Construct a new <code>DateChooser</code> with the specified selected
   * date.
   *
   * @param date The date for the selection.
   */
  
  public DateChooser(Calendar date)
  {
    l_days = new KLabel[7];

    int fd = date.getFirstDayOfWeek();
    DateFormatSymbols sym = LocaleManager.getDefault().getDateFormatSymbols();
    months = sym.getShortMonths();
    String wkd[] = sym.getShortWeekdays();
    
    for(int i = 0, ii = fd; i < l_days.length; i++)
    {
      int l = Math.min(wkd[ii].length(), 2);
      l_days[i] = new KLabel(wkd[ii].substring(0, l));
      l_days[i].setHorizontalAlignment(SwingConstants.CENTER);
      l_days[i].setVerticalAlignment(SwingConstants.BOTTOM);

      if((ii == Calendar.SATURDAY) || (ii == Calendar.SUNDAY))
        l_days[i].setForeground(weekendColor);

      if(++ii > 7)
        ii = 1;
    }
    
    group = new ButtonGroup();
    b_days = new JToggleButton[6 * 7];

    Insets cellMargin = new Insets(2, 2, 2, 2);
    
    for(int i = 0, ii = fd; i < b_days.length; i++)
    {
      b_days[i] = new JToggleButton();
      b_days[i].setOpaque(false);
      b_days[i].setMargin(cellMargin);
      // b_days[i].setFocusPainted(false);
      b_days[i].setHorizontalAlignment(SwingConstants.RIGHT);
      b_days[i].setVerticalAlignment(SwingConstants.TOP);
      if((ii == Calendar.SATURDAY) || (ii == Calendar.SUNDAY))
        b_days[i].setForeground(weekendColor);

      Dimension dim = new Dimension(CELL_SIZE, CELL_SIZE);
      b_days[i].setSize(dim);
      b_days[i].setMinimumSize(dim);
      b_days[i].setPreferredSize(dim);

      b_days[i].addActionListener(this);
      
      group.add(b_days[i]);

      if(++ii > 7)
        ii = 1;
    }

    asupport = new ActionSupport(this);

    setLayout(new BorderLayout(5, 5));

    KPanel top = new KPanel();
    top.setLayout(new BorderLayout(0, 0));

    KPanel p1 = new KPanel();
    p1.setLayout(new FlowLayout(FlowLayout.LEFT));
    top.add("West", p1);
    
    b_lmonth = new KButton(i_left);
    b_lmonth.setMargin(KiwiUtils.emptyInsets);
    b_lmonth.setFocusPainted(false);
    b_lmonth.setOpaque(false);
    b_lmonth.addActionListener(this);
    p1.add(b_lmonth);

    l_month = new KLabel();
    p1.add(l_month);

    b_rmonth = new KButton(i_right);
    b_rmonth.setMargin(KiwiUtils.emptyInsets);
    b_rmonth.setFocusPainted(false);
    b_rmonth.setOpaque(false);
    b_rmonth.addActionListener(this);
    p1.add(b_rmonth);

    KPanel p2 = new KPanel();
    p2.setLayout(new FlowLayout(FlowLayout.LEFT));
    top.add("East" ,p2);
    
    b_lyear = new KButton(i_left);
    b_lyear.setMargin(KiwiUtils.emptyInsets);
    b_lyear.setFocusPainted(false);
    b_lyear.setOpaque(false);
    b_lyear.addActionListener(this);
    p2.add(b_lyear);

    l_year = new KLabel();
    p2.add(l_year);

    b_ryear = new KButton(i_right);
    b_ryear.setMargin(KiwiUtils.emptyInsets);
    b_ryear.setFocusPainted(false);
    b_ryear.setOpaque(false);
    b_ryear.addActionListener(this);
    p2.add(b_ryear);

    add("North", top);

    KPanel p_grid = new KPanel();
    p_grid.setLayout(new BorderLayout(0, 0));

    KPanel p_headings = new KPanel();
    p_headings.setLayout(new GridLayout(0, 7, 1, 1));
    
    for(int i = 0; i < l_days.length; i++)
      p_headings.add(l_days[i]);

    p_grid.add("North", p_headings);

    KPanel p_days = new KPanel();
    p_days.setLayout(new GridLayout(0, 7, 1, 1));
    
    for(int i = 0; i < b_days.length; i++)
      p_days.add(b_days[i]);

    p_grid.add("Center", p_days);

    add("Center", p_grid);

    l_date = new KLabel("Date", SwingConstants.CENTER);
    add("South", l_date);

    Font f = getFont();
    super.setFont(new Font(f.getName(), Font.BOLD, f.getSize()));

    setSelectedDate(date);
  }
  
  /** Get a copy of the <code>Calendar</code> object that represents the
   * currently selected date.
   *
   * @return The currently selected date.
   */
  
  public Calendar getSelectedDate()
  {
    return((Calendar)selectedDate.clone());
  }
  
  /**
   * Set the selected date for the chooser.
   *
   * @param date The date to select.
   */

  public void setSelectedDate(Calendar date)
  {
    selectedDate = copyDate(date, selectedDate);
    selectedDay = selectedDate.get(Calendar.DAY_OF_MONTH);

    _refresh();
  }

  /** Set the earliest selectable date for the chooser.
   *
   * @param date The (possibly <code>null</code>) minimum selectable date.
   */
  
  public void setMinimumDate(Calendar date)
  {
    minDate = ((date == null) ? null : copyDate(date, minDate));
    minDay = ((date == null) ? -1 : minDate.get(Calendar.DATE));
    
    _refresh();
  }

  /** Get the earliest selectable date for the chooser.
   *
   * @return The minimum selectable date, or <code>null</code> if there is no
   * minimum date currently set.
   */
  
  public Calendar getMinimumDate()
  {
    return(minDate);
  }

  /** Set the latest selectable date for the chooser.
   *
   * @param date The (possibly <code>null</code>) maximum selectable date.
   */
  
  public void setMaximumDate(Calendar date)
  {
    maxDate = ((date == null) ? null : copyDate(date, maxDate));
    maxDay = ((date == null) ? -1 : maxDate.get(Calendar.DATE));

    _refresh();
  }

  /** Get the latest selectable date for the chooser.
   *
   * @return The maximum selectable date, or <code>null</code> if there is no
   * maximum date currently set.
   */
  
  public Calendar getMaximumDate()
  {
    return(maxDate);
  }
  
  /**
   * Set the format for the textual date display at the bottom of the
   * component.
   *
   * @param format The new date format to use.
   */
  
  public void setDateFormat(SimpleDateFormat format)
  {
    datefmt = format;

    _refreshDate();
  }

  /** Handle events. This method is public as an implementation side-effect. */
  
  public void actionPerformed(ActionEvent evt)
  {
    Object o = evt.getSource();

    if(o instanceof JToggleButton)
    {
      for(int i = 0; i < b_days.length; i++)
      {
        if(b_days[i].isSelected())
        {
          selectedDay = i - firstCell + 1;
          selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);
          asupport.fireActionEvent(DATE_CHANGE_CMD);
          
          break;
        }
      }

      _refreshDate();

      return;
    }
    
    if(o == b_lmonth)
      selectedDate.add(Calendar.MONTH, -1);

    else if(o == b_rmonth)
      selectedDate.add(Calendar.MONTH, 1);

    else if(o == b_lyear)
    {
      selectedDate.add(Calendar.YEAR, -1);
      if(minDate != null)
      {
        int m = minDate.get(Calendar.MONTH);
        if(selectedDate.get(Calendar.MONTH) < m)
          selectedDate.set(Calendar.MONTH, m);
      }
    }

    else if(o == b_ryear)
    {
      selectedDate.add(Calendar.YEAR, 1);
      if(maxDate != null)
      {
        int m = maxDate.get(Calendar.MONTH);
        if(selectedDate.get(Calendar.MONTH) > m)
          selectedDate.set(Calendar.MONTH, m);
      }
    }

    selectedDay = 1;
    selectedDate.set(Calendar.DATE, selectedDay);
    _refresh();
    b_days[selectedDay + firstCell - 1].setSelected(true);

    asupport.fireActionEvent(((o == b_lmonth) || (o == b_rmonth))
                             ? MONTH_CHANGE_CMD : YEAR_CHANGE_CMD);
  }

  /* Determine what day of week the first day of the month falls on. It's too
   * bad we have to resort to this hack; the Java API provides no means of
   * doing this any other way.
   */

  private void _computeFirstDay()
  {
    int d = selectedDate.get(Calendar.DAY_OF_MONTH);
    selectedDate.set(Calendar.DAY_OF_MONTH, 1);
    firstDay = selectedDate.get(Calendar.DAY_OF_WEEK);
    selectedDate.set(Calendar.DAY_OF_MONTH, d);
  }

  private void _refreshDate()
  {
    l_date.setText(datefmt.format(selectedDate.getTime()));
  }
  
  /* This method is called whenever the month or year changes. Its job
   * is to reconfigure the grid and determine whether any selection
   * range limits have been reached.
   */
  
  private void _refresh()
  {
    l_year.setText(String.valueOf(selectedDate.get(Calendar.YEAR)));
    l_month.setText(months[selectedDate.get(Calendar.MONTH)]);

    _computeFirstDay();
    clipMin = clipMax = clipAllMin = clipAllMax = false;

    b_lyear.setEnabled(true);
    b_ryear.setEnabled(true);
    b_lmonth.setEnabled(true);
    b_rmonth.setEnabled(true);
    
    // Disable anything that would cause the date to go out of range. This
    // logic is extremely sensitive so be very careful when making changes.
    // Every condition test in here is necessary, so don't remove anything.

    if(minDate != null)
    {
      int y = selectedDate.get(Calendar.YEAR);
      int y0 = minDate.get(Calendar.YEAR);
      int m = selectedDate.get(Calendar.MONTH);
      int m0 = minDate.get(Calendar.MONTH);

      b_lyear.setEnabled(y > y0);
      if(y == y0)
      {
        b_lmonth.setEnabled(m > m0);

        if(m == m0)
        {
          clipMin = true;
          int d0 = minDate.get(Calendar.DATE);
          
          if(selectedDay < d0)
            selectedDate.set(Calendar.DATE, selectedDay = d0);

          // allow out-of-range selection
          // selectedDate.set(Calendar.DATE, selectedDay);
        }
      }
 
      // clip if years same but month earlier, OR if year is earlier.
      // fix submitted by Robert Heitzmann.
      clipAllMin = (((y == y0) && (m < m0)) || (y < y0));
    }

    if(maxDate != null)
    {
      int y = selectedDate.get(Calendar.YEAR);
      int y1 = maxDate.get(Calendar.YEAR);
      int m = selectedDate.get(Calendar.MONTH);
      int m1 = maxDate.get(Calendar.MONTH);

      b_ryear.setEnabled(y < y1);
      if(y == y1)
      {
        b_rmonth.setEnabled(m < m1);
        if(m == m1)
        {
          clipMax = true;
          int d1 = maxDate.get(Calendar.DATE);
          if(selectedDay > d1)
            selectedDate.set(Calendar.DATE, selectedDay = d1);

          // allow out-of-range selection
          // selectedDate.set(Calendar.DATE, selectedDay);          
        }
      }

      // clip if years same but month later, OR if year is later.
      // fix submitted by Robert Heitzmann.

      clipAllMax = (((y == y1) && (m > m1)) || (y > y1));
    }

    // update the grid buttons

    firstCell = ((firstDay - selectedDate.getFirstDayOfWeek() + 7) % 7);

    // find out how many days there are in the current month
    
    int month = DateChooser.this.selectedDate.get(Calendar.MONTH);
    int dmax = (isLeapYear(DateChooser.this.selectedDate.get(Calendar.YEAR))
                ? daysInMonthLeap[month] : daysInMonth[month]);
    
    for(int c = 0, ii = 0; c < b_days.length; c++)
    {
      if((c < firstCell) || (ii == dmax))
      {
        b_days[c].setText(null);
        b_days[c].setVisible(false);
      }
      else
      {
        b_days[c].setText(String.valueOf(++ii));
        b_days[c].setVisible(true);

        int d = c - firstCell + 1;

        boolean disabled = ((clipMin && (d < minDay))
                            || (clipMax && (d > maxDay))
                            || clipAllMin || clipAllMax);

        b_days[c].setEnabled(! disabled);
      }
    }

    _refreshDate();
  }

  /** Determine if a year is a leap year.
   *
   * @param year The year to check.
   * @return <code>true</code> if the year is a leap year, and
   * <code>false</code> otherwise.
   */
  
  public static boolean isLeapYear(int year)
  {
    return((((year % 4) == 0) && ((year % 100) != 0)) || ((year % 400) == 0));
  }
    
  /* Copy the relevant portions of a date. */
  
  private Calendar copyDate(Calendar source, Calendar dest)
  {
    if(dest == null)
      dest = Calendar.getInstance();
    
    dest.set(Calendar.YEAR, source.get(Calendar.YEAR));
    dest.set(Calendar.MONTH, source.get(Calendar.MONTH));
    dest.set(Calendar.DATE, source.get(Calendar.DATE));

    return(dest);
  }

  /** Add a <code>ActionListener</code> to this component's list of listeners.
   *
   * @param listener The listener to add.
   */  
  
  public void addActionListener(ActionListener listener)
  {
    asupport.addActionListener(listener);
  }

  /** Remove a <code>ActionListener</code> from this component's list of
   * listeners.
   *
   * @param listener The listener to remove.
   */

  public void removeActionListener(ActionListener listener)
  {
    asupport.removeActionListener(listener);
  }

  /** Set the size of date cells in the calendar pane.
   *
   * @param cellSize The width and height, in pixels, of a cell.
   *
   * @since Kiwi 2.0
   */
  
  public void setCellSize(int cellSize)
  {
    Dimension dim = new Dimension(cellSize, cellSize);
    
    for(int i = 0; i < b_days.length; i++)
    {    
      b_days[i].setSize(dim);
      b_days[i].setMinimumSize(dim);
      b_days[i].setPreferredSize(dim);
    }
  }

}

/* end of source file */
