package jworkspace.ui;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * @author Anton Troshin
 */
public class Utils {

    private Utils() {}

    /**
     * Create button from action without text
     */
    public static JButton createButtonFromAction(Action a) {
        return createButtonFromAction(a, false);
    }

    /**
     * Wraps lines at the given number of columns
     */
    public static String wrapLines(String s, int cols) {

        char[] c = s.toCharArray();
        char[] d = new char[c.length];

        int i = 0;
        int j = 0;
        int lastspace = -1;
        while (i < c.length) {
            if (c[i] == '\n') {
                j = 0;
            }
            if (j > cols && lastspace > 0) {
                d[lastspace] = '\n';
                j = i - lastspace;
                lastspace = -1;
            }
            if (c[i] == ' ') {
                lastspace = i;
            }
            d[i] = c[i];
            i++;
            j++;
        }
        return new String(d);
    }

    /**
     * Create button from action
     */
    public static JButton createButtonFromAction(Action a, boolean text) {

        JButton b = new JButton((Icon) a.getValue(Action.SMALL_ICON));
        b.setAction(a);
        if (text) {
            b.setText((String) a.getValue(Action.NAME));
        } else {
            b.setText("");
        }
        b.setEnabled(a.isEnabled());
        b.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
        return b;
    }

    /**
     * Creates a wrapped mulit-line label from several labels
     */
    public static JPanel createMultiLineLabel(String s, int cols) {

        boolean done = false;
        String msg = wrapLines(s, cols);
        char[] c = msg.toCharArray();
        int i = 0;
        StringBuffer sb = new StringBuffer();

        // use grid bag layout
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        // set layout on the panel
        JPanel p = new JPanel(gb);
        JLabel l = null;

        // iterate until all strings are added
        while (!done) {
            if (i >= c.length || c[i] == '\n') {
                l = new JLabel(sb.toString());
                sb = new StringBuffer();
                // add first label
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.weightx = 1;
                gbc.insets = KiwiUtils.LAST_INSETS;
                p.add(l, gbc);
                if (i >= c.length) {
                    done = true;
                }
            } else {
                sb.append(c[i]);
            }
            i++;
        }
        return p;
    }
}
