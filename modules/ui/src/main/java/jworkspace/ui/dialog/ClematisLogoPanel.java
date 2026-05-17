package jworkspace.ui.dialog;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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

   Author may be contacted at:

   anton.troshin@gmail.com
   ----------------------------------------------------------------------------
*/
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.hyperrealm.kiwi.util.KiwiUtils;

import static jworkspace.ui.util.VersionUtils.getBuildString;
import jworkspace.Workspace;
import jworkspace.api.IWorkspaceUI;
import jworkspace.installer.WorkspaceInstaller;
import jworkspace.runtime.RuntimeManager;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.users.ProfilesManager;


public class ClematisLogoPanel extends JPanel {

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    public ClematisLogoPanel() {
        // Set up a clean white background
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 0));

        ImageIcon flowerIcon = new ImageIcon(
            WorkspaceGUI.getResourceManager().getImage("logo/Logo_L.png")
                .getScaledInstance(303, 370, Image.SCALE_SMOOTH)
        );
        JLabel iconLabel = new JLabel(flowerIcon);
        add(iconLabel, BorderLayout.WEST);

        JPanel textContainer = new JPanel();
        textContainer.setBackground(Color.WHITE);
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Clematis");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        // Deep Purple #4B10B0
        titleLabel.setForeground(new Color(75, 16, 176));

        JLabel subtitleLabel = new JLabel("THE JAVA WORKSPACE");
        subtitleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        // Dark Gray

        JLabel featuresLabel = new JLabel("Launch  •  Extend  •  Integrate");
        featuresLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        featuresLabel.setForeground(new Color(75, 16, 176));

        JLabel descLabel = new JLabel("Java applications. Plugins. Native tools.");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(new Color(119, 119, 119));
        // Lighter Gray

        Font versionFont = new Font("SansSerif", Font.PLAIN, 10);
        Color tagColor = Color.decode("#4B10B0");
        Color infoColor = Color.decode("#888888");

        JLabel systemLabel = new JLabel(String.format(
            "<html><font color='#%s'><b>Workspace:</b></font> &nbsp; <font color='#%s'>%s</font></html>",
            tagColor, infoColor, getBuildString(Workspace.class)
        ));
        systemLabel.setFont(versionFont);

        JLabel apiLabel = new JLabel(String.format(
            "<html><font color='#%s'><b>API:</b></font> &nbsp; <font color='#%s'>%s</font></html>",
            tagColor, infoColor, getBuildString(IWorkspaceUI.class)
        ));
        apiLabel.setFont(versionFont);

        JLabel profilesLabel = new JLabel(String.format(
            "<html><font color='#%s'><b>Profiles:</b></font> &nbsp; <font color='#%s'>%s</font></html>",
            tagColor, infoColor, getBuildString(ProfilesManager.class)
        ));
        profilesLabel.setFont(versionFont);

        JLabel runtimeLabel = new JLabel(String.format(
            "<html><font color='#%s'><b>Runtime:</b></font> &nbsp; <font color='#%s'>%s</font></html>",
            tagColor, infoColor, getBuildString(RuntimeManager.class)
        ));
        runtimeLabel.setFont(versionFont);

        JLabel installerLabel = new JLabel(String.format(
            "<html><font color='#%s'><b>Installer:</b></font> &nbsp; <font color='#%s'>%s</font></html>",
            tagColor, infoColor, getBuildString(WorkspaceInstaller.class)
        ));
        installerLabel.setFont(versionFont);

        JLabel kiwiLabel = new JLabel(String.format(
            "<html><font color='#%s'><b>Kiwi:</b></font> &nbsp; <font color='#%s'>%s</font></html>",
            tagColor, infoColor, getBuildString(KiwiUtils.class)
        ));
        kiwiLabel.setFont(versionFont);

        JLabel uiLabel = new JLabel(String.format(
            "<html><font color='#%s'><b>UI:</b></font> &nbsp; <font color='#%s'>%s</font></html>",
            tagColor, infoColor, getBuildString(WorkspaceGUI.class)
        ));
        uiLabel.setFont(versionFont);

        // Assembly Layout Flow
        textContainer.add(Box.createVerticalGlue());
        textContainer.add(titleLabel);
        textContainer.add(Box.createVerticalStrut(2));
        textContainer.add(subtitleLabel);
        textContainer.add(Box.createVerticalStrut(15));
        textContainer.add(featuresLabel);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(descLabel);

        // Inject the hyper-compact text block
        textContainer.add(Box.createVerticalStrut(14));
        textContainer.add(systemLabel);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(apiLabel);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(profilesLabel);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(installerLabel);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(runtimeLabel);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(kiwiLabel);
        textContainer.add(Box.createVerticalStrut(4));
        textContainer.add(uiLabel);

        textContainer.add(Box.createVerticalGlue());
        textContainer.setBorder(new  EmptyBorder(0, 0, 0, 20));

        add(textContainer, BorderLayout.CENTER);
    }
}
