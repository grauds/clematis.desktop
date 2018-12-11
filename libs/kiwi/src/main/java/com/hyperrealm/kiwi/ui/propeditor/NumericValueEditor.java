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

package com.hyperrealm.kiwi.ui.propeditor;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.hyperrealm.kiwi.text.FormatConstants;
import com.hyperrealm.kiwi.ui.NumericField;
import com.hyperrealm.kiwi.util.DoubleHolder;

/**
 * A property editor for editing numeric properties.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */
@SuppressWarnings("MagicNumber")
public class NumericValueEditor extends PropertyValueEditor {
    private NumericField field;

    /**
     * Construct a new <code>NumericValueEditor</code>.
     */

    public NumericValueEditor() {
        field = new NumericField(10, FormatConstants.INTEGER_FORMAT);
    }

    /**
     *
     */

    protected void prepareEditor() {
        NumericPropertyType type = (NumericPropertyType) property.getType();
        field.setType(type.getFormat());
        field.clearMinValue();
        field.clearMaxValue();

        if (type.hasMaximumValue()) {
            field.setMaxValue(type.getMaximumValue());
        }

        if (type.hasMinimumValue()) {
            field.setMinValue(type.getMinimumValue());
        }

        DoubleHolder holder = (DoubleHolder) property.getValue();

        if (holder != null) {
            field.setValue(holder.getValue());
        } else {
            field.setText(null);
        }
    }

    /**
     *
     */

    public void commitInput() {
        DoubleHolder holder = (DoubleHolder) property.getValue();
        if (holder == null) {
            holder = new DoubleHolder();
            property.setValue(holder);
        }

        holder.setValue(field.getValue());
    }

    /**
     *
     */

    public boolean validateInput() {
        return (field.validateInput());
    }

    /**
     *
     */

    public void addActionListener(ActionListener listener) {
        field.addActionListener(listener);
    }

    /**
     *
     */

    public void removeActionListener(ActionListener listener) {
        field.removeActionListener(listener);
    }

    /**
     *
     */

    public JComponent getEditorComponent() {
        return (field);
    }

    /**
     *
     */

    public void startFocus() {
        field.requestFocus();
    }

}
