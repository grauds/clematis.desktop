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
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.Icon;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_PADDING;

import com.hyperrealm.kiwi.ui.KLabel;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;
import com.hyperrealm.kiwi.util.ProgressObserver;
import com.hyperrealm.kiwi.util.Task;

/**
 * A simple dialog with a message and progress meter, for use when a
 * non-interruptible, lengthy task is being performed. The dialog tracks the
 * progress of a <code>Task</code>, which periodically notifies the dialog
 * of the percentage of the task completed.
 *
 * <p><center>
 * <img src="snapshot/ProgressDialog.gif"><br>
 * <i>An example ProgressDialog.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.Task
 */
@SuppressWarnings("unused")
public class ProgressDialog extends KDialog implements ProgressObserver {

    private JProgressBar bar;

    private KLabel label, iconLabel;

    /**
     * Construct a new <code>ProgressDialog</code> with a default title.
     *
     * @param parent The parent window for this dialog.
     * @param modal  A flag specifying whether the dialog should be modal.
     */

    public ProgressDialog(Frame parent, boolean modal) {
        this(parent, "", modal);
    }

    /**
     * Construct a new <code>ProgressDialog</code> with a default title.
     *
     * @param parent The parent window for this dialog.
     * @param modal  A flag specifying whether the dialog should be modal.
     * @since Kiwi 1.4
     */

    public ProgressDialog(Dialog parent, boolean modal) {
        this(parent, "", modal);
    }

    /**
     * Construct a new <code>ProgressDialog</code>.
     *
     * @param parent The parent window for this dialog.
     * @param title  The title for the dialog window.
     * @param modal  A flag specifying whether the dialog should be modal.
     */

    public ProgressDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        init();
    }

    /**
     * Construct a new <code>ProgressDialog</code>.
     *
     * @param parent The parent window for this dialog.
     * @param title  The title for the dialog window.
     * @param modal  A flag specifying whether the dialog should be modal.
     * @since Kiwi 1.4
     */

    public ProgressDialog(Dialog parent, String title, boolean modal) {
        super(parent, title, modal);

        init();
    }

    /*
     */

    private void init() {

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        KPanel main = getMainContainer();

        main.setLayout(DEFAULT_BORDER_LAYOUT);
        main.setBorder(KiwiUtils.DEFAULT_BORDER);

        label = new KLabel(loc.getMessage("kiwi.dialog.prompt.wait"));

        main.add("North", label);

        KPanel p = new KPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, DEFAULT_PADDING, DEFAULT_PADDING));

        iconLabel = new KLabel(KiwiUtils.getResourceManager().getIcon("ani-gear.gif"));
        p.add(iconLabel);

        bar = new JProgressBar();
        bar.setMinimum(0);
        bar.setMaximum(ComponentDialog.MAXIMUM);
        bar.setValue(0);
        bar.setOpaque(false);
        p.add(bar);

        main.add(CENTER_POSITION, p);

        if (getTitle().length() == 0) {
            setTitle(loc.getMessage("kiwi.dialog.title.progress"));
        }

        pack();
    }

    /**
     * Implementation of the <code>ProgressObserver</code> interface.
     */

    public void setProgress(int progress) {

        int progressInt = progress;

        if (progressInt < 0) {
            progressInt = 0;
        } else if (progressInt > ComponentDialog.MAXIMUM) {
            progressInt = ComponentDialog.MAXIMUM;
        }

        bar.setValue(progressInt);
        if (progressInt == ComponentDialog.MAXIMUM) {
            KiwiUtils.paintImmediately(bar);
            KiwiUtils.sleep(1);
            setVisible(false);
        }
    }

    /**
     * Set the dialog's ICON. The dialog's ICON can be changed via a call to
     * this method. Animated GIF images add a professional touch when used with
     * <code>ProgressDialog</code>s.
     *
     * @param icon The new ICON to use.
     */

    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
    }

    /**
     * Set the message for the dialog.
     *
     * @param message The message to display in the dialog.
     */

    public void setMessage(String message) {
        label.setText(message);
        pack();
    }

    /**
     * Track the progress of a task. Displays the dialog and tracks the progress
     * of the task, updating the progress meter accordingly. When the task
     * is completed, the dialog automatically disappears.
     *
     * @param task The <code>Task</code> to track; it should not be currently
     *             running, as the dialog will start the task itself.
     */

    public void track(Task task) {
        bar.setValue(0);
        task.addProgressObserver(this);
        Thread t = new Thread(task);
        t.start();
        setVisible(true);
        task.removeProgressObserver(this);
    }

}
