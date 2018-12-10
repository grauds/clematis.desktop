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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalTheme;

import org.jdom.Attribute;
import org.jdom.Element;

/**
 * This class configures extra information about each plaf in xml,
 * including themes, methods for setting such themes and etc.
 */
class XPlafConnector {
    /**
     * Look and feel instance
     */
    UIManager.LookAndFeelInfo info = null;
    /**
     * Array of assosiated themes with this laf
     * All themes are from metal plaf.
     */
    MetalTheme[] themes = null;
    /**
     * Current theme
     */
    MetalTheme currentTheme = null;
    /**
     * A JDOM element that is a XML representation of method
     * to be invoked to set theme on this plaf.
     */
    Element method = null;

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
        Element clazz_name_el = element.getChild("class");
        String laf_clazz_name = clazz_name_el.getText();
        try {
            Class clazz = Class.forName(laf_clazz_name);
            LookAndFeel laf = (LookAndFeel) clazz.newInstance();
            connector.info = new UIManager.LookAndFeelInfo(laf.getName(),
                laf_clazz_name);
            //** check if this laf is installed in system
            if (PlafFactory.getInstance().getLookAndFeel(laf_clazz_name) == null) {
                UIManager.installLookAndFeel(connector.info);
            }
        } catch (Exception e) {
            return null;
        } catch (Error err) {
            return null;
        }
        //** look for themes
        List themes = element.getChildren("theme");
        ArrayList ths = new ArrayList();
        for (int i = 0; i < themes.size(); i++) {
            Element theme = (Element) themes.get(i);
            String theme_name = theme.getText();
            if (theme_name != null && !theme_name.trim().equals("")) {
                try {
                    Class clazz = Class.forName(theme_name);
                    MetalTheme th = (MetalTheme) clazz.newInstance();
                    ths.add(th);
                    /**
                     * Set current theme, do not install it in look and feel
                     */
                    if (theme.getAttribute("current") != null &&
                        theme.getAttributeValue("current").equals("true")) {
                        connector.currentTheme = th;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        //** copy themes to array
        connector.themes = new MetalTheme[ths.size()];
        System.arraycopy(ths.toArray(), 0, connector.themes, 0, ths.size());
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
    public Element serialize() {
        /**
         * Root
         */
        Element plaf = new Element("plaf");
        plaf.setAttribute("name", info.getName());
        /**
         * Class
         */
        Element clazz = new Element("class");
        clazz.setText(info.getClassName());
        plaf.addContent(clazz);

        if (themes != null) {
            for (int i = 0; i < themes.length; i++) {
                Element theme = new Element("theme");
                theme.setText(themes[i].getClass().getName());
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
    public MetalTheme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Sets current theme for this look and feel.
     *
     * @param clazz of theme
     */
    public void setCurrentTheme(String clazz) {
        currentTheme = lookUpTheme(clazz);
    }

    /**
     * Look up theme by its class name
     *
     * @param clazz name of the theme
     * @return theme or null, if no theme is found
     */
    public MetalTheme lookUpTheme(String clazz) {
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].getClass().getName().equals(clazz)) {
                return themes[i];
            }
        }
        return null;
    }

    /**
     * Sets look and feel, this class is connected to, along with
     * currently selected theme.
     */
    public void setLookAndFeel() {
        /**
         * We use only metal themes by now
         */
        Class[] args_types = new Class[]{MetalTheme.class};
        /**
         * Instantiate method
         */
        try {
            Class laf_clazz = Class.forName(info.getClassName());
            /**
             * Invoke method
             */
            if (method != null) {
                Attribute name_attr = method.getAttribute("name");
                Attribute static_attr = method.getAttribute("static");
                Method setTheme = laf_clazz.getMethod(name_attr.getValue(), args_types);
                /**
                 * Only for static invokation by now
                 */
                if (currentTheme != null && static_attr.getValue().equals("true")) {
                    setTheme.invoke(null, currentTheme);
                }
            }
            UIManager.setLookAndFeel(info.getClassName());
        } catch (Exception e) {
        }
    }
}