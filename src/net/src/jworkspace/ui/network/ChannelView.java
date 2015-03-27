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
import javax.swing.*;
import java.awt.*;

class ChannelView extends JTextArea
{
   public ChannelView()
   {
       super();
       setBorder(null);
       setEditable(false);
       setLineWrap(false);
       setWrapStyleWord(false);
       setOpaque(false);
       append("Network Console 1.0.3");
       append("Type /help to list all commands");
   }
   public void setFont(Font font)
   {
     font = new Font("Monospaced", Font.PLAIN, 12);
     super.setFont(font);
   }
   public void append(String str)
   {
     super.append(str + "\n");
     int height = Toolkit.getDefaultToolkit().getFontMetrics(getFont()).getHeight();
     Rectangle b = new Rectangle(0, getHeight() - height, getWidth(), getHeight());
     scrollRectToVisible(b);
   }
   public void setBackground(Color bg)
   {
     super.setBackground(bg);
   }
   public void setForeground(Color fg)
   {
     super.setForeground(fg);
   }
}