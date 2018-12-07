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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.NORTH_POSITION;

import com.hyperrealm.kiwi.ui.model.KDocument;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;
import com.hyperrealm.kiwi.util.ResourceManager;

/**
 * A simple text editor for entering unformatted text. The editor consists of
 * a scrollable <code>JTextArea</code> and <i>Cut</i>, <i>Copy</i>, and
 * <i>Paste</i> buttons.
 *
 * <p><center>
 * <img src="snapshot/SimpleEditor.gif"><br>
 * <i>An example SimpleEditor.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.SimpleStyledEditor
 */

public class SimpleEditor extends KPanel {

    private static final int DEFAULT_ROWS_NUMBER = 10;

    private static final int DEFAULT_COLUMNS_NUMBER = 60;
    /**
     * The <code>JTextArea</code> that holds the text for this component.
     */
    private JTextArea tText;

    private KButton bCut, bCopy, bPaste;

    private JToolBar toolBar;

    private KDocument doc;

    /**
     * Construct a new <code>SimpleEditor</code>. The editor is created with
     * a default <code>JTextArea</code> size of 10 rows by 60 columns.
     */

    public SimpleEditor() {
        this(DEFAULT_ROWS_NUMBER, DEFAULT_COLUMNS_NUMBER);
    }

    /**
     * Construct a new <code>SimpleEditor</code> with the specified number of
     * rows and columns.
     *
     * @param rows    The number of rows for the <code>JTextArea</code>.
     * @param columns The number of columns for the <code>JTextArea</code>.
     */

    public SimpleEditor(int rows, int columns) {

        setLayout(DEFAULT_BORDER_LAYOUT);

        ResourceManager rm = KiwiUtils.getResourceManager();

        KPanel pTop = new KPanel();
        pTop.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(NORTH_POSITION, pTop);

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Object o = evt.getSource();

                if (o == bCut) {
                    cut();
                } else if (o == bCopy) {
                    copy();
                } else if (o == bPaste) {
                    paste();
                }

                tText.requestFocus();
            }
        };

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);

        pTop.add(toolBar);

        bCut = new KButton(rm.getIcon("cut.png"));
        bCut.addActionListener(al);
        bCut.setToolTipText(loc.getMessage("kiwi.tooltip.cut"));
        toolBar.add(bCut);

        bCopy = new KButton(rm.getIcon("copy.png"));
        bCopy.addActionListener(al);
        bCopy.setToolTipText(loc.getMessage("kiwi.tooltip.copy"));
        toolBar.add(bCopy);

        bPaste = new KButton(rm.getIcon("paste.png"));
        bPaste.addActionListener(al);
        bPaste.setToolTipText(loc.getMessage("kiwi.tooltip.paste"));
        toolBar.add(bPaste);

        doc = new KDocument();

        tText = new JTextArea(doc, null, rows, columns);
        add(CENTER_POSITION, new KScrollPane(tText));
        tText.setLineWrap(true);
        tText.setWrapStyleWord(true);
    }

    /**
     * Request focus for the editor.
     */

    public void requestFocus() {
        if (tText != null) {
            tText.requestFocus();
        } else {
            super.requestFocus();
        }
    }

    /**
     * Get the <code>JTextArea</code> used by this <code>SimpleEditor</code>.
     *
     * @return The <code>JTextArea</code> for this editor.
     */

    public JTextArea getJTextArea() {
        return (tText);
    }

    /**
     * Insert text into the editor, replacing the current selection (if any).
     *
     * @param text The text to insert.
     */

    public synchronized void insertText(String text) {
        tText.replaceSelection(text);
    }

    /**
     * Set the editable state of the editor.
     *
     * @param flag If <code>true</code>, the editor will be editable, otherwise
     *             it will be non-editable.
     */

    public void setEditable(boolean flag) {
        tText.setEditable(flag);

        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            Component c = toolBar.getComponent(i);

            if (c instanceof AbstractButton) {
                c.setEnabled(flag);
            }
        }
    }

    /**
     * Get the text in the editor.
     *
     * @return The text currently in the editor.
     */

    public synchronized String getText() {
        return (tText.getText());
    }

    /**
     * Set the text in the editor.
     *
     * @param text The text to display in the editor.
     */

    public synchronized void setText(String text) {
        tText.setText(text);
        doc.clearModified();
    }

    /**
     * Determine if the text was modified interactively since the last call
     * to <code>setText()</code>.
     *
     * @since Kiwi 2.0.1
     */

    public boolean isModified() {
        return (doc.isModified());
    }

    /**
     * Perform a <i>cut</i> operation on the editor. Removes the selected text
     * from the editor, and stores it in the system clipboard.
     */

    public void cut() {
        tText.cut();
    }

    /**
     * Perform a <i>copy</i> operation on the editor. Copies the selected text
     * from the editor to the system clipboard.
     */

    public void copy() {
        tText.copy();
    }

    /**
     * Perform a <i>paste</i> operation on the editor. Inserts text from the
     * system clipboard into the editor.
     */

    public void paste() {
        tText.paste();
    }

    /**
     * Add a button to the editor's tool bar. The button is added to the right
     * of the last button in the toolbar. This method does <i>not</i> register
     * this editor as an <code>ActionListener</code> for the button.
     *
     * @param button The button to add.
     */

    public void addButton(KButton button) {
        toolBar.add(button);
    }

    /**
     * Add a separator to the editor's tool bar. The separator is added to the
     * right of the last button in the toolbar.
     *
     * @since Kiwi 2.0
     */

    public void addSeparator() {
        toolBar.addSeparator();
    }

    /**
     * Set the caret position on the text area.
     *
     * @param pos The new caret (insert) position.
     * @since Kiwi 2.0
     */

    public void setCaretPosition(int pos) {
        tText.setCaretPosition(pos);
    }

    /**
     * Set the font on the text area.
     *
     * @param font The new font.
     * @since Kiwi 2.0
     */

    public void setTextFont(Font font) {
        tText.setFont(font);
    }

}
