package jworkspace.ui.runtime.process;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jworkspace.api.IRuntime;
import jworkspace.api.IWorkspaceListener;
import jworkspace.config.ServiceLocator;
import jworkspace.runtime.AbstractTask;
import jworkspace.runtime.RuntimeManager;
import jworkspace.ui.config.DesktopServiceLocator;
import jworkspace.ui.runtime.LangResource;

public class ProcessesController {

    private final RuntimeManager runtimeManager;

    private final JList<AbstractTask> tasksList;

    private final ProcessesActions actions;

    public ProcessesController(
        JList<AbstractTask> tasksList,
        ProcessesActions actions
    ) {

        this.tasksList = tasksList;
        this.actions = actions;

        this.runtimeManager = ServiceLocator.getInstance().getRuntimeManager();
        initListeners();

        update();
    }

    private void initListeners() {

        runtimeManager.addListener(new IWorkspaceListener() {
                @Override
                public int getCode() {
                    return IRuntime.BEFORE_EXECUTE_EVENT;
                }

                @Override
                public void processEvent(Integer integer, Object l, Object r) {
                    SwingUtilities.invokeLater(
                        ProcessesController.this::update
                    );
                }
            }
        );

        runtimeManager.addListener(new IWorkspaceListener() {

                @Override
                public int getCode() {
                    return IRuntime.AFTER_EXECUTE_EVENT;
                }

                @Override
                public void processEvent(Integer integer, Object l, Object r) {
                    SwingUtilities.invokeLater(
                        ProcessesController.this::update
                    );
                }
            }
        );

        tasksList.addListSelectionListener(e
            -> SwingUtilities.invokeLater(()
                -> actions.enableActions(!tasksList.isSelectionEmpty())
            )
        );
    }

    public void kill() {
        List<AbstractTask> selected = tasksList.getSelectedValuesList();
        for (AbstractTask task : selected) {
            if (task != null) {
                task.stop();
            }
        }
        update();
    }

    public void killAll() {
        runtimeManager.stopAll();
        update();
    }

    public void killAndRemove() {
        List<AbstractTask> selected = tasksList.getSelectedValuesList();
        for (AbstractTask task : selected) {
            if (task != null) {
                task.stop();
                runtimeManager.remove(task);
            }
        }
        update();
    }

    public void killAndRemoveAll() {
        runtimeManager.stopAll();
        runtimeManager
            .getAllTasks()
            .forEach(runtimeManager::remove);
        update();
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    public void copyLog() {
        List<AbstractTask> selected = tasksList.getSelectedValuesList();
        if (selected.isEmpty()) {
            return;
        }

        if (selected.size() > 1) {
            JOptionPane.showMessageDialog(
                DesktopServiceLocator.getInstance().getWorkspaceGUI().getFrame(),
                LangResource.getString(
                    "message#252"
                )
            );
        } else {
            AbstractTask task = selected.getFirst();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(task.getLogs()), null
            );
        }
    }

    public void update() {

        SwingUtilities.invokeLater(() -> {
            List<AbstractTask> tasks = runtimeManager.getAllTasks();
            tasksList.setListData(
                tasks.toArray(
                    AbstractTask[]::new
                )
            );
            tasksList.repaint();
            actions.enableActions(!tasksList.isSelectionEmpty());
        });
    }
}
