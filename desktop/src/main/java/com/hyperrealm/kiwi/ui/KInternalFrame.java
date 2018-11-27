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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

import com.hyperrealm.kiwi.util.*;

/** <code>KInternalFrame</code> is a trivial extension of
 * <code>JInternalFrame</code> that provides support for tiling the background
 * of the frame with an image.
 * <p>
 * The method <code>getMainContainer()</code> will return the frame's
 * <code>KPanel</code>. Add child components to the frame by adding
 * them to this <code>KPanel</code>.
 *
 * @see com.hyperrealm.kiwi.ui.KPanel
 *
 * @author Mark Lindner
 */

public class KInternalFrame extends JInternalFrame
{
  private KPanel _main;
  private _PropertyChangeListener propListener;

  /** Construct a new <code>KInternalFrame</code> that is non-resizable,
   * non-closable, non-maximizable, non-iconifiable, and with no title. */

  public KInternalFrame()
  {
    this("", false, false, false, false);
  }

  /** Construct a new <code>KInternalFrame</code> that is non-resizable,
   * non-closable, non-maximizable, non-iconifiable, and with the specified
   * title.
   *
   * @param title The title for the frame.
   */

  public KInternalFrame(String title)
  {
    this(title, false, false, false, false);
  }

  /** Construct a new <code>KInternalFrame</code> that is non-closable,
   * non-maximizable, non-iconifiable, and with the specified resizability and
   * title.
   *
   * @param title The title for the frame.
   * @param resizable A flag specifying whether the frame will be resizable.
   */
  
  public KInternalFrame(String title, boolean resizable)
  {
    this(title, resizable, false, false, false);
  }

  /** Construct a new <code>KInternalFrame</code> that is non-maximizable,
   * non-iconifiable, and with the specified resizability, closability, and
   * title.
   *
   * @param title The title for the frame.
   * @param resizable A flag specifying whether the frame will be resizable.
   * @param closable A flag specifying whether the frame will be closable.
   */
  
  public KInternalFrame(String title, boolean resizable, boolean closable)
  {
    this(title, resizable, closable, false, false);
  }

  /** Construct a new <code>KInternalFrame</code> that is non-iconifiable, and
   * with the specified resizability, closability, maximizability, and
   * title.
   *
   * @param title The title for the frame.
   * @param resizable A flag specifying whether the frame will be resizable.
   * @param closable A flag specifying whether the frame will be closable.
   * @param maximizable A flag specifying whether the frame will be
   * maximizable.
   */
  
  public KInternalFrame(String title, boolean resizable, boolean closable,
                        boolean maximizable)
  {
    this(title, resizable, closable, maximizable, false);
  }

  /** Construct a new <code>KInternalFrame</code> with the specified
   * resizability, closability, maximizability, iconafiability, and title.
   *
   * @param title The title for the frame.
   * @param resizable A flag specifying whether the frame will be resizable.
   * @param closable A flag specifying whether the frame will be closable.
   * @param maximizable A flag specifying whether the frame will be
   * maximizable.
   * @param iconifiable A flag specifying whether the frame will be
   * iconifiable.
   */
  
  public KInternalFrame(String title, boolean resizable, boolean closable,
                        boolean maximizable, boolean iconifiable)
  {
    super(title, resizable, closable, maximizable, iconifiable);

    addInternalFrameListener(new _WindowListener());
    
    ResourceManager rm = KiwiUtils.getResourceManager();
    getContentPane().setLayout(new GridLayout(1, 0));
    _main = new KPanel(UIChangeManager.getInstance()
                       .getDefaultTexture());
    _main.setOpaque(true);
    getContentPane().add(_main);
    setFrameIcon(rm.getIcon("kiwi.png"));

    UIChangeManager.getInstance().registerComponent(getRootPane());
    propListener = new _PropertyChangeListener();
    UIChangeManager.getInstance().addPropertyChangeListener(propListener);
  }

  /** Get a reference to the main container (in this case, the
   * <code>KPanel</code> that is the child of the frame's content pane).
   */

  public KPanel getMainContainer()
  {
    return(_main);
  }

  /** Set the background texture.
   *
   * @param image The image to use as the background texture for the frame.
   */

  public void setTexture(Image image)
  {
    _main.setTexture(image);
    invalidate();
    repaint();
  }

  /** Set the background color.
   *
   * @param color The new background color.
   */
  
  public void setColor(Color color)
  {
    _main.setBackground(color);
  }

  /** Called in response to a frame close event to determine if this frame
   * may be closed.
   *
   * @return <code>true</code> if the frame is allowed to close, and
   * <code>false</code> otherwise. The default implementation returns
   * <code>true</code>.
   */
  
  protected boolean canClose()
  {
    return(true);
  }

  /** Show or hide the frame.
   *
   * @param flag A flag specifying whether the frame should be shown or
   * hidden. If <code>true</code>, the <code>startFocus()</code> method is
   * called to allow the subclasser to request focus for a given child
   * component.
   *
   * @see #startFocus
   */
  
  public void setVisible(boolean flag)
  {
    if(flag)
      startFocus();

    super.setVisible(flag);
  }
  
  /** This method is called when the frame is made visible; it should
   * transfer focus to the appropriate child component. The default
   * implementation does nothing.
   */
  
  protected void startFocus()
  {
  }
  
  /** Turn the busy cursor on or off for this window.
   *
   * @param flag If <code>true</code>, the wait cursor will be set for this
   * window, otherwise the default cursor will be set.
   */
  
  public void setBusyCursor(boolean flag)
  {
    setCursor(Cursor.getPredefinedCursor(flag ? Cursor.WAIT_CURSOR
                                         : Cursor.DEFAULT_CURSOR));
  }
  
  /** Destroy this frame. Call this method when the frame is no longer needed.
   * The frame will detach its listeners from the
   * <code>UIChanageManager</code>.
   */

  public void destroy()
  {
    UIChangeManager.getInstance().unregisterComponent(getRootPane());
    UIChangeManager.getInstance().removePropertyChangeListener(propListener);
  }

  /* WindowListener */
  
  private class _WindowListener extends InternalFrameAdapter
  {
    public void internalFrameClosing(InternalFrameEvent evt)
    {
      if(canClose())
      {
        setVisible(false);
        dispose();
      }
    }
  }

  /* PropertyChangeListener */

  private class _PropertyChangeListener implements PropertyChangeListener
  {
    public void propertyChange(PropertyChangeEvent evt)
    {
      if(evt.getPropertyName().equals(UIChangeManager.TEXTURE_PROPERTY))
        setTexture((Image)evt.getNewValue());
    }
  }

}

/* end of source file */
