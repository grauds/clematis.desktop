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

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.hyperrealm.kiwi.ui.AudioClip;

/**
 * A utility class containing methods for retrieving application resources;
 * these resources typically reside within a JAR file among the classes that
 * make up an application. The location of a resource is specified as a path
 * relative to the location of a class within the application's class
 * hierarchy. This "anchor class" is specified in the constructor.
 * <p>
 * Resources may be retrieved as byte arrays, as <code>String</code>s, as
 * <code>InputStream</code>s, as <code>AudioClip</code>s, as
 * <code>Image</code>s, or as <code>Properties</code> objects.
 * <p>
 * See <code>ResourceManager</code> for a higher-level interface.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.ResourceManager
 */

public class ResourceLoader {
    /**
     * The class object associated with this resource loader.
     */
    protected Class clazz;

    private ResourceDecoder decoder;

    /**
     * Construct a new <code>ResourceLoader</code>. A new resource loader is
     * created with a default input buffer size.
     */

    public ResourceLoader(Class clazz) {
        this.clazz = clazz;

        decoder = new ResourceDecoder();
    }

    /**
     * Retrieve a resource as a URL.
     *
     * @param path The location of the resource.
     * @return A <code>URL</code> reference to the resource.
     */

    public URL getResourceAsURL(String path) {
        return (clazz.getResource(path));
    }

    /**
     * Retrieve a resource as a stream.
     *
     * @param path The location of the resource.
     * @return An <code>InputStream</code> from which the resource data may be
     * read.
     * @throws java.io.IOException If the resource was not found.
     */

    public InputStream getResourceAsStream(String path) throws IOException {
        InputStream is = clazz.getResourceAsStream(path);

        if (is == null) {
            throw (new IOException("Resource not found"));
        }

        return (is);
    }

    /**
     * Retrieve a resource as a <code>String</code>. Retrieves the specified
     * resource, returning its data as a <code>String</code>. It is assumed that
     * the resource contains printable text.
     *
     * @param path The location of the resource.
     * @throws java.io.IOException If an error occurred while reading the
     *                             resource's data.
     */

    public final String getResourceAsString(String path) throws IOException {
        InputStream is = getResourceAsStream(path);
        String s = decoder.decodeString(is);
        is.close();

        return (s);
    }

    /**
     * Retrieve a resource as an <code>AudioClip</code>. Retrieves the specified
     * resource, returning its data as an <code>AudioClip</code>. It is assumed
     * that the resource contains valid audio data.
     *
     * @param path The location of the resource.
     */

    public final AudioClip getResourceAsAudioClip(String path) {
        AudioClip clip = null;

        try {
            InputStream is = getResourceAsStream(path);
            clip = decoder.decodeAudioClip(is);
        } catch (IOException ignored) {
        }

        return (clip);
    }

    /**
     * Retrieve a resource as an <code>Image</code>. Retrieves the specified
     * resource, returning its data as an <code>Image</code>. It is assumed that
     * the resource contains valid image data.
     *
     * @param path The location of the resource.
     */

    public synchronized Image getResourceAsImage(String path) {

        Image im = null;

        try {
            InputStream is = getResourceAsStream(path);
            im = decoder.decodeImage(is);
        } catch (IOException ignored) {
        }

        return (im);
    }

    /**
     * Retrieve a resource as a <code>Properties</code> object. Retrieves the
     * specified resource, returning its data as a <code>Properties</code>
     * object. It is assumed that the resource is a properly-formatted property
     * list.
     *
     * @param path The location of the resource.
     * @throws java.io.IOException If an error occurred while reading the
     *                             resource's data.
     */

    public final Properties getResourceAsProperties(String path)
        throws IOException {
        InputStream is = getResourceAsStream(path);
        Properties prop = decoder.decodeProperties(is);
        is.close();

        return (prop);
    }

}
