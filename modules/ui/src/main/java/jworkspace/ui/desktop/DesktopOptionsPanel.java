package jworkspace.ui.desktop;

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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import jworkspace.LangResource;

/**
 * Class to set desktop options.
 */
public class DesktopOptionsPanel extends KPanel implements ActionListener {
    /**
     * Toggle drag mode
     */
    public static final String TOGGLE_DRAG_MODE = "TOGGLE_DRAG_MODE";
    /**
     * Toggle transparency
     */
    public static final String TOGGLE_TRANSPARENCY = "TOGGLE_TRANSPARENCY";
    /**
     * Desktop that has to be edited
     */
    Desktop desktop;
    /**
     * Drag mode
     */
    private JCheckBox drag_mode = new JCheckBox();
    /**
     * Drag mode
     */
    private JCheckBox transparent = new JCheckBox();
    /**
     * Outline flag
     */
    private boolean outline = false;

    public DesktopOptionsPanel(Desktop desktop) {
        super();
        this.desktop = desktop;
        setName(LangResource.getString("DesktopOptionsPanel.title"));

        outline = desktop.getDragMode() == JDesktopPane.OUTLINE_DRAG_MODE;

        drag_mode.setText
            (LangResource.getString("DesktopOptionsPanel.outlineDrag"));
        drag_mode.addActionListener(this);
        drag_mode.setActionCommand(DesktopOptionsPanel.TOGGLE_DRAG_MODE);
        drag_mode.setOpaque(false);

        transparent.setText
            (LangResource.getString("DesktopOptionsPanel.transparency"));
        transparent.addActionListener(this);
        transparent.setActionCommand(DesktopOptionsPanel.TOGGLE_TRANSPARENCY);
        transparent.setOpaque(false);

        setData();

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(drag_mode, gbc);

        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(transparent, gbc);
    }

    public boolean setData() {
        if (desktop.getDragMode() == JDesktopPane.OUTLINE_DRAG_MODE) {
            drag_mode.setSelected(true);
        }
        transparent.setSelected(!desktop.isOpaque());
        return true;
    }

    public boolean syncData() {
        return true;
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        if (command.equals(DesktopOptionsPanel.TOGGLE_DRAG_MODE)) {
            outline = !outline;
            if (outline) {
                desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
            } else {
                desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
            }
        } else if (command.equals(DesktopOptionsPanel.TOGGLE_TRANSPARENCY)) {
            desktop.setOpaque(!transparent.isSelected());
            desktop.revalidate();
            desktop.repaint();
        }
    }
}