package jworkspace.ui.dialog;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2002 Anton Troshin

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

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;
import jworkspace.ui.WorkspaceClassCache;
import jworkspace.ui.WorkspaceGUI;
import jworkspace.ui.widgets.ImageRenderer;
import jworkspace.ui.widgets.ResourceExplorerDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * General settings panel for settings dialog.
 */
class TexturePanel extends KPanel implements ActionListener
{
    private JButton b_icon_browse, b_lib_browser;
    private ImageRenderer l_image;
    private JCheckBox ch_show_texture, ch_show_w_textures;

    public TexturePanel()
    {
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
        p1.setBorder(new TitledBorder
                (LangResource.getString("TexturePanel.textureBorder.title")));

        l_image = new jworkspace.ui.widgets.ImageRenderer();
        JScrollPane sp = new JScrollPane(l_image);
        sp.setPreferredSize(new Dimension(200, 200));
        p1.add("Center", sp);
        /**
         * Buttons panel
         */
        KPanel bp = new KPanel();
        bp.setLayout(new GridLayout(0, 1, 5, 5));

        ImageIcon icon = new ImageIcon(Workspace.
                                       getResourceManager().getImage("folder.png"));
        b_icon_browse = new JButton(icon);
        b_icon_browse.setToolTipText
                (LangResource.getString("TexturePanel.texture.browse"));
        b_icon_browse.addActionListener(this);
        b_icon_browse.setDefaultCapable(false);
        b_icon_browse.setOpaque(false);
        bp.add(b_icon_browse);

        icon = new ImageIcon(Workspace.
                             getResourceManager().getImage("repository.png"));
        b_lib_browser = new JButton(icon);
        b_lib_browser.setToolTipText
                (LangResource.getString("TexturePanel.textureRepos.browse"));
        b_lib_browser.addActionListener(this);
        b_lib_browser.setDefaultCapable(false);
        b_lib_browser.setOpaque(false);
        bp.add(b_lib_browser);

        KPanel p5 = new KPanel();
        p5.setLayout(new BorderLayout(5, 5));
        p5.add("North", bp);

        p1.add("East", p5);

        KPanel p6 = new KPanel();
        p6.setLayout(gb);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;

        ch_show_texture = new JCheckBox
                (LangResource.getString("TexturePanel.texture.show"));
        ch_show_texture.setOpaque(false);
        gbc.insets = KiwiUtils.lastBottomInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        p6.add(ch_show_texture, gbc);

        ch_show_w_textures = new JCheckBox
                (LangResource.getString("TexturePanel.texturesKiwi.show"));
        ch_show_w_textures.setOpaque(false);
        ch_show_w_textures.addActionListener(this);
        gbc.insets = KiwiUtils.lastBottomInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        p6.add(ch_show_w_textures, gbc);

        p1.add("South", p6);

        // end of icon chooser panel

        gbc.insets = KiwiUtils.lastBottomInsets;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(p1, gbc);
    }

    public void actionPerformed(ActionEvent evt)
    {
        Object o = evt.getSource();
        if (o == b_icon_browse)
        {
            Image im = WorkspaceClassCache.chooseImage(this);
            if (im != null)
                l_image.setImage(im);
        }
        else if (o == b_lib_browser)
        {
            ResourceExplorerDialog res_browser = new ResourceExplorerDialog
                    (Workspace.getUI().getFrame());
            callResourceBrowser(res_browser);
            /**
             * Delete all trash after disposal of large number of
             * graphic resources.
             */
            Runtime rt = Runtime.getRuntime();
            rt.gc();
            rt.runFinalization();
        }
        else if (o == ch_show_w_textures)
        {
            ((WorkspaceGUI) Workspace.getUI()).setKiwiTextureVisible
                    (ch_show_w_textures.isSelected());
        }
    }

    protected void callResourceBrowser(ResourceExplorerDialog res_browser)
    {
        res_browser.setHint(true);
        String path = Workspace.getProfilesEngine().getParameters().
                getString("TEXTURES_REPOSITORY");
        if (path == null && Workspace.getUI() instanceof WorkspaceGUI)
        {
            path = ((WorkspaceGUI) Workspace.getUI()).getTexturesPath();
            Workspace.getProfilesEngine().
                    getParameters().putString("TEXTURES_REPOSITORY", path);
        }
        res_browser.setData(path);
        res_browser.setVisible(true);

        if (!res_browser.isCancelled())
        {
            ImageIcon[] icons = res_browser.getSelectedImages();
            if (icons != null && icons.length != 0
                    && icons[0] != null)
            {
                l_image.setImage(icons[0].getImage());
            }
        }
    }

    public void setData()
    {
        l_image.setImage(((WorkspaceGUI) Workspace.getUI()).getTexture());
        ch_show_texture.setSelected
                (((WorkspaceGUI) Workspace.getUI()).isTextureVisible());
        ch_show_w_textures.setSelected
                (((WorkspaceGUI) Workspace.getUI()).isKiwiTextureVisible());
    }

    public boolean syncData()
    {
        /**
         * Set texture
         */
        Image imtexture = l_image.getImage();

        if (imtexture != null && Workspace.getUI() instanceof WorkspaceGUI) {

            ImageIcon textureIcon = new ImageIcon(imtexture);
            BufferedImage bi = new BufferedImage(
                    textureIcon.getIconWidth(),
                    textureIcon.getIconHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.createGraphics();
            // paint the Icon to the BufferedImage.
            textureIcon.paintIcon(null, g, 0,0);
            g.dispose();

            ((WorkspaceGUI) Workspace.getUI()).setTexture(bi);
        }

        if (Workspace.getUI() instanceof WorkspaceGUI)
        {
            if (ch_show_texture.isSelected())
            {
                ((WorkspaceGUI) Workspace.getUI()).setTextureVisible(true);
            }
            else
            {
                ((WorkspaceGUI) Workspace.getUI()).setTextureVisible(false);
            }
        }
        else
        {
            // ERROR possibly not Workspace GUI
        }
        /**
         * Set flag whether the texture is visible
         */
        return true;
    }
}
