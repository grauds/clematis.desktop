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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * A separator much like a <code>JSeparator</code> that also displays
 * a title and/or icon in its center.
 *
 * <p><center>
 * <img src="snapshot/TitledSeparator.gif"><br>
 * <i>An example TitledSeparator.</i>
 * </center>
 *
 * @author Mark Lindner
 * @since Kiwi 2.4
 */

public class TitledSeparator extends KPanel {
    /**
     * Construct a new <code>TitledSeparator</code> with the given icon.
     *
     * @param icon The icon.
     */

    public TitledSeparator(Icon icon) {
        this(null, icon);
    }

    /**
     * Construct a new <code>TitledSeparator</code> with the given title.
     *
     * @param title The title.
     */

    public TitledSeparator(String title) {
        this(title, null);
    }

    /**
     * Construct a new <code>TitledSeparator</code> with the given title
     * and icon.
     *
     * @param title The title.
     * @param icon  The icon.
     */

    public TitledSeparator(String title, Icon icon) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.FIRST_BOTTOM_INSETS;

        add(new JSeparator(), gbc);

        KLabel label = new KLabel(title, icon, SwingConstants.LEFT);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(label, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        add(new JSeparator(), gbc);
    }

}
