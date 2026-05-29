package jworkspace.runtime.plugin;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.swing.SwingWorker;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;
import com.hyperrealm.kiwi.plugin.VersionInfo;

import lombok.extern.java.Log;
import tools.jackson.databind.ObjectMapper;

@Log
public class PluginUpdateChecker {

    public static final String PLUGIN_HAS_UPDATE = "HasUpdate";

    static final ObjectMapper MAPPER = new ObjectMapper();

    private PluginUpdateChecker() {}

    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:NestedIfDepth", "checkstyle:ReturnCount"})
    public static boolean checkUpdateAvailable(PluginDTO plugin) throws Exception {
        if (plugin == null) {
            return false;
        }

        String localVersion = plugin.getVersion();
        String localBuildNum = plugin.getBuildNumber();
        String localBuildDateStr = "";

        if (plugin.getBuildDate() != null) {
            localBuildDateStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(plugin.getBuildDate());
        }

        VersionInfo localInfo = new VersionInfo(
            localVersion, localBuildNum, localBuildDateStr, URI.create("file:" + plugin.getJarFile()).toURL()
        );

        // Fallback or dynamic repository extraction logic parsing
        String repoPath = "grauds/clematis.desktop";
        if (plugin.getHelpURL() != null) {
            String urlStr = plugin.getHelpURL().toString();
            if (urlStr.contains("://github.com/")) {
                String afterGithub = urlStr.split("://github.com/")[1];
                String[] parts = afterGithub.split("/");
                if (parts.length >= 2) {
                    repoPath = parts[0] + "/" + parts[1];
                }
            }
        }

        // Resolve dynamic metadata from GitHub API
        VersionInfo remoteInfo = getLatestReleaseAsset(repoPath, plugin.getName());
        if (remoteInfo == null) {
            log.warning("Could not resolve asset metadata from the GitHub API release pipeline.");
            return false;
        }

        // Process update evaluation algorithm
        if (isUpdateAvailable(localInfo, remoteInfo)) {
            plugin.setUpdateVersion(remoteInfo);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Resolves the latest release data from GitHub and locates the appropriate asset URL and its version string.
     * @param repoPath, username and repo, for example grauds/clematis.desktop
     * @param pluginName, the name of the plugin to find the update for
     */
    @SuppressWarnings({
        "checkstyle:ReturnCount",
        "checkstyle:MultipleStringLiterals",
        "checkstyle:MagicNumber"
    })
    public static VersionInfo getLatestReleaseAsset(String repoPath, String pluginName) {
        if (pluginName == null || repoPath == null || repoPath.isEmpty()) {
            return null;
        }

        try {
            String apiEndpoint = String.format("https://api.github.com/repos/%s/releases/latest", repoPath);
            URL url = URI.create(apiEndpoint).toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("User-Agent", "Java-Plugin-Updater");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            try (InputStream is = conn.getInputStream()) {
                GitHubReleaseDTO release = MAPPER.readValue(is, GitHubReleaseDTO.class);
                // Delegate to the shared extraction engine (looking for .jar files)
                return extractAsset(release, pluginName, ".jar");
            }
        } catch (Exception e) {
            log.severe("Failed parsing GitHub release endpoint payload: " + e.getMessage());
        }
        return null;
    }

    /**
     * Shared extraction logic made package-private so it can be thoroughly unit tested.
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    static VersionInfo extractAsset(GitHubReleaseDTO release, String pluginName, String extension)
        throws Exception {
        if (release == null || release.assets() == null || pluginName == null) {
            return null;
        }

        String assetPrefix = pluginName.toLowerCase() + "_";

        for (GitHubAssetDTO asset : release.assets()) {
            if (asset.name() == null) {
                continue;
            }

            String assetName = asset.name().toLowerCase();

            if (assetName.startsWith(assetPrefix) && assetName.endsWith(extension)) {
                return fetchRemoteVersion(asset.browserDownloadUrl());
            }
        }
        return null;
    }

    /**
     * Downloads and parses the manifest header directly from the online binary JAR stream.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public static VersionInfo fetchRemoteVersion(String urlString) throws Exception {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("User-Agent", "Java-Plugin-Updater");

        // Handle GitHub redirect logic cleanly via stream processing
        try (InputStream is = conn.getInputStream();
             JarInputStream jis = new JarInputStream(is)) {

            Manifest manifest = jis.getManifest();
            if (manifest == null) {
                throw new IOException("No manifest file could be resolved within the target remote Jar stream.");
            }

            Attributes attr = manifest.getMainAttributes();
            // Matching the Custom Keys defined inside your PluginDTO manifest builder
            String version = attr.getValue("Implementation-Version");
            String buildNumber = attr.getValue("Build-Number");
            String buildDate = attr.getValue("Build-Date");

            return new VersionInfo(version, buildNumber, buildDate, url);
        }
    }

    @SuppressWarnings({
        "checkstyle:MultipleStringLiterals",
        "checkstyle:CyclomaticComplexity",
        "checkstyle:ReturnCount"
    })
    public static boolean isUpdateAvailable(VersionInfo local, VersionInfo remote) {
        // Always compare semantic versions first
        String[] localParts = local.version().replace("-SNAPSHOT", "").split("\\.");
        String[] remoteParts = remote.version().replace("-SNAPSHOT", "").split("\\.");

        int length = Math.max(localParts.length, remoteParts.length);
        for (int i = 0; i < length; i++) {
            int localPart = i < localParts.length
                ? Integer.parseInt(localParts[i].replaceAll("\\D", "")) : 0;
            int remotePart = i < remoteParts.length
                ? Integer.parseInt(remoteParts[i].replaceAll("\\D", "")) : 0;

            if (remotePart > localPart) {
                return true;  // Newer remote version is always an update
            }
            if (remotePart < localPart) {
                return false; // Remote is older than local
            }
        }

        // Versions are equal. Compare build dates next.
        if (!remote.buildDate().isEmpty() && !local.buildDate().isEmpty()) {
            int dateCompare = remote.buildDate().compareTo(local.buildDate());
            if (dateCompare > 0) {
                return true;  // Remote is newer
            }
            if (dateCompare < 0) {
                return false; // Local is newer
            }
        }

        // Versions and dates are equal. Compare build numbers.
        boolean isLocalDev = local.buildNumber().startsWith("LOC") || local.buildNumber().equals("DEV");
        boolean isRemoteDev = remote.buildNumber().startsWith("LOC") || remote.buildNumber().equals("DEV");

        // Local developer builds always override the remote build number
        if (isLocalDev) {
            return false;
        }
        // Remote dev builds win over any formal local build
        if (isRemoteDev) {
            return true;
        }

        // Compare formal numeric builds
        try {
            if (!local.buildNumber().isEmpty() && !remote.buildNumber().isEmpty()) {
                int localNum = Integer.parseInt(local.buildNumber());
                int remoteNum = Integer.parseInt(remote.buildNumber());
                return remoteNum > localNum;
            }
        } catch (NumberFormatException ignored) {}

        return false;
    }

    public static void findUpdates(List<Plugin> plugins) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (Plugin plugin : plugins) {
                    try {
                        plugin.getProperties().put(
                            PLUGIN_HAS_UPDATE,
                            checkUpdateAvailable(plugin)
                        );
                    } catch (Exception e) {
                        log.warning(
                            String.format("Can't find an update for %s, %s ", plugin.toString(), e.getMessage())
                        );
                    }
                }
                return null;
            }
        };
        worker.execute();
    }

}

