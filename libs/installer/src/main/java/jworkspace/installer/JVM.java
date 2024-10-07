package jworkspace.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-1999 Mark A. Lindner,
          2000-2024 Anton Troshin

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
   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/

import java.io.File;
import java.io.IOException;

import javax.swing.Icon;

import com.hyperrealm.kiwi.io.ConfigFile;

import jworkspace.api.DefinitionNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * JVM entry is a definition node, that stores
 * its data in file on disk, which is in file hierarchy
 * inside virtual machines root directory.
 *
 * @author Anton Troshin
 * @author Mark Lindner
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class JVM extends DefinitionNode {

    public static final String CK_NAME = "jvm.name",
        CK_VERSION = "jvm.version",
        CK_PATH = "jvm.path",
        CK_SOURCE = "jvm.source",
        CK_ARGS = "jvm.arguments",
        CK_DOCDIR = "jvm.documentation_dir",
        CK_DESCRIPTION = "jvm.description";

    private static final Icon ICON = WorkspaceInstaller.getResourceManager().getIcon("jvm.gif");

    private static final String JAVA_VIRTUAL_MACHINE_DEFINITION = "Java Virtual Machine Definition";

    private String name;
    private String version = "";
    private String path = "";
    /**
     *  Sets java virtual machine arguments. This can
     *  be everything supported by VM.
     *  Note, that next parameters are required by
     *  Java Workspace Installer to launch application
     *  with all options:
     *  <b>-cp %c %m %a</b>.
     *  <ol>
     *  <li>%c - include classpath
     *  <li>%m - include main class
     *  <li>%a - include application command line parameters
     *  </li>
     *  If any of these three parameters are missing,
     *  corresponding part of full command line, nessesary
     *  to launch application, will be omitted.
     */
    private String arguments = "-cp %c %m %a";
    private String description = "";
    private String docs = "";
    private String source = "";

    private ConfigFile config;

    /**
     * Public jvm constructor.
     *
     * @param parent node {@link DefinitionNode
     * @param file   to hold jvm data java.io.File
     */
    public JVM(DefinitionNode parent, File file) {
        super(parent, file);
        this.name = getNodeName();
    }

    /**
     * Public jvm constructor.
     *
     * @param parent node {@link DefinitionNode
     * @param name   of file to hold jvm data java.lang.String
     */
    public JVM(DefinitionNode parent, String name) {
        super(parent, name + DefinitionNode.FILE_EXTENSION);
        this.name = name;
    }

    public static JVM getCurrentJvm(DefinitionNode parent) throws IOException {

        JVM jvm = new JVM(parent, "current_jvm");

        jvm.setDescription("the jvm this instance of workspace is currently running");
        jvm.setPath(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        jvm.setVersion(System.getProperty("java.version"));
        jvm.setArguments(jvm.getArguments());
        jvm.save();

        return jvm;
    }

    /**
     * Returns closed ICON to represent
     * jvm in tree control.
     */
    public Icon getClosedIcon() {
        return ICON;
    }

    /**
     * Returns open ICON to represent
     * jvm in tree control.
     */
    public Icon getOpenIcon() {
        return ICON;
    }

    /**
     * Indicates that this is a leaf,
     * not a branch, as it is cannot
     * be expanded.
     */
    public boolean isExpandable() {
        return false;
    }

    /**
     * Loads class data from disk file
     */
    @SuppressWarnings("Duplicates")
    public void load() throws IOException {

        config = new ConfigFile(file, JAVA_VIRTUAL_MACHINE_DEFINITION);
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
    @SuppressWarnings("Duplicates")
    public void save() throws IOException {
        if (config == null) {
            config = new ConfigFile(file, JAVA_VIRTUAL_MACHINE_DEFINITION);
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
     * Returns brief jvm info, that is used in installer configuration dialogs.
     */
    public String toString() {
        return name + " " + version;
    }
}