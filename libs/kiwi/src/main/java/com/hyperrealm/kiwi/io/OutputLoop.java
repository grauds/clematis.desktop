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

package com.hyperrealm.kiwi.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * A class for redirecting a program's standard output stream back into
 * itself. An output loop can be used to redirect the output of
 * <code>println()</code> methods to a graphical console or log file.
 *
 * @author Mark Lindner
 */

public class OutputLoop {

    private PipedInputStream pipein;

    private PipedOutputStream pipeout;

    private PrintStream oldout, newout;

    /**
     * Construct a new <code>OutputLoop</code>.
     *
     * @throws java.io.IOException If an error occurred while creating the
     *                             pipe.
     */

    public OutputLoop() throws IOException {

        pipeout = new PipedOutputStream();
        pipein = new PipedInputStream(pipeout);

        oldout = System.out;
        newout = new PrintStream(pipeout, false, StandardCharsets.UTF_8.name()); // un-deprecated in JDK 1.2
    }

    /**
     * Get the stream from which output can be read.
     */

    public InputStream getInputStream() {
        return (pipein);
    }

    /**
     * Turn the loop on or off. While the loop is on, standard output is
     * directed to the loop; if it is off, it's directed to the console.
     *
     * @param active A flag specifying whether the loop should be turned on
     *               or off.
     */

    public void setActive(boolean active) {
        if (active) {
            System.setOut(newout);
        } else {
            System.setOut(oldout);
        }
    }

    /**
     * Dispose of the loop. Turns the loop off, destroys the pipe, and
     * reconnects the standard output stream to the console.
     */

    public void dispose() {
        setActive(false);

        try {
            pipein.close();
            pipeout.close();
        } catch (IOException ignored) {
        } finally {
            pipein = null;
            pipeout = null;
        }
    }

}
