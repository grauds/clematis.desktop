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

package com.hyperrealm.kiwi.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * An implementation of <code>LoggingEndpoint</code> for ordinary files.
 *
 * @author Mark Lindner
 */

public class FileLoggingEndpoint implements LoggingEndpoint {

    private static final String[] TYPES = {"INFO   ", "STATUS ", "WARNING", "ERROR  "};

    private static final int MAX_TYPE = 3;

    private BufferedWriter out;

    /**
     * Construct a new <code>FileLoggingEndpoint</code>.
     *
     * @param filename The name of the file to which log messages will be
     *                 written.
     * @throws java.io.IOException If the file could not be opened for
     *                             writing.
     */

    public FileLoggingEndpoint(String filename) throws IOException {
        FileOutputStream f = new FileOutputStream(filename, true);
        out = new BufferedWriter(new OutputStreamWriter(f, StandardCharsets.UTF_8));
    }

    /**
     * Write a message to the log file.
     *
     * @param type    The message type; one of the static constants defined in
     *                <code>LoggingEndpoint</code>.
     * @param message The message to be written.
     */

    public void logMessage(int type, String message) {

        int typeInt = type;

        if ((type < 0) || (type > MAX_TYPE)) {
            typeInt = 1;
        }

        try {
            out.write(TYPES[typeInt] + " - " + message);
            out.newLine();
            out.flush();
        } catch (IOException ignored) {
        }
    }

    /**
     * Close the log file. Once the file is closed, this logging endpoint can no
     * longer be used.
     */

    public void close() {
        try {
            out.close();
        } catch (IOException ignored) {
        }
        out = null;
    }

}
