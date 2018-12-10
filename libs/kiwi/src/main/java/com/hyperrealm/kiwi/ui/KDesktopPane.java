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

import java.awt.Graphics;

import javax.swing.JDesktopPane;

/**
 * A trivial extension of <code>JDesktopPane</code> that supports an opacity
 * setting.
 *
 * @author Mark Lindner
 */

public class KDesktopPane extends JDesktopPane {
    private boolean opaque = true;

    /**
     * Construct a new <code>KDesktopPane</code>.
     */

    public KDesktopPane() {
        super();
    }

    /**
     * Paint the component. Delegates to superclass if the component is
     * opaque.
     */

    protected void paintComponent(Graphics gc) {
        if (opaque) {
            super.paintComponent(gc);
        }
    }

    /**
     * Get the opacity state of this component.
     *
     * @return <code>true</code> if this component is opaque, and
     * <code>false</code> otherwise.
     */

    public boolean isOpaque() {
        return (opaque);
    }

    /**
     * Set the opacity state of this component.
     *
     * @param opaque A flag specifying whether this component will be opaque.
     */

    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
        repaint();
    }

}
