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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.ui.ButtonPanel;
import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KLabel;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A base class for custom dialog windows. This class provides some base
 * functionality that can be useful across many different types of dialogs.
 * The class constructs a skeleton dialog consisting of an optional comment
 * line, an ICON, <i>OK</i> and <i>Cancel</i> buttons, and a middle area that
 * must be filled in by subclassers.
 * <p>
 * A <code>ComponentDialog</code> is <i>accepted</i> by clicking the
 * <i>OK</i> button, though subclassers can determine the conditions under
 * which a dialog may be accepted by overriding the <code>accept()</code>
 * method; it is <i>cancelled</i> by clicking the <i>Cancel</i> button or
 * closing the window.
 *
 * @author Mark Lindner
 */
@SuppressWarnings("unused")
public abstract class ComponentDialog extends KDialog {

    public static final String CENTER_POSITION = "Center";

    public static final String NORTH_POSITION = "North";

    public static final String SOUTH_POSITION = "South";

    public static final String EAST_POSITION = "East";

    public static final String WEST_POSITION = "West";

    public static final BorderLayout DEFAULT_BORDER_LAYOUT = new BorderLayout(5, 5);

    public static final Dimension DEFAULT_FRAME_SIZE = new Dimension(500, 600);

    public static final Dimension DEFAULT_LIST_SIZE = new Dimension(350, 150);

    public static final int DEFAULT_PADDING = 5;

    public static final Insets DEFAULT_INSETS = new Insets(3, 3, 3, 3);

    public static final int DEFAULT_ROW_HEIGHT = 18;

    public static final int MAXIMUM = 100;

    protected static final int DEFAULT_LABEL_LENGTH = 30;

    protected static final int DEFAULT_FIELD_LENGTH = 15;

    protected static final int DEFAULT_ROWS_NUMBER = 3;

    protected static final String POSITION_OUT_OF_RANGE = "Position out of range.";

    /**
     * The OK button.
     */
    KButton bOk;
    /**
     * The Cancel button.
     */
    KButton bCancel = null;

    private ActionListener actionListener;

    private KPanel main;

    private KLabel iconLabel, commentLabel;

    private JTextField inputComponent = null;

    private ButtonPanel buttons;

    private int fixedButtons = 1;

    /**
     * Construct a new <code>ComponentDialog</code>.
     *
     * @param parent The parent dialog for this dialog.
     * @param title  The title for this dialog's window.
     * @param modal  A flag specifying whether this dialog will be modal.
     */

    public ComponentDialog(Dialog parent, String title, boolean modal) {
        this(parent, title, modal, true);
    }

    /**
     * Construct a new <code>ComponentDialog</code>.
     *
     * @param parent    The parent dialog for this dialog.
     * @param title     The title for this dialog's window.
     * @param modal     A flag specifying whether this dialog will be modal.
     * @param hasCancel A flag specifying whether this dialog should have a
     *                  <i>Cancel</i> button.
     */

    public ComponentDialog(Dialog parent, String title, boolean modal,
                           boolean hasCancel) {
        super(parent, title, modal);

        init(hasCancel);
    }

    /**
     * Construct a new <code>ComponentDialog</code>.
     *
     * @param parent The parent frame for this dialog.
     * @param title  The title for this dialog's window.
     * @param modal  A flag specifying whether this dialog will be modal.
     */

    public ComponentDialog(Frame parent, String title, boolean modal) {
        this(parent, title, modal, true);
    }

    /**
     * Construct a new <code>ComponentDialog</code>.
     *
     * @param parent    The parent frame for this dialog.
     * @param title     The title for this dialog's window.
     * @param modal     A flag specifying whether this dialog will be modal.
     * @param hasCancel A flag specifying whether this dialog should have a
     *                  <i>Cancel</i> button.
     */

    public ComponentDialog(Frame parent, String title, boolean modal,
                           boolean hasCancel) {
        super(parent, title, modal);

        init(hasCancel);
    }

    /*
     * Common initialization.
     */

    private void init(boolean hasCancel) {

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        actionListener = new ActionListener();

        main = getMainContainer();
        main.setBorder(new EmptyBorder(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING));
        main.setLayout(new BorderLayout(DEFAULT_PADDING, DEFAULT_PADDING));

        commentLabel = new KLabel(loc.getMessage("kiwi.dialog.prompt"));
        main.add(NORTH_POSITION, commentLabel);

        iconLabel = new KLabel();
        iconLabel.setBorder(new EmptyBorder(DEFAULT_PADDING, 0, DEFAULT_PADDING, 0));
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        buttons = new ButtonPanel();

        bOk = new KButton(loc.getMessage("kiwi.button.ok"));
        bOk.addActionListener(actionListener);
        buttons.addButton(bOk);

        if (hasCancel) {
            bCancel = new KButton(loc.getMessage("kiwi.button.cancel"));
            bCancel.addActionListener(actionListener);
            buttons.addButton(bCancel);
            fixedButtons++;
        }

        main.add(SOUTH_POSITION, buttons);

        installDialogUI();
    }

    /** Get the icon to display in the top part of the dialog window. By
     * default, this method returns <b>null</b>, which signifies that no icon
     * will be displayed. The method can be called by classes that extend this
     * class to provide an appropriate icon for the dialog.
     */

    public void setTopIcon(Icon icon) {
        commentLabel.setIcon(icon);
    }

    /**
     * Install the component returned by <code>buildDialogUI()</code>
     * into the dialog. If it is not possible to build the dialog UI at
     * the time that the constructor calls <code>buildDialogUI()</code>,
     * that method should return <b>null</b>. The subclass constructor
     * can then explicitly call <code>installDialogUI()</code>, at which
     * time <code>buildDialogUI</code> should return a valid component.
     */

    private void installDialogUI() {
        Component c = buildDialogUI();
        if (c != null) {
            main.add(CENTER_POSITION, c);
            pack();
        }
    }

    /**
     * Set the vertical position of the ICON.
     *
     * @param position The vertical position: one of
     *                 <code>SwingConstants.TOP</code>,
     *                 <code>SwingConstants.BOTTOM</code>, or
     *                 <code>SwingConstants.CENTER</code>.
     * @since Kiwi 2.1
     */

    public void setIconPosition(int position) {
        iconLabel.setVerticalAlignment(position);
    }

    /**
     * Construct the component that will be displayed in the center of the
     * dialog window. Subclassers implement this method to customize the look of
     * the dialog.
     *
     * @return A <code>Component</code> to display in the dialog, or
     * <code>null</code> if the component will be constructed later.
     */

    protected abstract Component buildDialogUI();

    /**
     * Show or hide the dialog.
     */

    public void setVisible(boolean flag) {
        if (flag) {
            pack();
            if (getParent() != null) {
                setLocationRelativeTo(getParent());
            }
            validate();
        }
        super.setVisible(flag);
    }

    /**
     * Register a text field with this dialog. In some dialogs, most notably
     * <code>KInputDialog</code>, pressing <i>Return</i> in a text field is
     * equivalent to pressing the dialog's <i>OK</i> button. Subclassers may use
     * this method to register a text field that should function in this way.
     *
     * @param c The <code>JTextField</code> to register.
     */

    protected void registerTextInputComponent(JTextField c) {
        inputComponent = c;
        inputComponent.addActionListener(actionListener);
    }

    /**
     * Change the dialog's comment.
     *
     * @param comment The new text to display in the comment portion of the
     *                dialog.
     */

    public void setComment(String comment) {
        setComment(null, comment);
    }

    /**
     * Change the dialog's comment.
     *
     * @param comment The new text to display in the comment portion of the
     *                dialog.
     * @param icon    An ICON to display to the left of the comment text.
     * @since Kiwi 2.0
     */

    public void setComment(Icon icon, String comment) {
        commentLabel.setText(comment);
        commentLabel.setIcon(icon);
        commentLabel.invalidate();
        commentLabel.validate();
    }

    /**
     * Set the ICON to display in the left part of the dialog
     * window. The method can be called by classes that extend this
     * class to provide an appropriate ICON for the dialog.
     *
     * @param icon The ICON.
     */

    public void setIcon(Icon icon) {
        if (icon != null) {
            iconLabel.setIcon(icon);
            main.add(WEST_POSITION, iconLabel);
        } else {
            main.remove(iconLabel);
        }
    }

    /* action listener */

    /**
     * Determine if the dialog can be cancelled. This method is called in
     * response to a click on the <i>Cancel</i> button or on the dialog
     * window's close ICON/option. Subclassers may wish to override this
     * method to prevent cancellation of a window in certain circumstances.
     *
     * @return <code>true</code> if the dialog may be cancelled,
     * <code>false</code> otherwise. The default implementation returns
     * <code>true</code> if the dialog has a <i>Cancel</i> button and
     * <code>false</code> otherwise.
     */

    protected boolean canCancel() {
        return (bCancel != null);
    }

    void setButtonOpacity(boolean flag) {
        Component[] c = buttons.getComponents();

        for (Component component : c) {
            if (component instanceof JButton) {
                ((JButton) component).setOpaque(flag);
            }
        }
    }

    /* Set the opacity on all the buttons */

    /**
     * Add a button to the dialog's button panel. The button is added
     * immediately before the <i>OK</i> button.
     *
     * @param button The button to add.
     */

    protected void addButton(JButton button) {
        addButton(button, -1);
    }

    /**
     * Add a button to the dialog's button panel at the specified position.
     *
     * @param button The button to add.
     * @param pos    The position at which to add the button. A value of 0 denotes
     *               the first position, and -1 denotes the last position. The possible
     *               range of values for <code>pos</code> excludes the <i>OK</i> and
     *               (if present) <i>Cancel</i> buttons; buttons may not be added after
     *               these "fixed" buttons.
     */

    protected void addButton(JButton button, int pos)
        throws IllegalArgumentException {

        int posInt = getPosInt(pos);
        buttons.addButton(button, posInt);
    }

    /**
     * Remove a button from the dialog's button panel.
     *
     * @param button The button to remove. Neither the <i>OK</i> nor the
     *               <i>Cancel</i> button may be removed.
     */

    protected void removeButton(JButton button) {
        if ((button != bOk) && (button != bCancel)) {
            buttons.removeButton(button);
        }
    }

    /**
     * Remove a button from the specified position in the  dialog's button
     * panel.
     *
     * @param pos The position of the button to remove, where 0 denotes
     *            the first position, and -1 denotes the last position. The possible
     *            range of values for <code>pos</code> excludes the <i>OK</i> and
     *            (if present) <i>Cancel</i> buttons; these "fixed" buttons may not be
     *            removed.
     */

    protected void removeButton(int pos) {
        int posInt = getPosInt(pos);
        buttons.removeButton(posInt);
    }

    private int getPosInt(int pos) {

        int posInt = pos;
        int bc = buttons.getButtonCount();
        int maxpos = bc - fixedButtons;

        if (posInt > maxpos) {
            throw (new IllegalArgumentException(POSITION_OUT_OF_RANGE));
        } else if (posInt < 0) {
            posInt = maxpos;
        }
        return posInt;
    }

    /**
     * Set the label text for the <i>OK</i> button.
     *
     * @param text The text for the accept button (for example, "Yes").
     */

    public void setAcceptButtonText(String text) {
        bOk.setText(text);
    }

    /**
     * Set the label text for the <i>Cancel</i> button.
     *
     * @param text The text for the cancel button (for example, "No").
     */

    public void setCancelButtonText(String text) {
        if (bCancel != null) {
            bCancel.setText(text);
        }
    }

    private class ActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            if ((o == bOk) || (o == inputComponent)) {
                doAccept();
            } else if (o == bCancel) {
                if (canCancel()) {
                    doCancel();
                }
            }
        }
    }

}
