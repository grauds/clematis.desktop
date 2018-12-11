package jworkspace.ui;

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

import com.hyperrealm.kiwi.ui.KFrame;

import jworkspace.api.UI;
/**
 * Default graphic User Interface of Java Workspace
 *
 * @author Anton Troshin
 */
public class DefaultUI implements UI {

    /**
     * Returns own clipboard
     */
    @Override
    public Clipboard getClipboard() {
        return null;
    }

    /**
     * Main frame for the application.
     */
    @Override
    public Frame getFrame() {
        return new KFrame();
    }

    /**
     * Logo screen displays information
     * about version of Java Workspace.
     */
    @Override
    public Window getLogoScreen() {
        return null;
    }

    /**
     * Check whether this UI is modified.
     */
    @Override
    public boolean isModified() {
        return false;
    }

    /**
     * Returns all registered components.
     */
    @Override
    public Hashtable getAllRegistered() {
        return null;
    }

    /**
     * Check whether gui shell is registered
     *
     * @param clazz
     * @return registered component.
     */
    @Override
    public Object isRegistered(String clazz) {
        return null;
    }

    /**
     * Register gui shell.
     *
     * @param obj
     */
    @Override
    public void register(Object obj) {

    }

    /**
     * Unregister gui shell.
     *
     * @param clazz
     */
    @Override
    public void unregister(String clazz) {

    }

    /**
     * Updates all UI.
     */
    @Override
    public void update() {

    }

    @Override
    public void showError(String usermsg, Throwable ex) {
        WorkspaceError.exception(usermsg, ex);
    }

    @Override
    public void showMessage(String usermsg) {
        WorkspaceError.msg(usermsg);
    }

    /**
     * Save engine
     */
    @Override
    public void load() {

    }

    /**
     * Load engine
     */
    @Override
    public void save() {

    }

    /**
     * Reset engine
     */
    @Override
    public void reset() {

    }

    /**
     * Get human readable name
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Processes event. Such event is send to every subscribed event listener in synchronous manner.
     *
     * @param event
     * @param lparam
     * @param rparam
     */
    @Override
    public void processEvent(Object event, Object lparam, Object rparam) {

    }
}
