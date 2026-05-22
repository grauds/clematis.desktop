package jworkspace.ui.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hyperrealm.kiwi.util.KiwiUtils;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
public class TextUtils {

    private TextUtils() {}

    /**
     * Wraps lines at the given number of columns
     */
    @Deprecated
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
     * Creates a wrapped mulit-line label from several labels
     */
    @Deprecated
    public static JPanel createMultiLineLabel(String s, int cols) {

        boolean done = false;
        String msg = wrapLines(s, cols);
        char[] c = msg.toCharArray();
        int i = 0;
        StringBuilder sb = new StringBuilder();

        // use grid bag layout
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        // set layout on the panel
        JPanel p = new JPanel(gb);
        JLabel l;

        // iterate until all strings are added
        while (!done) {
            if (i >= c.length || c[i] == '\n') {
                l = new JLabel(sb.toString());
                sb = new StringBuilder();
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
