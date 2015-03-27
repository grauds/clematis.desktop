/* ----------------------------------------------------------------------------
   The Kiwi Toolkit
   Copyright (C) 1998-2001 Mark A. Lindner

   This file is part of Kiwi.

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   mark_a_lindner@yahoo.com
   ----------------------------------------------------------------------------
   $Log: KDialog.java,v $
   Revision 1.8  2001/03/12 05:19:58  markl
   Source code cleanup.

   Revision 1.7  2000/12/18 23:37:29  markl
   Added new constructors.

   Revision 1.6  1999/07/19 04:09:52  markl
   Fixed listener problem, renamed dispose() to destroy().

   Revision 1.5  1999/07/12 08:50:21  markl
   Listen for texture change events.

   Revision 1.4  1999/07/06 09:18:24  markl
   Added cursor method.

   Revision 1.3  1999/06/03 06:47:47  markl
   Added startFocus() method.

   Revision 1.2  1999/01/10 03:22:17  markl
   added GPL header & RCS tag
   ----------------------------------------------------------------------------
*/

package kiwi.ui.dialog;

import java.awt.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;

import kiwi.event.*;
import kiwi.util.*;
import kiwi.ui.*;

/** <code>KDialog</code> is a trivial extension of <code>JDialog</code>
  * that provides support for tiling the background of the dialog with an
  * image and for firing dismissal events.
  * <p>
  * <code>KDialog</code> introduces the notion of a <i>cancelled</i>
  * dialog versus an <i>accepted</i> dialog. Collectively, these are known as
  * <i>dialog dismissals</i>. A dialog may be <i>cancelled</i> by
  * pressing a <i>Cancel</i> button or by closing the dialog window
  * altogether. A dialog may be <i>accepted</i> by pressing an <i>OK</i> button
  * or entering a value in one of the dialog's input components. It is
  * ultimately up to the subclasser to determine what  constitutes a dialog
  * dismissal. The convenience method <code>fireDialogDismissed()</code> is
  * provided to generate dialog dismissal events. See
  * <code>ComponentDialog</code> for an example of this functionality.
  *
  * <p><center>
  * <img src="snapshot/KDialog.gif"><br>
  * <i>An example KDialog.</i>
  * </center>
  *
  * @see kiwi.ui.KPanel
  * @see kiwi.ui.KFrame
  * @see kiwi.ui.dialog.ComponentDialog
  * @see kiwi.event.DialogDismissEvent
  *
  * @author Mark Lindner
  * @author PING Software Group
  */

public class KDialog extends JDialog
  {
  private KPanel _main;
  private _PropertyChangeListener propListener;
  private Vector _listeners = new Vector();

  /** Construct a new <code>KDialog</code>.
    *
    * @param parent The parent dialog for this dialog.
    * @param title The title for this dialog.
    * @param modal A flag specifying whether this dialog should be modal.
    */

  public KDialog(Dialog parent, String title, boolean modal)
    {
    super(parent, title, modal);

    _init();
    }

  /** Construct a new <code>KDialog</code>.
    *
    * @param parent The parent frame for this dialog.
    * @param title The title for this dialog.
    * @param modal A flag specifying whether this dialog should be modal.
    */

  public KDialog(Frame parent, String title, boolean modal)
    {
    super(parent, title, modal);

    _init();
    }

  /*
   * Common initialization.
   */

  private void _init()
    {
    ResourceManager rm = KiwiUtils.getResourceManager();
    getContentPane().setLayout(new GridLayout(1, 0));
    _main = new KPanel(UIChangeManager.getDefaultTexture());
    _main.setOpaque(true);
    getContentPane().add(_main);
    //setIconImage(rm.getImage("kiwi-icon.gif"));

    UIChangeManager.getInstance().registerComponent(getRootPane());
    propListener = new _PropertyChangeListener();
    UIChangeManager.getInstance().addPropertyChangeListener(propListener);
    }

  /** Get a reference to the main container (in this case, the
    * <code>KPanel</code> that is the child of the frame's content pane).
    */

  protected KPanel getMainContainer()
    {
    return(_main);
    }
    /**
     * Each KIWI dialog should be able to center
     * itself on screen.
     * author: Anton Troshin aka Tysinsh.
     */
    public void centerDialog()
    {
      Dimension screenSize = this.getToolkit().getScreenSize();
      Dimension size = this.getSize();
      screenSize.height = screenSize.height / 2;
      screenSize.width = screenSize.width / 2;
      size.height = size.height / 2;
      size.width = size.width / 2;
      int y = screenSize.height - size.height;
      int x = screenSize.width - size.width;
      this.setLocation(x, y);
    }
  /** Set the background image for the dialog.
   *
   * @param image The new background image.
    */

  public void setTexture(Image image)
    {
    _main.setTexture(image);
    invalidate();
    validate();
    repaint();
    }

  /** Add a <code>DialogDismissListener</code> to this dialog's list of
    * listeners.
    *
    * @param listener The listener to add.
    * @see #removeDialogDismissListener
    */

  public void addDialogDismissListener(DialogDismissListener listener)
    {
    _listeners.addElement(listener);
    }

  /** Remove a <code>DialogDismissListener</code> from this dialog's list
    * of listeners.
    *
    * @param listener The listener to remove.
    * @see #addDialogDismissListener
    */

  public void removeDialogDismissListener(DialogDismissListener listener)
    {
    _listeners.removeElement(listener);
    }

  /** Fire a <i>dialog dismissed</i> event. Notifies listeners that this dialog
    * is being dismissed.
    *
    * @param type The event type.
    */

  protected void fireDialogDismissed(int type)
    {
    fireDialogDismissed(type, null);
    }

  /** Fire a <i>dialog dismissed</i> event. Notifies listeners that this dialog
    * is being dismissed.
    *
    * @param type The event type.
    * @param userObj An arbitrary user object argument to pass in the event.
    */

  protected void fireDialogDismissed(int type, Object userObj)
    {
    DialogDismissEvent evt = null;
    DialogDismissListener listener;

    Enumeration e = _listeners.elements();
    while(e.hasMoreElements())
      {
      listener = (DialogDismissListener)e.nextElement();
      if(evt == null) evt = new DialogDismissEvent(this, type, userObj);
      listener.dialogDismissed(evt);
      }
    }

  /** Show or hide the dialog.
   *
   * @param flag A flag specifying whether the dialog should be shown
   * or hidden. If <code>true</code>, the <code>startFocus()</code>
   * method is called to allow the subclasser to request focus for a
   * given child component.
   *
   * @see #startFocus
   */

  public void setVisible(boolean flag)
    {
    if(flag)
      startFocus();

    super.setVisible(flag);
    }

  /** This method is called when the dialog is made visible; it should
   * transfer focus to the appropriate child component. The default
   * implementation does nothing.
   */

  protected void startFocus()
    {
    }

  /** Turn the busy cursor on or off for this dialog.
   *
   * @param flag If <code>true</code>, the wait cursor will be set for
   * this dialog, otherwise the default cursor will be set.
   */

  public void setBusyCursor(boolean flag)
    {
    setCursor(Cursor.getPredefinedCursor(flag ? Cursor.WAIT_CURSOR
                                         : Cursor.DEFAULT_CURSOR));
    }

  /** Determine if this dialog can be closed.
   *
   * @return <code>true</code> if the dialog may be closed, and
   * <code>false</code> otherwise. The default implementation returns
   * <code>true</code>.
   */

  protected boolean canClose()
    {
    return(true);
    }

  /** Destroy this dialog. Call this method when the dialog is no longer
   * needed. The dialog will detach its listeners from the
   * <code>UIChanageManager</code>.
   */

  public void destroy()
    {
    UIChangeManager.getInstance().unregisterComponent(getRootPane());
    UIChangeManager.getInstance().removePropertyChangeListener(propListener);
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
