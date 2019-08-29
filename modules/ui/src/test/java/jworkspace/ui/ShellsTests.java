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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.hyperrealm.kiwi.util.plugin.Plugin;
import com.hyperrealm.kiwi.util.plugin.PluginDTO;
import com.hyperrealm.kiwi.util.plugin.PluginException;

import jworkspace.kernel.WorkspacePluginLocator;

/**
 * @author Anton Troshin
 */
public class ShellsTests {

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();

        ShellHelper.preparePlugins(testFolder.getRoot());
    }

    @Test
    public void testIsLoading() throws PluginException {

        Plugin testPlugin = new WorkspacePluginLocator()
            .loadPlugin(WorkspacePluginLocator.getPluginFile(testFolder.getRoot(),
                ShellHelper.SHELL_JAR), PluginDTO.PLUGIN_TYPE_ANY);

        ShellHelper.assertPluginEqualsManifest(testPlugin);

        testPlugin.reload();
        Object obj = testPlugin.newInstance();
        assert obj instanceof TestShell;

        TestShell plugin = (TestShell) obj;
        assert plugin.getButtons().length == 0;
    }

    @After
    public void after() {
        testFolder.delete();
    }
}
