package jworkspace.ui.resources;
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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.CompletableFuture;

import javax.swing.SwingUtilities;

import com.hyperrealm.kiwi.runtime.Task;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;

import jworkspace.ui.config.DesktopServiceLocator;
import lombok.Getter;

public class TaskTrackerComponent {

    /**
     * Progress dialog for observing shells load.
     */
    @Getter
    private final ProgressDialog progressDialog;
    /**
     * Worker task
     */
    private final Task task;

    public TaskTrackerComponent(Task task, String title) {
        this.task = task;

        progressDialog = new ProgressDialog(
            DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(), title, true
        );
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        progressDialog.setLocation(
            (screenSize.width - progressDialog.getWidth()) / 2,
            (screenSize.height - progressDialog.getHeight()) / 2
        );

        this.task.addProgressObserver(progressDialog);
    }

    public CompletableFuture<Void> runAndTrack() {
        // Create a future that represents the complete lifecycle
        CompletableFuture<Void> future = new CompletableFuture<>();

        // Schedule the dialog tracking on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                progressDialog.track(this.task);

                // If the dialog setup triggers the task execution,
                // complete the future when the task is actually done.
                // (Assuming task has a way to signal completion, or blocks here)
                future.complete(null);

            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        // Return the future immediately so the caller can chain actions
        return future;
    }
}
