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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
public class LogReaderThread extends Thread {

    /** Size of the read buffer (in bytes) */
    static final int BUFFER_SIZE = 360;

    /** Associated Java process providing elapsed time information */
    private final AbstractTask task;

    /** Input stream to read process output from */
    private final InputStream stream;

    /** Reusable buffer for reading stream data */
    private final byte[] buf = new byte[BUFFER_SIZE];

    /** Target output stream where formatted log data is written */
    private final OutputStream outputStream;

    /**
     * Creates a new log reader thread.
     *
     * @param task         task wrapper used for elapsed time prefixing
     * @param stream       input stream to read (stdout or stderr)
     * @param outputStream destination stream for log output
     */
    public LogReaderThread(AbstractTask task, InputStream stream, OutputStream outputStream) {
        this.task = task;
        this.stream = stream;
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
    @Override
    public void run() {
        // Loop indefinitely until EOF or I/O error
        for (;;) {
            try {
                // Read available bytes into the buffer
                int b = stream.read(buf);

                if (b > 0) {
                    // Convert raw bytes to UTF-8 text
                    String str = new String(buf, 0, b, StandardCharsets.UTF_8);

                    // Prefix log output with elapsed process time
                    this.outputStream.write(
                        String.format("%s: %s", task.getElapsedTime(), str)
                            .getBytes(StandardCharsets.UTF_8)
                    );
                } else {
                    // End of stream reached
                    if (b < 0) {
                        break;
                    }
                }
            } catch (IOException ex) {
                // Stop the thread on any I/O error
                break;
            }
        }
    }
}

