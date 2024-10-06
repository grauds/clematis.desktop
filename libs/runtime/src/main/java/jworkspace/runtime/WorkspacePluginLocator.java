package jworkspace.runtime;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;
import com.hyperrealm.kiwi.plugin.PluginException;
import com.hyperrealm.kiwi.plugin.PluginLocator;

import lombok.NonNull;
/**
 * @author Anton Troshin
 */
public class WorkspacePluginLocator extends PluginLocator {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspacePluginLocator.class);

    /**
     * Construct a new <code>PluginLocator</code> with Workspace plugin context.
     */
    public WorkspacePluginLocator() {
        super(new WorkspacePluginContext());

        addRestrictedPackage("jworkspace.kernel.*");
    }

    public static void writePluginJarFile(File classesLocation,
                                          String[] classes,
                                          Manifest manifest,
                                          File jarPath,
                                          String jarFileName)
            throws IOException {

        Files.createDirectories(jarPath.toPath());

        try (OutputStream os = Files.newOutputStream(getPluginFile(jarPath, jarFileName).toPath());
             JarOutputStream target = new JarOutputStream(os, manifest)) {

            for (String cl : classes) {

                try (InputStream is = Files.newInputStream(Paths.get(classesLocation.getAbsolutePath(), cl))) {

                    JarEntry entry = new JarEntry(cl);
                    target.putNextEntry(entry);
                    target.write(StreamUtils.readStreamToByteArray(is));
                    target.closeEntry();
                }
            }
        }
    }

    public static File getPluginFile(File folder, String file) {
        return new File(folder, file);
    }

    /**
     * Compiles input files and stores the output class files in the destination folder
     * @param input files including all dependencies
     * @param dest folder to store class files in
     */
    public static void compile(@NonNull File[] input, @NonNull File dest) {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager = compiler.
            getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compUnit =
            fileManager.getJavaFileObjectsFromFiles(Arrays.asList(input));

        Iterable<String> options = Arrays.asList("-d", dest.getAbsolutePath(), "-proc:none");
        if (!compiler
            .getTask(null, fileManager, null, options, null, compUnit).call()) {
            throw new IllegalStateException("Couldn't compile the sources");
        }
    }

    /**
     * Load plugins from specified directory. This method traverses directory, with all subdirectories,
     * searches for jar file and tries to load all plugins.
     *
     * @param directory path to directory
     */
    public List<Plugin> loadPlugins(Path directory) {
        if (directory != null) {

            LOG.info("> Loading plugins from " + directory);
            return scanPluginsDir(directory.toFile());
        } else {

            return Collections.emptyList();
        }
    }

    private List<Plugin> scanPluginsDir(@NonNull File dir) {

        List<Plugin> plugins = new ArrayList<>();
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
                Plugin plugin = loadPlugin(dir, PluginDTO.PLUGIN_TYPE_ANY);
                plugins.add(plugin);
            }
        } catch (PluginException ex) {
            LOG.warn("Cannot load plugins from " + dir.getAbsolutePath() + " - " + ex.getMessage());
        }
        return plugins;
    }
}
