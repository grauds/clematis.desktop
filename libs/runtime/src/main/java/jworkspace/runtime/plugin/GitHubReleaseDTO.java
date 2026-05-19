package jworkspace.runtime.plugin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Maps the core payload from the GitHub /releases/latest endpoint.
 */
@SuppressWarnings("checkstyle:JavadocType")
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubReleaseDTO(
    @JsonProperty("id") Long id,
    @JsonProperty("tag_name") String tagName,
    @JsonProperty("name") String name,
    @JsonProperty("prerelease") boolean prerelease,
    @JsonProperty("published_at") String publishedAt,
    @JsonProperty("assets") List<GitHubAssetDTO> assets
) {}
