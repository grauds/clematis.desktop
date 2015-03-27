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
   $Log: PluginLocator.java,v $
   Revision 1.5  2002/01/04 22:18:25  markl
   Throw exception rather than returning null if manifest entry not found

   Revision 1.4  2001/08/28 21:38:31  markl
   More fixes to defer GUI-stuff when display not available.

   Revision 1.3  2001/08/28 20:28:21  markl
   Fixes to defer access to Toolkit for situations where no X Display is
   available.

   Revision 1.2  2001/03/18 07:58:50  markl
   Added javadoc.

   Revision 1.1  2001/03/12 10:21:00  markl
   New classes.
   ----------------------------------------------------------------------------
*/

package kiwi.util.plugin;

import java.awt.Image;
import java.io.*;
import java.util.*;
import java.util.jar.*;
import javax.swing.ImageIcon;

import kiwi.util.*;

/** A utility class for locating plugins in JAR files. This class is the
 * heart of the Kiwi plugin API.
 * <p>
 * A plugin consists of one or more classes that implement the plugin itself
 * (with one of those classes being the main or entry-point class). These
 * classes are stored in a JAR file. The JAR file Manifest must contain an
 * entry for each plugin in the JAR. This entry identifies the plugin
 * entry-point class and other information about the plugin. Here is an
 * example entry:
 * <p>
 * <pre>
 * Name: com/foo/imaging/BlurFilter.class
 * PluginName: Blur Filter
 * Type: Image Filter
 * DescriptionFile: com/foo/imaging/info/description.txt
 * ConfigFile: com/foo/imaging/conf/BlurFilter.cfg
 * IconFile: com/foo/imaging/icons/BlurFilter.gif
 * BigIconFile: com/foo/imaging/icons/BlurFilterBig.gif
 * Customizer: com.foo.imaging.BlurFilterCustomizer
 * Version: 1.1.2
 * </pre>
 * <p>
 * The <code>Name</code> field identifies the file in the JAR that is the
 * plugin entry-point class. The <code>PluginName</code> field provides a
 * textual name for the plugin, and the <code>Type</code> field identifies
 * the type of the plugin (the meaning of which is application-dependent).
 * These fields must be present in the entry, whereas the remaining fields
 * are optional.
 * <p>
 * The <code>DescriptionFile</code> field identifies a text file in the JAR
 * that contains a detailed description of the plugin (and may optionally
 * include a copyright message or other related information). The
 * <code>ConfigFile</code> entry identifies a configuration file in the JAR
 * that contains the default settings for the plugin. <code>IconFile</code>
 * identifies an icon image for the plugin. <code>Customizer</code> is the
 * fully-qualified name of a class within the JAR that provides a GUI for
 * configuring the plugin. Finally, <code>Version</code> specifies the
 * version number of the plugin.
 * <p>
 * The <code>PluginLocator</code> reads a JAR Manifest, searching for entries
 * that include a <code>PluginName</code> field. For each valid entry that
 * is found, a <code>Plugin</code> object is constructed which encapsulates
 * all of the information specified in the entry.
 *
 * @since Kiwi 1.3
 *
 * @author Mark Lindner
 * @author PING Software Group
 */

public class PluginLocator
  {
  private Hashtable jarCache;
  private PluginClassLoader loader;
  private PluginContext context;
  private ResourceDecoder decoder;
  private boolean displayAvailable = true;

  /** Construct a new <code>PluginLocator</code> with the specified plugin
   * context.
   *
   * @param context The <code>PluginContext</code> for this plugin locator.
   */

  public PluginLocator(PluginContext context)
    {
    jarCache = new Hashtable();

    loader = new PluginClassLoader();
    decoder = new ResourceDecoder();

    this.context = context;
    }

  /** Specify whether a graphical display is available. If no display is
   * available, the plugin locator will not attempt to load icons or
   * customizers.
   *
   * @param flag A flag specifying whether icons should be loaded or not.
   */

  public void setDisplayAvailable(boolean flag)
    {
    displayAvailable = flag;
    }

  /** Add a package to the locator's list of restricted packages. Plugins are
   * allowed to access classes in restricted packages, but they are not allowed
   * to declare classes that belong to those packages.
   *
   * @param pkg The package name.
   */

  public void addRestrictedPackage(String pkg)
    {
    loader.addRestrictedPackage(pkg);
    }

  /** Add a package to the locator's list of forbidden packages. Plugins are
   * not allowed to access classes in forbidden packages, nor are they allowed
   * to declare classes that belong to those packages.
   *
   * @param pkg The package name.
   */

  public void addForbiddenPackage(String pkg)
    {
    loader.addForbiddenPackage(pkg);
    }

  /* Construct a plugin object from data obtained in the manifest.
   */

  private Plugin constructPlugin(JarFile jar, String className,
                                 Attributes attrs) throws PluginException
    {
    Properties props = new Properties();
    String name = null, type = null, desc = null, descFile = null, vers = null,
      configFile = null, iconFile = null, bigIconFile = null, customizerClass = null;
    ImageIcon icon = null;
    ImageIcon bigIcon = null;
    PluginCustomizer customizer = null;
    Config config = null;

    Iterator attr = attrs.keySet().iterator();

    while(attr.hasNext())
      {
      Attributes.Name nm = (Attributes.Name)attr.next();
      String a = nm.toString();
      String v = attrs.getValue(nm);

      if(a.equals("PluginName"))
        name = v;
      else if(a.equals("Type"))
        type = v;
      else if(a.equals("DescriptionFile"))
        descFile = v;
      else if(a.equals("ConfigFile"))
        configFile = v;
      else if(a.equals("IconFile"))
        iconFile = v;
      else if(a.equals("BigIconFile"))
        bigIconFile = v;
      else if(a.equals("Version"))
        vers = v;
      else if(a.equals("Customizer"))
        customizerClass = v;
      else
        props.put(a, v);
      }

    if((type == null)  || (name == null))
      return(null);

    /* load icon */

    if((iconFile != null) && displayAvailable)
      {
      JarEntry entry = (JarEntry)jar.getEntry(iconFile);
      if(entry != null)
        {
        try
          {
          InputStream in = jar.getInputStream(entry);
          Image im = decoder.decodeImage(in);
          if(im != null)
            icon = new ImageIcon(im);
          in.close();
          }
        catch(IOException ex)
          {
          icon = null;
          }
        }
      }

    /* load big icon */

    if((bigIconFile != null) && displayAvailable)
      {
      JarEntry entry = (JarEntry)jar.getEntry(bigIconFile);
      if(entry != null)
        {
        try
          {
          InputStream in = jar.getInputStream(entry);
          Image im = decoder.decodeImage(in);
          if(im != null)
            bigIcon = new ImageIcon(im);
          in.close();
          }
        catch(IOException ex)
          {
          bigIcon = null;
          }
        }
      }

    /* load config */

    if(configFile != null)
      {
      JarEntry entry = (JarEntry)jar.getEntry(configFile);
      if(entry != null)
        {
        try
          {
          InputStream in = jar.getInputStream(entry);
          config = decoder.decodeConfig(in);
          in.close();
          }
        catch(IOException ex)
          {
          /* ignore error */
          }
        }
      }

    /* load description */

    if(descFile != null)
      {
      JarEntry entry = (JarEntry)jar.getEntry(descFile);
      if(entry != null)
        {
        try
          {
          InputStream in = jar.getInputStream(entry);
          desc = StreamUtils.readStreamToString(new DataInputStream(in));
          in.close();
          }
        catch(IOException ex)
          {
          /* ignore error */
          }
        }
      }

    /* load customizer */

    if((customizerClass != null) && displayAvailable)
      {
      try
        {
        Class c = loader.loadClass(customizerClass, true);
        customizer = (PluginCustomizer)c.newInstance();
        }
      catch(Exception ex)
        {
        ex.printStackTrace();
        throw(new PluginException(ex.toString()));
        }
      }

    /* create plugin */

    Plugin p = new Plugin(loader, context, jar, className, name, type, desc,
                          vers, props, config, icon, bigIcon, customizer);

    return(p);
    }

  /** Find all plugins in the specified JAR file.
   *
   * @param jarFile The path of a JAR file.
   *
   * @return A (possibly empty) <code>Enumeration</code> of
   * <code>Plugin</code> objects representing the plugins found in the JAR
   * file.
   *
   * @exception java.io.FileNotFoundException If the JAR file was not found.
   * @exception java.io.IOException If an error occurred while reading the
   * JAR file.
   * @exception kiwi.util.plugin.PluginException If an error occurred while
   * reading plugin information.
   */

  public Enumeration findPlugins(String jarFile)
    throws FileNotFoundException, IOException, PluginException
    {
    Vector entries = new Vector();

    CacheEntry e = getCacheEntry(jarFile);

    if(e.manifest != null)
      {
      Map map = e.manifest.getEntries();

      Iterator iter = map.keySet().iterator();
      while(iter.hasNext())
        {
        String classFile = (String)iter.next();
        Attributes attrs = (Attributes)map.get(classFile);

        String className = PluginClassLoader.classPathToName(classFile);

        Plugin p = constructPlugin(e.jar, className, attrs);
        if(p != null)
          entries.addElement(p);
        }
      }

    return(entries.elements());
    }

  /** Find a plugin by name.
   *
   * @param jarFile The JAR file to search.
   * @param name The class name of the plugin.
   *
   * @return The <code>Plugin</code>, if found, or <code>null</code> otherwise.
   */

  public Plugin findPlugin(String jarFile, String className)
    throws PluginException
    {
    /* is there a manifest cached for jarFile? if no, load it */

    CacheEntry e = null;

    try
      {
      e = getCacheEntry(jarFile);
      }
    catch(IOException ex)
      {
      throw(new PluginException(ex.toString()));
      }

    /* find <name> in manifest cached for <jarFile>. & return it */

    if(e.manifest == null)
      return(null);

    Plugin p = null;

    String path = PluginClassLoader.classNameToPath(className);

    Attributes attrs = (Attributes)e.manifest.getEntries().get(path);
    if(attrs != null)
      p = constructPlugin(e.jar, className, attrs);

    if(p == null)
      throw(new PluginException("No manifest entry found for plugin: "
        + className));

    return(p);
    }

  /* Locate a JarFile in the cache.
   */

  private CacheEntry getCacheEntry(String jarFile) throws IOException
    {
    CacheEntry e = (CacheEntry)jarCache.get(jarFile);

    if(e == null)
      {
      JarFile jar = new JarFile(new File(jarFile));
      Manifest mf = jar.getManifest();

      loader.addJarFile(jar);

      e = new CacheEntry(jar, mf);
      jarCache.put(jarFile, e);
      }

    return(e);
    }

  /* Cache entry.
   */

  private class CacheEntry
    {
    JarFile jar;
    Manifest manifest;

    CacheEntry(JarFile jar, Manifest manifest)
      {
      this.jar = jar;
      this.manifest = manifest;
      }
    }

  }

/* end of source file */
