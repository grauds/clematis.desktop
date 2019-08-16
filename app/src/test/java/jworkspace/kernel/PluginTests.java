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
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.hyperrealm.kiwi.io.StreamUtils;
import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;
import com.hyperrealm.kiwi.util.plugin.PluginLocator;

/**
 * @author Anton Troshin
 */
public class PluginTests {

    private static final String PLUGIN_JAR = "plugin.jar";
    private static final String ANOTHER_PLUGIN_JAR = "another_plugin.jar";

    private static final String TEST_PLUGIN_CLASS_PACKAGE = "jworkspace.kernel.";
    private static final String TEST_PLUGIN_CLASS = "TestPlugin.class";
    private static final String TEST_PLUGIN_CLASS_2 = "TestPlugin2.class";
    private static final String TEST_PLUGIN = "jworkspace.kernel.TestPlugin";
    private static final String TEST_PLUGIN_2 = "jworkspace.kernel.TestPlugin2";

    private static final String TEST_PLUGIN_NAME = "Test plugin";
    private static final String TEST_PLUGIN_DESCRIPTION = "Test plugin description";
    private static final String TEST_PLUGIN_VERSION = "1.0.0";
    private static final String TEST_PLUGIN_HELP_URL = "http://test.com";
    private static final String TEST_PLUGIN_ICON = "/dummy/path.png";

    private static final String TEST_PLUGIN_NAME_2 = "Test plugin 2";
    private static final String TEST_PLUGIN_DESCRIPTION_2 = "Test plugin 2 description";
    private static final String TEST_PLUGIN_VERSION_2 = "2.0.0";
    private static final String TEST_PLUGIN_ICON_2 = "/dummy/path2.png";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();

        File sourceFile = new File(testFolder.getRoot(), TEST_PLUGIN_CLASS);
        Files.write(sourceFile.toPath(),
            StreamUtils.readStreamToByteArray(getClass().getResourceAsStream(TEST_PLUGIN_CLASS)));

        File sourceFile2 = new File(testFolder.getRoot(), TEST_PLUGIN_CLASS_2);
        Files.write(sourceFile2.toPath(),
            StreamUtils.readStreamToByteArray(getClass().getResourceAsStream(TEST_PLUGIN_CLASS_2)));

        Manifest manifest = getManifest();
        writeJarFile(manifest, PLUGIN_JAR, TEST_PLUGIN_CLASS);
        writeJarFile(manifest, ANOTHER_PLUGIN_JAR, TEST_PLUGIN_CLASS);

    }

    private void writeJarFile(Manifest manifest, String pluginJar, String testPluginClass) throws IOException {

        try (InputStream is = Files.newInputStream(Paths.get(testFolder.getRoot().getAbsolutePath(), testPluginClass));
             OutputStream os = new FileOutputStream(getPluginArchiveFile(pluginJar));
             JarOutputStream target = new JarOutputStream(os, manifest)) {

            JarEntry entry = new JarEntry(TEST_PLUGIN_CLASS_PACKAGE + testPluginClass);
            target.putNextEntry(entry);
            target.write(StreamUtils.readStreamToByteArray(is));
            target.closeEntry();
        }
    }

    private File getPluginArchiveFile(String pluginJar) {
        return new File(testFolder.getRoot(), pluginJar);
    }

    private static Manifest getManifest() {

        Manifest manifest = getManifestHeader();

        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name(Plugin.PLUGIN_NAME), TEST_PLUGIN_NAME);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_DESCRIPTION), TEST_PLUGIN_DESCRIPTION);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_VERSION), TEST_PLUGIN_VERSION);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_HELP_URL), TEST_PLUGIN_HELP_URL);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_ICON), TEST_PLUGIN_ICON);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_TYPE), Plugin.PLUGIN_TYPE_ANY);

        manifest.getEntries().put(TEST_PLUGIN_CLASS_PACKAGE + TEST_PLUGIN_CLASS, attributes);
        return manifest;
    }

    private static Manifest getManifestHeader() {

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        return manifest;
    }

    private static Manifest getManifest2() {

        Manifest manifest = getManifestHeader();

        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name(Plugin.PLUGIN_NAME), TEST_PLUGIN_NAME_2);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_DESCRIPTION), TEST_PLUGIN_DESCRIPTION_2);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_VERSION), TEST_PLUGIN_VERSION_2);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_HELP_URL), TEST_PLUGIN_HELP_URL);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_ICON), TEST_PLUGIN_ICON_2);
        attributes.put(new Attributes.Name(Plugin.PLUGIN_TYPE), Plugin.PLUGIN_TYPE_ANY);

        manifest.getEntries().put(TEST_PLUGIN_CLASS_PACKAGE + TEST_PLUGIN_CLASS_2, attributes);
        return manifest;
    }

    private static void assertPluginEqualsManifest(Plugin<ITestPlugin> testPlugin) {

        assert testPlugin.getName().equals(TEST_PLUGIN_NAME);
        assert testPlugin.getDescription().equals(TEST_PLUGIN_DESCRIPTION);
        assert testPlugin.getClassName().equals(TEST_PLUGIN);
        assert testPlugin.getVersion().equals(TEST_PLUGIN_VERSION);
        assert testPlugin.getHelpURL().toString().equals(TEST_PLUGIN_HELP_URL);
        assert testPlugin.getType().equals(Plugin.PLUGIN_TYPE_ANY);
    }

    @Test
    public void testLoadPlugin() throws PluginException {

        Plugin<ITestPlugin> testPlugin = new WorkspacePluginLocator<ITestPlugin>()
            .loadPlugin(getPluginArchiveFile(PLUGIN_JAR), Plugin.PLUGIN_TYPE_ANY);

        assertPluginEqualsManifest(testPlugin);

        testPlugin.reload();
        Object obj = testPlugin.newInstance();
        assert obj instanceof TestPlugin;

        TestPlugin plugin = (TestPlugin) obj;
        assert plugin.doPluginWork() == 2;
    }

    @Test
    public void testTwoPlugins() throws PluginException {

        final WorkspacePluginLocator<ITestPlugin> pluginLocator = new WorkspacePluginLocator<>();
        List<Plugin<ITestPlugin>> plugins = pluginLocator.loadPlugins(testFolder.getRoot().toPath());

        assert plugins.size() == 2;
        assert !plugins.get(0).equals(plugins.get(1));

        Object obj1 = plugins.get(0).newInstance();
        assert obj1 instanceof TestPlugin;

        Object obj2 = plugins.get(1).newInstance();
        assert obj2 instanceof TestPlugin;

    }

    @Test
    public void testUpdatePlugin() throws IOException, PluginException {

        final PluginLocator<ITestPlugin> pluginLocator = new WorkspacePluginLocator<>();

// create version one
        Plugin<ITestPlugin> testPlugin = pluginLocator
            .loadPlugin(getPluginArchiveFile(PLUGIN_JAR), Plugin.PLUGIN_TYPE_ANY);

        Object obj1 = testPlugin.newInstance();
        assert obj1 instanceof TestPlugin;

// update the archive by any means (may be a download from Internet)
        Manifest manifest2 = getManifest2();
        writeJarFile(manifest2, PLUGIN_JAR, TEST_PLUGIN_CLASS_2);

// create version two from the same archive
        testPlugin = pluginLocator
            .loadPlugin(getPluginArchiveFile(PLUGIN_JAR), Plugin.PLUGIN_TYPE_ANY);

        Object obj2 = testPlugin.newInstance();
        assert !(obj2 instanceof TestPlugin);
        assert obj2 instanceof TestPlugin2;

// version one is still in memory
        assert !obj1.equals(obj2);
    }

    @After
    public void after() {
        testFolder.delete();
    }
}
