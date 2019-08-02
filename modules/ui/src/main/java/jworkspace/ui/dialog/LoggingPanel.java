// Created: 04.11.2003 T 14:36:26
// Copyright (C) 2003 by John Wiley & Sons Inc. All Rights Reserved.
package jworkspace.ui.dialog;
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

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.hyperrealm.kiwi.ui.KPanel;

import jworkspace.LangResource;
import jworkspace.ui.logging.WindowHandler;

/**
 * Panel with a list of loggers, available in system
 * This is taken from book of Gregory M. Travis "JDK 1.4 tutorial"
 *
 * @author <a href='mailto:anton.troshin@gmail.com'>Anton Troshin</a>
 */
class LoggingPanel extends KPanel {
    /**
     * List displaying the currently-instantiated loggers
     */
    private JList<String> loggerNamesList;
    /**
     * List displaying the currently-instantiated loggers
     */
    private List<Logger> loggerList;
    /**
     * Set up the interface and make visible
     */
    LoggingPanel() {
        super();
        setupGUI();
        populateList();
        setVisible(true);
    }

    /**
     * Set up the interface
     */
    private void setupGUI() {
        loggerList = new ArrayList<>();
        loggerNamesList = new JList<>();
        loggerNamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setLayout(new BorderLayout());
        add(new JScrollPane(loggerNamesList), BorderLayout.CENTER);
        JButton showButton = new JButton(LangResource.getString("LoggingPanel.show"));
        add(showButton, BorderLayout.SOUTH);
        showButton.addActionListener(ae -> {
            String name = loggerNamesList.getSelectedValue();
            if (name != null && !name.equals("")) {
                loggerList.add(openWindow(name));
            }
        });
    }

    /**
     * List the currently-instantiated Loggers
     */
    private void populateList() {
        // Get the names of the currently-instantiated loggers
        LogManager logManager = LogManager.getLogManager();
        Enumeration e = logManager.getLoggerNames();
        // Build a Vector of the names
        Vector<String> names = new Vector<>();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            names.add(name);
        }
        // Display the names in the JList
        loggerNamesList.setListData(names);
    }

    /**
     * OpenJDK introduces a potential incompatibility. In particular, the java.util.logging.Logger behavior has changed.
     * Instead of using strong references, it now uses weak references internally. That's a reasonable change, but
     * unfortunately some code relies on the old behavior - when changing logger configuration, it simply drops the
     * logger reference. That means that the garbage collector is free to reclaim that memory, which means that the
     * logger configuration is lost. For example, consider:
     *
     * public static void initLogging() throws Exception {
     *  Logger logger = Logger.getLogger("edu.umd.cs");
     *  logger.addHandler(new FileHandler()); // call to change logger configuration
     *  logger.setUseParentHandlers(false); // another call to change logger configuration
     * }
     * The logger reference is lost at the end of the method (it doesn't escape the method), so if you have a garbage
     * collection cycle just after the call to initLogging, the logger configuration is lost (because Logger only
     * keeps weak references).
     *
     * public static void main(String[] args) throws Exception {
     *  initLogging(); // adds a file handler to the logger
     *  System.gc(); // logger configuration lost
     *  Logger.getLogger("edu.umd.cs").info("Some message"); // this isn't logged to the file as expected
     *
     * @param loggerName of logger to follow
     */
    private Logger openWindow(String loggerName) {

        Logger logger = Logger.getLogger(loggerName);
        // Create a WindowHandler
        WindowHandler windowHandler = new WindowHandler(loggerName);
        // Install it as a handler for the logger
        logger.addHandler(windowHandler);

        return logger;
    }
}

