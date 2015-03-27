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
 * This is an interface that will provide DTD information about an element
 * definition.
 * <P>
 *
 * <PRE>
 * <!ELEMENT   blah    (foo?, bar+, baz*)>
 * </PRE>
 *  <P>
 * In the above example, the element's name is "blah", and it has a
 * content spec with (foo?, bar+, baz*).
 *
 */

public interface IDTDElement
{
    /**
     * Returns a single named attribute for this element
     */
    public IDTDAttribute getAttribute(String name);

    /**
     * Returns the list of attributes
     * @return an enumeration consisting of DTDAttribute objects or null
     */
    public Enumeration getAttributes();

    /**
     * Returns the name of this element
     */
    public String getName();
}
