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

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;

import com.hyperrealm.kiwi.text.FontFormatter;
import com.hyperrealm.kiwi.text.ParsingException;

/**
 * Locale-specific message bundle. This class serves as a lookup dictionary
 * for localized messages, and provides some convenience methods for formatting
 * the messages.
 *
 * @author Mark Lindner
 */

public class LocaleData {
    /**
     * The default message list delimiter.
     */
    private static final String DEFAULT_DELIMITER = ",";

    private final Object[] unitArray = new Object[1];

    private Map source;

    // The parent. Any resources not found in this object will be looked up
    // recursively in the parent.
    private LocaleData parent;

    /**
     * Construct a new <code>LocaleData</code> object from the given input
     * stream.
     *
     * @param instream The stream to read the data from.
     * @throws java.io.IOException If an error occurred while reading from
     *                             the stream.
     */

    public LocaleData(InputStream instream) throws IOException {
        Properties props = new Properties();
        props.load(instream);

        source = props;
    }

    /**
     * Construct a new <code>LocaleData</code> object from the given map.
     *
     * @param source A map that contains the key/value pairs.
     * @since Kiwi 1.3
     */

    public LocaleData(Map source) {
        this.source = source;
    }

    LocaleData getParent() {
        return parent;
    }

    void setParent(LocaleData parent) {
        this.parent = parent;
    }

    /**
     * Get a message for the specified key. If the message is not found in this
     * locale then it will be looked up in the less specific parent
     * locale. (E.g. if the message is not found in the locale "en_GB", it will
     * be looked up in "en".)
     *
     * @param key The key.
     * @return A message for the specified key.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            specified key was not found.
     */

    public String getMessage(String key) throws ResourceNotFoundException {
        Object o = source.get(key);
        if (o == null) {
            if (parent != null) {
                o = parent.getMessage(key);
            } else {
                throw (new ResourceNotFoundException("Resource not found: " + key));
            }
        }
        return ((String) o);
    }

    /**
     * Get a message for the specified key, and format the message, substituting
     * the specified arguments for the message's placeholders. Messages may have
     * placeholders of the form {n}, where n is a non-negative integer. For
     * example, the message <tt>"My name is {0}, and I am {1} years old."</tt>
     * and argument list <code>{ "Joe", new Integer(12) }</code> would be
     * formatted as <tt>My name is Joe, and I am 12 years old.</tt>
     *
     * @param key  The key.
     * @param args An array of arguments for the message.
     * @return A formatted message for the specified key.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            specified key was not found.
     */

    public String getMessage(String key, Object... args) {
        return (MessageFormat.format(getMessage(key), args));
    }

    /**
     * Get a message for the specified key, and format the message, substituting
     * the specified argument for the message's first placeholder. Messages may
     * have* placeholders of the form {n}, where n is a non-negative integer. For
     * example, the message <tt>"My name is {0}"</tt> and argument
     * <code>"Joe"</code> would be formatted as <tt>My name is Joe.</tt>
     *
     * @param key The key.
     * @param arg A single argument for the message.
     * @return A formatted message for the specified key.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            specified key was not found.
     */

    public String getMessage(String key, Object arg) {
        // Reuse a single array so we don't waste heap space.

        synchronized (unitArray) {
            unitArray[0] = arg;
            return (getMessage(key, unitArray));
        }
    }

    /**
     * Get a message list for the specified key. Retrieves a message for the
     * specified key, and breaks the message on the default delimiter (",")
     * constructing an array in the process.
     *
     * @param key The key.
     * @return An array of messages for the specified key.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            specified key was not found.
     */

    public String[] getMessageList(String key) throws ResourceNotFoundException {
        return (getMessageList(key, DEFAULT_DELIMITER));
    }

    /**
     * Get a message list for the specified key. Retrieves a message for the
     * specified key, and breaks the message on the specified delimiter
     * constructing an array in the process.
     *
     * @param key       The key.
     * @param delimiter The delimiter to use.
     * @return An array of messages for the specified key.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                           specified key was not found.
     */

    public String[] getMessageList(String key, String delimiter)
        throws ResourceNotFoundException {
        String msg = getMessage(key);

        return (StringUtils.split(msg, delimiter));
    }

    /**
     * Get a font for the specified key. Retrieves a message for the
     * specified key, and instantiates a Font object specified by that
     * message. (The format used is the same as that defined by the
     * {@link com.hyperrealm.kiwi.text.FontFormatter} class.)
     *
     * @param key         The key.
     * @param defaultFont The font to return if the font specified by the
     *                    message could not be parsed successfully.
     * @return The font.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                           specified key was not found.
     * @since Kiwi 2.2
     */

    public Font getFont(String key, Font defaultFont)
        throws ResourceNotFoundException {
        String msg = getMessage(key);

        Font font = defaultFont;

        try {
            font = FontFormatter.parse(msg);
        } catch (ParsingException ignored) {
        }

        return (font);
    }

    /**
     * Get an integer array for the specified key. Retrieves a message
     * for the specified key, which is assumed to be a comma-separated
     * list of integer values, and returns the parsed values as an
     * array. This method is useful for fetching table column width arrays.
     *
     * @param key          The key.
     * @param defaultValue The default value for the array elements.
     * @return The array of values.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                           specified key was not found.
     * @since Kiwi 2.4
     */

    public int[] getIntArray(String key, int[] array, int defaultValue)
        throws ResourceNotFoundException {
        String msg = getMessage(key);

        return (StringUtils.parseIntArray(msg, array, defaultValue));
    }

    /**
     * Determine if a message is defined for the specified key.
     *
     * @param key The key.
     * @return <code>true</code> if the key exists, and <code>false</code>
     * otherwise.
     */

    public boolean isMessageDefined(String key) {
        return (source.get(key) != null);
    }

}
