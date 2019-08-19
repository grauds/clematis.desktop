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
import java.awt.datatransfer.Clipboard;

import com.hyperrealm.kiwi.ui.KFrame;

import jworkspace.api.UI;

/**
 * Default graphic user interface
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
     * Check whether this UI is modified.
     */
    @Override
    public boolean isModified() {
        return false;
    }

    /**
     * Updates all UI.
     */
    @Override
    public void update() {}

    @Override
    public void showError(String usermsg, Throwable ex) {}

    @Override
    public void showMessage(String usermsg) {}

    @Override
    public void load() {}

    @Override
    public void save() {}

    @Override
    public void reset() {}

    @Override
    public String getName() {
        return null;
    }
}
