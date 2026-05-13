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
import java.util.function.Supplier;

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
    private final LiveLogOutputStream liveStream = new LiveLogOutputStream(logBuffer, () -> logLock);
    private final OutputStream logs = new BufferedOutputStream(liveStream);

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

    public void clearLogs() {
        synchronized (logLock) {
            logBuffer.reset();
        }
    }

    public abstract boolean isAlive();

    public abstract void stop();

    /**
     * Stream interceptor that decouples locks to guarantee thread-safe operations.
     */
    private static final class LiveLogOutputStream extends OutputStream {
        private final OutputStream target;
        private final Supplier<Object> lockProvider;
        private Consumer<String> listener;

        LiveLogOutputStream(OutputStream target, Supplier<Object> lockProvider) {
            this.target = target;
            this.lockProvider = lockProvider;
        }

        public void setListener(Consumer<String> listener) {
            synchronized (lockProvider.get()) {
                this.listener = listener;
            }
        }

        @Override
        public void write(int b) throws IOException {
            synchronized (lockProvider.get()) {
                target.write(b);
                triggerListener(new byte[]{(byte) b}, 0, 1);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            synchronized (lockProvider.get()) {
                target.write(b, off, len);
                triggerListener(b, off, len);
            }
        }

        private void triggerListener(byte[] b, int off, int len) {
            if (listener != null) {
                String text = new String(b, off, len, StandardCharsets.UTF_8);
                listener.accept(text);
            }
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
                target.close();
            }
        }
    }
}

