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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.Timer;

/**
 * A trivial extension to <code>JButton</code> that performs some simple
 * customizations.
 *
 * @author Mark Lindner
 * @see javax.swing.JButton
 */

public class KButton extends JButton {

    private static final int DEFAULT_INITIAL_DELAY = 300;

    private static final int DEFAULT_REPEAT_DELAY = 60;

    private int initialDelay = DEFAULT_INITIAL_DELAY;

    private int repeatDelay = DEFAULT_REPEAT_DELAY;

    private boolean repeating = false;

    private boolean pressed = false;

    private Timer timer;

    private int modifiers;

    /**
     * Construct a new <code>KButton</code>.
     *
     * @param action The action for the button.
     * @since Kiwi 2.1.4
     */

    public KButton(Action action) {
        super(action);
        init();
    }

    /**
     * Construct a new <code>KButton</code>. A new, transparent button will be
     * created.
     *
     * @param text The text to display in the button.
     */

    public KButton(String text) {
        super(text);
        init();
    }

    /**
     * Construct a new <code>KButton</code>. A new, transparent button will be
     * created.
     *
     * @param text The text to display in the button.
     * @param icon The ICON to display in the button.
     */

    public KButton(String text, Icon icon) {
        super(text, icon);
        init();
    }

    /**
     * Construct a new <code>KButton</code>. A new, transparent button will be
     * created.
     *
     * @param icon The ICON to display in the button.
     */

    public KButton(Icon icon) {
        super(icon);
        init();
    }

    /**
     * Determine if repeating mode is enabled or disabled on the button.
     *
     * @since Kiwi 2.2
     */

    public boolean isRepeating() {
        return (repeating);
    }

    /**
     * Enable or disable repeating mode on the button. When enabled, the button
     * fires <code>ActionEvent</code>s at regular intervals while it is
     * depressed.
     *
     * @since Kiwi 2.2
     */

    public void setRepeating(boolean repeating) {
        if (!repeating) {
            pressed = false;
            if (timer.isRunning()) {
                timer.stop();
            }
        }

        this.repeating = repeating;
    }

    /**
     * Get the initial repeat delay.
     *
     * @since Kiwi 2.2
     */

    public int getInitialDelay() {
        return (initialDelay);
    }

    /**
     * Set the initial repeat delay. This delay represents the time
     * between the initial mouse press and the time of the firing of the
     * first <code>ActionEvent</code>. The default delay is 60 ms.
     *
     * @param delay The delay, in milliseconds. Negative values are silently
     *              clipped to 0.
     * @since Kiwi 2.2
     */

    public void setInitialDelay(int delay) {
        if (delay < 0) {
            this.initialDelay = 0;
        } else {
            this.initialDelay = delay;
        }
    }

    /**
     * Get the repeat delay. This delay represents the amount of time between
     * subsequent firings of <code>ActionEvent</code> when the button remains
     * pressed.
     *
     * @since Kiwi 2.2
     */

    public int getRepeatDelay() {
        return (repeatDelay);
    }

    /**
     * Set the repeat delay. The default delay is 30 ms.
     *
     * @param delay The delay, in milliseconds. Negative values are silently
     *              clipped to 0.
     * @since Kiwi 2.2
     */

    public void setRepeatDelay(int delay) {
        if (delay < 0) {
            this.repeatDelay = 0;
        } else {
            this.repeatDelay = delay;
        }
    }

    /*
     */
    private void init() {
        setOpaque(!UIChangeManager.getButtonsAreTransparent());

        @SuppressWarnings("AnonInnerLength")
        MouseListener mouseListener = new MouseListener() {
            public void mouseClicked(MouseEvent mevt) {
                pressed = false;
                if (timer.isRunning()) {
                    timer.stop();
                }
            }

            public void mousePressed(MouseEvent mevt) {
                if (isEnabled() && repeating) {
                    pressed = true;
                    if (!timer.isRunning()) {
                        modifiers = mevt.getModifiers();
                        timer.setInitialDelay(initialDelay);
                        timer.start();
                    }
                }
            }

            public void mouseReleased(MouseEvent mevt) {
                pressed = false;
                if (timer.isRunning()) {
                    timer.stop();
                }
            }

            public void mouseEntered(MouseEvent mevt) {
                if (isEnabled() && repeating) {
                    if (pressed && !timer.isRunning()) {
                        timer.setInitialDelay(repeatDelay);
                        timer.start();
                    }
                }
            }

            public void mouseExited(MouseEvent mevt) {
                if (timer.isRunning()) {
                    timer.stop();
                }
            }
        };

        addMouseListener(mouseListener);

        timer = new Timer(
            repeatDelay,
            new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    ActionEvent nevt = new ActionEvent(this,
                        ActionEvent.ACTION_PERFORMED,
                        KButton.super.getActionCommand(),
                        modifiers);
                    KButton.this.fireActionPerformed(nevt);
                }
            });
        timer.setRepeats(true);
    }

    /**
     * Enable or disable the button.
     */

    public void setEnabled(boolean enabled) {
        if (enabled != super.isEnabled()) {
            pressed = false;
            if (timer.isRunning()) {
                timer.stop();
            }
        }

        super.setEnabled(enabled);
    }

}
