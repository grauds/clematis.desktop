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

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTheme;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jworkspace.ui.WorkspaceGUI;

/**
 * Pluggable look and feel factory
 * @author Anton Troshin
 */
public class PlafFactory {
    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(PlafFactory.class);
    /**
     * The directory of config
     */
    private static final String CONFIG_FILE_PATH = "config";
    /**
     * Single instance
     */
    private static PlafFactory instance;
    /**
     * The name of configuration file
     */
    private static String sysConfig = "lafs.xml";
    /**
     * Array of plaf connectors
     */
    private List<XPlafConnector> connectors = new ArrayList<>();
    /**
     * Private constructor
     */
    private PlafFactory() {
    }

    /**
     * Get a single instance of this factory
     *
     * @return single instance
     */
    public static synchronized PlafFactory getInstance() {
        if (instance == null) {
            instance = new PlafFactory();
            instance.load();
        }
        return instance;
    }

    /**
     * Load and install look and feels from user configuration file
     */
    private void load() {
        String fileName = CONFIG_FILE_PATH + File.separator + sysConfig;
        LOG.info(WorkspaceGUI.PROMPT + "Reading file" + WorkspaceGUI.LOG_SPACE + fileName + WorkspaceGUI.LOG_FINISH);
        try {
            File file = new File(fileName);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(file);
            Element root = doc.getRootElement();
            /*
             * Here we should find all plaf elements, described
             * in xml file under <plaf> tags
             */
            List<Element> plafs = root.getChildren("plaf");
            for (Element plaf : plafs) {
                XPlafConnector connector = XPlafConnector.create(plaf);
                if (connector != null) {
                    connectors.add(connector);
                }
            }
        } catch (JDOMException | IOException e) {
            LOG.error("Cannot load plaf factory", e);
        }
    }

    /**
     * Save all look and feel user config
     */
    public void save() {
        String fileName = CONFIG_FILE_PATH + File.separator + sysConfig;
        LOG.info(WorkspaceGUI.PROMPT + "Writing file" + WorkspaceGUI.LOG_SPACE + fileName + WorkspaceGUI.LOG_FINISH);
        File file = new File(fileName);

        try (StringWriter sw = new StringWriter();
             FileOutputStream os = new FileOutputStream(file)) {

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
            LOG.warn("Cannot save plaf factory", e);
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
                Class clazz = Class.forName(selectedLaf, true, Thread.currentThread().
                    getContextClassLoader());
                try {
                    return (LookAndFeel) clazz.newInstance();
                } catch (Exception e) {
                    LOG.error("Cannot instantiate the plaf discovered", e);
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
        for (Object o : connectors) {
            XPlafConnector connector = (XPlafConnector) o;

            if (connector.getInfo().getClassName().equals(className)) {
                return connector;
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
            LOG.error("Cannot set look and feel", ex);
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
            connector.setCurrentTheme(theme);
        }
    }
}

