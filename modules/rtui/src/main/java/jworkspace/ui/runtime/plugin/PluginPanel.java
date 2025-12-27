package jworkspace.ui.runtime.plugin;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2025 Anton Troshin

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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;
import com.hyperrealm.kiwi.ui.KPanel;

import static jworkspace.ui.WorkspaceGUI.getResourceManager;
import jworkspace.ui.runtime.LangResource;

public class PluginPanel extends KPanel implements IPluginSelectionListener {

    private final JTextPane l;
    private JComponent log = null;

    @SuppressWarnings("checkstyle:MagicNumber")
    public PluginPanel() {
        super();
        setLayout(new BorderLayout(5, 5));
        l = createDefaultLabel();
        add(l, BorderLayout.CENTER);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private JTextPane createDefaultLabel() {

        JTextPane label = new JTextPane();

        label.setContentType("text/html");
        label.setBackground(Color.white);
        label.setOpaque(true);
        label.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        //label.setHorizontalAlignment(JLabel.CENTER);
        label.setEditable(false);
      /*  label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setIconTextGap(10);
*/
        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));

        label.setBorder(new EmptyBorder(15, 10, 3, 10));
        String sb = "<html><b>" + LangResource.getString("No_messages") + "</b><br><br></html>";
        label.setText(sb);
        return label;
    }

    /**
     * Create plugin report
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    void createPluginReport(Plugin plugin) {
        if (log != null) {
            remove(log);
            log = null;
        }
        String sb = "<html><font color=\"black\">"
            + "<b>"
            + LangResource.getString("Name") + ": "
            + "</b>"
            + plugin.getName()
            + "<br><b>"
            + LangResource.getString("Type") + ": "
            + "</b>"
            + plugin.getType()
            + "<br><b>"
            + LangResource.getString("Version") + ": "
            + "</b>"
            + plugin.getVersion()
            + "<br><b>"
            + LangResource.getString("Class") + ": "
            + "</b>"
            + plugin.getClassName()
            + "<br><b>"
            + LangResource.getString("Loaded") + ": "
            + "</b>"
            + plugin.isLoaded()
            + "<br><b>"
            + LangResource.getString("Home") + ": "
            + "</b><a href=\""
            + plugin.getHelpURL().toString()
            + "\">" + plugin.getHelpURL() + "</a><br>"
            + "<br>"
            + "</font></html>";


//        if (!plugin.getProperties().isEmpty()) {
//            sb.append(LangResource.getString("Properties") + ": ");
//            sb.append("</b><br>");
//            sb.append("--------------------");
//
//            Enumeration en = plugin.getProperties().keys();
//
//            while (en.hasMoreElements()) {
//                String key = (String) en.nextElement();
//                sb.append("<br><b>");
//                sb.append(key);
//                sb.append("</b>");
//                sb.append(plugin.getProperty(key, "none"));
//                sb.append("</b>");
//            }
//            sb.append("<br>");
//            sb.append("--------------------");
//        }

        Icon icon = plugin.getIcon();
        if (icon == null && plugin.getType().equals(PluginDTO.PLUGIN_TYPE_USER)) {
            icon = new ImageIcon(getResourceManager().getImage("shell_big.png"));
        } else if (icon == null && plugin.getType().equals(PluginDTO.PLUGIN_TYPE_SYSTEM)) {
            icon = new ImageIcon(getResourceManager().getImage("plugin_big.png"));
        } else if (icon == null) {
            icon = new ImageIcon(getResourceManager().getImage("unknown_big.png"));
        }
        layoutReport(sb, icon);
    }

    private void layoutReport(String text, Icon icon) {
      /*  l.setHorizontalAlignment(JLabel.CENTER);
        l.setVerticalAlignment(JLabel.CENTER);
        l.setHorizontalTextPosition(JLabel.CENTER);
        l.setVerticalTextPosition(JLabel.BOTTOM);
        if (icon != null) {
            l.setIcon(icon);
        }*/
        l.setText(text);
        if (log != null) {
            remove(l);
            add(l, BorderLayout.NORTH);
            add(log, BorderLayout.CENTER);
        } else {
            remove(l);
            add(l, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    @Override
    public void pluginSelected(Plugin p) {
        createPluginReport(p);
    }
}
