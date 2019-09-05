package jworkspace.ui.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002, 2019 Anton Troshin

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
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.ResourceLoader;
import com.hyperrealm.kiwi.util.plugin.Plugin;

import jworkspace.kernel.JavaProcess;
import jworkspace.kernel.Workspace;

/**
 * @author Anton Troshin
 */
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
public class PropertiesPanel extends KPanel {

    private JLabel l;

    private JComponent log = null;

    PropertiesPanel() {
        super();
        setLayout(new BorderLayout(5, 5));
        l = createDefaultLabel();
        add(l, BorderLayout.CENTER);
    }

    /**
     * Get performance label
     */
    private JLabel createDefaultLabel() {

        JLabel label = new JLabel();

        label.setBackground(Color.white);
        label.setOpaque(true);
        label.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setIconTextGap(10);

        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));

        label.setBorder(new EmptyBorder(15, 10, 3, 10));
        String sb = "<html><b>" + LangResource.getString("No_messages") + "</b><br><br></html>";
        label.setText(sb);
        return label;
    }

    /**
     * Get performance label
     */
    protected JLabel createPluginsLabel(String text) {
        JLabel label = new JLabel();

        label.setBackground(Color.white);
        label.setOpaque(true);
        label.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);

        Font font = label.getFont();
        label.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));

        label.setText(text);
        return label;
    }

    /**
     * Create process report
     */
    void createProcessReport(JavaProcess pr) {
        if (log != null) {
            remove(log);
            log = null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html><font color=black><b>");
        sb.append(LangResource.getString("Name")).append(": ");
        sb.append("</b>");
        sb.append(pr.getName());
        sb.append("<br><b>");
        sb.append(LangResource.getString("Started_at")).append(": ");
        sb.append("</b>");
        sb.append(pr.getStartTime().toString());
        sb.append("<br></html>");
//        log = pr.getVLog();
//        log.setPreferredSize(new Dimension(200, 200));
        layoutReport(sb.toString(), new ImageIcon(new ResourceLoader(RuntimeManagerWindow.class)
            .getResourceAsImage("images/process.png")));
    }

    /**
     * Create plugin report
     */
    void createPluginReport(Plugin plugin) {
        if (log != null) {
            remove(log);
            log = null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html><font color=\"black\">");
        sb.append("<br><br><b>");
        sb.append(LangResource.getString("Name")).append(": ");
        sb.append("</b>");
        sb.append(plugin.getName());
        sb.append("<br><b>");
        sb.append(LangResource.getString("Type")).append(": ");
        sb.append("</b>");
        sb.append(plugin.getType());
        sb.append("<br><b>");
        sb.append(LangResource.getString("Version")).append(": ");
        sb.append("</b>");
        sb.append(plugin.getVersion());
        sb.append("<br><b>");
        sb.append(LangResource.getString("Class_Name")).append(": ");
        sb.append("</b>");
        sb.append(plugin.getClassName());
        sb.append("<br><b>");
        sb.append(LangResource.getString("Loaded")).append(": ");
        sb.append("</b>");
        sb.append(plugin.isLoaded());
        sb.append("<br><b>");
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
        sb.append("</font></html>");

        Icon icon = plugin.getIcon();
        if (icon == null && plugin.getType().equals("XShell")) {
            icon = new ImageIcon(Workspace.getResourceManager().getImage("shell_big.png"));
        } else if (icon == null && plugin.getType().equals("XPlugin")) {
            icon = new ImageIcon(Workspace.getResourceManager().getImage("plugin_big.png"));
        } else if (icon == null) {
            icon = new ImageIcon(Workspace.getResourceManager().getImage("unknown_big.png"));
        }
        layoutReport(sb.toString(), icon);
    }

    void createDefaultReport() {
        if (log != null) {
            remove(log);
            log = null;
        }
        remove(l);
        l = createDefaultLabel();
        add(l, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void layoutReport(String text, Icon icon) {
        l.setHorizontalAlignment(JLabel.CENTER);
        l.setVerticalAlignment(JLabel.CENTER);
        l.setHorizontalTextPosition(JLabel.CENTER);
        l.setVerticalTextPosition(JLabel.BOTTOM);
        if (icon != null) {
            l.setIcon(icon);
        }
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
}