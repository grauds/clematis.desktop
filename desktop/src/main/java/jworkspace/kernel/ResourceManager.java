package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

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

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;

import com.hyperrealm.kiwi.util.ResourceNotFoundException;
import jworkspace.WorkspaceResourceAnchor;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Special resource manager that takes advantage of Apache Commons image loading library.
 */
public class ResourceManager extends com.hyperrealm.kiwi.util.ResourceManager {

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(ResourceManager.class);
    /**
     * Cache for images
     */
    private HashMap<String, Image> images = new HashMap<String, Image>();

    /**
     * Empty default constructor
     */
    public ResourceManager() {
        super(WorkspaceResourceAnchor.class);
    }

    /**
     * Image loader with advanced capabilities
     *
     * @param name of resource
     * @return loaded image
     */
    public Image getImage(String name) {

        Image image = images.get(name);

        if (image != null) {
            return (image);
        }

        try {

            image = super.getImage(name);
        } catch (ResourceNotFoundException ex) {
            /*
             * May also occur if base class does
             * not support image format
             */
            LOG.warn("Can't find the image", ex);
        }

        try {

            image = Imaging.getBufferedImage(getClass().getResourceAsStream(imagePath + name));
        } catch (ImageReadException | IOException ex) {
            /*
             * May also occur if Apache Advanced Imaging does not support image format
             */
            LOG.warn("Can't load or read the image", ex);
        }

        if (image != null) {
            images.put(name, image);
        }
        return image;
    }
}
