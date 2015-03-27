package jworkspace.kernel;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2000 Anton Troshin
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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
/**
 * Starts the application. Dynamically loads
 * Java Workspace libraries from ./lib directory.
 */
public class WorkspaceLauncher implements FilenameFilter
{
    /**
     * Starts the application.
     * @param args an array of command-line arguments
     */
    public static void main(java.lang.String[] args)
    {
        WorkspaceLauncher launcher = new WorkspaceLauncher();
        /**
         * Fill command line for a new process.
         */
        StringBuffer commandLine = launcher.getCommandLine();
        /**
         * Add arguments
         */
        commandLine.append(" ");
        commandLine.append("jworkspace.kernel.Workspace");
        commandLine.append(" ");

        for (int i = 0; i < args.length; i++)
        {
          commandLine.append(args[i]);
          commandLine.append(" ");
        }
        /**
         * Launch workspace
         */
        try
        {
            Runtime.getRuntime().exec(commandLine.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Get command line for launching Java Workspace.
     * @return command line for launching Java Workspace
     */
    protected StringBuffer getCommandLine()
    {
      StringBuffer sb = new StringBuffer();
      //********************* lib path *****************************
      File lib = new File("lib/");
      if (lib.exists())
      {
         String[] jars = lib.list(this);
         for (int i = 0; i < jars.length; i++)
         {
             if ( jars[i].startsWith("_") )
             {
                 sb.insert(0, File.pathSeparator);
                 sb.insert(0, jars[i]);
                 sb.insert(0, File.separator);
                 sb.insert(0, "lib");
                 sb.insert(0, File.separator);
                 sb.insert(0, ".");
             }
             else
             {
                 sb.append(".");
                 sb.append(File.separator);
                 sb.append("lib");
                 sb.append(File.separator);
                 sb.append(jars[i]);
                 sb.append(File.pathSeparator);
             }
         }
      }
      sb.insert( 0, "javaw -Djava.library.path=." + File.separator
                 + "lib -classpath .;." + File.separator + "i18n"
                 + File.pathSeparator);
      //********************* lib path *****************************
      return sb;
    }
    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param   dir    the directory in which the file was found.
     * @param   name   the name of the file.
     * @return  <code>true</code> if and only if the name should be
     * included in the file list; <code>false</code> otherwise.
     */
    public boolean accept(File dir, String name)
    {
        return name.toLowerCase().endsWith("jar");
    }
}