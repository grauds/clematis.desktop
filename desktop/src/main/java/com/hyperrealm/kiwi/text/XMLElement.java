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

package com.hyperrealm.kiwi.text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents an XML element. It includes the element tag as well
 * as a hash table of all of the tag's parameters and their values.
 * <p>
 * An example XML element might look like this:
 * <p>
 * <code>
 * &lt;img src=&quot;image.gif&quot; width=100 height=150&gt;
 * </code>
 * <p>
 * In this case <tt>img</tt> is the tag, and <tt>src</tt>, <tt>width</tt> and
 * <tt>height</tt> are the parameters, with values <tt>image.gif</tt>,
 * <tt>100</tt> and <tt>150</tt>, respectively.
 *
 * @author Mark Lindner
 */

public class XMLElement {

    private String tag;

    private HashMap<String, String> attrs;

    private boolean end, empty;

    /**
     * Construct a new <code>XMLElement</code>.
     *
     * @param tag The element's tag.
     * @param end A boolean flag specifying whether this is an end tag. For
     *            example, <code></b></code> is an end tag.
     */

    public XMLElement(String tag, boolean end) {
        this.tag = tag;
        this.end = end;
        attrs = new HashMap<>();
    }

    /**
     * Construct a new <code>XMLElement</code>. The tag is set to the empty
     * string and the end flag is set to <code>false</code>.
     */

    public XMLElement() {
        this("", false);
    }

    /**
     * Check if this is an end tag.
     *
     * @return <code>true</code> if this is an end tag and <code>false</code>
     * otherwise.
     */

    public boolean isEnd() {
        return (end);
    }

    /**
     * Set the end tag flag.
     *
     * @param end The new end tag flag value.
     */

    public void setEnd(boolean end) {
        this.end = end;
    }

    /**
     * Check if this is an empty tag, e.g., <code>&lt;br/&gt;</code>.
     *
     * @return <code>true</code> if this is an empty tag and <code>false</code>
     * otherwise.
     * @since Kiwi 2.1.1
     */

    public boolean isEmpty() {
        return (empty);
    }

    /**
     * Set the empty tag flag.
     *
     * @param empty The new empty tag flag value.
     * @since Kiwi 2.1.1
     */

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    /**
     * Get the tag.
     *
     * @return The element's tag.
     */

    public String getTag() {
        return (tag);
    }

    /**
     * Set the tag.
     *
     * @param tag The new tag.
     */

    public void setTag(String tag) {
        this.tag = tag.toLowerCase();
    }

    /**
     * Add an attribute. Adds an attribute to the element's attribute list.
     * If there is already an attribute with the given name in the list, it is
     * replaced.
     *
     * @param name  The name of the attribute.
     * @param value The value for the attribute (may be <code>null</code>).
     * @since Kiwi 2.1
     */

    public void addAttribute(String name, String value) {
        attrs.put(name.toLowerCase(), value);
    }

    /**
     * Get the value of an attribute.
     *
     * @param name The name of the attribute.
     * @return The value of the named attribute, or <code>null</code> if the
     * attribute does not exist, or has no value.
     * @since Kiwi 2.1
     */

    public String getAttributeValue(String name) {
        return (attrs.get(name));
    }

    /**
     * Get an iterator to the attribute names.
     *
     * @since Kiwi 2.1
     */

    public Iterator<String> getAttributeNames() {
        return (attrs.keySet().iterator());
    }

    /**
     * Create a string representatin of the element.
     */

    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append('<');
        if (end) {
            s.append('/');
        }
        s.append(tag);

        for (Map.Entry<String, String> e : attrs.entrySet()) {

            s.append(' ');
            s.append(e.getKey());
            String val = e.getValue();
            if (val != null) {
                s.append('=');
                s.append('"');
                s.append(val);
                s.append('"');
            }
        }

        if (empty) {
            s.append('/');
        }
        s.append('>');
        return (s.toString());
    }

}
