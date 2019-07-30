package jworkspace.network.datagram;

/* ----------------------------------------------------------------------------
   Clematis Collaboration Network 1.0.3
   Copyright (C) 2001-2003 Anton Troshin
   This file is part of Java Workspace Collaboration Network.
   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
   Authors may be contacted at:
  larsyde@diku.dk
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
 */

import java.io.*;
import java.util.*;

/**
 * This class implements a container used for transmitting
 * data between clients.
 */
public class DataPackage implements Serializable
{
  /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -1464383390635540403L;

    public final static String AUTHOR_NAME = "Author name";
    public final static String DOCPATH = "Document name and path";
    public final static String DOCNAME =
            "Document name (without path)";
    public final static String DESCRIPTION = "Description";
    public Properties properties = new java.util.Properties();
    public Object contents = null;
    public final static String USERGROUP_ID = "Usergroup ID";
    public final static String MESSAGE_ID = "Message ID";
    public final static String CLIENT_NAME = "Client name";

    /**
     * DataPackage constructor comment.
     */
    public DataPackage()
    {
        super();
    }

    /**
     * Get contents of data message. This is actually a serialized
     * object. The type of this object is assumed to be known
     * for each partisipats of communication.
     */
    public Object getContents()
    {
        return contents;
    }

    /**
     * Get generic message properties
     */
    public Properties getProperties()
    {
        return properties;
    }

    /**
     * Sets contents of this message
     */
    public void setContents(Object contents)
    {
        this.contents = contents;
    }

    /**
     * Sets this message properties
     */
    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }
}
