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
import java.util.function.Consumer;

/**
 * Agnostic data contract for components capable of supplying streaming log output.
 */
public interface LogStreamProvider {
    /**
     * Retrieves accumulated logs.
     */
    String getLogs();

    /**
     * Attaches a listener to capture real-time streaming updates.
     * Pass null to detach the listener.
     */
    void setStreamListener(Consumer<String> listener);
}
