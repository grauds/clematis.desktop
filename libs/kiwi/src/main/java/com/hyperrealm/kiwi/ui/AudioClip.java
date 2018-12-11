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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

//CHECKSTYLE:OFF
/**
 * This class represents an audio clip. The audio data is restricted to the
 * 8000Hz, single-channel u-law format. The class relies on the undocumented
 * <b>sun.audio</b> package and thus may not be portable.
 * <p>
 * <code>AudioClip</code>s may be read from streams, from files, or loaded as
 * system resources using a <code>ResourceManager</code> or
 * <code>ResourceLoader</code>.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.ResourceManager#getSound
 * @see com.hyperrealm.kiwi.util.ResourceLoader#getResourceAsURL
 */
@SuppressWarnings("sunapi")
public class AudioClip implements java.applet.AudioClip {

    static int length;

    private sun.audio.AudioData audioData;

    private sun.audio.AudioDataStream audioStream = null;

    private sun.audio.ContinuousAudioDataStream cAudioStream = null;

    private String name = "Untitled";

    /**
     * Construct a new <code>AudioClip</code>.
     *
     * @param url The location of the audio data.
     * @throws java.io.IOException If there is a problem reading from the
     *                             specified URL.
     */

    public AudioClip(URL url) throws IOException {
        audioData = new sun.audio.AudioStream(url.openStream()).getData();
    }

    /**
     * Construct a new <code>AudioClip</code>.
     *
     * @param file The name of the file that contains the audio data.
     * @throws java.io.IOException If there is a problem reading from the
     *                             specified file.
     */

    public AudioClip(String file) throws IOException {
        this(new FileInputStream(file));
    }

    /**
     * Construct a new <code>AudioClip</code>.
     *
     * @param stream The stream to read the audio data from.
     * @throws java.io.IOException If there is a problem reading from the
     *                             specified stream.
     */

    public AudioClip(InputStream stream) throws IOException {
        audioData = new sun.audio.AudioStream(stream).getData();
    }

    /**
     * Play the audio clip.
     */

    public void play() {
        audioStream = new sun.audio.AudioDataStream(audioData);
        sun.audio.AudioPlayer.player.start(audioStream);
    }

    /**
     * Play the audio clip continuously.
     */

    public void loop() {
        cAudioStream = new sun.audio.ContinuousAudioDataStream(audioData);
        sun.audio.AudioPlayer.player.start(cAudioStream);
    }

    /**
     * Stop playing the audio clip.
     */

    public void stop() {
        if (audioStream != null) {
            sun.audio.AudioPlayer.player.stop(audioStream);
        }
        if (cAudioStream != null) {
            sun.audio.AudioPlayer.player.stop(cAudioStream);
        }
    }

    /**
     * Get the audio clip's name.
     *
     * @return The name.
     */

    public String getName() {
        return (name);
    }

    /**
     * Set the audio clip's name.
     *
     * @param name The name.
     */

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    /**
     * Get a string representation of this object.
     *
     * @return The name of the audio clip.
     */

    public String toString() {
        return (getName());
    }

}

//CHECKSTYLE:ON