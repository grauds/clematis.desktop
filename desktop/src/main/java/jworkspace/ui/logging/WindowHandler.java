package jworkspace.ui.logging;
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
import jworkspace.LangResource;
import jworkspace.kernel.Workspace;

import java.util.logging.*;
import java.util.Hashtable;
import java.io.OutputStream;
/**
 * Window logging handler
 * This is taken from book of Gregory M. Travis "JDK 1.4 tutorial"
 * @version 1.0
 * @author <a href='mailto:anton.troshin@gmail.com'>Anton Troshin</a>
 */
public class WindowHandler extends StreamHandler
{
    // The default width and height for a logging window;
    // these can be overridden in the logging.properties file
    static private final int defaultWidth = 400;
    static private final int defaultHeight = 500;
    // The logger being displayed in this window
    private Logger logger;
    /**
    * Set up the connection between the stream
    * handler and the stream window: log data
    * written to the handler goes to the window
    */
    public WindowHandler( String loggerName )
    {
        logger = Logger.getLogger( loggerName );
        // Get the output stream that feeds the window
        // and install it in the Stream handler
        WindowHandlerWindow whw = new WindowHandlerWindow( loggerName );
        OutputStream out = whw.getOutputStream();
        setOutputStream( out );
        setLevel( Level.ALL );
    }
    /**
    * Log a LogRecord. We flush after every log
    * because we want to see log messages as soon as
    * they arrive
    */
    public void publish( LogRecord lr )
    {
        // Check any filter, and possibly other criteria,
        // before publishing
        if (!isLoggable( lr ))
        {
            return;
        }
        super.publish( lr );
        flush();
    }
    /**
    * De-install this Handler from its Logger
    */
    private void removeHandler()
    {
        logger.removeHandler( this );
    }
    /**
    * Inner class: WindowHandlerWindow is a StreamWindow.
    * We need to override closeWindow() so that we
    * can de-install the handler when the window is
    * closed
    */
    class WindowHandlerWindow extends StreamWindow
    {
        public WindowHandlerWindow( String name )
        {
            super( LangResource.getString("LoggingPanel.loggerFor") + " " + name );
            // Assume the defaults, initially
            int width = defaultWidth;
            int height = defaultHeight;
            LogManager manager = LogManager.getLogManager();
            // We need the fully-qualified class name to access
            // the properties
            String className = WindowHandler.class.getName();
            String widthString = manager.getProperty( className+".width" );
            if (widthString != null)
            {
                width = Integer.parseInt( widthString );
            }
            String heightString = manager.getProperty( className+".height" );
            if (heightString != null)
            {
                height = Integer.parseInt( heightString );
            }
            setSize( width, height );
            // Fire event
            Hashtable lparam = new Hashtable();
            lparam.put("frame", this);
            Workspace.fireEvent( new Integer(1003), lparam, null );
        }
        public void dispose()
        {
            removeHandler();
            super.dispose();
        }
    }
}