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

import java.io.File;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.EAST_POSITION;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;
import com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A text entry component for entering a filename. The component consists of
 * a text field and a ``browse'' button which activates a file chooser dialog
 * when clicked.
 *
 * <p><center>
 * <img src="snapshot/FileChooserField.gif"><br>
 * <i>An example FileChooserField.</i>
 * </center>
 * <p>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.dialog.KFileChooserDialog
 * @since Kiwi 2.0
 */

public class FileChooserField extends KPanel implements Editor<File> {

    private KTextField tFile;

    private KButton bBrowse;

    private KFileChooserDialog dFile;

    /**
     * Construct a new <code>FileChooserField</code>.
     *
     * @param width     The width for the field.
     * @param maxLength The maximum length of input allowed in the field.
     * @param dialog    The file chooser dialog to use when browsing for a file.
     */

    public FileChooserField(int width, int maxLength, KFileChooserDialog dialog) {

        dFile = dialog;

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        setLayout(DEFAULT_BORDER_LAYOUT);

        tFile = new KTextField(width);
        tFile.setMaximumLength(maxLength);
        add(CENTER_POSITION, tFile);

        bBrowse = new KButton(KiwiUtils.getResourceManager().getIcon("folder_magnify.png"));
        bBrowse.setMargin(ComponentDialog.DEFAULT_INSETS);
        bBrowse.setToolTipText(loc.getMessage("kiwi.tooltip.select_file"));

        bBrowse.addActionListener(evt -> browse());

        add(EAST_POSITION, bBrowse);
    }

    /**
     *
     */

    public final void requestFocus() {
        tFile.requestFocus();
    }

    /**
     *
     */

    public final boolean validateInput() {
        return (tFile.validateInput());
    }

    /**
     *
     */

    public final void setEnabled(boolean enabled) {
        tFile.setEnabled(enabled);
        bBrowse.setEnabled(enabled);
    }

    /**
     *
     */

    public final JComponent getEditorComponent() {
        return (this);
    }

    /**
     *
     */

    private void browse() {
        KiwiUtils.centerWindow(this, dFile);
        dFile.setVisible(true);

        if (dFile.isCancelled()) {
            return;
        }

        File file = dFile.getSelectedFile();
        tFile.setText(file.getAbsolutePath());
    }

    /**
     * Get the file that is currently displayed in the field.
     *
     * @return The file.
     */

    public File getFile() {
        String s = tFile.getText().trim();
        if (s.equals("")) {
            return (null);
        }

        return (new File(s));
    }

    /**
     * Set the file to be displayed in the field.
     *
     * @param file The new file.
     */

    public void setFile(File file) {
        tFile.setText((file == null) ? null : file.getAbsolutePath());
    }

    /**
     * Get the object being edited. Equivalent to <code>getFile()</code>.
     */

    public File getObject() {
        return (getFile());
    }

    /**
     * Set the object being edited. Equivalent to <code>setFile()</code>.
     */

    public void setObject(File obj) {
        setFile(obj);
    }

    /**
     * Clear the field.
     */

    public void clear() {
        setFile(null);
    }

    /**
     * Add a <code>ChangeListener</code> to this component's list of listeners.
     * <code>ChangeEvent</code>s are fired when the text field's document model
     * changes.
     *
     * @param listener The listener to add.
     * @since Kiwi 2.1.1
     */

    public void addChangeListener(ChangeListener listener) {
        tFile.addChangeListener(listener);
    }

    /**
     * Remove a <code>ChangeListener</code> from this component's list
     * of listeners.
     *
     * @param listener The listener to remove.
     * @since Kiwi 2.1.1
     */

    public void removeChangeListener(ChangeListener listener) {
        tFile.removeChangeListener(listener);
    }

}
