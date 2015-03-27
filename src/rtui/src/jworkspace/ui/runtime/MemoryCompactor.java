package jworkspace.ui.runtime;

/**
    Part of the org.flat222.mac package of Java utilities, libraries and
    applications.

    Copyright (C) 1999  Michael Anthony Connell

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    The GPL should be in the file LICENSE.TXT located in the root directory
    of the archive from which you extracted this software. If it is not
    please contact the author at

    mac@flat222.org

 */
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.text.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import kiwi.ui.*;
/**
 *  Runs as a separate thread and
 *  mesaures amount of available memory.
*/
public class MemoryCompactor extends Thread
{

  JPanel statusarea=null; // if set we can put a button here...
    KButton button; //update this with our status

    boolean timetodie=false; // when true, we'll exit...
    boolean[] runningflag;    // set this to false before we exit...
    int minfree; // % minimum free memory before we try to gc

    /**
       The runningflag is set false just before we exit
     */
    public MemoryCompactor(int minfree,boolean[] runningflag)
    {
      this.runningflag=runningflag;
      this.runningflag[0]=true;
      this.minfree=minfree;
    }
    /**
       If a Statusarea is set,update it with  status
       The Statusarea is just a JLabel (panel?)
     */
    public void setStatusArea(JPanel widget)
    {
      statusarea=widget;
      button=new KButton(LangResource.getString("MC"));
      button.setDefaultCapable(false);
      button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent ex)
        {
           collectGarbage();
        }
      });
      button.setToolTipText(LangResource.getString("message#246"));
      statusarea.add(button);
      setStatus(LangResource.getString("message#243"));
    }
    /**
       This is where our thread enters...
     */
    public void run()
    {
      StringBuffer sb=new StringBuffer();
      Runtime rt=Runtime.getRuntime();
      NumberFormat nf=NumberFormat.getNumberInstance();
      nf.setMaximumFractionDigits(0);

      double maxmem;
      double freemem;
      double pcfree;

      int time=0;
      for(;;)
      {
        //Workspace.logInfo("Thumbnailer in for(;;) loop...");
        if (timetodie==true)
        {
          //Workspace.logInfo("timetodie. t="+t);
          runningflag[0]=false;
          return; // and we go...
        }
        maxmem=rt.totalMemory(); maxmem/=(1024.0);
        freemem=rt.freeMemory(); freemem/=(1024.0);
        pcfree=(freemem/maxmem)*100.0;

        if (pcfree < 0.6)
        {
           collectGarbage();
        }

        sb.setLength(0);
        sb.append(LangResource.getString("message#242") + ": ");
        sb.append(nf.format(pcfree));
        sb.append(LangResource.getString("message#235"));

        setStatus(sb.toString());
        try
        {
          Thread.sleep(1000); // Sleep 1 second
        }
        catch (InterruptedException ie)
        {
          // contine for(;;) loop
        }
        time++;
      }
    }

    /**
       When we are shutting dow, call this to exit cleanly
     */
    public void shutdown()
    {
      timetodie=true;
    }
    private void collectGarbage()
    {
      Runtime rt=Runtime.getRuntime();
      rt.gc();
      rt.runFinalization();
    }
    /**
       Sets the status label
     */
    private void setStatus(String s)
    {
      if (statusarea!=null)
      {
        final JButton b=button;
        final String ss=s;
        SwingUtilities.invokeLater(new Runnable()
        {
          public synchronized void run()
          {
               if (b != null)
               {
                 b.setText(ss);
                 b.revalidate();
               }
          }
        });
      }
    }
}
