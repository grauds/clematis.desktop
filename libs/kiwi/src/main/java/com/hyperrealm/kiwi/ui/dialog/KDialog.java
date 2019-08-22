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

package com.hyperrealm.kiwi.ui.dialog;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import com.hyperrealm.kiwi.event.DialogDismissEvent;
import com.hyperrealm.kiwi.event.DialogDismissListener;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.UIChangeManager;
import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * <code>KDialog</code> is a trivial extension of <code>JDialog</code>
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
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.KPanel
 * @see com.hyperrealm.kiwi.ui.KFrame
 * @see com.hyperrealm.kiwi.ui.dialog.ComponentDialog
 * @see com.hyperrealm.kiwi.event.DialogDismissEvent
 */
@SuppressWarnings("unused")
public class KDialog extends JDialog {

    private KPanel main;

    private PropertyChangeListener propListener;

    private final ArrayList<DialogDismissListener> listeners = new ArrayList<DialogDismissListener>();

    private boolean cancelled = false;

    /**
     * Construct a new <code>KDialog</code>.
     *
     * @param parent The parent dialog for this dialog.
     * @param title  The title for this dialog.
     * @param modal  A flag specifying whether this dialog should be modal.
     */

    public KDialog(Dialog parent, String title, boolean modal) {
        super(parent, title, modal);

        init();
    }

    /**
     * Construct a new <code>KDialog</code>.
     *
     * @param parent The parent frame for this dialog.
     * @param title  The title for this dialog.
     * @param modal  A flag specifying whether this dialog should be modal.
     */

    public KDialog(Window parent, String title, boolean modal) {
        super(parent, title, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        init();
    }

    /*
     * Common initialization.
     */

    private void init() {

        getContentPane().setLayout(new GridLayout(1, 0));

        main = new KPanel(UIChangeManager.getInstance().getDefaultTexture());

        main.setOpaque(true);

        getContentPane().add(main);

        UIChangeManager.getInstance().registerComponent(getRootPane());
        propListener = new PropertyChangeListener();
        UIChangeManager.getInstance().addPropertyChangeListener(propListener);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                if (canCancel()) {
                    doCancel();
                } else {
                    doAccept();
                }
            }

            public void windowOpened(WindowEvent evt) {
                startFocus();
            }

            public void windowActivated(WindowEvent evt) {
                startFocus();
            }
        });
    }

    /**
     * Get a reference to the main container (in this case, the
     * <code>KPanel</code> that is the child of the frame's content pane).
     */

    protected KPanel getMainContainer() {
        return (main);
    }

    /**
     * Set the background image for the dialog.
     *
     * @param image The new background image.
     */

    public void setTexture(Image image) {
        main.setTexture(image);
        invalidate();
        validate();
        repaint();
    }

    /**
     * Add a <code>DialogDismissListener</code> to this dialog's list of
     * listeners.
     *
     * @param listener The listener to add.
     * @see #removeDialogDismissListener
     */

    public void addDialogDismissListener(DialogDismissListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a <code>DialogDismissListener</code> from this dialog's list
     * of listeners.
     *
     * @param listener The listener to remove.
     * @see #addDialogDismissListener
     */

    public void removeDialogDismissListener(DialogDismissListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Fire a <i>dialog dismissed</i> event. Notifies listeners that this dialog
     * is being dismissed.
     *
     * @param type The event type.
     */

    public void fireDialogDismissed(int type) {
        fireDialogDismissed(type, null);
    }

    /**
     * Fire a <i>dialog dismissed</i> event. Notifies listeners that this dialog
     * is being dismissed.
     *
     * @param type    The event type.
     * @param userObj An arbitrary user object argument to pass in the event.
     */

    protected void fireDialogDismissed(int type, Object userObj) {
        DialogDismissEvent evt = null;
        DialogDismissListener listener;

        synchronized (listeners) {
            for (DialogDismissListener listener1 : listeners) {
                listener = listener1;
                if (evt == null) {
                    evt = new DialogDismissEvent(this, type, userObj);
                }

                listener.dialogDismissed(evt);
            }
        }
    }

    /**
     * This method is called when the dialog is made visible; it should
     * transfer focus to the appropriate child component. The default
     * implementation does nothing.
     */

    protected void startFocus() {
    }

    /**
     * Turn the busy cursor on or off for this dialog.
     *
     * @param flag If <code>true</code>, the wait cursor will be set for
     *             this dialog, otherwise the default cursor will be set.
     */

    public void setBusyCursor(boolean flag) {
        setCursor(Cursor.getPredefinedCursor(flag ? Cursor.WAIT_CURSOR
            : Cursor.DEFAULT_CURSOR));
    }

    /**
     * Determine if this dialog can be closed.
     *
     * @return <code>true</code> if the dialog may be closed, and
     * <code>false</code> otherwise. The default implementation returns
     * <code>true</code>.
     */

    protected boolean canClose() {
        return (true);
    }

    /**
     * Destroy this dialog. Call this method when the dialog is no longer
     * needed. The dialog will detach its listeners from the
     * <code>UIChanageManager</code>.
     */

    public void destroy() {
        UIChangeManager.getInstance().unregisterComponent(getRootPane());
        UIChangeManager.getInstance().removePropertyChangeListener(propListener);
    }

    /**
     * Accept user input. The dialog calls this method in response to a
     * click on the dialog's <i>OK</i> button. If this method returns
     * <code>true</code>, the dialog disappears; otherwise, it remains
     * on the screen. This method can be overridden to check input in
     * the dialog before allowing it to be dismissed. The default
     * implementation of this method returns <code>true</code>.
     *
     * @return <code>true</code> if the dialog may be dismissed, and
     * <code>false</code> otherwise.
     * @since Kiwi 2.0
     */

    protected boolean accept() {
        return (true);
    }

    /**
     * Programmatically accept user input.
     *
     * @since Kiwi 2.0
     */

    protected void doAccept() {
        if (accept()) {
            setCancelled(false);
            fireDialogDismissed(DialogDismissEvent.OK);
            setVisible(false);
            dispose();
        }
    }

    /**
     * Cancel the dialog. The dialog calls this method in response to a click on
     * the dialog's <i>Cancel</i> button, or on a close of the dialog window
     * itself. Subclassers may override this method to provide any special
     * processing that is required when the dialog is cancelled. The default
     * implementation of this method does nothing.
     *
     * @since Kiwi 2.0
     */

    protected void cancel() {
    }

    /**
     * Programmatically cancel the dialog.
     *
     * @since Kiwi 2.0
     */

    protected void doCancel() {
        setCancelled(true);
        cancel();
        setVisible(false);
        dispose();
        fireDialogDismissed(DialogDismissEvent.CANCEL);
    }

    /**
     * Get the <i>cancelled</i> state of the dialog. This method should be
     * called after the dialog is dismissed to determine if it was cancelled by
     * the user.
     *
     * @return <code>true</code> if the dialog was cancelled, and
     * <code>false</code> otherwise.
     * @since Kiwi 2.0
     */

    public boolean isCancelled() {
        return (cancelled);
    }

    /**
     * Set the <i>cancelled</i> state of the dialog. Custom dialogs which
     * subclass <code>KDialog</code> directory may use this method to record the
     * fact that the dialog was cancelled.
     *
     * @since Kiwi 2.0
     */

    protected void setCancelled(boolean flag) {
        cancelled = flag;
    }

    /**
     * Determine if the dialog can be cancelled. This method is called in
     * response to a click on the <i>Cancel</i> button or on the dialog
     * window's close ICON/option. Subclassers may wish to override this
     * method to prevent cancellation of a window in certain circumstances.
     *
     * @return The default implementation returns<code>true</code>.
     */

    protected boolean canCancel() {
        return (true);
    }

    /* PropertyChangeListener */

    /**
     * Set the font for this dialog window. This method sets the font for
     * each component in the window's component hierarchy.
     *
     * @param font The new font.
     * @since Kiwi 2.2
     */

    public void setFontRecursively(Font font) {
        KiwiUtils.setFonts(getMainContainer(), font);
    }

    private class PropertyChangeListener implements java.beans.PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(UIChangeManager.TEXTURE_PROPERTY)) {
                setTexture((Image) evt.getNewValue());
            }
        }
    }

}
