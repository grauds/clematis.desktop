package jworkspace;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jworkspace.runtime.JavaProcess;
import lombok.extern.java.Log;

/**
 * Starts the application.
 * <p>
 * 1. Dynamically loads Java Workspace libraries from ./lib directory.
 * 2. Checks 'plugins' directory
 * 3. Checks 'users' directory
 *
 * @author Anton Troshin
 */
@Log
public class ConsoleLauncher {

    static final String JVM_ARGS_DELIMITER = " ";
    /**
     * Workspace main class name
     */
    private static final String JWORKSPACE_CLASS = "jworkspace.Workspace";
    /**
     * Prompt for console username
     */
    private static final String USERNAME_PROMPT = "Username: ";
    /**
     * Prompt for console password
     */
    private static final String PASSWORD_PROMPT = "Password: ";

    private ConsoleLauncher() {}

    public static <T> Consumer<T> withCounter(BiConsumer<Integer, T> consumer) {
        AtomicInteger counter = new AtomicInteger(0);
        return item -> consumer.accept(counter.getAndIncrement(), item);
    }

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
        commandLine.append(JVM_ARGS_DELIMITER);
        commandLine.append(JWORKSPACE_CLASS);
        commandLine.append(JVM_ARGS_DELIMITER);

        if (!userName.isEmpty()) {
            commandLine.append(" --name ").append(userName);
        }

        if (!password.isEmpty()) {
            commandLine.append(" --password ").append(password);
        }

        /*
         * Launch workspace
         */
        try {
            log.info(commandLine.toString());
            Process process = Runtime.getRuntime().exec(commandLine.toString());
            new JavaProcess(process, "Java Workspace");
        } catch (IOException e) {
            log.severe(e.getMessage());
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
        sb.append(".").append(File.pathSeparator);

        File lib = Paths.get(
            "lib"
        ).toFile();

        try {
            if (!lib.exists()) {
                Files.createDirectories(lib.toPath());
            }
            File[] jars = lib.listFiles((dir, name) -> name.toLowerCase().endsWith("jar"));
            if (jars != null) {
                Arrays.stream(jars).forEach(withCounter((i, jar) -> {
                    String classpathChunk = Paths.get(
                        lib.getAbsolutePath(), jar.getName()
                    ).toAbsolutePath()
                        + ((i == (jars.length - 1)) ? "" : File.pathSeparator);

                    if (jar.getName().startsWith("_")) {
                        sb.insert(0, classpathChunk);
                    } else {
                        sb.append(classpathChunk);
                    }
                }));
            }
            sb.append("jworkspace-2.0.0-SNAPSHOT.jar");
            sb.insert(0, "java -Djava.library.path="
                + lib.getName()
                + " -classpath "
            );
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
        return sb;
    }
}