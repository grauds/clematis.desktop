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

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hyperrealm.kiwi.event.ChangeSupport;
import com.hyperrealm.kiwi.util.Config;

/**
 * This class serves as a factory of <code>WizardPanel</code>s for a
 * <code>WizardView</code>. A <code>WizardPanelSequence</code> maintains a set
 * of <code>WizardPanel</code>s and a <code>Config</code> object. When a
 * <code>WizardPanel</code> is added to the sequence, a reference to the
 * <code>Config</code> object is passed to the panel. The panel may use this
 * <code>Config</code> object to store results from user input or to look up
 * the current or default values for its input fields.
 * <p>
 * Whenever the value of a property is changed or a property is added to or
 * removed from  the <code>Config</code> object, the <code>Config</code>
 * object notifies the <code>WizardPanelSequence</code> via a
 * <code>ChangeEvent</code>. Subclassers may override the
 * <code>stateChanged()</code> method to handle these events.
 *
 * @author Mark Lindner
 * @see com.hyperrealm.kiwi.ui.WizardView
 * @see com.hyperrealm.kiwi.ui.WizardPanel
 * @see com.hyperrealm.kiwi.util.Config
 * @see javax.swing.event.ChangeEvent
 */

public class WizardPanelSequence implements ChangeListener {
    /**
     * The list of <code>WizardPanel</code>s.
     */
    protected ArrayList<WizardPanel> panels;
    /**
     * The index of the currently-selected <code>WizardPanel</code>.
     */
    protected int currentIndex = -1;
    /**
     * The configuration object for this sequence.
     */
    protected Config config;
    private ChangeSupport support;

    /**
     * Construct a new <code>WizardPanelSequence</code>.
     */

    public WizardPanelSequence() {
        this(new Config());
    }

    /**
     * Construct a new <code>WizardPanelSequence</code> with the given
     * configuration object.
     *
     * @param config The configuration object to use as a datasource.
     */

    public WizardPanelSequence(Config config) {
        panels = new ArrayList<>();
        support = new ChangeSupport(this);
        this.config = config;
        config.addChangeListener(this);
    }

    /**
     * Add a <code>WizardPanel</code> to this sequence. All panels should be
     * added before the sequence is used to construct a <code>WizardView</code>.
     *
     * @param panel The panel to add.
     * @see #addPanels
     */

    public final void addPanel(WizardPanel panel) {
        panels.add(panel);
        panel.addChangeListener(this);
        panel.setConfig(config);
    }

    /**
     * Add an array of <code>WizardPanel</code>s to this sequence. All panels
     * should be added before the sequence is used to construct a
     * <code>WizardView</code>.
     *
     * @param panels The panels to add.
     * @see #addPanel
     */

    public final void addPanels(WizardPanel[] panels) {
        for (WizardPanel panel : panels) {
            addPanel(panel);
        }
    }

    /**
     * Reset the sequence. Resets the sequence so that the first panel in the
     * sequence becomes the current panel; thus the next call to
     * <code>getNextPanel()</code> will return the first panel in the sequence.
     *
     * @see #getNextPanel
     */

    public void reset() {
        currentIndex = -1;
    }

    /**
     * Get a reference to this <code>WizardPanelSequence</code>'s
     * <code>Config</code> object.
     *
     * @return The <code>Config</code> object.
     */

    public final Config getConfig() {
        return (config);
    }

    /**
     * Get the currently displayed panel from this sequence.
     *
     * @return The current panel.
     */

    public WizardPanel getCurrentPanel() {
        return ((currentIndex < 0) ? null : panels.get(currentIndex));
    }

    /**
     * Get the next panel from this sequence. The default implementation
     * returns the next panel from the <code>panels</code> vector and
     * increments the <code>currentIndex</code> variable only if the end of
     * the sequence has not been reached <i>and</i> the next panel is
     * reachable (e.g., if <code>canMoveForward()</code> returns
     * <cod>true</code>).
     *
     * @return The next panel to be displayed in the <code>WizardView</code>.
     */

    public WizardPanel getNextPanel() {
        if (isLastPanel()) {
            return (null);
        }

        return (panels.get(++currentIndex));
    }

    /**
     * Get the previous panel from this sequence. The default implementation
     * returns the previous panel from the <code>panels</code> vector and
     * decrements the <code>currentIndex</code> variable only if the beginning
     * of the sequence has not been reached <i>and</i> the previous panel is
     * reachable (e.g., if <code>canMoveBackward()</code> returns
     * <code>true</code>).
     *
     * @return The previous panel to be displayed in the
     * <code>WizardView</code>.
     */

    public WizardPanel getPreviousPanel() {
        if (currentIndex == 0) {
            return (null);
        }

//    if(!canMoveBackward()) return(null);

        return (panels.get(--currentIndex));
    }

    /**
     * Determine if the current panel is the last panel in the sequence. Once
     * the last panel in a sequence has been reached, th
     * <code>WizardView</code> changes its <i>Next</i> button to a
     * <i>Finish</i> button.
     *
     * @return <code>true</code> if the current panel is the last panel, and
     * <code>false</code> otherwise.
     */

    public boolean isLastPanel() {
        return (currentIndex == (panels.size() - 1));
    }

    /**
     * Determine if the user is allowed to move to the next panel. The
     * default implementation returns <code>true</code> if the current panel
     * is not the last panel in the sequence <i>and</i> the
     * <code>canMoveForward()</code> method of the current panel
     * returns <code>true</code>. Subclassers may wish to override this
     * method to allow movement forward only if certain conditions are
     * met.
     *
     * @return <code>true</code> if the previous panel is reachable, and
     * <code>false</code> otherwise.
     */

    public boolean canMoveForward() {
        if (currentIndex > panels.size() - 1) {
            return (false);
        }

        WizardPanel p = getCurrentPanel();
        return ((p != null) && p.canMoveForward());
    }

    /**
     * Determine if the user is allowed to move to the previous
     * panel. The default implementation returns <code>true</code> if the
     * current panel is not the first panel in the sequence <i>and</i>
     * the <code>canMoveBackward()</code> method of the current panel
     * returns <code>true</code>. Subclassers may wish to override this
     * method to allow movement backward only if certain conditions are
     * met.
     */

    public boolean canMoveBackward() {
        if (currentIndex <= 0) {
            return (false);
        }

        WizardPanel p = getCurrentPanel();
        return ((p != null) && p.canMoveBackward());
    }

    /**
     * Handle <code>ChangeEvent</code>s fired by the <code>WizardPanel</code>s
     * that belong to this sequence. The default implementation does nothing;
     * subclassers may wish to add logic to determine (based on the current
     * values of properties in the <code>Config</code> object) whether movement
     * to the next or previous panel is allowed, or to determine which panel
     * will be displayed next, for example.
     *
     * @param evt The event. The source of the event is the
     *            <code>WizardPanel</code> that fired it.
     * @see java.util.EventObject#getSource
     */

    public void stateChanged(ChangeEvent evt) {
        // handle change events here

        fireChangeEvent();
    }

    /**
     * Add a <code>ChangeListener</code> to this object's list of listeners.
     *
     * @param listener The listener to add.
     */

    public void addChangeListener(ChangeListener listener) {
        support.addChangeListener(listener);
    }

    /**
     * Remove a <code>ChangeListener</code> from this object's list of
     * listeners.
     *
     * @param listener The listener to remove.
     */

    public void removeChangeListener(ChangeListener listener) {
        support.removeChangeListener(listener);
    }

    /**
     * Fire a change event. Notify listeners (typically only the
     * <code>WizardView</code> that owns this sequence) that the state of this
     * <code>WizardPanelSequence</code> has changed. Subclassers may wish to
     * call this method from the body of the <code>stateChanged()</code>
     * method if a change in the <code>Config</code> object has changed the
     * state of this <code>WizardPanelSequence</code> in a way that will
     * affect the appearance of the <code>WizardView</code>.
     */

    protected void fireChangeEvent() {
        support.fireChangeEvent();
    }

    /**
     * Dispose of this object.
     */

    public void dispose() {
        config.removeChangeListener(this);
        config = null;
    }

}
