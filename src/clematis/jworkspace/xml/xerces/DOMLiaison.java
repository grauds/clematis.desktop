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

import jworkspace.kernel.*;
import jworkspace.xml.*;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.*;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.xml.sax.*;

import java.io.*;
import java.util.*;

/**
 * Xerces DOM Liaison Implementation
 */
public class DOMLiaison implements ValidDOMLiaison
{
    protected List entityResolverList;

    /**
     * Error handling class for the validating parser
     */
    public class DefaultErrorHandler implements ErrorHandler
    {
        public DefaultErrorHandler()
        {
        }

        public void warning(SAXParseException exception) throws SAXException
        {
            Workspace.getLogger().info("Parser warning: " + exception.getMessage());
        }

        public void error(SAXParseException exception) throws SAXException
        {
            Workspace.getLogger().warning("Parser error: " + exception.getMessage());
        }

        public void fatalError(SAXParseException exception) throws SAXException
        {
            Workspace.getLogger().severe("Parser fatal error: " + exception.getMessage());
            throw exception;
        }
    }

    public class XercesResolver implements EntityResolver
    {
        ValidDocument vdoc = null;

        public XercesResolver(ValidDocument doc)
        {
            this.vdoc = doc;
        }

        /**
         * This entity resolver finds a dtd file on the filesystem if it can.
         * It does this by first checking the specified file (given as the
         * systemId paramter which comes from the SYSTEM specifier in the
         * XML &lt;!DOCTYPE&gt; definition. If the systemId isn't a full path or
         * url to a valid file, then the resolver tries to find the file using the
         * path.dtd resource from ResourceCatalog.
         *
         * @param publicId the public identifier for the entity
         * @param systemId the system identifier (usually a filename or url)
         * of the external entitiy.
         * @exception SAXException this is thrown by the DTD parser during DTD parsing.
         * @exception IOException FileNotFound is the typical IOException thrown in the
         * case that the external entity file can't be found. Other IOExceptions may be
         * thrown depending on the external entity file operations.
         */
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException
        {
            DTDCacheEntry dtdentry = null;
            // this section of code tries to find a dtd relative to another dtd
            // already loaded
            if (vdoc.getMainDTDDocument() != null)
            {
                Stack dtdStack = vdoc.getDTDStackCopy();
                while (!dtdStack.empty())
                {
                    DTDCacheEntry entry = (DTDCacheEntry) dtdStack.pop();
                    String parent = entry.getFilePath();
                    if (parent != null)
                    {
                        dtdentry = DTDCache.getSharedInstance().
                                findDTD(publicId, systemId, parent);
                        if (dtdentry != null)
                        {
                            break;
                        }
                    }
                }
            }
            if (dtdentry == null)
            {
                dtdentry =
                        DTDCache.getSharedInstance().findDTD(
                                publicId,
                                systemId,
                                vdoc.getFileLocation());
            }
            if (dtdentry != null)
            {
                vdoc.addDTD(dtdentry, systemId);
                char[] chars = dtdentry.getCachedDTDStream();
                CharArrayReader r = new CharArrayReader(chars);
                return new InputSource(r);
            }
            else if (entityResolverList != null)
            {
                // try other entity resolvers
                Iterator it = entityResolverList.iterator();
                while (it.hasNext() && dtdentry == null)
                {
                    Object o = it.next();
                    if (o instanceof EntityResolver)
                    {
                        EntityResolver er = (EntityResolver) o;
                        dtdentry = DTDCache.getSharedInstance().resolveDTD(
                                publicId,
                                systemId,
                                er,
                                vdoc.getFileLocation());
                    }
                }
                if (dtdentry != null)
                {
                    CharArrayReader car = new CharArrayReader(dtdentry.
                                                              getCachedDTDStream());
                    vdoc.addDTD(dtdentry, systemId);
                    return new InputSource(car);
                }
            }
            return null;
        }
    }

    /**
     * Extra entity resolvers to use to find a dtd. This allows the app
     * to provide it's own. For example, if the app wants to present the user
     * with a dialog to allow them to find the DTD.
     */
    public void addEntityResolver(EntityResolver er)
    {
        if (entityResolverList == null)
        {
            entityResolverList = new ArrayList();
        }
        entityResolverList.add(er);
    }

    /**
     * Create a Document
     * @return An empty Document
     */
    public Document createDocument()
    {
        return new DocumentImpl();
    }

    public ValidDocument createValidDocument()
    {
        ValidDocument vdoc = new ValidDocument(createDocument());
        return vdoc;
    }

    /**
     * Parse a stream of XML into a Document
     *
     * @return The Document that was parsed
     * @exception DOMLiaisonImplException
     * Wrapper exception that is thrown if the implementing class
     * throws any kind of exception.
     * @deprecated Use parseXMLStream(Reader)
     */
    public Document parseXMLStream(InputStream is)
            throws DOMLiaisonImplException
    {
        return parseXMLStream(new InputSource(is));
    }

    public Document parseXMLStream(Reader in)
            throws DOMLiaisonImplException
    {
        return parseXMLStream(new InputSource(in));
    }

    private Document parseXMLStream(InputSource is)
            throws DOMLiaisonImplException
    {
        DOMParser parser;
        parser = new DOMParser();

        try
        {
            parser.parse(is);
        }
        catch (SAXException e)
        {
            throw new DOMLiaisonImplException(e);
        }
        catch (IOException e)
        {
            throw new DOMLiaisonImplException(e);
        }
        return parser.getDocument();
    }

    /**
     * Parses an input stream containing XML using a validating parser.
     * Returns a ValidDocument which gives access to DTD information
     * and stuff.
     */
    public ValidDocument parseValidXMLStream(InputStream is, String fileLocation)
            throws DOMLiaisonImplException
    {
        try
        {
            DOMParser parser = null;
            ValidDocument doc = new ValidDocument();
            doc.setFileLocation(fileLocation);
            // create a validating DOMParser
            InputSource input = new InputSource(is);

            parser = new DOMParser();
            parser.setErrorHandler(new DefaultErrorHandler());
            parser.setEntityResolver(new XercesResolver(doc));
            parser.setFeature("http://xml.org/sax/features/external-general-entities",
                              false);
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities",
                              false);
            parser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes",
                              false);
            parser.parse(input);
            doc.setEncoding(input.getEncoding());

            Document xdoc = parser.getDocument();

            DocumentType doctype = xdoc.getDoctype();
            Node node = doctype.getNextSibling();
            while (node.getNodeType() == Node.COMMENT_NODE)
            {
                Node nextNode = node.getNextSibling();
                xdoc.removeChild(node);
                node = nextNode;
            }
            doc.setDocument(xdoc);
            return doc;
        }
        catch (Exception ex)
        {
            int linenumber;
            int colnumber;
            String appendMsg = null;
            if (ex instanceof SAXParseException)
            {
                linenumber = ((SAXParseException) ex).getLineNumber();
                colnumber = ((SAXParseException) ex).getColumnNumber();
                appendMsg = " on line " + linenumber + ", column " + colnumber;
                throw new DOMLiaisonImplException(ex, appendMsg);
            }
            else if (ex instanceof SAXException)
            {
                Exception inex = ((SAXException) ex).getException();
                if (inex != null)
                {
                    throw new DOMLiaisonImplException(inex);
                }
                else
                {
                    throw new DOMLiaisonImplException(ex);
                }
            }
            else
            {
                throw new DOMLiaisonImplException(ex);
            }
        }
    }

    public void print(ValidDocument doc, Writer output,  boolean format)
            throws DOMLiaisonImplException
    {
        Document d = doc.getDocument();
        printImpl(d, output, format);
    }

    /**
     * Print a Document
     *
     * @param doc The Document to print
     * @param output Writer to send the output to
     * @param resultns Result name space for the output.  Used for things
     * like HTML hacks.
     * @param format If true, output will be nicely tab-formatted.
     * If false, there shouldn't be any line breaks or tabs between
     * elements in the output.  Sometimes setting this to false
     * is necessary to get your HTML to work right.
     * @exception DOMLiaisonImplException
     * Wrapper exception that is thrown if the implementing class
     * throws any kind of exception.
     */
    public void print(Document doc, Writer output, String resultns, boolean format)
            throws DOMLiaisonImplException
    {
        printImpl(doc, output, format);
    }

    private void printImpl(Document doc, Writer output, boolean format)
            throws DOMLiaisonImplException
    {
        String enc = "UTF8";

        try
        {
            PrintWriter pw = new PrintWriter(output);
            pw.print("<?xml version=\"1.0\"");
            if (output instanceof OutputStreamWriter)
            {
                enc = ((OutputStreamWriter) output).getEncoding();
            }
            pw.println("?>");
            SerializerFactory sfact = SerializerFactory.getSerializerFactory(Method.XML);

            OutputFormat outformat = new OutputFormat(doc, enc, format);
            outformat.setOmitXMLDeclaration(true);
            outformat.setPreserveSpace(false);
            outformat.setOmitXMLDeclaration(true);

            Serializer serializer = sfact.makeSerializer(pw, outformat);
            if (serializer instanceof DOMSerializer)
            {
                ((DOMSerializer) serializer).serialize(doc);
            }
        }
        catch (IOException ex)
        {
            throw new DOMLiaisonImplException(ex);
        }
    }

    public void setProperties(Properties props)
    {
    }
}
