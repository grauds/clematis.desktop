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

import java.net.MalformedURLException;
import java.net.URL;

import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * A subclass of <code>DataField</code> for the input and display of URLs.
 *
 * @author Mark Lindner
 * @since Kiwi 2.0
 */

public class URLField extends DataField<URL> {

    private URL url = null;

    /**
     * Construct a new <code>NumericField</code> of the specified width and
     * a default type of <code>DECIMAL_FORMAT</code>.
     *
     * @param width The width of the field.
     */

    public URLField(int width) {
        super(width);

        setFont(KiwiUtils.BOLD_FONT);
    }

    /**
     * Get the URL that is currently displayed by this field.
     *
     * @return The URL, or <code>null</code> if there is no text in the field.
     */

    public URL getURL() {
        return (url);
    }

    /**
     * Set the URL to be displayed by this field.
     *
     * @param url The URL.
     */

    public void setURL(URL url) {
        this.url = url;

        setText(url == null ? null : url.toString());
    }

    /**
     * Get the object being edited. Equivalent to <code>getURL()</code>.
     */

    public URL getObject() {
        return (getURL());
    }

    /**
     * Set the object being edited. Equivalent to <code>setURL()</code>.
     */

    public void setObject(URL url) {
        setURL(url);
    }

    /**
     * Validate the input in this field.
     *
     * @return <code>true</code> if the field contains a valid URL, and
     * <code>false</code> otherwise.
     */

    protected boolean checkInput() {
        invalid = false;

        if (getText().trim().equals("")) {
            url = null;
            invalid = isInputRequired();
        } else {
            try {
                url = new URL(getText());
            } catch (MalformedURLException ex) {
                invalid = true;
            }
        }

        paintInvalid(invalid);

        return (!invalid);
    }

}
