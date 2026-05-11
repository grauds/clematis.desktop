package jworkspace.ui.desktop.actions;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class AbstractActionsCollection {
    protected final Map<String, Action> actions = new HashMap<>();

    protected void register(Action action) {
        String key = (String) action.getValue(Action.ACTION_COMMAND_KEY);
        actions.put(key, action);
    }

    public Action get(String command) {
        return actions.get(command);
    }

    public Collection<Action> all() {
        return actions.values();
    }

    protected void initKeyBindings(JComponent component) {
        InputMap im = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = component.getActionMap();

        for (Action action : actions.values()) {
            Object key = action.getValue(Action.ACTION_COMMAND_KEY);
            KeyStroke ks = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);

            am.put(key, action);

            if (ks != null) {
                im.put(ks, key);
            }
        }
    }
}
