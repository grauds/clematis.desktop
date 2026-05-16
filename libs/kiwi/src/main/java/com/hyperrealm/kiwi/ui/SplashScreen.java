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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.hyperrealm.kiwi.util.KiwiUtils;

/**
 * This class represents a <i>splash screen</i>: an untitled, frameless window
 * that briefly appears on the desktop, typically while an application or
 * installer program is launching. A <code>SplashScreen</code> contains an
 * image and, optionally, a one-line textual caption. It is drawn with a
 * 1-pixel wide black border and appears at the center of the screen when
 * shown. The <code>SplashScreen</code> appears above all other windows on the
 * desktop.
 * <p>
 * As with all <code>Component</code>s, the <code>setForeground()</code> and
 * <code>setBackground()</code> methods may be called to change the appearance
 * of the splash screen.
 * <p>
 * <p><center>
 * <img src="snapshot/SplashScreen.gif"><br>
 * <i>An example SplashScreen.</i>
 * </center>
 *
 * @author Mark Lindner
 */
public class SplashScreen extends JWindow {

    private static final int DEFAULT_DELAY = 30;

    private int delay = DEFAULT_DELAY;
    private Image image;
    private String caption;
    private Component customComponent;

    /**
     * Construct an image-based legacy Splash Screen.
     */
    public SplashScreen(Image image, String caption) {
        this(KiwiUtils.getPhantomFrame(), image, caption);
    }

    public SplashScreen(Frame parent, Image image, String caption) {
        super(parent);
        this.image = image;
        this.caption = caption;
        setupUI();
    }

    public SplashScreen(Component component) {
        this(KiwiUtils.getPhantomFrame(), component);
    }

    public SplashScreen(Frame parent, Component component) {
        super(parent);
        this.customComponent = component;
        setupUI();
    }

    /**
     * Uniform layout manager initializer using standard Swing content pane hooks.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private void setupUI() {
        // Replaces the custom drawRect border routine with a clean look
        getRootPane().setBorder(new LineBorder(Color.BLACK, 1));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        if (customComponent != null) {
            // Direct component injection strategy
            getContentPane().add(customComponent, BorderLayout.CENTER);
        } else {
            // Backward compatibility strategy for old Image + Caption inputs
            if (image != null) {
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                getContentPane().add(imageLabel, BorderLayout.CENTER);
            }
            if (caption != null) {
                JLabel captionLabel = new JLabel(caption, SwingConstants.CENTER);
                captionLabel.setForeground(Color.BLACK);
                // Simple empty border mimicking old pixel padding offsets
                captionLabel.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
                getContentPane().add(captionLabel, BorderLayout.SOUTH);
            }
        }
    }

    public void setDelay(int seconds) throws IllegalArgumentException {
        if (seconds < 0) {
            throw new IllegalArgumentException("Delay must be >= 0 seconds.");
        }
        delay = seconds;
    }

    @Override
    public void setVisible(boolean flag) {
        if (flag) {
            pack(); // Let layouts automatically calculate modern boundaries
            KiwiUtils.centerWindow(this);
        }
        super.setVisible(flag);

        if (flag && (delay > 0)) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep((long) delay * KiwiUtils.MILLISEC_IN_SECOND);
                } catch (InterruptedException ignored) {
                }
                dispose();
            });
            thread.start();
        }
    }
}

