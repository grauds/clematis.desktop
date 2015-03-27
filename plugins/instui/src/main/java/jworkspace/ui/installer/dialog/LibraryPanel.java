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
import javax.swing.*;
import javax.swing.border.*;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import kiwi.ui.*;

import jworkspace.installer.InstallationException;
import jworkspace.installer.Library;
import jworkspace.ui.WorkspaceClassCache;
import jworkspace.ui.installer.*;
/**
 * Library configuration panel.
 */
class LibraryPanel extends KPanel implements ActionListener
  {

  private JTextField t_name, t_version,
           t_docs, t_archive, t_source;
  private KButton b_browse, b_browse1, b_browse2;
  private JTextArea t_desc;
  private Library library;


  LibraryPanel()
  {
  GridBagLayout gb = new GridBagLayout();
  GridBagConstraints gbc = new GridBagConstraints();
  setLayout(gb);

  gbc.anchor = gbc.NORTHWEST;
  gbc.fill = gbc.HORIZONTAL;
  gbc.weightx = 0;
  JLabel l;

  JSeparator sep = new JSeparator();
  gbc.gridwidth = gbc.REMAINDER;
  gbc.insets = KiwiUtils.lastInsets;
  add(sep, gbc);

  l = new JLabel(LangResource.getString("message#127") + ":");
  gbc.gridwidth = 1;
  gbc.insets = KiwiUtils.firstInsets;
  add(l, gbc);

  t_name = new JTextField(20);
  t_name.setPreferredSize(new Dimension(150, 20));
  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  add(t_name, gbc);

  l = new JLabel(LangResource.getString("message#195") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  add(l, gbc);

  t_version = new JTextField(20);
  t_version.setPreferredSize(new Dimension(150, 20));
  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  add(t_version, gbc);

  l = new JLabel(LangResource.getString("message#168") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  add(l, gbc);

  KPanel p1 = new KPanel();
  p1.setLayout(new BorderLayout(5, 5));
  p1.setPreferredSize(new Dimension(150, 20));

  t_archive = new JTextField(20);
  p1.add("Center", t_archive);

  b_browse = new KButton("...");
  b_browse.setToolTipText(LangResource.getString("message#191"));
  b_browse.addActionListener(this);
  b_browse.setDefaultCapable(false);
  p1.add("East", b_browse);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  add(p1, gbc);

  l = new JLabel(LangResource.getString("message#199") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  add(l, gbc);

  KPanel p2 = new KPanel();
  p2.setLayout(new BorderLayout(5, 5));
  p2.setPreferredSize(new Dimension(150, 20));

  t_source = new JTextField(20);
  p2.add("Center", t_source);

  b_browse2 = new KButton("...");
  b_browse2.setToolTipText(LangResource.getString("message#191"));
  b_browse2.addActionListener(this);
  b_browse2.setDefaultCapable(false);
  p2.add("East", b_browse2);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  add(p2, gbc);

  l = new JLabel(LangResource.getString("message#166") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstInsets;
  add(l, gbc);

  KPanel p3 = new KPanel();
  p3.setLayout(new BorderLayout(5, 5));
  p3.setPreferredSize(new Dimension(150, 20));

  t_docs = new JTextField(20);
  p3.add("Center", t_docs);

  b_browse1 = new KButton("...");
  b_browse1.setToolTipText(LangResource.getString("message#191"));
  b_browse1.addActionListener(this);
  b_browse1.setDefaultCapable(false);
  p3.add("East", b_browse1);

  gbc.gridwidth = gbc.REMAINDER;
  gbc.weightx = 1;
  gbc.insets = KiwiUtils.lastInsets;
  add(p3, gbc);

  l = new JLabel(LangResource.getString("message#155") + ":");
  gbc.gridwidth = 1;
  gbc.weightx = 0;
  gbc.insets = KiwiUtils.firstBottomInsets;
  add(l, gbc);

  gbc.fill = gbc.BOTH;
  gbc.gridwidth = gbc.REMAINDER;
  gbc.weighty = 1;
  t_desc = new JTextArea(5, 20);
  t_desc.setLineWrap(true);
  t_desc.setWrapStyleWord(true);
  gbc.insets = KiwiUtils.lastBottomInsets;
  add(new JScrollPane(t_desc), gbc);

  setBorder(new EmptyBorder(0,5,5,5));
}
public void actionPerformed(ActionEvent evt)
{
  Object o = evt.getSource();
  if (o == b_browse)
  {
    File f = WorkspaceClassCache.chooseArchive(this);
    if (f != null)
      t_archive.setText(f.getPath());
  }
  else if (o == b_browse2)
  {
    File f = WorkspaceClassCache.chooseArchiveOrDir(this);
    if (f != null)
      t_source.setText(f.getPath());
  }
  else if (o == b_browse1)
  {
    File f = WorkspaceClassCache.chooseHTMLFile(this);
    if (f != null)
      t_docs.setText(f.getPath());
  }
}
public void setData(jworkspace.installer.DefinitionNode data)
{
  library = (Library) data;
  t_name.setText(library.getName());
  t_version.setText(library.getVersion());
  t_archive.setText(library.getPath());
  t_desc.setText(library.getDescription());
  t_source.setText(library.getSource());
  t_docs.setText(library.getDocs());
}
public boolean syncData()
{
  try
  {
    library.setName(t_name.getText());
    library.setVersion(t_version.getText());
    library.setPath(t_archive.getText());
    library.setDescription(t_desc.getText());
    library.setSource(t_source.getText());
    library.setDocs(t_docs.getText());
  }
  catch (InstallationException ex)
  {
    JOptionPane.showMessageDialog(this,
       "JVM is not set up properly", "Warning",
       JOptionPane.WARNING_MESSAGE);
    return (false);
  }
  return (true);
}
}
