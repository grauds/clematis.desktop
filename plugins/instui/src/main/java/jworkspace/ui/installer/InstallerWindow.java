package jworkspace.ui.installer;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 - 2002 Anton Troshin

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
import javax.swing.*;

import java.io.*;
import java.util.*;
import java.awt.*;

import com.hyperrealm.kiwi.util.Config;
import com.hyperrealm.kiwi.util.ProgressObserver;
import kiwi.util.*;
import kiwi.util.plugin.*;

import jworkspace.ui.views.*;
import jworkspace.ui.cpanel.*;
import jworkspace.kernel.Workspace;

/**
 * Installer panel works as UI
 * component for Workspace install engine.
 */
public class InstallerWindow extends DefaultCompoundView
                                     implements ProgressObserver
{
  /**
   * Parent plugin
   */
  private Plugin plugin = null;
  /**
   * Tabbed panel.
   */
  JTabbedPane tabbed_pane = new JTabbedPane();
  /**
   *  Text files extensions config
   */
  Config preferences = null;
  /**
   * Save path. Relative to user.home
   */
  private String path = "installer";
  /**
   * Text preferences file name
   */
  public static final String PREFERENCES_FILE_NAME = "ext.cfg";
  /**
   * Save root.
   */
  String saveRoot = System.getProperty("user.dir") + File.separator +
                Workspace.getProfilesEngine().getPath();
  /**
   * Progress bar for lengthy background operations,
   * like images of source files load.
   */
  JProgressBar progress = new JProgressBar(0, 100);
  /**
   * Status component.
   */
  private JComponent status = new StatusBar();
  /**
   * Progress bar
   */
  class StatusBar extends JComponent
  {
      public StatusBar()
      {
         super();
         setLayout(new java.awt.BorderLayout());
      }
  }
  /**
   * InstallerPanel constructor comment.
   */
  public InstallerWindow()
  {
    super();

    tabbed_pane.addTab(LangResource.getString("Applications"),
                new ApplicationWorkbench(this));

    tabbed_pane.addTab(LangResource.getString("Libraries"),
                new LibraryWorkbench(this));

    tabbed_pane.addTab(LangResource.getString("message#201"),
                new JvmWorkbench(this));

    this.setLayout(new BorderLayout());
    this.add(tabbed_pane, BorderLayout.CENTER);

    status.setVisible(true);
    this.add(status, java.awt.BorderLayout.SOUTH);

    setName(LangResource.getString("Installer"));
  }
  public InstallerWindow(Plugin plugin)
  {
    this();
    this.plugin = plugin;
  }
  /**
   * Return buttons for control panel
   */
  public CButton[] getButtons()
  {
    /**
     * Start button.
     */
    Image normal = new ResourceLoader(InstallerWindow.class)
         .getResourceAsImage("images/installer.png");
    Image hover = new ResourceLoader(InstallerWindow.class)
         .getResourceAsImage("images/installer.png");

    CButton button = CButton.create(this, new ImageIcon(normal),
          new ImageIcon(hover),	InstallerWindow.SHOW,
          LangResource.getString("Installer"));

    return new CButton[]{ button };
  }
  /**
   * Set this flag to true, if you want component
   * to be unique among all workspace views.
   * This component will be registered.
   * @return boolean
   */
  public boolean isUnique()
  {
    return true;
  }
  /**
   * Loads profile data from file structure
   * in users folder "installations".
   */
  public void load() throws java.io.IOException
  {
    /**
     * Read configuration files
     */
    getPreferences();
  }
  /**
   * Returns preferences for installer window.
   */
  public Config getPreferences()
  {
      if (preferences == null)
      {
          preferences = new Config("Files extensions");
          preferences.put("text_files", "java,html,xml,txt,htm,mf,html,shtml,php3,txt,bsh");
          preferences.put("image_files", "gif,jpg,jpeg,jpe,png,tif,bmp,xpm");
          loadPreferences();
      }
      return preferences;
  }
  /**
   * Begin process
   */
  public void beginProcess()
  {
   // initialize the statusbar
   status.add(progress, java.awt.BorderLayout.CENTER);
   status.setVisible(true);
  }
  /**
   * End process
   */
  public void endProcess()
  {
    // we are done... get rid of progressbar
    status.removeAll();
    status.setVisible(false);
    update();
  }
  /**
   * Tells if there is a graphics file
   * or not.
   * @return boolean
   * @param entryname java.lang.String
   */
  protected boolean isGraphics(String name)
  {
   /**
    * Find extension of file
    */
   int iext = name.lastIndexOf("");
   if (iext == -1)
    return false;
   String ext = name.substring(iext + 1, name.length());
   String ext_set = (String)getPreferences().get("image_files");
   StringTokenizer st = new StringTokenizer(ext_set);
   while (st.hasMoreTokens())
     if (st.nextToken(",").equalsIgnoreCase(ext))
       return true;
   return false;
  }
  /**
   * Tells if there is a text source file or not.
   * @return boolean
   * @param entryname java.lang.String
   */
  protected boolean isSource(String name)
  {
   /**
    * Find extension of file
    */
   int iext = name.lastIndexOf("");
   if (iext == -1)
    return true;
   String ext = name.substring(iext + 1, name.length());
   String ext_set = (String)getPreferences().get("text_files");
   StringTokenizer st = new StringTokenizer(ext_set);
   while (st.hasMoreTokens())
     if (st.nextToken(",").equalsIgnoreCase(ext))
       return true;
   return false;
  }
  private void loadPreferences()
  {
     StringBuffer load_path = new StringBuffer();
     load_path.append(System.getProperty("user.home"));
     load_path.append(File.separator);
     load_path.append(path);
     load_path.append(File.separator);
     load_path.append(InstallerWindow.PREFERENCES_FILE_NAME);

     File file = new File(load_path.toString());
     if (file.exists())
     {
        try
        {
          FileInputStream f = new FileInputStream(load_path.toString());
          preferences.load(f);
          f.close();
        }
        catch (FileNotFoundException ffe)
        {
          /**
           * 	The exception can be handled by ignoring it since the
           *  default values in p will substitute for what should
           *  have been read from file.
           */
          Workspace.logException(LangResource.getString("message#106")
                   + ffe.getMessage());

        }
        catch (IOException ie)
        {
          Workspace.logException(LangResource.getString("message#106")
                   + ie.getMessage());
        }
     }
  }
  private void writePreferences()
  {
     StringBuffer save_path = new StringBuffer();
     save_path.append(System.getProperty("user.home"));
     save_path.append(File.separator);
     save_path.append(path);
     File file = new File(save_path.toString());

     if (!file.exists()) file.mkdirs();

     save_path.append(File.separator);
     save_path.append(InstallerWindow.PREFERENCES_FILE_NAME);

    try
    {
      FileOutputStream f = new FileOutputStream(save_path.toString());
      preferences.store(f, "InstallerUI configuration file");
      f.close();
    }
    catch (IOException ie)
    {
          Workspace.logException(LangResource.getString("message#182")
                   + ie.getMessage());
    }
  }
  /**
   * This method actually saves data in plugins
   */
  public void dispose()
  {
    try
    {
      save();
    }
    catch(IOException ex)
    {
      Workspace.logException(">Exception - Cannot save installer preferences");
    }
  }
  /**
   * This method saves preferences for workspace
   * installer.
   */
  public void save() throws java.io.IOException
  {
     writePreferences();
  }
 /**
  * Set the progress amount to <code>progress</code>, which is a value
  * between 0 and 100. (Out of range values should be silently clipped.)
  *
  * @progress The percentage of the task completed.
  */
  public void setProgress(int value)
  {
    progress.setValue(value);
    progress.revalidate();
    progress.repaint();
  }
}
