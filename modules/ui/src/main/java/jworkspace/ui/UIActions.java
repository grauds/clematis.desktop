package jworkspace.ui;

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import jworkspace.LangResource;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.api.IConstants;
import jworkspace.kernel.Workspace;
import jworkspace.ui.action.AbstractStateAction;
import jworkspace.ui.dialog.SettingsDialog;
import jworkspace.ui.dialog.UserDetailsDialog;
import jworkspace.ui.widgets.ImageRenderer;

import com.hyperrealm.kiwi.ui.DocumentBrowserFrame;
import com.hyperrealm.kiwi.ui.model.DocumentDataSource;
import com.hyperrealm.kiwi.util.ResourceLoader;
import com.hyperrealm.kiwi.util.ResourceNotFoundException;
/**
 * This class contains all actions of workspace frame that are for system use.
 * @author Anton Troshin
 */
public class UIActions implements IConstants {
    /**
     * Logoff action name
     */
    static final String LOGOFF_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.logoff") + "...";
    /**
     * Exit action name
     */
    static final String EXIT_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.exit") + "...";
    /**
     * About action name
     */
    static final String ABOUT_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.about");
    /**
     * Help action name
     */
    static final String HELP_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.help") + "...";
    /**
     * Settings action name
     */
    static final String SETTINGS_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.settings") + "...";
    /**
     * My details action name
     */
    static final String MY_DETAILS_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.mydetails") + "...";
    /**
     * New user action name
     */
    static final String NEW_USER_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.newuser") + "...";
    /**
     * Show control panel action name
     */
    static final String SHOW_PANEL_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.cp");
    /**
     * Full screen action name
     */
    static final String FULL_SCREEN_ACTION_NAME =
        LangResource.getString("WorkspaceFrame.menu.fullscr");
    /**
     * Instance of Workspace GUI
     */
    protected WorkspaceGUI gui = null;
    /**
     * All actions
     */
    private Hashtable actions = new Hashtable();
    /**
     * Logoff action
     */
    private Action logoffAction;
    /**
     * Exit action
     */
    private Action exitAction;
    /**
     * About action
     */
    private Action aboutAction;
    /**
     * Help action
     */
    private Action helpAction;
    /**
     * Settings action
     */
    private Action settingsAction;
    /**
     * My details action
     */
    private Action myDetailsAction;
    /**
     * New user action
     */
    private Action newUserAction;
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
        return (Action) actions.get(name);
    }

    public Action[] getActions() {
        Enumeration e = actions.elements();
        Action[] temp = new Action[actions.size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = (Action) e.nextElement();
        }
        return temp;
    }

    ShowPanelAction getShowPanelAction() {
        return showPanelAction;
    }

    private void createActions() {

        aboutAction = new AboutAction();
        helpAction = new HelpAction();
        exitAction = new ExitAction();
        logoffAction = new LogoffAction();
        myDetailsAction = new MyDetailsAction();
        newUserAction = new NewUserAction();
        settingsAction = new SettingsAction();
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

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JDialog aboutFrame = new JDialog(Workspace.getUi().getFrame());
        Image im = new ResourceLoader(WorkspaceResourceAnchor.class)
            .getResourceAsImage("logo/Logo.gif");

        aboutFrame.getContentPane().setLayout(new BorderLayout());
        ImageRenderer imr = new ImageRenderer();
        imr.setImage(im);
        aboutFrame.getContentPane().add(imr, BorderLayout.CENTER);
        aboutFrame.pack();
        aboutFrame.setLocation((screenSize.width - aboutFrame.getWidth()) / 2,
            (screenSize.height - aboutFrame.getHeight()) / 2);

        aboutFrame.setResizable(false);
        aboutFrame.setModal(true);
        aboutFrame.setTitle(LangResource.getString("Workspace.about.title"));
        aboutFrame.setVisible(true);
    }

    /**
     * Show help
     */
    private void showHelp() {
        try {
            DocumentDataSource helpSource = new DocumentDataSource(Workspace.getResourceManager());
            DocumentBrowserFrame dbf = new DocumentBrowserFrame(Workspace.getVersion()
                + " " +
                LangResource.getString("message#184"),
                "", helpSource);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            dbf.setBounds(10, 10, (int) (2 * screenSize.getWidth() / 3),
                (int) (2 * screenSize.getHeight() / 3));
            dbf.setVisible(true);
        } catch (ResourceNotFoundException ex) {
            JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
                LangResource.getString("UIActions.resource.notFound")
                    + " " + ex.getMessage());
        }
    }

    /**
     * Show my details dialog
     */
    public void showMyDetails() {
        UserDetailsDialog dlg =
            new UserDetailsDialog(Workspace.getUI().getFrame());
        dlg.setData();
        dlg.setVisible(true);
    }

    /**
     * Show workspace ui settings dialog
     */
    public void showSettings() {
        SettingsDialog dlg = new SettingsDialog(Workspace.getUI().getFrame());
        dlg.setData();
        dlg.setVisible(true);
    }

    protected class LogoffAction extends AbstractAction {
        LogoffAction() {
            super(LOGOFF_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            Workspace.changeCurrentProfile();
        }
    }

    protected class ExitAction extends AbstractAction {
        public ExitAction() {
            super(EXIT_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            Workspace.exit();
        }
    }

    protected class AboutAction extends AbstractAction {
        public AboutAction() {
            super(ABOUT_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            about();
        }
    }

    protected class HelpAction extends AbstractAction {
        public HelpAction() {
            super(HELP_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            showHelp();
        }
    }

    protected class SettingsAction extends AbstractAction {
        public SettingsAction() {
            super(SETTINGS_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            showSettings();
        }
    }

    protected class MyDetailsAction extends AbstractAction {
        public MyDetailsAction() {
            super(MY_DETAILS_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            showMyDetails();
        }
    }

    protected class NewUserAction extends AbstractAction {
        public NewUserAction() {
            super(NEW_USER_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    protected class ShowPanelAction extends AbstractStateAction {
        public ShowPanelAction() {
            super(SHOW_PANEL_ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            ((MainFrame) gui.getFrame()).switchControlPanel();
            setSelected(((MainFrame) gui.getFrame()).getControlPanel().isVisible());
        }
    }
}