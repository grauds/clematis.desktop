package jworkspace.ui.network;

/* ----------------------------------------------------------------------------
   Clematis Collaboration Network 1.0.3
   Copyright (C) 2001-2003 Anton Troshin
   This file is part of Java Workspace Collaboration Network.
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
   tysinsh@comail.ru
  ----------------------------------------------------------------------------
 */

import java.io.*;
import java.util.*;
import java.util.logging.Level;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import jworkspace.kernel.*;
import jworkspace.network.user.*;
import jworkspace.ui.views.DefaultView;

import kiwi.io.*;
import kiwi.ui.*;

import kiwi.util.*;
import org.eldos.MsgConnect.MCBase.MCMessage;
import org.eldos.MsgConnect.MCBase.EMCError;

public class NetworkConsole extends DefaultView
                            implements CommandProcessor,
                            IWorkspaceListener
{
    /**
     * Status label
     */
    JLabel t = null;
    /**
     * Group channel for this console
     */
    String channel = UserInfo.getPreferences().
            getString(UserInfo.CK_MANAGEMENT_CHANNEL_NAME);
    /**
     * Connected console image
     */
    ImageIcon connectedIcon = null;
    /**
     * Disconnected console image
     */
    ImageIcon disconnectedIcon = null;
    /**
     * Command history
     */
    private Vector history = null;
    /**
     * Current line in history
     */
    private int histLine;
    /**
     * Beggining line
     */
    private String startedLine = "no line";
    /**
     * Command processor
     */
    private CommandDispatcher cd = new CommandDispatcher(this);
    /**
     * Scrollback view
     */
    ChannelView view = new ChannelView();
    /**
     * Command line
     */
    CommandLine command_line = new CommandLine();

    class HistoryScroller extends KeyAdapter
    {
        public void keyReleased(KeyEvent e)
        {
            switch (e.getKeyCode())
            {
                case (KeyEvent.VK_UP):
                    historyUp();
                    e.consume();
                    break;
                case (KeyEvent.VK_DOWN):
                    historyDown();
                    e.consume();
                    break;
            }
        }

        public void keyPressed(KeyEvent e)
        {
            switch (e.getKeyCode())
            {
                case (KeyEvent.VK_ENTER):
                    submitCommand();
                    break;
                default:
            }
        }

        private void historyUp()
        {
            if (getHistory().size() == 0)
            {
                return;
            }
            if (histLine == 0)
            { // save current line
                startedLine = getCmd();
            }
            if (histLine < getHistory().size())
            {
                histLine++;
                showHistoryLine();
            }
        }

        private void historyDown()
        {
            if (histLine == 0)
            {
                return;
            }
            histLine--;
            showHistoryLine();
        }

        private String getCmd()
        {
            return command_line.getText();
        }

        private void showHistoryLine()
        {
            String showline;
            if (histLine == 0)
            {
                showline = startedLine;
            }
            else
            {
                showline = (String)getHistory().elementAt(getHistory().size() - histLine);
            }
            command_line.setText(showline);
        }
    }
    /**
     * List of system channel users
     */
    JList server_users = new JList()
    {
        public void setFont(Font font)
        {
            font = new Font("Monospaced", Font.PLAIN, 12);
            super.setFont(font);
        }

        public void setBackground(Color bg)
        {
            super.setBackground(bg);
        }

        public void setForeground(Color fg)
        {
            super.setForeground(fg);
        }

        public Dimension getPreferredSize()
        {
            return new Dimension(200, 200);
        }
    };
    /**
     * Public constructor
     */
    public NetworkConsole()
    {
        super();
//*********************************************************
        /**
         * Assemble GUI
         */
        this.setLayout(new BorderLayout());
        /**
         * Scroller for our layout
         */
        JScrollPane scroller = new JScrollPane(server_users);
        /**
         * Scroller for server users
         */
        scroller.setPreferredSize(new Dimension(200, 300));
        server_users.setOpaque(false);
        server_users.setCellRenderer(new CustomListRenderer());
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
        /**
         * Splitter for our layout
         */
        JSplitPane splitter = new JSplitPane();
        /**
         * Add command line
         */
        this.add(command_line, BorderLayout.SOUTH);
        command_line.setOpaque(false);
        command_line.addKeyListener(new HistoryScroller());
        /**
         * Right panel with the header
         */
        KPanel p = new KPanel();
        p.setLayout(new BorderLayout());
        p.add(getHeaderLabel(), BorderLayout.NORTH);
        p.add(scroller, BorderLayout.CENTER);
        /**
         * View for channel commands and messages.
         */
        scroller = new JScrollPane();
        scroller.setViewportView(getChannelView());
        scroller.setBorder(null);
        scroller.setMinimumSize(new Dimension(500, 300));
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);

        splitter.setOneTouchExpandable(true);
        splitter.setRightComponent(p);
        splitter.setLeftComponent(scroller);
        splitter.setOpaque(false);

        add(splitter, BorderLayout.CENTER);
//***********************************************************
        /**
         * Set name
         */
        this.setName("Network Console");
    }
    /**
     * Return history
     * @return
     */
    public Vector getHistory()
    {
       if (history == null)
       {
          history = new Vector();
       }
       return history;
    }

    /**
     * Create and return channel view component
     */
    public ChannelView getChannelView()
    {
        if (view == null)
        {
            view = new ChannelView();
        }
        return view;
    }

    protected JList getServerUsersList()
    {
        return server_users;
    }

    public JLabel getHeaderLabel()
    {
       if ( t == null )
       {
          t = new JLabel();
          t.setHorizontalAlignment(JLabel.CENTER);
          t.setPreferredSize(new Dimension(200, 30));
       }
       return t;
    }
    /**
     * Create component from the scratch. Used for
     * default assemble of ui components.
     */
    public void create()
    {
        /**
         * Register for workspace events and messenger
         * messages.
         */
        Workspace.addListener(this);
        Messenger.addMessageHandler(new NetworkMessageHandler(this));
        /**
         * Connected console image
         */
        connectedIcon = new ImageIcon(new ResourceLoader(NetworkConsole.class)
                                      .getResourceAsImage("images/online.gif"));
        /**
         * Disconnected console image
         */
        disconnectedIcon = new ImageIcon(new ResourceLoader(NetworkConsole.class)
                                         .getResourceAsImage("images/offline.gif"));
    }
    /**
     * Set this flag to true, if you want component
     * to be unique among all workspace views.
     * This component will be registered.
     * @return boolean
     */
    public boolean isUnique()
    {
        return false;
    }
//********************** Request methods ***************************
    /**
     * Send request to User plugin to perform operation with
     * given code.
     * @param code of operation
     * @param data string data for message
     */
    protected void postRequest(int code, String data)
    {
        MCMessage message = new MCMessage();
        message.setMsgCode(code);
        if (data != null)
        {
            message.setData(data.getBytes());
        }
        try
        {
            Messenger.getMessenger().postMessage("LOCAL:SYSTEM", message,
                    Messenger.getMessageCredentials());
        }
        catch (EMCError emcError)
        {
            emcError.printStackTrace();
        }
    }
    /**
     * Send request to User plugin to perform operation with
     * given code.
     * @param code of operation
     * @param data string data for message
     */
    protected int sendRequest(int code, String data)
    {
        MCMessage message = new MCMessage();
        message.setMsgCode(code);
        if (data != null)
        {
            message.setData(data.getBytes());
        }
        try
        {
            return Messenger.getMessenger().sendMessage("LOCAL:SYSTEM", message,
                    Messenger.getMessageCredentials());
        }
        catch (EMCError emcError)
        {
            Workspace.getLogger().log( Level.WARNING, "Cannot send request", emcError );
        }
        catch (InterruptedException e)
        {
            Workspace.getLogger().log( Level.WARNING, "Cannot send request", e );
        }
        return -1;
    }
//**************************** Workspace UI View methods ********************
    public void activated(boolean flag)
    {
        if (flag)
        {
            command_line.requestFocus();
            /**
             * Check online status
             */
            sendRequest(1814, null);
            /**
             * Request group users
             */
            sendRequest(1813, null);
        }
    }
    /**
     * Append text to console
     * @param str new text
     */
    public void append(String str)
    {
        view.append(str);
    }

    public void load() throws java.io.IOException
    {
      /**
       * Load command history
       */
      try
      {
          String file = System.getProperty("user.home")
                  + File.separator + "network" +
                  File.separator + ".history";
          FileInputStream in = new FileInputStream(file);
          XDataInputStream xin = new XDataInputStream(in);

          while (true)
          {
              String line = xin.getLine();
              if (line != null)
              {
                  getHistory().addElement(line);
              }
              else
              {
                  break;
              }
          }
      }
      catch (FileNotFoundException ex) { }
    }

    public void reset() {  }
    /**
     * This method actually saves data in plugins
     */
    public void dispose()
    {
        try
        {
            save();
        }
        catch (IOException ex)
        {
            Workspace.getLogger().log( Level.WARNING,
                                       "Exception - Cannot save network console preferences", ex );
        }
    }

    /**
     * Add this shell to the list of workspace
     * listeners and load command history.
     */
    public void save() throws java.io.IOException
    {
        /**
         * Save command history
         */
        try
        {
            String file = System.getProperty("user.home")
                    + File.separator + "network" +
                    File.separator + ".history";
            FileOutputStream out = new FileOutputStream(file);
            XDataOutputStream xin = new XDataOutputStream(out);
            Enumeration els = getHistory().elements();
            /**
             * Do not save lines of plain text, only commands
             */
            while (els.hasMoreElements())
            {
                String str = (String)els.nextElement();
                if (str.startsWith("/"))
                {
                    xin.putLine(str);
                }
            }
        }
        catch (IOException ex){ }
    }
    /**
     * Set status label
     */
    protected void setStatus(String status, boolean b_connected)
    {
        /**
         * Add pictures for two states of console
         */
        if (b_connected && connectedIcon != null)
        {
            getHeaderLabel().setIcon(connectedIcon);
        }
        else if (!b_connected && disconnectedIcon != null)
        {
            getHeaderLabel().setIcon(disconnectedIcon);
        }
        getHeaderLabel().setText(status);
        getHeaderLabel().revalidate();
        getHeaderLabel().repaint();
    }
//****************** Commands section *******************
    private void submitCommand()
    {
        String command = command_line.getText();
        /**
         * Empty command line
         */
        command_line.setText("");
        /**
         * Add to history
         */
        getHistory().addElement(command);
        /**
         * Execute command or translate plain text
         * over system channel.
         */
        if (command.equalsIgnoreCase("/connect")
                || command.startsWith("/connect_as")
                || command.equalsIgnoreCase("/help")
                || command.startsWith("/history")
                || command.startsWith("/clear"))
        {
            view.append(command);
            cd.dispatch(command.substring(1, command.length()));
            return;
        }
        if (command.startsWith("/"))
        {
           cd.dispatch(command.substring(1, command.length()));
        }
        else
        {
         /**
          * Send text to currently active group channel
          */
           postRequest(1816, command);
        }
        view.append(command);
    }
    public void argumentCountError(String param)
    {
        view.append("[ No overloaded method found - " + param + " ]");
    }
    public void argumentFormatError(String param1, String param2)
    {
        view.append("[ No overloaded method found - " + param1 + " - " + param2 +  " ]");
    }
    public void invocationError(String param1, Exception param2)
    {
        view.append("[ Invocation error - " + param1 + " - " + param2+ " ]");
    }
    public void unknownCommandError(String param)
    {
        view.append("[ Unknown command - " + param + " ]");
    }
    /**
     * Command section. Connect.
     */
    public void cmd_connect()
    {
        postRequest(1800, null);
    }
    /**
     * Command section. Connect as different nick, but the same password.
     */
    public void cmd_connect_as(String name)
    {
        postRequest(1801, name);
    }
    /**
     * Command section. Connect.
     */
    public void cmd_connect_as(String name, String password)
    {
        postRequest(1802, name + ":" + password);
    }
    /**
     * Command section. Alternative connect.
     */
    public void cmd_alt_connect()
    {
        postRequest(1802, UserInfo.getPreferences().getString(UserConstants.CK_ALT_USER_NAME)
                          + ":" +
                          UserInfo.getPreferences().getString(UserConstants.CK_ALT_USER_PASSWORD));
    }
    /**
     * Disconnect from current server
     */
    public void cmd_disconnect()
    {
        postRequest(1803, null);
    }
    /**
     * Disconnect from group
     */
    public void cmd_grp_leave(String name)
    {
        postRequest(1803, name);
    }
    /**
     * Create users group
     */
    public void cmd_grp_create(String name)
    {
        postRequest(1804, name);
    }
    /**
     * Join specified group
     */
    public void cmd_grp_join(String name)
    {
        postRequest(1805, name);
    }
    /**
     * Join the most recent group
     */
    public void cmd_grp_join_recent()
    {
        postRequest(1806, null);
    }
    /**
     * List existing groups
     */
    public void cmd_grp_list()
    {
        postRequest(1807, null);
    }
    /**
     * Print group participants
     */
    public void cmd_grp_print(String name)
    {
       postRequest(1808, name);
    }
    /**
     * Delete group
     */
    public void cmd_grp_remove(String name)
    {
       postRequest(1809, name);
    }
    /**
     * Print information about current user
     */
    public void cmd_usr_info()
    {
        postRequest(1810, null);
    }
    /**
     * Load group resource
     * @param name of resource to load
     */
    public void cmd_grp_res_load(String name)
    {
       postRequest(1810, name);
    }
    /**
     * Set alternative user name for user management,
     * if existing one is already used on server.
     */
    public void cmd_set_name(String name)
    {
       postRequest(1811, name);
    }
    /**
     * Set alternative user name and password for group management client.
     */
    public void cmd_set_alt_info(String userName, String passwd)
    {
       postRequest(1812, userName + ":" + passwd);
    }
    /**
     * Updates controls for the component.
     * For example, accociated menu items,
     * buttons etc.
     */
    public void update()
    {
        postRequest(1813, null);
    }
    /**
     * Set active group. User should be connected
     * to that group first.
     */
    public void cmd_grp_set_active(String name)
    {
       postRequest(1815, name);
    }
    /**
     * History operations: -c delete history, -v view history.
     */
    public void cmd_history(String arg)
    {
        if (arg.equals("-c"))
        {
            getHistory().removeAllElements();
            histLine = 0;
            view.append("History is deleted.");
        }
        else if (arg.equals("-v"))
        {
            view.append("History list:");
            for (int i = 0; i < getHistory().size(); i++)
            {
                view.append((String)getHistory().elementAt(i));
            }
        }
    }
    /**
     * Clear console
     */
    public void cmd_clear()
    {
        view.setText("");
    }
    /**
     * Help on commands
     */
    public void cmd_help()
    {
    }
//************************* Process global workspace events ***************
    /**
     * Process workspace event from user manager
     */
    public void processEvent(Object event, Object lparam, Object rparam)
    {
        /**
         * Connected to server reply
         */
        if (event instanceof Integer && ((Integer)event).intValue() == 2001
                && lparam instanceof String && rparam instanceof Boolean)
        {
            setStatus((String)lparam, ((Boolean)rparam).booleanValue());
            update();
        }
        /**
         * Connected to server notify
         */
        else if (event instanceof Integer && ((Integer)event).intValue() == 2002
                && lparam instanceof String && rparam instanceof Boolean)
        {
            setStatus((String)lparam, ((Boolean)rparam).booleanValue());
            update();
        }
        /**
         * Incoming list of server users
         */
        else if (event instanceof Integer && ((Integer)event).intValue() == 2003)
        {
            if (rparam instanceof String[])
            {
                server_users.setListData((String[])rparam);
            }
            else
            {
                server_users.setListData(new Vector());
            }
        }
    }
}