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

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

/**
 * A trivial extension to <code>KRadioButton</code> that performs some simple
 * customizations.
 *
 * @author Mark Lindner
 */

public class KRadioButton extends JRadioButton {
    /**
     * Construct a new <code>KRadioButton</code>.
     *
     * @param action The action for the button.
     * @since Kiwi 2.2
     */

    public KRadioButton(Action action) {
        super(action);
        init();
    }

    /**
     * Construct a new <code>KRadioButton</code>.
     *
     * @param text The text to display in the button.
     */

    public KRadioButton(String text) {
        super(text);
        init();
    }

    /**
     * Construct a new <code>KRadioButton</code>.
     *
     * @param text The text to display in the button.
     * @param icon The ICON to display in the button.
     */

    public KRadioButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    /**
     * Construct a new <code>KRadioButton</code>.
     *
     * @param icon The ICON to display in the button.
     */

    public KRadioButton(Icon icon) {
        super(icon);
        init();
    }

    /*
     */

    private void init() {
        setOpaque(!UIChangeManager.getInstance().getButtonsAreTransparent());
    }

}
