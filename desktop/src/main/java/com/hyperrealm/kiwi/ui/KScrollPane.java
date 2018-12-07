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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * A trivial extension of <code>JScrollPane</code> that renders its contents
 * with a transparent background.
 *
 * @author Mark Lindner
 */

public class KScrollPane extends JScrollPane {
    /**
     * Construct a new <code>KScrollPane</code>.
     */

    public KScrollPane() {
        super();

        init();
    }

    /**
     * Construct a new <code>KScrollPane</code> for the given component.
     *
     * @param view The component to display in the scroll pane.
     */

    public KScrollPane(Component view) {
        super(view);

        init();
    }

    /**
     * Construct a new <code>KScrollPane</code> for the given component and
     * scrollbar policies.
     *
     * @param view      THe component to display in the scroll pane.
     * @param vsbPolicy The vertical scrollbar policy.
     * @param hsbPolicy The horizontal scrollbar policy.
     * @since Kiwi 2.0
     */

    public KScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);

        init();
    }

    /* common initialization */

    private void init() {
        setBackground(Color.white);
        getViewport().setBackground(Color.white);

        setOpaque(false);

        JScrollBar sb = getHorizontalScrollBar();
        if (sb != null) {
            sb.setOpaque(false);
        }

        sb = getVerticalScrollBar();
        if (sb != null) {
            sb.setOpaque(false);
        }
    }

}
