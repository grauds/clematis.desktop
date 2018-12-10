package jworkspace.api;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

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

import java.awt.Frame;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.util.Hashtable;

/**
 * Graphic User Interface systems of Java Workspace
 * should implement this interface as it provides minimum
 * services for Java Workspace Kernel and other engines.
 *
 * @author Anton Troshin
 */
public interface GUI extends IEngine, IWorkspaceListener {
    /**
     * Returns special clipboard for graphic interface..
     */
    Clipboard getClipboard();

    /**
     * Main frame for the application.
     */
    Frame getFrame();

    /**
     * Logo screen displays information
     * about version of Java Workspace.
     */
    Window getLogoScreen();

    /**
     * Check whether this GUI is modified.
     */
    boolean isModified();

    /**
     * Returns all registered components.
     */
    Hashtable getAllRegistered();

    /**
     * Check whether gui shell is registered
     *
     * @return registered component.
     */
    Object isRegistered(String clazz);

    /**
     * Register gui shell.
     */
    void register(Object obj);

    /**
     * Unregister gui shell.
     */
    void unregister(String clazz);

    /**
     * Updates all GUI.
     */
    void update();
}