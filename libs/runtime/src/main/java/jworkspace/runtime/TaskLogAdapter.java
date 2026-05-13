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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class TaskLogAdapter implements LogStreamProvider {

    // Keep a shared map of broadcasters per task instance to prevent split pipelines
    private static final Map<AbstractTask, BroadcastLogListener> BROADCASTERS = new ConcurrentHashMap<>();

    private final AbstractTask task;

    public TaskLogAdapter(AbstractTask task) {
        this.task = task;
    }

    @Override
    public String getLogs() {
        return task != null ? task.getLogsText() : "";
    }

    @Override
    public void setStreamListener(Consumer<String> listener) {
        if (task == null) {
            return;
        }

        // Fetch or create a shared multiplexer for this task instance
        BroadcastLogListener broadcaster = BROADCASTERS.computeIfAbsent(task, t -> {
            BroadcastLogListener b = new BroadcastLogListener();
            t.setLogStreamListener(b); // Register the multiplexer once directly onto the task
            return b;
        });

        if (listener != null) {
            broadcaster.addTarget(listener);
        }
    }

    /**
     * Explicit unbind method to fix the instance tracking bug and prevent memory leaks.
     * Use this when a specific UI panel or view is destroyed.
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    public void removeStreamListener(Consumer<String> listener) {
        if (task == null || listener == null) {
            return;
        }

        BroadcastLogListener broadcaster = BROADCASTERS.get(task);
        if (broadcaster != null) {
            broadcaster.removeTarget(listener);

            // Atomically clean up the task mapping only if no other views are actively listening
            if (broadcaster.isEmpty()) {
                // Synchronize on the broadcaster to ensure no new views can attach mid-cleanup
                synchronized (broadcaster) {
                    if (broadcaster.isEmpty()) {
                        task.setLogStreamListener(null);
                        BROADCASTERS.remove(task, broadcaster); // Safe atomic map removal verification
                    }
                }
            }
        }
    }
}

