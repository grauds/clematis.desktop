package jworkspace.xml.xerces;

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

import com.wutka.dtd.*;
import jworkspace.xml.IDTDConstants;
import jworkspace.xml.IDTDDocument;
import jworkspace.xml.IDTDElement;
import org.w3c.dom.Element;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *  A DTDDocument based on the Xerces package
 */
public class DTDDocumentImpl implements IDTDDocument
{
    private DTD doc = null;
    private Hashtable elements = null;
    private boolean initialized = false;

    private String publicId;
    private String systemId;

    public DTDDocumentImpl(DTD doc,
                           String publicId, String systemId)
    {
        this.doc = doc;
        this.publicId = publicId;
        this.systemId = systemId;
    }

    protected IDTDElement fetchElement(String name)
    {
        if (elements != null)
        {
            IDTDElement el = (IDTDElement) elements.get(name);
            return el;
        }
        return null;
    }

    public Enumeration getElements()
    {
        // lazy init the element list
        if (!initialized && elements == null && doc != null)
        {
            Enumeration e = doc.elements.elements();
            initialized = true;
            if (e != null)
            {
                elements = new Hashtable();
                /**
                 * Add all DTDElements to our enumeration
                 */
                while (e.hasMoreElements())
                {
                    Object obj = e.nextElement();
                    if (obj instanceof DTDElement)
                    {
                        IDTDElement el = new DTDElementImpl(doc,
                                                            (DTDElement) obj);
                        elements.put(el.getName(), el);
                    }
                }
            }
        }
        if (elements != null)
        {
            return elements.elements();
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the external identifier or null if there is none.
     * <P>
     * The string should include PUBLIC and SYSTEM identifiers if they
     * are available.
     */
    public String getExternalID()
    {
        StringBuffer sb = new StringBuffer();
        boolean added_public = false;

        if (publicId != null && !publicId.equals(""))
        {
            sb.append("PUBLIC \"" + publicId + "\"");
            added_public = true;
        }
        if (systemId == null)
        {
            systemId = "";
        }

        if (!added_public)
        {
            sb.append("SYSTEM");
        }
        sb.append(" \"" + systemId + "\"");

        if (sb.length() > 0)
        {
            return sb.toString();
        }

        return null;
    }

    public Enumeration getInsertableElements(Element el, int index)
    {
        Vector v = new Vector();
        String tag_name = el.getTagName();
        /**
         * Query dtd document for the element named [tag_name]
         */
        DTDElement base = null;
        v = doc.getItemsByType(DTDElement.class);
        for (int i = 0; i < v.size(); i++)
        {
            if (((DTDElement) v.elementAt(i)).getName().equals(tag_name))
            {
                base = (DTDElement) v.elementAt(i);
                break;
            }
        }
        /**
         * Havent found - return empty set
         */
        if (base == null) return new Vector().elements();
        /**
         * Now found all insertables - such are in items list
         */
        DTDItem content = base.getContent();
        Vector res = new Vector();
        findAllInsertables(res, content);
        return res.elements();
    }

    /**
     * Find all insertables
     */
    protected void findAllInsertables(Vector res, DTDItem content)
    {
        DTDItem[] insertable = new DTDItem[]{};

        if (content instanceof DTDMixed)
        {
            DTDMixed mixed = (DTDMixed) content;
            /**
             * Get all insertable items
             */
            insertable = mixed.getItems();
        }
        else if (content instanceof DTDChoice)
        {
            DTDChoice choice = (DTDChoice) content;
            insertable = choice.getItems();
        }
        else if (content instanceof DTDSequence)
        {
            DTDSequence seq = (DTDSequence) content;
            insertable = seq.getItems();
        }
        /**
         * Now if we parsed one level children, try
         * to fill resulting vector
         */
        if (insertable != null)
        {
            for (int i = 0; i < insertable.length; i++)
            {
                IDTDElement e = null;
                if (insertable[i] instanceof DTDName)
                {
                    e = this.fetchElement(((DTDName) insertable[i]).value);
                    if (e != null)
                    {
                        res.addElement(e);
                    }
                }
                else if (insertable[i] instanceof DTDPCData)
                {
                    res.addElement(new DTDElementImpl(doc,
                                                      IDTDConstants.PCDATA_KEY));
                }
                else
                {
                    findAllInsertables(res, insertable[i]);
                }
            }
        }
    }

    public String getName()
    {
        return publicId;//doc.getName();
    }
}
