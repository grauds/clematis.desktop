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
import java.awt.Dimension;
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
import jworkspace.LangResource;
import jworkspace.ui.plaf.PlafFactory;
import jworkspace.ui.widgets.ThemesChooser;

/**
 * Panel for customizing look and feel of java workspace.
 */
class PlafPanel extends KPanel implements ActionListener {
    /**
     * Look and feel chooser
     */
    private LookAndFeelChooser lfchooser = null;
    /**
     * Themes chooser
     */
    private ThemesChooser themesChooser = null;
    /**
     * Look and feel comments field
     */
    private JTextArea comments = null;
    /**
     * Currently selected laf
     */
    private LookAndFeel selectedLaf = null;

    /**
     * Public constructor
     */
    public PlafPanel() {
        super();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        //***** laf chooser *****

        KPanel kp = new KPanel();
        kp.setBorder(new TitledBorder(LangResource.getString("PlafPanel.lafBorder.title")));
        kp.setLayout(new BorderLayout());

        getLfChooser().setPreferredSize(new Dimension(150, 20));
        getLfChooser().addActionListener(this);
        kp.add(getLfChooser(), BorderLayout.NORTH);
        kp.add(getTextAreaScroll(), BorderLayout.CENTER);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(kp, gbc);

        //*** themes chooser ********

        kp = new KPanel();
        kp.setBorder(new TitledBorder(LangResource.getString("PlafPanel.themesBorder.title")));
        kp.setLayout(new BorderLayout());

        getThemesChooser().setPreferredSize(new Dimension(150, 20));
        getThemesChooser().addActionListener(this);
        kp.add(getThemesChooser(), BorderLayout.CENTER);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.insets = KiwiUtils.LAST_INSETS;
        add(kp, gbc);
        /**
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
        } else if (e.getSource() == getThemesChooser()) {
            PlafFactory.getInstance().setCurrentTheme(getLfChooser().getLookAndFeel(),
                getThemesChooser().getTheme());
        }
    }

    private ThemesChooser getThemesChooser() {
        if (themesChooser == null) {
            themesChooser = new ThemesChooser();
        }
        return themesChooser;
    }

    /**
     * Fetch information about currently selected LAF. This includes
     * its description, plus ability to install themes and so on.
     */
    protected void fetchInfo() {
        /**
         * Get laf name
         */
        String selectedLafName = getLfChooser().getLookAndFeel();
        selectedLaf = PlafFactory.getInstance().getLookAndFeel(selectedLafName);
        /**
         * Get description of selected laf
         */
        if (selectedLaf != null) {
            getTextArea().setText(selectedLaf.getDescription());
            /**
             * Get a list of themes for a current LAF
             */
            MetalTheme[] themes = PlafFactory.getInstance().listThemes(selectedLaf);
            getThemesChooser().setModel(new DefaultComboBoxModel(themes));

            MetalTheme currentTheme = PlafFactory.getInstance().getCurrentTheme(selectedLaf);
            getThemesChooser().setSelectedItem(currentTheme);

            getThemesChooser().setEnabled(themes.length > 0);
        }
    }

    /**
     * Returns component for displaying description about
     * currently selected laf.
     *
     * @return text component for displaying description.
     */
    public JTextArea getTextArea() {
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
    protected JScrollPane getTextAreaScroll() {
        JScrollPane scrollPane = new JScrollPane(getTextArea());
        scrollPane.setPreferredSize(new Dimension(150, 60));
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        return scrollPane;
    }

    public boolean syncData() {
        return PlafFactory.getInstance().setLookAndFeel(getLfChooser().getLookAndFeel());
    }
}
