package jworkspace.ui.runtime.monitor;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016,2025 Anton Troshin

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
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;

public class Nest extends Box {

    public Nest(List<Monitor> monitors) {
        super(BoxLayout.Y_AXIS);
        setOpaque(false);
        add(new HeaderPanel());
        for (Monitor monitor : monitors) {
            add(monitor);
        }
    }



}