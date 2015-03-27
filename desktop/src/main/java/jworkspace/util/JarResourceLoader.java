package jworkspace.util;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import com.hyperrealm.kiwi.util.ProgressObserver;
import jworkspace.kernel.Workspace;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Loads images from jar any file unlike other
 * resorce loaders, that load images only as
 * class resources from paths, denoted by
 * full qualified name of related class.
 * All that nessesary for this class - is
 * the name of jar and name of image inside jar.
 */
public class JarResourceLoader implements ImageObserver
{
    private static boolean imageLoaded = false;
    private static ProgressObserver progressObserver = null;

    /**
     * Default public constructor.
     */
    public JarResourceLoader()
    {
        super();
    }

    /**
     * Public constructor
     * @param progressObserver kiwi.util.ProgressObserver
     */
    public JarResourceLoader(ProgressObserver progressObserver)
    {
        super();
        this.progressObserver = progressObserver;
    }

    /**
     * Reads Image from jar file.
     */
    public synchronized final Image getJarImage(String file, String imageName)
    {
        try
        {
            JarFile jar = new JarFile(file);
            JarEntry jentry = jar.getJarEntry(imageName);
            InputStream is = jar.getInputStream(jentry);
            int avail = is.available();
            int zsize = (int) jentry.getSize();
            int size = avail > zsize ? avail : zsize;
            byte[] bytes = new byte[size];
            int inc = 0;
            while ((inc += is.read(bytes, inc, size - inc)) < size)
            {
                if (progressObserver != null)
                {
                    progressObserver.setProgress(inc * 100 / size);
                }
            }
            if (progressObserver != null)
            {
                progressObserver.setProgress(100);
            }

            Toolkit tk = Toolkit.getDefaultToolkit();
            Image im = tk.createImage(bytes);
            tk.prepareImage(im, -1, -1, this);

            while (!imageLoaded)
            {
                try
                {
                    wait();
                }
                catch (InterruptedException ex)
                {
                    Workspace.getLogger().warning("Cannot load image from jar file: "
                                           + ex.toString());
                }
            }

            return im;
        }
        catch (IOException ex)
        {
            Workspace.getLogger().warning("Cannot load image from jar file: "
                                   + ex.toString());
            return null;
        }
    }

    /**
     * Reads string from jar file.
     */
    public synchronized final String getJarString(String file, String imageName)
    {
        try
        {
            JarFile jar = new JarFile(file);
            JarEntry jentry = jar.getJarEntry(imageName);
            InputStream is = jar.getInputStream(jentry);
            int avail = is.available();
            int zsize = (int) jentry.getSize();
            int size = avail > zsize ? avail : zsize;
            byte[] bytes = new byte[size];
            int inc = 0;
            while ((inc += is.read(bytes, inc, size - inc)) < size)
            {
                if (progressObserver != null)
                {
                    progressObserver.setProgress(inc * 100 / size);
                }
//			Workspace.getLogger().info("Loaded " + new Integer(inc * 100 / size).toString());
            }
            if (progressObserver != null)
            {
                progressObserver.setProgress(100);
            }

            return new String(bytes);
        }
        catch (IOException ex)
        {
            Workspace.getLogger().warning("Cannot load string from jar file: "
                                   + ex.toString());
            return null;
        }
    }

    /**
     * Image tracker method. This is an internal method
     * and should not be called directly.
     */
    public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h)
    {
        if ((infoflags & (ALLBITS | FRAMEBITS)) != 0)
        {
            imageLoaded = true;
            notifyAll();
        }
        return (true);
    }
}