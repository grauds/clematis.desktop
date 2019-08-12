package jworkspace.ui.desktop;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003, 2019 Anton Troshin

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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import static jworkspace.ui.desktop.DesktopConstants.PASTE;
import static jworkspace.ui.desktop.DesktopConstants.SELECT_ALL;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.Utils;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.action.UISwitchListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Desktop menu
 * @author Anton Troshin
 */
@EqualsAndHashCode(callSuper = true)
@Data
class DesktopMenu extends JPopupMenu {

    private Desktop desktop;

    private JMenuItem createShortcut;

    private JMenuItem gradientFill;

    private JMenuItem paste;

    private JMenuItem selectAll;

    private JMenuItem changeBackgroundColour;

    private JMenuItem closeAllWindows;

    private JMenuItem switchCover;

    private JMenuItem chooseBgImage;

    DesktopMenu(Desktop desktop, ActionListener listener) {
        super();
        this.desktop = desktop;

        createShortcut = add(Utils.createMenuItem(listener,
            WorkspaceResourceAnchor.getString("Desktop.menu.createShortcut") + WorkspaceGUI.LOG_FINISH,
                DesktopConstants.CREATE_SHORTCUT, null));

        paste = add(Utils.createMenuItem(listener, WorkspaceResourceAnchor.getString(
            "Desktop.menu.paste"), PASTE, null));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));

        selectAll = add(Utils.createMenuItem(listener, WorkspaceResourceAnchor.getString("Desktop.menu.selectAll"),
                SELECT_ALL, null));
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
        this.addSeparator();

        gradientFill = add(Utils.createMenuItem(listener,
            WorkspaceResourceAnchor.getString("Desktop.menu.gradientFill"), DesktopConstants.GRADIENT_FILL, null));

        changeBackgroundColour = add(Utils.createMenuItem(listener,
            WorkspaceResourceAnchor.getString("Desktop.menu.background"), DesktopConstants.BACKGROUND, null));

        chooseBgImage = add(Utils.createMenuItem(listener,
            WorkspaceResourceAnchor.getString("Desktop.menu.chooseBgImage"),
            DesktopConstants.CHOOSE_BACKGROUND_IMAGE, null));
        switchCover = add(Utils.createMenuItem(listener,
            WorkspaceResourceAnchor.getString("Desktop.menu.switchCover"), DesktopConstants.SWITCH_COVER, null));

        this.addSeparator();

        closeAllWindows = add(Utils.createMenuItem(listener,
            WorkspaceResourceAnchor.getString("Desktop.menu.closeAllWindows"),
            DesktopConstants.CLOSE_ALL_WINDOWS, null));

        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    public void setVisible(boolean flag) {
        removeAll();

        add(createShortcut);
        add(gradientFill);
        add(paste);

        this.addSeparator();

        add(selectAll);
        add(changeBackgroundColour);
        add(chooseBgImage);
        add(switchCover);

        this.addSeparator();

        add(closeAllWindows);

        desktop.updateMenuItems();

        super.setVisible(flag);
    }
}
