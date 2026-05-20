package jworkspace.ui.runtime.plugin.reports;
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
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.event.HyperlinkEvent;

import com.hyperrealm.kiwi.plugin.Plugin;

import jworkspace.runtime.plugin.PluginUpdateChecker;
import jworkspace.ui.runtime.plugin.IPluginSelectionListener;
import jworkspace.ui.runtime.plugin.actions.PluginInstallAction;
import jworkspace.ui.runtime.plugin.actions.PluginUninstallAction;

public class PluginReport extends SimplePluginReport implements IPluginSelectionListener {

    public static final String PLUGIN_DASHBOARD_IMAGE_PATH = "PluginDashboard";
   // public static final String PLUGIN_SETTINGS_IMAGE_PATH = "PluginSettings";

    private String htmlImagesTemplate = "";
    private String htmlDescriptionTemplate = "";
    private String htmlUpdateTemplate = "";

    private PluginInstallAction pluginInstallAction;
    private PluginUninstallAction pluginUninstallAction;

    @SuppressWarnings("checkstyle:MagicNumber")
    public PluginReport() {
        super();

        loadHtmlImagesTemplate();
        loadHtmlDescriptionTemplate();
        loadHtmlUpdateTemplate();

        getLabel().addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if ("uninstall://trigger".equals(e.getDescription()) && pluginUninstallAction != null) {
                    pluginUninstallAction.actionPerformed(null);
                } else if ("install://trigger".equals(e.getDescription()) && pluginInstallAction != null) {
                    pluginInstallAction.actionPerformed(null);
                }
            }
        });

        setPreferredSize(new Dimension(420, 400));
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private void loadHtmlDescriptionTemplate() {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    getClass().getResourceAsStream("/jworkspace/ui/runtime/plugin_description_template.html")
                ),
                StandardCharsets.UTF_8
            )
        )) {
            htmlDescriptionTemplate = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            htmlDescriptionTemplate = "<html><body>Error loading description layout template.</body></html>";
        }
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private void loadHtmlImagesTemplate() {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    getClass().getResourceAsStream("/jworkspace/ui/runtime/plugin_screenshots_template.html")
                ),
                StandardCharsets.UTF_8
            )
        )) {
            htmlImagesTemplate = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            htmlImagesTemplate = "<html><body>Error loading images layout template.</body></html>";
        }
    }

    private void loadHtmlUpdateTemplate() {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                Objects.requireNonNull(
                    getClass().getResourceAsStream("/jworkspace/ui/runtime/update_plugin_template.html")
                ),
                StandardCharsets.UTF_8
            )
        )) {
            htmlUpdateTemplate = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            htmlUpdateTemplate = "<html><body>Error loading update plugin layout template.</body></html>";
        }
    }

    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:CyclomaticComplexity"})
    public String assembleReport(Plugin plugin) {

        String finalHtml = super.assembleReport(plugin);

        this.pluginInstallAction = new PluginInstallAction(plugin);
        this.pluginUninstallAction = new PluginUninstallAction(plugin);

        URL dashboardUrl = plugin.getProperties().getProperty(PLUGIN_DASHBOARD_IMAGE_PATH) != null
            ? getClass().getResource(plugin.getProperties().getProperty(PLUGIN_DASHBOARD_IMAGE_PATH)) : null;
      //  URL settingsUrl = plugin.getProperties().getProperty(PLUGIN_SETTINGS_IMAGE_PATH) != null
      //      ? getClass().getResource(plugin.getProperties().getProperty(PLUGIN_SETTINGS_IMAGE_PATH)) : null;

        if (dashboardUrl != null) {
            finalHtml = finalHtml
                + htmlImagesTemplate
                        .replace("${imgUrl1}", dashboardUrl.toExternalForm())
                        .replace("${imgCaption1}", "Main View");
                //.replace("${imgUrl2}", settingsUrl != null ? settingsUrl.toExternalForm() : "")
        }

        Object o = plugin.getProperties().get(PluginUpdateChecker.PLUGIN_HAS_UPDATE);
        boolean hasUpdate = o != null ? (Boolean) o : false;
        if (hasUpdate) {
            finalHtml = finalHtml + htmlUpdateTemplate;
        }

        boolean hasDescription = plugin.getDescription() != null && !plugin.getDescription().trim().isEmpty();
        if (hasDescription) {
            finalHtml = finalHtml + htmlDescriptionTemplate.replace("${description}", plugin.getDescription());
        }

        return finalHtml;
    }

    @Override
    public void pluginSelected(Plugin p) {
        createReport(p);
    }
}
