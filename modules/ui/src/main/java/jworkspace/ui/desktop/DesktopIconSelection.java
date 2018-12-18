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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * Desktop ICON selection is nessesary for
 * group operations with icons.
 */
class DesktopIconSelection implements ClipboardOwner, Transferable {
    public static DataFlavor DesktopIconFlavor
        = new DataFlavor(DesktopIconSelectionData.class, "Desktop Icon Collection");
    private DesktopIconSelectionData data;
    private DataFlavor[] flavors = {DesktopIconFlavor};

    /**
     * Public constructor.
     */
    public DesktopIconSelection(DesktopIconSelectionData data) {
        super();
        this.data = data;
    }

    /**
     * Returns transfer data, that is actually a desktop ICON data.
     */
    public Object getTransferData(DataFlavor flavor)
        throws java.io.IOException, UnsupportedFlavorException {
        if (flavor.equals(DesktopIconFlavor)) {
            return data;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.equals(DesktopIconFlavor));
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}