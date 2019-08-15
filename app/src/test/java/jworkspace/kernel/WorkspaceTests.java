package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2019 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   VERSION 2 of the License, or (at your option) any later VERSION.

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;
import com.hyperrealm.kiwi.util.plugin.PluginLocator;

import jworkspace.ui.WorkspacePluginContext;

/**
 * @author Anton Troshin
 */
@RunWith(PowerMockRunner.class)
public class WorkspaceTests {

    private static final String PLUGIN_JAR = "plugin.jar";
    private static final String TEST_PLUGIN_CLASS = "TestPlugin.class";
    private static final String TEST_PLUGIN_CLASS_PACKAGE = "jworkspace.kernel.";
    private static final String TEST_PLUGIN = "jworkspace.kernel.TestPlugin";
    private static final String PLUGIN_TYPE_ANY = "ANY";
    private static final String TEST_PLUGIN_NAME = "Test plugin";
    private static final String TEST_PLUGIN_DESCRIPTION = "Test plugin description";
    private static final String TEST_PLUGIN_VERSION = "1.0.0";
    private static final String TEST_PLUGIN_HELP_URL = "http://test.com";
    private static final String TEST_PLUGIN_ICON = "/dummy/path.png";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final TemporaryFolder testFolder = new TemporaryFolder();
    private File pluginArchiveFile;

    @Before
    public void before() throws IOException {
        testFolder.create();

        File sourceFile = new File(testFolder.getRoot(), TEST_PLUGIN_CLASS);
        Files.write(sourceFile.toPath(),
            StreamUtils.readStreamToByteArray(getClass().getResourceAsStream(TEST_PLUGIN_CLASS)));

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name(Plugin.PLUGIN_NAME), TEST_PLUGIN_NAME);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_DESCRIPTION), TEST_PLUGIN_DESCRIPTION);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_VERSION), TEST_PLUGIN_VERSION);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_HELP_URL), TEST_PLUGIN_HELP_URL);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_ICON), TEST_PLUGIN_ICON);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_TYPE), PLUGIN_TYPE_ANY);

        manifest.getEntries().put(TEST_PLUGIN_CLASS_PACKAGE + TEST_PLUGIN_CLASS, attributes);

        InputStream is = Files.newInputStream(Paths.get(testFolder.getRoot().getAbsolutePath(), TEST_PLUGIN_CLASS));
        pluginArchiveFile = new File(testFolder.getRoot(), PLUGIN_JAR);

        try (OutputStream os = new FileOutputStream(pluginArchiveFile);
             JarOutputStream target = new JarOutputStream(os, manifest)) {

            JarEntry entry = new JarEntry(TEST_PLUGIN_CLASS_PACKAGE + TEST_PLUGIN_CLASS);
            target.putNextEntry(entry);
            target.write(StreamUtils.readStreamToByteArray(is));
            target.closeEntry();
        }
    }

    /**
     * System plugins are not aware of user profiles and are the same for all
     * workspaces. They are used to extend the core functionality
     */
    @Test
    public void testSystemPlugins() throws PluginException, IOException {
        Plugin testPlugin = new PluginLocator(new WorkspacePluginContext())
            .loadPlugin(pluginArchiveFile, PLUGIN_TYPE_ANY);

        assert testPlugin.getName().equals(TEST_PLUGIN_NAME);
        assert testPlugin.getDescription().equals(TEST_PLUGIN_DESCRIPTION);
        assert testPlugin.getClassName().equals(TEST_PLUGIN);
        assert testPlugin.getVersion().equals(TEST_PLUGIN_VERSION);
        assert testPlugin.getHelpURL().toString().equals(TEST_PLUGIN_HELP_URL);
        assert testPlugin.getType().equals(PLUGIN_TYPE_ANY);

        testPlugin.reload();

        // we can't cast to a class loaded with a parent classloader and call plugin directly
        thrown.expect(ClassCastException.class);
        assert ((ITestPlugin) testPlugin.newInstance()).doPluginWork() == 2;
    }

}
