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

package com.hyperrealm.kiwi.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.PrintJob;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.AboutFrame;

/**
 * This class consists of several convenience routines and constants,
 * all of which are static.
 *
 * @author Mark Lindner
 */
@SuppressWarnings("unused")
public final class KiwiUtils {
    /**
     * The data transfer block size.
     */
    public static final int BLOCK_SIZE = 4096;
    /**
     * Empty insets (zero pixels on all sides).
     */
    public static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    /**
     * A default Kiwi border (empty, 5 pixels on all sides).
     */
    public static final EmptyBorder DEFAULT_BORDER = new EmptyBorder(5, 5, 5, 5);
    /**
     * A default Kiwi border (empty, 3 pixels on all sides).
     *
     * @since Kiwi 2.4
     */
    public static final EmptyBorder THIN_BORDER = new EmptyBorder(3, 3, 3, 3);
    /**
     * A default Kiwi border (empty, 0 pixels on all sides).
     */
    public static final EmptyBorder EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);
    /**
     * Predefined insets for first component on first or subsequent lines.
     * Defines 5 pixels of space to the right and below.
     */
    public static final Insets FIRST_INSETS = new Insets(0, 0, 5, 5);
    /**
     * Predefined insets for last component on first or subsequent lines.
     * Defines 5 pixels of space below.
     */
    public static final Insets LAST_INSETS = new Insets(0, 0, 5, 0);
    /**
     * Predefined insets for first component on last line.
     * Defines 5 pixels of space to the right.
     */
    public static final Insets FIRST_BOTTOM_INSETS = new Insets(0, 0, 0, 5);
    /**
     * Predefined insets for last component on last line.
     * Defines no pixels of space.
     */
    public static final Insets LAST_BOTTOM_INSETS = EMPTY_INSETS;
    /**
     * The root of the filesystem.
     */
    public static final File FILESYSTEM_ROOT = new File(System.getProperty("file.separator"));
    /**
     * An origin point: (0,0)
     */
    public static final Point ORIGIN = new Point(0, 0);
    /**
     * Milliseconds in one second
     */
    public static final int MILLISEC_IN_SECOND = 1000;
    /**
     *
     */
    private static final String DEFAULT_FONT_NAME = "Dialog";
    /**
     * A default bold font.
     */
    public static final Font BOLD_FONT = new Font(DEFAULT_FONT_NAME, Font.BOLD, 12);
    /**
     * A default plain font.
     */
    public static final Font PLAIN_FONT = new Font(DEFAULT_FONT_NAME, Font.PLAIN, 12);
    /**
     * A default italic font.
     */
    public static final Font ITALIC_FONT = new Font(DEFAULT_FONT_NAME, Font.ITALIC, 12);
    /**
     *
     */
    private static final double PRINT_SCALE_FACTOR = 0.75;
    /**
     *
     */
    private static final int DEFAULT_OFFSET = 40;
    /**
     *
     */
    private static final int STACK_INITIAL_SIZE = 256;
    /**
     *
     */
    private static Random rand = new Random(System.currentTimeMillis());
    /**
     * A phantom Frame.
     */
    private static Frame phantomFrame = null;

    private static Cursor lastCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    private static Clipboard clipboard = null;

    private static ResourceManager resourceManager = null;

    private static AboutFrame aboutFrame = null;

    private KiwiUtils() {
    }

    /**
     * Paint a component immediately. Paints a component immediately (as opposed
     * to queueing a repaint request in the event queue.)
     *
     * @param c The component to repaint.
     */

    public static void paintImmediately(Component c) {
        Graphics gc = c.getGraphics();
        if (gc != null) {
            c.paint(gc);
            gc.dispose();
        }
    }

    /**
     * Paint a component immediately. Paints a component immediately (as opposed
     * to queueing a repaint request in the event queue.)
     *
     * @param c The component to repaint.
     */

    public static void paintImmediately(JComponent c) {
        Rectangle rect = new Rectangle();
        c.computeVisibleRect(rect);
        c.paintImmediately(rect);
    }

    /**
     * Cascade a window off of a parent window. Moves a window a specified
     * number of pixels below and to the right of another window.
     *
     * @param parent The parent window.
     * @param w      The window to cascade.
     * @param offset The number of pixels to offset the window by
     *               vertically and horizontally.
     */

    public static void cascadeWindow(Window parent, Window w, int offset) {
        doCascadeWindow(parent, w, offset, offset);
    }

    /**
     * Cascade a window off of a parent window. Moves a window a specified
     * number of pixels below and to the right of another window.
     *
     * @param parent  The parent window.
     * @param w       The window to cascade.
     * @param offsetx The number of pixels to offset the window by horizontally.
     * @param offsety The number of pixels to offset the window by vertically.
     */

    public static void cascadeWindow(Window parent, Window w, int offsetx,
                                     int offsety) {
        doCascadeWindow(parent, w, offsetx, offsety);
    }

    /**
     * Cascade a window off of a parent window. Moves a window 40 pixels below
     * and to the right of another window.
     *
     * @param parent The parent window.
     * @param w      The window to cascade.
     */

    public static void cascadeWindow(Window parent, Window w) {
        doCascadeWindow(parent, w, DEFAULT_OFFSET, DEFAULT_OFFSET);
    }

    /**
     * Cascade a window off of a component's parent window.
     *
     * @param w The window to cascade.
     * @param c The component off whose parent window this window should be
     *          cascaded. If a window cannot be found in the component hierarchy above
     *          <code>c</code>, the window is centered on the screen.
     */

    public static void cascadeWindow(Component c, Window w) {
        Window pw = SwingUtilities.windowForComponent(c);

        if (pw == null) {
            KiwiUtils.centerWindow(w);
        } else {
            KiwiUtils.cascadeWindow(pw, w);
        }
    }

    /* common window cascading code */

    private static void doCascadeWindow(Window parent,
                                        Window w,
                                        int offsetx,
                                        int offsety) {
        Dimension sSize, wSize;
        Point loc = parent.getLocation();
        loc.translate(offsetx, offsety);

        positionWindow(w, loc.x, loc.y);
    }

    /**
     * Center a window on the screen.
     *
     * @param w The window to center.
     */

    public static void centerWindow(Window w) {
        Dimension sSize, wSize;
        int x, y;

        sSize = w.getToolkit().getScreenSize();
        wSize = w.getSize();

        x = (sSize.width - wSize.width) / 2;
        y = (sSize.height - wSize.height) / 2;

        positionWindow(w, x, y);
    }

    /*
     * Make sure no part of the window is off-screen.
     */

    private static void positionWindow(Window w, int x, int y) {
        Dimension sSize, wSize;

        int xInt = x;
        int yInt = y;

        sSize = w.getToolkit().getScreenSize();
        wSize = w.getSize();

        if (x < 0) {
            xInt = 0;
        }

        if (y < 0) {
            yInt = 0;
        }

        int xe = (xInt + wSize.width) - sSize.width;
        int ye = (yInt + wSize.height) - sSize.height;

        if (xe > 0) {
            xInt -= xe;
        }
        if (ye > 0) {
            yInt -= ye;
        }

        w.setLocation(xInt, yInt);
    }

    /**
     * Center a window within the bounds of another window.
     *
     * @param w      The window to center.
     * @param parent The window to center within.
     */

    public static void centerWindow(Window parent, Window w) {
        if (!parent.isVisible()) {
            centerWindow(w);
            return;
        }

        Dimension pSize, wSize;
        int x, y;

        pSize = parent.getSize();
        wSize = w.getSize();
        Point pLoc = parent.getLocationOnScreen();

        x = ((pSize.width - wSize.width) / 2) + pLoc.x;
        y = ((pSize.height - wSize.height) / 2) + pLoc.y;

        positionWindow(w, x, y);
    }

    /**
     * Center a window within the bounds of a component's parent window.
     *
     * @param w The window to center.
     * @param c The component within whose parent window this window should be
     *          centered. If a window cannot be found in the component hierarchy above
     *          <code>c</code>, the window is centered on the screen.
     */

    public static void centerWindow(Component c, Window w) {
        Window pw = SwingUtilities.windowForComponent(c);

        if (pw == null) {
            KiwiUtils.centerWindow(w);
        } else {
            KiwiUtils.centerWindow(pw, w);
        }
    }

    /**
     * Turn on a busy cursor.
     *
     * @param c The component whose cursor will be changed.
     */

    public static void busyOn(Component c) {
        lastCursor = c.getCursor();
        c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     * Turn off the busy cursor. The last cursor saved will be restored.
     *
     * @param c The component whose cursor will be changed.
     */

    public static void busyOff(Component c) {
        c.setCursor(lastCursor);
    }

    /**
     * Get the Frame parent of a component. This method searches upward in the
     * component hierarchy, searching for an ancestor that is a Frame.
     */

    public static Frame getFrameForComponent(Component c) {
        Component cInt = c;
        while ((cInt = cInt.getParent()) != null) {
            if (cInt instanceof Frame) {
                return ((Frame) cInt);
            }
        }

        return (null);
    }

    /**
     * Get the Window parent of a component. This method searches upward in the
     * component hierarchy, searching for an ancestor that is a Frame.
     *
     * @since Kiwi 2.0
     */

    public static Window getWindowForComponent(Component c) {
        Component cInt = c;
        while ((cInt = cInt.getParent()) != null) {
            if (c instanceof Window) {
                return ((Window) cInt);
            }
        }

        return (null);
    }

    /**
     * Print a hardcopy of the contents of a window.
     *
     * @param window The window to print.
     * @param title  A title for the print job.
     * @return <code>true</code> if the print job was started, or
     * <code>false</code> if the user cancelled the print dialog.
     */

    public static boolean printWindow(Window window, String title) {
        PrintJob pj = Toolkit.getDefaultToolkit()
            .getPrintJob(getPhantomFrame(), title, null);

        if (pj == null) {
            return (false);
        }

        int res = Toolkit.getDefaultToolkit().getScreenResolution();
        Dimension d = pj.getPageDimension(); // buggy in JDK 1.1.x

        d.width -= (int) (res * PRINT_SCALE_FACTOR /*in*/);
        d.height -= (int) (res * PRINT_SCALE_FACTOR /*in*/);

        Graphics gc = pj.getGraphics();
        window.paint(gc);

        gc.dispose();
        pj.end();
        return (true);
    }

    /**
     * Get a reference to the system clipboard.
     *
     * @return The clipboard.
     */

    public static Clipboard getClipboard() {
        if (clipboard == null) {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }

        return (clipboard);
    }

    /**
     * Copy text from the system clipboard.
     *
     * @return The text that is in the clipboard, or <code>null</code> if the
     * clipboard is empty or does not contain plain text.
     */

    public static synchronized String getClipboardText() {
        try {
            return ((String) (getClipboard().getContents(Void.class)
                .getTransferData(DataFlavor.stringFlavor)));
        } catch (Exception ex) {
            return (null);
        }
    }

    /**
     * Copy text to the system clipboard.
     *
     * @param text The text to copy to the clipboard.
     */

    public static synchronized void setClipboardText(String text) {
        StringSelection sel = new StringSelection(text);
        getClipboard().setContents(sel, sel);
    }

    /**
     * Get a reference to the internal resource manager singleton. The Kiwi
     * Toolkit has a small built-in collection of textures, icons, audio clips,
     * and HTML files.
     */

    public static ResourceManager getResourceManager() {
        if (resourceManager == null) {
            resourceManager = new ResourceManager(com.hyperrealm.kiwi.ResourceAnchor.class);
        }

        return (resourceManager);
    }

    /**
     * Suspend the calling thread. Suspends the calling thread, returning
     * immediately if an exception occurs.
     *
     * @param sec The number of seconds to sleep.
     */

    public static void sleep(int sec) {
        try {
            Thread.sleep(sec * MILLISEC_IN_SECOND);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Get a reference to a phantom frame.
     *
     * @return The phantom frame.
     */

    public static Frame getPhantomFrame() {
        if (phantomFrame == null) {
            phantomFrame = new Frame();
        }

        return (phantomFrame);
    }

    /**
     * Get an instance to a prebuilt about window that describes the Kiwi
     * Toolkit itself.
     */

    public static AboutFrame getKiwiAboutFrame() {
        if (aboutFrame == null) {
            aboutFrame = new AboutFrame("About Kiwi",
                getResourceManager().getURL("kiwi.html"),
                true);
        }

        return (aboutFrame);
    }

    /**
     * Recursively delete files in a directory. Deletes all files and
     * subdirectories in the given directory.
     *
     * @param parent The parent (presumed to be a directory) of the files to
     *               be deleted. The parent is not deleted.
     * @return The number of files and directories deleted.
     */

    public static int deleteTree(File parent) {
        int ct = 0;

        if (!parent.isDirectory()) {
            return (0);
        }

        String[] files = parent.list();
        if (files != null) {

            for (String file : files) {
                File f = new File(parent.getAbsolutePath(), file);

                if (f.isDirectory()) {
                    ct += deleteTree(f);
                }

                if (f.delete()) {
                    ct++;
                }
            }
        }

        return (ct);
    }

    /**
     * Recursively set the font on a container and all of its descendant
     * components.
     *
     * @param container The container.
     * @param font      The new font.
     * @since Kiwi 1.4.3
     */

    public static void setFonts(Container container, Font font) {
        container.setFont(font);

        Component[] components = container.getComponents();

        for (Component component : components) {
            if (component instanceof Container) {
                setFonts((Container) component, font);
            } else {
                component.setFont(font);
            }
        }
    }

    /**
     * Set the default cross-platform Look and Feel
     *
     * @since Kiwi 1.4.3
     */

    public static void setDefaultLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    /**
     * Set the native (system) Look and Feel
     *
     * @since Kiwi 1.4.3
     */

    public static void setNativeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    /**
     * Return a random integer in the range 0 to <i>range</i> - 1.
     *
     * @since Kiwi 2.0
     */

    public static int randomInt(int range) {
        return (rand.nextInt(range));
    }

    /**
     * Get a String representation of a stack trace for a throwable object.
     *
     * @param t The throwable.
     * @return The stack trace, as a string.
     * @since Kiwi 2.0
     */
    @SuppressWarnings("Regexp")
    public static String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter(STACK_INITIAL_SIZE);
        PrintWriter pw = new PrintWriter(sw);

        t.printStackTrace(pw);

        return (sw.toString());
    }

}
