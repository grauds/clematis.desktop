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

package com.hyperrealm.kiwi.ui.applet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import com.hyperrealm.kiwi.ui.KiwiAudioClip;

import lombok.extern.java.Log;

/**
 * A basic implementation of an applet context. The
 * <code>showDocument()</code> and <code>showStatus()</code> methods are
 * no-ops and may be overridden by subclasses to provide application-specific
 * behavior.
 *
 * @author Mark Lindner
 * @since Kiwi 1.4.2
 */
@Log
public abstract class KiwiAppletContext implements AppletContext {

    private HashMap<String, Applet> applets;

    private HashMap<String, InputStream> streams;

    /**
     * Construct a new Kiwi applet context.
     */

    public KiwiAppletContext() {
        applets = new HashMap<>();
        streams = new HashMap<>();
    }

    /**
     * Add an applet to the applet context.
     *
     * @param name   The name of the applet.
     * @param applet The applet to add.
     */

    public void addApplet(String name, Applet applet) {
        applets.put(name, applet);
    }

    /**
     * Remove an applet from the applet context.
     *
     * @param name The name of the applet to remove.
     */

    public void removeApplet(String name) {
        applets.remove(name);
    }

    /**
     * Look up an applet by name.
     *
     * @param name The name of the applet
     * @return The <code>Applet</code> object on success, or <code>null</code>
     * if an applet with the given name was not found in this applet context.
     */

    public Applet getApplet(String name) {
        return applets.get(name);
    }

    /**
     * Get all of the applets managed by this applet context.
     *
     * @return An enumeration of <code>Applet</code> objects.
     */

    public Enumeration<Applet> getApplets() {
        return Collections.enumeration(applets.values());
    }

    /**
     * Load an audio clip at the specified URL.
     *
     * @param url The URL of the audio clip to load.
     * @return The audio clip on success, or <code>null</code> on failure.
     */

    public KiwiAudioClip getAudioClip(URL url) {
        KiwiAudioClip ac = null;

        try {
            ac = new KiwiAudioClip(url);

        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return (ac);
    }

    /**
     * Load an image at the specified URL.
     *
     * @param url The URL of the image to load.
     * @return The image on success, or <code>null</code> on failure.
     */

    public Image getImage(URL url) {
        Toolkit tk = Toolkit.getDefaultToolkit();

        return tk.getImage(url);
    }

    /**
     * Display the document at the given URL. The default implementation does
     * nothing.
     */

    public void showDocument(URL url) {
    }

    /**
     * Display the document at the given URL. The default implementation does
     * nothing.
     */

    public void showDocument(URL url, String target) {
    }

    /**
     * Get a list of keys of the streams associated with this applet context.
     *
     * @return An iterator for the stream keys.
     */

    public Iterator<String> getStreamKeys() {
        return streams.keySet().iterator();
    }

    /**
     * Associate a stream with this applet context.
     *
     * @param key    The key for the stream.
     * @param stream The stream.
     */

    public void setStream(String key, InputStream stream) {
        streams.put(key, stream);
    }

    /**
     * Look up a stream by key.
     *
     * @param key The key for the stream.
     * @return The <code>InputStream</code> on success, or <code>null</code> if
     * a stream with the given key is not associated with this applet context.
     */

    public InputStream getStream(String key) {
        return streams.get(key);
    }

    /**
     * Display a status message from the applet. The default implementation
     * does nothing.
     *
     * @param msg The message to display.
     */

    public void showStatus(String msg) {
    }

}
