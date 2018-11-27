package jworkspace.ui.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin

   This file is part of Java Workspace.

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   Author may be contacted at:

   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/

import jworkspace.LangResource;
import kiwi.ui.dialog.ComponentDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Settings dialog shows a list of general workspace options.
 * This configures laf, texture and other features.
 */
public class SettingsDialog extends ComponentDialog
{
    private TexturePanel first_panel;
    private PlafPanel second_panel;
    private LoggingPanel logging_panel;
    private JTabbedPane tabbed_pane;

    public SettingsDialog(Frame parent)
    {
        super(parent, LangResource.getString("SettingsDialog.title"), true);
        setResizable(false);
    }

    protected JComponent buildDialogUI()
    {
        setComment(null);

        tabbed_pane = new JTabbedPane();
        first_panel = new TexturePanel();
        second_panel = new PlafPanel();
        logging_panel = new LoggingPanel();
        tabbed_pane.addTab(LangResource.getString("SettingsDialog.textureTab"),
                           first_panel);
        tabbed_pane.addTab(LangResource.getString("SettingsDialog.lafTab"),
                           second_panel);
        tabbed_pane.addTab(LangResource.getString("SettingsDialog.loggingTab"),
                           logging_panel);
        return (tabbed_pane);
    }

    protected boolean accept()
    {
        return (true && first_panel.syncData() && second_panel.syncData());
    }

    public void dispose()
    {
        destroy();
        super.dispose();
    }

    public void setData()
    {
        first_panel.setData();
    }
}