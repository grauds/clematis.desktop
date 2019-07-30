package jworkspace.ui.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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
import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;
import jworkspace.kernel.*;
import kiwi.ui.*;
import kiwi.util.*;
import kiwi.util.plugin.*;

public class PropertiesPanel extends KPanel
{
  JLabel l = null;
  JComponent log = null;
  public PropertiesPanel()
  {
    super();
    setLayout(new BorderLayout(5,5));
    l = createDefaultLabel();
    add(l, BorderLayout.CENTER);
  }
  /**
   * Get performance label
   */
  protected JLabel createDefaultLabel()
  {
    JLabel l = new JLabel();

    l.setBackground(Color.white);
    l.setOpaque(true);
    l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    l.setHorizontalAlignment(JLabel.CENTER);
    l.setVerticalAlignment(JLabel.CENTER);
    l.setHorizontalTextPosition(JLabel.CENTER);
    l.setVerticalTextPosition(JLabel.BOTTOM);
    l.setIconTextGap(10);

    Font font = l.getFont();
    l.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));

    l.setBorder(new EmptyBorder(15, 10, 3, 10));
    StringBuffer sb = new StringBuffer();
    sb.append("<html><b>");
    sb.append(LangResource.getString("No_messages"));
    sb.append("</b><br>");
    sb.append("<br>");
    sb.append("</html>");
    l.setText(sb.toString());
    return l;
  }
  /**
   * Get performance label
   */
  protected JLabel createPluginsLabel(String text)
  {
    JLabel l = new JLabel();

    l.setBackground(Color.white);
    l.setOpaque(true);
    l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    l.setHorizontalAlignment(JLabel.CENTER);
    l.setVerticalAlignment(JLabel.CENTER);

    Font font = l.getFont();
    l.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));

    l.setText(text);
    return l;
  }
  /**
   * Create process report
   */
  public void createProcessReport(JavaProcess pr)
  {
     if (log != null)
     {
        remove(log);
        log = null;
     }
     StringBuffer sb = new StringBuffer();
     sb.append("<html><font color=black>");
     sb.append("<b>");
     sb.append(LangResource.getString("Name") + ": ");
     sb.append("</b>");
     sb.append(pr.getName());
     sb.append("<br>");
     sb.append("<b>");
     sb.append(LangResource.getString("Started_at") + ": ");
     sb.append("</b>");
     sb.append(pr.getStartTime().toString());
     sb.append("<br>");
     sb.append("</html>");
     log = pr.getVLog();
     log.setPreferredSize( new Dimension(200, 200));
     layoutReport(sb.toString(), new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
      .getResourceAsImage("images/process.png")));
  }
  /**
   * Create plugin report
   */
  public void createPluginReport(Plugin plugin)
  {
     if (log != null)
     {
        remove(log);
        log = null;
     }
     StringBuffer sb = new StringBuffer();
     sb.append("<html><font color=\"black\">");
     sb.append("<br><br>");
     sb.append("<b>");
     sb.append(LangResource.getString("Name") + ": ");
     sb.append("</b>");
     sb.append(plugin.getName());
     sb.append("<br>");
     sb.append("<b>");
     sb.append(LangResource.getString("Type") + ": ");
     sb.append("</b>");
     sb.append(plugin.getType());
     sb.append("<br>");
     sb.append("<b>");
     sb.append(LangResource.getString("Version") + ": ");
     sb.append("</b>");
     sb.append(plugin.getVersion());
     sb.append("<br>");
     sb.append("<b>");
     sb.append(LangResource.getString("Class_Name") + ": ");
     sb.append("</b>");
     sb.append(plugin.getClassName());
     sb.append("<br>");
     sb.append("<b>");
     sb.append(LangResource.getString("Loaded") + ": ");
     sb.append("</b>");
     sb.append(plugin.isLoaded());
     sb.append("<br>");
     sb.append("<b>");
     if (!plugin.getProperties().isEmpty())
     {
         sb.append(LangResource.getString("Properties") + ": ");
         sb.append("</b><br>");
         sb.append("--------------------");

         Enumeration en = plugin.getProperties().keys();

         while (en.hasMoreElements())
         {
           String key = (String) en.nextElement();
           sb.append("<br>");
           sb.append("<b>");
           sb.append(key);
           sb.append("</b>");
           sb.append(plugin.getProperty(key, "none"));
           sb.append("</b>");
         }
         sb.append("<br>");
         sb.append("--------------------");
     }
     sb.append("</font></html>");

     Icon icon = plugin.getBigIcon();
     if (icon == null && plugin.getType().equals("XShell"))
     {
       icon = new ImageIcon(Workspace.getResourceManager().
                            getImage("shell_big.png") );
     }
     else if (icon == null && plugin.getType().equals("XPlugin"))
     {
       icon = new ImageIcon(Workspace.getResourceManager().
                            getImage("plugin_big.png") );
     }
     else if (icon == null)
     {
       icon = new ImageIcon(Workspace.getResourceManager().
                               getImage("unknown_big.png"));
     }
     layoutReport(sb.toString(), icon);
  }
  public void createDefaultReport()
  {
     if (log != null)
     {
        remove(log);
        log = null;
     }
    remove(l);
    l = createDefaultLabel();
    add(l, BorderLayout.CENTER);
    revalidate();
    repaint();
  }
  protected void layoutReport(String text, Icon icon)
  {
     l.setHorizontalAlignment(JLabel.CENTER);
     l.setVerticalAlignment(JLabel.CENTER);
     l.setHorizontalTextPosition(JLabel.CENTER);
     l.setVerticalTextPosition(JLabel.BOTTOM);
     if (icon != null)
     {
        l.setIcon(icon);
     }
     l.setText(text);
     if (log != null)
     {
        remove(l);
        add(l, BorderLayout.NORTH);
        add(log, BorderLayout.CENTER);
     }
     else
     {
        remove(l);
        add(l, BorderLayout.CENTER);
     }
     revalidate();
     repaint();
  }
}