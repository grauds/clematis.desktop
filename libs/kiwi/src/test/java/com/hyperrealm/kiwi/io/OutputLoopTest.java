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
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Anton Troshin
 */
public class OutputLoopTest {

    private static final String TEXT = "This is a test string, Ceci est une chaîne de test, Это тестовая строка";

    private final TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        testFolder.create();
    }

    @Test
    @SuppressWarnings({"Regexp", "checkstyle:MagicNumber"})
    public void testLoop() throws IOException, InterruptedException {
        OutputLoop outputLoop = new OutputLoop();
        outputLoop.setActive(true);
        Thread thread = new Thread(() -> {

            try (InputStream is = outputLoop.getInputStream()) {
                final String string = StreamUtils.readStreamToString(is);
                outputLoop.dispose();
                assertEquals(TEXT, string);

            } catch (Exception | Error e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });
        thread.start();
        System.out.print(TEXT);
        System.out.close();
        thread.join();
    }

    @After
    public void after() {
        testFolder.delete();
    }
}