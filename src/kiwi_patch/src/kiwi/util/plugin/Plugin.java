/* ----------------------------------------------------------------------------
   The Kiwi Toolkit
   Copyright (C) 1998-2001 Mark A. Lindner

   This file is part of Kiwi.

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   mark_a_lindner@yahoo.com
   ----------------------------------------------------------------------------
   $Log: Plugin.java,v $
   Revision 1.4  2001/06/26 06:15:58  markl
   Added setConfig() method.

   Revision 1.3  2001/03/20 00:54:56  markl
   Fixed deprecated calls.

   Revision 1.2  2001/03/18 07:58:50  markl
   Added javadoc.

   Revision 1.1  2001/03/12 10:20:59  markl
   New classes.
   ----------------------------------------------------------------------------
*/

package kiwi.util.plugin;

import java.lang.reflect.*;
import java.util.*;
import java.util.jar.*;
import javax.swing.Icon;

import kiwi.io.*;
import kiwi.util.*;

/** A class that represents a plugin. A <code>Plugin</code> object encapsulates
 * all of the data that makes up a plugin, as specified by the plugin's
 * entry in a JAR Manifest.
 *
 * @since Kiwi 1.3
 *
 * @author Mark Lindner
 * @author PING Software Group
 */

public final class Plugin
  {
  private String className;
  private String name;
  private String type;
  private String desc;
  private String configFile;
  private Properties props;
  private String version;
  /* stuff that must be loaded */
  private Object plugin = null;
  private Config config;
  private Icon icon;
  private Icon big_icon;
  /* support objects */
  private PluginClassLoader loader;
  private PluginContext context;
  private static final Class args[] = new Class[] { Plugin.class };
  private JarFile jar;
  private PluginCustomizer customizer;
  private static final Class disposeTypes[] = new Class[] { };
  private static final Object disposeArgs[] = new Object[] { };

  /* Construct a new plugin.
   */

  Plugin(PluginClassLoader loader, PluginContext context, JarFile jar,
         String className, String name, String type, String desc,
         String version, Properties props, Config config, Icon icon,
         Icon big_icon, PluginCustomizer customizer)
    {
    this.loader = loader;
    this.context = context;
    this.className = className;
    this.name = name;
    this.type = type;
    this.desc = desc;
    this.version = version;
    this.props = props;
    this.config = config;
    this.icon = icon;
    this.big_icon = big_icon;
    this.customizer = customizer;
    this.jar = jar;

    if(customizer != null)
      customizer.setPlugin(this);
    }

  /** Get the version number for this plugin.
   *
   * @return The version number.
   */

  public String getVersion()
    {
    return(version);
    }

  /** Get the class name for this plugin.
   *
   * @return The class name.
   */

  public String getClassName()
    {
    return(className);
    }

  /** Get the <code>PluginContext</code> for this plugin.
   *
   * @return The context.
   */

  public PluginContext getContext()
    {
    return(context);
    }

  /** Get the name of this plugin.
   *
   * @return The name.
   */

  public String getName()
    {
    return(name);
    }

  /** Get the type of this plugin.
   *
   * @return The type.
   */

  public String getType()
    {
    return(type);
    }

  /** Get the user-defined properties for the plugin. These correspond to the
   * user-defined key/value pairs in the Manifest entry for this plugin.
   *
   * @return A <code>Properties</code> object containing the user-defined
   * properties.
   */

  public Properties getProperties()
    {
    return(props);
    }

  /** Get a specific user-defined property for the plugin. User-defined
   * properties are those fields in the Manifest entry for the plugin that are
   * not defined and recognized by the plugin framework itself.
   *
   * @param name The property name (key).
   * @return The value of the property, or <code>null</code> if there is no
   * property with the given name.
   */

  public String getProperty(String name)
    {
    return(props.getProperty(name));
    }

  /** Get a specific user-defined property for the plugin.
   *
   * @param name The property name (key).
   * @param defaultValue The default value for this property.
   * @return The value of the property, or <code>defaultValue</code> if there
   * is no property with the given name.
   */

  public String getProperty(String name, String defaultValue)
    {
    return(props.getProperty(name, defaultValue));
    }

  /** Get the description of this plugin.
   *
   * @return The description, or <b>null</b> if no description is available.
   */

  public String getDescription()
    {
    return(desc);
    }

  /** Get the icon for this plugin.
   *
   * @return The icon, or <b>null</b> if no icon is available.
   */

  public Icon getIcon()
    {
    return(icon);
    }

  /** Get the big icon for this plugin.
   *
   * @return The icon, or <b>null</b> if no icon is available.
   */

  public Icon getBigIcon()
    {
    return(big_icon);
    }

  /** Get the configuration settings for this plugin.
   *
   * @return The configuration, or <b>null</b> if none is available.
   */

  public Config getConfig()
    {
    return(config);
    }

  /** Replace the configuration settings for this plugin.
   *
   * @param config A new set of configuration settings.
   */

  public void setConfig(Config config)
    {
    this.config = config;
    }

  /** Determine if the plugin is loaded.
   *
   * @return <b>true</b> if the plugin is loaded and <b>false</b> otherwise.
   */

  public boolean isLoaded()
    {
    return(plugin != null);
    }

  /** Load the plugin. This method attempts to load the plugin entry-point
   * class and create an instance of it. The entry-point class must have a
   * public constructor that accepts a <code>Plugin</code> object as an
   * argument; it will be invoked with a reference to this plugin.
   *
   * @throws kiwi.util.plugin.PluginException If the plugin could not be
   * loaded.
   */

  public void load() throws PluginException
    {
    Class clazz;

    if(plugin != null)
      return;

    try
      {
      clazz = loader.loadClass(className);
      Constructor c = clazz.getConstructor(args);
      plugin = c.newInstance(new Object[] { this });
      }
    catch(Exception ex)
      {
      ex.printStackTrace();

      throw(new PluginException(ex.toString()));
      }
    }

  /** Dispose of the plugin. This method calls the <code>dispose()</code>
   * method on the plugin entry-point class, if there is one, and then
   * destroys its reference to the class. The class can be re-loaded via
   * another call to <code>load()</code>.
   */

  public void dispose() throws PluginException
    {
    if(plugin != null)
      {
      try
        {
        Method m = plugin.getClass().getMethod("dispose", disposeTypes);
        m.invoke(plugin, disposeArgs);
        }
      catch(Exception ex) { /* ignore */ }

      plugin = null;
      }
    }

  /** Get a reference to the instance of the plugin entry-point class.
   *
   * @return The instance, or <code>null</code> if the class has not yet been
   * loaded.
   */

  public Object getPluginObject()
    {
    return(plugin);
    }

  /** Get a reference to the instance of the plugin customizer class.
   *
   * @return The instance, or <code>null</code> if the plugin has not yet been
   * loaded or if no customizer is available for this plugin.
   */

  public PluginCustomizer getCustomizer()
    {
    return(customizer);
    }

  public String toString()
    {
    String n = getName();
    String v = getVersion();

    if(v != null)
      n += " " + v;

    return(n);
    }
  }

/* end of source file */
