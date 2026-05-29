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
import java.util.Objects;
import java.util.stream.Collectors;

import jworkspace.runtime.plugin.WorkspacePluginLocator;

public class InstalledPluginReport extends PluginReport {

    private String htmlRemoveTemplate = "";

    public  InstalledPluginReport() {
        super();
        loadHtmlRemoveTemplate();
    }

    private void loadHtmlRemoveTemplate() {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    getClass().getResourceAsStream("/jworkspace/ui/runtime/remove_danger_template.html")
                ),
                StandardCharsets.UTF_8
            )
        )) {
            htmlRemoveTemplate = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            htmlRemoveTemplate = "<html><body>Error loading remove plugin layout template.</body></html>";
        }
    }

    @Override
    public String assembleReport() {
        Object del = plugin.getProperties().get(WorkspacePluginLocator.PLUGIN_DELETED);
        boolean deleted = del != null ? (Boolean) del : false;

        String finalHtml = super.assembleReport();
        finalHtml = finalHtml + (deleted ? "Plugin is deleted, restart is required" : htmlRemoveTemplate);
        return finalHtml;
    }
}
