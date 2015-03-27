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

import com.wutka.dtd.DTDAttribute;
import com.wutka.dtd.DTDDecl;
import com.wutka.dtd.DTDEnumeration;
import jworkspace.xml.IDTDAttribute;
import jworkspace.xml.IDTDConstants;

import java.util.Enumeration;

/**
 *  DTDAttribute
 */
public class DTDAttributeImpl implements IDTDAttribute, IDTDConstants
{
    private DTDAttribute attr = null;

    public DTDAttributeImpl(DTDAttribute attr)
    {
        this.attr = attr;
    }

    public int getDefaultType()
    {
        DTDDecl decl = this.attr.getDecl();
        int i = decl.type;

        if (i == DTDDecl.IMPLIED.type)
        {
            return IMPLIED;
        }
        else if (i == DTDDecl.REQUIRED.type)
        {
            return REQUIRED;
        }
        else if (i == DTDDecl.FIXED.type)
        {
            return FIXED;
        }
        else if (i == DTDDecl.VALUE.type)
        {
            return VALUE;
        }
        else
        {
            return NONE;
        }
    }

    public String getDefaultValue()
    {
        return attr.getDefaultValue();
    }

    public String getName()
    {
        return attr.getName();
    }

    public Enumeration getTokens()
    {
        Object type = attr.getType();
        if (type instanceof DTDEnumeration)
        {
            DTDEnumeration enu = (DTDEnumeration) type;
            return enu.getItemsVec().elements();
        }
/*  switch (t)
    {
      case TOKEN_GROUP:
        return attDef.elements();
      case NMTOKEN:
        v = new Vector();
        v.addElement(attDef.elementAt(0));
        return v.elements();
      case NMTOKENS:
        return attDef.elements();
    }*/
        return null;
    }

    /**
     * Return the type of this attribute
     */
    public int getType()
    {
        Object type = attr.getType();
        if (type instanceof String)
        {
            String str_type = (String) type;
            if (str_type.equals("CDATA"))
            {
                return CDATA;
            }
            else if (str_type.equals("ID"))
            {
                return ID;
            }
            else if (str_type.equals("IDREF"))
            {
                return IDREF;
            }
            else if (str_type.equals("NMTOKEN"))
            {
                return NMTOKEN;
            }
        }
        else if (type instanceof DTDEnumeration)
        {
            return TOKEN_GROUP;
            // return NMTOKENS;
        }
        return NONE;
    }
}
