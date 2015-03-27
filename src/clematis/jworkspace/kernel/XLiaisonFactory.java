package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin
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
import jworkspace.xml.ValidDOMLiaison;
import jworkspace.util.WorkspaceException;

public class XLiaisonFactory
{
    /**
     * Validating DOMLiaison implementation instance
     */
    protected static ValidDOMLiaison domLiaison = null;

    /**
     * Gets the validating dom liaison implementation for the application
     * @return a DOMLiaison instance to use for parsing and writing XML
     * @exception WorkspaceException if the dom liaison class specified in the
     * properties file is not an instance of ValidDOMLiaison, or another error
     * occurs while instanciating the DOMLiaison class
     */
    public static ValidDOMLiaison getDOMLiaison() throws WorkspaceException
    {
        String dlclassname = null;
        try
        {
            if (domLiaison == null)
            {
                dlclassname = "jworkspace.xml.xerces.DOMLiaison";
                Class dlclass = Class.forName(dlclassname);
                if (!ValidDOMLiaison.class.isAssignableFrom(dlclass))
                {
                    throw new WorkspaceException(
                            "Class " + dlclassname + " does not implement ValidDOMLiason");
                }
                domLiaison = (ValidDOMLiaison) dlclass.newInstance();
            }
            return domLiaison;
        }
        catch (InstantiationException ie)
        {
            throw new WorkspaceException("Instantiation error on class: " + dlclassname);
        }
        catch (ClassNotFoundException cnf)
        {
            throw new WorkspaceException("DOMLiaison class not found: " + dlclassname);
        }
        catch (IllegalAccessException ia)
        {
            throw new WorkspaceException("Illegal access while trying to create the DOM Document");
        }
    }
}
