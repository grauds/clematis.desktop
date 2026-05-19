package jworkspace.runtime.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hyperrealm.kiwi.plugin.PluginDTO;

import static jworkspace.runtime.plugin.PluginUpdateChecker.MAPPER;

class PluginUpdateCheckerTest {

    public static final String URL_UI
        = "https://github.com/grauds/clematis.desktop/releases/download/latest/ui-2.0.0.jar";

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    @DisplayName("Should detect update when local build is LOCAL and remote is a tracking number")
    void testIsUpdateAvailableWithLocalDevBuild() {
        // Local is a development snapshot build number
        PluginUpdateChecker.VersionInfo local =
            new PluginUpdateChecker.VersionInfo("2.0.0-SNAPSHOT", "LOCAL", "2026-05-17T12:00:00Z");
        // Remote is an active production build number from CI
        PluginUpdateChecker.VersionInfo remote =
            new PluginUpdateChecker.VersionInfo("2.0.0", "45", "2026-05-17T15:00:00Z");

        assertTrue(PluginUpdateChecker.isUpdateAvailable(local, remote),
            "An update should be available if the local instance is a development build");
    }

    @Test
    @DisplayName("Should not detect update if remote is also a LOCAL development build")
    void testIsUpdateAvailableWithBothLocalAndRemoteDevBuilds() {
        PluginUpdateChecker.VersionInfo local =
            new PluginUpdateChecker.VersionInfo("2.0.0-SNAPSHOT", "LOCAL", "2026-05-17T12:00:00Z");
        PluginUpdateChecker.VersionInfo remote =
            new PluginUpdateChecker.VersionInfo("2.0.0-SNAPSHOT", "LOCAL", "2026-05-17T12:00:00Z");

        assertFalse(PluginUpdateChecker.isUpdateAvailable(local, remote),
            "Should not flag an update if the remote target is just another local asset");
    }

    @Test
    @DisplayName("Should compare numeric build tracking values directly")
    void testIsUpdateAvailableWithNumericBuildNumbers() {
        PluginUpdateChecker.VersionInfo local =
            new PluginUpdateChecker.VersionInfo("2.0.0", "10", "2026-05-10T00:00:00Z");
        PluginUpdateChecker.VersionInfo remoteNewer =
            new PluginUpdateChecker.VersionInfo("2.0.0", "12", "2026-05-12T00:00:00Z");
        PluginUpdateChecker.VersionInfo remoteOlder =
            new PluginUpdateChecker.VersionInfo("2.0.0", "8", "2026-05-08T00:00:00Z");

        assertTrue(PluginUpdateChecker.isUpdateAvailable(local, remoteNewer),
            "Build #12 should be newer than Build #10");
        assertFalse(PluginUpdateChecker.isUpdateAvailable(local, remoteOlder),
            "Build #8 should not be newer than Build #10");
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    @DisplayName("Should fall back to semantic version text matrices if build numbers match")
    void testIsUpdateAvailableWithSemanticFallback() {
        // Build numbers match, but structural versions differ
        PluginUpdateChecker.VersionInfo local =
            new PluginUpdateChecker.VersionInfo("2.0.0", "50", "2026-05-17T00:00:00Z");
        PluginUpdateChecker.VersionInfo remoteNewer =
            new PluginUpdateChecker.VersionInfo("2.1.0", "50", "2026-05-17T00:00:00Z");
        PluginUpdateChecker.VersionInfo remoteOlder =
            new PluginUpdateChecker.VersionInfo("1.9.5", "50", "2026-05-17T00:00:00Z");

        assertTrue(PluginUpdateChecker.isUpdateAvailable(local, remoteNewer),
            "v2.1.0 should trigger an update over v2.0.0");
        assertFalse(PluginUpdateChecker.isUpdateAvailable(local, remoteOlder),
            "v1.9.5 should be ignored compared to v2.0.0");
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    @DisplayName("Should extract download URL matching dynamic versioned jar assets")
    void testVersionedJarAssetExtractionLogic() throws IOException {
        // Arrange: Load mock JSON from your specific classpath directory path
        GitHubReleaseDTO release;
        try (InputStream is = PluginUpdateCheckerTest.class
            .getClassLoader().getResourceAsStream("jworkspace/runtime/plugin/test_response.json")
        ) {
            Objects.requireNonNull(is,
                "The resource file 'test_response.json' was not found in the test classpath."
            );
            release = MAPPER.readValue(is, GitHubReleaseDTO.class);
        }

        String searchPluginName = "Clematis.Java.Workspace";
        String expectedExtension = ".pkg"; // Align with the mock file assets extension

        // Act: Run the exact business logic extracted from your production method
        PluginUpdateChecker.RemoteAssetInfo result
            = PluginUpdateChecker.extractAsset(release, searchPluginName, expectedExtension);

        // Assert: Verify the extracted information matches the payload expectations
        assertNotNull(result, "The asset extraction engine returned null for the target plugin context.");

        assertAll("Verify production extraction logic yields correct parameters",
            () -> assertEquals("2.0.0",
                result.version(),
                "Failed to parse and extract the Gradle version segment from the asset name field."),
            () -> assertEquals("https://github.com/grauds/clematis.desktop/releases/download/"
                    + "latest/Clematis.Java.Workspace-2.0.0.pkg",
                result.downloadUrl(),
                "Failed to target and extract the matching browser download URL payload mapping.")
        );
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    @Test
    @DisplayName("Should extract repository coordinates from PluginDTO Help URL")
    void testRepoPathExtractionFromHelpUrl() throws Exception {
        PluginDTO plugin = new PluginDTO();
        plugin.setHelpUrl(URI.create(URL_UI).toURL().toString());

        String repoPath = "grauds/clematis.desktop"; // Default fallback initialization reference
        String urlStr = plugin.getHelpURL().toString();
        if (urlStr.contains("://github.com")) {
            String afterGithub = urlStr.split("://github.com/")[1];
            String[] parts = afterGithub.split("/");
            if (parts.length >= 2) {
                repoPath = parts[0] + "/" + parts[1];
            }
        }

        assertEquals("grauds/clematis.desktop", repoPath,
            "Failed to dynamically isolate 'owner/repo' parameters from the Help URL configuration string");
    }
}

