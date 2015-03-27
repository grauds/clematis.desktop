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

import org.w3c.dom.*;

/**
 * DOM Utilities
 */
public class DOMUtil
{

    /**
     * Import a single node into a document.  The node may have come from an
     * external document.
     *
     * @param parent Node at which to insert the imported node
     * @param child Node to append to insertionPoint
     *
     */
    public static Node importNode(Node parent, Node child)
    {
        return importNode(parent, child, true);
    }

    /**
     * Import a single node into a document.  The node may have come from an
     * external document.
     *
     * @param parent    Node at which to insert the imported node
     * @param child     Node to append to insertionPoint
     * @param doappend  if true, this will go ahead and append the
     * node, otherwise, no action is done with the newly created node.
     */
    public static Node importNode(Node parent, Node child, boolean doappend)
    {

        Attr attr;
        int i;
        NamedNodeMap attributes;
        Node copy = null;
        NodeList children = null;
        String tagName = null;
        Document doc = parent.getOwnerDocument();

        try
        {
            children = child.getChildNodes();
        }
        catch (DOMException e)
        {
        }

        switch (child.getNodeType())
        {
            case Node.CDATA_SECTION_NODE:
                copy = doc.createCDATASection(((CDATASection) child).getData());
                break;
            case Node.COMMENT_NODE:
                copy = doc.createComment(((Comment) child).getData());
                break;
            case Node.DOCUMENT_FRAGMENT_NODE:
                copy = doc.createDocumentFragment();
                break;
            case Node.ELEMENT_NODE:
                tagName = ((Element) child).getTagName();
                copy = doc.createElement(tagName);
                if ((attributes = child.getAttributes()) != null)
                {
                    for (i = 0; i < attributes.getLength(); i++)
                    {
                        attr = (Attr) attributes.item(i);
                        ((Element) copy).setAttribute(attr.getName(), attr.getValue());
                    }
                }
                break;
            case Node.ENTITY_REFERENCE_NODE:
                copy = doc.createEntityReference(child.getNodeName());
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                copy = doc.createProcessingInstruction(((ProcessingInstruction) child).getTarget(),
                                                       ((ProcessingInstruction) child).getData());
                break;
            case Node.TEXT_NODE:
                copy = doc.createTextNode(((Text) child).getData());
                break;
            default:
                return null;

        }

        if (children != null)
        {
            for (i = 0; i < children.getLength(); i++)
            {
                if (children.item(i) != null)
                {	//Retard check for xml4j
                    importNode(copy, children.item(i));
                }
            }
        }
        if (doappend)
        {
            parent.appendChild(copy);
        }


        return copy;

    }
}
