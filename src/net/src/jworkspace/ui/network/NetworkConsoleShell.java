package jworkspace.ui.network;
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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/
import jworkspace.ui.IView;
import jworkspace.ui.views.DefaultShell;
import jworkspace.ui.cpanel.CButton;

import java.awt.*;
import java.io.IOException;

import kiwi.util.ResourceLoader;
import kiwi.util.plugin.Plugin;

import javax.swing.*;

public class NetworkConsoleShell extends DefaultShell
{
    /**
     * Constructor for plugin
     * @param plugin
     */
    public NetworkConsoleShell(Plugin plugin)
    {
        super();
    }

    protected IView getView()
    {
        NetworkConsole console = new NetworkConsole();
        console.create();
        try
        {
            console.load();
        }
        catch(IOException ex)
        {
           // do nothing here
        }
        return console;
    }

    /**
     * Return buttons for control panel
     */
    public CButton[] getButtons()
    {
        /**
         * Start button.
         */
        Image normal = new ResourceLoader(NetworkConsole.class)
                .getResourceAsImage("images/network.png");
        Image hover = new ResourceLoader(NetworkConsole.class)
                .getResourceAsImage("images/network.png");

        CButton b_show = CButton.create(this, new ImageIcon(normal),
                                        new ImageIcon(hover), DefaultShell.NEW_VIEW,
                                        "Network Console");

        return new CButton[]
        {
            b_show
        };
    }
}
