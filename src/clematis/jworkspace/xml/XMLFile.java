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

import jworkspace.kernel.XLiaisonFactory;
import jworkspace.util.WorkspaceException;
import jworkspace.util.WorkspaceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.Enumeration;

/**
 * An XML file. This provides an interface into a particular XML file,
 * including its dtd and its file location. It provides methods for
 * loading and parsing a file, saving a file, and accessing the content
 * model in the dtd.
 */
public class XMLFile

{
    /**
     * The parsed DOM document with validation
     */
    protected ValidDocument doc = null;
    /**
     * The document type (dtd)
     */
    protected DocumentType docType = null;
    /**
     * The file on the filesystem
     */
    protected File file = null;
    /**
     * Status holder for marking the file as needing a save
     */
    protected boolean dirty = false;
    /**
     * Status marker for brand new files so we can call saveas instead of save
     */
    protected boolean _new = false;
    /**
     * property change delegate
     */
    protected PropertyChangeSupport propchange;

    /**
     * Creates a new file with a blank Document tree
     */
    public XMLFile() throws WorkspaceException
    {
        propchange = new PropertyChangeSupport(this);
        doc = XLiaisonFactory.getDOMLiaison().createValidDocument();
    }

    /**
     * Reads in the given filename to create the Document tree
     */

    public XMLFile(File f) throws WorkspaceException
    {
        file = f;
        propchange = new PropertyChangeSupport(this);
        // now parse the file and get a Document
        parseDocument();
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        propchange.addPropertyChangeListener(l);
    }

    public void firePropertyChange(String s, boolean ov, boolean nv)
    {
        propchange.firePropertyChange(s, ov, nv);
    }

    public DocumentType getDoctype()
    {
        return docType;
    }

    /**
     * Returns the DOM document for this file
     */
    public Document getDocument()
    {
        return doc.getDocument();
    }

    /**
     * Returns the main DTDDocument for this file
     */
    public IDTDDocument getDTD(String name)
    {
        return doc.getDTDDocument(name);
    }

    public Enumeration getDTDAttributes(String elementName)
    {
        return doc.getDTDAttributes(elementName);
    }

    public Enumeration getInsertableElements(Element el, int index)
    {
        IDTDDocument tdoc = doc.getDTDForElement(el);
        if (tdoc != null)
        {
            Enumeration e = tdoc.getInsertableElements(el, index);
            return e;
        }
        return null;
    }

    public String getName()
    {
        return file.getName();
    }

    public String getPath()
    {
        return file.getPath();
    }

    /**
     * Returns the DOMLiaison ValidDocument wrapper for this file
     */
    public ValidDocument getValidDocument()
    {
        return doc;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    /**
     * returns the new property
     */
    public boolean isNew()
    {
        return _new;
    }

    protected void parseDocument() throws WorkspaceException
    {
        try
        {
            InputStream fis = WorkspaceUtils.getInputStream(file, this.getClass());
            // get a DOMLiaison from the settings and parse the given file
            ValidDOMLiaison domlia = XLiaisonFactory.getDOMLiaison();

            if (domlia != null)
            {
                doc = domlia.parseValidXMLStream(fis, file.getCanonicalPath());
            }
            if (doc != null || doc.getDocument() == null)
            {
                docType = doc.getDocument().getDoctype();
            }
            else
            {
                throw new WorkspaceException("xml.file.open.nodocument");
            }
        }
        catch (FileNotFoundException fnf)
        {
            throw new WorkspaceException("File not found: " + file, fnf);
        }
        catch (IOException ioex)
        {
            throw new WorkspaceException("IOException: " + file, ioex);
        }
        catch (DOMLiaisonImplException dle)
        {
            throw new WorkspaceException("Parse error: " + dle.getMessage(), dle);
        }
    }

    public void printRawXML(OutputStream s, boolean pretty) throws WorkspaceException
    {
        try
        {
            Writer w;
            String encoding = doc.getEncoding();
            if (encoding != null)
            {
                w = new OutputStreamWriter(s, encoding);
            }
            else
            {
                w = new OutputStreamWriter(s);
            }
            XLiaisonFactory.getDOMLiaison().print(doc, w, pretty);
        }
        catch (Exception ex)
        {
            throw new WorkspaceException("Cannot write to file: " + ex.toString());
        }
    }

    /**
     * Saves in the same file we opened
     */
    public void save() throws WorkspaceException
    {
        saveAs(file);
    }

    /**
     * Saves to a new file, leaving bak file near
     */
    public void saveAs(File f) throws WorkspaceException
    {
        try
        {
            // keep a backup of the original file incase the saveAs fails
            File tmpFile = new File(f.getAbsolutePath() + ".tmpsave");

            //			_file = f;
            OutputStream s = new FileOutputStream(tmpFile);
            printRawXML(s, true);
            s.close();

            // if it didn't work an exception will be thrown and we won't get here
            // now replace the old file with the tmp one
            File backup = new File(file.getAbsolutePath() + ".bak");
            // if the backup already exists... remove it
            if (backup.exists())
            {
                backup.delete();
            }
            if (!_new) WorkspaceUtils.copyFile(file, backup);
            WorkspaceUtils.copyFile(tmpFile, f);
            tmpFile.delete();

            file = f;

            setDirty(false);
            setNew(false);
        }
        catch (IOException ex)
        {
            throw new WorkspaceException("IOException while saving file: " + ex.getMessage(), ex);
        }
    }

    public void setDirty(boolean tf)
    {
        boolean old = dirty;
        dirty = tf;
        firePropertyChange("dirty", old, tf);
    }

    /**
     * Sets the new property
     */
    public void setNew(boolean tf)
    {
        _new = tf;
    }
}
