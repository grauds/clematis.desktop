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
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.EAST_POSITION;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A date entry component which consists of a combination of a
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
 * @see com.hyperrealm.kiwi.ui.DateField
 * @see com.hyperrealm.kiwi.ui.DateChooser
 * @see com.hyperrealm.kiwi.ui.dialog.DateChooserDialog
 * @since Kiwi 1.4
 */

public class DateChooserField extends KPanel {

    private static final int DEFAULT_WIDTH = 10;

    protected DateChooser chooser;

    private DateField tDate;

    private JButton bChooser;

    private JPopupMenu menu;

    /**
     * Construct a <code>DateChooserField</code> object using the
     * default date format.
     */

    public DateChooserField() {
        this(null);
    }

    /**
     * Construct a <code>DateChooserField</code> with the specified width,
     * using the default date format.
     *
     * @param width The width for the field.
     */

    public DateChooserField(int width) {
        this(width, null);
    }

    /**
     * Construct a <code>DateChooserField</code> with the default width,
     * using the specified date format.
     *
     * @param format The format to display the date in.
     */

    public DateChooserField(String format) {
        this(DEFAULT_WIDTH, format);
    }

    /**
     * Construct a <code>DateChooserField</code> with the specified width and
     * date format.
     *
     * @param width  The width for the field.
     * @param format The format to display the date in.
     */

    public DateChooserField(int width, String format) {

        setLayout(new BorderLayout(2, 2));

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        tDate = new DateField(width, format);
        tDate.setOpaque(false);
        add(CENTER_POSITION, tDate);

        bChooser = new KButton(KiwiUtils.getResourceManager().getIcon("calendar_date.png"));
        bChooser.setToolTipText(loc.getMessage("kiwi.tooltip.select_date"));
        add(EAST_POSITION, bChooser);

        menu = new JPopupMenu();
        menu.setOpaque(false);
        chooser = new DateChooser();
        chooser.setOpaque(false);

        KPanel pPopup = new KPanel(UIChangeManager.getInstance().getDefaultTexture());
        pPopup.setLayout(new GridLayout(1, 0));
        pPopup.setBorder(KiwiUtils.DEFAULT_BORDER);
        pPopup.add(chooser);
        menu.add(pPopup);

        bChooser.addActionListener(evt -> {
            Date d = tDate.getDate();
            if (d != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                chooser.setSelectedDate(c);
            }

            menu.show(bChooser, 0, 0);
        });

        chooser.addActionListener(evt -> {
            if (evt.getActionCommand().equals(DateChooser.DATE_CHANGE_CMD)) {
                Date d = chooser.getSelectedDate().getTime();
                tDate.setDate(d);
                menu.setVisible(false);
            }
        });
    }

    /**
     * Get the date that is currently displayed in the field.
     *
     * @return The date.
     */

    public Date getDate() {
        return (tDate.getDate());
    }

    /**
     * Set the date to be displayed in the field.
     *
     * @param date The new date.
     */

    public void setDate(Date date) {
        tDate.setDate(date);
    }

}
