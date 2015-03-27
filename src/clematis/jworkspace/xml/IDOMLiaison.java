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
import org.xml.sax.EntityResolver;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 * DOM Liaison
 *
 * This interface makes up for deficiencies in the DOM API.
 * It allows to plug in different XML libraries by creating
 * implementations of this interface.
 *
 */
public interface IDOMLiaison
{

    public void addEntityResolver(EntityResolver er);

    /**
     * Create a Document
     * @return An empty Document
     */
    public Document createDocument();

    /**
     * Parse a stream of XML into a Document
     *
     * @return The Document that was parsed
     * @deprecated Use parseXMLStream(Reader)
     */
    public Document parseXMLStream(InputStream is)
            throws DOMLiaisonImplException;

    public Document parseXMLStream(Reader in)
            throws DOMLiaisonImplException;

    /**
     * Print a Document
     *
     * @param doc The Document to print
     * @param output Writer to send the output to
     * @param resultns Result name space for the output.  Used for things
     *       	like HTML hacks.
     * @param format If true, output will be nicely tab-formatted.
     *       	If false, there shouldn't be any line breaks or tabs between
     *       	elements in the output.  Sometimes setting this to false
     *       	is necessary to get your HTML to work right.
     */
    public void print(Document doc, Writer output, String resultns, boolean format)
            throws DOMLiaisonImplException;

    public void setProperties(Properties props);
}
