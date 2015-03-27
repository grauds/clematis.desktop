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

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDAttlist;
import com.wutka.dtd.DTDAttribute;
import com.wutka.dtd.DTDElement;
import jworkspace.xml.IDTDAttribute;
import jworkspace.xml.IDTDElement;

import java.util.Enumeration;
import java.util.Vector;

/**
 *  A DTDDocument based on the wutka package
 */
public class DTDElementImpl implements IDTDElement, Comparable
{
    private DTD doc = null;
    private String name = null;
    private Vector attrs = null;

    public DTDElementImpl(DTD doc, DTDElement decl)
    {
        this.doc = doc;
        name = decl.getName();
    }

    public DTDElementImpl(DTD doc, String name)
    {
        this.doc = doc;
        this.name = name;
    }

    /**
     * Compare by names
     */
    public int compareTo(Object o)
    {
        if (o instanceof DTDElementImpl)
        {
            return (getName().compareTo(((DTDElementImpl) o).getName()));
        }
        else
        {
            throw new ClassCastException();
        }
    }

    /**
     * Return attribute from this declaration
     */
    public IDTDAttribute getAttribute(String name)
    {
        DTDElement currElem = (DTDElement) doc.elements.get(name);
        if (currElem != null)
        {
            DTDAttribute attr = currElem.getAttribute(name);
            if (attr != null)
            {
                return new DTDAttributeImpl(attr);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public Enumeration getAttributes()
    {
        // lazily init the attributes
        if (attrs == null)
        {
            /**
             * Find all attribute declarations for the current document
             * First sort out all attribute lists
             */
            Vector vec = doc.getItemsByType(DTDAttlist.class);
            if (vec != null)
            {
                attrs = new Vector();
                Enumeration en = vec.elements();
                while (en.hasMoreElements())
                {
                    /**
                     * Find attribute list for the current
                     * element.
                     */
                    DTDAttlist attlist = (DTDAttlist) en.nextElement();
                    if (attlist.getName().equals(this.getName()))
                    {
                        /**
                         * Found attribute list, get all attributes and
                         * return a set of attributes implementations
                         */
                        for (int i = 0; i < attlist.getAttribute().length; i++)
                        {
                            DTDAttribute attribute = attlist.getAttribute(i);
                            attrs.addElement(new DTDAttributeImpl(attribute));
                        }
                    }
                }
            }
            else
            {
                return null;
            }
        }
        if (attrs.size() == 0)
        {
            return null;
        }
        return attrs.elements();
    }

    public String getName()
    {
        return name;
    }
}
