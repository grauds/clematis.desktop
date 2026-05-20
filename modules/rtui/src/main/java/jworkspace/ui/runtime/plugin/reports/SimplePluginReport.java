package jworkspace.ui.runtime.plugin.reports;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.hyperrealm.kiwi.plugin.Plugin;

import static jworkspace.ui.WorkspaceGUI.getResourceManager;
import jworkspace.runtime.plugin.WorkspacePluginLocator;
import jworkspace.ui.plugins.ShellsLoader;
import jworkspace.ui.runtime.AbstractReportPanel;

public class SimplePluginReport extends AbstractReportPanel {

    private String htmlTemplate = "";

    public SimplePluginReport() {
        super();
        loadHtmlTemplate();
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private void loadHtmlTemplate() {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    getClass().getResourceAsStream("/jworkspace/ui/runtime/simple_plugin_report_template.html")
                ),
                StandardCharsets.UTF_8
            )
        )) {
            htmlTemplate = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            htmlTemplate = "<html><body>Error loading report layout template.</body></html>";
        }
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    protected String assembleReport(Plugin plugin) {
        StringBuilder sb = new StringBuilder();
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

        return htmlTemplate
            .replace("${title}", plugin.getTitle())
            .replace("${type}", plugin.getType())
            .replace("${level}", plugin.getLevel())
            .replace("${jar}", plugin.getJarFile())
            .replace("${home}", plugin.getHelpURL().toString())
            .replace("${className}", plugin.getClassName())
            .replace("${version}", plugin.getVersion())
            .replace("${buildNumber}", plugin.getBuildNumber())
            .replace("${buildDate}", plugin.getBuildDate().toString())
            .replace("${imgCaption2}", "Settings Panel")
            .replace("${properties}", sb.toString());
    }

    public void createReport(Plugin plugin) {

        String finalHtml = assembleReport(plugin);

        Icon icon = plugin.getIcon();
        if (icon == null && plugin.getType().equals(ShellsLoader.PLUGIN_TYPE_UI)) {
            icon = new ImageIcon(getResourceManager().getImage("shell_big.png"));
        } else if (icon == null && plugin.getType().equals(WorkspacePluginLocator.PLUGIN_TYPE_PLUGIN)) {
            icon = new ImageIcon(getResourceManager().getImage("plugin_big.png"));
        } else if (icon == null) {
            icon = new ImageIcon(getResourceManager().getImage("unknown_big.png"));
        }

        layoutReport(finalHtml, icon);
    }

}
