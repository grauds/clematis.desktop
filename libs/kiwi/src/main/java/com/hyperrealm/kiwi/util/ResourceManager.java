/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/

package com.hyperrealm.kiwi.util;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.hyperrealm.kiwi.ui.ColorTheme;
import com.hyperrealm.kiwi.ui.KiwiAudioClip;

import lombok.extern.java.Log;

/**
 * This class provides base functionality for a resource manager; it includes
 * support for the caching of images and sounds, and provides convenience
 * methods for retrieving other types of resources. All resources are
 * retrieved relative to an <i>anchor class</i>. The resource manager assumes
 * that images will be within an "images" directory, textures within a
 * "textures" directory, sounds within a "sounds" directory, URL-based
 * references within a "html" directory, properties within a "properties"
 * directory, resource bundles within a "locale" directory, and color theme
 * definitions within a "themes" directory. All of these paths, however, are
 * configurable.
 * <p>
 * The Kiwi library includes a resource library of its own; the resources
 * within the library are accessible through the internal
 * <code>ResourceManager</code>, a reference to which may be obtained via
 * a call to
 * <code>com.hyperrealm.kiwi.util.KiwiUtils.getResourceManager()</code>.
 * Links to index files of some of the resources are listed below:
 * <p><ul>
 * <li><a href="../../../../images_index.html">images</a>
 * <li><a href="../../../../textures_index.html">textures</a>
 * <li><a href="../../../../locale_index.html">resource bundles</a>
 * <li><a href="../../../../sounds_index.html">sounds</a>
 * </ul>
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.util.ResourceLoader
 */
@Log
@SuppressWarnings("unused")
public class ResourceManager {
    /**
     * The default base path for images.
     */
    private static final String IMAGE_PATH = "images";
    /**
     * The default base path for audio clips.
     */
    private static final String SOUND_PATH = "sounds";
    /**
     * The default base path for HTML documents.
     */
    private static final String HTML_PATH = "html";
    /**
     * The default base path for color themes.
     */
    private static final String THEME_PATH = "themes";
    /**
     * The default base path for textures.
     */
    private static final String TEXTURE_PATH = "textures";
    /**
     * The default base path for property files.
     */
    private static final String PROPERTY_PATH = "properties";
    /**
     * The default base path for message bundles.
     */
    private static final String BUNDLE_PATH = "locale";
    /**
     * The file extension for resource bundles.
     */
    private static final String BUNDLE_EXT = ".msg";
    /**
     * The file extension for color themes.
     */
    private static final String THEME_EXT = ".thm";

    private static final String RES_PATH_SEPARATOR = "/";

    private static final int INITIAL_CAPACITY = 5;

    private static final String NAME_DIVIDER = "_";

    private static ResourceManager kiwiResourceManager = null;
    /**
     * The base path for images.
     */
    protected String imagePath;
    /**
     * The base path for audio clips.
     */
    private String soundPath;
    /**
     * The base path for HTML documents.
     */
    private String htmlPath;
    /**
     * The base path for color themes.
     */
    private String themePath;
    /**
     * The base path for textures.
     */
    private String texturePath;
    /**
     * The base path for property files.
     */
    private String propertyPath;
    /**
     * The base path for message bundles.
     */
    private String bundlePath;

    private final HashMap<String, Image> images;

    private final HashMap<String, Icon> icons;

    private final HashMap<String, KiwiAudioClip> sounds;

    private final HashMap<String, Image> textures;

    private final HashMap<String, LocaleData> bundles;

    private final ResourceLoader loader;

    /**
     * Construct a new <code>ResourceManager</code>.
     *
     * @param clazz The resource anchor class.
     */

    public ResourceManager(Class<?> clazz) {

        loader = new ResourceLoader(clazz);
        images = new HashMap<>();
        icons = new HashMap<>();
        textures = new HashMap<>();
        sounds = new HashMap<>();
        bundles = new HashMap<>();

        setImagePath(IMAGE_PATH);
        setSoundPath(SOUND_PATH);
        setHTMLPath(HTML_PATH);
        setThemePath(THEME_PATH);
        setTexturePath(TEXTURE_PATH);
        setPropertyPath(PROPERTY_PATH);
        setResourceBundlePath(BUNDLE_PATH);
    }

    /**
     * Get a reference to the internal Kiwi resource manager.
     */
    public static synchronized ResourceManager getKiwiResourceManager() {
        if (kiwiResourceManager == null) {
            kiwiResourceManager = new ResourceManager(com.hyperrealm.kiwi.ResourceAnchor.class);
        }

        return (kiwiResourceManager);
    }

    private String checkPath(String path) {
        if (path == null) {
            return (RES_PATH_SEPARATOR);
        }

        return (path.endsWith(RES_PATH_SEPARATOR) ? path
            : (path + RES_PATH_SEPARATOR));
    }

    /**
     * Set an alternate path (relative to the anchor class) for image and ICON
     * resources.
     *
     * @param path The new resource path.
     * @since Kiwi 1.3.4
     */

    public void setImagePath(String path) {
        imagePath = checkPath(path);
    }

    /**
     * Set an alternate path (relative to the anchor class) for audio clip
     * resources.
     *
     * @param path The new resource path.
     * @since Kiwi 1.3.4
     */

    public void setSoundPath(String path) {
        soundPath = checkPath(path);
    }

    /**
     * Set an alternate path (relative to the anchor class) for HTML document
     * resources.
     *
     * @param path The new resource path.
     * @since Kiwi 1.3.4
     */

    public void setHTMLPath(String path) {
        htmlPath = checkPath(path);
    }

    /**
     * Set an alternate path (relative to the anchor class) for theme
     * resources.
     *
     * @param path The new resource path.
     * @since Kiwi 1.3.4
     */

    public void setThemePath(String path) {
        themePath = checkPath(path);
    }

    /**
     * Set an alternate path (relative to the anchor class) for texture
     * resources.
     *
     * @param path The new resource path.
     * @since Kiwi 1.3.4
     */

    public void setTexturePath(String path) {
        texturePath = checkPath(path);
    }

    /**
     * Set an alternate path (relative to the anchor class) for property
     * resources.
     *
     * @param path The new resource path.
     * @since Kiwi 1.3.4
     */

    public void setPropertyPath(String path) {
        propertyPath = checkPath(path);
    }

    /**
     * Set an alternate path (relative to the anchor class) for message bundle
     * resources.
     *
     * @param path The new resource path.
     * @since Kiwi 1.3.4
     */

    public void setResourceBundlePath(String path) {
        bundlePath = checkPath(path);
    }

    /**
     * Clear the image resource cache.
     */

    public void clearImageCache() {
        images.clear();
    }

    /**
     * Clear the ICON resource cache.
     */

    public void clearIconCache() {
        icons.clear();
    }

    /**
     * Clear the texture resource cache.
     */

    public void clearTextureCache() {
        textures.clear();
    }

    /**
     * Clear the audio clip resource cache.
     */

    public void clearAudioClipCache() {
        sounds.clear();
    }

    /**
     * Clear the resource bundle cache.
     */

    public void clearResourceBundleCache() {
        bundles.clear();
    }

    /**
     * Retrieve an internal <code>Icon</code> resource. This is a convenience
     * method that makes a call to <code>getImage()</code> and then wraps the
     * result in a Swing <code>ImageIcon</code> object.
     *
     * @param name The name of the resource.
     * @return An <code>Icon</code> for the specified image. If an ICON for this
     * image has previously been constructed, the cached copy is returned.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     * @see javax.swing.ImageIcon
     */

    public Icon getIcon(String name) {
        Icon icon = icons.get(name);
        if (icon != null) {
            return (icon);
        }

        Image image = getImage(name);
        if (image != null) {
            icon = new ImageIcon();
            icons.put(name, icon);
        }
        return (icon);
    }

    /**
     * Retrieve an internal <code>Image</code> resource. If the named image has
     * previously been loaded, a cached copy is returned.
     *
     * @param name The name of the resource.
     * @return The <code>Image</code> object representing the resource.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     */

    public Image getImage(String name) {
        return getImage(name, images, imagePath);
    }

    private Image getImage(String name, HashMap<String, Image> images, String imagePath) {
        checkResourceName(name);

        Image image = images.get(name);
        if (image != null) {
            return (image);
        }

        String path = imagePath + name;
        image = loader.getResourceAsImage(path);
        if (image != null) {
            images.put(name, image);
        }

        return (image);
    }

    /**
     * Retrieve an internal texture resource. If the named texture has
     * previously been loaded, a cached copy is returned.
     *
     * @param name The name of the resource.
     * @return The <code>Image</code> object representing the resource.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     */

    public Image getTexture(String name) {
        return getImage(name, textures, texturePath);
    }

    /**
     * Retrieve an internal <code>URL</code> resource.
     *
     * @param name The name of the resource.
     * @return A URL for the resource.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     */

    public URL getURL(String name) {
        checkResourceName(name);

        String path = htmlPath + name;
        URL url = loader.getResourceAsURL(path);
        if (url == null) {
            throw (new ResourceNotFoundException(path));
        }

        return (url);
    }

    /**
     * Retrieve an internal <code>AudioClip</code> resource. If the named sound
     * has previously been loaded, a cached copy is returned.
     *
     * @param name The name of the resource.
     * @return The <code>AudioClip</code> object representing the resource.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     */

    public KiwiAudioClip getSound(String name) {
        checkResourceName(name);

        KiwiAudioClip clip = sounds.get(name);
        if (clip != null) {
            return (clip);
        }

        String path = soundPath + name;
        clip = loader.getResourceAsAudioClip(soundPath + name);
        if (clip == null) {
            throw (new ResourceNotFoundException(path));
        }
        sounds.put(name, clip);

        return (clip);
    }

    /**
     * Get a reference to a <code>Properties</code> resource.
     *
     * @param name The name of the resource.
     * @return The <code>Properties</code> object representing the resource.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     */

    public Properties getProperties(String name) {
        checkResourceName(name);

        Properties props = null;
        String path = propertyPath + name;

        try {
            props = loader.getResourceAsProperties(path);
        } catch (IOException ignored) {
        }

        if (props == null) {
            throw (new ResourceNotFoundException(path));
        }

        return (props);
    }

    /**
     * Get a reference to a <code>LocaleData</code> object for the default
     * locale. The locale naming convention
     * <i>basename_language_country_variant</i> is
     * supported; a search is performed starting with the most specific name
     * and ending with the most generic.
     *
     * @param name The name of the resource; this should be the base name of
     *             the resource bundle; the appropriate locale country, language, and
     *             variant codes and the ".msg" extension will be automatically
     *             appended to the name.
     * @return The <code>LocaleData</code> object representing the resource.
     * If the resource bundle has been previously loaded, a cached copy is
     * returned.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     */

    public LocaleData getResourceBundle(String name)
        throws ResourceNotFoundException {
        return (getResourceBundle(name, Locale.getDefault()));
    }

    /**
     * Get a reference to a <code>LocaleData</code> object for a specified
     * locale. The locale naming convention
     * <i>basename_language_country_variant</i> is
     * supported; a search is performed starting with the most specific name
     * and ending with the most generic. If the resource bundle has been
     * previously loaded, a cached copy is returned.
     *
     * @param name   The name of the resource; this should be the base name of
     *               the resource bundle; the appropriate locale contry, language, and
     *               variant codes and the ".msg" extension will be automatically
     *               appended to the name.
     * @param locale The locale for the resource.
     * @return The <code>LocaleData</code> object representing the resource.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     */

    public LocaleData getResourceBundle(String name, Locale locale)
        throws ResourceNotFoundException {
        checkResourceName(name);

        String path = bundlePath + name;
        String cpath = null;

        LocaleData bundle;
        LocaleData baseBundle = null;
        LocaleData prevBundle = null;

        com.hyperrealm.kiwi.util.Stack<String> paths
            = new com.hyperrealm.kiwi.util.Stack<>(INITIAL_CAPACITY);

        paths.push(path + BUNDLE_EXT);
        path += NAME_DIVIDER + locale.getLanguage();
        paths.push(path + BUNDLE_EXT);

        String country = locale.getCountry();
        if ((country != null) && (!country.isEmpty())) {
            path += NAME_DIVIDER + country;
            paths.push(path + BUNDLE_EXT);

            String variant = locale.getVariant();
            if ((variant != null) && (!variant.isEmpty())) {
                path += NAME_DIVIDER + variant;
                paths.push(path + BUNDLE_EXT);
            }
        }

        while (!paths.empty()) {
            cpath = paths.pop();

            bundle = bundles.get(cpath);
            if (bundle == null) {
                try {
                    InputStream is = loader.getResourceAsStream(cpath);
                    bundle = new LocaleData(is);
                    bundles.put(cpath, bundle);
                } catch (IOException ex) {
                    log.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

            if (bundle != null) {
                if (baseBundle == null) {
                    baseBundle = bundle;
                }
                if (prevBundle != null) {
                    prevBundle.setParent(bundle);
                }
                prevBundle = bundle;
            }
        }
        if (baseBundle == null) {
            throw (new ResourceNotFoundException(cpath));
        }

        return (baseBundle);
    }

    /**
     * Get a reference to a <code>ColorTheme</code> resource.
     *
     * @param name The name of the resource; this should be the base name of
     *             the color theme; the ".thm" extension will be automatically
     *             appended to the name.
     * @return The <code>ColorTheme</code> object representing the resource.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If the
     *                                                            resource was not found.
     */

    public ColorTheme getTheme(String name) {
        checkResourceName(name);

        String path = themePath + name + THEME_EXT;
        ColorTheme theme;

        try {
            InputStream is = loader.getResourceAsStream(path);
            Config cfg = new Config();
            cfg.load(is);
            theme = new ColorTheme(cfg);
        } catch (IOException ex) {
            throw (new ResourceNotFoundException(path));
        }

        return (theme);
    }

    /**
     * Get a stream to a resource.
     *
     * @param name The name of the resource.
     * @return An <code>InputStream</code> from which the resource can be read.
     * @throws com.hyperrealm.kiwi.util.ResourceNotFoundException If
     *                                                            the resource was not found.
     * @since Kiwi 1.3
     */

    public InputStream getStream(String name) throws ResourceNotFoundException {
        checkResourceName(name);

        InputStream is;

        try {
            is = loader.getResourceAsStream(name);
        } catch (IOException ex) {
            throw (new ResourceNotFoundException(name));
        }

        return (is);
    }

    /*
     */

    private void checkResourceName(String name) throws IllegalArgumentException {
        if ((name == null) || name.isEmpty()) {
            throw (new IllegalArgumentException("Null or empty resource name"));
        }
    }

}
