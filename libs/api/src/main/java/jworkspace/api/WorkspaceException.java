package jworkspace.api;

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

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/


import lombok.Getter;

/**
 * An exception with ability to wrap a source exception and be caught by a single try/catch.
 * May be used to show the source exception to user or to log it.
 */
@Getter
public class WorkspaceException extends Exception {

    private Throwable wrappedException = null;

    public WorkspaceException() {
        super();
    }

    public WorkspaceException(String s) {
        super(s);
    }

    public WorkspaceException(String s, Throwable t) {
        super(s);
        wrappedException = t;
    }

    public WorkspaceException(Throwable t) {
        super();
        wrappedException = t;
    }

}
