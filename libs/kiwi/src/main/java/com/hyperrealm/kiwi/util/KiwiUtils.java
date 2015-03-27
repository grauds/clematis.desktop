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

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.AboutFrame;

/** This class consists of several convenience routines and constants,
 * all of which are static.
 *
 * @author Mark Lindner
 */

public final class KiwiUtils
{
  private static Random _rand = new Random(System.currentTimeMillis());
  
  /** The data transfer block size. */
  public static final int blockSize = 4096;

  /** A phantom Frame. */
  private static Frame phantomFrame = null;

  /** Empty insets (zero pixels on all sides). */
  public static final Insets emptyInsets = new Insets(0, 0, 0, 0);

  /** A default Kiwi border (empty, 5 pixels on all sides). */
  public static final EmptyBorder defaultBorder = new EmptyBorder(5, 5, 5, 5);

  /** A default Kiwi border (empty, 3 pixels on all sides).
   * @since Kiwi 2.4
   */
  public static final EmptyBorder thinBorder = new EmptyBorder(3, 3, 3, 3);
  
  /** A default Kiwi border (empty, 0 pixels on all sides). */
  public static final EmptyBorder emptyBorder = new EmptyBorder(0, 0, 0, 0);

  /** Predefined insets for first component on first or subsequent lines.
   * Defines 5 pixels of space to the right and below.
   */
  public static final Insets firstInsets = new Insets(0, 0, 5, 5);

  /** Predefined insets for last component on first or subsequent lines.
   * Defines 5 pixels of space below.
   */
  public static final Insets lastInsets = new Insets(0, 0, 5, 0);

  /** Predefined insets for first component on last line.
   * Defines 5 pixels of space to the right.
   */
  public static final Insets firstBottomInsets = new Insets(0, 0, 0, 5);

  /** Predefined insets for last component on last line.
   * Defines no pixels of space.
   */
  public static final Insets lastBottomInsets = emptyInsets;  
  
  /** The root of the filesystem. */
  public static final File filesystemRoot
    = new File(System.getProperty("file.separator"));

  /** A default bold font. */
  public static final Font boldFont = new Font("Dialog", Font.BOLD, 12);

  /** A default plain font. */
  public static final Font plainFont = new Font("Dialog", Font.PLAIN, 12);

  /** A default italic font. */
  public static final Font italicFont = new Font("Dialog", Font.ITALIC, 12);

  /** An origin point: (0,0) */
  public static final Point origin = new Point(0, 0);
  
  private static Cursor lastCursor
    = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
  private static Clipboard clipboard = null;
  private static ResourceManager resmgr = null;
  private static AboutFrame aboutFrame = null;
  
  private KiwiUtils() { }

  /** Paint a component immediately. Paints a component immediately (as opposed
   * to queueing a repaint request in the event queue.)
   *
   * @param c The component to repaint.
   */

  public static final void paintImmediately(Component c)
  {
    Graphics gc = c.getGraphics();
    if(gc != null)
    {
      c.paint(gc);
      gc.dispose();
    }
  }

  /** Paint a component immediately. Paints a component immediately (as opposed
   * to queueing a repaint request in the event queue.)
   *
   * @param c The component to repaint.
   */

  public static final void paintImmediately(JComponent c)
  {
    Rectangle rect = new Rectangle();
    c.computeVisibleRect(rect);
    c.paintImmediately(rect);
  }
  
  /** Cascade a window off of a parent window. Moves a window a specified
   * number of pixels below and to the right of another window.
   *
   * @param parent The parent window.
   * @param w The window to cascade.
   * @param offset The number of pixels to offset the window by
   * vertically and horizontally.
   */
  
  public static final void cascadeWindow(Window parent, Window w, int offset)
  {
    _cascadeWindow(parent, w, offset, offset);
  }

  /** Cascade a window off of a parent window. Moves a window a specified
   * number of pixels below and to the right of another window.
   *
   * @param parent The parent window.
   * @param w The window to cascade.
   * @param offsetx The number of pixels to offset the window by horizontally.
   * @param offsety The number of pixels to offset the window by vertically.
   */

  public static final void cascadeWindow(Window parent, Window w, int offsetx,
                                         int offsety)
  {
    _cascadeWindow(parent, w, offsetx, offsety);
  }

  /** Cascade a window off of a parent window. Moves a window 40 pixels below
   * and to the right of another window.
   *
   * @param parent The parent window.
   * @param w The window to cascade.
   */

  public static final void cascadeWindow(Window parent, Window w)
  {
    _cascadeWindow(parent, w, 40, 40);
  }

  /** Cascade a window off of a component's parent window.
   *
   * @param w The window to cascade.
   * @param c The component off whose parent window this window should be
   * cascaded. If a window cannot be found in the component hierarchy above
   * <code>c</code>, the window is centered on the screen.
   */
  
  public static final void cascadeWindow(Component c, Window w)
  {
    Window pw = SwingUtilities.windowForComponent(c);

    if(pw == null)
      KiwiUtils.centerWindow(w);
    else
      KiwiUtils.cascadeWindow(pw, w);
  }
  
  /* common window cascading code */

  private static void _cascadeWindow(Window parent, Window w, int offsetx,
                                     int offsety)
  {
    Dimension s_size, w_size;
    Point loc = parent.getLocation();
    loc.translate(offsetx, offsety);
    
    _positionWindow(w, loc.x, loc.y);
  }

  /** Center a window on the screen.
   *
   * @param w The window to center.
   */
  
  public static final void centerWindow(Window w)
  {
    Dimension s_size, w_size;
    int x, y;

    s_size = w.getToolkit().getScreenSize();
    w_size = w.getSize();

    x = (s_size.width - w_size.width) / 2;
    y = (s_size.height - w_size.height) / 2;

    _positionWindow(w, x, y);
  }

  /*
   * Make sure no part of the window is off-screen.
   */

  private static final void _positionWindow(Window w, int x, int y)
  {
    Dimension s_size, w_size;

    s_size = w.getToolkit().getScreenSize();
    w_size = w.getSize();

    if(x < 0)
      x = 0;

    if(y < 0)
      y = 0;
    
    int xe = (x + w_size.width) - s_size.width;
    int ye = (y + w_size.height) - s_size.height;

    if(xe > 0)
      x -= xe;
    if(ye > 0)
      y -= ye;
    
    w.setLocation(x, y);    
  }
  
  /** Center a window within the bounds of another window.
   *
   * @param w The window to center.
   * @param parent The window to center within.
   */

  public static final void centerWindow(Window parent, Window w)
  {
    if(! parent.isVisible())
    {
      centerWindow(w);
      return;
    }
    
    Dimension p_size, w_size, s_size;
    int x, y;

    p_size = parent.getSize();
    w_size = w.getSize();
    s_size = w.getToolkit().getScreenSize();
    Point p_loc = parent.getLocationOnScreen();

    x = ((p_size.width - w_size.width) / 2) + p_loc.x;
    y = ((p_size.height - w_size.height) / 2) + p_loc.y;

    _positionWindow(w, x, y);
  }

  /** Center a window within the bounds of a component's parent window.
   *
   * @param w The window to center.
   * @param c The component within whose parent window this window should be
   * centered. If a window cannot be found in the component hierarchy above
   * <code>c</code>, the window is centered on the screen.
   */
  
  public static final void centerWindow(Component c, Window w)
  {
    Window pw = SwingUtilities.windowForComponent(c);

    if(pw == null)
      KiwiUtils.centerWindow(w);
    else
      KiwiUtils.centerWindow(pw, w);
  }

  /** Turn on a busy cursor.
   *
   * @param c The component whose cursor will be changed.
   */
  
  public static final void busyOn(Component c)
  {
    lastCursor = c.getCursor();
    c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }

  /** Turn off the busy cursor. The last cursor saved will be restored.
   *
   * @param c The component whose cursor will be changed.
   */

  public static final void busyOff(Component c)
  {
    c.setCursor(lastCursor);
  }

  /** Get the Frame parent of a component. This method searches upward in the
   * component hierarchy, searching for an ancestor that is a Frame.
   */

  public static final Frame getFrameForComponent(Component c)
  {
    while((c = c.getParent()) != null)
      if(c instanceof Frame)
        return((Frame)c);

    return(null);
  }

  /** Get the Window parent of a component. This method searches upward in the
   * component hierarchy, searching for an ancestor that is a Frame.
   *
   * @since Kiwi 2.0
   */

  public static final Window getWindowForComponent(Component c)
  {
    while((c = c.getParent()) != null)
      if(c instanceof Window)
        return((Window)c);

    return(null);
  }
  
  /** Print a hardcopy of the contents of a window.
   *
   * @param window The window to print.
   * @param title A title for the print job.
   *
   * @return <code>true</code> if the print job was started, or
   * <code>false</code> if the user cancelled the print dialog.
   */

  public static final boolean printWindow(Window window, String title)
  {
    PrintJob pj = Toolkit.getDefaultToolkit()
      .getPrintJob(getPhantomFrame(), title, null);

    if(pj == null)
      return(false);

    int res = Toolkit.getDefaultToolkit().getScreenResolution();
    Dimension d = pj.getPageDimension(); // buggy in JDK 1.1.x
    //Dimension d = new Dimension((int)(2 * res * 8.5), (int)(res * 11 * 2));
    
    d.width -= (int)(res * 0.75 /*in*/);
    d.height -= (int)(res * 0.75 /*in*/);
    
    Graphics gc = pj.getGraphics();
    window.paint(gc);

    gc.dispose();
    pj.end();
    return(true);
  }

  /** Get a reference to the system clipboard.
   *
   * @return The clipboard.
   */

  public static final Clipboard getClipboard()
  {
    if(clipboard == null)
      clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    return(clipboard);
  }
  
  /** Copy text to the system clipboard.
   *
   * @param text The text to copy to the clipboard.
   */

  public static synchronized final void setClipboardText(String text)
  {
    StringSelection sel = new StringSelection(text);
    getClipboard().setContents(sel, sel);
  }

  /** Copy text from the system clipboard.
   *
   * @return The text that is in the clipboard, or <code>null</code> if the
   * clipboard is empty or does not contain plain text.
   */

  public static synchronized final String getClipboardText()
  {
    try
    {
      return((String)(getClipboard().getContents(Void.class)
                      .getTransferData(DataFlavor.stringFlavor)));
    }
    catch(Exception ex)
    {
      return(null);
    }
  }

  /** Get a reference to the internal resource manager singleton. The Kiwi
   * Toolkit has a small built-in collection of textures, icons, audio clips,
   * and HTML files.
   */

  public static final ResourceManager getResourceManager()
  {
    if(resmgr == null)
      resmgr = new ResourceManager(com.hyperrealm.kiwi.ResourceAnchor.class);

    return(resmgr);
  }

  /** Suspend the calling thread. Suspends the calling thread, returning
   * immediately if an exception occurs.
   *
   * @param sec The number of seconds to sleep.
   */
  
  public static final void sleep(int sec)
  {
    try
    {
      Thread.currentThread().sleep(sec * 1000);
    }
    catch(InterruptedException ex)
    {
    }
  }

  /** Get a reference to a phantom frame.
   *
   * @return The phantom frame.
   */

  public static final Frame getPhantomFrame()
  {
    if(phantomFrame == null)
      phantomFrame = new Frame();

    return(phantomFrame);
  }
  
  /** Get an instance to a prebuilt about window that describes the Kiwi
   * Toolkit itself.
   */
  
  public static final AboutFrame getKiwiAboutFrame()
  {
    if(aboutFrame == null)
      aboutFrame = new AboutFrame("About Kiwi",
                                  getResourceManager().getURL("kiwi.html"),
                                  true);

    return(aboutFrame);
  }

  /** Recursively delete files in a directory. Deletes all files and
   * subdirectories in the given directory.
   *
   * @param parent The parent (presumed to be a directory) of the files to
   * be deleted. The parent is not deleted.
   *
   * @return The number of files and directories deleted.
   */

  public static final int deleteTree(File parent)
  {
    int ct = 0;
    
    if(!parent.isDirectory()) return(0);
    
    String files[] = parent.list();
    for(int i = 0; i < files.length; i++)
    {
      File f = new File(parent.getAbsolutePath(), files[i]);
      
      if(f.isDirectory())
        ct += deleteTree(f);

      f.delete();
      ct++;
    }
    
    return(ct);
  }

  /**
   * Recursively set the font on a container and all of its descendant
   * components.
   *
   * @param container The container.
   * @param font The new font.
   *
   * @since Kiwi 1.4.3
   */
  
  public static void setFonts(Container container, Font font)
  {
    container.setFont(font);

    Component[] components = container.getComponents();

    for(int i = 0; i < components.length; i++)
    {
      if(Container.class.isInstance(components[i]))
        setFonts((Container)components[i], font);
      else
        components[i].setFont(font);
    }
  }

  /**
   * Set the default cross-platform Look and Feel
   *
   * @since Kiwi 1.4.3
   */

  public static void setDefaultLookAndFeel()
  {
    try
    {
      UIManager.setLookAndFeel(
        UIManager.getCrossPlatformLookAndFeelClassName());
    }
    catch(Exception ex) { }
  }

  /**
   * Set the native (system) Look and Feel
   *
   * @since Kiwi 1.4.3
   */

  public static void setNativeLookAndFeel()
  {
    try
    {
      UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception ex) { }
  }

  /**
   * Return a random integer in the range 0 to <i>range</i> - 1.
   *
   * @since Kiwi 2.0
   */

  public static int randomInt(int range)
  {
    return(_rand.nextInt(range));
  }

  /**
   * Get a String representation of a stack trace for a throwable object.
   *
   * @param t The throwable.
   * @return The stack trace, as a string.
   *
   * @since Kiwi 2.0
   */

  public static String stackTraceToString(Throwable t)
  {
    StringWriter sw = new StringWriter(256);
    PrintWriter pw = new PrintWriter(sw);

    t.printStackTrace(pw);

    return(sw.toString());
  }
  
}

/* end of source file */
