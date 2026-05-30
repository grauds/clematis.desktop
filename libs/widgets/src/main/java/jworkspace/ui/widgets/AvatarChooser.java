package jworkspace.ui.widgets;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import com.hyperrealm.kiwi.ui.ButtonPanel;
import com.hyperrealm.kiwi.ui.KButton;
import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;


public class AvatarChooser extends KPanel implements ActionListener {

    private static final int MAX_PORTRAIT_WIDTH = 250;
    private static final int MAX_PORTRAIT_HEIGHT = 250;

    private final ResourceManager kresmgr = KiwiUtils.getResourceManager();

    private KButton addButton;
    private KButton deleteButton;
    private KButton confirmButton; // The OK button

    // New physical zoom buttons
    private KButton zoomInButton;
    private KButton zoomOutButton;

    private Avatar avatar;
    private final ImageIcon noIcon;
    private ImageIcon rawUncroppedIcon;

    private boolean isNewUnclippedImage = false;

    public AvatarChooser(ImageIcon noIcon) {
        this.noIcon = noIcon;

        setLayout(new BorderLayout());

        ButtonPanel buttonPanel = new ButtonPanel();
        buttonPanel.addButton(getAddButton());

        // Add the zoom buttons and confirmation button to the panel line
        buttonPanel.addButton(getZoomInButton());
        buttonPanel.addButton(getZoomOutButton());
        buttonPanel.addButton(getConfirmButton());

        buttonPanel.addButton(getDeleteButton());

        add(getAvatar(), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateButtonStates();
    }

    public KButton getAddButton() {
        if (addButton == null) {
            addButton = new KButton(new ImageIcon(kresmgr.getImage("plus.png")));
            addButton.addActionListener(this);
            addButton.setDefaultCapable(false);
        }
        return addButton;
    }

    public KButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = new KButton(new ImageIcon(kresmgr.getImage("minus.png")));
            deleteButton.addActionListener(this);
            deleteButton.setDefaultCapable(false);
        }
        return deleteButton;
    }

    public KButton getConfirmButton() {
        if (confirmButton == null) {
            confirmButton = new KButton("OK");
            confirmButton.addActionListener(this);
            confirmButton.setDefaultCapable(true);
        }
        return confirmButton;
    }

    // Lazy initialization for the Zoom In button
    public KButton getZoomInButton() {
        if (zoomInButton == null) {
            zoomInButton = new KButton("+");
            zoomInButton.addActionListener(this);
            zoomInButton.setDefaultCapable(false);
        }
        return zoomInButton;
    }

    // Lazy initialization for the Zoom Out button
    public KButton getZoomOutButton() {
        if (zoomOutButton == null) {
            zoomOutButton = new KButton("-");
            zoomOutButton.addActionListener(this);
            zoomOutButton.setDefaultCapable(false);
        }
        return zoomOutButton;
    }

    public Avatar getAvatar() {
        if (avatar == null) {
            avatar = new Avatar();
            avatar.setPreferredSize(new Dimension(MAX_PORTRAIT_WIDTH, MAX_PORTRAIT_HEIGHT));
            avatar.setIcon(noIcon);
        }
        return avatar;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.addButton) {
            Image image = ClassCache.chooseImage(this);
            if (image != null) {
                rawUncroppedIcon = new ImageIcon(image);
                isNewUnclippedImage = true;

                getAvatar().resetTransformations();
                scaleInitialImage(rawUncroppedIcon);
                getAvatar().setEditable(true);
            }
        } else if (e.getSource() == this.zoomInButton) {
            if (getAvatar().isEditable()) {
                // Increase scale by 10%, capping it at a maximum value of 10.0
                double currentScale = getAvatar().getImageScale();
                getAvatar().setImageScale(Math.min(10.0, currentScale * 1.1));
            }
        } else if (e.getSource() == this.zoomOutButton) {
            if (getAvatar().isEditable()) {
                // Decrease scale by 10%, capping it at a minimum value of 0.1
                double currentScale = getAvatar().getImageScale();
                getAvatar().setImageScale(Math.max(0.1, currentScale * 0.9));
            }
        } else if (e.getSource() == this.confirmButton) {
            if (getAvatar().isEditable() && isNewUnclippedImage) {
                ImageIcon cropped = getAvatar().getCroppedIcon();
                getAvatar().resetTransformations();
                getAvatar().setIcon(cropped);
                getAvatar().setEditable(false);
                isNewUnclippedImage = false;
            }
        } else if (e.getSource() == this.deleteButton) {
            rawUncroppedIcon = null;
            isNewUnclippedImage = false;
            getAvatar().resetTransformations();
            getAvatar().setIcon(noIcon);
            getAvatar().setEditable(false);
        }
        updateButtonStates();
        repaint();
    }

    /**
     * Controls button visibility rules based on editing states.
     */
    private void updateButtonStates() {
        boolean showEditingControls = isNewUnclippedImage && getAvatar().getIcon() != noIcon;

        // Toggle visibility for OK, Zoom In, and Zoom Out buttons all at once
        getConfirmButton().setVisible(showEditingControls);
        getZoomInButton().setVisible(showEditingControls);
        getZoomOutButton().setVisible(showEditingControls);

        // Ensure layout container re-aligns elements when hidden/shown
        if (getConfirmButton().getParent() != null) {
            getConfirmButton().getParent().revalidate();
        }
    }

    public void setIcon(ImageIcon icon) {
        rawUncroppedIcon = icon;
        isNewUnclippedImage = false;
        getAvatar().resetTransformations();
        getAvatar().setEditable(false);
        scaleInitialImage(icon);
        updateButtonStates();
    }

    private void scaleInitialImage(ImageIcon photo) {
        if (photo == null || photo == noIcon) {
            getAvatar().setIcon(noIcon);
            return;
        }

        int iw = photo.getIconWidth();
        int ih = photo.getIconHeight();

        double xScale = (double) MAX_PORTRAIT_WIDTH / iw;
        double yScale = (double) MAX_PORTRAIT_HEIGHT / ih;
        double scale = Math.max(xScale, yScale);

        if (scale < 1.0) {
            int targetWidth = (int) (iw * scale);
            int targetHeight = (int) (ih * scale);
            Image scaledImg = photo.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            getAvatar().setIcon(new ImageIcon(scaledImg));
        } else {
            getAvatar().setIcon(photo);
        }
    }

    public ImageIcon getIcon() {
        return getAvatar().getIcon();
    }
}

