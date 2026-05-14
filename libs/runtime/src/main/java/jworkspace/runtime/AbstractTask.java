package jworkspace.runtime;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2025 Anton Troshin

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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Consumer;

import static com.hyperrealm.kiwi.util.KiwiUtils.MILLISEC_IN_SECOND;
import com.hyperrealm.kiwi.runtime.Task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractTask extends Task {

    private String name;
    private Date startTime;

    private final ByteArrayOutputStream logBuffer = new ByteArrayOutputStream();

    // Explicit internal synchronization lock to separate state locking from method scopes
    private final Object logLock = new Object();

    // The stream target directly delegates to the class lock via constructor pass-through
    private LiveLogOutputStream liveStream = new LiveLogOutputStream(
        logBuffer,
        () -> logLock,
        this::getElapsedTime
    );

    private OutputStream logs = new BufferedOutputStream(liveStream);

    public AbstractTask(String name) {
        this.name = name;
    }

    protected AbstractTask() {}

    /**
     * Registers or clears the functional callback interface used for live text streaming.
     */
    public void setLogStreamListener(Consumer<String> listener) {
        synchronized (logLock) {
            this.liveStream.setListener(listener);
        }
    }

    public long getElapsedTime() {
        return (System.currentTimeMillis() - getStartTime().getTime()) / MILLISEC_IN_SECOND;
    }

    public String getLogsText() {
        synchronized (logLock) {
            try {
                logs.flush();
            } catch (IOException ignored) {
                // Ignore flush issues
            }
            return logBuffer.toString(StandardCharsets.UTF_8);
        }
    }

    public synchronized void clearLogs() {
        synchronized (logLock) {
            logBuffer.reset();
            // Re-initialize the pipeline layers if the stream was previously closed
            this.liveStream = new LiveLogOutputStream(
                logBuffer,
                () -> logLock,
                this::getElapsedTime
            );
            this.logs = new BufferedOutputStream(liveStream);
        }
    }

    public abstract boolean isAlive();

    public abstract void stop();

    /**
     * Stream interceptor that decouples locks to guarantee thread-safe operations.
     */
    private static final class LiveLogOutputStream extends OutputStream {
        private final OutputStream target;
        private final java.util.function.Supplier<Object> lockProvider;
        private final java.util.function.Supplier<Long> elapsedTimeProvider;
        private Consumer<String> listener;

        // Accumulates incoming bytes until a complete line delimiter (\n) is found
        private final ByteArrayOutputStream lineAccumulator = new ByteArrayOutputStream();

        LiveLogOutputStream(
            OutputStream target,
            java.util.function.Supplier<Object> lockProvider,
            java.util.function.Supplier<Long> elapsedTimeProvider
        ) {
            this.target = target;
            this.lockProvider = lockProvider;
            this.elapsedTimeProvider = elapsedTimeProvider;
        }

        public void setListener(Consumer<String> listener) {
            synchronized (lockProvider.get()) {
                this.listener = listener;
            }
        }

        @Override
        public void write(int b) throws IOException {
            synchronized (lockProvider.get()) {
                processByte((byte) b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            synchronized (lockProvider.get()) {
                for (int i = 0; i < len; i++) {
                    processByte(b[off + i]);
                }
            }
        }

        /**
         * Scans incoming bytes for line breaks. When a line breaks, it formats the line,
         * writes it to the historical target stream, and broadcasts it to the UI.
         */
        private void processByte(byte sign) throws IOException {
            lineAccumulator.write(sign);

            // Look for a newline marker character to finalize a complete line
            if (sign == '\n') {
                flushCurrentLine();
            }
        }

        private void flushCurrentLine() throws IOException {
            if (lineAccumulator.size() == 0) {
                return;
            }

            // Extract the raw line text (omitting the trailing newline for formatting)
            String rawLine = lineAccumulator.toString(StandardCharsets.UTF_8);

            long elapsed = elapsedTimeProvider.get();
            String formattedLine = String.format("%s: %s", elapsed, rawLine);
            byte[] formattedBytes = formattedLine.getBytes(StandardCharsets.UTF_8);

            // Commit the formatted line bytes to the persistent log history buffer
            target.write(formattedBytes);

            // Broadcast the formatted text line out to your live UI log viewers
            if (listener != null) {
                listener.accept(formattedLine);
            }

            lineAccumulator.reset();
        }

        @Override
        public void flush() throws IOException {
            synchronized (lockProvider.get()) {
                target.flush();
            }
        }

        @Override
        public void close() throws IOException {
            synchronized (lockProvider.get()) {
                // If the stream closes but the last line didn't end with a newline, flush it anyway
                if (lineAccumulator.size() > 0) {
                    lineAccumulator.write('\n');
                    flushCurrentLine();
                }
                target.close();
                lineAccumulator.close();
            }
        }
    }
}

