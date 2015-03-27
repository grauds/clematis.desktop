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
import kiwi.util.StringUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Application entry is a definition node, that stores
 * its data in file on disk, which is in file hierarchy
 * inside applications root directory. This class
 * also calculates proper classpath for java application
 * its presenting.
 */
public class Application extends DefinitionNode
{
    private String name;
    private String version;
    private String jvm;
    private String archive;
    private String mainClass;
    private String arguments;
    private String workingDir;
    private String libList = "";
    private Vector libs;
    private String description;
    private String source;
    private String docs;

    private boolean launch_at_startup = false;
    private boolean separate_process = true;
    private boolean system_user_folder = false;

    public static final Icon icon = Workspace.getResourceManager()
            .getIcon("installer/application.gif");
    private ConfigFile config;

    private static final String CK_NAME = "application.name",
    CK_VERSION = "application.version", CK_ARCHIVE = "application.archive",
    CK_SOURCE = "application.source",
    CK_JVM = "application.jvm", CK_MAINCLASS = "application.mainclass",
    CK_ARGS = "application.arguments", CK_LIBS = "application.libraries",
    CK_DESCRIPTION = "application.description",
    CK_WORKINGDIR = "application.working_dir",
    CK_DOCDIR = "application.documentation_dir",
    CK_LAUNCH_AT_STARTUP = "application.launch_at_startup",
    CK_SEPARATE_PROCESS = "application.separate_process",
    CK_SYSTEM_USER_FOLDER = "application.system_user_folder";

    /**
     * Public application constructor.
     * @param parent node jworkspace.installer.DefinitionNode
     * @param file to hold application data java.io.File
     */
    public Application(DefinitionNode parent, File file) throws IOException
    {
        super(parent, file);
        load();
        this.name = getNodeName();
    }

    /**
     * Public application constructor.
     * @param parent node jworkspace.installer.DefinitionNode
     * @param name of file to hold application data java.lang.String
     */
    public Application(DefinitionNode parent, String name)
    {
        super(parent, name + WorkspaceInstaller.FILE_EXTENSION);
        this.name = name;
    }

    /**
     * Returns path to application jar file.
     */
    public String getArchive()
    {
        return (archive);
    }

    /**
     * Returns arguments of this application.
     */
    public String getArguments()
    {
        return (arguments);
    }

    /**
     * Returns closed icon to represent
     * application in tree control.
     */
    public Icon getClosedIcon()
    {
        return (icon);
    }

    /**
     * Returns application description
     */
    public String getDescription()
    {
        return (description);
    }

    /**
     * Returns directory or jar file, containing
     * application documentation.
     */
    public java.lang.String getDocs()
    {
        return docs;
    }

    /**
     * Returns command line configured
     * to launch application.
     */
    public String[] getInvocationArgs()
    {
        Vector v = new Vector();

        // first get the VM information

        JVM jvmProg = (JVM) WorkspaceInstaller.jvmData.findNode(jvm);
        if (jvmProg == null)
            return (null);
        v.addElement(jvmProg.getPath());

        if (!system_user_folder)
            v.addElement("-Duser.home=" + System.getProperty("user.dir") + File.separator +
                         Workspace.getProfilesEngine().getPath());

        // next, construct the classpath

        String pathSeparator = System.getProperty("path.separator");
        StringBuffer sb = new StringBuffer();
        sb.append('"');
        sb.append('.');
        Enumeration e = loadLibraries();
        while (e.hasMoreElements())
        {
            Library lib = (Library) e.nextElement();
            if (sb.length() > 0)
                sb.append(pathSeparator);
            sb.append(lib.getPath());
        }

        // append the library for the program itself to the classpath

        if (sb.length() > 0)
            sb.append(pathSeparator);
        sb.append(archive);
        sb.append('"');
        String classpath = sb.toString();

        // finally, construct the full command line

        StringTokenizer st = new StringTokenizer(jvmProg.getArguments(), " ");
        while (st.hasMoreTokens())
        {
            // expand special tokens

            String arg = st.nextToken();
            if (arg.equals("%c"))
                v.addElement(classpath);
            else if (arg.equals("%m"))
                v.addElement(mainClass);
            else if (arg.equals("%a"))
            {
                String a[] = StringUtils.split(arguments, " ");
                for (int i = 0; i < a.length; i++)
                    v.addElement(a[i]);
            }
            else
                v.addElement(arg); // other stuff copies literally
        }
        String argList[] = new String[v.size()];
        v.copyInto(argList);
        return (argList);
    }

    /**
     * Returns the name of application jvm.
     */
    public String getJVM()
    {
        return (jvm);
    }

    /**
     * Returns application main class name.
     */
    public String getMainClass()
    {
        return (mainClass);
    }

    /**
     * Returns application name.
     */
    public String getName()
    {
        return (name);
    }

    /**
     * Returns open icon to represent
     * application in tree control.
     */
    public Icon getOpenIcon()
    {
        return (icon);
    }

    /**
     * Returns whether if this application uses Java Worskpace
     * user home path as value of system "user.home" property.
     */
    public boolean isSystemUserFolder()
    {
        return system_user_folder;
    }

    /**
     * Returns directory or jar file, containing
     * application source.
     */
    public java.lang.String getSource()
    {
        return source;
    }

    /**
     * Returns version of this application.
     */
    public String getVersion()
    {
        return (version);
    }

    /**
     * Returns application working directory.
     */
    public String getWorkingDirectory()
    {
        return (workingDir);
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
     * Returns whether if this application should be launched
     * at user login.
     */
    public boolean isLoadedAtStartup()
    {
        return (launch_at_startup);
    }

    /**
     * Returns whether if this application should be launched
     * in separate java virtual machine.
     */
    public boolean isSeparateProcess()
    {
        return (separate_process);
    }

    /**
     * Loads class data from configuration file
     */
    public void load() throws IOException
    {
        config = new ConfigFile(file, "Application Definition");
        config.load();
        name = config.getString(CK_NAME, "");
        version = config.getString(CK_VERSION, "");
        archive = config.getString(CK_ARCHIVE, "");
        source = config.getString(CK_SOURCE, "");
        jvm = config.getString(CK_JVM, "");
        mainClass = config.getString(CK_MAINCLASS, "");
        arguments = config.getString(CK_ARGS, "");
        libList = config.getString(CK_LIBS, "");
        docs = config.getString(CK_DOCDIR, "");
        description = config.getString(CK_DESCRIPTION, "");
        workingDir = config.getString(CK_WORKINGDIR, ".");
        launch_at_startup = config.getBoolean(CK_LAUNCH_AT_STARTUP, false);
        separate_process = config.getBoolean(CK_SEPARATE_PROCESS, false);
        system_user_folder = config.getBoolean(CK_SYSTEM_USER_FOLDER, false);
    }

    /**
     * Load libraries from configuration file.
     */
    public Enumeration loadLibraries()
    {
        libs = new Vector();
        String linkPaths[] = StringUtils.split(libList, ",");
        for (int i = 0; i < linkPaths.length; i++)
        {
            DefinitionNode node = WorkspaceInstaller.libraryData.
                    findNode(linkPaths[i]);
            if (node != null)
                libs.addElement(node);
        }
        return (libs.elements());
    }

    /**
     * Stores class data to configuration file
     */
    public void save() throws IOException
    {
        if (config == null)
            config = new ConfigFile(file, "Application definition");
        config.putString(CK_NAME, name);
        config.putString(CK_VERSION, version);
        config.putString(CK_ARCHIVE, archive);
        config.putString(CK_SOURCE, source);
        config.putString(CK_JVM, jvm);
        config.putString(CK_MAINCLASS, mainClass);
        config.putString(CK_ARGS, arguments);
        config.putString(CK_LIBS, libList);
        config.putString(CK_DOCDIR, docs);
        config.putString(CK_DESCRIPTION, description);
        config.putString(CK_WORKINGDIR, workingDir);
        config.putBoolean(CK_LAUNCH_AT_STARTUP, launch_at_startup);
        config.putBoolean(CK_SEPARATE_PROCESS, separate_process);
        config.putBoolean(CK_SYSTEM_USER_FOLDER, system_user_folder);
        config.store();
    }

    /**
     * Sets path to application jar file.
     */
    public void setArchive(String archive) throws InstallationException
    {
        if (archive == null) throw new InstallationException("Archive name is null.");
        this.archive = archive;
    }

    /**
     * Sets command line arguments for application.
     */
    public void setArguments(String arguments)
    {
        this.arguments = arguments;
    }

    /**
     * Sets description of application. This is optional,
     * as installer does not recognize this.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Sets directory or jar file, containing
     * application documentation.
     * @param docs java.lang.String
     */
    public void setDocs(java.lang.String docs)
    {
        this.docs = docs;
    }

    /**
     * Sets the name of jvm, which will be used with
     * this application.
     * @param jvm java.lang.String
     */
    public void setJVM(String jvm) throws InstallationException
    {
        if (jvm == null) throw new InstallationException("Jvm is null.");
        this.jvm = jvm;
    }

    /**
     * Sets list of libraries that are nessesary for this application.
     * @param <code>jworkspace.installer.Library<code> objects java.util.Enumeration
     */
    public void setLibraryList(Enumeration libs)
    {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        while (libs.hasMoreElements())
        {
            Library l = (Library) libs.nextElement();
            if (!first)
                sb.append(',');
            first = false;
            sb.append(l.getLinkString());
        }
        libList = sb.toString();
    }

    /**
     * Set this flag to true to allow workspace
     * to launch this application everytime
     * current user log into the system.
     */
    public void setLoadedAtStartup(boolean launch_at_startup)
    {
        this.launch_at_startup = launch_at_startup;
    }

    /**
     * Sets main class for this application. This must
     * be a fully qualified class name, for example
     * <code>java.lang.Object</code>.
     */
    public void setMainClass(String mainClass) throws InstallationException
    {
        if (mainClass == null)
        {
            throw new InstallationException("Main Class is null");
        }
        this.mainClass = mainClass;
    }

    /**
     * Sets human readable name of application.
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
     * Set this flag to true to launch new java virtual machine
     * with spawned application. Note, that option only works
     * for classes which have both main method and workspace
     * plugin API. In other words, it is impossible to launch
     * main method of external application in workspace vm.
     */
    public void setSeparateProcess(boolean separate_process)
    {
        this.separate_process = separate_process;
    }

    /**
     * Sets directory or jar file, containing
     * application source.
     */
    public void setSource(java.lang.String source)
    {
        this.source = source;
    }

    /**
     * Set this flag to true to allow to spawn new process
     * with virtual machine which has "user.home" property
     * set to which Java Workspace uses, rather than OS.
     * In this case all user data of spawned application
     * will be stored in workspace user folder.
     */
    public void setSystemUserFolder(boolean system_user_folder)
    {
        this.system_user_folder = system_user_folder;
    }

    /**
     * Sets version of application. This can be useful for
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
     * Sets application working directory. This makes
     * workspace to change working directory while launching
     * application to this path, returning afterwards
     * to its original working directory. It is nessesary
     * for spawned process, as it has to find
     * resources, relative to working directory. This is
     * the only case, then workspace uses its native
     * library.
     */
    public void setWorkingDirectory(String dir) throws InstallationException
    {
        if (dir == null)
        {
            throw new InstallationException("Working Directory is null");
        }
        this.workingDir = dir;
    }

    /**
     * Returns brief library info, that is used
     * in installer configuration dialogs.
     */
    public String toString()
    {
        return (name + " " + version);
    }
}