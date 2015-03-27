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

import java.io.InputStream;
import java.io.Writer;

/**
 * This interface provides means to access the DTD
 * definitions as well as the DOM document
 * <P>
 */
public interface ValidDOMLiaison extends IDOMLiaison
{
    /**
     * Creates a new document that should maintain validity.
     * not really used or implemented yet
     */
    public ValidDocument createValidDocument();

    /**
     * This parses an XML stream using a validating parser
     * and maintains references to the
     * DTDDocuments used in it. It returns a ValidatedDocument which contains
     * a org.w3c.dom.Document and the DTDDocuments it uses.
     * <P>
     * Uses the default EntityResolver for resolving the DTD documents
     * <P>
     * @param is InputStream to parse
     * @param fileLocation optional URL for the file used to find relative DTD's
     *
     * @return com.channelpoint.commerce.util.xml.ValidatedDocument
     * @exception DOMLiaisonImplException wrapper for exceptions thrown
     * by the validating parser.
     */
    public ValidDocument parseValidXMLStream(InputStream is, String fileLocation)
            throws DOMLiaisonImplException;

    /**
     * Print that takes a valid document so it can print out the DTD
     * specification properly.
     */
    public void print(ValidDocument doc, Writer output, boolean pretty)
            throws DOMLiaisonImplException;
}
