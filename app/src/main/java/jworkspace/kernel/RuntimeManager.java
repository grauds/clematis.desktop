package jworkspace.kernel;

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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jworkspace.api.IConstants;
import jworkspace.api.WorkspaceException;
/**
 * Runtime manager is a core component for Java Workspace to start/stop processes registered in installer
 *
 * @author Anton Troshin
 */
public final class RuntimeManager {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeManager.class);

    private static final String PROGRAMS = "programs/";

    private static final String USER_DIR = "user.dir";

    private static final String CANNOT_SET_WORKING_DIRECTORY = "Cannot set working directory";

    private static final String CANNOT_START_APPLICATION = "Cannot start application";

    private static final String CANNOT_SET_OLD_WORKING_DIRECTORY = "Cannot set old working directory";

    private static RuntimeManager instance = null;

    /**
     * The list of all external processes in Java Workspace.
     */
    private Vector<JavaProcess> processes = new Vector<>();

    /**
     * Default constructor.
     */
    private RuntimeManager() {
        super();
    }

    /**
     * Kill all processes.
     */
    public void killAllProcesses() {

        boolean alive = false;

        for (JavaProcess jp : getAllProcesses()) {
            if (jp.isAlive()) {
                alive = true;
                break;
            }
        }

        if (!alive) {
            return;
        }


        for (JavaProcess jp : getAllProcesses()) {
            if (jp != null) {
                jp.kill();
            }
        }

    }

    public static synchronized RuntimeManager getInstance() {
        if (instance == null) {
            instance = new RuntimeManager();
        }
        return instance;
    }

    /**
     * Executes program externally launching separate java process.
     *
     * @param path in workspace installer's database
     */
    private synchronized void executeExternalProcess(String path) throws WorkspaceException, IOException {

        String[] args = Workspace.getWorkspaceInstaller().getInvocationArgs(path);
        String workingDir = Workspace.getWorkspaceInstaller().getApplicationWorkingDir(path);

        executeExternalProcess(args, workingDir, trimPath(path));
    }

    /**
     * Trims path to installed java application if it was given with the "programs" root folder
     *
     * @param path to trim
     * @return trimmed path
     */
    private static String trimPath(String path) {
        if (path.startsWith(PROGRAMS)) {
            return path.substring(PROGRAMS.length());
        }
        return path;
    }

    /**
     * Executes program launching separate process.
     */
    public void executeExternalProcess(String[] args, String workingDir, String name) {

        String oldWorkingDir = System.getProperty(USER_DIR);
        String workingDirInt = workingDir;

        if (args == null) {
            return;
        }

        if (workingDirInt == null) {
            workingDirInt = System.getProperty(USER_DIR);
        } else {
            File wd = new File(workingDirInt);
            if (!wd.exists()) {
                workingDirInt = System.getProperty(USER_DIR);
            }
        }

        try {
            /*
             * Try to set working directory
             */
            if (!NativeLib.setCurrentDir(workingDir)) {
                throw new IOException(CANNOT_SET_WORKING_DIRECTORY + IConstants.WHITESPACE + workingDirInt);
            }
            /*
             * Create java process
             */
            JavaProcess process = new JavaProcess(args, name);
            /*
             * Add new element to process
             */
            processes.addElement(process);

        } catch (IOException | Error e) {
            Workspace.getUi().showError(CANNOT_START_APPLICATION, e);
        }

        try {
            /*
             * Try to set old working directory
             */
            if (!NativeLib.setCurrentDir(oldWorkingDir)) {
                Workspace.getUi().showMessage(CANNOT_SET_OLD_WORKING_DIRECTORY
                    + IConstants.WHITESPACE + oldWorkingDir);
            }

        } catch (Error err) {
            Workspace.getUi().showError(CANNOT_START_APPLICATION, err);
        }
    }

    /**
     * Execute native command
     */
    public void executeNativeCommand(String command, String workingDir) {

        String oldWorkingDir = System.getProperty(USER_DIR);

        try {
            /*
             * Try to set working directory
             */
            if (workingDir != null) {
                if (!NativeLib.setCurrentDir(workingDir)) {
                    Workspace.getUi().showMessage(CANNOT_SET_WORKING_DIRECTORY
                        + IConstants.WHITESPACE + oldWorkingDir);
                }
            }
            Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            Workspace.getUi().showError("Cannot execute native command", ex);
        }
        if (workingDir != null) {
            /*
             * Try to set old working directory
             */
            if (!NativeLib.setCurrentDir(oldWorkingDir)) {
                Workspace.getUi().showMessage(CANNOT_SET_OLD_WORKING_DIRECTORY
                    + IConstants.WHITESPACE + oldWorkingDir);
            }
        }
    }

    /**
     * This method executes application, previously configured by installer. Path is an address
     * of application configuration file, relative to /programs/ folder.
     */
    public void run(String path) throws WorkspaceException, IOException {
        executeExternalProcess(path);
    }

    /**
     * Returns list of running processes in system.
     *
     * @return jworkspace.kernel.JavaProcess[]
     */
    public JavaProcess[] getAllProcesses() {
        JavaProcess[] prs = new JavaProcess[processes.size()];
        processes.copyInto(prs);
        return prs;
    }

    /**
     * Finds process by name.
     */
    public Optional<JavaProcess> getByName(String name) {
        return processes.stream().filter(javaProcess -> javaProcess.getName().equals(name)).findAny();
    }

    /**
     * Removes terminated processes from the list.
     */
    public synchronized void removeTerminated() {

        JavaProcess[] temp = processes.stream().filter(JavaProcess::isAlive).toArray(JavaProcess[]::new);
        processes.clear();
        processes.addAll(Arrays.asList(temp));
    }

    /**
     * Removes terminated process.
     */
    public void remove(JavaProcess pr) {
        processes.removeElement(pr);
    }
}
