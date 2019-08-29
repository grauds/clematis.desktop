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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalTheme;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.LookAndFeelChooser;
import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.ui.config.plaf.PlafFactory;
import jworkspace.ui.widgets.ThemeChooser;

/**
 * Panel for customizing look and feel of java workspace.
 */
@SuppressWarnings("MagicNumber")
class PlafPanel extends KPanel implements ActionListener {
    /**
     * Look and feel chooser
     */
    private LookAndFeelChooser lfchooser = null;
    /**
     * Themes chooser
     */
    private ThemeChooser themeChooser = null;
    /**
     * Look and feel comments field
     */
    private JTextArea comments = null;

    /**
     * Public constructor
     */
    PlafPanel() {
        super();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        //***** laf chooser *****

        KPanel kp = new KPanel();
        kp.setBorder(new TitledBorder(WorkspaceResourceAnchor.getString("PlafPanel.lafBorder.title")));
        kp.setLayout(new BorderLayout());

     //   getLfChooser().setPreferredSize(new Dimension(150, 20));
        getLfChooser().addActionListener(this);
        kp.add(getLfChooser(), BorderLayout.NORTH);
        kp.add(getTextAreaScroll(), BorderLayout.CENTER);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(kp, gbc);

        //*** themes chooser ********

        kp = new KPanel();
        kp.setBorder(new TitledBorder(WorkspaceResourceAnchor.getString("PlafPanel.themesBorder.title")));
        kp.setLayout(new BorderLayout());

   //     getThemeChooser().setPreferredSize(new Dimension(150, 20));
        getThemeChooser().addActionListener(this);
        kp.add(getThemeChooser(), BorderLayout.CENTER);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(kp, gbc);
        /*
         * Fetch info about currently selected laf.
         */
        fetchInfo();
    }

    private LookAndFeelChooser getLfChooser() {
        if (lfchooser == null) {
            PlafFactory.getInstance();
            lfchooser = new LookAndFeelChooser();
        }
        return lfchooser;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getLfChooser()) {
            fetchInfo();
        } else if (e.getSource() == getThemeChooser()) {
            PlafFactory.getInstance().setCurrentTheme(getLfChooser().getLookAndFeel(),
                getThemeChooser().getTheme());
        }
    }

    private ThemeChooser getThemeChooser() {
        if (themeChooser == null) {
            themeChooser = new ThemeChooser();
        }
        return themeChooser;
    }

    /**
     * Fetch information about currently selected LAF. This includes
     * its description, plus ability to install themes and so on.
     */
    private void fetchInfo() {
        /*
         * Get laf name
         */
        String selectedLafName = getLfChooser().getLookAndFeel();
        /*
         * Currently selected laf
         */
        LookAndFeel selectedLaf = PlafFactory.getInstance().getLookAndFeel(selectedLafName);
        /*
         * Get description of selected laf
         */
        if (selectedLaf != null) {
            getTextArea().setText(selectedLaf.getDescription());
            /*
             * Get a list of themes for a current LAF
             */
            MetalTheme[] themes = PlafFactory.getInstance().listThemes(selectedLaf);
            getThemeChooser().setModel(new DefaultComboBoxModel<>(themes));

            MetalTheme currentTheme = PlafFactory.getInstance().getCurrentTheme(selectedLaf);
            getThemeChooser().setSelectedItem(currentTheme);

            getThemeChooser().setEnabled(themes.length > 0);
        }
    }

    /**
     * Returns component for displaying description about
     * currently selected laf.
     *
     * @return text component for displaying description.
     */
    private JTextArea getTextArea() {
        if (comments == null) {
            comments = new JTextArea();
            comments.setEditable(false);
            comments.setBackground(this.getBackground());
            comments.setLineWrap(true);
            comments.setWrapStyleWord(true);
        }
        return comments;
    }

    /**
     * Scroll pane for text area
     *
     * @return scroll pane
     */
    private JScrollPane getTextAreaScroll() {
        JScrollPane scrollPane = new JScrollPane(getTextArea());
  //      scrollPane.setPreferredSize(new Dimension(150, 60));
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        return scrollPane;
    }

    boolean syncData() {
        return PlafFactory.getInstance().setLookAndFeel(getLfChooser().getLookAndFeel());
    }
}
