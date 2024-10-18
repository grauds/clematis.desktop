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

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import com.hyperrealm.kiwi.ui.dialog.KDialog;

/**
 * A direct replacement for <code>JFileChooser</code> that supports background
 * texturing. All methods behave equivalently to the superclass, with the
 * exception of <code>getSelectedFile()</code>, which now returns
 * <code>null</code> if the <i>Cancel</i> button was pressed.
 *
 * <p><center>
 * <img src="snapshot/KFileChooser.gif"><br>
 * <i>An example KFileChooser.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog
 * @since Kiwi 1.4
 */

public class KFileChooser extends JFileChooser {

    protected KDialog dialog = null;

    private int returnValue = ERROR_OPTION;

    /**
     *
     */

    public KFileChooser() {
        super();

        init();
    }

    /**
     *
     */

    public KFileChooser(File currentDirectory) {
        super(currentDirectory);

        init();
    }

    /**
     *
     */

    public KFileChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);

        init();
    }

    /**
     *
     */

    public KFileChooser(FileSystemView fsv) {
        super(fsv);

        init();
    }

    /**
     *
     */

    public KFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);

        init();
    }

    /**
     *
     */

    public KFileChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);

        init();
    }

    /*
     */

    private void init() {
        removeOpacity(this);
    }

    /*
     */

    private void removeOpacity(Component c) {
        if (!(c instanceof JComponent)) {
            return;
        }

        if ((c instanceof JPanel) || (c instanceof AbstractButton)
            || (c instanceof JComboBox)) {
            ((JComponent) c).setOpaque(false);
        }

        Container cont = (Container) c;

        for (int i = 0; i < cont.getComponentCount(); i++) {
            removeOpacity(cont.getComponent(i));
        }
    }

    /**
     *
     */

    public int showDialog(Component parent, String approveButtonText) {
        if (approveButtonText != null) {
            setApproveButtonText(approveButtonText);
            setDialogType(CUSTOM_DIALOG);
        }

        Frame frame = ((parent instanceof Frame) ? (Frame) parent
            : (Frame) SwingUtilities.getAncestorOfClass(Frame.class,
            parent));

        String title = null;

        if (getDialogTitle() != null) {
            title = getDialogTitle(); //dialogTitle;
        } else {
            title = getUI().getDialogTitle(this);
        }

        if (dialog == null) {
            dialog = new Dialog(frame, title, true, this);
        }

        dialog.setLocationRelativeTo(parent);
        rescanCurrentDirectory();
        dialog.setVisible(true);
        return (returnValue);
    }

    /*
     */

    /**
     *
     */

    public void approveSelection() {
        // todo: ugly hack because "returnValue" and "dialog" are private in the superclass

        super.approveSelection();
        if (dialog != null) {
            dialog.setVisible(false);
        }

        returnValue = APPROVE_OPTION;
    }

    /**
     *
     */

    public void cancelSelection() {
        // todo: ugly hack because "returnValue" and "dialog" are private in the superclass

        super.cancelSelection();
        if (dialog != null) {
            dialog.setVisible(false);
        }

        setSelectedFile(null); // how did they overlook this???
        setSelectedFiles(null);
        returnValue = CANCEL_OPTION;
    }

    private static class Dialog extends KDialog {

        Dialog(Frame parent, String title, boolean modal,
               KFileChooser chooser) {
            super(parent, title, modal);

            KPanel p = getMainContainer();
            p.setLayout(new GridLayout(1, 0));
            p.add(chooser);

            pack();
        }
    }

}
