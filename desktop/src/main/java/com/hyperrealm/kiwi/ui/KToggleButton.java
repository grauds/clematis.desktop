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
import javax.swing.JToggleButton;

/**
 * A trivial extension to <code>JToggleButton</code> that performs some simple
 * customizations.
 *
 * @author Mark Lindner
 * @see javax.swing.JToggleButton
 */

public class KToggleButton extends JToggleButton {

    /**
     * Construct a new <code>KToggleButton</code>. A new, transparent button
     * will be created.
     *
     * @param action The action for the button.
     * @since Kiwi 2.1.4
     */

    public KToggleButton(Action action) {
        super(action);

        init();
    }

    /**
     * Construct a new <code>KToggleButton</code>. A new, transparent button
     * will be created.
     *
     * @param text The text to display in the button.
     */

    public KToggleButton(String text) {
        super(text);

        init();
    }

    /**
     * Construct a new <code>KToggleButton</code>. A new, transparent button
     * will be created.
     *
     * @param text The text to display in the button.
     * @param icon The icon to display in the button.
     */

    public KToggleButton(String text, Icon icon) {
        super(text, icon);

        init();
    }

    /**
     * Construct a new <code>KToggleButton</code>. A new, transparent button
     * will be created with zero-pixel margins and focus painting turned off.
     *
     * @param icon The icon to display in the button.
     */

    public KToggleButton(Icon icon) {
        super(icon);

        init();
    }

    /*
     */

    private void init() {
        setOpaque(!UIChangeManager.getButtonsAreTransparent());
    }

}
