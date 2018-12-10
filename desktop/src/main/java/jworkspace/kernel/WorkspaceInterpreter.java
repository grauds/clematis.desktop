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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import bsh.EvalError;
import bsh.Interpreter;
import jworkspace.util.WorkspaceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class WorkspaceInterpreter {

    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceInterpreter.class);
    /**
     * Single instance
     */
    private static WorkspaceInterpreter ourInstance;
    /**
     * Bean Shell interpreter for scripted methods invoked from different command sources like desktop shortcuts.
     */
    private Interpreter interpreter = null;
    /**
     * Console is being executed in the separate thread
     */
    private Thread interpreterThread = null;
    /**
     * Stream for interpreter
     */
    private PipedOutputStream outPipe;
    /**
     * Stream for interpreter
     */
    private InputStream in;

    public synchronized static WorkspaceInterpreter getInstance() {

        if (ourInstance == null) {
            ourInstance = new WorkspaceInterpreter();
            ourInstance.startInterpreter();
        }
        return ourInstance;
    }

    /**
     * Re/starts interpreter thread
     */
    private synchronized void startInterpreter() {

        WorkspaceInterpreter.LOG.info(">" + "Starting Bean Shell Interpreter");
        outPipe = new PipedOutputStream();
        try {
            in = new PipedInputStream(outPipe);
        } catch (IOException e) {
            WorkspaceError.exception("Cannot start Bean Shell Interpreter", e);
        }
        interpreter = new Interpreter(new InputStreamReader(in),
            System.out, System.out, false, null);

        interpreterThread = new Thread(interpreter);
        interpreterThread.start();
        WorkspaceInterpreter.LOG.info(">" + "Bean Shell Interpreter succefully started");
    }

    /**
     * Executes script file
     */
    public void sourceScriptFile(String file_name) {
        try {
            if (!isAlive()) {
                startInterpreter();
            }
            interpreter.source(file_name);
        } catch (EvalError | IOException ex) {
            WorkspaceError.exception("Cannot interpret" + " " + file_name, ex);
        }
    }

    /**
     * Executes script
     */
    public void executeScript(String command_line) {
        try {
            if (!isAlive()) {
                startInterpreter();
            }
            if (!command_line.endsWith(";")) {
                command_line = command_line + ";";
            }
            outPipe.write(command_line.getBytes());
            outPipe.flush();
        } catch (IOException e) {
            WorkspaceError.exception("Cannot interpret" + " " + command_line, e);
        }
    }

    public synchronized boolean isAlive() {
        return interpreterThread.isAlive();
    }
}

