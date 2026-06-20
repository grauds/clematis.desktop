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
