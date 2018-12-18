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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

import static com.hyperrealm.kiwi.util.KiwiUtils.MILLISEC_IN_SECOND;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.MAXIMUM;

import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ProgressObserver;

/**
 * This class represents a status bar that includes a message area
 * and an optional progress meter. Status bars are typically placed
 * at the bottom of an application window. Status messages disappear
 * after a fixed number of seconds, unless they are specified as
 * non-expiring; the delay is adjustable.
 *
 * <p><center>
 * <img src="snapshot/StatusBar.gif"><br>
 * <i>An example StatusBar.</i>
 * </center>
 *
 * @author Mark Lindner
 */

public class StatusBar extends KPanel implements ProgressObserver,
    ActionListener {

    private static final Border SOFT_BEVEL_BORDER = new SoftBevelBorder(SoftBevelBorder.LOWERED);

    private static final Insets INSETS = new Insets(0, 2, 0, 0);

    private static final int TEN_SECONDS_IN_MILLIS = 10000;

    private JTextField label;

    private JProgressBar meter;

    private Timer timer;

    private GridBagConstraints gbc;

    /**
     * Construct a new <code>StatusBar</code>. Constructs a new status bar
     * without a progress meter.
     */

    public StatusBar() {
        this(false);
    }

    /**
     * Construct a new <code>StatusBar</code>.
     *
     * @param showMeter A flag specifying whether the status bar should include
     *                  a progress meter.
     */

    public StatusBar(boolean showMeter) {

        timer = new Timer(TEN_SECONDS_IN_MILLIS, this);

        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);

        gbc = new GridBagConstraints();
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 0;
        gbc.ipadx = 2;

        label = new JTextField();
        label.setFont(KiwiUtils.BOLD_FONT);
        label.setHighlighter(null);
        label.setEditable(false);
        label.setForeground(Color.black);
        label.setOpaque(false);
        label.setBorder(SOFT_BEVEL_BORDER);
        add(label, gbc);

        gbc.ipadx = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        gbc.insets = INSETS;

        if (showMeter) {
            meter = new JProgressBar(0, MAXIMUM);

            addStatusComponent(meter);

            // super.setOpaque(true);

            meter.setStringPainted(true);
            meter.setOpaque(true);
        }
    }

    /**
     * Remove a component from the status bar. The specified component will be
     * removed from the status bar. The text status field and the progress meter
     * (if present) cannot be removed.
     *
     * @param c The component to remove.
     * @throws IllegalArgumentException If an attempt was made to
     *                                  remove the text status field or tje progress meter.
     */

    public void removeStatusComponent(JComponent c) {
        if ((c == label) || ((meter != null) && (c == meter))) {
            throw (new IllegalArgumentException("Cannot remove label or meter."));
        }

        remove(c);
    }

    /**
     * Add a component to the status bar. The new component will be fitted with
     * a beveled SOFT_BEVEL_BORDER and made transparent to match the other components in the
     * status bar, and will be added at the right end of the status bar.
     *
     * @param c The component to add.
     */

    public void addStatusComponent(JComponent c) {
        c.setBorder(SOFT_BEVEL_BORDER);
        c.setOpaque(false);
        add(c, gbc);
    }

    /**
     * Determine if the meter slider is currently active.
     *
     * @return <code>true</code> if the slider is active, and <code>false</code>
     * otherwise.
     */

    public synchronized boolean isBusy() {
        return ((meter != null) && meter.isIndeterminate());
    }

    /**
     * Start or stop the slider. Starts or stops the progress meter's slider.
     * If this status bar was created without a progress meter, this method will
     * have no effect.
     *
     * @param flag A flag specifying whether the slider should be started or
     *             stopped.
     */

    public synchronized void setBusy(boolean flag) {
        if (meter != null) {
            meter.setIndeterminate(flag);
        }
    }

    /**
     * Set the message font. Sets the font for the text in the status bar.
     *
     * @param font The new font.
     */

    public void setFont(Font font) {
        if (label != null) {
            label.setFont(font);
        }
    }

    /**
     * Set the text color. Sets the color of the text displayed in the status
     * bar.
     *
     * @param color The text new color.
     */

    public void setTextColor(Color color) {
        label.setForeground(color);
    }

    /**
     * Set the meter color.
     *
     * @param color The new forground color for the progress meter.
     */

    public void setMeterColor(Color color) {
        meter.setForeground(color);
    }

    /**
     * Set a percentage on the meter. The <code>percent</code> value, which must
     * be between 0 and 100 inclusive, specifies how much of the meter should be
     * filled in with the foreground color.
     *
     * @param percent The percentage, a value between 0 and 100 inclusive. If
     *                the value is out of range, it is clipped.
     */

    public void setProgress(int percent) {
        meter.setValue(percent);
    }

    /**
     * Set the text to be displayed in the status bar. The status bar will be
     * cleared when the delay expires.
     *
     * @param text The text to display in the status bar.
     */

    public void setText(String text) {
        setText(text, true);
    }

    /**
     * Set the text to be displayed in the status bar.
     *
     * @param text    The text to display in the status bar.
     * @param expires A flag specifying whether the message "expires." If
     *                <code>true</code>, the status bar will be cleared after the message has
     *                been in the status bar for a specified number of seconds. The default
     *                delay is 10 seconds, but can be adjusted via the <code>setDelay()</code>
     *                method.
     * @see #setDelay
     */

    public synchronized void setText(String text, boolean expires) {

        label.setText(text == null ? "" : text);

        KiwiUtils.paintImmediately(label);

        if (expires) {
            if (timer.isRunning()) {
                timer.restart();
            } else {
                timer.start();
            }
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
        }
    }

    /**
     * Set the delay on status bar messages.
     *
     * @param seconds The number of seconds before a message disappears from the
     *                status bar.
     */

    public synchronized void setDelay(int seconds) {
        timer.setDelay(seconds * MILLISEC_IN_SECOND);
    }

    /**
     * This method is public as an implementation side-effect.
     */

    public void actionPerformed(ActionEvent evt) {
        setText(null);
    }

}
