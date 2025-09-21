package jworkspace.ui.utils;

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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.ui.api.Constants;
import jworkspace.ui.api.action.ActionChangedListener;
import jworkspace.ui.api.cpanel.CButton;

/**
 * Workspace utils.
 *
 * @author Anton Troshin
 */
public final class SwingUtils implements Constants {

    private SwingUtils() {}

    /**
     * Draw dashed rectangle.
     */
    public static void drawDashedRect(Graphics g, int x, int y, int width, int height) {
        drawUpperLowerDashes(g, x, y, width, height);
        drawLeftRightDashes(g, x, y, width, height);
    }

    private static void drawUpperLowerDashes(Graphics g, int x, int y, int width, int height) {

        int vx;

        // draw upper and lower horizontal dashes
        for (vx = x; vx < (x + width); vx += 2) {
            g.drawLine(vx, y, vx, y);
            g.drawLine(vx, y + height - 1, vx, y + height - 1);
        }
    }

    /**
     * Returns input stream from given file.
     */
    public static InputStream getInputStream(File file, Class<?> c) {

        InputStream rtn = null;
        String s;
        if (file != null) {
            try {
                rtn = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                s = file.toString();
                int i = s.indexOf(File.separator);
                if (i >= 0) {
                    s = s.substring(i);
                    s = s.replaceAll("\\\\", "/");
                    rtn = c.getResourceAsStream(s);
                }
            }
        }
        return rtn;
    }

    /**
     * Draw dashed rectangle.
     */
    public static void drawDashedRect(Graphics g, Rectangle rect) {

        int x = rect.getLocation().x;
        int y = rect.getLocation().y;
        int width = rect.getSize().width;
        int height = rect.getSize().height;

        drawUpperLowerDashes(g, x, y, width, height);
        drawLeftRightDashes(g, x, y, width, height);
    }

    private static void drawLeftRightDashes(Graphics g, int x, int y, int width, int height) {

        int vy;

        // draw left and right vertical dashes
        for (vy = y; vy < (y + height); vy += 2) {
            g.drawLine(x, vy, x, vy);
            g.drawLine(x + width - 1, vy, x + width - 1, vy);
        }
    }

    /**
     * Finds distance between two points
     */
    public static int distance(int i, int j) {
        return max(i, j) - min(i, j);
    }

    /**
     * Finds distance between two 2D points
     */
    public static double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /**
     * 2D routine, that checks whether if given point is
     * on north from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isNorthernQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) <= distance(center.y, b.y)) && (center.y >= b.y);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on south from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isSouthernQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) <= distance(center.y, b.y)) && (center.y <= b.y);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on east from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isEasternQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) >= distance(center.y, b.y)) && (center.x <= b.x);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on east from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isWesternQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) >= distance(center.y, b.y)) && (center.x >= b.x);
    }

    /**
     * Finds maximum of two values
     */
    public static int max(int i, int j) {
        return Math.max(i, j);
    }

    /**
     * Finds the minimum of two values
     */
    public static int min(int i, int j) {
        return Math.min(i, j);
    }

    public static ImageIcon toImageIcon(Icon icon) {
        if (icon instanceof ImageIcon imageIcon) {
            return imageIcon;
        }

        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        // Create a BufferedImage and paint the icon into it
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        return new ImageIcon(image);
    }

    /**
     * Create a button from action
     */
    public static CButton createCButtonFromAction(Action a) {

        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        CButton b = new CButton(icon, icon);
        b.setEnabled(a.isEnabled());
        b.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
        b.setAction(a);
        b.setText(null);
        b.addPropertyChangeListener(createActionChangeListener(b));
        return b;
    }

    public static JMenuItem createMenuItem(String name,
                                           ActionListener listener,
                                           String actionCommand,
                                           KeyStroke accelerator
    ) {
        JMenuItem menuItem = new JMenuItem(name);
        if (listener != null) {
            menuItem.addActionListener(listener);
        }
        if (actionCommand != null) {
            menuItem.setActionCommand(actionCommand);
        }
        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }
        menuItem.addPropertyChangeListener(createActionChangeListener(menuItem));
        return menuItem;
    }

    public static JMenuItem createMenuItem(String name,
                                           ActionListener listener,
                                           String actionCommand,
                                           KeyStroke accelerator,
                                           Icon icon
    ) {
        JMenuItem menuItem = createMenuItem(name, listener, actionCommand, accelerator);
        menuItem.setIcon(icon);
        menuItem.addPropertyChangeListener(createActionChangeListener(menuItem));
        return menuItem;
    }

    private static ActionChangedListener createActionChangeListener(JMenuItem b) {
        return new ActionChangedListener(b);
    }

    private static ActionChangedListener createActionChangeListener(CButton b) {
        return new ActionChangedListener(b);
    }

    /**
     * Create menu item. This method installs small ICON
     * instead of action ICON in Swing.
     */
    public static JMenuItem createMenuItem(Action a) {
        JMenuItem menuItem = new JMenuItem(a);
        menuItem.setEnabled(a.isEnabled());
        Icon icon = (Icon) a.getValue(MENU_ICON);
        menuItem.setIcon(icon);
        menuItem.addPropertyChangeListener(createActionChangeListener(menuItem));
        return menuItem;
    }

    /**
     * Create checkbox menu item.
     */
    public static JCheckBoxMenuItem createCheckboxMenuItem(Action a) {
        JCheckBoxMenuItem boxMenuItem = new JCheckBoxMenuItem(a);
        boxMenuItem.setEnabled(a.isEnabled());
        boxMenuItem.addPropertyChangeListener(createActionChangeListener(boxMenuItem));
        return boxMenuItem;
    }

    /**
     * Create radio menu item.
     */
    public static JRadioButtonMenuItem createRadioMenuItem(Action a) {
        JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(a);
        menuItem.setEnabled(a.isEnabled());
        menuItem.addPropertyChangeListener(createActionChangeListener(menuItem));
        return menuItem;
    }


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