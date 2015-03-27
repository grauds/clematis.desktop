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

import org.w3c.dom.Element;

import java.util.Enumeration;

/**
 * This interface provides means to access the DTD definitions in a manner
 * that can be independent of whatever third party underlying structures
 * are used for the implementation.
 * <P>
 */

public interface IDTDDocument
{

    /**
     * Returns the list of declared elements from the document.
     * @return Enumeration consisting of DTDElement objects or null
     */
    public Enumeration getElements();

    /**
     * Returns the SYSTEM identifier for a dtd
     */
    public String getExternalID();
    /**
     * Returns a list of the possible elements that can be inserted or appended
     * on this element.
     *
     * @param el A DOM element
     * @return vector containing DTDElement objects
     */
    //	public Enumeration    getAppendableElements(Element el);

    public Enumeration getInsertableElements(Element el, int index);

    /**
     * Returns the name of the DTD
     */
    public String getName();
}
