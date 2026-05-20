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

import com.hyperrealm.kiwi.plugin.Plugin;

public class DownloadedPluginReport extends PluginReport {

    private String htmlInstallDownloadTemplate = "";

    public DownloadedPluginReport() {
        super();
        loadHtmlRemoveDownloadTemplate();
    }

    private void loadHtmlRemoveDownloadTemplate() {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    getClass().getResourceAsStream("/jworkspace/ui/runtime/install_download_template.html")
                ),
                StandardCharsets.UTF_8
            )
        )) {
            htmlInstallDownloadTemplate = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            htmlInstallDownloadTemplate = "<html><body>Error loading remove download layout template.</body></html>";
        }
    }

    @Override
    public String assembleReport(Plugin plugin) {
        String finalHtml =  super.assembleReport(plugin);
        finalHtml = finalHtml + htmlInstallDownloadTemplate;
        return finalHtml;
    }
}
