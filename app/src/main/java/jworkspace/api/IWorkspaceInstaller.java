package jworkspace.api;

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

import java.util.List;

import jworkspace.installer.Application;
import jworkspace.installer.DefinitionDataSource;
import jworkspace.installer.Library;

/**
 * Installation systems of Java Workspace
 * should implement this interface as it provides minimum
 * services for Java Workspace Kernel and other engines.
 *
 * @author Anton Troshin
 */
public interface IWorkspaceInstaller extends WorkspaceComponent {

    /**
     * Returns application data.
     *
     * @return jworkspace.installer.DefinitionDataSource
     */
    DefinitionDataSource getApplicationData();

    /**
     * Returns command line for application,
     * found by its path.
     *
     * @param path String
     * @return String
     */
    String[] getInvocationArgs(String path) throws WorkspaceException;

    /**
     * Returns jvm data.
     *
     * @return jworkspace.installer.DefinitionDataSource
     */
    DefinitionDataSource getJvmData();

    /**
     * Returns library data.
     *
     * @return jworkspace.installer.DefinitionDataSource
     */
    DefinitionDataSource getLibraryData();

    /**
     * Get the list of application libraries.
     *
     * @param application to get libraries of
     * @return a list of libraries
     */
    List<Library> getApplicationLibraries(Application application);

    /**
     * Returns command line configured to launch application.
     *
     * @param application application
     */
    String[] getInvocationArgs(Application application);

    /**
     * Returns working directory for new process.
     *
     * @param path String
     * @return String
     */
    String getApplicationWorkingDir(String path) throws WorkspaceException;

}