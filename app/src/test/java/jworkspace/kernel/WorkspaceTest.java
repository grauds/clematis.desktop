package jworkspace.kernel;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2019 Anton Troshin

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
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import jworkspace.api.IConstants;
import jworkspace.api.ProfileOperationException;
import jworkspace.installer.Application;
import jworkspace.api.DefinitionDataSource;
import jworkspace.api.DefinitionNode;

/**
 * @author Anton Troshin
 */
public class WorkspaceTest {

    private static final String USERNAME = "root";
    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();
        PluginHelper.preparePlugins(testFolder.getRoot(), Paths.get(testFolder.getRoot().getAbsolutePath(),
                IConstants.PLUGINS_DIRECTORY).toFile());
    }

    @Test
    public void testBeginWork() throws IOException, ProfileOperationException {
// I would like to start a new workspace
        Workspace.start(testFolder.getRoot().toPath(), USERNAME, "");
// assert user is logged in
        assert Workspace.getUserManager().getUserName().equalsIgnoreCase(USERNAME);
// create a sample application record
        DefinitionDataSource dds = Workspace.getWorkspaceInstaller().getApplicationData();

        Application testApplication = new Application(dds.getRoot(), "test_application");
        testApplication.setArchive("./application.jar");
        testApplication.setDescription("test java standalone application");
        testApplication.setDocs("path_to_docs");
        testApplication.setMainClass("jworkspace.testapp");
        testApplication.setSource("path_to_source");
        testApplication.setVersion("1.0.0");
        testApplication.setWorkingDirectory("./");
        testApplication.save();

        Workspace.changeCurrentProfile("anton", "");

        DefinitionNode testApplication2 = Workspace.getWorkspaceInstaller().getApplicationData()
                .findNode(testApplication.getLinkString());

        assert testApplication2 == null;

        Workspace.changeCurrentProfile(USERNAME, "");

        testApplication2 = Workspace.getWorkspaceInstaller().getApplicationData()
                .findNode(testApplication.getLinkString());
        assert testApplication2.equals(testApplication);

        Workspace.removeUserWorkspace();
    }

    @After
    public void after() {
        testFolder.delete();
    }
}
