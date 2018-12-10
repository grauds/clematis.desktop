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

import javax.swing.JComponent;

/**
 * An interface that describes the behavior of a viewer for
 * <code>UIElement</code>s.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.UIElementChooser
 */

public interface UIElementViewer {
    /**
     * Get a reference to the viewer component.
     *
     * @return The viewer component itself.
     */

    JComponent getViewerComponent();

    /**
     * Display the given element in the viewer.
     *
     * @param element The element to display.
     */

    void showElement(UIElement element);
}
