package jworkspace.ui.api.views;

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import jworkspace.config.ServiceLocator;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.IShell;
import jworkspace.ui.api.cpanel.CButton;
import lombok.Getter;
import lombok.Setter;

/**
 * Simple user gui shell and view. This shell does nothing, but appears as plain panel in desktop.
 * @author Anton Troshin
 */
@Getter
@Setter
public class DefaultCompoundView extends DefaultView
    implements IShell, ActionListener {

    private boolean buttonsLoaded = false;

    protected DefaultCompoundView() {
        super();
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(DefaultView.SHOW)) {
            /*
             * Send message to workspace gui with request to add to layout
             */
            Map<String, Object> lparam = new HashMap<>();
            lparam.put("view", this);
            lparam.put("display", Boolean.TRUE);
            lparam.put("register", isUnique());
            fireEvent(Constants.WORKSPACE_VIEW_LISTENER_CODE, lparam);
        }
    }

    public static void fireEvent(int code, Map<String, Object> lparam) {
        ServiceLocator.getInstance()
            .getEventsDispatcher().fireEvent(
                code, lparam, null
            );
    }

    public CButton[] getButtons() {
        return null;
    }

}