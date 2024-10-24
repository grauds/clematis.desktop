package jworkspace.ui;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016, 2019 Anton Troshin

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
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.hyperrealm.kiwi.ui.DocumentBrowserFrame;
import com.hyperrealm.kiwi.ui.model.datasource.DocumentDataSource;
import com.hyperrealm.kiwi.util.ResourceNotFoundException;

import jworkspace.Workspace;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.api.Constants;
import jworkspace.ui.api.action.AbstractStateAction;
import jworkspace.ui.dialog.SettingsDialog;
import jworkspace.ui.dialog.UserDetailsDialog;
import jworkspace.ui.widgets.ImageRenderer;

/**
 * This class contains all actions of workspace frame that are for system use.
 * @author Anton Troshin
 */
public class UIActions implements Constants {

    /**
     * Logoff action name
     */
    static final String LOGOFF_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.logoff") + Constants.LOG_FINISH;
    /**
     * Exit action name
     */
    static final String EXIT_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.exit") + Constants.LOG_FINISH;
    /**
     * About action name
     */
    static final String ABOUT_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.about");
    /**
     * Help action name
     */
    static final String HELP_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.help") + Constants.LOG_FINISH;
    /**
     * Settings action name
     */
    static final String SETTINGS_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.settings") + Constants.LOG_FINISH;
    /**
     * My details action name
     */
    static final String MY_DETAILS_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.mydetails") + Constants.LOG_FINISH;
    /**
     * Show control panel action name
     */
    static final String SHOW_PANEL_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.cp");

    /**
     * New user action name
     */
    private static final String NEW_USER_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.newuser") + Constants.LOG_FINISH;

    /**
     * Full screen action name
     */
    private static final String FULL_SCREEN_ACTION_NAME =
        WorkspaceResourceAnchor.getString("WorkspaceFrame.menu.fullscr");

    private static final int MARGIN = 10;

    private static final int SIZE_FACTOR = 3;
    /**
     * Instance of Workspace GUI
     */
    protected WorkspaceGUI gui;
    /**
     * All actions
     */
    private final Map<String, Action> actions = new HashMap<>();
    /**
     * Show control panel action
     */
    private ShowPanelAction showPanelAction;

    /**
     * Public constructor
     */
    UIActions(WorkspaceGUI gui) {
        super();
        this.gui = gui;
        createActions();
    }

    Action getAction(String name) {
        return actions.get(name);
    }

    public Action[] getActions() {
        Collection<Action> e = actions.values();
        Action[] temp = new Action[actions.size()];
        return e.toArray(temp);
    }

    ShowPanelAction getShowPanelAction() {
        return showPanelAction;
    }

    private void createActions() {

        /*
         * About action
         */
        Action aboutAction = new AboutAction();
        /*
         * Help action
         */
        Action helpAction = new HelpAction();
        /*
         * Exit action
         */
        Action exitAction = new ExitAction();
        /*
         * Logoff action
         */
        Action logoffAction = new LogoffAction();
        /*
         * My details action
         */
        Action myDetailsAction = new MyDetailsAction();
        /*
         * New user action
         */
        Action newUserAction = new NewUserAction();
        /*
         * Settings action
         */
        Action settingsAction = new SettingsAction();

        showPanelAction = new ShowPanelAction();

        actions.put(ABOUT_ACTION_NAME, aboutAction);
        actions.put(HELP_ACTION_NAME, helpAction);
        actions.put(EXIT_ACTION_NAME, exitAction);
        actions.put(LOGOFF_ACTION_NAME, logoffAction);
        actions.put(MY_DETAILS_ACTION_NAME, myDetailsAction);
        actions.put(NEW_USER_ACTION_NAME, newUserAction);
        actions.put(SETTINGS_ACTION_NAME, settingsAction);
        actions.put(SHOW_PANEL_ACTION_NAME, showPanelAction);

    }

    /**
     * Show about dialog
     */
    private void about() {

        JDialog aboutFrame = new JDialog(gui.getFrame());
        Image im = WorkspaceGUI.getResourceManager().getImage("logo/Logo.png");

        aboutFrame.getContentPane().setLayout(new BorderLayout());
        ImageRenderer imr = new ImageRenderer();
        imr.setImage(im);
        aboutFrame.getContentPane().add(imr, BorderLayout.CENTER);
        aboutFrame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        aboutFrame.setLocation((screenSize.width - aboutFrame.getWidth()) / 2,
            (screenSize.height - aboutFrame.getHeight()) / 2);

        aboutFrame.setResizable(false);
        aboutFrame.setModal(true);
        aboutFrame.setTitle(WorkspaceResourceAnchor.getString("Workspace.about.title"));
        aboutFrame.setVisible(true);
    }

    /**
     * Show help
     */
    private void showHelp() {
        try {

            DocumentDataSource helpSource = new DocumentDataSource(WorkspaceGUI.getResourceManager());
            DocumentBrowserFrame dbf = new DocumentBrowserFrame(
                Workspace.VERSION
                    + Constants.LOG_SPACE
                    + WorkspaceResourceAnchor.getString("message#184"),
                "", helpSource
            );

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            dbf.setBounds(MARGIN, MARGIN,
                (int) (2 * screenSize.getWidth() / SIZE_FACTOR),
                (int) (2 * screenSize.getHeight() / SIZE_FACTOR)
            );
            dbf.setVisible(true);
        } catch (ResourceNotFoundException ex) {
            JOptionPane.showMessageDialog(gui.getFrame(),
                WorkspaceResourceAnchor.getString("UIActions.resource.notFound")
                    + " " + ex.getMessage());
        }
    }

    /**
     * Show my details dialog
     */
    private void showMyDetails() {
        UserDetailsDialog dlg = new UserDetailsDialog(gui.getFrame());
        dlg.setData();
        dlg.setVisible(true);
    }

    /**
     * Show workspace ui settings dialog
     */
    private void showSettings() {
        SettingsDialog dlg = new SettingsDialog(gui.getFrame());
        dlg.setData();
        dlg.setVisible(true);
    }

    protected static class LogoffAction extends AbstractAction {
        LogoffAction() {
            super(LOGOFF_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Workspace.getInstance().stop();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    protected static class ExitAction extends AbstractAction {
        ExitAction() {
            super(EXIT_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
           // todo Workspace.exit();
        }
    }

    protected class AboutAction extends AbstractAction {
        AboutAction() {
            super(ABOUT_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            about();
        }
    }

    protected class HelpAction extends AbstractAction {
        HelpAction() {
            super(HELP_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            showHelp();
        }
    }

    protected class SettingsAction extends AbstractAction {
        SettingsAction() {
            super(SETTINGS_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            showSettings();
        }
    }

    protected class MyDetailsAction extends AbstractAction {
        MyDetailsAction() {
            super(MY_DETAILS_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            showMyDetails();
        }
    }

    protected static class NewUserAction extends AbstractAction {
        NewUserAction() {
            super(NEW_USER_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    protected class ShowPanelAction extends AbstractStateAction {
        ShowPanelAction() {
            super(SHOW_PANEL_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            ((MainFrame) gui.getFrame()).switchControlPanel();
            setSelected(((MainFrame) gui.getFrame()).getControlPanel().isVisible());
        }
    }
}