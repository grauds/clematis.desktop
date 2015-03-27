package jworkspace.kernel.engines;

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

import jworkspace.kernel.engines.IEngine;
import jworkspace.kernel.IWorkspaceListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.util.Hashtable;

/**
 * Graphic User Interface systems of Java Workspace
 * should implement this interface as it provides minimum
 * services for Java Workspace Kernel and other engines.
 */

/**
 * Change log:
 * 15.11.2001 - Added isModified method to indicate
 * user activity in gui before logging out or exitinig
 * workspace. This is done, because service stores all
 * data in configuration or log files, but any open editor
 * or log in GUI should ask for the file name before
 * saving any data. This method bridges the gap.
 */
public interface GUI extends IEngine, IWorkspaceListener
{
    /**
     * Returns special clipboard for graphic interface..
     */
    public Clipboard getClipboard();

    /**
     * Main frame for the application.
     */
    public Frame getFrame();

    /**
     * Logo screen displays information
     * about version of Java Workspace.
     */
    public Window getLogoScreen();

    /**
     * Check whether this GUI is modified.
     */
    public boolean isModified();

    /**
     * Returns all registered components.
     */
    public Hashtable getAllRegistered();

    /**
     * Check whether gui shell is registered
     * @return registered component.
     */
    public Object isRegistered(String clazz);

    /**
     * Register gui shell.
     */
    public void register(Object obj);

    /**
     * Unregister gui shell.
     */
    public void unregister(String clazz);

    /**
     * Loads shell, that is a user interface for system services.
     */
//public Object loadShell(String name, String savePath);

    /**
     * Updates all GUI.
     */
    public void update();
}