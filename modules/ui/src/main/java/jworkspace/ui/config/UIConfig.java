package jworkspace.ui.config;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTheme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperrealm.kiwi.io.ConfigFile;

import jworkspace.kernel.Workspace;
import jworkspace.ui.api.Constants;
import jworkspace.ui.config.plaf.PlafFactory;
import lombok.NonNull;

/**
 * @author Anton Troshin
 */
public class UIConfig {

    /**
     *
     */
    public static final String CK_TEXTURE_FILE_NAME = "texture.jpg";
    /**
     *
     */
    public static final String CK_TEXTURE = "gui.texture";
    /**
     *
     */
    public static final String CK_TEXTURE_VISIBLE = "gui.texture.visible";
    /**
     *
     */
    public static final String CK_LAF = "gui.laf";
    /**
     *
     */
    public static final String CK_THEME = "gui.theme";
    /**
     *
     */
    public static final String CK_KIWI = "gui.kiwi.texture.visible";
    /**
     *
     */
    public static final String CK_UNDECORATED = "gui.frame.undecorated";
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(UIConfig.class);
    /**
     * Workspace background texture
     */
    private Image texture = null;
    /**
     * Configuration
     */
    private ConfigFile config;

    public UIConfig(@NonNull File configFile) {
        this.config = new ConfigFile(configFile, "GUI Related Properties");
    }

    /**
     * Returns texture image for settings dialog
     */
    public Image getTexture() {
        return texture;
    }

    /**
     * Set texture for java workspace gui
     */
    public void setTexture(Image texture) {
        this.texture = texture;
    }

    /**
     * Is texture visible
     */
    public boolean isTextureVisible() {
        return config.getBoolean(CK_TEXTURE_VISIBLE, false);
    }


    public void setTextureVisible(boolean b) {
        config.putBoolean(CK_TEXTURE_VISIBLE, b);
    }

    /**
     * Is Kiwi texture visible?
     */
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

    public String getTheme() {
        return config.getString(CK_THEME, "");
    }

    public void saveTheme() {
        MetalTheme theme = PlafFactory.getInstance().getCurrentTheme();
        if (theme != null) {
            config.putString(CK_THEME, theme.getClass().getName());
        } else {
            config.remove(CK_THEME);
        }
    }

    public boolean isDecorated() {
        return config.getBoolean(CK_UNDECORATED, false);
    }

    /**
     * Loads workspace GUI configuration
     */
    public void load() {

        try {
            config.load();

        } catch (IOException e) {
            LOG.warn("Couldn't load the config, continuing with defaults");
        }
        /*
         * Load recently used texture
         */
        try {
            /*
             * Read texture
             */
            setTexture(ImageIO.read(Workspace.ensureUserHomePath().resolve(CK_TEXTURE_FILE_NAME).toFile()));

        } catch (IOException e) {
            LOG.warn("Cannot read texture: " + e.getMessage());
            setTextureVisible(false);

        }
    }

    /**
     * Saves workspace GUI configuration
     */
    public void save() {

        try {
            saveLaf();
            saveTheme();
            config.store();
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        /*
         * Write texture on disk
         */
        if (getTexture() != null) {

            try (OutputStream os = new FileOutputStream(Workspace.ensureUserHomePath()
                .resolve(CK_TEXTURE_FILE_NAME).toFile())) {

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
                LOG.warn("Cannot write texture: " + e.getMessage());
            }
        }
    }

}