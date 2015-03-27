package jworkspace.network;

/* ----------------------------------------------------------------------------
   Clematis Collaboration Network 1.0.3
   Copyright (C) 2001-2003 Anton Troshin
   This file is part of Java Workspace Collaboration Network.
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

import com.sun.media.jsdt.*;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

/**
 * This class implements a representation of a Java Workspace
 * group of related users.
 */
public class UserGroup implements Serializable
{
    /**
     * Group name
     */
    private String name = "";
    /**
     * Group description
     */
    private String description = "";
    /**
     * URL of group session
     */
    private URLString sessionURL = null;
    /**
     * List of group runtime users.
     */
    private HashSet users = new HashSet();
    /**
     * The name of user, who created this session.
     * It could be a teacher in a class or any
     * selected person.
     */
    private String main_user = "";
    /**
     * Creation date
     */
    private Date creationDate = new Date();
    /**
     * Fields for managers table.
     */
    private final static String fieldNames[] =
            {
                "Group name",
                "Description",
                "Active users",
                "Creation date",
                "Created by"
            };

    /**
     * Empty UserGroup constructor.
     */
    public UserGroup()
    {
        super();
    }

    /**
     * Copy constructor
     */
    public UserGroup(UserGroup usersGroup)
    {
        super();
        setName(usersGroup.getName());
        setCreationDate(usersGroup.getCreationDate());
        setMainUser(usersGroup.getMainUser());
        setDescription(usersGroup.getDescription());
        setUsers((HashSet)usersGroup.getUsers().clone());
        setSessionURL(usersGroup.getSessionURL());
    }

    /**
     * Create group of users from XML formatted data
     *
     * <group>
     *   <name></name>
     *   <main_user></main_user>
     *   <desc></desc>
     *   <creation_date></creation_date>
     *   <session></session>
     *   <users>
     *       <user>Tony</user>
     *       <user>Max</user>
     *   </users>
     * </group>
     * @return new group of users
     */
    public static UserGroup create(String xml) throws NullPointerException,
                                                      JDOMException, IOException
    {
        /**
          * Validate data
          */
         if ( xml == null )
         {
             throw new IllegalArgumentException("Null data in UserGroup constructor");
         }
         /**
          * Parse incoming data
          */
          SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
          Document doc = builder.build( new StringReader(xml) );
         /**
          * New users group
          */
          Element group = doc.getRootElement();
          UserGroup ug = new UserGroup();

          Element el = group.getChild("name");
          if ( el != null )
          {
            ug.setName( el.getTextTrim() );
          }

          el = group.getChild("main_user");
          if ( el != null )
          {
            ug.setMainUser( el.getTextTrim() );
          }

          el = group.getChild("desc");
          if ( el != null )
          {
            ug.setDescription( el.getTextTrim() );
          }

          el = group.getChild("creation_date");
          if ( el != null )
          {
             try
             {
                 long creation_date = Long.parseLong(el.getTextTrim());
                 ug.setCreationDate( new Date(creation_date));
             }
             catch(NumberFormatException ex)
             {
                 ug.setCreationDate( new Date());
             }
          }

          el = group.getChild("session");
          if ( el != null )
          {
             String url = el.getTextTrim();
             URLString urlString = new URLString(url);
             ug.setSessionURL( urlString );
          }

          el = group.getChild("users");
          if ( el != null )
          {
             List users = el.getChildren("users");
             if ( users != null )
             {
                 Iterator it = users.iterator();
                 while (it.hasNext())
                 {
                     ug.getUsers().add( ((Element)it.next()).getTextTrim() );
                 }
             }
          }

          return ug;
    }
    /**
     * Save users group data into XML formatted string
     *
     * <group>
     *   <name></name>
     *   <main_user></main_user>
     *   <desc></desc>
     *   <creation_date></creation_date>
     *   <session></session>
     *   <users>
     *       <user>Tony</user>
     *       <user>Max</user>
     *   </users>
     * </group>
     *
     * @return xml string representation of this group
     */
    public String toXString() throws IOException
    {
         StringWriter sw = new StringWriter();
         Element group = new Element("group");

         Element el = new Element("name");
         el.setText( getName() );
         group.addContent(el);

         el = new Element("main_user");
         el.setText( getMainUser() );
         group.addContent(el);

         el = new Element("desc");
         el.setText( getDescription() );
         group.addContent(el);

         el = new Element("creation_date");
         el.setText( Long.toString( getCreationDate().getTime() ) );
         group.addContent(el);

         el = new Element("session");
         el.setText( getSessionURL().toString() );
         group.addContent(el);

         el = new Element("users");
         group.addContent(el);

         Iterator it = getUsers().iterator();
         while ( it.hasNext() )
         {
            Element user = new Element("user");
            user.setText( (String) it.next() );
            el.addContent( user );
         }

         XMLOutputter serializer = new XMLOutputter();
         serializer.setIndent("  "); // use two space indent
         serializer.setNewlines(true);
         serializer.output(group, sw);

         return sw.toString();
    }
    /**
     * Get creation date
     */
    public Date getCreationDate()
    {
        return creationDate;
    }

    /**
     * Get main user
     */
    public String getMainUser()
    {
        return main_user;
    }

    /**
     * Get description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Get field names
     */
    public final static String[] getFieldNames()
    {
        return fieldNames;
    }

    /**
     * Pack the data fields corresponding to the entries in the field
     * label array into a vector and return it. This method
     * primarily intended for use with tabular display
     * (a vector corresponds to a row of table data).
     */
    public Vector getGroupAsVector()
    {
        Vector v = new Vector();

        v.addElement(getName());
        v.addElement(getDescription());
        v.addElement(String.valueOf(users.size()));
        v.addElement(getCreationDate().toString());
        v.addElement(getMainUser());

        return v;
    }

    /**
     * Get users group name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get all users
     */
    public HashSet getUsers()
    {
        return users;
    }

    /**
     * Get URL string for this group of users
     */
    public URLString getSessionURL()
    {
        return sessionURL;
    }

    /**
     * Create and return unique identifier for the JSDT channel
     * created to convey information pertaining to workgroup.
     */
    public String getUsersGroupChannelID()
    {
        return getUsersGroupID() + " - GROUPCHANNEL";
    }

    /**
     * Same as getUsersGroupChannelID(), only with a specifiable suffix.
     */
    public String getUsersGroupChannelID(String suffix)
    {
        return getUsersGroupID() + " - " + suffix;
    }

    /**
     * Create and return a unique identifier for the workgroup
     */
    public String getUsersGroupID()
    {
        return getName();
    }

    /**
     * Checks if user is registered in this group
     */
    public boolean hasUser(String name)
    {
        return users.contains(name);
    }

    /**
     * Sets creation date for this users group.
     * Once set, this cannot be modified.
     */
    public void setCreationDate(Date creationDate)
    {
        if (this.creationDate != null)
        {
            return;
        }
        this.creationDate = creationDate;
    }

    /**
     * Sets main user for this group
     */
    public void setMainUser(String main_user)
    {
        this.main_user = main_user;
    }

    /**
     * Sets description for this group
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Sets group name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets active users list for this group.
     */
    public void setUsers(HashSet users)
    {
        this.users = users;
    }

    /**
     * Sets session URL for this group
     */
    public void setSessionURL(URLString sessionURL)
    {
        this.sessionURL = sessionURL;
    }
}
