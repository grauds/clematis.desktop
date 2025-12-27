/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 2019 Anton Troshin

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/
package com.hyperrealm.kiwi.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.Icon;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * A class is a lightweight plugin fields data transfer object
 *
 * @author Anton Troshin
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class PluginDTO {

    public static final String PLUGIN_TYPE_ANY = "ANY";

    public static final String PLUGIN_TYPE_USER = "USER";

    public static final String PLUGIN_TYPE_SYSTEM = "SYSTEM";

    public static final String PLUGIN_NAME = "PluginName";

    public static final String PLUGIN_TYPE = "PluginType";

    public static final String PLUGIN_DESCRIPTION = "PluginDescription";

    public static final String PLUGIN_ICON = "PluginIcon";

    public static final String PLUGIN_VERSION = "PluginVersion";

    public static final String PLUGIN_HELP_URL = "PluginHelpURL";

    protected String className;

    protected String name;

    protected String type;

    protected String description;

    protected String iconFile;

    protected String version;

    protected String jarFile;

    protected Icon icon = null;

    protected URL helpURL = null;

    PluginDTO(String type, String jarFile) {
        this.type = type;
        this.jarFile = jarFile;
    }

    public PluginDTO(String className, String name, String type,
                     String description, String iconFile, String version,
                     String helpUrl) {

        this.className = className;
        this.name = name;
        this.type = type;
        this.description = description;
        this.iconFile = iconFile;
        this.version = version;
        setHelpUrl(helpUrl);
    }

    @SuppressFBWarnings("DE_MIGHT_IGNORE")
    public void setHelpUrl(String helpUrl) {
        try {
            helpURL = new URL(helpUrl);
        } catch (MalformedURLException ex) { /* ignore */ }
    }

    public static Manifest getManifestHeader() {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        return manifest;
    }

    public static Manifest getManifest(@NonNull PluginDTO plugin) {
        Manifest manifest = getManifestHeader();

        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name(PLUGIN_NAME), plugin.getName());
        attributes.put(new Attributes.Name(PLUGIN_DESCRIPTION), plugin.getDescription());
        attributes.put(new Attributes.Name(PLUGIN_VERSION), plugin.getVersion());
        attributes.put(new Attributes.Name(PLUGIN_HELP_URL), plugin.getHelpURL().toString());
        attributes.put(new Attributes.Name(PLUGIN_ICON), plugin.getIcon());
        attributes.put(new Attributes.Name(PLUGIN_TYPE), plugin.getType());

        manifest.getEntries().put(plugin.getClassName(), attributes);
        return manifest;
    }
}
