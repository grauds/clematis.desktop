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
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
 */

import java.io.*;

/**
 * This class implements a container for transmitting compressed byte
 * arrays and their descriptors around a network using
 * the Java serialization mechanism for marshalling and unmarshalling.
 */
public class CompressedDocumentPackage
        implements Serializable
{
    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -1464383390635540404L;

    private int uncompressedLength;
    private int compressedLength;
    private byte[] compressedContents = null;
    private java.lang.String path = null;
    private java.lang.String name = null;

    /**
     * CompressedDocumentPackage constructor comment.
     */
    public CompressedDocumentPackage()
    {
        super();
    }

    /**
     * Gets compressed contents of this package
     */
    public byte[] getCompressedContents()
    {
        return compressedContents;
    }

    /**
     * Gets length of compressed contents
     */
    public int getCompressedLength()
    {
        return compressedLength;
    }

    /**
     * Gets name of file for this package
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets path of file for this package
     */
    public java.lang.String getPath()
    {
        return path;
    }

    /**
     * Get initial (uncompressed) length od data
     */
    public int getUncompressedLength()
    {
        return uncompressedLength;
    }

    /**
     * Copies compressed contents into our message.
     */
    public void setCompressedContents(byte[] compressedContents)
    {
        this.compressedContents = new byte[compressedContents.length];
        System.arraycopy(compressedContents, 0, this.compressedContents,
                         0, compressedContents.length);
        ;
    }

    /**
     * Sets compressed length for this message
     */
    public void setCompressedLength(int compressedLength)
    {
        this.compressedLength = compressedLength;
    }

    /**
     * Sets name for the contents of message
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets path of the file which is contents for this package
     */
    public void setPath(String path)
    {
        this.path = path;
    }

    /**
     * Sets uncompressed length of data contents
     */
    public void setUncompressedLength(int uncompressedLength)
    {
        this.uncompressedLength = uncompressedLength;
    }
}
