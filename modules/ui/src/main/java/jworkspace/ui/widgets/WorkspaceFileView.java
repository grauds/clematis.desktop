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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import jworkspace.ui.WorkspaceGUI;

/**
 * @author Anton Troshin
 */
@SuppressWarnings("MagicNumber")
public class WorkspaceFileView extends FileView {

    private static final Icon HTML_ICON = WorkspaceGUI.getResourceManager().getIcon("filedlg/html.gif");

    private final Map<String, Icon> icons = new HashMap<>();

    private final Map<File, String> fileDescriptions = new HashMap<>();

    private final Map<String, String> typeDescriptions = new HashMap<>();

    public WorkspaceFileView() {
        super();
        putIcon("jpg", WorkspaceGUI.getResourceManager().
            getIcon("filedlg/jpg.gif"));
        putIcon("gif", WorkspaceGUI.getResourceManager().
            getIcon("filedlg/gif.gif"));
        putIcon("jar", WorkspaceGUI.getResourceManager().
            getIcon("filedlg/jar.gif"));
        putIcon("java", WorkspaceGUI.getResourceManager().
            getIcon("filedlg/java.gif"));
        putIcon("class", WorkspaceGUI.getResourceManager().
            getIcon("filedlg/class.gif"));
        putIcon("jsp", WorkspaceGUI.getResourceManager().
            getIcon("filedlg/jsp.gif"));
        putIcon("html", HTML_ICON);
        putIcon("htm", HTML_ICON);
        putIcon("shtml", HTML_ICON);
    }

    public String getDescription(File f) {
        return fileDescriptions.get(f);
    }

    /**
     * Convenient method that returns the extension for the given file.
     */
    private String getExtension(File f) {
        if (f != null) {
            String name = f.getName();
            int extensionIndex = name.lastIndexOf('.');
            return extensionIndex < 0 ? null : name.substring(extensionIndex + 1).toLowerCase();
        }
        return null;
    }

    public Icon getIcon(File f) {
        Icon icon = null;
        String extension = getExtension(f);
        if (extension != null) {
            icon = icons.get(extension);
        }
        return icon;
    }

    public String getTypeDescription(File f) {
        return typeDescriptions.get(getExtension(f));
    }

    /**
     * Whether the file is hidden or not. This implementation returns
     * true if the filename starts with a "."
     */
    public Boolean isHidden(File f) {
        if (f != null && !f.getName().isEmpty() && f.getName().charAt(0) == '.') {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean isTraversable(File f) {
        if (f.isDirectory()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Adds a human readable description of the file.
     */
    public void putDescription(File f, String fileDescription) {
        fileDescriptions.put(f, fileDescription);
    }

    /**
     * Adds an ICON based on the file type "dot" extension
     * string, e.g: ".gif". Case is ignored.
     */
    private void putIcon(String extension, Icon icon) {
        icons.put(extension, icon);
    }

    /**
     * Adds a human readable type description for files of the type of
     * the passed in file. Based on "dot" extension strings, e.g: ".gif".
     * Case is ignored.
     */
    public void putTypeDescription(File f, String typeDescription) {
        putTypeDescription(getExtension(f), typeDescription);
    }

    /**
     * Adds a human readable type description for files. Based on "dot"
     * extension strings, e.g: ".gif". Case is ignored.
     */
    private void putTypeDescription(String extension, String typeDescription) {
        typeDescriptions.put(typeDescription, extension);
    }
}