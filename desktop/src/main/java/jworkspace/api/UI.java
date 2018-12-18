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

import javax.swing.Icon;

/**
 * Graphic User Interface systems of Java Workspace
 * should implement this interface as it provides minimum
 * services for Java Workspace Kernel and other engines.
 *
 * @author Anton Troshin
 */
public interface UI extends IEngine, IWorkspaceListener {

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
     * Check whether this UI is modified.
     */
    boolean isModified();

    /**
     * Returns all registered components.
     */
    Hashtable getAllRegistered();

    /**
     * Check whether if a component is registered
     *
     * @return registered component.
     */
    Object isRegistered(String clazz);

    /**
     * Register component.
     */
    void register(Object obj);

    /**
     * Unregister component by class name.
     */
    void unregister(String clazz);

    /**
     * Updates all UI.
     */
    void update();

    /**
     * Show error to user either way it capable of
     *
     * @param question message
     */
    boolean showConfirmDialog(String question, String title, Icon icon);

    /**
     * Show error to user either way it capable of
     *
     * @param usermsg message
     * @param ex exception
     */
    void showError(String usermsg, Throwable ex);

    /**
     * Show message to user either way it capable of
     *
     * @param usermsg message
     */
    void showMessage(String usermsg);
}