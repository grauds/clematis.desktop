package jworkspace.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 Anton Troshin

   This file is part of Java Workspace.

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of
   the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU  General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   Authors may be contacted at:

   frenzy@ix.netcom.com
   tysinsh@comail.ru
   ----------------------------------------------------------------------------
*/

import jworkspace.kernel.Workspace;
import kiwi.io.ConfigFile;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * JVM entry is a definition node, that stores
 * its data in file on disk, which is in file hierarchy
 * inside virtual machines root directory.
 */
public class JVM extends DefinitionNode
{
    private String name = "";
    private String version = "";
    private String path = "";
    private String arguments = "-cp %c %m %a";
    private String description = "";
    private String docs = "";
    private String source = "";
    public static final Icon icon = Workspace.getResourceManager()
            .getIcon("installer/jvm.gif");
    private ConfigFile config;

    private static final String CK_NAME = "jvm.name",
    CK_VERSION = "jvm.version", CK_PATH = "jvm.path",
    CK_SOURCE = "jvm.source",
    CK_ARGS = "jvm.arguments",
    CK_DOCDIR = "jvm.documentation_dir",
    CK_DESCRIPTION = "jvm.description";

    /**
     * Public jvm constructor.
     * @param parent node jworkspace.installer.DefinitionNode
     * @param file to hold jvm data java.io.File
     */
    public JVM(DefinitionNode parent, File file) throws IOException
    {
        super(parent, file);
        load();
        this.name = getNodeName();
    }

    /**
     * Public jvm constructor.
     * @param parent node jworkspace.installer.DefinitionNode
     * @param name of file to hold jvm data java.lang.String
     */
    public JVM(DefinitionNode parent, String name)
    {
        super(parent, name + WorkspaceInstaller.FILE_EXTENSION);
        this.name = name;
    }

    /**
     * Returns arguments for current jvm.
     */
    public String getArguments()
    {
        return (arguments);
    }

    /**
     * Returns closed icon to represent
     * jvm in tree control.
     */
    public Icon getClosedIcon()
    {
        return (icon);
    }

    /**
     * Returns jvm description
     */
    public String getDescription()
    {
        return (description);
    }

    /**
     * Returns directory or jar file, containing
     * jvm documentation.
     */
    public java.lang.String getDocs()
    {
        return docs;
    }

    /**
     * Returns jvm nickname.
     */
    public String getName()
    {
        return (name);
    }

    /**
     * Returns open icon to represent
     * jvm in tree control.
     */
    public Icon getOpenIcon()
    {
        return (icon);
    }

    /**
     * Returns path to jvm executable.
     */
    public String getPath()
    {
        return (path);
    }

    /**
     * Returns version of this JVM.
     */
    public String getVersion()
    {
        return (version);
    }

    /**
     * Indicates that this is a leaf,
     * not a branch, as it is cannot
     * be expanded.
     */
    public boolean isExpandable()
    {
        return (false);
    }

    /**
     * Loads class data from disk file
     */
    public void load() throws IOException
    {
        config = new ConfigFile(file, "Java Virtual Machine Definition");
        config.load();
        name = config.getString(CK_NAME, "");
        version = config.getString(CK_VERSION, "");
        path = config.getString(CK_PATH, "");
        source = config.getString(CK_SOURCE, "");
        arguments = config.getString(CK_ARGS, "");
        docs = config.getString(CK_DOCDIR, "");
        description = config.getString(CK_DESCRIPTION, "");
    }

    /**
     * Stores class data to disk file
     */
    public void save() throws IOException
    {
        if (config == null)
        {
            config = new ConfigFile(file, "Java Virtual Machine Definition");
        }
        config.putString(CK_NAME, name);
        config.putString(CK_VERSION, version);
        config.putString(CK_PATH, path);
        config.putString(CK_ARGS, arguments);
        config.putString(CK_SOURCE, source);
        config.putString(CK_DOCDIR, docs);
        config.putString(CK_DESCRIPTION, description);
        config.store();
    }

    /**
     * Sets java virtual mechine arguments. This can
     * be everything supported by VM.
     * Note, that next parameters are required by
     * Java Workspace Installer to launch application
     * with all options:
     * <b>-cp %c %m %a</b>.
     * <ol>
     *  <li>%c - include classpath
     *  <li>%m - include mainclass
     *  <li>%a - include application command line parameters
     * </li>
     * If any of these three parameters are missing,
     * corresponding part of full command line, nessesary
     * to launch application, will be omitted.
     */
    public void setArguments(String arguments)
    {
        this.arguments = arguments;
    }

    /**
     * Sets description of jvm. This is optional,
     * as installer does not recognize this.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Sets directory or jar file, containing
     * jvm documentation.
     */
    public void setDocs(java.lang.String docs)
    {
        this.docs = docs;
    }

    /**
     * Sets human readable name of jvm.
     */
    public void setName(String name) throws InstallationException
    {
        if (name == null)
        {
            throw new InstallationException("Name is null");
        }
        this.name = name;
    }

    /**
     * Sets path to jvm executable file.
     * This will be a part of command line for application
     * that will choose to use this jvm.
     */
    public void setPath(String path) throws InstallationException
    {
        if (path == null)
        {
            throw new InstallationException("Path is null");
        }
        this.path = path;
    }

    /**
     * Sets version of jvm. This can be useful for
     * user, as version is not recognized by installer.
     */
    public void setVersion(String version) throws InstallationException
    {
        if (version == null)
        {
            throw new InstallationException("Version is null");
        }
        this.version = version;
    }

    /**
     * Returns brief jvm info, that is used
     * in installer configuration dialogs.
     */
    public String toString()
    {
        return (name + " " + version);
    }

    /**
     * Returns directory or jar file, containing
     * jvm source.
     */
    public java.lang.String getSource()
    {
        return source;
    }

    /**
     * Sets directory or jar file, containing
     * jvm source.
     */
    public void setSource(java.lang.String source)
    {
        this.source = source;
    }
}