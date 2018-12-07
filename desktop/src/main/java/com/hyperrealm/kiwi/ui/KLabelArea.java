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

import javax.swing.JTextArea;
import javax.swing.plaf.ComponentUI;

import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * A multi-line label. This class renders a string as one or more lines,
 * breaking text on whitespace and producing a left-justified paragraph. This
 * class is basically a <code>JTextArea</code> that is transparent, non-
 * editable, non-scrollable, and non-highlightable.
 *
 * <p><center>
 * <img src="snapshot/KLabelArea.gif"><br>
 * <i>An example KLabelArea.</i>
 * </center>
 *
 * @author Mark Lindner
 * @since Kiwi 1.3
 */

public class KLabelArea extends JTextArea {
    /**
     * Construct a new <code>KLabelArea</code> with the specified text and rows
     * and columns.
     *
     * @param text The text to display.
     * @param rows The height of the label in rows.
     * @param cols The width of the label in columns.
     */

    public KLabelArea(String text, int rows, int cols) {
        super(text, rows, cols);

        init();
    }

    /**
     * Construct a new <code>KLabelArea</code> with the specified number of
     * rows and columns.
     *
     * @param rows The height of the label in rows.
     * @param cols The width of the label in columns.
     */

    public KLabelArea(int rows, int cols) {
        super(rows, cols);

        init();
    }

    /* initialize component */

    private void init() {
        setEditable(false);
        setOpaque(false);
        setHighlighter(null);
        setFont(KiwiUtils.boldFont);
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    /* The following are overridden to counteract Swing bugs. */

    public void setUI(ComponentUI newUI) {
        super.setUI(newUI);
        init();
    }

    public void updateUI() {
        super.updateUI();
        init();
    }

    /**
     * Overridden to return <code>false</code>.
     */

    public final boolean isFocusable() {
        return (false);
    }

}
