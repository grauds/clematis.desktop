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
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.ui.KPanel;

import static jworkspace.ui.WorkspaceGUI.getResourceManager;
import jworkspace.runtime.plugin.WorkspacePluginLocator;
import jworkspace.ui.plugins.ShellsLoader;
import jworkspace.ui.runtime.LangResource;

public class PluginPanel extends KPanel implements IPluginSelectionListener {

    private JTextPane label = null;

    @SuppressWarnings("checkstyle:MagicNumber")
    public PluginPanel() {
        super();
        setLayout(new BorderLayout(5, 5));
        add(new JScrollPane(getLabel()), BorderLayout.CENTER);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private JTextPane getLabel() {

        if (label == null) {
            label = new JTextPane();

            label.setContentType("text/html");
            label.setBackground(Color.white);
            label.setOpaque(true);
            label.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            label.setEditable(false);
            label.setCaret(new DefaultCaret() {
                @Override
                public void paint(java.awt.Graphics g) {
                    // Do nothing - prevents the cursor from being drawn
                }
            });
            label.setBorder(new EmptyBorder(15, 10, 3, 10));

            Font font = label.getFont();
            label.setFont(new Font(font.getName(), Font.PLAIN, font.getSize()));
            String sb = "<html><b>Select a plugin from the list</b><br><br></html>";
            label.setText(sb);
        }
        return label;
    }

    /**
     * Create a plugin report
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    void createPluginReport(Plugin plugin) {
        String props = "<font color=\"black\">"
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
            + LangResource.getString("Jar") + ": "
            + "</b>"
            + plugin.getJarFile()
            + "<br><b>"
            + LangResource.getString("Home") + ": "
            + "</b><a href=\""
            + plugin.getHelpURL().toString()
            + "\">" + plugin.getHelpURL() + "</a>"
            + "<br>";

        StringBuilder sb = new StringBuilder(props);
        if (!plugin.getProperties().isEmpty()) {
            sb.append("<br>");
            sb.append("--------------------");
            Enumeration<Object> en = plugin.getProperties().keys();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                sb.append("<br><b>");
                sb.append(key);
                sb.append("</b>: ");
                sb.append(plugin.getProperty(key, "none"));
                sb.append("</b>");
            }
            sb.append("<br>");
            sb.append("--------------------");
            sb.append("</font>");
        }

        Icon icon = plugin.getIcon();
        if (icon == null && plugin.getType().equals(ShellsLoader.PLUGIN_TYPE_UI)) {
            icon = new ImageIcon(getResourceManager().getImage("shell_big.png"));
        } else if (icon == null && plugin.getType().equals(WorkspacePluginLocator.PLUGIN_TYPE_PLUGIN)) {
            icon = new ImageIcon(getResourceManager().getImage("plugin_big.png"));
        } else if (icon == null) {
            icon = new ImageIcon(getResourceManager().getImage("unknown_big.png"));
        }
        layoutReport(sb.toString(), icon);
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private void layoutReport(String html, Icon icon) {
        HTMLDocument doc = (HTMLDocument) label.getDocument();
        HTMLEditorKit kit = (HTMLEditorKit) label.getEditorKit();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body style='")
                .append("text-align:center;")
                .append("font-family:sans-serif;'>");
            sb.append("<div>")
                .append(html)
                .append("</div>");

            sb.append("</body></html>");
            doc.remove(0, doc.getLength());
            if (icon != null) {
                label.insertIcon(icon);
            }
            kit.insertHTML(doc, doc.getLength(), sb.toString(), 0, 0, null);
            label.setCaretPosition(0);
        } catch (BadLocationException | IOException ex) {
            /* ignore */
        }

        revalidate();
        repaint();
    }

    @Override
    public void pluginSelected(Plugin p) {
        createPluginReport(p);
    }
}
