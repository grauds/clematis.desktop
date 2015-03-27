package jworkspace.ui.widgets;

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

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import jworkspace.kernel.Workspace;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import java.io.File;
import java.util.Hashtable;

public class WorkspaceFileView extends FileView
{
    private Hashtable icons = new Hashtable(5);
    private Hashtable fileDescriptions = new Hashtable(5);
    private Hashtable typeDescriptions = new Hashtable(5);

    public WorkspaceFileView()
    {
        super();
        putIcon("jpg", Workspace.getResourceManager().
                       getIcon("filedlg/jpg.gif"));
        putIcon("gif", Workspace.getResourceManager().
                       getIcon("filedlg/gif.gif"));
        putIcon("jar", Workspace.getResourceManager().
                       getIcon("filedlg/jar.gif"));
        putIcon("java", Workspace.getResourceManager().
                        getIcon("filedlg/java.gif"));
        putIcon("class", Workspace.getResourceManager().
                         getIcon("filedlg/class.gif"));
        putIcon("jsp", Workspace.getResourceManager().
                       getIcon("filedlg/jsp.gif"));
        putIcon("html", Workspace.getResourceManager().
                        getIcon("filedlg/html.gif"));
        putIcon("htm", Workspace.getResourceManager().
                       getIcon("filedlg/html.gif"));
        putIcon("shtml", Workspace.getResourceManager().
                         getIcon("filedlg/html.gif"));
    }

    public String getDescription(File f)
    {
        return (String) fileDescriptions.get(f);
    }

    /**
     * Conveinience method that returnsa the "dot" extension for the
     * given file.
     */
    public String getExtension(File f)
    {
        String name = f.getName();
        if (name != null)
        {
            int extensionIndex = name.lastIndexOf('.');
            if (extensionIndex < 0)
            {
                return null;
            }
            return name.substring(extensionIndex + 1).toLowerCase();
        }
        return null;
    }

    public Icon getIcon(File f)
    {
        Icon icon = null;
        String extension = getExtension(f);
        if (extension != null)
        {
            icon = (Icon) icons.get(extension);
        }
        return icon;
    }

    public String getName(File f)
    {
        return null;
    }

    public String getTypeDescription(File f)
    {
        return (String) typeDescriptions.get(getExtension(f));
    }

    /**
     * Whether the file is hidden or not. This implementation returns
     * true if the filename starts with a "."
     */
    public Boolean isHidden(File f)
    {
        String name = f.getName();
        if (name != null && !name.equals("") && name.charAt(0) == '.')
        {
            return Boolean.TRUE;
        }
        else
        {
            return Boolean.FALSE;
        }
    }

    public Boolean isTraversable(File f)
    {
        if (f.isDirectory())
        {
            return Boolean.TRUE;
        }
        else
        {
            return Boolean.FALSE;
        }
    }

    /**
     * Adds a human readable description of the file.
     */
    public void putDescription(File f, String fileDescription)
    {
        fileDescriptions.put(fileDescription, f);
    }

    /**
     * Adds an icon based on the file type "dot" extension
     * string, e.g: ".gif". Case is ignored.
     */
    public void putIcon(String extension, Icon icon)
    {
        icons.put(extension, icon);
    }

    /**
     * Adds a human readable type description for files of the type of
     * the passed in file. Based on "dot" extension strings, e.g: ".gif".
     * Case is ignored.
     */
    public void putTypeDescription(File f, String typeDescription)
    {
        putTypeDescription(getExtension(f), typeDescription);
    }

    /**
     * Adds a human readable type description for files. Based on "dot"
     * extension strings, e.g: ".gif". Case is ignored.
     */
    public void putTypeDescription(String extension, String typeDescription)
    {
        typeDescriptions.put(typeDescription, extension);
    }
}