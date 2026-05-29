package com.hyperrealm.kiwi.plugin;

import java.net.URL;

public record VersionInfo(String version, String buildNumber, String buildDate, URL url) {

    public VersionInfo(String version, String buildNumber, String buildDate) {
        this(version, buildNumber, buildDate, null);
    }
    public VersionInfo(String version, String buildNumber, String buildDate, URL url) {
        this.version = version != null ? version : "0.0.0";
        this.buildNumber = buildNumber != null ? buildNumber.trim().toUpperCase() : "LOCAL";
        this.buildDate = buildDate != null ? buildDate : "";
        this.url = url;
    }
}