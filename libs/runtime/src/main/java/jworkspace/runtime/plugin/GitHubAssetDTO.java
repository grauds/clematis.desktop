package jworkspace.runtime.plugin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps individual entries within the "assets" array.
 */
@SuppressWarnings("checkstyle:JavadocType")
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubAssetDTO(
    @JsonProperty("id") Long id,
    @JsonProperty("name") String name,
    @JsonProperty("size") Long size,
    @JsonProperty("content_type") String contentType,
    @JsonProperty("browser_download_url") String browserDownloadUrl
) {}
