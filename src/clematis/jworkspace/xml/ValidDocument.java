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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

/**
 * Container for a validated Document and it's DTDDocuments
 */
public class ValidDocument
{
    private Document doc;
    private Hashtable dtds;
    private IDTDDocument maindtd = null;
    private String encoding = null;
    private DTDCacheEntry cachedDTD = null;
    /**
     * Location of the file used to find relative DTD's
     */
    private String fileLocation = null;
    /**
     * Hashtable of all the defined elements in the different DTD's
     */
    private Hashtable elements = null;
    /**
     * Reverse hashtable for looking up what document type a element
     * came from
     */
    private Hashtable element2DTD = null;
    /**
     * Stack for keeping track of multiple DTD's used by a document
     */
    private Stack dtdStack;


    public ValidDocument()
    {
        this.doc = null;
        this.dtds = new Hashtable();
        this.dtdStack = new Stack();
    }

    public ValidDocument(Document doc)
    {
        this.doc = doc;
        this.dtds = new Hashtable();
    }

    public void addDTD(DTDCacheEntry cachedDTD, String doctype)
    {
        if (this.maindtd == null)
        {
            this.maindtd = cachedDTD.getParsedDTD();
            this.cachedDTD = cachedDTD;
        }
        this.dtds.put(doctype, cachedDTD.getParsedDTD());
        this.dtdStack.push(cachedDTD);
    }

    public Document getDocument()
    {
        return this.doc;
    }

    public Enumeration getDTDAttributes(String elementName)
    {
        lazyInitElements();
        IDTDElement el = (IDTDElement) this.elements.get(elementName);
        if (el != null)
        {
            return el.getAttributes();
        }
        return null;
    }

    public DTDCacheEntry getDTDCacheEntry()
    {
        return this.cachedDTD;
    }

    public IDTDDocument getDTDDocument(String name)
    {
        return (IDTDDocument) this.dtds.get(name);
    }

    public IDTDDocument getDTDForElement(Element el)
    {
        lazyInitElements();
        Object o = this.element2DTD.get(el.getNodeName());
        if (o != null && o instanceof IDTDDocument)
        {
            return (IDTDDocument) o;
        }
        return null;
    }

    public Stack getDTDStackCopy()
    {
        return (Stack) this.dtdStack.clone();
    }

    public String getEncoding()
    {
        return this.encoding;
    }

    public String getFileLocation()
    {
        return this.fileLocation;
    }

    public IDTDDocument getMainDTDDocument()
    {
        return this.maindtd;
    }

    protected void lazyInitElements()
    {
        if (this.elements == null)
        {
            this.elements = new Hashtable();
            this.element2DTD = new Hashtable();

            Enumeration e = this.dtds.elements();
            while (e.hasMoreElements())
            {
                IDTDDocument doc = (IDTDDocument) e.nextElement();
                Enumeration els = doc.getElements();
                if (els != null)
                {
                    while (els.hasMoreElements())
                    {
                        IDTDElement el = (IDTDElement) els.nextElement();
                        this.elements.put(el.getName(), el);
                        this.element2DTD.put(el.getName(), doc);
                    }
                }
            }
        }
    }

    public void setDocument(Document doc)
    {
        this.doc = doc;
    }

    public void setEncoding(String enc)
    {
        this.encoding = enc;
    }

    public void setFileLocation(String fileLocation)
    {
        this.fileLocation = fileLocation;
    }
}
