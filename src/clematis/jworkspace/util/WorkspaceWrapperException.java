package jworkspace.util;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2002 Anton Troshin

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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Wrapper Exception
 *
 * Used to wrap exceptions, usually those thrown by
 * implementations of custom interfaces
 */
public class WorkspaceWrapperException extends Exception
{
    protected Exception realException;

    public WorkspaceWrapperException(Exception realException)
    {
        super(realException.getMessage());
        this.realException = realException;
    }

    public WorkspaceWrapperException(Exception realException, String appendMsg)
    {
        super(realException.getMessage() + " " + appendMsg);
        this.realException = realException;
    }

    public Exception getRealException()
    {
        return realException;
    }

    public void printStackTrace()
    {
        System.err.println("Wrapped Exception:");
        realException.printStackTrace();
        super.printStackTrace();
    }

    public void printStackTrace(PrintStream s)
    {
        s.println("Wrapped Exception:");
        realException.printStackTrace(s);
        super.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter w)
    {
        w.println("Wrapped Exception:");
        realException.printStackTrace(w);
        super.printStackTrace(w);
    }
}
