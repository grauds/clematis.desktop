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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import static jworkspace.ui.utils.SwingUtils.createMenuItem;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.action.UISwitchListener;
import lombok.Getter;

/**
 * Desktop menu
 * @author Anton Troshin
 */
@Getter
public class DesktopMenu extends JPopupMenu {
    
    private final DesktopTheme theme;
    private final ActionListener[] listeners;

    private final JMenuItem createShortcut;
    private final JMenuItem gradientFill;
    private final JMenuItem paste;
    private final JMenuItem arrangeAll;
    private final JMenuItem selectAll;
    private final JMenuItem changeBackgroundColour;
    private final JMenuItem closeAllWindows;
    private final JMenuItem switchCover;
    private final JMenuItem chooseBgImage;

    public DesktopMenu(DesktopTheme theme, ActionListener... listeners) {
        super();
        this.theme = theme;
        this.listeners = listeners;

        createShortcut = createMenuItem("New Shortcut...",
            this::fireActionEvent,
            Constants.CREATE_SHORTCUT,
            null
        );

        paste = createMenuItem("Paste",
            this::fireActionEvent,
            DesktopInteractionLayer.PASTE,
            KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK)
        );

        arrangeAll = createMenuItem("Arrange All",
            this::fireActionEvent,
            DesktopInteractionLayer.ARRANGE,
            null
        );

        selectAll = createMenuItem("Select All",
            this::fireActionEvent,
            DesktopInteractionLayer.SELECT_ALL,
            KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK)
        );

        changeBackgroundColour = createMenuItem("Background Colour...",
            this::fireActionEvent,
            Constants.BACKGROUND,
            null
        );

        chooseBgImage = createMenuItem("Background Image...",
            this::fireActionEvent,
            Constants.CHOOSE_BACKGROUND_IMAGE,
            null
        );

        gradientFill = createMenuItem("Switch Gradient Fill",
            this::fireActionEvent,
            Constants.GRADIENT_FILL,
            null
        );

        switchCover = createMenuItem("Switch Background Image",
            this::fireActionEvent,
            Constants.SWITCH_COVER,
            null
        );

        closeAllWindows = createMenuItem("Close All Windows",
            this::fireActionEvent,
            Constants.CLOSE_ALL_WINDOWS,
            null
        );

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

    private void fireActionEvent(ActionEvent e) {
        for (ActionListener listener : listeners) {
            listener.actionPerformed(e);
        }
    }

    public void setVisible(boolean flag) {
        updateMenuItems();
        super.setVisible(flag);
    }

    public void updateMenuItems() {

        if (this.theme.isGradientFill()) {
            getGradientFill().setText("Hide Gradient");
        } else {
            getGradientFill().setText("Show Gradient");
        }
        if (this.theme.getCover() == null) {
            getSwitchCover().setEnabled(false);
            getSwitchCover().setText("No Background Image");
        } else {
            getSwitchCover().setEnabled(true);
            if (this.theme.isCoverVisible()) {
                getSwitchCover().setText("Hide Background Image");
            } else {
                getSwitchCover().setText("Show Background Image");
            }
        }
        /*Transferable contents = DesktopServiceLocator.getInstance()
            .getWorkspaceGUI().getClipboard().getContents(this);
        if (contents == null) {
            desktopPopupgetPaste().setEnabled(false);
        } else {
            desktopPopupgetPaste().setEnabled(contents instanceof DesktopIconSelection);
        }*/
    }
}
