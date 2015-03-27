package jworkspace.kernel;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin
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
   tysinsh@comail.ru
  ----------------------------------------------------------------------------
 */
import jworkspace.util.WorkspaceError;
import java.io.*;
import bsh.Interpreter;

public final class WorkspaceInterpreter
{
    /**
     * Built-in iterpretator. Executes
     * scripted methods invoked from different
     * command sources like desktop shortcuts.
     */
    private Interpreter interpreter = null;
    private Thread interpreter_thread = null;
    /**
     * Single instance
     */
    private static WorkspaceInterpreter ourInstance;
    /**
     * Stream for interpreter
     */
    private PipedOutputStream outPipe;
    /**
     * Stream for interpreter
     */
    private InputStream in;
    public synchronized static WorkspaceInterpreter getInstance()
    {
        if (ourInstance == null)
        {
            ourInstance = new WorkspaceInterpreter();
            ourInstance.startInterpreter();
        }
        return ourInstance;
    }
    /**
      * Re/starts interpreter thread
      */
     public synchronized void startInterpreter()
     {
         Workspace.getLogger().info(">" + "Starting Bean Shell Interpreter");
         outPipe = new PipedOutputStream();
         try
         {
             in = new PipedInputStream(outPipe);
         }
         catch (IOException e)
         {
             WorkspaceError.exception("Cannot start Bean Shell Interpreter", e);
         }
         interpreter = new Interpreter(new InputStreamReader(in),
                                       System.out, System.out, false, null);

         interpreter_thread = new Thread(interpreter);
         interpreter_thread.start();
         Workspace.getLogger().info(">" + "Bean Shell Interpreter succefully started");
     }

    /**
     * Executes script file
     */
    public void sourceScriptFile(String file_name)
    {
        try
        {
            if ( ! isInterpreterAlive() )
            {
                startInterpreter();
            }
            interpreter.source(file_name);
        }
        catch (FileNotFoundException ex)
        {
            WorkspaceError.exception("Cannot interpret" + " " + file_name, ex);
        }
        catch (bsh.EvalError err)
        {
            WorkspaceError.exception("Cannot interpret" + " " + file_name, err);
        }
        catch (IOException ex)
        {
            WorkspaceError.exception("Cannot interpret" + " " + file_name, ex);
        }
    }
   /**
     * Executes script
     */
    public void executeScript(String command_line)
    {
        try
        {
            if ( ! isInterpreterAlive() )
            {
                startInterpreter();
            }
            if ( !command_line.endsWith(";") )
            {
                command_line = command_line + ";";
            }
            outPipe.write(command_line.getBytes());
            outPipe.flush();
        }
        catch (IOException e)
        {
            WorkspaceError.exception("Cannot interpret" + " " + command_line, e);
        }
    }

    /**
     * This checks if interpreter thread is alive.
     */
    public synchronized boolean isInterpreterAlive()
    {
        return interpreter_thread.isAlive();
    }
}

