package jworkspace.ui;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2002 Anton Troshin

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

import com.hyperrealm.kiwi.ui.DocumentBrowserFrame;
import com.hyperrealm.kiwi.ui.model.DocumentDataSource;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceLoader;
import com.hyperrealm.kiwi.util.ResourceNotFoundException;
import jworkspace.LangResource;
import jworkspace.WorkspaceResourceAnchor;
import jworkspace.kernel.IConstants;
import jworkspace.kernel.Workspace;
import jworkspace.ui.action.AbstractStateAction;
import jworkspace.ui.dialog.SettingsDialog;
import jworkspace.ui.dialog.UserDetailsDialog;
import jworkspace.ui.widgets.ImageRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class contains all actions of workspace frame that are
 * for system use.
 */
public class UIActions implements IConstants
{
    /**
     * Instance of Workspace GUI
     */
    protected WorkspaceGUI gui = null;
    /**
     * All actions
     */
    protected Hashtable actions = new Hashtable();
    /**
     * Logoff action name
     */
    public static final String logoffActionName =
            LangResource.getString("WorkspaceFrame.menu.logoff") + "...";
    /**
     * Exit action name
     */
    public static final String exitActionName =
            LangResource.getString("WorkspaceFrame.menu.exit") + "...";
    /**
     * About action name
     */
    public static final String aboutActionName =
            LangResource.getString("WorkspaceFrame.menu.about");
    /**
     * Help action name
     */
    public static final String helpActionName =
            LangResource.getString("WorkspaceFrame.menu.help") + "...";
    /**
     * Settings action name
     */
    public static final String settingsActionName =
            LangResource.getString("WorkspaceFrame.menu.settings") + "...";
    /**
     * My details action name
     */
    public static final String myDetailsActionName =
            LangResource.getString("WorkspaceFrame.menu.mydetails") + "...";
    /**
     * New user action name
     */
    public static final String newUserActionName =
            LangResource.getString("WorkspaceFrame.menu.newuser") + "...";
    /**
     * Show control panel action name
     */
    public static final String showPanelActionName =
            LangResource.getString("WorkspaceFrame.menu.cp");
    /**
     * Full screen action name
     */
    public static final String fullScreenActionName =
            LangResource.getString("WorkspaceFrame.menu.fullscr");
    /**
     * Logoff action
     */
    protected Action logoffAction;

    protected class LogoffAction extends AbstractAction
    {
        public LogoffAction()
        {
            super(logoffActionName);
        }

        public void actionPerformed(ActionEvent e)
        {
            Workspace.changeCurrentProfile();
        }
    }

    /**
     * Exit action
     */
    protected Action exitAction;

    protected class ExitAction extends AbstractAction
    {
        public ExitAction()
        {
            super(exitActionName);
        }

        public void actionPerformed(ActionEvent e)
        {
            Workspace.exit();
        }
    }

    /**
     * About action
     */
    protected Action aboutAction;

    protected class AboutAction extends AbstractAction
    {
        public AboutAction()
        {
            super(aboutActionName);
        }

        public void actionPerformed(ActionEvent e)
        {
            about();
        }
    }

    /**
     * Help action
     */
    protected Action helpAction;

    protected class HelpAction extends AbstractAction
    {
        public HelpAction()
        {
            super(helpActionName);
        }

        public void actionPerformed(ActionEvent e)
        {
            showHelp();
        }
    }

    /**
     * Settings action
     */
    protected Action settingsAction;

    protected class SettingsAction extends AbstractAction
    {
        public SettingsAction()
        {
            super(settingsActionName);
        }

        public void actionPerformed(ActionEvent e)
        {
            showSettings();
        }
    }

    /**
     * My details action
     */
    protected Action myDetailsAction;

    protected class MyDetailsAction extends AbstractAction
    {
        public MyDetailsAction()
        {
            super(myDetailsActionName);
        }

        public void actionPerformed(ActionEvent e)
        {
            showMyDetails();
        }
    }

    /**
     * New user action
     */
    protected Action newUserAction;

    protected class NewUserAction extends AbstractAction
    {
        public NewUserAction()
        {
            super(newUserActionName);
        }

        public void actionPerformed(ActionEvent e)
        {
        }
    }

    /**
     * Show control panel action
     */
    protected ShowPanelAction showPanelAction;

    protected class ShowPanelAction extends AbstractStateAction
    {
        public ShowPanelAction()
        {
            super(showPanelActionName);
        }

        public void actionPerformed(ActionEvent e)
        {
            ((WorkspaceFrame) gui.getFrame()).switchControlPanel();
            setSelected(((WorkspaceFrame) gui.getFrame()).getControlPanel().isVisible());
        }
    }

    /**
     * Public constructor
     */
    public UIActions(WorkspaceGUI gui)
    {
        super();
        this.gui = gui;
        createActions();
    }

    public Action getAction(String name)
    {
        return (Action) actions.get(name);
    }

    public Action[] getActions()
    {
        Enumeration e = actions.elements();
        Action[] temp = new Action[actions.size()];
        for (int i = 0; i < temp.length; i++)
        {
            temp[i] = (Action) e.nextElement();
        }
        return temp;
    }

    public ShowPanelAction getShowPanelAction()
    {
        return showPanelAction;
    }

    protected Hashtable createActions()
    {
        aboutAction = new AboutAction();
        helpAction = new HelpAction();
        exitAction = new ExitAction();
        logoffAction = new LogoffAction();
        myDetailsAction = new MyDetailsAction();
        newUserAction = new NewUserAction();
        settingsAction = new SettingsAction();
        showPanelAction = new ShowPanelAction();

        actions.put(aboutActionName, aboutAction);
        actions.put(helpActionName, helpAction);
        actions.put(exitActionName, exitAction);
        actions.put(logoffActionName, logoffAction);
        actions.put(myDetailsActionName, myDetailsAction);
        actions.put(newUserActionName, newUserAction);
        actions.put(settingsActionName, settingsAction);
        actions.put(showPanelActionName, showPanelAction);

        return actions;
    }

    /**
     * Show about dialog
     */
    public void about()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JDialog aboutFrame = new JDialog(Workspace.getUI().getFrame());
        Image im = new ResourceLoader(WorkspaceResourceAnchor.class)
                .getResourceAsImage("logo/Logo.gif");

        aboutFrame.getContentPane().setLayout(new BorderLayout());
        ImageRenderer imr = new ImageRenderer();
        imr.setImage(im);
        aboutFrame.getContentPane().add(imr, BorderLayout.CENTER);
        aboutFrame.pack();
        aboutFrame.setLocation((screenSize.width -
                                aboutFrame.getWidth()) / 2,
                               (screenSize.height - aboutFrame.getHeight()) / 2);

        aboutFrame.setResizable(false);
        aboutFrame.setModal(true);
        aboutFrame.setTitle
                (LangResource.getString("Workspace.about.title"));
        aboutFrame.setVisible(true);
    }

    /**
     * Show help
     */
    public void showHelp()
    {
        try
        {
            DocumentDataSource help_source = new DocumentDataSource(Workspace.getResourceManager());
            DocumentBrowserFrame dbf = new DocumentBrowserFrame(Workspace.getVersion()
                                                                + " " +
                                                                LangResource.getString("message#184"),
                                                                "", help_source);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            dbf.setBounds(10, 10, (int) (2 * screenSize.getWidth() / 3),
                          (int) (2 * screenSize.getHeight() / 3));
            dbf.setVisible(true);
        }
        catch (ResourceNotFoundException ex)
        {
            JOptionPane.showMessageDialog(Workspace.getUI().getFrame(),
                                          LangResource.getString("UIActions.resource.notFound")
                                          + " " + ex.getMessage());
        }
    }

    /**
     * Show my details dialog
     */
    public void showMyDetails()
    {
        UserDetailsDialog dlg =
                new UserDetailsDialog(Workspace.getUI().getFrame());
        dlg.setData();
        dlg.setVisible(true);
    }

    /**
     * Show workspace ui settings dialog
     */
    public void showSettings()
    {
        SettingsDialog dlg = new SettingsDialog(Workspace.getUI().getFrame());
        dlg.setData();
        dlg.setVisible(true);
    }
}