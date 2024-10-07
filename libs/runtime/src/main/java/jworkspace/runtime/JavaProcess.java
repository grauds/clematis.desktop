package jworkspace.runtime;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2018 Anton Troshin

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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * This class represent a single java runtime process, with all required means for diagnostics and management.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@Getter
public final class JavaProcess extends AbstractTask {

    /**
     * Native process
     */
    @Setter
    private Process process;
    /**
     * Output stream for process logs
     */
    private final OutputStream logs = new BufferedOutputStream(new ByteArrayOutputStream());

    public JavaProcess(@NonNull Process process, @NonNull String name) {
        super(name);
        setProcess(process);
    }

    public JavaProcess(@NonNull String[] args, @NonNull String name) throws IOException {
        this(Runtime.getRuntime().exec(args), name);
    }

    @Override
    public void run() {
        if (process != null && process.isAlive()) {
            setStartTime(new Date());

            new ReaderThread(process.getInputStream(), this.logs).start();
            new ReaderThread(process.getErrorStream(), this.logs).start();
            try {
                int ret = process.waitFor();
                // todo add workspace listener invocation
            } catch (InterruptedException ignored) {
                /* */
            }
        }
    }

    public boolean isAlive() {
        return process != null && process.isAlive();
    }

    /**
     * Kills this process.
     */
    public void kill() {
        if (process != null) {
            process.destroy();
        }
    }

    /**
     * Reader thread to get all process log information
     */
    private class ReaderThread extends Thread {

        static final int BUFFER_SIZE = 360;

        private final InputStream stream;

        private final byte[] buf = new byte[BUFFER_SIZE];

        private final OutputStream outputStream;

        ReaderThread(InputStream stream, OutputStream outputStream) {
            this.stream = stream;
            this.outputStream = outputStream;
            setDaemon(true);
        }

        public void run() {
            for (;;) {
                try {
                    int b = stream.read(buf);
                    if (b > 0) {
                        String str = new String(buf, 0, b, StandardCharsets.UTF_8);
                        this.outputStream.write(
                            String.format("%s: %s", getElapsedTime(), str).getBytes(StandardCharsets.UTF_8)
                        );
                    } else {
                        if (b < 0) {
                            break;
                        }
                    }
                } catch (IOException ex) {
                    break;
                }
            }
        }
    }
}