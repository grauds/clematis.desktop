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

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.Properties;

import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.ui.AudioClip;

/** A class that decodes various types of resources from input streams. This
 * class is used by resource loaders to read resources from files or network
 * connections. The functionality is provided for its possible use in other
 * contexts.
 *
 * @see com.hyperrealm.kiwi.util.ResourceLoader
 * @see com.hyperrealm.kiwi.util.ResourceManager
 * @since Kiwi 1.3
 *
 * @author Mark Lindner
 */

public class ResourceDecoder implements ImageObserver
{
  private static boolean imageLoaded = false;

  /** Construct a new <code>ResourceDecoder</code>.
   */

  public ResourceDecoder()
  {
  }

  /** Decode a string from an input stream. Constructs a <code>String</code>
   * object from all of the data read from an input stream.
   *
   * @param stream The input stream.
   * @return The resulting <code>String</code> object.
   * @throws java.io.IOException If an error occurred while reading from the
   * stream.
   */
  
  public String decodeString(InputStream stream) throws IOException
  {
    return(StreamUtils.readStreamToString(stream));
  }

  /** Decode an audio clip from an input stream. Constructs an
   * <code>AudioClip</code> object from all of the data read from an input
   * stream.
   *
   * @param stream The input stream.
   * @return The resulting <code>AudioCip</code> object.
   * @throws java.io.IOException If an error occurred while reading from the
   * stream.
   */

  public AudioClip decodeAudioClip(InputStream stream) throws IOException
  {
    return(new AudioClip(stream));
  }

  /** Decode an image from an input stream. Constructs an <code>Image</code>
   * object from all of the data read from an input stream.
   *
   * @param stream The input stream.
   * @return The resulting <code>Image</code> object.
   * @throws java.io.IOException If an error occurred while reading from the
   * stream.
   */

  public synchronized Image decodeImage(InputStream stream) throws IOException
  {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    byte data[] = StreamUtils.readStreamToByteArray(stream);
    Image im = toolkit.createImage(data);
    imageLoaded = false;
    toolkit.prepareImage(im, -1, -1, this);
    
    while(!imageLoaded)
    {
      try { wait(); }
      catch(InterruptedException ex) {}
    }

    return(im);
  }

  /** Decode a properties list from an input stream. Constructs a
   * <code>Properties</code> object from all of the data read from an input
   * stream.
   *
   * @param stream The input stream.
   * @return The resulting <code>Properties</code> object.
   * @throws java.io.IOException If an error occurred while reading from the
   * stream.
   */

  public Properties decodeProperties(InputStream stream) throws IOException
  {
    Properties prop = new Properties();
    prop.load(stream);

    return(prop);
  }

  /** Decode a configuration from an input stream. Constructs a
   * <code>Config</code> object from all of the data read from an input
   * stream.
   *
   * @param stream The input stream.
   * @return The resulting <code>Config</code> object.
   * @throws java.io.IOException If an error occurred while reading from the
   * stream.
   */

  public Config decodeConfig(InputStream stream) throws IOException
  {
    Config config = new Config();
    config.load(stream);

    return(config);
  }
  
  /** Image tracker method. This is an internal method and should not be called
   * directly.
   */

  public synchronized boolean imageUpdate(Image img, int infoflags, int x,
                                          int y, int w, int h)
  {
    if((infoflags & (ALLBITS | FRAMEBITS)) != 0)
    {
      imageLoaded = true;
      notifyAll();
    }
    return(true);
  }
}

/* end of source file */
