package jworkspace.ui;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import com.hyperrealm.kiwi.plugin.Plugin;
import com.hyperrealm.kiwi.plugin.PluginDTO;
import com.hyperrealm.kiwi.plugin.PluginException;

import jworkspace.runtime.WorkspacePluginLocator;
import jworkspace.ui.api.Constants;
import jworkspace.ui.config.UIConfig;

/**
 * @author Anton Troshin
 */
public class ShellsTests {

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeEach
    public void before() throws IOException {
        testFolder.create();
        ShellHelper.preparePlugins(testFolder.getRoot());
    }

    @Test
    public void testIsLoading() throws PluginException, IOException {

        Plugin testPlugin = new WorkspacePluginLocator()
            .loadPlugin(
                WorkspacePluginLocator.getPluginFile(
                    testFolder.getRoot(), ShellHelper.SHELL_JAR
                ), PluginDTO.PLUGIN_TYPE_ANY
            );

        Object obj = testPlugin.newInstance();

        Assertions.assertNotEquals(ClassLoader.getSystemClassLoader(), obj.getClass().getClassLoader());

        ShellHelper.assertPluginEqualsManifest(testPlugin);

        testPlugin.reload();
        Assertions.assertEquals("jworkspace.ui.TestShell", obj.getClass().getName());

        ((ITestShell) obj).setPath(testFolder.getRoot().getPath());
        ((ITestShell) obj).load();
    }

    @Test
    public void testChildIsLoading() throws PluginException {

        Plugin testPlugin = new WorkspacePluginLocator()
            .loadPlugin(
                WorkspacePluginLocator.getPluginFile(
                    testFolder.getRoot(), ShellHelper.CHILD_SHELL_JAR
                ),
                PluginDTO.PLUGIN_TYPE_ANY
            );

        Object obj = testPlugin.newInstance();

        Assertions.assertNotEquals(ClassLoader.getSystemClassLoader(), obj.getClass().getClassLoader());
        ShellHelper.assertPluginEqualsChildManifest(testPlugin);

        testPlugin.reload();
        Assertions.assertEquals("jworkspace.ui.ChildTestShell", obj.getClass().getName());
    }

    @Test
    public void testConfig() {

        UIConfig uiConfig = new UIConfig(testFolder.getRoot().toPath().resolve(Constants.CONFIG_FILE).toFile());

        uiConfig.saveLaf();
        uiConfig.setKiwiTextureVisible(true);
        uiConfig.setTexture(WorkspaceGUI.getResourceManager().getImage("test_texture.gif"));
        uiConfig.setTextureVisible(true);

        uiConfig.save();

        uiConfig = new UIConfig(testFolder.getRoot().toPath().resolve(Constants.CONFIG_FILE).toFile());
        uiConfig.load();

        assert uiConfig.getTexture() != null;
        assert uiConfig.isTextureVisible();
        assert uiConfig.isKiwiTextureVisible();
        assert !uiConfig.isDecorated();
    }

    @AfterEach
    public void after() {
        testFolder.delete();
    }
}
