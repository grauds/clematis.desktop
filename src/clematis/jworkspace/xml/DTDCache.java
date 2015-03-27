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

import jworkspace.kernel.*;
import jworkspace.util.WorkspaceUtils;
import jworkspace.xml.xerces.DTDDocumentImpl;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * This singleton class is responsible for loading and caching
 * all DTD's required by the system.
 * <P>
 * Apps should use this class to retrieve all their DTD's for
 * valid documents (non-validating
 * apps usually ignore the DTD anyway, so they don't really need
 * to use this, but if they do
 * get a DTD, it might be a good idea to call into this class.
 * <P>
 * Here's an example of getting a dtd:<br>
 * <tt>DTDCacheEntry dtdentry = DTDCache.getSharedInstance().findDTD(publicId, systemId);</tt>
 * <br>
 * where <tt>publicId</tt> is the DOCTYPE's given public identifier (can be null), and <tt>systemId</tt>
 * is a system designator (file path or URL)
 * <P>
 * This can also cache DTD's from other entity resolvers via the
 * <A href="#resolveDTD">resolveDTD</a> method.
 */
public class DTDCache
{
    /**
     * key is the public id, val is a DTDCacheEntry
     */
    protected Map publicIdCache;
    /**
     * key is the system id, val is a DTDCacheEntry
     */
    protected Map systemIdCache;
    /**
     * key is the file path (including a ! and the path within a jar), val is a DTDCacheEntry
     */
    protected Map filepathCache;
    /**
     * List of unique dtd entries. key is the dtd entry, value is not used (null)
     */
    protected Map dtdEntries;
    /**
     * Properties for getting dtd path, etc. from
     */
    protected Properties properties;
    /**
     * singleton instance
     */
    protected static DTDCache instance;
    /**
     * synch object for creating the instance
     */
    protected static final Object synchronizer = new Object();

    /**
     * Empty constructor
     */
    protected DTDCache()
    {
        publicIdCache = new HashMap();
        systemIdCache = new HashMap();
        filepathCache = new HashMap();
        dtdEntries = new HashMap();
    }

    /**
     * Checks the timestamp associated with a cache entry and reloads the
     * dtd file if it has changed.
     */
    public void checkCacheEntryTimestamp(DTDCacheEntry entry)
    {
        boolean found = false;
        if (entry != null)
        {
            long timestamp = entry.getTimestamp();
            if (timestamp > 0)
            {
                File dtdFile = null;
                InputStream is = null;
                URL u = null;
                // first see if the systemId exists as-is
                // (is full path hard coded in the xml?)
                String path = entry.getFilePath();
                if (path != null)
                {
                    dtdFile = new File(path);
                    if (dtdFile.exists())
                    {
                        // we found it
                        found = true;
                        long newtime = dtdFile.lastModified();
                        if (newtime > timestamp)
                        {
                            entry.setTimestamp(newtime);
                            try
                            {
                                is = new FileInputStream(dtdFile);
                            }
                            catch (Exception ex)
                            {
                            }
                        }
                    }
                    // still don't have it?
                    if (!found && is == null)
                    {
                        // try it as a url
                        try
                        {
                            u = new URL(path);
                            URLConnection connection = u.openConnection();
                            long newtime = connection.getExpiration();
                            if (newtime > timestamp)
                            {
                                is = connection.getInputStream();
                                entry.setTimestamp(connection.
                                                   getExpiration());
                                // cache until the document expires
                                if (entry.getTimestamp() < System.currentTimeMillis())
                                {
                                    // ok, they don't want us to cache it... we will anyway.
                                    // use the modified time as the timestamp
                                    entry.setTimestamp(connection.getLastModified());
                                }
                            }
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                    try
                    {
                        if (is != null)
                        {
                            loadDTDIntoCache(is, entry);
                        }
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        }
    }

    /**
     * simple debugging print routine
     */
    protected void debug(String s)
    {
        if (System.getProperty("DEBUG") != null)
        {
            Workspace.getLogger().info("[debug] " + s);
        }
    }

    /**
     * find a DTD based on the public id and system id
     */
    public DTDCacheEntry findDTD(String pubid, String sysid, String fileLocation)
    {
        DTDCacheEntry ret = null;
        if (pubid != null)
        {
            ret = findDTDbyPublicId(pubid, sysid);
        }
        if (ret == null && sysid != null)
        {
            ret = findDTDbySystemId(pubid, sysid, fileLocation);
        }
        return ret;
    }

    /**
     * Looks in our cache for a file with a given public ID
     */
    public DTDCacheEntry findDTDbyPublicId(String publicId, String systemId)
    {
        DTDCacheEntry entry = (DTDCacheEntry) publicIdCache.get(publicId);
        if (entry != null)
        {
            checkCacheEntryTimestamp(entry);
            return entry;
        }
        return null;
    }

    /**
     * Finds a dtd given a system identifier. If it cannot be found, null
     * is returned
     */
    public DTDCacheEntry findDTDbySystemId(String publicId, String systemId,
                                           String fileLocation)
    {
        String filename = null;
        URL u = null;
        InputStream is = null;
        File dtdFile = null;
        String newSystemId = null;
        DTDCacheEntry entry = null;

        try
        {
            // see if it's cached
            entry = (DTDCacheEntry) systemIdCache.get(systemId);
            if (entry != null)
            {
                debug("found cached DTD: systemId=" + systemId);
                checkCacheEntryTimestamp(entry);
                return entry;
            }
            // first see if the systemId exists as-is (is full path hard coded in the xml?)
            dtdFile = new File(systemId);
            if (dtdFile.exists())
            {
                if (!systemId.startsWith("file:"))
                {
                    systemId = "file:" + systemId;
                }
                entry = new DTDCacheEntry(publicId, systemId);
                entry.setTimestamp(dtdFile.lastModified());
                entry.setFilePath(dtdFile.getCanonicalPath());
                is = new FileInputStream(dtdFile);
            }
            // still don't have it?
            if (entry == null)
            {
                // try it as a url
                try
                {
                    u = new URL(systemId);
                    URLConnection connection = u.openConnection();
                    is = connection.getInputStream();
                    entry = new DTDCacheEntry(publicId, systemId);
                    entry.setFilePath(u.toString());
                    entry.setTimestamp(connection.getExpiration());
                    // cache until the document expires
                    if (entry.getTimestamp() < System.currentTimeMillis())
                    {
                        // ok, they don't want us to cache it... we will anyway.
                        // use the modified time as the timestamp
                        entry.setTimestamp(connection.getLastModified());
                    }
                }
                catch (Exception ex)
                {
                }
            }
            // check if it's relative to the file we're opening
            if (entry == null && fileLocation != null)
            {
                try
                {
                    File f = new File(fileLocation);
                    String parent = f.getParent();
                    if (parent != null)
                    {
                        dtdFile = new File(parent, systemId);
                        if (dtdFile.exists() && dtdFile.canRead())
                        {
                            entry = new DTDCacheEntry(publicId, systemId);
                            entry.setFilePath(dtdFile.getCanonicalPath());
                            entry.setTimestamp(dtdFile.lastModified());
                            is = new FileInputStream(dtdFile);
                        }
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            // now search the dtd path for the file
            if (entry == null)
            {
                String tempSysid = systemId;
                // strip off the file:/cwd if it's on there
                if (systemId.startsWith("file:"))
                {
                    filename = fixslashes(systemId.substring("file:".length()));
                    // here we are seeing if the XML parser got a relative URL from the
                    // DOCTYPE. the only way we can tell is to see if the current dir
                    // is the first part of the URL. if it is, we search our dtd path
                    // using the relative filepath which we assume is the filepath - cwd
                    String currentdir = fixslashes(System.getProperty("user.dir"));
                    if (filename.startsWith(currentdir))
                    {
                        filename = filename.substring(currentdir.length());
                        if (filename.charAt(0) == '/')
                        { // always use / cause it's a url
                            filename = filename.substring(1);
                            tempSysid = filename;
                            //	debug("tempSysid = "+tempSysid);
                        }
                    }
                }
                // hunt for the dtd files
                String pathsep = System.getProperty("path.separator");
                String filesep = System.getProperty("file.separator");
                String dtdpath = null;
                if (properties != null)
                {
                    dtdpath = properties.getProperty("path.dtd");
                }
                else
                {
                    debug("DTDCache: properties is null");
                    dtdpath = "";
                }
                if (dtdpath != null)
                {
                    StringTokenizer st = new StringTokenizer(dtdpath, pathsep);
                    String prepend;
                    while (st.hasMoreTokens())
                    {
                        prepend = st.nextToken();
                        // try the file
                        filename = prepend + "/" + tempSysid;
                        dtdFile = new File(filename);
                        if (dtdFile.exists())
                        {
                            entry = new DTDCacheEntry(publicId, systemId);
                            entry.setFilePath(dtdFile.getCanonicalPath());
                            entry.setTimestamp(dtdFile.lastModified());
                            is = new FileInputStream(dtdFile);
                            // modify the systemid to match that the file is in the dtdpath
                            newSystemId = tempSysid;
                            break;
                        }
                        else
                        {
                            try
                            {
                                // try getting it via the classloader?
                                is = WorkspaceUtils.getInputStream(dtdFile, this.getClass());
                                // ok? set the timestamp to never reload
                                entry = new DTDCacheEntry(publicId, systemId);
                                entry.setTimestamp(0);
                                entry.setFilePath(this.getClass().toString() + ":" + dtdFile);
                                newSystemId = tempSysid;
                                break;
                            }
                            catch (FileNotFoundException e)
                            {
                            }
                        }
                    }
                }
            }
            if (entry == null || is == null)
            {
                //	debug("DTD SYSTEMID='"+systemId+"' NOT FOUND");
                //	new Exception().printStackTrace();
                return null;
            }
            /**
             * XXX this is lame and uses the xml4j parser directly instead of
             * through the
             * DOMLiason... needs extrapolated and isulated a bit
             * more than likely, someone else (like an entity resolver)
             * wants to read this dtd stream also, so we might as
             * well cache the whole dtd into memory (lame yes, but nice if the
             * dtd came from a remote URL, or a jar/zip file.
             */

            loadDTDIntoCache(is, entry);

            if (newSystemId != null)
            {
                entry.setSystemId(newSystemId);
                systemIdCache.put(newSystemId, entry);
            }
            String filepath = entry.getFilePath();
            if (filepath != null)
            {
                filepathCache.put(filepath, entry);
            }
            return entry;
        }
        catch (IOException ex)
        {
            return null;
        }
    }

    /**
     * make all slashes forward slashes cause windows sucks
     */
    protected String fixslashes(String s)
    {
        StringBuffer sb = new StringBuffer(s);

        // strip off any leading slashes cause windows sucks and makes URL's like:
        // file:/C:/blah
        // when the cwd comes out like C:/blah
        if (sb.charAt(0) == '/' && sb.charAt(2) == ':')
        {
            sb.deleteCharAt(0);
        }

        for (int i = 0; i < sb.length(); i++)
        {
            if (sb.charAt(i) == '\\')
            {
                sb.setCharAt(i, '/');
            }
        }
        return sb.toString();
    }

    public Collection getCachedDTDEntries()
    {
        Set keys = dtdEntries.keySet();
        TreeSet sortedkeys = new TreeSet(keys);
        return sortedkeys;
    }

    /**
     * gets the singleton instance.
     */
    public static DTDCache getSharedInstance()
    {
        if (instance == null)
        {
            synchronized (synchronizer)
            {
                if (instance == null)
                {
                    instance = new DTDCache();
                }
            }
        }
        return instance;
    }

    /**
     * Loads a dtd into a DTDCacheEntry.
     */
    public void loadDTDIntoCache(InputStream is, DTDCacheEntry entry)
            throws IOException
    {
        Reader r = new InputStreamReader(is);
        loadDTDIntoCache(r, entry);
    }

    /**
     * Loads a dtd into a DTDCacheEntry. The public and system id's
     * should be set on the dtd entry.
     */
    public void loadDTDIntoCache(Reader r, DTDCacheEntry entry)
            throws IOException
    {
        BufferedReader br = new BufferedReader(r);
        CharArrayWriter caw = new CharArrayWriter();
        BufferedWriter bw = new BufferedWriter(caw);

        int c;
        while ((c = br.read()) >= 0)
        {
            bw.write(c);
        }
        bw.flush();
        bw.close();
        br.close();

        entry.setCachedDTDStream(caw.toCharArray());
        CharArrayReader car = new CharArrayReader(entry.getCachedDTDStream());

        // now parse the dtd for ourselves
        String filename = entry.getFilePath();
        if (filename == null)
        {
            filename = entry.getSystemId();
        }
        String errorPrefix = filename;
        if (errorPrefix == null || errorPrefix.trim().equals(""))
        {
            errorPrefix = "error";
        }
        /**
         * THIS IS A DTD PARSER IMPLEMENTATION
         */
        com.wutka.dtd.DTDParser p =
                new com.wutka.dtd.DTDParser(new URL(filename));
        com.wutka.dtd.DTD dtd = p.parse();

        String publicId = entry.getPublicId();
        String systemId = entry.getSystemId();

        DTDDocumentImpl dtdimpl =
                new DTDDocumentImpl(dtd, publicId, systemId);

        entry.setParsedDTD(dtdimpl);
        car.close();

        if (systemId != null && !systemId.trim().equals(""))
        {
            systemIdCache.put(systemId, entry);
            debug("added " + systemId + " to cache [2]");
        }
        if (publicId != null && !publicId.trim().equals(""))
        {
            publicIdCache.put(publicId, entry);
        }
        dtdEntries.put(entry, null);
    }

    public void printCache()
    {
        Set s = publicIdCache.keySet();
        Iterator it = s.iterator();
        debug("PUBLIC Id's:\n");

        while (it.hasNext())
        {
            debug(it.next() + "\n");
        }
        s = systemIdCache.keySet();
        it = s.iterator();
        debug("\nSYSTEM Id's:\n");

        while (it.hasNext())
        {
            debug(it.next() + "\n");
        }
        debug("\n");
    }

    /**
     * resolve a dtd from another resolver. This way we can cache it locally.
     */
    public DTDCacheEntry resolveDTD(String publicId, String systemId, EntityResolver resolver, String fileLocation)
            throws SAXException, IOException
    {
        debug("Resolve DTD: " + systemId);

        InputSource is = resolver.resolveEntity(publicId, systemId);
        if (is != null)
        {
            String newPublicId = is.getPublicId();
            String newSystemId = is.getSystemId();
            if (newPublicId != null)
            {
                publicId = newPublicId;
            }
            if (newSystemId != null)
            {
                systemId = newSystemId;
            }

            // check to see if the resovler put one in our cache somehow
            if (publicIdCache.containsKey(publicId))
            {
                DTDCacheEntry entry = (DTDCacheEntry) publicIdCache.get(publicId);
                checkCacheEntryTimestamp(entry);
                return entry;
            }

            // create a new DtdEntry for it
            DTDCacheEntry entry = new DTDCacheEntry(publicId, systemId);

            InputStream stream = is.getByteStream();
            Reader charstream = is.getCharacterStream();
            if (charstream == null && stream != null)
            {
                loadDTDIntoCache(stream, entry);
            }
            else if (charstream != null)
            {
                loadDTDIntoCache(charstream, entry);
            }
            else
            {
                return null;
            }
            return entry;
        }
        return null;
    }

    /**
     * set the properties. should really only be called once by some app initializer
     */
    public void setProperties(Properties props)
    {
        properties = props;
    }
}
