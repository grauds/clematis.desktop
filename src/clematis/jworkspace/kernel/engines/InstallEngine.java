package jworkspace.kernel.engines;

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
    public static final String FILE_EXTENSION = ".dat";

    /**
     * Returns application data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getApplicationData();

    /**
     * Returns tree model for application data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public kiwi.ui.model.DynamicTreeModel getApplicationModel();

    /**
     * Returns command line for application,
     * found by its path.
     * @return java.lang.String
     * @param path java.lang.String
     */
    public java.lang.String[] getInvocationArgs(String path);

    /**
     * Returns jar file for installation.
     * @return java.lang.String
     * @param path java.lang.String
     */
    public java.lang.String getJarFile(String path);

    /**
     * Returns jvm data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getJvmData();

    /**
     * Returns tree model for jvm data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public kiwi.ui.model.DynamicTreeModel getJvmModel();

    /**
     * Returns library data.
     * @return jworkspace.installer.DefinitionDataSource
     */
    public DefinitionDataSource getLibraryData();

    /**
     * Returns tree model for library data.
     * @return kiwi.ui.model.DynamicTreeModel
     */
    public kiwi.ui.model.DynamicTreeModel getLibraryModel();

    /**
     * Returns main class for installation.
     * @return java.lang.String
     * @param path java.lang.String
     */
    public java.lang.String getMainClass(String path);

    /**
     * Returns working directory for new process.
     * @return java.lang.String
     * @param path java.lang.String
     */
    public java.lang.String getWorkingDir(String path);

    /**
     * Returns flag, that tells Workspace
     * to launch this application
     * on startup. Usually, this flag should
     * set to "true" for services like
     * network connection or system clocks.
     *
     * @return java.lang.String
     * @param path java.lang.String
     */
    public boolean isLoadedAtStartup(String path);

    /**
     * Returns flag, that tells Workspace
     * to launch this application
     * in separate process. Usually,
     * this flag should
     * set to "true" for external java
     * applications.
     *
     * @return java.lang.String
     * @param path java.lang.String
     */
    public boolean isSeparateProcess(String path);
}