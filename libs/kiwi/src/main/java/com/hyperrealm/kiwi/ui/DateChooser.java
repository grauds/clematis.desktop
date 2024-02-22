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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.EAST_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.NORTH_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.SOUTH_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.WEST_POSITION;
import com.hyperrealm.kiwi.event.ActionSupport;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * This class represents a date chooser. The chooser allows an arbitrary date
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

public class DateChooser extends KPanel implements ActionListener {

    /**
     * <i>Date changed</i> event command.
     */
    static final String DATE_CHANGE_CMD = "dateChanged";
    /**
     * <i>Month changed</i> event command.
     */
    private static final String MONTH_CHANGE_CMD = "monthChanged";
    /**
     * <i>Year changed</i> event command.
     */
    private static final String YEAR_CHANGE_CMD = "yearChanged";
    /**
     * The default cell size.
     */
    private static final int CELL_SIZE = 30;

    private static final int[] DAYS_IN_MONTH
        = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final int[] DAYS_IN_MONTH_LEAP
        = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final Color WEEKEND_COLOR = Color.red.darker();

    private static final Icon I_LEFT
        = KiwiUtils.getResourceManager().getIcon("repeater_left.png");

    private static final Icon I_RIGHT
        = KiwiUtils.getResourceManager().getIcon("repeater_right.png");

    private static final int DAYS_IN_WEEK = 7;

    private KLabel lDate, lYear, lMonth;

    private KButton bLyear, bRyear, bLmonth, bRmonth;

    private SimpleDateFormat datefmt = new SimpleDateFormat("E  d MMM yyyy");

    private Calendar selectedDate = null, minDate = null, maxDate = null;

    private int selectedDay, firstDay, minDay = -1, maxDay = -1, firstCell = 0;

    private JToggleButton[] bDays;

    private String[] months;

    private ActionSupport asupport;

    /**
     * Construct a new <code>DateChooser</code>. The selection will be
     * initialized to the current date.
     */

    public DateChooser() {
        this(Calendar.getInstance());
    }

    /**
     * Construct a new <code>DateChooser</code> with the specified selected
     * date.
     *
     * @param date The date for the selection.
     */

    public DateChooser(Calendar date) {

        KLabel[] lDays = new KLabel[DAYS_IN_WEEK];

        int fd = date.getFirstDayOfWeek();
        DateFormatSymbols sym = LocaleManager.getDefault().getDateFormatSymbols();
        months = sym.getShortMonths();
        String[] wkd = sym.getShortWeekdays();

        for (int i = 0, ii = fd; i < lDays.length; i++) {
            int l = Math.min(wkd[ii].length(), 2);
            lDays[i] = new KLabel(wkd[ii].substring(0, l));
            lDays[i].setHorizontalAlignment(SwingConstants.CENTER);
            lDays[i].setVerticalAlignment(SwingConstants.BOTTOM);

            if ((ii == Calendar.SATURDAY) || (ii == Calendar.SUNDAY)) {
                lDays[i].setForeground(WEEKEND_COLOR);
            }

            if (++ii > DAYS_IN_WEEK) {
                ii = 1;
            }
        }

        ButtonGroup group = new ButtonGroup();
        bDays = new JToggleButton[(DAYS_IN_WEEK - 1) * DAYS_IN_WEEK];

        Insets cellMargin = new Insets(2, 2, 2, 2);

        for (int i = 0, ii = fd; i < bDays.length; i++) {
            bDays[i] = new JToggleButton();
            bDays[i].setOpaque(false);
            bDays[i].setMargin(cellMargin);
            // bDays[i].setFocusPainted(false);
            bDays[i].setHorizontalAlignment(SwingConstants.RIGHT);
            bDays[i].setVerticalAlignment(SwingConstants.TOP);
            if ((ii == Calendar.SATURDAY) || (ii == Calendar.SUNDAY)) {
                bDays[i].setForeground(WEEKEND_COLOR);
            }

            Dimension dim = new Dimension(CELL_SIZE, CELL_SIZE);
            bDays[i].setSize(dim);
            bDays[i].setMinimumSize(dim);
            bDays[i].setPreferredSize(dim);

            bDays[i].addActionListener(this);

            group.add(bDays[i]);

            if (++ii > DAYS_IN_WEEK) {
                ii = 1;
            }
        }

        asupport = new ActionSupport(this);

        setLayout(DEFAULT_BORDER_LAYOUT);

        KPanel top = new KPanel();
        top.setLayout(new BorderLayout(0, 0));

        KPanel p1 = new KPanel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        top.add(WEST_POSITION, p1);

        bLmonth = new KButton(I_LEFT);
        bLmonth.setMargin(KiwiUtils.EMPTY_INSETS);
        bLmonth.setFocusPainted(false);
        bLmonth.setOpaque(false);
        bLmonth.addActionListener(this);
        p1.add(bLmonth);

        lMonth = new KLabel();
        p1.add(lMonth);

        bRmonth = new KButton(I_RIGHT);
        bRmonth.setMargin(KiwiUtils.EMPTY_INSETS);
        bRmonth.setFocusPainted(false);
        bRmonth.setOpaque(false);
        bRmonth.addActionListener(this);
        p1.add(bRmonth);

        KPanel p2 = new KPanel();
        p2.setLayout(new FlowLayout(FlowLayout.LEFT));
        top.add(EAST_POSITION, p2);

        bLyear = new KButton(I_LEFT);
        bLyear.setMargin(KiwiUtils.EMPTY_INSETS);
        bLyear.setFocusPainted(false);
        bLyear.setOpaque(false);
        bLyear.addActionListener(this);
        p2.add(bLyear);

        lYear = new KLabel();
        p2.add(lYear);

        bRyear = new KButton(I_RIGHT);
        bRyear.setMargin(KiwiUtils.EMPTY_INSETS);
        bRyear.setFocusPainted(false);
        bRyear.setOpaque(false);
        bRyear.addActionListener(this);
        p2.add(bRyear);

        add(NORTH_POSITION, top);

        KPanel pGrid = new KPanel();
        pGrid.setLayout(new BorderLayout(0, 0));

        KPanel pHeadings = new KPanel();
        pHeadings.setLayout(new GridLayout(0, DAYS_IN_WEEK, 1, 1));

        for (KLabel lDay : lDays) {
            pHeadings.add(lDay);
        }

        pGrid.add(NORTH_POSITION, pHeadings);

        KPanel pDays = new KPanel();
        pDays.setLayout(new GridLayout(0, DAYS_IN_WEEK, 1, 1));

        for (JToggleButton bDay : bDays) {
            pDays.add(bDay);
        }

        pGrid.add(CENTER_POSITION, pDays);

        add(CENTER_POSITION, pGrid);

        lDate = new KLabel("Date", SwingConstants.CENTER);
        add(SOUTH_POSITION, lDate);

        Font f = getFont();
        super.setFont(new Font(f.getName(), Font.BOLD, f.getSize()));

        setSelectedDate(date);
    }

    /**
     * Determine if a year is a leap year.
     *
     * @param year The year to check.
     * @return <code>true</code> if the year is a leap year, and
     * <code>false</code> otherwise.
     */
    @SuppressWarnings({"MagicNumber"})
    private static boolean isLeapYear(int year) {
        return ((((year % 4) == 0) && ((year % 100) != 0)) || ((year % 400) == 0));
    }

    /**
     * Get a copy of the <code>Calendar</code> object that represents the
     * currently selected date.
     *
     * @return The currently selected date.
     */

    public Calendar getSelectedDate() {
        return ((Calendar) selectedDate.clone());
    }

    /**
     * Set the selected date for the chooser.
     *
     * @param date The date to select.
     */

    public void setSelectedDate(Calendar date) {
        selectedDate = copyDate(date, selectedDate);
        selectedDay = selectedDate.get(Calendar.DAY_OF_MONTH);

        refresh();
    }

    /**
     * Get the earliest selectable date for the chooser.
     *
     * @return The minimum selectable date, or <code>null</code> if there is no
     * minimum date currently set.
     */

    public Calendar getMinimumDate() {
        return (minDate);
    }

    /**
     * Set the earliest selectable date for the chooser.
     *
     * @param date The (possibly <code>null</code>) minimum selectable date.
     */

    public void setMinimumDate(Calendar date) {
        minDate = ((date == null) ? null : copyDate(date, minDate));
        minDay = ((date == null) ? -1 : minDate.get(Calendar.DATE));

        refresh();
    }

    /**
     * Get the latest selectable date for the chooser.
     *
     * @return The maximum selectable date, or <code>null</code> if there is no
     * maximum date currently set.
     */

    public Calendar getMaximumDate() {
        return (maxDate);
    }

    /**
     * Set the latest selectable date for the chooser.
     *
     * @param date The (possibly <code>null</code>) maximum selectable date.
     */

    public void setMaximumDate(Calendar date) {
        maxDate = ((date == null) ? null : copyDate(date, maxDate));
        maxDay = ((date == null) ? -1 : maxDate.get(Calendar.DATE));

        refresh();
    }

    /**
     * Set the format for the textual date display at the bottom of the
     * component.
     *
     * @param format The new date format to use.
     */

    public void setDateFormat(SimpleDateFormat format) {
        datefmt = format;

        refreshDate();
    }

    /* Determine what day of week the first day of the month falls on. It's too
     * bad we have to resort to this hack; the Java API provides no means of
     * doing this any other way.
     */

    /**
     * Handle events. This method is public as an implementation side-effect.
     */

    public void actionPerformed(ActionEvent evt) {
        Object o = evt.getSource();

        if (o instanceof JToggleButton) {
            for (int i = 0; i < bDays.length; i++) {
                if (bDays[i].isSelected()) {
                    selectedDay = i - firstCell + 1;
                    selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);
                    asupport.fireActionEvent(DATE_CHANGE_CMD);

                    break;
                }
            }

            refreshDate();

            return;
        }

        if (o == bLmonth) {
            selectedDate.add(Calendar.MONTH, -1);
        } else if (o == bRmonth) {
            selectedDate.add(Calendar.MONTH, 1);
        } else if (o == bLyear) {
            selectedDate.add(Calendar.YEAR, -1);
            if (minDate != null && selectedDate.get(Calendar.MONTH) < minDate.get(Calendar.MONTH)) {
                selectedDate.set(Calendar.MONTH, minDate.get(Calendar.MONTH));
            }
        } else if (o == bRyear) {
            selectedDate.add(Calendar.YEAR, 1);
            if (maxDate != null && selectedDate.get(Calendar.MONTH) > maxDate.get(Calendar.MONTH)) {
                selectedDate.set(Calendar.MONTH, maxDate.get(Calendar.MONTH));
            }
        }

        selectedDay = 1;
        selectedDate.set(Calendar.DATE, selectedDay);
        refresh();
        bDays[selectedDay + firstCell - 1].setSelected(true);

        asupport.fireActionEvent(((o == bLmonth) || (o == bRmonth))
            ? MONTH_CHANGE_CMD : YEAR_CHANGE_CMD);
    }

    private void computeFirstDay() {
        int d = selectedDate.get(Calendar.DAY_OF_MONTH);
        selectedDate.set(Calendar.DAY_OF_MONTH, 1);
        firstDay = selectedDate.get(Calendar.DAY_OF_WEEK);
        selectedDate.set(Calendar.DAY_OF_MONTH, d);
    }

    /* This method is called whenever the month or year changes. Its job
     * is to reconfigure the grid and determine whether any selection
     * range limits have been reached.
     */

    private void refreshDate() {
        lDate.setText(datefmt.format(selectedDate.getTime()));
    }

    @SuppressWarnings({"CyclomaticComplexity", "NestedIfDepth"})
    private void refresh() {

        lYear.setText(String.valueOf(selectedDate.get(Calendar.YEAR)));
        lMonth.setText(months[selectedDate.get(Calendar.MONTH)]);

        computeFirstDay();

        boolean clipAllMax = false;
        boolean clipAllMin = false;
        boolean clipMax = false;
        boolean clipMin = false;

        bLyear.setEnabled(true);
        bRyear.setEnabled(true);
        bLmonth.setEnabled(true);
        bRmonth.setEnabled(true);

        // Disable anything that would cause the date to go out of range. This
        // logic is extremely sensitive so be very careful when making changes.
        // Every condition test in here is necessary, so don't remove anything.

        if (minDate != null) {
            int y = selectedDate.get(Calendar.YEAR);
            int y0 = minDate.get(Calendar.YEAR);
            int m = selectedDate.get(Calendar.MONTH);
            int m0 = minDate.get(Calendar.MONTH);

            bLyear.setEnabled(y > y0);
            if (y == y0) {
                bLmonth.setEnabled(m > m0);

                if (m == m0) {
                    clipMin = true;
                    int d0 = minDate.get(Calendar.DATE);

                    if (selectedDay < d0) {
                        selectedDay = d0;
                        selectedDate.set(Calendar.DATE, selectedDay);
                    }

                    // allow out-of-range selection
                    // selectedDate.set(Calendar.DATE, selectedDay);
                }
            }

            // clip if years same but month earlier, OR if year is earlier.
            // fix submitted by Robert Heitzmann.
            clipAllMin = (((y == y0) && (m < m0)) || (y < y0));
        }

        if (maxDate != null) {
            int y = selectedDate.get(Calendar.YEAR);
            int y1 = maxDate.get(Calendar.YEAR);
            int m = selectedDate.get(Calendar.MONTH);
            int m1 = maxDate.get(Calendar.MONTH);

            bRyear.setEnabled(y < y1);
            if (y == y1) {
                bRmonth.setEnabled(m < m1);
                if (m == m1) {
                    clipMax = true;
                    int d1 = maxDate.get(Calendar.DATE);
                    if (selectedDay > d1) {
                        selectedDay = d1;
                        selectedDate.set(Calendar.DATE, selectedDay);
                    }
                }
            }

            // clip if years same but month later, OR if year is later.
            // fix submitted by Robert Heitzmann.

            clipAllMax = (((y == y1) && (m > m1)) || (y > y1));
        }

        // update the grid buttons

        firstCell = ((firstDay - selectedDate.getFirstDayOfWeek() + DAYS_IN_WEEK) % DAYS_IN_WEEK);

        // find out how many days there are in the current month

        int month = DateChooser.this.selectedDate.get(Calendar.MONTH);
        int dmax = (isLeapYear(DateChooser.this.selectedDate.get(Calendar.YEAR))
            ? DAYS_IN_MONTH_LEAP[month] : DAYS_IN_MONTH[month]);

        for (int c = 0, ii = 0; c < bDays.length; c++) {
            if ((c < firstCell) || (ii == dmax)) {
                bDays[c].setText(null);
                bDays[c].setVisible(false);
            } else {
                bDays[c].setText(String.valueOf(++ii));
                bDays[c].setVisible(true);

                int d = c - firstCell + 1;

                boolean disabled = ((clipMin && (d < minDay))
                    || (clipMax && (d > maxDay))
                    || clipAllMin || clipAllMax);

                bDays[c].setEnabled(!disabled);
            }
        }

        refreshDate();
    }

    /* Copy the relevant portions of a date. */

    private Calendar copyDate(Calendar source, Calendar dest) {

        Calendar ret = dest;

        if (dest == null) {
            ret = Calendar.getInstance();
        }

        ret.set(Calendar.YEAR, source.get(Calendar.YEAR));
        ret.set(Calendar.MONTH, source.get(Calendar.MONTH));
        ret.set(Calendar.DATE, source.get(Calendar.DATE));

        return (ret);
    }

    /**
     * Add a <code>ActionListener</code> to this component's list of listeners.
     *
     * @param listener The listener to add.
     */

    public void addActionListener(ActionListener listener) {
        asupport.addActionListener(listener);
    }

    /**
     * Remove a <code>ActionListener</code> from this component's list of
     * listeners.
     *
     * @param listener The listener to remove.
     */

    public void removeActionListener(ActionListener listener) {
        asupport.removeActionListener(listener);
    }

    /**
     * Set the size of date cells in the calendar pane.
     *
     * @param cellSize The width and height, in pixels, of a cell.
     * @since Kiwi 2.0
     */

    public void setCellSize(int cellSize) {
        Dimension dim = new Dimension(cellSize, cellSize);

        for (JToggleButton bDay : bDays) {
            bDay.setSize(dim);
            bDay.setMinimumSize(dim);
            bDay.setPreferredSize(dim);
        }
    }

}
