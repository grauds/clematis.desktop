package jworkspace.network.datagram;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright 2000 Anton Troshin
   This file is part of Java Workspace.
   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
   Authors may be contacted at:
  larsyde@diku.dk
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
 */

import java.io.*;
import java.util.*;

/**
 * This class implements a representation of the messages that
 * are for instant messaging format
 */
public class Message  implements Serializable
{
   /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -1464383390635540405L;

    private String contents = "";
    private String author = "";
    private String title = "";
    private String document = "";
    private Date creationDate = GregorianCalendar.getInstance().getTime();
    private String creator = "";

    private final static java.lang.String[] FIELD_NAMES = {
        "Author", "Title", "Contents", "MessageID", "DocumentID", "Created on"};

    /**
     * Message constructor.
     */
    public Message()
    {
        super();
    }

    /**
     */
    public java.lang.String getAuthor()
    {
        return author;
    }

    /**
     */
    public java.lang.String getContents()
    {
        return contents;
    }

    /**
     */
    public java.util.Date getCreationDate()
    {
        return creationDate;
    }

    /**
     */
    public java.lang.String getCreator()
    {
        return creator;
    }

    /**
     */
    public java.util.Vector getDataAsVector()
    {
        Vector v = new Vector();

        v.addElement(getAuthor());
        v.addElement(getTitle());
        v.addElement(getContents());
        v.addElement(getMessageID());
        v.addElement(getDocument());
        v.addElement(getCreationDate().toString());

        return v;
    }

    /**
     */
    public java.lang.String getDocument()
    {
        return document;
    }

    /**
     */
    public final static java.lang.String[] getFieldNames()
    {
        return FIELD_NAMES;
    }

    /**
     */
    public java.lang.String getMessageID()
    { // I initially opted for a combination of the toString() method and the document name as unique identifier, but
        // the scheme seemed both overly cautious (what is the likelihood of two identically named authors submitting
        // identically entitled messages at the exact same millisecond ?) and aesthetically unpleasing when presented
        // to the user.
        return this.toString() + getDocument();
    }

    /**
     */
    public java.lang.String getTitle()
    {
        return title;
    }

    /**
     */
    public void setAuthor(java.lang.String newAuthor)
    {
        author = newAuthor;
    }

    /**
     */
    public void setContents(java.lang.String newContents)
    {
        contents = newContents;
    }

    /**
     */
    public void setCreator(java.lang.String newCreator)
    {
        creator = newCreator;
    }

    /**
     */
    public void setDocument(java.lang.String newDocument)
    {
        document = newDocument;
    }

    /**
     */
    public void setTitle(java.lang.String newTitle)
    {
        title = newTitle;
    }

    /**
     */
    public String toString()
    {
        return title + ", by " + author + " on " + creationDate.toString();
    }
}
