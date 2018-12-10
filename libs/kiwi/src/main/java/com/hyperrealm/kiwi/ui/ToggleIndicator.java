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

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * A graphical toggle component. This component displays one of two images,
 * depending on whether it is "toggled" on or off. This component can be used
 * to create LEDs and other types of on-off indicators.
 *
 * <p><center>
 * <img src="snapshot/ToggleIndicator.gif"><br>
 * <i>Some example ToggleIndicators.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class ToggleIndicator extends JLabel {

    private Icon icon, altIcon;

    private boolean state = false;

    /**
     * Construct a new <code>ToggleIndicator</code> with the specified icons for
     * the "on" and "off" states.
     *
     * @param icon    The ICON to display when the toggle is off
     * @param altIcon The ICON to display when the toggle is on.
     */

    public ToggleIndicator(Icon icon, Icon altIcon) {
        this.icon = icon;
        this.altIcon = altIcon;

        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);

        setIcon(icon);
    }

    /**
     * Get the current state of the toggle.
     *
     * @return The current toggle state.
     */

    public synchronized boolean getState() {
        return (state);
    }

    /**
     * Set the toggle state.
     *
     * @param state <code>true</code> to turn the toggle "on",
     *              <code>false</code> to turn it "off." The toggle will be repainted
     *              immediately.
     */

    public synchronized void setState(boolean state) {
        this.state = state;

        setIcon(state ? altIcon : icon);
        KiwiUtils.paintImmediately(this);
    }

}
