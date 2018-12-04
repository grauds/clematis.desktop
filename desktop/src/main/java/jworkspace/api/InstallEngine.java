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

import com.hyperrealm.kiwi.ui.model.DefaultKTreeModel;

import jworkspace.installer.DefinitionDataSource;

/**
 * Installation systems of Java Workspace
 * should implement this interface as it provides minimum
 * services for Java Workspace Kernel and other engines.
 */
public interface InstallEngine extends IEngine
{
    /**
     * File extension for configuration file.
     */
    String FILE_EXTENSION = ".dat";

    /**
     * Returns application data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    DefinitionDataSource getApplicationData();

    /**
     * Returns tree model for application data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    DefaultKTreeModel getApplicationModel();

    /**
     * Returns command line for application,
     * found by its path.
     * @return String
     * @param path String
     */
    String[] getInvocationArgs(String path);

    /**
     * Returns jar file for installation.
     * @return String
     * @param path String
     */
    String getJarFile(String path);

    /**
     * Returns jvm data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    DefinitionDataSource getJvmData();

    /**
     * Returns tree model for jvm data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    DefaultKTreeModel getJvmModel();

    /**
     * Returns library data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    DefinitionDataSource getLibraryData();

    /**
     * Returns tree model for library data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    DefaultKTreeModel getLibraryModel();

    /**
     * Returns main class for installation.
     * @return String
     * @param path String
     */
    String getMainClass(String path);

    /**
     * Returns working directory for new process.
     * @return String
     * @param path String
     */
    String getWorkingDir(String path);

    /**
     * Returns flag, that tells Workspace to launch this application on startup. Usually, this flag should
     * set to "true" for services like network connection or system clocks.
     *
     * @return String
     * @param path String
     */
    boolean isLoadedAtStartup(String path);

    /**
     * Returns flag, that tells Workspace to launch this application in separate process. Usually,
     * this flag should set to "true" for external java applications.
     *
     * @return String
     * @param path String
     */
    boolean isSeparateProcess(String path);
}