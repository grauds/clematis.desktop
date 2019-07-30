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
import java.util.Enumeration;
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
    private JList<String> loggerList;

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
        loggerList = new JList<>();
        loggerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setLayout(new BorderLayout());
        add(new JScrollPane(loggerList), BorderLayout.CENTER);
        JButton showButton = new JButton(LangResource.getString("LoggingPanel.show"));
        add(showButton, BorderLayout.SOUTH);
        showButton.addActionListener(ae -> {
            String name = loggerList.getSelectedValue();
            if (name != null && !name.equals("")) {
                openWindow(name);
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
        loggerList.setListData(names);
    }

    private void openWindow(String loggerName) {
        Logger logger = Logger.getLogger(loggerName);
        // Create a WindowHandler
        WindowHandler windowHandler = new WindowHandler(loggerName);
        // Install it as a handler for the logger
        logger.addHandler(windowHandler);
    }
}

