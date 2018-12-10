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

package com.hyperrealm.kiwi.ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * An extension of <code>JPanel</code> that provides some additional
 * functionality including support for background texture tiling and
 * named-component lookup. Lightweight components nested within the
 * <code>KPanel</code> should be transparent and unbuffered; the tiled
 * background will not show through heavyweight components.
 * <p>
 * Note that images with transparent portions should <i>not</i> be used with
 * <code>KPanel</code>s.
 * <p>
 * A <code>KPanel</code> will always be transparent if a background
 * texture has not been specified. Therefore <code>KPanel</code>s may be
 * safely nested if only the outermost instance has a texture applied. It is
 * recommended that <code>KPanel</code>s always be used in place of Swing
 * <code>JPanel</code>s.
 * <p>
 * In most cases it is convenient to use a <code>KFrame</code> to provide a
 * textured background for an entire window.
 * <p>
 * If a solid color background is desired in place of a texture, then the
 * outermost <code>KPanel</code> should be made opaque via the call
 * <code>setOpaque(true)</code>, and the texture should be turned off via a
 * call to <code>setTexture(null)</code>. The background color can be set
 * using <code>setBackground()</code> as usual.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.KFrame
 */

public class KPanel extends JPanel {

    static final int DEFAULT_DELAY = 30;

    private static final int DIVIDER = 10;

    private static final float DEFAULT_ALPHA_STEP = 0.1f;

    private Image image = null;

    private float alpha = 1.0f;

    private float alphaStep = 0.0f;

    private AlphaComposite alphaComposite = null;

    private final Timer timer = new Timer(DEFAULT_DELAY, evt -> doFade());

    private boolean fading = false;

    /**
     * Construct a new <code>KPanel</code>. The newly created
     * <code>KPanel</code> will be transparent.
     */

    public KPanel() {
        super(false);

        init();
    }

    /**
     * Construct a new <code>KPanel</code>. Creates a new
     * <code>KPanel</code> with the specified layout manager. The newly
     * created <code>KPanel</code> will be transparent.
     *
     * @param lm The layout manager to use for the panel.
     */

    public KPanel(LayoutManager lm) {
        super(lm, false);

        init();
    }

    /**
     * Construct a new <code>KPanel</code>. Creates a new
     * <code>KPanel</code> with the specified layout manager and
     * background image.
     *
     * @param lm    The layout manager to use for the panel.
     * @param image The image with which the background of the panel will be
     *              tiled.
     */

    public KPanel(LayoutManager lm, Image image) {
        super(lm, false);

        this.image = image;
        init();
    }

    /**
     * Construct a new <code>KPanel</code>. Creates a new
     * <code>KPanel</code> with a <code>BorderLayout</code> and the
     * specified background image.
     *
     * @param image The image with which the background of the panel will be
     *              tiled.
     */

    public KPanel(Image image) {
        super(new BorderLayout(0, 0), true);

        this.image = image;
        init();
    }

    /*
     */

    private void init() {
        super.setOpaque(false);
    }

    /**
     * Paint the component. Tiles the component with the background image,
     * if one has been provided.
     */

    public void paintComponent(Graphics gc) {
        if (alphaComposite != null) {
            Graphics2D gc2d = (Graphics2D) gc;
            gc2d.setComposite(alphaComposite);
        }

        if (image == null) {
            super.paintComponent(gc);
        } else {
            Dimension size = new Dimension();
            getSize(size);
            int ih = image.getHeight(null);
            int iw = image.getWidth(null);

            int vc = (size.height / ih) + 1;
            int hc = (size.width / iw) + 1;

            for (int y = 0; y < vc; y++) {
                for (int x = 0; x < hc; x++) {
                    gc.drawImage(image, x * iw, y * ih, null);
                }
            }
        }
    }

    /**
     * Set the background texture.
     *
     * @param image The image to use as the background texture for the panel.
     */

    public void setTexture(Image image) {
        this.image = image;
        repaint();
    }

    /**
     * Get the transparency level for this component.
     *
     * @since Kiwi 2.2
     */

    public float getAlpha() {
        return (alpha);
    }

    /**
     * Set the transparency level for this component.
     *
     * @param alpha The transparency level, a value between 0.0 (fully
     *              transparent) and 1.0 (fully opaque). Out of range values are silently
     *              clipped.
     * @since Kiwi 2.2
     */
    @SuppressWarnings("all")
    public void setAlpha(float alpha) {
        if (alpha < 0.0f) {
            alpha = 0.0f;
        } else if (alpha > 1.0f) {
            alpha = 1.0f;
        }

        this.alpha = alpha;

        if (alpha != 1.0f) {
            alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                alpha);
        } else {
            alphaComposite = null;
        }

        repaint();
    }

    /**
     * Determine if automatic fading is enabled on the panel.
     *
     * @since Kiwi 2.2
     */

    public boolean isFadingEnabled() {
        return (fading);
    }

    /**
     * Enable or disable automatic fading. When fading is enabled, the panel
     * will gradually fade in or fade out in response to calls to
     * <code>setVisible()</code>.
     *
     * @param fading <b>true</b> to enable automatic fading, <b>false</b> to
     *               disable it. The feature is disabled by default.
     * @since Kiwi 2.2
     */

    public void setFadingEnabled(boolean fading) {
        this.fading = fading;
    }

    /**
     * Fade the panel in (from invisible to visible).
     *
     * @since Kiwi 2.2
     */

    public void fadeIn() {
        synchronized (timer) {
            alphaStep = DEFAULT_ALPHA_STEP;
            if (!timer.isRunning()) {
                timer.start();
            }
        }
    }

    /**
     * Fade the panel out (from visible to invisible).
     *
     * @since Kiwi 2.2
     */

    private void fadeOut() {
        synchronized (timer) {
            alphaStep = -DEFAULT_ALPHA_STEP;
            if (!timer.isRunning()) {
                timer.start();
            }
        }
    }

    /*
     */

    public void setVisible(boolean flag) {
        if (fading) {
            if (flag) {
                fadeIn();
            } else {
                fadeOut();
            }
        } else {
            super.setVisible(flag);
        }
    }

    /*
     */

    private synchronized void doFade() {
        int a = (int) ((getAlpha() + alphaStep) * DIVIDER);
        float na = (float) a / DIVIDER;
        setAlpha(na);

        if ((na == 0.0) || (na == 1.0)) {
            alphaStep = 0;
            timer.stop();
        }

        if (na == 0.0) {
            super.setVisible(false);
        } else if (!isVisible()) {
            super.setVisible(true);
        }
    }

    /**
     * Search for a component by name. Components can be named using the
     * <code>setName()</code> method of <code>Component</code>. This is a useful
     * way of identifying a component when comparison by reference is not
     * possible. This method searches this <code>KPanel</code>'s component
     * hierarchy for a component with the given name.
     *
     * @param name The name of the component to search for.
     * @return The matching component, or <code>null</code> if there is no
     * component in the component hierarchy with the given name.
     * @see java.awt.Component#setName
     * @see java.awt.Component#getName
     */

    public Component getComponentByName(String name) {
        return findComponent(this, name);
    }

    /*
     */

    private Component findComponent(Container cont, String name) {

        int ct = cont.getComponentCount();

        Component ret = null;

        for (int i = 0; i < ct; i++) {
            Component subc = cont.getComponent(i);
            if (subc.getName().equals(name)) {
                ret = subc;
                break;
            }
        }

        if (ret != null) {
            return ret;
        }

        // none of them matched, so recurse

        for (int i = 0; i < ct; i++) {
            Component c = cont.getComponent(i);

            if (c instanceof Container) {
                Component subc = findComponent((Container) c, name);
                if (subc != null) {
                    ret = subc;
                    break;
                }
            }
        }

        return ret;
    }

    /**
     * Set the focus order for this component. The focus order specifies the
     * order in which child components will receive focus when the user presses
     * the tab key to move between input components.
     *
     * @param order The component array representing the desired focus order.
     */

    public void setFocusOrder(JComponent[] order) {
        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new KFocusTraversalPolicy(order));
    }

    /*
     */

    private class KFocusTraversalPolicy extends FocusTraversalPolicy {
        private JComponent[] order;

        /*
         */

        KFocusTraversalPolicy(JComponent[] order) {
            this.order = order;
        }

        /*
         */

        public Component getComponentAfter(Container focusCycleRoot,
                                           Component component) {
            int i = 0;

            for (; i < order.length; i++) {
                if (order[i] == component) {
                    break;
                }
            }

            if (i >= order.length - 1) {
                return (null);
            } else {
                return order[++i];
            }
        }

        /*
         */

        public Component getComponentBefore(Container focusCycleRoot,
                                            Component component) {
            int i = 0;

            for (; i < order.length; i++) {
                if (order[i] == component) {
                    break;
                }
            }

            if ((i >= order.length - 1) || (i == 0)) {
                return (null);
            } else {
                return order[--i];
            }
        }

        /*
         */

        public Component getFirstComponent(Container focusCycleRoot) {
            if (order.length == 0) {
                return (null);
            } else {
                return order[0];
            }
        }

        /*
         */

        public Component getLastComponent(Container focusCycleRoot) {
            if (order.length == 0) {
                return (null);
            } else {
                return order[order.length - 1];
            }
        }

        /*
         */

        public Component getDefaultComponent(Container focusCycleRoot) {
            return (getFirstComponent(focusCycleRoot));
        }
    }

}
