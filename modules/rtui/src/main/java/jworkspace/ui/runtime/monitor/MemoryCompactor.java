package jworkspace.ui.runtime.monitor;
/* ----------------------------------------------------------------------------
 * Part of the org.flat222.mac package of Java utilities, libraries and applications.
 * <p>
 * Copyright (C) 1999  Michael Anthony Connell
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * <p>
 * The GPL should be in the file LICENSE.TXT located in the root directory
 * of the archive from which you extracted this software. If it is not
 * please contact the author at
 * <p>
 * mac@flat222.org
  ----------------------------------------------------------------------------
*/
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.joda.time.DateTimeConstants;

import com.hyperrealm.kiwi.ui.KButton;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jworkspace.ui.runtime.LangResource;

/**
 * Runs as a separate thread and measures amount of available memory.
 * @author Michael Anthony Connell
 */
@SuppressFBWarnings("DM_GC")
public class MemoryCompactor extends Thread {

    private static final double PERCENT_100 = 100.0;
    private static final double TWO_POWER_10 = 1024.0;
    private JPanel statusarea = null; // if set we can put a button here...
    private KButton button; //update this with our status

    private boolean timetodie = false; // when true, we'll exit...
    private final boolean[] runningflag;    // set this to false before we exit...
    private final int minfree; // % minimum free memory before we try to gc

    /**
     */
    MemoryCompactor(int minfree, boolean[] runningflag) {
        this.runningflag = runningflag;
        this.runningflag[0] = true;
        this.minfree = minfree;
    }

    /**
     */
    void setStatusArea(JPanel widget) {
        statusarea = widget;
        button = new KButton(LangResource.getString("MC"));
        button.setDefaultCapable(false);
        button.addActionListener(ex -> collectGarbage());
        button.setToolTipText(LangResource.getString("message#246"));
        statusarea.add(button);
        setStatus(LangResource.getString("message#243"));
    }

    /**
     */
    public void run() {
        StringBuilder sb = new StringBuilder();
        Runtime rt = Runtime.getRuntime();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(0);

        double maxmem;
        double freemem;
        double pcfree;

        for (;;) {
            //Workspace.logInfo("Thumbnailer in for(;;) loop...");
            if (timetodie) {
                //Workspace.logInfo("timetodie. t="+t);
                runningflag[0] = false;
                return; // and we go...
            }
            maxmem = rt.totalMemory();
            maxmem /= TWO_POWER_10;
            freemem = rt.freeMemory();
            freemem /= TWO_POWER_10;
            pcfree = (freemem / maxmem) * PERCENT_100;

            if (pcfree < minfree) {
                collectGarbage();
            }

            sb.setLength(0);
            sb.append(LangResource.getString("message#242")).append(": ");
            sb.append(nf.format(pcfree));
            sb.append(LangResource.getString("message#235"));

            setStatus(sb.toString());
            try {
                Thread.sleep(DateTimeConstants.MILLIS_PER_SECOND); // Sleep 1 second
            } catch (InterruptedException ie) {
                // contine for(;;) loop
            }
        }
    }

    /**
     */
    public void shutdown() {
        timetodie = true;
    }

    private void collectGarbage() {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        rt.runFinalization();
    }

    /**
     */
    private void setStatus(String s) {
        if (statusarea != null) {
            final JButton b = button;
            final String ss = s;
            SwingUtilities.invokeLater(new Runnable() {
                public synchronized void run() {
                    if (b != null) {
                        b.setText(ss);
                        b.revalidate();
                    }
                }
            });
        }
    }
}
