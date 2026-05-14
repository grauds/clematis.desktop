package jworkspace.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import lombok.extern.java.Log;

/**
 * Background thread that continuously reads log output from a running
 * {@link AbstractTask} and forwards it to an output stream.
 *
 * <p>This thread is typically used to consume {@code stdout} or {@code stderr}
 * of an external Java process, prepend each chunk with the process elapsed
 * time, and write it to a target stream (e.g. log file, console, UI pipe).</p>
 *
 * <p>The thread is marked as daemon so it does not prevent JVM shutdown.</p>
 */
@Log
public class LogReaderThread extends Thread {

    /** Size of the read buffer (in bytes) */
    static final int BUFFER_SIZE = 360;

    /** Input stream to read process output from */
    private final InputStream inputStream;

    /** Reusable buffer for reading stream data */
    private final byte[] buf = new byte[BUFFER_SIZE];

    /** Target output stream where formatted log data is written */
    private final OutputStream outputStream;

    /**
     * Creates a new log reader thread.
     *
     * @param inputStream       input stream to read (stdout or stderr)
     * @param outputStream destination stream for log output
     */
    public LogReaderThread(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        // Daemon thread so it does not block JVM shutdown
        setDaemon(true);
    }

    /**
     * Continuously reads from the input stream until it is closed or
     * an I/O error occurs.
     *
     * <p>Each read chunk is converted to UTF-8 text, prefixed with the
     * process elapsed time, and written to the output stream.</p>
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void run() {
        try (inputStream; outputStream) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error reading log", e);
        }
    }
}

