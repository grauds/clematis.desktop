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

import java.util.Enumeration;

/**
 * This interface provides access to an attribute
 * <P>
 */
public interface IDTDAttribute extends IDTDConstants
{
    /**
     * Returns IMPLIED or REQUIRED, or NONE if nothing is specified
     */
    public int getDefaultType();

    /**
     * Returns the default value if none is specified as a String
     */
    public String getDefaultValue();

    /**
     * Returns the attribute name. i.e. &lt;!ATTLIST elname attname...
     * <P>
     * This would return "attname" in that case.
     */
    public String getName();

    /**
     * Returns an enumeration consisting of String objects
     * that are the tokens for this attribute definition
     */
    public Enumeration getTokens();

    /**
     * Returns the attribute type, either ANY, CDATA,
     * TOKEN_GROUP, NMTOKEN, NMTOKENS.
     * see DTDConstants for these definitions.
     */
    public int getType();
}
