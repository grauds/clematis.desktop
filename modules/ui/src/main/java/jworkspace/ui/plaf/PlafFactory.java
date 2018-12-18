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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTheme;

import jworkspace.kernel.Workspace;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Pluggable look and feel factory
 */
public class PlafFactory {
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
    private ArrayList connectors = new ArrayList();

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
    public synchronized static PlafFactory getInstance() {
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
        String fileName = "config" + File.separator + sysConfig;
        Workspace.getLogger().info(">" + "Reading file" + " " + fileName + "...");
        try {
            File file = new File(fileName);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(file);
            Element root = doc.getRootElement();
            /**
             * Here we should find all plaf elements, described
             * in xml file under <plaf> tags
             */
            List plafs = root.getChildren("plaf");
            for (int i = 0; i < plafs.size(); i++) {
                /**
                 * Parse each plaf individually
                 */
                XPlafConnector connector =
                    XPlafConnector.create((Element) plafs.get(i));
                if (connector != null) {
                    connectors.add(connector);
                }
            }
        } catch (JDOMException e) {
            Workspace.getLogger().log(Level.WARNING, "Cannot load plaf factory", e);
        } catch (IOException e) {
            Workspace.getLogger().log(Level.INFO, "Cannot load plaf factory due to IO exception " + e.getMessage());
        }
    }

    /**
     * Save all look and feel user config
     */
    public void save() {
        String fileName = "config" + File.separator + sysConfig;
        Workspace.getLogger().info(">" + "Writing file" + " " + fileName + "...");
        try {
            Element plafs = new Element("plafs");
            for (int i = 0; i < connectors.size(); i++) {
                plafs.addContent(((XPlafConnector) connectors.get(i)).serialize());
            }

            XMLOutputter serializer = new XMLOutputter();
            StringWriter sw = new StringWriter();
            serializer.setFormat(Format.getPrettyFormat());
            serializer.output(plafs, sw);

            File file = new File(fileName);
            FileOutputStream os = new FileOutputStream(file);
            os.write(sw.toString().getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            Workspace.getLogger().warning("Cannot save plaf factory");
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
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].getName().equals(selectedLaf)
                || infos[i].getClassName().equals(selectedLaf)) {
                Class clazz = Class.forName(selectedLaf, true, Thread.currentThread().
                    getContextClassLoader());
                try {
                    return (LookAndFeel) clazz.newInstance();
                } catch (Exception e) {
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
        for (int i = 0; i < connectors.size(); i++) {
            XPlafConnector connector = (XPlafConnector) connectors.get(i);

            if (connector.info.getName().equals(selectedLaf.getName())) {
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
        for (int i = 0; i < connectors.size(); i++) {
            XPlafConnector connector = (XPlafConnector) connectors.get(i);

            if (connector.info.getClassName().equals(className)) {
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
            return connector.themes;
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
            Workspace.getLogger().log(Level.WARNING, "Cannot set look and feel", ex);
        }
        return false;
    }

    /**
     * Returns currently selected theme for a given laf
     *
     * @param selectedLaf
     * @return currently selected theme
     */
    public MetalTheme getCurrentTheme(LookAndFeel selectedLaf) {
        /**
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
        /**
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
        /**
         * Look up connector for current look and feel
         */
        XPlafConnector connector = lookUpConnector(laf);
        if (connector != null) {
            connector.setCurrentTheme(theme);
        }
    }
}

