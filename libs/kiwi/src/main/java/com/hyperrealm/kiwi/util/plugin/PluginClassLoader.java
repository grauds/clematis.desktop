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

package com.hyperrealm.kiwi.util.plugin;

import java.io.*;
import java.util.*;
import java.util.jar.*;

/* The internal class loader for plugins.
 *
 * The plugin will be loaded in its own namespace. It will have access to all
 * classes in its own namespace, AND access to any class that belongs to any
 * of the _restrictedPackages_ list, BUT NOT access to any class that belongs
 * to any of the _forbiddenPackages_ list.
 *
 * - If asked to load a class that is from a restricted package, the class
 * loader delegates to the system class loader. Packages "java.*' and 'javax.*'
 * are automatically added to the restricted package list. This prevents the
 * plugin from loading classes into those packages, or replacing system classes
 * with its own in its namespace.
 *
 * - If asked to load a class that is from a forbidden package, the class
 * loader throws a ClassNotFoundException.
 *
 * - If asked to load any other class other than those listed above, the class
 * loader attempts to load the class by searching for it in the registered JAR
 * files.
 *
 * @author Mark Lindner
 */

class PluginClassLoader extends ClassLoader
{
  private ArrayList<String> forbiddenPackages;
  private ArrayList<String> restrictedPackages;
  private ArrayList<JarFile> jars;

  /*
   */
  
  PluginClassLoader(ArrayList<String> forbiddenPackages,
                    ArrayList<String> restrictedPackages)
  {
    this.forbiddenPackages = forbiddenPackages;
    this.restrictedPackages = restrictedPackages;
    
    jars = new ArrayList<JarFile>();
  }

  /*
   */
  
  void addJarFile(JarFile file)
  {
    synchronized(jars)
    {
      if((file != null) && ! jars.contains(file))
        jars.add(file);
    }
  }

  /**
   */
  
  public synchronized Class loadClass(String className, boolean resolve) 
    throws ClassNotFoundException
  {
    Class result = null;

    // extract package name
    
    int ind = className.lastIndexOf('.');
    if(ind < 0)
      return(null); // won't support classes in unnamed packages

    String classPackage = className.substring(0, ind);
    
    // first check our cache

    result = findLoadedClass(className);

    // not in cache...
    
    if(result == null)
    {

      // try to load it from the system class loader first, but only if it's
      // not from a forbidden package

      if(! isForbiddenPackage(classPackage))
      {
        try
        {
          return(findSystemClass(className));
        }
        catch(ClassNotFoundException ex)
        {
          /* ignore & continue */
        }

        // no luck, so scan through JAR files looking for the class, but only
        // if it's not a restricted class (or a forbidden class)
      
        if(! isRestrictedPackage(classPackage))
        {
          String path = classNameToPath(className);

          Iterator<JarFile> iter = jars.iterator();
          while(iter.hasNext())
          {
            JarFile jar = iter.next();
            JarEntry entry = (JarEntry)jar.getEntry(path);
            if(entry == null)
              continue; // move on to next JAR file
                    
            // found it! so let's load it...

            BufferedInputStream ins = null;
            int r = 0, size = 0;
            byte b[] = null;
            
            try
            {
              ins = new BufferedInputStream(jar.getInputStream(entry));
              size = (int)entry.getSize();
              
              b = new byte[size];
              r = ins.read(b, 0, size);
            }
            catch(IOException ex)
            {
              r = -1;
            }
            
            if(ins != null)
            {
              try
              {
                ins.close();
              }
              catch(IOException ex) { /* ignore */ }
            }
            
            if(r != size) // got less or more bytes than we expected?
              throw(new ClassFormatError(className));
            
            result = defineClass(className, b, 0, size);
            break;
          }
        }
      }
    }

    if(result == null)
      throw(new ClassNotFoundException(className));

    if(resolve)
      resolveClass(result);

    return(result);
  }

  /*
   */

  private boolean isForbiddenPackage(String packageName)
  {
    return(findPackage(packageName, forbiddenPackages));
  }

  /*
   */

  private boolean isRestrictedPackage(String packageName)
  {
    return(findPackage(packageName, restrictedPackages));
  }

  /*
   */

  private boolean findPackage(String packageName,
                              ArrayList<String> packageList)
  {
    synchronized(packageList)
    {
      Iterator<String> iter = packageList.iterator();
    
      while(iter.hasNext())
      {
        String pkg = iter.next();
        
        if(pkg.endsWith(".*"))
        {
          if(packageName.startsWith(pkg.substring(0, pkg.length() - 1)))
            return(true);
        }
        else if(pkg.equals(packageName))
          return(true);
      }
    }

    return(false);
  }

  /**
   */

  public synchronized InputStream getResourceAsStream(String name)
  {
    /* Scan through JAR files looking for the resource */

    Iterator<JarFile> iter = jars.iterator();
    while(iter.hasNext())
    {
      JarFile jar = (JarFile)iter.next();
      JarEntry entry = jar.getJarEntry(name);

      if(entry != null)
      {
        try
        {
          return(jar.getInputStream(entry));
        }
        catch(IOException ex)
        {
          /* ignore error, & continue */
        }        
      }
    }
    
    return(null);
  }

  /* convert a class name to its corresponding JAR entry name
   */
  
  static String classNameToPath(String className)
  {
    return(className.replace('.', '/') + ".class");
  }

  /* convert a JAR entry name to its corresponding class name
   */
  
  static String pathToClassName(String path)
  {
    return(path.substring(0, path.length() - 6).replace('/', '.'));
  }

}

/* end of source file */
