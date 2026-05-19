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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;

import lombok.extern.java.Log;
import tools.jackson.databind.ObjectMapper;

@Log
public class PluginUpdateChecker {

    public static final String PLUGIN_HAS_UPDATE = "HasUpdate";

    static final ObjectMapper MAPPER = new ObjectMapper();

    private PluginUpdateChecker() {}

    @SuppressWarnings({
        "checkstyle:MultipleStringLiterals",
        "checkstyle:NestedIfDepth"
    })
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

        VersionInfo localInfo = new VersionInfo(localVersion, localBuildNum, localBuildDateStr);

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
        RemoteAssetInfo remoteAsset = getLatestReleaseAsset(repoPath, plugin.getName());
        if (remoteAsset == null) {
            throw new IOException("Could not resolve asset metadata from the GitHub API release pipeline.");
        }

        // Fetch Manifest parameters securely from the streaming target URL
        VersionInfo remoteInfo = fetchRemoteVersion(remoteAsset.downloadUrl());

        // Process update evaluation algorithm
        return isUpdateAvailable(localInfo, remoteInfo);
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
    public static RemoteAssetInfo getLatestReleaseAsset(String repoPath, String pluginName) {
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
    static RemoteAssetInfo extractAsset(GitHubReleaseDTO release, String pluginName, String extension) {
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
                String version = asset.name().substring(
                    assetPrefix.length(),
                    asset.name().length() - extension.length()
                );
                return new RemoteAssetInfo(asset.browserDownloadUrl(), version);
            }
        }
        return null;
    }

    /**
     * Downloads and parses the manifest header directly from the online binary JAR stream.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public static VersionInfo fetchRemoteVersion(String jarUrlString) throws Exception {
        URL url = URI.create(jarUrlString).toURL();
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
            String version = attr.getValue("PluginVersion");
            String buildNumber = attr.getValue("Build-Number");
            String buildDate = attr.getValue("Build-Date");

            return new VersionInfo(version, buildNumber, buildDate);
        }
    }

    /**
     * Checks, validates, and acts as the download orchestrator.
     * Saves the artifact to the designated local path if an update is found.
     */
    @SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:MultipleStringLiterals", "checkstyle:NestedIfDepth"})
    public static void downloadUpdate(String localDownloadDirectory,
                                      RemoteAssetInfo remoteAsset
    ) throws Exception {

        // Extract file name sequence from download endpoint URL
        String fileName = remoteAsset.downloadUrl().substring(remoteAsset.downloadUrl().lastIndexOf('/') + 1);
        File destinationFile = Paths.get(localDownloadDirectory, fileName).toFile();

        // Download file direct to disk location mapping
        URL url = URI.create(remoteAsset.downloadUrl()).toURL();
        try (InputStream in = url.openStream()) {
            Files.copy(in, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @SuppressWarnings({
        "checkstyle:MultipleStringLiterals",
        "checkstyle:CyclomaticComplexity",
        "checkstyle:ReturnCount"
    })
    public static boolean isUpdateAvailable(VersionInfo local, VersionInfo remote) {
        boolean isLocalDev = local.buildNumber.startsWith("LOC") || local.buildNumber.equals("DEV");
        boolean isRemoteDev = remote.buildNumber.startsWith("LOC") || remote.buildNumber.equals("DEV");

        if (isLocalDev && !isRemoteDev && !remote.buildNumber.isEmpty()) {
            return true;
        }
        if (isRemoteDev) {
            return false;
        }

        try {
            int localNum = Integer.parseInt(local.buildNumber);
            int remoteNum = Integer.parseInt(remote.buildNumber);
            if (remoteNum > localNum) {
                return true;
            }
            if (remoteNum < localNum) {
                return false;
            }
        } catch (NumberFormatException ignored) {}

        String[] localParts = local.version.replace("-SNAPSHOT", "").split("\\.");
        String[] remoteParts = remote.version.replace("-SNAPSHOT", "").split("\\.");

        int length = Math.max(localParts.length, remoteParts.length);
        for (int i = 0; i < length; i++) {
            int localPart = i < localParts.length ? Integer.parseInt(localParts[i].replaceAll("\\D", "")) : 0;
            int remotePart = i < remoteParts.length ? Integer.parseInt(remoteParts[i].replaceAll("\\D", "")) : 0;

            if (remotePart > localPart) {
                return true;
            }
            if (remotePart < localPart) {
                return false;
            }
        }

        if (!remote.buildDate.isEmpty() && !local.buildDate.isEmpty()) {
            return remote.buildDate.compareTo(local.buildDate) > 0;
        }

        return false;
    }

    public static void findUpdates(List<Plugin> plugins) {
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
    }

    public record VersionInfo(String version, String buildNumber, String buildDate) {
        public VersionInfo(String version, String buildNumber, String buildDate) {
            this.version = version != null ? version : "0.0.0";
            this.buildNumber = buildNumber != null ? buildNumber.trim().toUpperCase() : "LOCAL";
            this.buildDate = buildDate != null ? buildDate : "";
        }
    }

    public record RemoteAssetInfo(String downloadUrl, String version) {}
}

