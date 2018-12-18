package jworkspace.ui;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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

import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.util.KiwiUtils;
import jworkspace.LangResource;
import jworkspace.api.IConstants;
import jworkspace.kernel.Workspace;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.cpanel.CButton;
import jworkspace.util.WorkspaceError;
import jworkspace.util.crypto.DesCipher;

/**
 * Workspace utils.
 */
public final class Utils implements IConstants {
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

    /**
     * todo
     */
    public static byte[] encrypt(String userName, String password) {
        if (password == null || password.equals("")) {
            return new byte[]{};
        }
        return password;
    }

    private static void _copyDir(File _dir, FileSystemView fileSystem) {
        File[] list = fileSystem.getFiles(_dir, false);
        Vector<File> dirList = new Vector<>();

        for (File file : list) {

            if (file.isDirectory()) {
                dirList.addElement(file);
                // calculate name of new directory
                File destSubDir = new File(dest + file.getAbsolutePath().substring(dir.length()));
                destSubDir.mkdir();
            } else if (file.isFile()) {
                File destFile = new File(dest + file.getAbsolutePath().
                    substring(dir.length()));

                try {
                    FileInputStream input = new FileInputStream(file);
                    FileOutputStream output = new FileOutputStream(destFile);
                    output = (FileOutputStream) StreamUtils.readStreamToStream(input, output);
                    input.close();
                    output.close();
                } catch (IOException e) {
                    WorkspaceError.exception
                        (LangResource.getString("Utils.cannotCopyDir"), e);
                }
            }
        }
        for (int i = 0; i < dirList.size(); i++) {
            _copyDir(dirList.elementAt(i), fileSystem);
        }
    }

    private static final void _moveDir(File _dir, FileSystemView fileSystem) {
        File[] list = fileSystem.getFiles(_dir, false);
        Vector dirList = new Vector();

        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                dirList.addElement(list[i]);
                // calculate name of new directory
                File destSubDir = new File(dest + list[i].getAbsolutePath().
                    substring(dir.length()));
                destSubDir.mkdir();
            } else if (list[i].isFile()) {
                File destFile = new File(dest + list[i].getAbsolutePath().
                    substring(dir.length()));
                list[i].renameTo(destFile);
            }
        }
        for (int i = 0; i < dirList.size(); i++) {
            _moveDir((File) dirList.elementAt(i), fileSystem);
        }
    }

    /**
     * Copies directory along with all files
     * within.
     */
    public static final void copyDir(String _dir, String _dest) {
        FileSystemView fileSystem = FileSystemView.getFileSystemView();

        File directory = new File(_dir);

        dir = _dir;
        dest = _dest;

        _copyDir(directory, fileSystem);
    }

    /**
     * Copy file to another directory
     */
    public static final String copyFileToDir(String _file, String _dest)
        throws IOException {
        File file = new File(_file);

        File destFile = new File(_dest + file.getAbsolutePath().
            substring(file.getAbsolutePath().lastIndexOf(File.separator)));

        FileInputStream input = new FileInputStream(file);
        FileOutputStream output = new FileOutputStream(destFile);
        output = (FileOutputStream) StreamUtils.readStreamToStream(input, output);
        input.close();
        output.close();

        return destFile.getAbsolutePath();
    }

    /**
     * Copy file to another file with the same extension
     */
    public static final String copyFile(String _file, String _dest,
                                        String _dest_file)
        throws IOException {
        File file = new File(_file);

        File destFile = new File(_dest + _dest_file + file.getAbsolutePath().
            substring(file.getAbsolutePath().lastIndexOf('.')));

        FileInputStream input = new FileInputStream(file);
        FileOutputStream output = new FileOutputStream(destFile);
        output = (FileOutputStream) StreamUtils.readStreamToStream(input, output);
        input.close();
        output.close();

        return destFile.getAbsolutePath();
    }

    /**
     * Utility method to copy a file from one directory to another
     */
    public static void copyFile(File from, File to) throws IOException {
        if (!from.canRead()) {
            throw new IOException("Cannot read file '" + from + "'.");
        }
        if (to.exists() && (!to.canWrite())) {
            throw new IOException("Cannot write to file '" +
                to + "'.");
        }

        FileInputStream fis = new FileInputStream(from);
        FileOutputStream fos = new FileOutputStream(to);

        byte[] buf = new byte[1024];
        int bytesLeft;
        while ((bytesLeft = fis.available()) > 0) {
            if (bytesLeft >= buf.length) {
                fis.read(buf);
                fos.write(buf);
            } else {
                byte[] smallBuf = new byte[bytesLeft];
                fis.read(smallBuf);
                fos.write(smallBuf);
            }
        }
        fos.close();
        fis.close();
    }

    /**
     * Copy file to another file with the same extension
     */
    public static final String copyFile(String _file, String _dest_file)
        throws IOException {
        File file = new File(_file);

        File destFile = new File(_dest_file);

        FileInputStream input = new FileInputStream(file);
        FileOutputStream output = new FileOutputStream(destFile);
        output = (FileOutputStream) StreamUtils.readStreamToStream(input, output);
        input.close();
        output.close();

        return destFile.getAbsolutePath();
    }

    /**
     * Draw dashed rectangle.
     */
    public static void drawDashedRect(Graphics g, int x, int y,
                                      int width, int height) {
        int vx, vy;

        // draw upper and lower horizontal dashes
        for (vx = x; vx < (x + width); vx += 2) {
            g.drawLine(vx, y, vx, y);
            g.drawLine(vx, y + height - 1, vx, y + height - 1);
        }

        // draw left and right vertical dashes
        for (vy = y; vy < (y + height); vy += 2) {
            g.drawLine(x, vy, x, vy);
            g.drawLine(x + width - 1, vy, x + width - 1, vy);
        }
    }

    /**
     * Returns input stream from given file.
     */
    public static InputStream getInputStream(File file, Class c)
        throws FileNotFoundException {
        InputStream rtn;
        String s;
        if (file != null) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                s = file.toString();
                int i = s.indexOf(File.separator);
                if (i >= 0) {
                    s = s.substring(i);
                    s = s.replaceAll("\\", "/");
                    if ((rtn = c.getResourceAsStream(s)) != null) {
                        return rtn;
                    }
                }
                throw e;
            }
        }
        return null;
    }

    /**
     * Draw dashed rectangle.
     */
    public static void drawDashedRect(Graphics g, Rectangle rect) {
        int x = rect.getLocation().x;
        int y = rect.getLocation().y;
        int width = rect.getSize().width;
        int height = rect.getSize().height;

        int vx, vy;

        // draw upper and lower horizontal dashes
        for (vx = x; vx < (x + width); vx += 2) {
            g.drawLine(vx, y, vx, y);
            g.drawLine(vx, y + height - 1, vx, y + height - 1);
        }

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
    public static final void moveDir(String _dir, String _dest) {
        FileSystemView fileSystem = FileSystemView.getFileSystemView();

        File directory = new File(_dir);

        dir = _dir;
        dest = _dest;

        _moveDir(directory, fileSystem);
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
        return (distance(center.x, b.x) <= distance(center.y, b.y)) &&
            (center.y >= b.y);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on south from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isSouthernQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) <= distance(center.y, b.y)) &&
            (center.y <= b.y);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on east from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isEasternQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) >= distance(center.y, b.y)) &&
            (center.x <= b.x);
    }

    /**
     * 2D routine, that checks whether if given point is
     * on east from the second one. Beginning of coordiantes is
     * a top left corner of a screen.
     */
    public static boolean isWesternQuadrant(Point center, Point b) {
        return (distance(center.x, b.x) >= distance(center.y, b.y)) &&
            (center.x >= b.x);
    }

    /**
     * Finds maximum of two values
     */
    public static int max(int i, int j) {
        return (i < j) ? j : i;
    }

    /**
     * Finds minimum of two values
     */
    public static int min(int i, int j) {
        return (i < j) ? i : j;
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
        if (Workspace.getUI() instanceof WorkspaceGUI) {
            ((WorkspaceGUI) Workspace.getUI()).
                createActionChangeListener(b);
        }
        return b;
    }

    /**
     * Create menu item.
     */
    public static JMenuItem createMenuItem(ActionListener listener,
                                           String name, String actionCommand, Icon icon) {
        JMenuItem menu_item = new JMenuItem(name, icon);
        menu_item.addActionListener(listener);
        menu_item.setActionCommand(actionCommand);
        if (Workspace.getUI() instanceof WorkspaceGUI) {
            ((WorkspaceGUI) Workspace.getUI()).
                createActionChangeListener(menu_item);
        }
        return menu_item;
    }

    /**
     * Create menu item. This method installs small ICON
     * instead of action ICON in Swing.
     */
    public static JMenuItem createMenuItem(Action a) {
        JMenuItem menu_item = new JMenuItem(a);
        menu_item.setEnabled(a.isEnabled());
        Icon icon = (Icon) a.getValue(MENU_ICON);
        menu_item.setIcon(icon);
        if (Workspace.getUI() instanceof WorkspaceGUI) {
            ((WorkspaceGUI) Workspace.getUI()).
                createActionChangeListener(menu_item);
        }
        return menu_item;
    }

    /**
     * Create checkbox menu item.
     */
    public static JCheckBoxMenuItem createCheckboxMenuItem(Action a) {
        JCheckBoxMenuItem menu_item = new JCheckBoxMenuItem(a);
        menu_item.setEnabled(a.isEnabled());
        if (Workspace.getUI() instanceof WorkspaceGUI) {
            ((WorkspaceGUI) Workspace.getUI()).
                createActionChangeListener(menu_item);
        }
        return menu_item;
    }

    /**
     * Create radio menu item.
     */
    public static JRadioButtonMenuItem createRadioMenuItem(Action a) {
        JRadioButtonMenuItem menu_item = new JRadioButtonMenuItem(a);
        menu_item.setEnabled(a.isEnabled());
        if (Workspace.getUI() instanceof WorkspaceGUI) {
            ((WorkspaceGUI) Workspace.getUI()).
                createActionChangeListener(menu_item);
        }
        return menu_item;
    }
}