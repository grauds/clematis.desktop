package com.hyperrealm.kiwi.io;
/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

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
import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Anton Troshin
 */
public class FilesystemTraverserTest {

    private static final String AFOLDER = "A";
    private static final String BFOLDER = "B";
    private static final String CFOLDER = "C";
    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();
        testFolder.newFolder(AFOLDER, BFOLDER, CFOLDER, "D");
        testFolder.newFolder(AFOLDER, BFOLDER, CFOLDER, "E");
        testFolder.newFolder(AFOLDER, BFOLDER, "W", "Z");
    }

    @Test
    public void traverse() {
        FilesystemTraverser filesystemTraverser = new FilesystemTraverser(testFolder.getRoot(), new FileConsumer() {
            @Override
            public boolean accept(File file) {
                return true;
            }

            @Override
            public boolean accessError(File file) {
                return true;
            }
        });
        filesystemTraverser.traverse();
    }

    @After
    public void after() {
        testFolder.delete();
    }
}