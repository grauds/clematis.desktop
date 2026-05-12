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

import static com.hyperrealm.kiwi.util.KiwiUtils.MILLISEC_IN_SECOND;
import com.hyperrealm.kiwi.runtime.Task;

import lombok.Getter;
import lombok.Setter;
/**
 * Abstract observable task
 */
@Getter
@Setter
public abstract class AbstractTask extends Task {

    private String name;

    private Date startTime;

    private final ByteArrayOutputStream logBuffer = new ByteArrayOutputStream();

    private final OutputStream logs = new BufferedOutputStream(logBuffer);

    public AbstractTask(String name) {
        this.name = name;
    }

    protected AbstractTask() {}

    public long getElapsedTime() {
        return (System.currentTimeMillis() - getStartTime().getTime()) / MILLISEC_IN_SECOND;
    }

    public synchronized String getLogsText() {
        try {
            logs.flush();
        } catch (IOException ignored) {
            // Ignore flush issues
        }
        return logBuffer.toString(StandardCharsets.UTF_8);
    }

    public synchronized byte[] getLogsBytes() {
        try {
            logs.flush();
        } catch (IOException ignored) {
            // Ignore flush issues
        }
        return logBuffer.toByteArray();
    }

    public synchronized void clearLogs() {
        logBuffer.reset();
    }

    public abstract boolean isAlive();

    public abstract void stop();
}
