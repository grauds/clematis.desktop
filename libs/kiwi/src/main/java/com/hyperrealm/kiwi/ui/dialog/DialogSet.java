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

import java.awt.Dialog;
import java.awt.Image;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;

import com.hyperrealm.kiwi.ui.UIChangeManager;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * This class is the Kiwi analogy to the Swing <i>JOptionPane</i>. It provides
 * a simple interface for displaying some of the more commonly-used Kiwi
 * dialogs without requiring any <code>Dialog</code> objects to be
 * instantiated.
 * <p>
 * A placement policy may be assigned to a <code>DialogSet</code>; this
 * policy defines where dialogs will appear relative to their owner
 * <code>Window</code>. If there is no owner, dialogs will appear in the
 * center of the screen.
 * <p>
 * Although each instance of <code>DialogSet</code> may have its own owner
 * <code>Window</code> and placement policy, they will all share singleton
 * instances of <code>KMessageDialog</code>, <code>KQuestionDialog</code>, and
 * <code>KInputDialog</code>.
 * <p>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.dialog.KMessageDialog
 * @see com.hyperrealm.kiwi.ui.dialog.KQuestionDialog
 * @see com.hyperrealm.kiwi.ui.dialog.KInputDialog
 */
@SuppressWarnings("unused")
public class DialogSet implements PropertyChangeListener {
    /**
     * Placement policy. Display dialogs centered within the bounds of their
     * owner <code>Window</code>.
     */
    public static final int CENTER_PLACEMENT = 0;
    /**
     * Placement policy. Display dialogs cascaded off the top-left corner of
     * their owner <code>Window</code>.
     */
    public static final int CASCADE_PLACEMENT = 1;

    private static KInputDialog dInput;

    private static KMessageDialog dMessage;

    private static KQuestionDialog dQuestion;

    private static DialogSet defaultSet;

    static {
        dInput = new KInputDialog(KiwiUtils.getPhantomFrame());
        dQuestion = new KQuestionDialog(KiwiUtils.getPhantomFrame());
        dMessage = new KMessageDialog(KiwiUtils.getPhantomFrame());
        defaultSet = new DialogSet();
    }

    private String sInput, sMessage, sQuestion;

    private Window owner;

    private int placement;

    private DialogSet() {
        this(null, CENTER_PLACEMENT);
        UIChangeManager.getInstance().addPropertyChangeListener(this);
    }

    /**
     * Construct a new <code>DialogSet</code> with the given owner and
     * placement policy.
     *
     * @param owner     The <code>Window</code> that is the owner of this
     *                  <code>DialogSet</code>.
     * @param placement The placement for dialogs in this
     *                  <code>DialogSet</code>; one of the numeric constants defined above.
     */

    public DialogSet(Window owner, int placement) throws IllegalArgumentException {
        if ((placement < 0) || (placement > 1)) {
            throw (new IllegalArgumentException());
        }

        this.owner = owner;
        this.placement = placement;

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        sInput = loc.getMessage("kiwi.dialog.title.input");
        sMessage = loc.getMessage("kiwi.dialog.title.message");
        sQuestion = loc.getMessage("kiwi.dialog.title.question");
    }

    /* Construct a new <code>DialogSet</code> with a phantom frame owner and
     * centered-on-screen placement policy.
     */

    /**
     * Get a reference to the default instance of <code>DialogSet</code>. This
     * instance has no owner <code>Window</code>, and hence its dialogs appear
     * centered on the screen.
     *
     * @return The default (singleton) instance.
     */

    public static DialogSet getInstance() {
        return (defaultSet);
    }

    /**
     * Set the owner window for this <code>DialogSet</code>.
     *
     * @param owner The new owner window.
     * @since Kiwi 2.0
     */

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    /**
     * Show a <code>KInputDialog</code>. Displays an input dialog and returns
     * when the dialog is dismissed.
     *
     * @param prompt The prompt to display in the dialog.
     * @return The text that was entered in the dialog, or <code>null</code>
     * if the dialog was cancelled.
     */

    public String showInputDialog(String prompt) {
        return (showInputDialog(sInput, null, prompt, null));
    }

    /**
     * Show a <code>KInputDialog</code>. Displays an input dialog and returns
     * when the dialog is dismissed.
     *
     * @param parent The parent window.
     * @param prompt The prompt to display in the dialog.
     * @return The text that was entered in the dialog, or <code>null</code>
     * if the dialog was cancelled.
     */

    public String showInputDialog(Window parent, String prompt) {
        return (doShowInputDialog(parent, sInput, null, prompt, null));
    }

    /**
     * Show a <code>KInputDialog</code>. Displays an input dialog and returns
     * when the dialog is dismissed.
     *
     * @param prompt       The prompt to display in the dialog.
     * @param defaultValue The default value to display in the input field.
     * @return The text that was entered in the dialog, or <code>null</code>
     * if the dialog was cancelled.
     */

    public String showInputDialog(String prompt, String defaultValue) {
        return (showInputDialog(sInput, null, prompt, defaultValue));
    }

    /**
     * Show a <code>KInputDialog</code>. Displays an input dialog and returns
     * when the dialog is dismissed.
     *
     * @param title  The title for the dialog window.
     * @param icon   The ICON to display in the dialog.
     * @param prompt The prompt to display in the dialog.
     * @return The text that was entered in the dialog, or <code>null</code>
     * if the dialog was cancelled.
     */

    public synchronized String showInputDialog(String title, Icon icon,
                                               String prompt) {
        return (showInputDialog(title, icon, prompt, null));
    }

    /**
     * Show a <code>KInputDialog</code>. Displays an input dialog and returns
     * when the dialog is dismissed.
     *
     * @param title        The title for the dialog window.
     * @param icon         The ICON to display in the dialog.
     * @param prompt       The prompt to display in the dialog.
     * @param defaultValue The default value to display in the input field.
     * @return The text that was entered in the dialog, or <code>null</code>
     * if the dialog was cancelled.
     */

    public String showInputDialog(String title, Icon icon,
                                               String prompt,
                                               String defaultValue) {
        return doShowInputDialog(owner, title, icon, prompt, defaultValue);
    }

    /**
     * Show a <code>KInputDialog</code>. Displays an input dialog and returns
     * when the dialog is dismissed.
     *
     * @param parent       The parent window.
     * @param title        The title for the dialog window.
     * @param icon         The ICON to display in the dialog.
     * @param prompt       The prompt to display in the dialog.
     * @param defaultValue The default value to display in the input field.
     * @return The text that was entered in the dialog, or <code>null</code>
     * if the dialog was cancelled.
     * @since Kiwi 2.0
     */

    public String showInputDialog(Window parent, String title,
                                               Icon icon, String prompt,
                                               String defaultValue) {
        return doShowInputDialog(parent, title, icon, prompt, defaultValue);
    }

    /* show an input dialog */

    private synchronized String doShowInputDialog(Window owner, String title, Icon icon,
                                     String prompt, String defaultValue) {
        dInput.setTitle((title == null) ? sInput : title);
        dInput.setPrompt(prompt);
        if (icon != null) {
            dInput.setIcon(icon);
        }
        KiwiUtils.centerWindow(dInput);
        dInput.setText((defaultValue == null) ? "" : defaultValue);
        doPositionDialog(owner, dInput);
        dInput.setVisible(true);
        if (dInput.isCancelled()) {
            return null;
        }
        return dInput.getText();
    }

    /**
     * Show a <code>KQuestionDialog</code>. Displays a question dialog and
     * returns when the dialog is dismissed.
     *
     * @param prompt The prompt to display in the dialog.
     * @param type   The question dialog type to display; one of the symbolic
     *               constants defined in <code>KQuestionDialog</code>.
     * @return The status of the dialog; <code>true</code> if the dialog was
     * accepted or <code>false</code> if it was cancelled.
     */

    public boolean showQuestionDialog(String prompt, int type) {
        return showQuestionDialog(sQuestion, null, prompt, type);
    }

    /**
     * Show a <code>KQuestionDialog</code>. Displays a question dialog and
     * returns when the dialog is dismissed.
     *
     * @param prompt The prompt to display in the dialog.
     * @return The status of the dialog; <code>true</code> if the dialog was
     * accepted or <code>false</code> if it was cancelled.
     */

    public boolean showQuestionDialog(String prompt) {
        return (showQuestionDialog(sQuestion, null, prompt, KQuestionDialog.OK_CANCEL_DIALOG));
    }

    /**
     * Show a <code>KQuestionDialog</code>. Displays a question dialog and
     * returns when the dialog is dismissed.
     *
     * @param parent The parent window.
     * @param prompt The prompt to display in the dialog.
     * @return The status of the dialog; <code>true</code> if the dialog was
     * accepted or <code>false</code> if it was cancelled.
     * @since Kiwi 2.0
     */

    public boolean showQuestionDialog(Window parent, String prompt) {
        return (showQuestionDialog(parent, sQuestion, null, prompt, KQuestionDialog.OK_CANCEL_DIALOG));
    }

    /**
     * Show a <code>KQuestionDialog</code>. Displays a question dialog and
     * returns when the dialog is dismissed.
     *
     * @param parent The parent window.
     * @param prompt The prompt to display in the dialog.
     * @param type   The question dialog type to display; one of the symbolic
     *               constants defined in <code>KQuestionDialog</code>.
     * @return The status of the dialog; <code>true</code> if the dialog was
     * accepted or <code>false</code> if it was cancelled.
     * @since Kiwi 2.0
     */

    public boolean showQuestionDialog(Window parent, String prompt, int type) {
        return showQuestionDialog(parent, sQuestion, null, prompt, type);
    }

    /**
     * Show a <code>KQuestionDialog</code>. Displays a question dialog and
     * returns when the dialog is dismissed.
     *
     * @param title  The title for the dialog window.
     * @param icon   The ICON to display in the dialog.
     * @param prompt The promopt to display in the dialog.
     * @return The status of the dialog; <code>true</code> if the dialog was
     * accepted or <code>false</code> if it was cancelled.
     */

    public synchronized boolean showQuestionDialog(String title, Icon icon,
                                                   String prompt) {
        return (showQuestionDialog(title, icon, prompt, KQuestionDialog.OK_CANCEL_DIALOG));
    }

    /**
     * Show a <code>KQuestionDialog</code>. Displays a question dialog and
     * returns when the dialog is dismissed.
     *
     * @param title  The title for the dialog window.
     * @param icon   The ICON to display in the dialog.
     * @param prompt The promopt to display in the dialog.
     * @param type   The question dialog type to display; one of the symbolic
     *               constants defined in <code>KQuestionDialog</code>.
     * @return The status of the dialog; <code>true</code> if the dialog was
     * accepted or <code>false</code> if it was cancelled.
     */

    public synchronized boolean showQuestionDialog(String title, Icon icon,
                                                   String prompt, int type) {
        return doShowQuestionDialog(owner, title, icon, prompt, type);
    }

    /**
     * Show a <code>KQuestionDialog</code>. Displays a question dialog and
     * returns when the dialog is dismissed.
     *
     * @param parent The parent window.
     * @param title  The title for the dialog window.
     * @param icon   The ICON to display in the dialog.
     * @param prompt The promopt to display in the dialog.
     * @param type   The question dialog type to display; one of the symbolic
     *               constants defined in <code>KQuestionDialog</code>.
     * @return The status of the dialog; <code>true</code> if the dialog was
     * accepted or <code>false</code> if it was cancelled.
     * @since Kiwi 2.0
     */

    public synchronized boolean showQuestionDialog(Window parent, String title,
                                                   Icon icon, String prompt,
                                                   int type) {
        return doShowQuestionDialog(parent, title, icon, prompt, type);
    }

    /* show a question dialog */

    private boolean doShowQuestionDialog(Window parent, String title, Icon icon,
                                         String prompt, int type) {
        dQuestion.setTitle((title == null) ? sInput : title);
        dQuestion.setType(type);
        dQuestion.setMessage(prompt);
        if (icon != null) {
            dQuestion.setIcon(icon);
        }
        KiwiUtils.centerWindow(dQuestion);
        doPositionDialog(parent, dQuestion);
        dQuestion.setVisible(true);
        if (dQuestion.isCancelled()) {
            return (false);
        }
        return (dQuestion.getStatus());
    }

    /**
     * Show a <code>KMessageDialog</code>. Displays a message dialog and returns
     * when the dialog is dismissed.
     *
     * @param parent  The parent window.
     * @param message The prompt to display in the dialog.
     * @since Kiwi 2.0
     */

    public void showMessageDialog(Window parent, String message) {
        showMessageDialog(parent, sMessage, null, message);
    }

    /**
     * Show a <code>KMessageDialog</code>. Displays a message dialog and returns
     * when the dialog is dismissed.
     *
     * @param message The prompt to display in the dialog.
     */

    public void showMessageDialog(String message) {
        showMessageDialog(sMessage, null, message);
    }

    /**
     * Show a <code>KMessageDialog</code>. Displays a message dialog and returns
     * when the dialog is dismissed.
     *
     * @param title   The title for the dialog window.
     * @param icon    The ICON to display in the dialog.
     * @param message The prompt to display in the dialog.
     */

    public synchronized void showMessageDialog(String title, Icon icon,
                                               String message) {
        showMessageDialog(owner, title, icon, message);
    }

    /**
     * Show a <code>KMessageDialog</code>. Displays a message dialog and returns
     * when the dialog is dismissed.
     *
     * @param parent  The parent window.
     * @param title   The title for the dialog window.
     * @param icon    The ICON to display in the dialog.
     * @param message The prompt to display in the dialog.
     * @since Kiwi 2.0
     */

    public synchronized void showMessageDialog(Window parent, String title,
                                               Icon icon, String message) {
        doShowMessageDialog(parent, title, icon, message);
    }

    /* show a message dialog */

    private void doShowMessageDialog(Window owner, String title, Icon icon,
                                     String message) {
        dMessage.setTitle((title == null) ? sMessage : title);
        dMessage.setMessage(message);
        KiwiUtils.centerWindow(dMessage);
        if (icon != null) {
            dMessage.setIcon(icon);
        }
        doPositionDialog(owner, dMessage);
        dMessage.setVisible(true);
    }

    /* position a dialog */

    private void doPositionDialog(Window owner, Dialog dialog) {
        if (owner == null) {
            KiwiUtils.centerWindow(dialog);
        } else {
            switch (placement) {
                case CENTER_PLACEMENT:
                    KiwiUtils.centerWindow(owner, dialog);
                    break;
                case CASCADE_PLACEMENT:
                    KiwiUtils.cascadeWindow(owner, dialog);
                    break;
                default:
            }
        }
    }

    /**
     * Respond to property change events.
     */

    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if (prop.equals(UIChangeManager.TEXTURE_PROPERTY)) {
            Image img = (Image) evt.getNewValue();
            dInput.setTexture(img);
            dMessage.setTexture(img);
            dQuestion.setTexture(img);
        } else if (prop.equals(UIChangeManager.BUTTON_OPACITY_PROPERTY)) {
            boolean flag = (Boolean) evt.getNewValue();
            dInput.setButtonOpacity(flag);
            dMessage.setButtonOpacity(flag);
            dQuestion.setButtonOpacity(flag);
        }
    }

}
