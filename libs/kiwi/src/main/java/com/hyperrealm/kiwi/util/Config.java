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

package com.hyperrealm.kiwi.util;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.event.ChangeListener;

import com.hyperrealm.kiwi.event.ChangeSupport;
import com.hyperrealm.kiwi.event.PropertyChangeSource;
import com.hyperrealm.kiwi.text.ColorFormatter;
import com.hyperrealm.kiwi.text.FontFormatter;
import com.hyperrealm.kiwi.text.ParsingException;

/**
 * Configuration object. This class extends <code>Properties</code>,
 * adding convenience methods for storing and retrieving properties
 * as strings, integers, booleans, <code>Color</code>s, and
 * <code>Font</code>s. All values are stored internally as strings,
 * so that persisting the object will produce a human-readable and
 * -modifiable file.
 * <p>
 * Whenever the contents of the <code>Config</code> object change, a
 * <code>ChangeEvent</code> is fired. Also, when a specific property
 * in the object changes, a <code>PropertyChangeEvent</code> is
 * fired.
 *
 * @author Mark Lindner
 * @see java.util.Properties
 * @see com.hyperrealm.kiwi.io.ConfigFile
 * @see javax.swing.event.ChangeEvent
 * @see java.beans.PropertyChangeEvent
 */

public class Config extends Properties implements PropertyChangeSource {

    private static final String DEFAULT_DESCRIPTION = "Configuration Parameters";

    private static final String COMMA_DELIMITER = ",";
    /**
     * The description for this set of configuration parameters.
     */
    protected String description;
    /**
     * The support object for firing <code>ChangeEvent</code>s when the object
     * changes.
     */
    protected ChangeSupport support;
    /**
     * The support object for firing <coode>PropertyChangeEvent</code>s when
     * a property changes.
     */
    private PropertyChangeSupport psupport;

    /**
     * Construct a new <code>Config</code> with a default description.
     */

    public Config() {
        this(DEFAULT_DESCRIPTION);
    }

    /**
     * Construct a new <code>Config</code> object.
     *
     * @param description The description of the configuration parameters that
     *                    will be stored in this object (one line of text).
     */

    public Config(String description) {
        this.description = description;
        support = new ChangeSupport(this);
        psupport = new PropertyChangeSupport(this);
    }

    /**
     * Construct a new <code>Config</code> object from a <code>Properties</code>
     * list.
     *
     * @since Kiwi 1.3.2
     */

    public Config(Properties properties, String description) {
        this(description);

        for (Object o : properties.keySet()) {
            String key = (String) o;
            put(key, properties.get(key));
        }
    }

    /**
     * Get the description for this set of configuration parameters.
     *
     * @return The description.
     * @see #setDescription
     */

    public String getDescription() {
        return (description);
    }

    /**
     * Set the description for this set of configuration parameters.
     *
     * @param description The new description, or <code>null</code> if a default
     *                    description should be used.
     * @see #getDescription
     */

    public void setDescription(String description) {
        this.description = (description == null ? DEFAULT_DESCRIPTION
            : description);
    }

    /**
     * Look up a <code>String</code> property.
     *
     * @param key The name of the property.
     * @return The property's value, as a <code>String</code>, or
     * <code>null</code> if a property with the specified name does not exist.
     * @see #putString
     */

    public String getString(String key) {
        return ((String) get(key));
    }

    /**
     * Look up a <code>String</code> property.
     *
     * @param key          The name of the property.
     * @param defaultValue The default value to return.
     * @return The property's value, as a <code>String</code>, or
     * <code>defaultValue</code> if a property with the specified name does not
     * exist.
     * @see #putString
     */

    public String getString(String key, String defaultValue) {
        String s = (String) get(key);
        if (s == null) {
            s = defaultValue;
        }

        return (s);
    }

    /**
     * Store a <code>String</code> property.
     *
     * @param key   The name of the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or <code>null</code> if
     * there was no previous value.
     * @see #getString
     */

    public String putString(String key, String value) {
        String old = getString(key);
        put(key, value);

        return (old);
    }

    /**
     * Look up an integer property.
     *
     * @param key The name of the property.
     * @return The property's value, as an <code>int</code>, or <code>0</code>
     * if a property with the specified name does not exist.
     * @see #putInt
     */

    public int getInt(String key) {
        return (getInt(key, 0));
    }

    /**
     * Look up an integer property.
     *
     * @param key          The name of the property.
     * @param defaultValue The default value to return.
     * @return The property's value, as an <code>int</code>, or
     * <code>defaultValue</code> if a property with the specified name does not
     * exist.
     * @see #putInt
     */

    public int getInt(String key, int defaultValue) {
        String s = (String) get(key);
        if (s == null) {
            return (defaultValue);
        }

        int val = defaultValue;
        try {
            val = Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
        }

        return (val);
    }

    /**
     * Store an integer property.
     *
     * @param key   The name of the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or 0 if there
     * was no previous value.
     * @see #getInt
     */

    public int putInt(String key, int value) {
        int old = getInt(key);
        String s = String.valueOf(value);
        put(key, s);

        return (old);
    }

    /**
     * Look up a double property.
     *
     * @param key The name of the property.
     * @return The property's value, as a <code>double</code>, or <code>0.0</code>
     * if a property with the specified name does not exist.
     * @see #putDouble
     * @since Kiwi 2.2
     */

    public double getDouble(String key) {
        return (getDouble(key, 0.0));
    }

    /**
     * Look up an integer property.
     *
     * @param key          The name of the property.
     * @param defaultValue The default value to return.
     * @return The property's value, as an <code>double</code>, or
     * <code>defaultValue</code> if a property with the specified name does not
     * exist.
     * @see #putDouble
     * @since Kiwi 2.2
     */

    public double getDouble(String key, double defaultValue) {
        String s = (String) get(key);
        if (s == null) {
            return (defaultValue);
        }

        double val = defaultValue;
        try {
            val = Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
        }

        return (val);
    }

    /**
     * Store a double property.
     *
     * @param key   The name of the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or 0.0 if there
     * was no previous value.
     * @see #getDouble
     * @since Kiwi 2.2
     */

    public double putDouble(String key, double value) {
        double old = getDouble(key);
        String s = String.valueOf(value);
        put(key, s);

        return (old);
    }

    /**
     * Look up an boolean property.
     *
     * @param key The name of the property.
     * @return The property's value, as a <code>boolean</code>. Returns
     * <code>false</code> if a property with the specified name does not exist.
     * @see #putBoolean
     */

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Look up a boolean property.
     *
     * @param key          The name of the property.
     * @param defaultValue The default value to return.
     * @return The property's value, as a <code>boolean</code>, or
     * <code>defaultValue</code> if a property with the specified name does not
     * exist.
     * @see #putBoolean
     */

    public boolean getBoolean(String key, boolean defaultValue) {
        String s = (String) get(key);
        if (s == null) {
            return (defaultValue);
        }

        return Boolean.valueOf(s);
    }

    /**
     * Store a boolean property.
     *
     * @param key   The name of the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or <code>false</code> if
     * there was no previous value.
     * @see #getBoolean
     */

    public boolean putBoolean(String key, boolean value) {
        boolean old = getBoolean(key);
        String s = String.valueOf(value);
        put(key, s);

        return (old);
    }

    /**
     * Look up a boolean array property.
     *
     * @param key   The name of the property.
     * @param array The array in which to store the values. If the array is not
     *              large enough for all of the values, the extra values are discarded. If
     *              the array is too large, the extra elements are filled with the value
     *              <code>false</code>.
     * @return The array.
     * @see #putBooleanArray
     * @since Kiwi 2.2
     */

    public boolean[] getBooleanArray(String key, boolean[] array) {
        return (getBooleanArray(key, array, false));
    }

    /**
     * Look up a boolean array property.
     *
     * @param key          The name of the property.
     * @param array        The array in which to store the values. If the array is not
     *                     large enough for all of the values, the extra values are discarded. If
     *                     the array is too large, the extra elements are filled with the default
     *                     value.
     * @param defaultValue The default value for filling the array.
     * @return The array.
     * @see #putBooleanArray
     * @since Kiwi 2.2
     */

    public boolean[] getBooleanArray(String key, boolean[] array,
                                     boolean defaultValue) {
        int last = 0;
        String s = (String) get(key);
        if (s != null) {
            StringTokenizer st = new StringTokenizer(s, COMMA_DELIMITER);
            while (st.hasMoreTokens() && (last < array.length)) {
                array[last++] = Boolean.parseBoolean(st.nextToken());
            }
        }

        for (int i = last; i < array.length; i++) {
            array[i] = defaultValue;
        }

        return (array);
    }

    /**
     * Store a boolean array property.
     *
     * @param key   The name of the property.
     * @param array The values to store.
     * @see #getBooleanArray
     * @since Kiwi 2.2
     */

    public void putBooleanArray(String key, boolean[] array) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(',');
            }

            sb.append(array[i]);
        }

        put(key, sb.toString());
    }

    /**
     * Look up an integer array property.
     *
     * @param key   The name of the property.
     * @param array The array in which to store the values. If the array is not
     *              large enough for all of the values, the extra values are discarded. If
     *              the array is too large, the extra elements are filled with the value
     *              <code>0</code>.
     * @return The array.
     * @see #putIntArray
     * @since Kiwi 2.2
     */

    public int[] getIntArray(String key, int[] array) {
        return (getIntArray(key, array, 0));
    }

    /**
     * Look up an integer array property.
     *
     * @param key          The name of the property.
     * @param array        The array in which to store the values. If the array is not
     *                     large enough for all of the values, the extra values are discarded. If
     *                     the array is too large, the extra elements are filled with the default
     *                     value.
     * @param defaultValue The default value for filling the array.
     * @return The array.
     * @see #putIntArray
     * @since Kiwi 2.2
     */

    public int[] getIntArray(String key, int[] array, int defaultValue) {
        String s = (String) get(key);
        return (StringUtils.parseIntArray(s, array, defaultValue));
    }

    /**
     * Store an integer array property.
     *
     * @param key   The name of the property.
     * @param array The values to store.
     * @see #getBooleanArray
     * @since Kiwi 2.2
     */

    public void putIntArray(String key, int[] array) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(',');
            }

            sb.append(array[i]);
        }

        put(key, sb.toString());
    }

    /**
     * Look up a double array property.
     *
     * @param key   The name of the property.
     * @param array The array in which to store the values. If the array is not
     *              large enough for all of the values, the extra values are discarded. If
     *              the array is too large, the extra elements are filled with the value
     *              <code>0.0</code>.
     * @return The array.
     * @see #putDoubleArray
     * @since Kiwi 2.2
     */

    public double[] getDoubleArray(String key, double[] array) {
        return (getDoubleArray(key, array, 0.0));
    }

    /**
     * Look up a double array property.
     *
     * @param key          The name of the property.
     * @param array        The array in which to store the values. If the array is not
     *                     large enough for all of the values, the extra values are discarded. If
     *                     the array is too large, the extra elements are filled with the default
     *                     value.
     * @param defaultValue The default value for filling the array.
     * @return The array.
     * @see #putDoubleArray
     * @since Kiwi 2.2
     */

    public double[] getDoubleArray(String key, double[] array,
                                   double defaultValue) {
        int last = 0;
        String s = (String) get(key);
        if (s != null) {
            StringTokenizer st = new StringTokenizer(s, COMMA_DELIMITER);
            while (st.hasMoreTokens() && (last < array.length)) {
                double val = defaultValue;

                try {
                    val = Double.parseDouble(st.nextToken());
                } catch (NumberFormatException ignored) {
                }

                array[last++] = val;
            }
        }

        for (int i = last; i < array.length; i++) {
            array[i] = defaultValue;
        }

        return (array);
    }

    /**
     * Store a double array property.
     *
     * @param key   The name of the property.
     * @param array The values to store.
     * @see #getBooleanArray
     * @since Kiwi 2.2
     */

    public void putDoubleArray(String key, double[] array) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(',');
            }

            sb.append(array[i]);
        }

        put(key, sb.toString());
    }

    /**
     * Look up a <code>Color</code> property.
     *
     * @param key The name of the property.
     * @return The property's value, as a <code>Color</code>. Returns
     * <code>null</code> if a property with the specified name does not exist,
     * or is not a properly formatted color specification.
     * @see #putColor
     */

    public Color getColor(String key) {
        return (getColor(key, null));
    }

    /**
     * Look up a <code>Color</code> property.
     *
     * @param key          The name of the property.
     * @param defaultValue The default value to return.
     * @return The property's value, as a <code>Color</code>, or
     * <code>defaultValue</code> if a property with the specified name does not
     * exist.
     * @see #putColor
     */

    public Color getColor(String key, Color defaultValue) {

        Color c = defaultValue;
        String s = (String) get(key);
        if (s != null) {
            try {
                c = ColorFormatter.parse(s);
            } catch (ParsingException ignored) {

            }
        }
        return c;
    }

    /**
     * Store a <code>Color</code> property.
     *
     * @param key   The name of the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or <code>null</code> if
     * there was no previous value.
     * @see #getColor
     */

    public Color putColor(String key, Color value) {
        Color old = getColor(key);
        String s = ColorFormatter.format(value);
        put(key, s);

        return (old);
    }

    /**
     * Look up a <code>Font</code> property.
     *
     * @param key The name of the property.
     * @return The property's value, as a <code>Font</code>. Returns
     * <code>null</code> if a property with the specified name does not exist,
     * or is not a properly formatted font specification.
     * @see #putFont
     */

    public Font getFont(String key) {
        return (getFont(key, null));
    }

    /**
     * Look up a <code>Font</code> property.
     *
     * @param key          The name of the property.
     * @param defaultValue The default value to return.
     * @return The property's value, as a <code>Font</code>, or
     * <code>defaultValue</code> if a property with the specified name does not
     * exist.
     * @see #putFont
     */

    public Font getFont(String key, Font defaultValue) {

        Font c = defaultValue;
        String s = (String) get(key);
        if (s != null) {
            try {
                c = FontFormatter.parse(s);
            } catch (ParsingException ignored) {

            }
        }
        return c;
    }

    /**
     * Store a <code>Font</code> property.
     *
     * @param key   The name of the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or <code>null</code> if
     * there was no previous value.
     * @see #getFont
     */

    public Font putFont(String key, Font value) {
        Font old = getFont(key);
        String s = FontFormatter.format(value);
        put(key, s);

        return (old);
    }

    /**
     * Look up a <code>File</code> property.
     *
     * @param key          The name of the property.
     * @param defaultValue The default value to return.
     * @return The property's value, as a <code>File</code>, or
     * <code>defaultValue</code> if a property with the specified name does not
     * exist.
     * @see #putFile
     * @since Kiwi 2.1.1
     */

    public File getFile(String key, File defaultValue) {
        String s = (String) get(key);

        if (s == null) {
            return (defaultValue);
        } else {
            return (new File(s));
        }
    }

    /**
     * Store a <code>File</code> property.
     *
     * @param key   The name of the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or <code>null</code> if
     * there was no previous value.
     * @see #getFile
     * @since Kiwi 2.1.1
     */

    public File putFile(String key, File value) {
        String old = (String) get(key);

        put(key, value.getAbsolutePath());

        return (old == null ? null : new File(old));
    }

    /**
     * Look up an arbitrary object property.
     *
     * @param key          The name of the property.
     * @param defaultValue The default value to return.
     * @return The property's value, or <code>defaultValue</code> if a
     * property with the specified name does not exist.
     * @see #putObject
     * @since Kiwi 2.0
     */

    public Object getObject(String key, Object defaultValue) {
        Object o = get(key);
        if (o == null) {
            return (defaultValue);
        }

        return (o);
    }

    /**
     * Store an arbitrary object property.
     *
     * @param key   The name of the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or <code>null</code> if
     * there was no previous value.
     * @see #getObject
     * @since Kiwi 2.0
     */

    public Object putObject(String key, Object value) {
        Object old = get(key);
        put(key, value);

        return (old);
    }

    /**
     * Store an arbitrary property.
     *
     * @param key   The object that identifies the property.
     * @param value The value of the property.
     * @return The old value associated with this key, or <code>null</code> if
     * there was no previous value.
     */

    public Object put(Object key, Object value) {
        Object o = super.put(key, value);
        support.fireChangeEvent();
        psupport.firePropertyChange(key.toString(), o, value);

        return (o);
    }

    /**
     * Remove a property. Removes the property for the given key.
     *
     * @param key The object that identifies the property.
     * @return The value associated with this key, or <code>null</code> if there
     * was no property with the given key in this object.
     * @see #clear
     */

    public Object remove(Object key) {
        Object o = super.remove(key);
        if (o != null) {
            support.fireChangeEvent();
            psupport.firePropertyChange(key.toString(), o, null);
        }

        return (o);
    }

    /**
     * Remove all properties. Removes all properties from this object.
     */

    public void clear() {
        if (size() > 0) {
            super.clear();
            support.fireChangeEvent();
        }
    }

    /**
     * Get an iterator of property names.
     *
     * @since Kiwi 2.1
     */

    public Iterator iterator() {
        return (keySet().iterator());
    }

    /**
     * Add a <code>ChangeListener</code> to this object's list of listeners.
     *
     * @param listener The listener to add.
     */

    public void addChangeListener(ChangeListener listener) {
        support.addChangeListener(listener);
    }

    /**
     * Remove a <code>ChangeListener</code> from this object's list of
     * listeners.
     *
     * @param listener The listener to remove.
     */

    public void removeChangeListener(ChangeListener listener) {
        support.removeChangeListener(listener);
    }

    /**
     * Add a <code>PropertyChangeListener</code> to this object's list of
     * listeners.
     *
     * @param listener The listener to add.
     * @since Kiwi 1.3
     */

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        psupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a <code>PropertyChangeListener</code> from this object's list of
     * listeners.
     *
     * @param listener The listener to remove.
     * @since Kiwi 1.3
     */

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        psupport.removePropertyChangeListener(listener);
    }

}
