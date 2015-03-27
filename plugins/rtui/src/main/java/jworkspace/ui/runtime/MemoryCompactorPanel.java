package jworkspace.ui.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2000 Anton Troshin

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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import kiwi.util.*;
import kiwi.ui.*;

class MemoryCompactorPanel extends KPanel
{
 /**
  * Default quantity of free memory (%)
  */
 private int free = 50;
 /**
  * Use compactor or not
  */
 private boolean use = true;
 /**
  * Widget for memory compactor
  */
 private KPanel widget = new KPanel();
 /**
  * Memory compactor thread.
  */
 private MemoryCompactor mc = new MemoryCompactor(free, new boolean[1]);

  public MemoryCompactorPanel()
  {
   super();

   GridBagLayout gb = new GridBagLayout();
   GridBagConstraints gbc = new GridBagConstraints();
   setLayout(gb);
   setOpaque(false);

   gbc.anchor = gbc.NORTHWEST;
   gbc.fill = gbc.HORIZONTAL;
   gbc.weightx = 0;

   if (use)
   {
     mc.start();
   }

   mc.setStatusArea(widget);

   widget.setPreferredSize(new Dimension(150, 40));
   widget.setOpaque(false);
   gbc.weightx = 1;
   gbc.insets = KiwiUtils.lastInsets;
   gbc.gridwidth = gbc.REMAINDER;
   add(widget, gbc);
  }
  public Dimension getPreferredSize()
  {
    return new Dimension(200,130);
  }
}