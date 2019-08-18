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

import java.io.IOException;
import java.util.List;
import java.util.jar.Manifest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginException;
import com.hyperrealm.kiwi.util.plugin.PluginLocator;

/**
 * @author Anton Troshin
 */
public class PluginTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();
        PluginHelper.preparePlugins(testFolder.getRoot());
    }

    @Test
    public void testLoadPlugin() throws PluginException {

        Plugin<ITestPlugin> testPlugin = new WorkspacePluginLocator<ITestPlugin>()
            .loadPlugin(PluginHelper.getPluginFile(testFolder.getRoot(), PluginHelper.PLUGIN_JAR), Plugin.PLUGIN_TYPE_ANY);

        PluginHelper.assertPluginEqualsManifest(testPlugin);

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

        assert plugins.size() == 3;
        assert !plugins.get(0).equals(plugins.get(1));

        Object obj1 = plugins.get(0).newInstance();
        assert obj1 instanceof TestPlugin;

        Object obj2 = plugins.get(1).newInstance();
        assert obj2 instanceof TestPlugin;

        assert !obj1.equals(obj2);
    }

    @Test
    public void testUpdatePlugin() throws IOException, PluginException {

        final PluginLocator<ITestPlugin> pluginLocator = new WorkspacePluginLocator<>();

// create version one
        Plugin<ITestPlugin> testPlugin = pluginLocator
            .loadPlugin(PluginHelper.getPluginFile(testFolder.getRoot(), PluginHelper.PLUGIN_JAR), Plugin.PLUGIN_TYPE_ANY);

        Object obj1 = testPlugin.newInstance();
        assert obj1 instanceof TestPlugin;

// update the archive by any means (may be a download from Internet)
        Manifest manifest2 = PluginHelper.getManifest2();
        PluginHelper.writeJarFile(testFolder.getRoot(), PluginHelper.TEST_PLUGIN_CLASS_2, manifest2, PluginHelper.PLUGIN_JAR);

// create version two from the same archive
        testPlugin = pluginLocator
            .loadPlugin(PluginHelper.getPluginFile(testFolder.getRoot(), PluginHelper.PLUGIN_JAR), Plugin.PLUGIN_TYPE_ANY);

        Object obj2 = testPlugin.newInstance();
        assert !(obj2 instanceof TestPlugin);
        assert obj2 instanceof TestPlugin2;

// version one is still in memory
        assert !obj1.equals(obj2);
    }

    @Test
    public void testPluginsCommunication() throws PluginException {
        final PluginLocator<ITestPlugin> pluginLocator = new WorkspacePluginLocator<>();

// create version one
        Plugin<ITestPlugin> testPlugin = pluginLocator
                .loadPlugin(PluginHelper.getPluginFile(testFolder.getRoot(), PluginHelper.PLUGIN_JAR),
                        Plugin.PLUGIN_TYPE_ANY);

        Object obj1 = testPlugin.newInstance();
        assert obj1 instanceof TestPlugin;

// create version one
        Plugin<ITestPlugin> testPlugin2 = pluginLocator
                .loadPlugin(PluginHelper.getPluginFile(testFolder.getRoot(), PluginHelper.PLUGIN_JAR_2),
                        Plugin.PLUGIN_TYPE_ANY);

        Object obj2 = testPlugin2.newInstance();
        assert obj2 instanceof TestPlugin2;

        assert ((TestPlugin2) obj2).doPluginWork() == 3;
        assert ((TestPlugin) obj1).doPluginWork() == 2;
        assert ((TestPlugin2) obj2).doPluginWork() == 8;
    }
        @After
    public void after() {
        testFolder.delete();
    }
}
