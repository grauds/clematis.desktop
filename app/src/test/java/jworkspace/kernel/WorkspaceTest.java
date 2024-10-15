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
import java.nio.file.Path;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import com.hyperrealm.kiwi.plugin.PluginException;

import jworkspace.Workspace;
import jworkspace.users.Profile;
import jworkspace.users.ProfileOperationException;
import jworkspace.users.ProfilesManager;

/**
 * @author Anton Troshin
 */
public class WorkspaceTest {

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeEach
    public void before() throws IOException {
        testFolder.create();
    }

    @Test
    public void testBeginWork() throws ProfileOperationException, PluginException, IOException {
        Path basePath = Path.of(testFolder.getRoot().getPath());

        ProfilesManager profilesManager = new ProfilesManager(basePath);

        String testProfileName = "test";
        String password = "password";

        Profile testProfile = new Profile(testProfileName);
        testProfile.setPassword(password);
        profilesManager.add(testProfile);

        Assertions.assertEquals(1, Objects.requireNonNull(testFolder.getRoot().listFiles()).length);

        Workspace.start(testProfileName, password, basePath);

    }

    @AfterEach
    public void after() {
        testFolder.delete();
    }
}
