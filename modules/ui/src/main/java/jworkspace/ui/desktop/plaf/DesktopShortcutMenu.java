package jworkspace.ui.desktop.plaf;
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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import static jworkspace.ui.util.SwingUtils.createMenuItem;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.desktop.IDesktopShortcutActions;
import jworkspace.ui.desktop.actions.DesktopShortcutActions;

public class DesktopShortcutMenu extends JPopupMenu {

    private final DesktopShortcutActions desktopShortcutActions;

    public DesktopShortcutMenu(DesktopShortcutActions desktopShortcutActions) {
        super();
        this.desktopShortcutActions = desktopShortcutActions;

        JMenuItem open = createMenuItem(desktopShortcutActions.get(IDesktopShortcutActions.OPEN));
        JMenuItem rename = createMenuItem(desktopShortcutActions.get(IDesktopShortcutActions.RENAME));

        JMenuItem cut = createMenuItem(desktopShortcutActions.get(IDesktopShortcutActions.CUT));
        JMenuItem copy = createMenuItem(desktopShortcutActions.get(IDesktopShortcutActions.COPY));
        JMenuItem delete = createMenuItem(desktopShortcutActions.get(IDesktopShortcutActions.DELETE));

        JMenuItem properties = createMenuItem(desktopShortcutActions.get(IDesktopShortcutActions.PROPERTIES));

        add(open);
        addSeparator();
        add(cut);
        add(copy);
        addSeparator();
        add(delete);
        addSeparator();
        add(rename);
        add(properties);

        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    public void setVisible(boolean flag) {
        this.desktopShortcutActions.updateEnabledState();
        super.setVisible(flag);
    }
}
