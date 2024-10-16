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

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;

import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.action.ActionChangedListener;
import jworkspace.ui.cpanel.CButton;
import jworkspace.ui.widgets.WorkspaceError;

/**
 * Workspace utils.
 *
 * @author Anton Troshin
 */
public final class Utils implements Constants {

    private static final int BUFFER = 1024;
    /**
     * Directory for source files in copying or moving
     * whole directories
     */
    private static String dir = null;
    /**
     * Directory for destination files in copying or moving
     * whole directories
     */
    private static String dest = null;

    private Utils() {
    }

    private static void doCopyDir(File dir, FileSystemView fileSystem) throws IOException {

        File[] list = fileSystem.getFiles(dir, false);
        Vector<File> dirList = new Vector<>();

        for (File file : list) {

            if (file.isDirectory()) {
                dirList.addElement(file);
                // calculate name of new directory
                File destSubDir = new File(dest + file.getAbsolutePath().substring(Utils.dir.length()));
                FileUtils.forceMkdir(destSubDir);
            } else if (file.isFile()) {
                File destFile = new File(dest + file.getAbsolutePath().
                    substring(Utils.dir.length()));

                try (FileInputStream input = new FileInputStream(file);
                     FileOutputStream output = new FileOutputStream(destFile)) {

                    StreamUtils.readStreamToStream(input, output);
                } catch (IOException e) {
                    WorkspaceError.exception(WorkspaceResourceAnchor.getString("Utils.cannotCopyDir"), e);
                }
            }
        }

        for (int i = 0; i < dirList.size(); i++) {
            doCopyDir(dirList.elementAt(i), fileSystem);
        }
    }

    private static void doMoveDir(File dir, FileSystemView fileSystem) throws IOException {

        File[] list = fileSystem.getFiles(dir, false);
        Vector<File> dirList = new Vector<>();

        for (File file : list) {

            if (file.isDirectory()) {
                dirList.addElement(file);
                // calculate name of new directory
                File destSubDir = new File(dest + file.getAbsolutePath().
                    substring(Utils.dir.length()));
                FileUtils.forceMkdir(destSubDir);
            } else if (file.isFile()) {
                File destFile = new File(dest + file.getAbsolutePath().
                    substring(Utils.dir.length()));
                if (!file.renameTo(destFile)) {
                    throw new IOException("Couldn't rename the file: " + file.getName() + " to " + destFile.getName());
                }
            }
        }
        for (int i = 0; i < dirList.size(); i++) {
            doMoveDir(dirList.elementAt(i), fileSystem);
        }
    }

    /**
     * Copies directory along with all files within.
     */
    public static void copyDir(String dir, String dest) throws IOException {

        FileSystemView fileSystem = FileSystemView.getFileSystemView();
        File directory = new File(dir);

        Utils.dir = dir;
        Utils.dest = dest;

        doCopyDir(directory, fileSystem);
    }

    /**
     * Copy file to another directory
     */
    public static String copyFileToDir(String file, String dest) throws IOException {

        File f = new File(file);

        File destFile = new File(dest + f.getAbsolutePath().
            substring(f.getAbsolutePath().lastIndexOf(File.separator)));

        try (FileInputStream input = new FileInputStream(f);
             FileOutputStream output = new FileOutputStream(destFile)) {

            StreamUtils.readStreamToStream(input, output);
        }

        return destFile.getAbsolutePath();
    }

    /**
     * Copy file to another file with the same extension
     */
    public static String copyFile(String file, String dest, String destFile) throws IOException {

        File f = new File(file);

        File d = new File(dest + destFile
            + f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf('.')));

        try (FileInputStream input = new FileInputStream(f);
             FileOutputStream output = new FileOutputStream(d)) {

            StreamUtils.readStreamToStream(input, output);
        }
        return d.getAbsolutePath();
    }

    /**
     * Utility method to copy a file from one directory to another
     */
    public static void copyFile(File from, File to) throws IOException {

        if (!from.canRead()) {
            throw new IOException("Cannot read file: " + from);
        }
        if (to.exists() && (!to.canWrite())) {
            throw new IOException("Cannot write to file: " + to);
        }

        try (FileInputStream fis = new FileInputStream(from);
        FileOutputStream fos = new FileOutputStream(to)) {

            byte[] buf = new byte[BUFFER];
            int bytesLeft;
            while ((bytesLeft = fis.available()) > 0) {
                if (bytesLeft >= buf.length) {
                    if (fis.read(buf) != -1) {
                        fos.write(buf);
                    }
                } else {
                    byte[] smallBuf = new byte[bytesLeft];
                    if (fis.read(smallBuf) != -1) {
                        fos.write(smallBuf);
                    }
                }
            }
        }
    }

    /**
     * Copy file to another file with the same extension
     */
    public static String copyFile(String file, String destFile) throws IOException {

        File f = new File(file);
        File d = new File(destFile);

        try (FileInputStream input = new FileInputStream(f);
        FileOutputStream output = new FileOutputStream(d)) {
            StreamUtils.readStreamToStream(input, output);
        }

        return d.getAbsolutePath();
    }

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
    public static InputStream getInputStream(File file, Class c) {

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
     * Moves directory to new location along with all files
     * within.
     */
    public static void moveDir(String dir, String dest) throws IOException {

        FileSystemView fileSystem = FileSystemView.getFileSystemView();

        File directory = new File(dir);

        Utils.dir = dir;
        Utils.dest = dest;

        doMoveDir(directory, fileSystem);
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
     * Finds minimum of two values
     */
    public static int min(int i, int j) {
        return Math.min(i, j);
    }

    /**
     * Create button from action
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

    /**
     * Create menu item.
     */
    public static JMenuItem createMenuItem(ActionListener listener,
                                           String name, String actionCommand, Icon icon) {
        JMenuItem menuItem = new JMenuItem(name, icon);
        menuItem.addActionListener(listener);
        menuItem.setActionCommand(actionCommand);
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