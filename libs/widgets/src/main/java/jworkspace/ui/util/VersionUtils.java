package jworkspace.ui.util;

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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionUtils {

    private VersionUtils() {}

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static String getBuildString(Class<?> clazz) {
        String version = clazz.getPackage().getImplementationVersion();
        String buildNum = "LOCAL";
        String buildDate = "";

        try {
            String className = clazz.getSimpleName() + ".class";
            URL classUrl = clazz.getResource(className);

            if (classUrl != null && classUrl.toString().startsWith("jar")) {
                String classPath = classUrl.toString();
                String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
                try (InputStream is = URI.create(manifestPath).toURL().openStream()) {
                    Manifest manifest = new Manifest(is);
                    Attributes attr = manifest.getMainAttributes();

                    if (version == null) {
                        version = attr.getValue("Implementation-Version");
                    }
                    buildNum = attr.getValue("Build-Number");
                    buildDate = attr.getValue("Build-Date");

                    // Keep the date string clean by dropping the timezone if it's too long
                    if (buildDate != null && buildDate.contains("T")) {
                        buildDate = buildDate.split("T")[0]; // Just shows "YYYY-MM-DD"
                    }
                }
            }
        } catch (Exception ignored) {
            // Fallback gracefully if the manifest can't be opened
        }

        if (version == null) {
            version = "Development Build";
        }

        return buildDate != null && !buildDate.isEmpty()
            ? String.format("%s (Build #%s • %s)", version, buildNum, buildDate)
            : String.format("%s (Build #%s)", version, buildNum);
    }
}
