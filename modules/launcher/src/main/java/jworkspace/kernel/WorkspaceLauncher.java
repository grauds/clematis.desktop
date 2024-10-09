package jworkspace.kernel;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin
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

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import static jworkspace.utils.StreamUtils.withCounter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jworkspace.api.IConstants;

/**
 * Starts the application.
 * <p>
 * 1. Dynamically loads Java Workspace libraries from ./lib directory.
 * 2. Checks 'plugins' directory
 * 3. Checks 'users' directory
 *
 * @author Anton Troshin
 */
public class WorkspaceLauncher {
    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceLauncher.class);
    /**
     * Workspace main class name
     */
    private static final String JWORKSPACE_CLASS = "jworkspace.kernel.Workspace";
    /**
     * Prompt for console username
     */
    private static final String USERNAME_PROMPT = "Username (default 'root'): ";
    /**
     * Prompt for console password
     */
    private static final String PASSWORD_PROMPT = "Password: ";

    private WorkspaceLauncher() {}

    /**
     * Starts the application.
     *
     * @param args an array of command-line arguments
     */
    @SuppressWarnings("regexp")
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    public static void main(String[] args) {

        Console console = System.console();
        String userName = "root";
        String password = "";

        if (console != null) {
            console.readLine(USERNAME_PROMPT, userName);
            console.readPassword(PASSWORD_PROMPT, password);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println(USERNAME_PROMPT);
            userName = scanner.nextLine();
            System.out.println(PASSWORD_PROMPT);
            password = scanner.nextLine();
        }
        /*
         * Fill command line for a new process.
         */
        StringBuffer commandLine = getCommandLine();
        /*
         * Add arguments
         */
        commandLine.append(IConstants.WHITESPACE);
        commandLine.append(JWORKSPACE_CLASS);
        commandLine.append(IConstants.WHITESPACE);

        if (!userName.isEmpty()) {
            commandLine.append(" -username ").append(userName);
        }

        if (!password.isEmpty()) {
            commandLine.append(" -password ").append(password);
        }

        /*
         * Launch workspace
         */
        try {
            LOG.info(commandLine.toString());
            Process process = Runtime.getRuntime().exec(commandLine.toString());
            new JavaProcess(process, "Java Workspace");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Get command line for launching Java Workspace.
     *
     * @return command line for launching Java Workspace
     */
    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:NestedIfDepth"})
    public static StringBuffer getCommandLine() {
        StringBuffer sb = new StringBuffer();

        File lib = Paths.get(Workspace.getBasePath().toString(), "lib").toFile();

        try {
            if (!lib.exists()) {
                Files.createDirectories(lib.toPath());
            }
            File[] jars = lib.listFiles((dir, name) -> name.toLowerCase().endsWith("jar"));
            if (jars != null) {
                Arrays.stream(jars).forEach(withCounter((i, jar) -> {

                    String classpathChunk = Paths.get(lib.getAbsolutePath(), jar.getName()).toAbsolutePath()
                        + ((i == jars.length - 1) ? "" : File.pathSeparator);

                    if (jar.getName().startsWith("_")) {
                        sb.insert(0, classpathChunk);
                    } else {
                        sb.append(classpathChunk);
                    }
                }));
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        sb.insert(0, "java -Djava.library.path=" + lib.getAbsolutePath() + " -classpath ");
        return sb;
    }
}