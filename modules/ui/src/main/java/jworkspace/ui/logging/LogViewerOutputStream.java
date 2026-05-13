package jworkspace.ui.logging;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class LogViewerOutputStream extends OutputStream {

    private final LogViewerPanel viewer;

    private final StringBuilder buffer = new StringBuilder();

    public LogViewerOutputStream(LogViewerPanel viewer) {
        this.viewer = viewer;
    }

    @Override
    public void write(int b) {
        if (b == '\n') {
            flushLine();
        } else {
            buffer.append((char) b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        String s = new String(b, off, len, StandardCharsets.UTF_8);
        for (int i = 0; i < s.length(); i++) {
            write(s.charAt(i));
        }
    }

    private void flushLine() {
        if (buffer.isEmpty()) {
            return;
        }
        String line = buffer.toString();
        buffer.setLength(0);
        viewer.append(line);
    }

    @Override
    public void flush() {
        flushLine();
    }
}
