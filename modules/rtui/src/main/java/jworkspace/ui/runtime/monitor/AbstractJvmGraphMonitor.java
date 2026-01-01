package jworkspace.ui.runtime.monitor;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.Timer;

import com.hyperrealm.kiwi.ui.KPanel;


public abstract class AbstractJvmGraphMonitor extends KPanel {

    protected static final int UPDATE_MS = 1000;
    protected static final float SMOOTHING = 0.25f;
    protected static final float HEADROOM = 1.10f;

    protected float[] pts;
    protected int ptNum;
    protected float last;

    protected int w, h, ascent, descent;
    protected BufferedImage buffer;
    protected Graphics2D g2;

    protected final Font font = new Font("Times New Roman", Font.PLAIN, 11);
    protected final Timer timer;

    protected AbstractJvmGraphMonitor() {
        setBackground(Color.black);
        setToolTipText(""); // enable tooltips

        timer = new Timer(UPDATE_MS, e -> {
            sample();
            repaint();
        });
        timer.start();
    }

    // ===== subclass API =====
    protected abstract float sampleValue();
    protected abstract String title();
    protected abstract String unit();

    // ===== sampling =====
    protected void sample() {
        if (pts == null) {
            pts = new float[getPreferredSize().width];
            ptNum = 0;
        }

        float v = sampleValue();
        last = (last < 0) ? v : last + SMOOTHING * (v - last);
        pts[ptNum] = last;

        if (++ptNum == pts.length) {
            System.arraycopy(pts, 1, pts, 0, pts.length - 1);
            ptNum--;
        }
    }

    // ===== layout =====
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 100);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        resizeBuffer();
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        resizeBuffer();
    }

    protected void resizeBuffer() {
        w = getWidth();
        h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }

        buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2 = buffer.createGraphics();
        g2.setFont(font);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        ascent = fm.getAscent();
        descent = fm.getDescent();
    }

    // ===== painting =====
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void paint(Graphics g) {
        if (g2 == null) {
            return;
        }

        g2.setBackground(getBackground());
        g2.clearRect(0, 0, w, h);

        int top = ascent + descent;
        int graphX = 5;
        int graphY = top + 5;
        int graphW = w - 10;
        int graphH = h - graphY - 5;

        drawLegend(graphX);
        drawGrid(graphX, graphY, graphW, graphH);
        drawGcMarkers(graphX, graphY, graphW, graphH);
        drawGraph(graphX, graphY, graphW, graphH);

        g.drawImage(buffer, 0, 0, this);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    protected void drawLegend(int x) {
        g2.setColor(Color.green);
        g2.drawString(title(), x, ascent + 1);

        if (ptNum > 0) {
            String cur = String.format("%.1f %s", pts[ptNum - 1], unit());
            g2.drawString(cur, w - g2.getFontMetrics().stringWidth(cur) - 5,
                ascent + 1);
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    protected void drawGrid(int x, int y, int w, int h) {
        g2.setColor(new Color(46, 139, 87));
        g2.drawRect(x, y, w, h);

        Line2D line = new Line2D.Float();
        for (int i = 1; i < 10; i++) {
            int yy = y + h * i / 10;
            line.setLine(x, yy, x + w, yy);
            g2.draw(line);
        }
    }

    protected void drawGcMarkers(int x, int y, int w, int h) {
        List<Long> events =
            GcEventTracker.recentEvents((long) UPDATE_MS * w);

        long now = System.currentTimeMillis();
        g2.setColor(Color.magenta);

        for (long t : events) {
            int dx = (int) ((now - t) / (float) (UPDATE_MS * w) * w);
            int px = x + w - dx;
            g2.drawLine(px, y, px, y + h);
        }
    }

    protected void drawGraph(int x, int y, int w, int h) {
        if (pts.length < w) {
            pts = java.util.Arrays.copyOf(pts, w);
        }

        float max = 1f;
        int start = Math.max(0, ptNum - w);
        for (int i = start; i < ptNum; i++) {
            max = Math.max(max, pts[i]);
        }
        max *= HEADROOM;

        int px = -1, py = -1;
        g2.setColor(Color.yellow);

        for (int i = start; i < ptNum; i++) {
            int cx = x + w - (ptNum - i);
            float r = Math.min(1f, pts[i] / max);
            int cy = y + (int) (h * (1 - r));

            cy = Math.max(y, Math.min(y + h, cy));

            if (px >= 0) {
                g2.drawLine(px, py, cx, cy);
            }
            px = cx;
            py = cy;
        }
    }

    // ===== tooltip =====
    @SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:MagicNumber"})
    @Override
    public String getToolTipText(MouseEvent e) {
        if (pts == null || ptNum == 0) {
            return null;
        }

        int graphW = w - 10;
        int idx = ptNum - (w - e.getX());
        if (idx < 0 || idx >= ptNum) {
            return null;
        }

        return String.format("%.2f %s", pts[idx], unit());
    }
}

