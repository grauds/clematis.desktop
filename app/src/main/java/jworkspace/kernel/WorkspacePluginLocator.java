package jworkspace.kernel;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2019 Anton Troshin

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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;
import com.hyperrealm.kiwi.util.plugin.PluginLocator;

import lombok.NonNull;
/**
 * @author Anton Troshin
 * @param <T> type of plugin object to load
 */
public class WorkspacePluginLocator<T> extends PluginLocator<T> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspacePluginLocator.class);

    /**
     * Construct a new <code>PluginLocator</code> with Workspace plugin context.
     */
    public WorkspacePluginLocator() {
        super(new WorkspacePluginContext());

        addRestrictedPackage("jworkspace.kernel.*");
    }

    /**
     * Load plugins from specified directory. This method traverses directory, with all subdirectories,
     * searches for jar file and tries to load all plugins.
     *
     * @param directory path to directory
     */
    public List<Plugin<T>> loadPlugins(Path directory) {
        if (directory != null) {

            LOG.info("> Loading plugins from " + directory);
            return scanPluginsDir(directory.toFile());
        } else {

            return Collections.emptyList();
        }
    }

    private List<Plugin<T>> scanPluginsDir(@NonNull File dir) {

        List<Plugin<T>> plugins = new ArrayList<>();
        try {
            if (dir.isDirectory()) {

                File[] files = dir.listFiles();
                if (files != null) {
                    /*
                     * As there is no guarantee, that files will be in alphabetical order, lets sort
                     * directories and files.
                     */
                    Arrays.sort(files, Comparator.comparing(File::getName));

                    for (File file : files) {
                        plugins.addAll(scanPluginsDir(file));
                    }
                }
            } else if (dir.getName().endsWith("jar")) {
                Plugin<T> plugin = loadPlugin(dir, Plugin.PLUGIN_TYPE_ANY);
                plugins.add(plugin);
            }
        } catch (PluginException ex) {
            LOG.warn("Cannot load plugins from " + dir.getAbsolutePath() + " - " + ex.toString());
        }
        return plugins;
    }
}
