package jworkspace.ui.plaf;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import jworkspace.runtime.WorkspacePluginContext;
import jworkspace.ui.api.Constants;
import jworkspace.ui.config.DesktopServiceLocator;
import lombok.Getter;
import lombok.extern.java.Log;

/**
 * Pluggable look and feel factory
 * @author Anton Troshin
 */
@Log
@Getter
public class PlafFactory {
    /**
     * Key for UI properties
     */
    public static final String CK_THEME = "gui.theme";
    /**
     * The name of configuration file
     */
    private static final String LAFS_XML = "lafs.xml";
    /**
     * Support for i18n strings
     */
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("i18n/strings");
    /**
     * Workspace shared context for plugins
     */
    private final WorkspacePluginContext pluginContext;
    /**
     * Root folder for the data
     */
    private File configFile;
    /**
     * Array of plaf connectors
     */
    private final List<XPlafConnector> connectors = new ArrayList<>();

    public PlafFactory(WorkspacePluginContext pluginContext) {
        this.pluginContext = pluginContext;
    }

    /**
     * Load and install look and feels from user configuration file
     */
    public void load() {

        this.configFile = this.pluginContext.getUserDir().resolve(LAFS_XML).toFile();
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(configFile);
            Element element = doc.getRootElement();
            /*
             * Here we should find all plaf elements, described
             * in xml file under <plaf> tags
             */
            List<Element> plafs = element.getChildren("plaf");
            for (Element plaf : plafs) {
                XPlafConnector connector = XPlafConnector.create(plaf, this);
                if (connector != null) {
                    connectors.add(connector);
                }
            }

            /*
             * Set LAF and styles
             */
            try {
                if (DesktopServiceLocator.getInstance().getUiConfig().getLaf() != null
                    && !DesktopServiceLocator.getInstance().getUiConfig().getLaf().isEmpty()) {

                    if (DesktopServiceLocator.getInstance().getUiConfig().getLaf().equals(Constants.DEFAULT_LAF)
                        || !setLookAndFeel(
                                DesktopServiceLocator.getInstance().getUiConfig().getLaf()
                            )
                    ) {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } else {
                        setCurrentTheme(
                            DesktopServiceLocator.getInstance().getUiConfig().getLaf(),
                            DesktopServiceLocator.getInstance().getUiConfig().getString(CK_THEME)
                        );
                    }
                }
            } catch (Exception ex) {
                log.warning(ex.getMessage());
            }

        } catch (JDOMException | IOException e) {
            log.warning("Cannot load the factory, applying the defaults. Caused by: " + e.getMessage());

            UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
            MetalTheme currentTheme = MetalLookAndFeel.getCurrentTheme();

            for (UIManager.LookAndFeelInfo info : installed) {
                XPlafConnector connector = new XPlafConnector(this);
                connector.setInfo(info);
                connector.setThemes(new MetalTheme[0]);
                if (info.getClassName().equals(MetalLookAndFeel.class.getCanonicalName())) {
                    connector.setCurrentTheme(currentTheme);
                }
                connectors.add(connector);
            }
        }
    }

    /**
     * Save all look and feel user config
     */
    public void save() {

        this.configFile = this.pluginContext.getUserDir().resolve(LAFS_XML).toFile();

        try (StringWriter sw = new StringWriter();
             FileOutputStream os = new FileOutputStream(configFile)) {

            Element plafs = new Element("plafs");
            for (XPlafConnector connector : connectors) {
                plafs.addContent(connector.serialize());
            }

            XMLOutputter serializer = new XMLOutputter();
            serializer.setFormat(Format.getPrettyFormat());
            serializer.output(plafs, sw);

            os.write(sw.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();

        } catch (IOException e) {
            log.warning("Cannot save the factory: " + e.getMessage());
        }
    }

    /**
     * Returns an instance of look and feel object without installing
     * it or switching gui. This can be used if any info about laf is needed.
     *
     * @param selectedLaf a string name of look and feel
     * @return look and feel object
     */
    public LookAndFeel getLookAndFeel(String selectedLaf) {
        try {
            return lookUp(selectedLaf);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Lookup installed look and feel
     *
     * @param selectedLaf a string name of look and feel
     * @return look and feel object
     */
    private LookAndFeel lookUp(String selectedLaf) throws ClassNotFoundException {
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo info : infos) {
            if (info.getName().equals(selectedLaf)
                || info.getClassName().equals(selectedLaf)) {
                Class<?> clazz = Class.forName(selectedLaf, true, Thread.currentThread().
                    getContextClassLoader());
                try {
                    return (LookAndFeel) clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    log.severe("Cannot instantiate the plaf discovered: " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Look up a proper connector for look and feel to get
     * themes list and other extra info
     *
     * @param selectedLaf the laf, which connector is needed
     * @return plaf connector for selected laf
     */
    private XPlafConnector lookUpConnector(LookAndFeel selectedLaf) {
        for (XPlafConnector connector : connectors) {
            if (connector.getInfo().getName().equals(selectedLaf.getName())) {
                return connector;
            }
        }
        return null;
    }

    /**
     * Look up a proper connector for look and feel to get
     * themes list and other extra info
     *
     * @param className the class name of laf, which connector is needed
     * @return plaf connector for selected laf
     */
    private XPlafConnector lookUpConnector(String className) {
        for (XPlafConnector o : connectors) {
            if (o.getInfo().getClassName().equals(className)) {
                return o;
            }
        }
        return null;
    }

    /**
     * List themes for a given look and feel. All themes are stored
     * in internal laf database and should be in class path.
     *
     * @param selectedLaf the laf, which themes are to be inspected
     * @return an array of selected lafs
     */
    public MetalTheme[] listThemes(LookAndFeel selectedLaf) {
        XPlafConnector connector = lookUpConnector(selectedLaf);
        if (connector != null) {
            return connector.getThemes();
        } else {
            return new MetalTheme[0];
        }
    }

    /**
     * Set look and feel as UIManager does, except for applying selected theme
     * in look and feel connector
     */
    public boolean setLookAndFeel(String laf) {
        try {
            XPlafConnector connector = lookUpConnector(laf);
            if (connector != null) {
                connector.setLookAndFeel();
            } else {
                UIManager.setLookAndFeel(laf);
            }
            return true;
        } catch (Exception ex) {
            log.severe("Cannot set look and feel: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Returns currently selected theme for a given laf
     *
     * @param selectedLaf by user
     * @return currently selected theme
     */
    public MetalTheme getCurrentTheme(LookAndFeel selectedLaf) {
        /*
         * Look up connector for current look and feel
         */
        XPlafConnector connector = lookUpConnector(selectedLaf);
        if (connector != null) {
            return connector.getCurrentTheme();
        }
        return null;
    }

    /**
     * Returns currently selected theme for a current laf
     *
     * @return currently selected theme
     */
    public MetalTheme getCurrentTheme() {
        /*
         * Look up connector for current look and feel
         */
        XPlafConnector connector = lookUpConnector(UIManager.getLookAndFeel());
        if (connector != null) {
            return connector.getCurrentTheme();
        }
        return null;
    }

    /**
     * Sets current theme on current plaf
     *
     * @param laf   class of laf to be set with new theme
     * @param theme class of theme to be set
     */
    public void setCurrentTheme(String laf, String theme) {
        /*
         * Look up connector for current look and feel
         */
        XPlafConnector connector = lookUpConnector(laf);
        if (connector != null) {
            connector.setTheme(theme);
        }
    }

    public String getTheme() {
        return DesktopServiceLocator.getInstance().getUiConfig().getString(CK_THEME, "");
    }

    public void saveTheme() {
        MetalTheme theme = getCurrentTheme();
        if (theme != null) {
            DesktopServiceLocator.getInstance().getUiConfig().putString(CK_THEME, theme.getClass().getName());
        } else {
            DesktopServiceLocator.getInstance().getUiConfig().remove(CK_THEME);
        }
    }

    public static String getString(String key) {
        return PlafFactory.STRINGS.getString(key);
    }
}

