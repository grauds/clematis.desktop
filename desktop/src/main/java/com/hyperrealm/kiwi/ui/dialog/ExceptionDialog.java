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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KLabelArea;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.KScrollPane;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;
import com.hyperrealm.kiwi.util.ResourceManager;

/**
 * A dialog for presenting an exception to the user. The dialog displays a
 * message as well as the detail of the exception, including its stack trace.
 *
 * <p><center>
 * <img src="snapshot/ExceptionDialog.gif"><br>
 * <i>An example ExceptionDialog.</i>
 * </center>
 *
 * <p><center>
 * <img src="snapshot/ExceptionDialog_2.gif"><br>
 * <i>An example ExceptionDialog with the detail expanded.</i>
 * </center>
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */
@SuppressWarnings("unused")
public class ExceptionDialog extends ComponentDialog {

    private KButton bDetail, bCopy;

    private JList<StackTraceElement> lTrace;

    private KPanel pMain, pDetail;

    private JTextField tException;

    private KLabelArea lMessage;

    private boolean detailShown = false;

    private boolean expandable = true;

    private Throwable throwable;

    /**
     * Construct a new modal <code>ExceptionDialog</code> with the specified
     * parent window.
     *
     * @param parent The parent window for the dialog.
     * @param title  The title for the dialog window.
     */

    public ExceptionDialog(Frame parent, String title) {
        super(parent, title, true, false);
    }

    /**
     * Construct a new modal <code>ExceptionDialog</code> with the specified
     * parent window.
     *
     * @param parent The parent window for the dialog.
     * @param title  The title for the dialog window.
     * @since Kiwi 2.1
     */

    public ExceptionDialog(Dialog parent, String title) {
        super(parent, title, true, false);
    }

    /**
     * Set the "expandable" mode of the dialog. If the mode is enabled, the
     * dialog will include a "detail" button, which, when clicked, will
     * expand the dialog to display the exception type and stack trace.
     * This mode is enabled by default.
     */

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;

        bDetail.setVisible(expandable);
    }

    /**
     * Build the dialog user interface.
     */

    protected Component buildDialogUI() {
        setComment(null);

        ResourceManager resmgr = KiwiUtils.getResourceManager();

        setIcon(resmgr.getIcon("dialog_alert.png"));
        setIconPosition(SwingConstants.TOP);

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        bDetail = new KButton(loc.getMessage("kiwi.button.detail"),
            resmgr.getIcon("arrow_expand.png"));

        bDetail.addActionListener(evt -> {
            Object o = evt.getSource();

            if (o == bDetail) {
                detailShown = !detailShown;

                if (detailShown) {
                    pMain.add(CENTER_POSITION, pDetail);
                } else {
                    pMain.remove(pDetail);
                }

                pack();

                if (detailShown) {
                    bDetail.setVisible(false);
                    addButton(bCopy);
                }
            }
        });
        addButton(bDetail);

        bCopy = new KButton(loc.getMessage("kiwi.button.copy"));
        bCopy.addActionListener(evt -> {
            Object o = evt.getSource();

            if (o == bCopy) {
                KiwiUtils.setClipboardText(
                    KiwiUtils.stackTraceToString(throwable));
            }
        });

        pMain = new KPanel();
        pMain.setLayout(DEFAULT_BORDER_LAYOUT);

        lMessage = new KLabelArea(1, DEFAULT_LABEL_LENGTH);
        pMain.add(NORTH_POSITION, lMessage);

        pDetail = new KPanel();
        pDetail.setLayout(DEFAULT_BORDER_LAYOUT);

        tException = new JTextField();
        tException.setFont(KiwiUtils.boldFont);
        tException.setOpaque(false);
        tException.setEditable(false);

        pDetail.add(NORTH_POSITION, tException);

        lTrace = new JList<>();
        lTrace.setFont(KiwiUtils.plainFont);
        KScrollPane scroll = new KScrollPane(lTrace);
        scroll.setSize(DEFAULT_LIST_SIZE);
        scroll.setPreferredSize(DEFAULT_LIST_SIZE);

        pDetail.add(CENTER_POSITION, scroll);

        return (pMain);
    }

    /**
     * Set a textual error message and the throwable object to be displayed in
     * the dialog.
     *
     * @param message   The message.
     * @param throwable The throwable.
     */

    public void setException(String message, Throwable throwable) {

        StringBuilder sb = new StringBuilder(message);

        String tmsg = throwable.getMessage();
        if (tmsg != null) {
            sb.append("\n").append(tmsg);
        } else {
            sb.append("\n(no message)");
        }

        lMessage.setText(sb.toString());

        lTrace.setListData(throwable.getStackTrace());
        tException.setText(throwable.getClass().getName());
        this.throwable = throwable;
    }

    /**
     * Show or hide the dialog.
     */

    public void setVisible(boolean flag) {
        if (flag) {
            detailShown = false;
            pMain.remove(pDetail);
            bDetail.setVisible(expandable);
            removeButton(bCopy);
            pack();

            bOk.requestFocus();
        }

        super.setVisible(flag);
    }

}
