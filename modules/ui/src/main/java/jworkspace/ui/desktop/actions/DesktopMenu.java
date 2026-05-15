package jworkspace.ui.desktop.actions;
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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import static jworkspace.ui.util.SwingUtils.createMenuItem;
import jworkspace.ui.api.action.UISwitchListener;
import jworkspace.ui.desktop.IDesktopActions;
import lombok.Getter;

/**
 * Desktop menu
 * @author Anton Troshin
 */
@Getter
public class DesktopMenu extends JPopupMenu {
    
    private final JMenuItem createShortcut;
    private final JMenuItem gradientFill;
    private final JMenuItem paste;
    private final JMenuItem arrangeAll;
    private final JMenuItem selectAll;
    private final JMenuItem changeBackgroundColour;
    private final JMenuItem closeAllWindows;
    private final JMenuItem switchCover;
    private final JMenuItem chooseBgImage;

    private final DesktopActions desktopActions;

    public DesktopMenu(DesktopActions desktopActions) {
        super();

        createShortcut = createMenuItem(desktopActions.get(IDesktopActions.CREATE_SHORTCUT));
        paste = createMenuItem(desktopActions.get(IDesktopActions.PASTE));
        arrangeAll = createMenuItem(desktopActions.get(IDesktopActions.ARRANGE));
        selectAll = createMenuItem(desktopActions.get(IDesktopActions.SELECT_ALL));
        changeBackgroundColour = createMenuItem(desktopActions.get(IDesktopActions.BACKGROUND));
        chooseBgImage = createMenuItem(desktopActions.get(IDesktopActions.CHOOSE_BACKGROUND_IMAGE));
        gradientFill = createMenuItem(desktopActions.get(IDesktopActions.GRADIENT_FILL));
        switchCover = createMenuItem(desktopActions.get(IDesktopActions.TOGGLE_WALLPAPER));
        closeAllWindows = createMenuItem(desktopActions.get(IDesktopActions.CLOSE_ALL_WINDOWS));
        this.desktopActions = desktopActions;

        add(createShortcut);
        add(paste);
        addSeparator();
        add(arrangeAll);
        add(selectAll);
        addSeparator();
        add(changeBackgroundColour);
        add(chooseBgImage);
        add(gradientFill);
        add(switchCover);
        addSeparator();
        add(closeAllWindows);

        UIManager.addPropertyChangeListener(new UISwitchListener(this));
    }

    public void setVisible(boolean flag) {
        this.desktopActions.updateEnabledState();
        super.setVisible(flag);
    }

}
