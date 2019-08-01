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

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.BitSet;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

import com.hyperrealm.kiwi.event.ChangeSupport;
import com.hyperrealm.kiwi.text.FormatConstants;
import com.hyperrealm.kiwi.ui.model.KDocument;

/**
 * An abstract class that implements basic functionality for a text field
 * that places constraints on its input.
 * <code>DataField</code> validates its input when it loses or gains focus,
 * generates an action event, or when messaged with the
 * <code>validateInput()</code> method. The no-op method
 * <code>checkInput()</code> must be overridden by subclassers to perform
 * the actual data validation.
 * <p>
 * Entry of specific characters and ranges of characters can be selectively
 * disabled (and re-enabled) using the <code>disableChar()</code>,
 * <code>disableChars()</code>, <code>enableChar()</code>,
 * <code>enableChars()</code>, and <code>enableAllChars()</code> methods. By
 * default all characters are enabled.
 * <p>
 * Invalid input is flagged by repainting the contents of the field in red. If
 * a key is typed into a field so highlighted, the text reverts back to black
 * (non-flagged). Validation is not performed whenever the contents of the
 * field change, as the necessary parsing is an expensive operation.
 *
 * @param <T>
 * @author Mark Lindner
 */

public abstract class DataField<T> extends JTextField implements FormatConstants, Editor<T> {

    private static final int NBITS = 256;
    /**
     * A state flag for representing validation state.
     */
    protected boolean invalid = false;

    private ChangeSupport csupport;

    private DocumentListener documentListener = null;

    private boolean inputRequired = false;

    private boolean adjusting = false;

    private BitSet allowedMap = new BitSet(NBITS);

    /**
     * Construct a new <code>DataField</code>.
     */

    public DataField() {
        super();
        init();
    }

    /**
     * Construct a new <code>DataField</code> with the specified width.
     */

    public DataField(int width) {
        super(width);
        init();
    }

    /* initialization */

    private void init() {

        allowedMap.set(0, allowedMap.size() - 1);

        documentListener = new DocumentListener();
        csupport = new ChangeSupport(this);
        KDocument doc = new KDocument();
        setDocument(doc);
        doc.addDocumentListener(documentListener);

        addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {

                char c = evt.getKeyChar();
                if (!allowedMap.get(c)) {
                    getToolkit().beep();
                    evt.consume();
                    return;
                }

                // we don't want to validate after each keypress; too expensive

                if (invalid) {
                    invalid = false;
                    paintInvalid(invalid);
                }
            }
        });

        addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                validateInput();
            }

            public void focusGained(FocusEvent evt) {
                validateInput();
            }
        });

        addActionListener(evt -> validateInput());
        setMinimumSize(getPreferredSize());
    }

    /**
     * Set the document model for this text field.
     *
     * @param doc The new document model.
     */

    public void setDocument(Document doc) {
        super.setDocument(doc);

        Document oldDoc = getDocument();

        if ((documentListener != null) && (oldDoc != null)) {
            oldDoc.removeDocumentListener(documentListener);
            doc.addDocumentListener(documentListener);
        }
    }

    /**
     * Add a <code>ChangeListener</code> to this component's list of listeners.
     * <code>ChangeEvent</code>s are fired when this text field's document model
     * changes.
     *
     * @param listener The listener to add.
     */

    public void addChangeListener(ChangeListener listener) {
        csupport.addChangeListener(listener);
    }

    /**
     * Remove a <code>ChangeListener</code> from this component's list
     * of listeners.
     *
     * @param listener The listener to remove.
     */

    public void removeChangeListener(ChangeListener listener) {
        csupport.removeChangeListener(listener);
    }

    /* document listener */

    private void fireChange() {
        if (adjusting) {
            return;
        }

        SwingUtilities.invokeLater(() -> csupport.fireChangeEvent());
    }

    /* Delay-fire a change event, but only if the current DocumentEvent is not
     * the result of a call to setText().
     */

    /**
     * Get the object being edited.
     *
     * @since Kiwi 2.0
     */

    public abstract T getObject();

    /**
     * Set the object to edit.
     *
     * @since Kiwi 2.0
     */

    public abstract void setObject(T obj);

    /**
     * Set the text to be displayed by this field. A <code>ChangeEvent</code>
     * will <i>not</i> be fired when the data in the field is modified via this
     * call.
     *
     * @param text The text to set.
     */

    public final synchronized void setText(String text) {
        adjusting = true;
        super.setText(text);
        adjusting = false;
        fireChange();
    }

    /**
     * Paint the necessary decorations for the field to denote invalid (or
     * valid) input. The default implementation sets the text color to red
     * if the input is invalid and black otherwise. This method may be
     * overridden by subclassers who wish to customize the method of visual
     * feedback.
     *
     * @param invalid A flag specifying whether the input in the field is
     *                currently valid or invalid.
     */

    protected void paintInvalid(boolean invalid) {
        setForeground(invalid ? Color.red : Color.black);
    }

    /**
     * Set the editable state of this field.
     *
     * @param flag A flag specifying whether this field should be editable.
     *             Non-editable fields are made transparent.
     */

    public void setEditable(boolean flag) {
        super.setEditable(flag);
        setOpaque(flag);
    }

    /**
     * Determine if input is required in this field.
     *
     * @return <code>true</code> if input is required in this field, and
     * <code>false</code>
     * otherwise.
     */

    public boolean isInputRequired() {
        return (inputRequired);
    }

    /**
     * Specify whether an input is required in this field. If no input is
     * required, the <code>validateInput()</code> method will return
     * <code>true</code> if the field is left empty; otherwise it will return
     * <code>false</code>.
     *
     * @param flag The flag.
     * @see #validateInput
     * @see #isInputRequired
     */

    public void setInputRequired(boolean flag) {
        inputRequired = flag;
    }

    /**
     * Validate the input in this field.
     *
     * @return <code>true</code> if the field contains valid input or if the
     * field contains no input and input is not required, and <code>false</code>
     * otherwise.
     */

    public boolean validateInput() {
        String s = getText().trim();

        if (s.length() == 0) {
            return (!isInputRequired());
        }

        return (checkInput());
    }

    /**
     * Determine if the given input is valid for this field. The default
     * implementation returns <code>true</code>.
     *
     * @return <code>true</code> if the input is valid, and <code>false</code>
     * otherwise.
     */

    protected boolean checkInput() {
        return (true);
    }

    /**
     * Get the maxmium number of characters that may be entered into this field.
     *
     * @return The maximum length, or <code>KDocument.NO_LIMIT</code> if there
     * is no limit.
     */

    public int getMaximumLength() {
        Document d = getDocument();
        if (d instanceof KDocument) {
            return (((KDocument) d).getMaximumLength());
        } else {
            return (KDocument.NO_LIMIT);
        }
    }

    /**
     * Set the maximum number of characters that may be entered into this field.
     * This method will have no effect if the document has been changed from
     * a <code>KDocument</code> via a call to <code>setDocument()</code>.
     *
     * @param length The new maximum length, or <code>KDocument.NO_LIMIT</code>
     *               for unlimited length.
     * @see com.hyperrealm.kiwi.ui.model.KDocument
     */

    public void setMaximumLength(int length) {
        Document d = getDocument();
        if (d instanceof KDocument) {
            ((KDocument) d).setMaximumLength(length);
        }
    }

    /**
     * Determine if the field is empty (that is, it contains no non-whitespace
     * characters.)
     *
     * @since Kiwi 2.0
     */

    public boolean isEmpty() {
        // this is probably not be the most efficient way to do this...

        String text = getText().trim();
        return (text.length() == 0);
    }

    /**
     * Clear the field.
     *
     * @since Kiwi 2.0
     */

    public void clear() {
        setText(null);
    }

    /**
     * @since Kiwi 2.0
     */

    public final JComponent getEditorComponent() {
        return (this);
    }

    /**
     * Disable entry of the specified character.
     *
     * @param c The character to disallow.
     * @since Kiwi 2.0.1
     */

    public void disableChar(char c) {
        allowedMap.clear(c);
    }

    /**
     * Disable entry of a range of characters.
     *
     * @param lo The first character in the range.
     * @param hi The last character in the range.
     * @since Kiwi 2.0.1
     */

    public void disableChars(char lo, char hi) {
        if (hi > lo) {
            for (char c = lo; c <= hi; c++) {
                allowedMap.clear(c);
            }
        }
    }

    /**
     * Enable entry of the specified character.
     *
     * @param c The character to disallow.
     * @since Kiwi 2.0.1
     */

    public void enableChar(char c) {
        allowedMap.set(c);
    }

    /**
     * Enable entry of a range of characters.
     *
     * @param lo The first character in the range.
     * @param hi The last character in the range.
     * @since Kiwi 2.0.1
     */

    public void enableChars(char lo, char hi) {
        if (hi > lo) {
            for (char c = lo; c <= hi; c++) {
                allowedMap.set(c);
            }
        }
    }

    /**
     * Enable entry of all characters.
     *
     * @since Kiwi 2.0.1
     */

    public void enableAllChars() {
        allowedMap.set(0, allowedMap.size() - 1);
    }

    private class DocumentListener implements javax.swing.event.DocumentListener {

        public void changedUpdate(DocumentEvent evt) {
            fireChange();
        }

        public void insertUpdate(DocumentEvent evt) {
            fireChange();
        }

        public void removeUpdate(DocumentEvent evt) {
            fireChange();
        }
    }

}
