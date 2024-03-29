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

import javax.swing.JComboBox;
import javax.swing.UIManager;

/**
 * This class represents a combo box for selecting a Look & Feel. It lists
 * all L&Fs installed on the system.
 *
 * <p><center>
 * <img src="snapshot/LookAndFeelChooser.gif" border="0"><br>
 * <i>An example LookAndFeelChooser.</i>
 * </center>
 * <p>
 *
 * @author Mark Lindner
 * @see javax.swing.UIManager
 */

public class LookAndFeelChooser extends JComboBox {

    /**
     * Construct a new <code>LookAndFeelChooser</code>.
     */

    public LookAndFeelChooser() {
        UIManager.LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();

        for (UIManager.LookAndFeelInfo plaf : plafs) {

            addItem(new UIManager.LookAndFeelInfo(plaf.getName(),
                plaf.getClassName()) {
                public String toString() {
                    return (getName());
                }
            });
        }

        String curplaf = UIManager.getLookAndFeel().getName();

        for (int i = 0; i < plafs.length; i++) {
            if (curplaf.equals(plafs[i].getName())) {
                setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Get the currently selected Look & Feel.
     *
     * @return The <code>LookAndFeelInfo</code> object corresponding to the
     * currently-selected L&F.
     */

    public String getLookAndFeel() {
        return getSelectedItem() != null ? (((UIManager.LookAndFeelInfo) getSelectedItem()).getClassName()) : "";
    }

}
