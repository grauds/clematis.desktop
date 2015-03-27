package jworkspace.ui.desktop;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 Anton Troshin

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

   Authors may be contacted at:

   frenzy@ix.netcom.com
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
*/

import jworkspace.LangResource;
import kiwi.ui.dialog.ComponentDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Carrier class for <code>jworkspace.ui.desktop.DesktopIconPanel</code>
 */
class DesktopIconDialog extends ComponentDialog
{
    private DesktopIconPanel panel;

    DesktopIconDialog(Frame parent)
    {
        super(parent, LangResource.getString("DesktopIconDlg.title"), true);
        setResizable(false);
    }

    protected boolean accept()
    {
        return (panel.syncData());
    }

    protected JComponent buildDialogUI()
    {
        setComment(null);
        panel = new DesktopIconPanel();
        return (panel);
    }

    public void dispose()
    {
        destroy();
        super.dispose();
    }

    public void setData(DesktopIcon data)
    {
        panel.setData(data);
    }
}