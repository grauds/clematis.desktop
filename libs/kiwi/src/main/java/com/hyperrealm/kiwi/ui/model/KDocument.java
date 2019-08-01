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

package com.hyperrealm.kiwi.ui.model;

import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A specialization of <code>PlainDocument</code> that introduces length
 * limits on a document. This class is useful as a way to constrain the length
 * of input in a <code>JTextField</code> or similar text-entry component.
 *
 * @author Mark Lindner
 * @see javax.swing.JTextField
 */

public class KDocument extends PlainDocument {
    /**
     * A constant specifying a length of 'unlimited.'
     */
    public static final int NO_LIMIT = 0;
    private int maxLength = NO_LIMIT;
    private boolean modified = false;

    /**
     * Construct a new <code>KDocument</code> with unlimited length.
     */

    public KDocument() {
    }

    /**
     * Construct a new <code>KDocument</code> with the specified maximum length.
     *
     * @param length The maximum length for the document.  The constant
     *               <code>NO_LIMIT</code> may be passed to specify unlimited length.
     * @since Kiwi 2.0
     */

    public KDocument(int length) {
        maxLength = length;
    }

    /**
     * Get the current maximum length for this document.
     *
     * @return The current maximum length, or <code>NO_LIMIT</code> if the
     * length is unlimited.
     */

    public int getMaximumLength() {
        return (maxLength);
    }

    /**
     * Set the maximum length that this document is allowed to have. The
     * default length is <code>NO_LIMIT</code>, which means unlimited length.
     *
     * @param length The new maximum length for the document. If the document
     *               is currently longer than this length, the excess characters are deleted.
     *               The constant <code>NO_LIMIT</code> may be passed to specify unlimited
     *               length.
     */

    public void setMaximumLength(int length) {
        maxLength = Math.max(length, 0);

        if (maxLength != NO_LIMIT) {
            truncate();
        }
    }

    /**
     * Overridden to constrain document length.
     */

    public void insertString(int offset, String string, AttributeSet a)
        throws BadLocationException {
        if (maxLength == NO_LIMIT) {
            modified = true;
            super.insertString(offset, string, a);
        } else if (maxLength == getLength()) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            super.insertString(offset, string, a);
            truncate();
            modified = true;
        }
    }

    /**
     * Remove a range of text from the document.
     *
     * @param offset The offset of the range.
     * @param length The length of the range.
     * @since Kiwi 2.0.1
     */

    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);

        modified = true;
    }

    /**
     * Determine if this model has been modified in any way since its creation
     * or since the last call to <code>clearModified()</code>.
     *
     * @since Kiwi 2.0.1
     */

    public boolean isModified() {
        return (modified);
    }

    /**
     * Clear the <i>modified</i> flag for this model.
     *
     * @since Kiwi 2.0.1
     */

    public void clearModified() {
        modified = false;
    }

    /* Truncate excess length. */

    private void truncate() {
        int excess = getLength() - maxLength;
        if (excess > 0) {
            try {
                remove(maxLength, excess);
            } catch (BadLocationException ignored) {
            }
        }
    }

}
