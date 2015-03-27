package jworkspace.ui.runtime;

/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2000 Anton Troshin

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
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
/**
 * Monitor component. Shows amount of memory, available to system.
 */
class MemoryMonitor extends JPanel implements Runnable
{

  public Thread thread;
  private int w, h;
  private BufferedImage bimg;
  private Graphics2D big;
  private Font font = new Font("Times New Roman", Font.PLAIN, 11);
  private Runtime r = Runtime.getRuntime();
  private int columnInc;
  private float pts[];
  private int ptNum;
  private int ascent, descent;
  private float freeMemory, totalMemory;
  private Rectangle graphOutlineRect = new Rectangle();
  private Rectangle2D mfRect = new Rectangle2D.Float();
  private Rectangle2D muRect = new Rectangle2D.Float();
  private Line2D graphLine = new Line2D.Float();
  private Color graphColor = new Color(46, 139, 87);
  private Color mfColor = new Color(0, 100, 0);
public MemoryMonitor()
{
  setBackground(Color.black);

  addMouseListener(new MouseAdapter()
    {
     public void mouseClicked(MouseEvent e)
       {
        if (thread == null) start(); else stop();
     }
  });
  start();
}
/**
 * This method executes memory measurement
 * and prints all data into special array
 * of values. The size of such array is
 * a component's preferred width.
 * Creation date: (13.06.2001 13:23:38)
 */
protected void aquireValues()
{
  /**
    * Get free and total memory sizes,
    * store in array of pixels.
    */
  totalMemory = (float) r.totalMemory();
  freeMemory = (float) r.freeMemory();
  /**
    * First time created array of values.
    */
  if (pts == null)
  {
    pts = new float[getPreferredSize().width];
    /**
     * The number of current point.
     */
    ptNum = 0;
  }
  /**
   * Get used memory size
   */
  pts[ptNum] = totalMemory - freeMemory;
  /**
    * each new point stores amount of used memory
    */
  //Workspace.logInfo(ptNum + " - " + pts[ptNum]);
  /**
   * Now we have to follow to new cell of data
   * Check if it is available first
   */
  if (ptNum + 1 == pts.length)
  {
    /**
     * If we are here, the last point is filled.
     * Now we have to  throw out oldest point
     * and fill the same last point next time.
     */
    for (int j = 1; j < pts.length; j++)
    {
      pts[j - 1] = pts[j];
    }
  }
  else
  {
    /**
     * Easily proceed with next point
     */
    ptNum++;
  }
}
public Dimension getMaximumSize()
{
  return getPreferredSize();
}
public Dimension getMinimumSize()
{
    return getPreferredSize();
}
public Dimension getPreferredSize()
{
  return new Dimension(200,130);
}
public void paint(Graphics g)
{
  if (big == null)
  {
    return;
  }
  big.setBackground(getBackground());
  big.clearRect(0, 0, w, h);

  // .. Draw allocated and used strings ..
  big.setColor(Color.green);
  big.drawString(String.valueOf((int) totalMemory / 1024) +
            LangResource.getString("message#241"), 4.0f, (float) ascent + 0.5f);
  big.drawString(String.valueOf(((int) (totalMemory - freeMemory)) / 1024) +
            LangResource.getString("message#247"), 4, h - descent);

  // Calculate remaining size
  float ssH = ascent + descent;
  float remainingHeight = (float) (h - (ssH * 2) - 0.5f);
  float blockHeight = remainingHeight / 10;
  float blockWidth = 20.0f;
  float remainingWidth = (float) (w - blockWidth - 10);

  // .. Memory Free ..
  big.setColor(mfColor);
  int MemUsage = (int) ((freeMemory / totalMemory) * 10);
  int i = 0;
  for (; i < MemUsage; i++)
  {
    mfRect.setRect(5, (float) ssH + i * blockHeight, blockWidth,
             (float) blockHeight - 1);
    big.fill(mfRect);
  }

  // .. Memory Used ..
  big.setColor(Color.green);
  for (; i < 10; i++)
  {
    muRect.setRect(5, (float) ssH + i * blockHeight, blockWidth,
              (float) blockHeight - 1);
    big.fill(muRect);
  }

  // .. Draw History Graph ..
  big.setColor(graphColor);

  int graphX = 30;
  int graphY = (int) ssH;
  int graphW = w - graphX - 5;
  int graphH = (int) remainingHeight;

  graphOutlineRect.setRect(graphX, graphY, graphW, graphH);
  big.draw(graphOutlineRect);
  int graphRow = graphH / 10;

  // .. Draw row ..
  for (int j = graphY + graphH; j >= graphY; j -= graphRow)
  {
    graphLine.setLine(graphX, j, graphX + graphW, j);
    big.draw(graphLine);
  }

  /**
   *.. Draw animated column movement ..
   * Fixed size of 15 pixels
   */
  int graphColumn = 15;
  if (columnInc == 0)
  {
    columnInc = graphColumn;
  }
  for (int j = graphX + columnInc; j < graphW + graphX; j += graphColumn)
  {
    graphLine.setLine(j, graphY, j, graphY + graphH);
    big.draw(graphLine);
  }
  --columnInc;

  /**
   * History graph. Actually, drawing goes
   * on plot with width "graphW" - pts array is resized if
   * width of drawing area exceeds length of array of points.
   */
  big.setColor(Color.yellow);
  /**
   * If array of points is shorter, than width,
   * lets make it bigger.
   */
  if (pts.length < graphW)
  {
     float[] tmp = new float[graphW];
     System.arraycopy(pts, 0, tmp, 0, pts.length);
     pts = new float[graphW];
     System.arraycopy(tmp, 0, pts, 0, tmp.length);
  }
  /**
   * prepare actual array of scaled points
   */
  int[] temp = new int[pts.length];
  /**
   * Scale points with total memory
   */
  for (int z = 0; z < pts.length; z++)
  {
    temp[z] = (int) (graphY + graphH * (1 - pts[z] / totalMemory));
  }

  for (int j = graphX + graphW -
     Math.min(ptNum, graphW), k = Math.max(0, ptNum - graphW); k < ptNum; k++,
                                                                  j++)
  {
    if (k != 0)
    {
      if (pts[k] != pts[k - 1])
      {
        big.drawLine(j - 1, temp[k - 1], j, temp[k]);
      }
      else
      {
        big.fillRect(j, temp[k], 1, 1);
      }
    }
  }
  /**
   * Actual drawing
   */
  if (isShowing()) g.drawImage(bimg, 0, 0, this);
}
public void run()
{
  Thread me = Thread.currentThread();
  /**
   * Infinite loop
   */
  while (thread == me)
  {
    Dimension d = getSize();
    /**
     * If size changed or first time launched.
     * Refresh graphic related values.
     */
    if (d.width != w || d.height != h)
    {
      w = d.width;
      h = d.height;
      bimg = (BufferedImage) createImage(w, h);
      big = bimg.createGraphics();
      big.setFont(font);
      FontMetrics fm = big.getFontMetrics(font);
      ascent = (int) fm.getAscent();
      descent = (int) fm.getDescent();
    }

    aquireValues();
    /**
     * Paint aquired data
     */
    if (isShowing()) repaint();

    try
    {
      thread.sleep(999);
    }
    catch (InterruptedException e)
    {
      break;
    }
  }
}
public void start()
{
  thread = new Thread(this);
  thread.setPriority(Thread.MIN_PRIORITY);
  thread.setName("MemoryMonitor");
  thread.start();
}
public synchronized void stop()
{
  thread = null;
  notify();
}
}
