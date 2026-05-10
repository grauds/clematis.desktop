package jworkspace.ui.desktop.plaf;
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

    public DesktopMenu() {
        super();

        createShortcut = createMenuItem(DesktopAction.createAction);
        paste = createMenuItem(DesktopAction.pasteAction);
        arrangeAll = createMenuItem(DesktopAction.arrangeAllAction);
        selectAll = createMenuItem(DesktopAction.selectAllAction);
        changeBackgroundColour = createMenuItem(DesktopAction.chooseBackgroundColorAction);
        chooseBgImage = createMenuItem(DesktopAction.chooseBgImageAction);
        gradientFill = createMenuItem(DesktopAction.gradientFillAction);
        switchCover = createMenuItem(DesktopAction.switchCoverAction);
        closeAllWindows = createMenuItem(DesktopAction.closeAllWindowsAction);

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
        DesktopAction.updateEnabledState();
        super.setVisible(flag);
    }

}
