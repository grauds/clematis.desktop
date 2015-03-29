package jworkspace.ui.installer.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1998-99 Mark A. Lindner,
          2000 Anton Troshin

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
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.jar.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.*;

import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ProgressDialog;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;
import com.hyperrealm.kiwi.util.Task;
import jworkspace.installer.*;
import jworkspace.installer.InstallationException;
import jworkspace.ui.WorkspaceClassCache;
import jworkspace.util.JarClassLoader;
import jworkspace.ui.dialog.*;
import jworkspace.ui.installer.*;
import jworkspace.kernel.Workspace;

/**
 * Application configuration panel
 */
class ApplicationPanel extends KPanel implements ActionListener
  {
  private JTextField t_name, t_version, t_mainClass, t_args, t_archive,
   t_source, t_docs, t_vm, t_cwd;

  private KButton b_add, b_remove, b_browse0, b_browse1, b_browse2, b_browse3, b_browse4, b_detector;
  private JTextArea t_desc;
  private JList l_libs;
  private Application application;
  private LibraryChooserDialog d_libChooser = null;
  private JvmChooserDialog d_vmChooser = null;
  private JVM jvm;
  private DefaultListModel model;
  private JCheckBox b_launch_at_startup;
  private JCheckBox b_separate_process;
  private JCheckBox b_system_user_folder;
  private ProgressDialog pr = null;
  private Vector executables = new Vector();
  /**
   * Inner class for executable class search
   */
class Inspector extends Task
{
  File file = null;

  public Inspector(File file)
  {
    super();
    this.file = file;
    addProgressObserver(pr);
  }
  public void setFile(File file)
  {
    this.file = file;
  }
  public void run()
  {
    try
    {
      JarFile jar = new JarFile(file);

      for (Enumeration e = jar.entries(); e.hasMoreElements();)
      {
          JarEntry entry = (JarEntry) e.nextElement();
          String entry_name = entry.getName();
          /**
           * HACK: If entry name finished with "\" - this is
           * in zip files, ignore it as it is a directory
           * name.
           */
          if (entry_name.endsWith("/")) continue;
          String class_name = getFullClassName(entry_name, '/');
          if (!class_name.endsWith(".class"))
             continue;
          else
             class_name = class_name.substring(0, class_name.length() - ".class".length());

          try
          {
            Class inst = JarClassLoader.loadClass(file.getAbsolutePath(), class_name);
            inst.getMethod("main", new Class[] { String[].class });
            executables.addElement(class_name);
          }
          catch (NoSuchMethodException ex)
          {
             continue;
          }
          catch (IOException ex)
          {
             continue;
          }
          catch (ClassNotFoundException ex)
          {
             continue;
          }
          catch (NoClassDefFoundError err)
          {
             continue;
          }
      }
    }
    catch (IOException ex)
    {
      Workspace.getLogger().warning("Inspection of application file is failed: "
      + ex.toString());
    }
    pr.setProgress(100);
  }
  /**
   * Returns full qualified class name against jar file
   * entry name.
   * @return java.lang.String
   * @param entry_name java.lang.String
   */
  private String getFullClassName(String entry_name, char delimiter)
  {
    return entry_name.replace(delimiter,'.');
  }
}

public ApplicationPanel()
{
  JTabbedPane tabbed_pane = new JTabbedPane();
  tabbed_pane.setOpaque(false);
  setLayout(new BorderLayout());
  JSeparator sep = new JSeparator();
  add(sep, BorderLayout.NORTH);

  tabbed_pane.add(LangResource.getString("message#1000"), createFirstPanel());
  tabbed_pane.add(LangResource.getString("message#1001"),createLibraryPanel());
  tabbed_pane.add(LangResource.getString("message#1002"),createOptionsPanel());
/**
 * Notes.
 */
  KPanel notes_panel = new KPanel();
  notes_panel.setBorder(new EmptyBorder(5,5,5,5));
  notes_panel.setLayout(new BorderLayout());

  t_desc = new JTextArea(5, 1);
  t_desc.setLineWrap(true);
  t_desc.setWrapStyleWord(true);
  notes_panel.add(new JScrollPane(t_desc), BorderLayout.CENTER);
  tabbed_pane.add(LangResource.getString("message#1030"),notes_panel);

  add(tabbed_pane, BorderLayout.CENTER);
}
private KPanel createFirstPanel()
{
  KPanel first_panel = new KPanel();
  first_panel.setBorder(new EmptyBorder(5,5,5,5));

  GridBagLayout gb = new GridBagLayout();
  GridBagConstraints gbc = new GridBagConstraints();
  first_panel.setLayout(gb);

  gbc.anchor = gbc.NORTHWEST;
  gbc.fill = gbc.HORIZONTAL;
  gbc.weightx = 0;
  JLabel l;
/**
 * Name label and text field.
 */
  l = new JLabel(LangResource.getString("message#127") + ":");
  gbc.gridwidth = 1;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);

  t_name = new JTextField(12);
  t_name.setPreferredSize(new Dimension(10, 20));
  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(t_name, gbc);
/**
 * Version label and text field.
 */
  l = new JLabel(LangResource.getString("message#195") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);

  t_version = new JTextField(12);
  t_version.setPreferredSize(new Dimension(10, 20));
  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(t_version, gbc);
/**
 * JVM label, text field and browse button.
 */
  l = new JLabel(LangResource.getString("message#1006") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);
/**
 * Panel No.0.
 */
  KPanel p0 = new KPanel();
  p0.setLayout(new BorderLayout(5, 5));
  p0.setPreferredSize(new Dimension(10, 20));

  t_vm = new JTextField(12);
  t_vm.setPreferredSize(new Dimension(10, 20));
  t_vm.setOpaque(false);
  t_vm.setEditable(false);

  p0.add("Center", t_vm);

  b_browse0 = new KButton("...");
  b_browse0.setToolTipText(LangResource.getString("message#1007"));
  b_browse0.addActionListener(this);
  b_browse0.setDefaultCapable(false);
  p0.add("East", b_browse0);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(p0, gbc);
/**
 *  Jar with application.
 */
  l = new JLabel(LangResource.getString("message#168") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);
/**
 * Panel No.1.
 */
  KPanel p1 = new KPanel();
  p1.setLayout(new BorderLayout(5, 5));
  p1.setPreferredSize(new Dimension(10, 20));

  t_archive = new JTextField(12);
  t_archive.setPreferredSize(new Dimension(10, 20));
  p1.add("Center", t_archive);

  b_browse1 = new KButton("...");
  b_browse1.setToolTipText(LangResource.getString("message#191"));
  b_browse1.addActionListener(this);
  b_browse1.setDefaultCapable(false);
  p1.add("East", b_browse1);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(p1, gbc);
/**
 *  Application source.
 */
  l = new JLabel(LangResource.getString("message#199") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);
/**
 * Panel No.2.
 */
  KPanel p2 = new KPanel();
  p2.setLayout(new BorderLayout(5, 5));
  p2.setPreferredSize(new Dimension(10, 20));

  t_source = new JTextField(12);
  t_source.setPreferredSize(new Dimension(10, 20));
  p2.add("Center", t_source);

  b_browse2 = new KButton("...");
  b_browse2.setToolTipText(LangResource.getString("message#191"));
  b_browse2.addActionListener(this);
  b_browse2.setDefaultCapable(false);
  p2.add("East", b_browse2);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(p2, gbc);
/**
 *  Application documentation.
 */
  l = new JLabel(LangResource.getString("message#166") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);
/**
 * Panel No.6.
 */
  KPanel p6 = new KPanel();
  p6.setLayout(new BorderLayout(5, 5));
  p6.setPreferredSize(new Dimension(10, 20));

  t_docs = new JTextField(12);
  t_docs.setPreferredSize(new Dimension(10, 20));
  p6.add("Center", t_docs);

  b_browse4 = new KButton("...");
  b_browse4.setToolTipText(LangResource.getString("message#191"));
  b_browse4.addActionListener(this);
  b_browse4.setDefaultCapable(false);
  p6.add("East", b_browse4);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(p6, gbc);

/**
 *  Application working directory.
 */
  l = new JLabel(LangResource.getString("message#1008") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);
/**
 * Panel No.3.
 */
  KPanel p3 = new KPanel();
  p3.setLayout(new BorderLayout(5, 5));
  p3.setPreferredSize(new Dimension(10, 20));

  t_cwd = new JTextField(12);
  t_cwd.setPreferredSize(new Dimension(10, 20));
  p3.add("Center", t_cwd);

  b_browse3 = new KButton("...");
  b_browse3.setToolTipText(LangResource.getString("message#191"));
  b_browse3.addActionListener(this);
  b_browse3.setDefaultCapable(false);
  p3.add("East", b_browse3);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(p3, gbc);
/**
 * Application main class.
 */
  l = new JLabel(LangResource.getString("message#1009") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);

  KPanel pDet = new KPanel();
  pDet.setLayout(new BorderLayout(5, 5));
  pDet.setPreferredSize(new Dimension(10, 20));

  t_mainClass = new JTextField(12);
  t_mainClass.setPreferredSize(new Dimension(10, 20));
  pDet.add("Center", t_mainClass);

  b_detector = new KButton("?");
  b_detector.setToolTipText(LangResource.getString("message#1010"));
  b_detector.addActionListener(this);
  b_detector.setDefaultCapable(false);
  pDet.add("East", b_detector);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(pDet, gbc);

/**
 * Arguments.
 */
  l = new JLabel(LangResource.getString("message#172") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  first_panel.add(l, gbc);

  t_args = new JTextField(20);
  t_args.setPreferredSize(new Dimension(10, 20));
  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  first_panel.add(t_args, gbc);

  return first_panel;
}
private KPanel createLibraryPanel()
{
  KPanel lib_panel = new KPanel();
  lib_panel.setBorder(new EmptyBorder(5,5,5,5));
/**
 * Libraries.
 */
  GridBagLayout gb = new GridBagLayout();
  GridBagConstraints gbc = new GridBagConstraints();
  lib_panel.setLayout(gb);

  gbc.anchor = gbc.NORTHWEST;
  gbc.fill = gbc.HORIZONTAL;
  gbc.weightx = 0;
/**
 * Panel No.4.
 */
  KPanel p4 = new KPanel();

  p4.setLayout(new BorderLayout(5, 5));
  l_libs = new JList();
  l_libs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  l_libs.setMinimumSize(new Dimension(1, 75));
  model = new DefaultListModel();
  l_libs.setModel(model);
  p4.add("Center", new JScrollPane(l_libs));
/**
 * Buttons panel.
 */
  KPanel p_buttons = new KPanel();
  p_buttons.setLayout(new GridLayout(0, 1, 5, 5));
  ResourceManager kresmgr = KiwiUtils.getResourceManager();

  b_add = new KButton(kresmgr.getIcon("plus.gif"));
  b_add.setToolTipText(LangResource.getString("message#1011"));
  b_add.addActionListener(this);
  b_add.setDefaultCapable(false);
  p_buttons.add(b_add);

  b_remove = new KButton(kresmgr.getIcon("minus.gif"));
  b_remove.setToolTipText(LangResource.getString("message#1012"));
  b_remove.addActionListener(this);
  b_remove.setDefaultCapable(false);
  p_buttons.add(b_remove);
/**
 * Panel No.4.
 */
  KPanel p5 = new KPanel();
  p5.setLayout(new BorderLayout(5, 5));
  p5.add("North", p_buttons);

  p4.add("East", p5);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.weighty = 1;
  gbc.fill = gbc.BOTH;
  gbc.insets = KiwiUtils.lastInsets;
  lib_panel.add(p4, gbc);

  return lib_panel;
}
private KPanel createOptionsPanel()
{
  KPanel options_panel = new KPanel();
  options_panel.setBorder(new EmptyBorder(5,5,5,5));

  GridBagLayout gb = new GridBagLayout();
  GridBagConstraints gbc = new GridBagConstraints();
  options_panel.setLayout(gb);

  gbc.anchor = gbc.NORTHWEST;
  gbc.fill = gbc.VERTICAL;
  gbc.weightx = 0;

  b_launch_at_startup = new JCheckBox(LangResource.getString("message#1013"));
  b_launch_at_startup.setOpaque(false);
  gbc.insets = KiwiUtils.lastBottomInsets;
  gbc.gridwidth = gbc.REMAINDER;
  options_panel.add(b_launch_at_startup, gbc);

  b_separate_process = new JCheckBox(LangResource.getString("message#1014"));
  b_separate_process.setOpaque(false);
  b_separate_process.setEnabled(false);
  gbc.insets = KiwiUtils.lastBottomInsets;
  gbc.gridwidth = gbc.REMAINDER;
  options_panel.add(b_separate_process, gbc);

  b_system_user_folder = new JCheckBox(LangResource.getString("message#1015"));
  b_system_user_folder.setOpaque(false);
  gbc.insets = KiwiUtils.lastBottomInsets;
  gbc.gridwidth = gbc.REMAINDER;
  options_panel.add(b_system_user_folder, gbc);

  return options_panel;
}
public void actionPerformed(ActionEvent evt)
{
  Object o = evt.getSource();
  if (o == b_browse1)
  {
    File f = WorkspaceClassCache.chooseArchive(this);
    if (f != null)
      t_archive.setText(f.getPath());
  }
  else if (o == b_browse0)
  {
    if (d_vmChooser == null)
      d_vmChooser = new JvmChooserDialog(Workspace.getUI().getFrame());
    KiwiUtils.cascadeWindow(this, d_vmChooser);
    d_vmChooser.setVisible(true);
    if (d_vmChooser.isCancelled())
      return;
    jvm = d_vmChooser.getSelectedJVM();
    t_vm.setText(jvm.toString());
  }
  else if (o == b_browse3)
  {
    File dir = WorkspaceClassCache.chooseArchiveOrDir(this);
    if (dir != null)
      t_cwd.setText(dir.getPath());
  }
  else if (o == b_browse2)
  {
    File f = WorkspaceClassCache.chooseArchiveOrDir(this);
    if (f != null)
      t_source.setText(f.getPath());
  }
  else if (o == b_browse4)
  {
    File f = WorkspaceClassCache.chooseHTMLFile(this);
    if (f != null)
      t_docs.setText(f.getPath());
  }
  else if (o == b_add)
  {
    if (d_libChooser == null)
      d_libChooser = new LibraryChooserDialog(Workspace.getUI().getFrame());
      KiwiUtils.cascadeWindow(this, d_libChooser);
      d_libChooser.setVisible(true);
    if (d_libChooser.isCancelled())
      return;
    Library[] libs = d_libChooser.getSelectedLibraries();
    for (int i = 0; i < libs.length; i++)
       model.addElement(libs[i]);
  }
  else if (o == b_remove)
  {
    int index = l_libs.getSelectedIndex();
    if (index >= 0)
     model.removeElementAt(index);
  }
  else if (o == b_detector)
  {
    chooseMainClass();
  }
}
public void setData(Application data)
{
  application = data;
  t_name.setText(application.getName());
  t_version.setText(application.getVersion());
  t_archive.setText(application.getArchive());
  t_source.setText(application.getSource());
  t_docs.setText(application.getDocs());
  t_mainClass.setText(application.getMainClass());
  t_args.setText(application.getArguments());
  t_desc.setText(application.getDescription());
  t_cwd.setText(application.getWorkingDirectory());
  b_launch_at_startup.setSelected(application.isLoadedAtStartup());
  b_separate_process.setSelected(application.isSeparateProcess());
  b_system_user_folder.setSelected(!application.isSystemUserFolder());
  model.clear();

  Enumeration e = application.loadLibraries();
  while (e.hasMoreElements())
    model.addElement(e.nextElement());

  jvm = (JVM) Workspace.getInstallEngine().getJvmData().
      findNode(application.getJVM());

  if (jvm != null)
    t_vm.setText(jvm.toString());
  else
    t_vm.setText("");
}
public boolean syncData()
{
  try
  {
    application.setName(t_name.getText());
    application.setVersion(t_version.getText());
    application.setArchive(t_archive.getText());
    application.setSource(t_source.getText());
    application.setDocs(t_docs.getText());
    application.setMainClass(t_mainClass.getText());
    application.setJVM((jvm == null) ? null : jvm.getLinkString());
    application.setArguments(t_args.getText());
    application.setDescription(t_desc.getText());
    application.setLibraryList(model.elements());
    application.setWorkingDirectory(t_cwd.getText());
    application.setLoadedAtStartup(b_launch_at_startup.isSelected());
    application.setSeparateProcess(b_separate_process.isSelected());
    application.setSystemUserFolder(!b_system_user_folder.isSelected());
  }
  catch (InstallationException ex)
  {
    JOptionPane.showMessageDialog(this,
            "Application is not set up properly", "Warning",
            JOptionPane.WARNING_MESSAGE);
    return (false);
  }
  return (true);
}
/**
 * Autodetection of application main classes.
 */
protected void chooseMainClass()
{
   if (t_archive.getText() == null || t_archive.getText().equals(""))
   {
     JOptionPane.showMessageDialog(this, LangResource.getString("message#1017"));
     return;
   }
   File file = new File(t_archive.getText());

   if (!file.exists() || file.isDirectory())
   {
     JOptionPane.showMessageDialog(this, LangResource.getString("message#1018"));
     return;
   }
   executables.removeAllElements();
   pr = new ProgressDialog(Workspace.getUI().getFrame(),
                   LangResource.getString("message#1019"), true);
   Inspector inspector = new Inspector(file);
   pr.track(inspector);
   if (this.executables.size() == 1)
   {
      t_mainClass.setText((String) executables.elementAt(0));
   }
   else if(this.executables.size() > 1)
   {
      createOptionDlg();
   }
   else
   {
      JOptionPane.showMessageDialog(this, LangResource.getString("message#1020"));
   }
}
private void createOptionDlg()
{
  // Messages

   JComboBox cb = new JComboBox(executables.toArray());
   Object[] messages = new Object[] {cb};
   String[] options = new String[]{LangResource.getString("message#1021"), LangResource.getString("message#1022")};

   int result = JOptionPane.showOptionDialog(
        this,     // the parent that the dialog blocks
        messages,  // the dialog message array
        LangResource.getString("Installer"), // the title of the dialog window
        JOptionPane.DEFAULT_OPTION,  // option type
        JOptionPane.INFORMATION_MESSAGE, // message type
        null,   // optional icon, use null to use the default icon
        options,  // options string array, will be made into buttons
        options[0]  // option that should be made into a default button
    );
    switch(result) {
       case 0: // yes
         t_mainClass.setText((String)cb.getSelectedItem());
         break;
    }
}
}
