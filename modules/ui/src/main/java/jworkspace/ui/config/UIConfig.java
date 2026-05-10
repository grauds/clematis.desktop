package jworkspace.ui.config;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2025 Anton Troshin

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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import com.hyperrealm.kiwi.io.ConfigFile;

import jworkspace.ui.api.Constants;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;

/**
 * @author Anton Troshin
 */
@Log
@Setter
@Getter
public class UIConfig {

    public static final String CK_TEXTURE_FILE_NAME = "texture.jpg";
    public static final String CK_TEXTURE = "gui.texture";
    public static final String CK_TEXTURE_VISIBLE = "gui.texture.visible";
    public static final String CK_LAF = "gui.laf";
    public static final String CK_KIWI = "gui.kiwi.texture.visible";
    public static final String CK_UNDECORATED = "gui.frame.undecorated";
    /**
     * Workspace background texture
     */
    private Image texture = null;
    /**
     * Configuration
     */
    private ConfigFile config;

    public UIConfig() {}

    public UIConfig(@NonNull File configFile) {
        this.setConfigFile(configFile);
    }

    public void setConfigFile(@NonNull File configFile) {
        this.config = new ConfigFile(configFile, "GUI Related Properties");
    }

    public boolean isTextureVisible() {
        return config.getBoolean(CK_TEXTURE_VISIBLE, false);
    }

    public void setTextureVisible(boolean b) {
        config.putBoolean(CK_TEXTURE_VISIBLE, b);
    }

    public boolean isKiwiTextureVisible() {
        return config.getBoolean(CK_KIWI, true);
    }

    public void setKiwiTextureVisible(boolean visible) {
        config.putBoolean(CK_KIWI, visible);
    }

    public String getLaf() {
        return config.getString(CK_LAF, Constants.DEFAULT_LAF);
    }

    public void saveLaf() {
        if (UIManager.getLookAndFeel() != null) {
            config.putString(CK_LAF, UIManager.getLookAndFeel().getClass().getName());
        }
    }

    public boolean isDecorated() {
        return config.getBoolean(CK_UNDECORATED, false);
    }

    public void load() {

        try {
            config.load();
        } catch (IOException e) {
            log.warning("Couldn't load the config, continuing with defaults");
        }

        try {
            setTexture(ImageIO.read(
                Paths.get(this.config.getPath()).getParent().resolve(CK_TEXTURE_FILE_NAME).toFile()
            ));

        } catch (IOException e) {
            log.warning("Cannot read texture: " + e.getMessage());
            setTextureVisible(false);

        }
    }

    public void save() {

        try {
            saveLaf();
            config.store();
        } catch (IOException ex) {
            log.severe(ex.getMessage());
        }

        if (getTexture() != null) {

            try (OutputStream os = new FileOutputStream(
                Paths.get(this.config.getPath()).getParent().resolve(CK_TEXTURE_FILE_NAME).toFile()
            )) {

                ImageIcon textureIcon = new ImageIcon(getTexture());
                BufferedImage bi = new BufferedImage(textureIcon.getIconWidth(), textureIcon.getIconHeight(),
                    BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.createGraphics();
                // paint the Icon to the BufferedImage.
                textureIcon.paintIcon(null, g, 0, 0);
                g.dispose();
                if (textureIcon.getIconHeight() > 0 && textureIcon.getIconWidth() > 0) {
                    ImageIO.write(bi, "JPEG", os);
                }

            } catch (IOException e) {
                log.warning("Cannot write texture: " + e.getMessage());
            }
        }
    }

    public String getString(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    public String putString(String key, String value) {
        return config.putString(key, value);
    }

    public Object remove(String key) {
        return config.remove(key);
    }

    public String getString(String key) {
        return config.getString(key);
    }
}