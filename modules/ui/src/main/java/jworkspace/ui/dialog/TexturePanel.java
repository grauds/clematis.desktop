package jworkspace.ui.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2016 Anton Troshin

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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.kernel.Workspace;
import jworkspace.ui.ClassCache;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.widgets.ImageRenderer;
import jworkspace.ui.widgets.ResourceExplorerDialog;

/**
 * General settings panel for settings dialog.
 */
class TexturePanel extends KPanel implements ActionListener {
    private static final String TEXTURES_REPOSITORY = "TEXTURES_REPOSITORY";
    private JButton bIconBrowse, bLibBrowser;
    private ImageRenderer lImage;
    private JCheckBox chShowTexture, chShowWTextures;

    @SuppressWarnings("MagicNumber")
    TexturePanel() {
        super();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        KPanel p1 = new KPanel();
        p1.setLayout(new BorderLayout(5, 5));
        p1.setBorder(new EmptyBorder(0, 0, 5, 0));
        p1.setBorder(new TitledBorder(WorkspaceResourceAnchor.getString("TexturePanel.textureBorder.title")));

        lImage = new jworkspace.ui.widgets.ImageRenderer();
        JScrollPane sp = new JScrollPane(lImage);
        sp.setPreferredSize(new Dimension(200, 200));
        p1.add("Center", sp);

        KPanel bp = new KPanel();
        bp.setLayout(new GridLayout(0, 1, 5, 5));

        ImageIcon icon = new ImageIcon(WorkspaceGUI.getResourceManager().getImage("folder.png"));
        bIconBrowse = new JButton(icon);
        bIconBrowse.setToolTipText(WorkspaceResourceAnchor.getString("TexturePanel.texture.browse"));
        bIconBrowse.addActionListener(this);
        bIconBrowse.setDefaultCapable(false);
        bIconBrowse.setOpaque(false);
        bp.add(bIconBrowse);

        icon = new ImageIcon(WorkspaceGUI.getResourceManager().getImage("repository.png"));
        bLibBrowser = new JButton(icon);
        bLibBrowser.setToolTipText(WorkspaceResourceAnchor.getString("TexturePanel.textureRepos.browse"));
        bLibBrowser.addActionListener(this);
        bLibBrowser.setDefaultCapable(false);
        bLibBrowser.setOpaque(false);
        bp.add(bLibBrowser);

        KPanel p5 = new KPanel();
        p5.setLayout(new BorderLayout(5, 5));
        p5.add("North", bp);

        p1.add("East", p5);

        KPanel p6 = new KPanel();
        p6.setLayout(gb);

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;

        chShowTexture = new JCheckBox(WorkspaceResourceAnchor.getString("TexturePanel.texture.show"));
        chShowTexture.setOpaque(false);
        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        p6.add(chShowTexture, gbc);

        chShowWTextures = new JCheckBox(WorkspaceResourceAnchor.getString("TexturePanel.texturesKiwi.show"));
        chShowWTextures.setOpaque(false);
        chShowWTextures.addActionListener(this);
        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        p6.add(chShowWTextures, gbc);

        p1.add("South", p6);

        gbc.insets = KiwiUtils.LAST_BOTTOM_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(p1, gbc);
    }

    public void actionPerformed(ActionEvent evt) {
        Object o = evt.getSource();
        if (o == bIconBrowse) {
            Image im = ClassCache.chooseImage(this);
            if (im != null) {
                lImage.setImage(im);
            }
        } else if (o == bLibBrowser) {
            ResourceExplorerDialog resBrowser = new ResourceExplorerDialog(Workspace.getUi().getFrame());
            callResourceBrowser(resBrowser);
        } else if (o == chShowWTextures) {
            ((WorkspaceGUI) Workspace.getUi()).setKiwiTextureVisible(chShowWTextures.isSelected());
        }
    }

    private void callResourceBrowser(ResourceExplorerDialog resBrowser) {
        resBrowser.setHint(true);
        String path = Workspace.getUserManager().getParameters().getString(TEXTURES_REPOSITORY);
        if (path == null && Workspace.getUi() instanceof WorkspaceGUI) {

            path = WorkspaceGUI.getTexturesPath().toFile().getAbsolutePath();
            Workspace.getUserManager().getParameters().putString(TEXTURES_REPOSITORY, path);
        }
        resBrowser.setData(path);
        resBrowser.setVisible(true);

        if (!resBrowser.isCancelled()) {
            ImageIcon[] icons = resBrowser.getSelectedImages();
            if (icons != null && icons.length != 0
                && icons[0] != null) {
                lImage.setImage(icons[0].getImage());
            }
        }
    }

    public void setData() {
        lImage.setImage(((WorkspaceGUI) Workspace.getUi()).getTexture());
        chShowTexture.setSelected(((WorkspaceGUI) Workspace.getUi()).isTextureVisible());
        chShowWTextures.setSelected(((WorkspaceGUI) Workspace.getUi()).isKiwiTextureVisible());
    }

    public boolean syncData() {
        /*
         * Set texture
         */
        Image imtexture = lImage.getImage();

        if (imtexture != null && Workspace.getUi() instanceof WorkspaceGUI) {

            ImageIcon textureIcon = new ImageIcon(imtexture);
            BufferedImage bi = new BufferedImage(
                textureIcon.getIconWidth(),
                textureIcon.getIconHeight(),
                BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.createGraphics();
            // paint the Icon to the BufferedImage.
            textureIcon.paintIcon(null, g, 0, 0);
            g.dispose();

            ((WorkspaceGUI) Workspace.getUi()).setTexture(bi);
        }

        if (Workspace.getUi() instanceof WorkspaceGUI) {
            if (chShowTexture.isSelected()) {
                ((WorkspaceGUI) Workspace.getUi()).setTextureVisible(true);
            } else {
                ((WorkspaceGUI) Workspace.getUi()).setTextureVisible(false);
            }
        }
        /*
         * Set flag whether the texture is visible
         */
        return true;
    }
}
