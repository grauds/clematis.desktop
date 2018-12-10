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

import javax.swing.JTabbedPane;

/**
 * A specialization of <code>JTabbedPane</code> that provides a hook for data
 * validation. The method <code>canLeaveTab()</code> can be overriden to
 * provide data validation for the currently selected tab, thereby allowing or
 * disallowing the user from selecting a different tab.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class KTabbedPane extends JTabbedPane {
    private int curTab = 0;
    private boolean firstTime = true, isAdjusting = false;

    /**
     * Construct a new <code>KTabbedPane</code>.
     */

    public KTabbedPane() {
        setOpaque(false);

        addChangeListener(evt -> {
            if (firstTime) {
                firstTime = false;
                return;
            }

            if (!isAdjusting) {

                int index = getSelectedIndex();

                isAdjusting = true;
                setSelectedIndex(curTab);
                isAdjusting = false;

                if (canLeaveTab(curTab, index)) {
                    curTab = index;
                    isAdjusting = true;
                    setSelectedIndex(curTab);
                    isAdjusting = false;
                }
            }
        });
    }

    /**
     * Reset the <code>KTabbedPane</code>. The first tab is made active.
     */

    public void reset() {
        isAdjusting = true;
        setSelectedIndex(0);
        curTab = 0;
        isAdjusting = false;
    }

    /**
     * Determine if the user is allowed to select a different tab. The default
     * implementation simply returns <b>true</b>, but subclassers can provide
     * input validation logic for the currently displayed tab.
     *
     * @param currentTab The index of the tab that is currently selected.
     * @param newTab     The tab that the user wishes to switch to.
     * @return <b>true</b> if the new tab may be displayed, or <b>false</b>
     * if the current tab should remain selected.
     */

    protected boolean canLeaveTab(int currentTab, int newTab) {
        return (true);
    }

}
