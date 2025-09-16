package jworkspace.ui.desktop;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2025 Anton Troshin

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

import lombok.NonNull;

/**
 * Desktop icon selection is necessary for group operations with icons.
 */
class DesktopIconSelection implements ClipboardOwner, Transferable {

    static DataFlavor desktopIconFlavor
        = new DataFlavor(DesktopIconSelectionData.class, "Desktop Icon Collection");

    private final DesktopIconSelectionData data;

    private final DataFlavor[] flavors = {desktopIconFlavor};

    DesktopIconSelection(DesktopIconSelectionData data) {
        super();
        this.data = data;
    }

    @Override
    @NonNull
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(desktopIconFlavor)) {
            return data;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.equals(desktopIconFlavor));
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}