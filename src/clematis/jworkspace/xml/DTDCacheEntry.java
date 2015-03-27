package jworkspace.xml;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * This contains information we need to keep with a
 * dtd that has been cached.
 */
public class DTDCacheEntry implements Comparable
{
    /**
     * The public id
     */
    protected String publicId = null;
    /**
     * Optional system id
     */
    protected String systemId = null;
    /**
     * Root element for this particular entry
     */
    protected String rootElement = null;
    /**
     * Path to the file containing the DTD... can be a system path, a url,
     * or a path into a jar, including a ! if the file
     */
    protected String filePath = null;
    /**
     * last modification time of the file the dtd was loaded from. if this is 0,
     * then we cache the file indefinitely, and never check back with the source
     */
    protected long timestamp = 0;
    /**
     * cached char array of the dtd stream
     */
    protected char[] cachedDTDStream;
    /**
     * a parsed version of the dtd
     */
    protected IDTDDocument parsedDTD = null;

    // this could be a weak ref if we want to reduce
    // some memory overhead
    public DTDCacheEntry(String publicId, String systemId)
    {
        this.publicId = publicId;
        this.systemId = systemId;
    }

    public int compareTo(Object o)
            throws ClassCastException
    {
        int rtn = 0;
        if (o instanceof DTDCacheEntry)
        {
            DTDCacheEntry entry = (DTDCacheEntry) o;
            if (publicId != null && entry != null && entry.publicId != null)
            {
                rtn = publicId.compareTo(entry.publicId);
            }
            if (rtn == 0 && systemId != null && entry != null &&
                    entry.systemId != null)
            {
                rtn = systemId.compareTo(entry.systemId);
            }
            return rtn;
        }
        else
        {
            throw new ClassCastException();
        }
    }

    public char[] getCachedDTDStream()
    {
        return cachedDTDStream;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public IDTDDocument getParsedDTD()
    {
        return parsedDTD;
    }

    public List getPossibleRootNames()
    {
        Enumeration e = parsedDTD.getElements();
        ArrayList list = new ArrayList();
        while (e.hasMoreElements())
        {
            IDTDElement el = (IDTDElement) e.nextElement();
            String s = el.getName();
            list.add(s);
        }
        return list;
    }

    public String getPublicId()
    {
        return publicId;
    }

    public String getSystemId()
    {
        return systemId;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setCachedDTDStream(char[] s)
    {
        cachedDTDStream = s;
    }

    public void setFilePath(String s)
    {
        filePath = s;
    }

    public void setParsedDTD(IDTDDocument parsedDTD)
    {
        this.parsedDTD = parsedDTD;
    }

    public void setPublicId(String s)
    {
        publicId = s;
    }

    public void setSystemId(String s)
    {
        systemId = s;
    }

    public void setTimestamp(long t)
    {
        timestamp = t;
    }

    public String toString()
    {
        return "DTDCacheEntry(public="
                + publicId + " system=" + systemId + " file=" + filePath + " cachedDTDStream is "
                + (cachedDTDStream != null ? "non-" : "") + "null)";
    }
}
