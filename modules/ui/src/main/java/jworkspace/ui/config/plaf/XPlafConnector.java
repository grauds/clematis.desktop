package jworkspace.ui.config.plaf;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTheme;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class configures extra information about each plaf in xml,
 * including themes, methods for setting such themes and etc.
 */
class XPlafConnector {
    /**
     * Default logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(XPlafConnector.class);

    private static final String CURRENT = "current";

    private static final String CLASS_NODE = "class";

    private static final String THEME_NODE = "theme";

    private static final String NAME_ATTRIBUTE = "name";
    /**
     * Look and feel instance
     */
    private UIManager.LookAndFeelInfo info = null;
    /**
     * Array of assosiated themes with this laf
     * All themes are from metal plaf.
     */
    private MetalTheme[] themes = null;
    /**
     * Current theme
     */
    private MetalTheme currentTheme = null;
    /**
     * A JDOM element that is a XML representation of method
     * to be invoked to set theme on this plaf.
     */
    private Element method = null;

    /**
     * Create connector from binary jdom branch
     * <p>
     * Example element:
     * <plaf>
     * <class>com.incors.plaf.kunststoff.KunststoffLookAndFeel</class>
     * <theme>com.incors.plaf.kunststoff.themes.KunststoffDesktopTheme</theme>
     * <theme>com.incors.plaf.kunststoff.themes.KunststoffNotebookTheme</theme>
     * <theme>com.incors.plaf.kunststoff.themes.KunststoffPresentationTheme</theme>
     * <method access="public" static="true" type="void" name="setCurrentTheme"/>
     * </plaf>
     *
     * @param element binary jdom branch
     * @return new connector or null
     */
    public static XPlafConnector create(Element element) {
        XPlafConnector connector = new XPlafConnector();
        //** create and install laf, if it is not installed on creation
        Element clazzNameEl = element.getChild(CLASS_NODE);
        String lafClazzName = clazzNameEl.getText();
        try {
            Class clazz = Class.forName(lafClazzName);
            LookAndFeel laf = (LookAndFeel) clazz.newInstance();
            connector.info = new UIManager.LookAndFeelInfo(laf.getName(), lafClazzName);
            //** check if this laf is installed in system
            if (PlafFactory.getInstance().getLookAndFeel(lafClazzName) == null) {
                UIManager.installLookAndFeel(connector.info);
            }
        } catch (Exception | Error e) {
            LOG.error("Cannot create laf: ", e);
            return null;
        }
        //** look for themes
        List<Element> themes = element.getChildren(THEME_NODE);
        List<MetalTheme> ths = new ArrayList<>();
        /*
         * Set current theme, do not install it in look and feel
         */
        for (Element theme : themes) {
            String themeName = theme.getText();
            if (themeName != null && !themeName.trim().equals("")) {
                try {
                    Class clazz = Class.forName(themeName);
                    MetalTheme th = (MetalTheme) clazz.newInstance();
                    ths.add(th);
                    /*
                     * Set current theme, do not install it in look and feel
                     */
                    if (theme.getAttribute(CURRENT) != null
                        && theme.getAttributeValue(CURRENT).equalsIgnoreCase(Boolean.TRUE.toString())) {
                        connector.currentTheme = th;
                    }
                } catch (Exception e) {
                    LOG.error("Cannot load laf: ", e);
                }
            }
        }
        //** copy themes to array
        connector.themes = ths.toArray(new MetalTheme[0]);
        //** set method
        connector.method = element.getChild("method");
        //** finally return connector
        return connector;
    }

    /**
     * Writes the current state of connector to jdom element
     * <p>
     * Example element:
     * <plaf name="Kinststoff">
     * <class>com.incors.plaf.kunststoff.KunststoffLookAndFeel</class>
     * <theme current="false">com.incors.plaf.kunststoff.themes.KunststoffDesktopTheme</theme>
     * <theme current="true">com.incors.plaf.kunststoff.themes.KunststoffNotebookTheme</theme>
     * <theme current="false">com.incors.plaf.kunststoff.themes.KunststoffPresentationTheme</theme>
     * <method access="public" static="true" type="void" name="setCurrentTheme"/>
     * </plaf>
     *
     * @return jdom element
     */
    Element serialize() {
        /*
         * Root
         */
        Element plaf = new Element("plaf");
        plaf.setAttribute(NAME_ATTRIBUTE, info.getName());
        /*
         * Class
         */
        Element clazz = new Element(CLASS_NODE);
        clazz.setText(info.getClassName());
        plaf.addContent(clazz);

        if (themes != null) {
            for (MetalTheme metalTheme : themes) {
                Element theme = new Element(THEME_NODE);
                theme.setText(metalTheme.getClass().getName());
                plaf.addContent(theme);
            }
        }

        if (method != null) {
            plaf.addContent(method.detach());
        }
        return plaf;
    }

    /**
     * Returns currently set theme on this look and feel
     *
     * @return currently set theme on this look and feel
     */
    MetalTheme getCurrentTheme() {
        return currentTheme;
    }

    UIManager.LookAndFeelInfo getInfo() {
        return info;
    }

    public void setInfo(UIManager.LookAndFeelInfo info) {
        this.info = info;
    }

    public MetalTheme[] getThemes() {
        return themes;
    }

    public void setThemes(MetalTheme[] themes) {
        this.themes = themes;
    }

    /**
     * Sets current theme for this look and feel.
     *
     * @param clazz of theme
     */
    void setCurrentTheme(String clazz) {
        currentTheme = lookUpTheme(clazz);
    }

    /**
     * Sets current theme for this look and feel.
     *
     * @param theme
     */
    void setCurrentTheme(MetalTheme theme) {
        currentTheme = theme;
    }

    /**
     * Look up theme by its class name
     *
     * @param clazz name of the theme
     * @return theme or null, if no theme is found
     */
    private MetalTheme lookUpTheme(String clazz) {
        for (MetalTheme theme : themes) {
            if (theme.getClass().getName().equals(clazz)) {
                return theme;
            }
        }
        return null;
    }

    /**
     * Sets look and feel, this class is connected to, along with
     * currently selected theme.
     */
    void setLookAndFeel() {
        /*
         * We use only metal themes by now
         */
        Class[] argsTypes = new Class[]{MetalTheme.class};
        /*
         * Instantiate method
         */
        try {
            Class lafClazz = Class.forName(info.getClassName());
            /*
             * Invoke method
             */
            if (method != null) {
                Attribute nameAttr = method.getAttribute(NAME_ATTRIBUTE);
                Attribute staticAttr = method.getAttribute("static");
                Method setTheme = lafClazz.getMethod(nameAttr.getValue(), argsTypes);
                /*
                 * Only for static invocation by now
                 */
                if (currentTheme != null && staticAttr.getValue().equals("true")) {
                    setTheme.invoke(null, currentTheme);
                }
            }
            UIManager.setLookAndFeel(info.getClassName());
        } catch (Exception e) {
            LOG.error("Can't set look and feel", e);
        }
    }

}