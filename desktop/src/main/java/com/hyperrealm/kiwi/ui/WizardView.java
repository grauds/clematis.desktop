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

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.CENTER_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_BORDER_LAYOUT;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.SOUTH_POSITION;
import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.WEST_POSITION;

import com.hyperrealm.kiwi.event.ActionSupport;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.LocaleData;
import com.hyperrealm.kiwi.util.LocaleManager;

/**
 * A wizard-style component. <code>WizardView</code> essentially displays a
 * sequence of panels (or "cards") to the user;  each panel typically
 * contains messages and/or input elements. A <code>WizardPanelSequence</code>
 * object functions as the source of these panels, and determines the order
 * in which the panels are presented to the user, and the conditions under
 * which forward and backward movement is allowed between consecutive panels.
 * <p>
 * This component is arranged as follows. The leftmost portion of the
 * component is used to display an image (which for best results should be
 * transparent). Animated GIFs are acceptable. The bottom portion of the
 * component displays the <i>Back</i>, <i>Next</i>, and <i>Cancel</i>
 * buttons. The remaining space is occupied by the current
 * <code>WizardPanel</code> provided by the <code>WizardPanelSequence</code>
 * object.
 * <p>
 * The <code>WizardPanelSequence</code> determines when the user may move to
 * the next or previous panel. Whenever these conditions change, the
 * <code>WizardPanelSequence</code> fires a <code>ChangeEvent</code> to
 * notify the <code>WizardView</code>, which responds by dimming or
 * undimming the <i>Next</i> and <i>Back</i> buttons, as appropriate. When
 * the final panel in the sequence is reached, the <i>Next</i> button changes
 * to a <i>Finish</i> button.
 * <p>
 * If the <i>Cancel</i> button is pressed, an <code>ActionEvent</code> is
 * fired with an action command of "cancel". If the <i>Finish</i> button is
 * pressed, an <code>ActionEvent</code> is fired with an action command of
 * "finish".
 *
 * <p><center>
 * <img src="snapshot/WizardView.gif"><br>
 * <i>An example WizardView.</i>
 * </center>
 *
 * @author Mark Lindner
 * @see javax.swing.event.ChangeEvent
 * @see com.hyperrealm.kiwi.ui.WizardPanelSequence
 */

public abstract class WizardView extends KPanel {

    private static final Insets DEFAULT_INSETS = new Insets(1, 5, 1, 5);

    private KButton bPrev, bNext, bCancel;

    private KLabel iconLabel;

    private WizardPanelSequence sequence;

    private KPanel content;

    private WizardPanel curPanel = null;

    private Icon iNext;

    private boolean finish = false;

    private ActionSupport support;

    private String sNext;

    private String sFinish;

    private ButtonPanel bButtons;

    /**
     * Construct a new <code>WizardView</code>. The <code>buildSequence()</code>
     * method will be called to construct the <code>WizardPanel</code> sequence.
     *
     * @since Kiwi 2.0
     */

    public WizardView() {
        this(null);
    }

    /**
     * Construct a new <code>WizardView</code>.
     *
     * @param sequence The <code>WizardPanelSequence</code> that will provide
     *                 <code>WizardPanel</code>s for the wizard.
     */

    public WizardView(WizardPanelSequence sequence) {

        WizardPanelSequence sequenceInt = sequence;

        if (sequenceInt == null) {
            sequenceInt = buildSequence();
        }

        this.sequence = sequenceInt;

        sequenceInt.addChangeListener(evt -> refresh());

        LocaleData loc = LocaleManager.getDefault().getLocaleData("KiwiDialogs");

        sNext = loc.getMessage("kiwi.button.next");
        String sBack = loc.getMessage("kiwi.button.back");
        sFinish = loc.getMessage("kiwi.button.finish");
        String sCancel = loc.getMessage("kiwi.button.cancel");

        support = new ActionSupport(this);

        setLayout(DEFAULT_BORDER_LAYOUT);

        content = new KPanel();
        content.setLayout(new GridLayout(1, 0));

        iconLabel = new KLabel(KiwiUtils.getResourceManager().getIcon("wizard_panel.png"));
        add(WEST_POSITION, iconLabel);

        add(CENTER_POSITION, content);


        bButtons = new ButtonPanel();

        ActionListener actionListener = new ActionListener();

        bPrev = new KButton(sBack, KiwiUtils.getResourceManager().getIcon("wizard_left.png"));
        bPrev.setFocusPainted(false);
        bPrev.addActionListener(actionListener);
        bPrev.setEnabled(false);
        bPrev.setMargin(DEFAULT_INSETS);
        bButtons.addButton(bPrev);

        iNext = KiwiUtils.getResourceManager().getIcon("wizard_right.png");

        bNext = new KButton("");
        bNext.setHorizontalTextPosition(SwingConstants.LEFT);
        bNext.setFocusPainted(false);
        bNext.addActionListener(actionListener);
        bNext.setMargin(DEFAULT_INSETS);
        bButtons.addButton(bNext);

        bCancel = new KButton(sCancel);
        bCancel.setFocusPainted(false);
        bCancel.addActionListener(actionListener);
        bCancel.setMargin(DEFAULT_INSETS);
        bButtons.addButton(bCancel);

        KPanel pBottom = new KPanel();
        pBottom.setLayout(DEFAULT_BORDER_LAYOUT);

        pBottom.add(CENTER_POSITION, new JSeparator());
        pBottom.add(SOUTH_POSITION, bButtons);

        add(SOUTH_POSITION, pBottom);

        reset();
    }

    /**
     * A method for constructing the wizard panel sequence. This method may be
     * overridden by subclasses to provide the sequence for the view instead of
     * having it supplied via the constructor. The default implementation returns
     * <b>null</b>.
     *
     * @since Kiwi 2.0
     */

    abstract WizardPanelSequence buildSequence();

    /**
     * Get a reference to the <i>Cancel</i> button.
     *
     * @return The <i>Cancel</i> button.
     */

    public JButton getCancelButton() {
        return (bCancel);
    }

    /**
     * Get a reference to the <i>Finish</i> button.
     *
     * @return The <i>Finish</i> button.
     */

    public JButton getFinishButton() {
        return (bNext);
    }

    /**
     * Add a button to the <code>WizardView</code> at the specified position.
     *
     * @param button The button to add.
     * @param pos    The position at which to add the button. The value 0 denotes
     *               the first position, and -1 denotes the last position.
     * @throws IllegalArgumentException If the value of
     *                                  <code>pos</code> is invalid.
     */

    public void addButton(JButton button, int pos)
        throws IllegalArgumentException {
        bButtons.addButton(button, pos);
    }

    /**
     * Remove a button from the specified position in the
     * <code>ButtonPanel</code>.
     *
     * @param pos The position of the button to remove, where 0 denotes the
     *            first position.
     * @throws IllegalArgumentException If an attempt is made
     *                                  to remove one of the predefined wizard buttons.
     */

    public void removeButton(int pos) throws IllegalArgumentException {
        JButton b = (JButton) bButtons.getButton(pos);
        if ((b == bCancel) || (b == bPrev) || (b == bNext)) {
            throw (new IllegalArgumentException("Can't remove predefined buttons."));
        } else {
            bButtons.removeButton(pos);
        }
    }

    /**
     * Set the component's icon. Animated and/or transparent GIF images add a
     * professional touch when used with <code>WizardView</code>s.
     *
     * @param icon The new icon to use, or <code>null</code> if no icon is
     *             needed.
     */

    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
    }

    /* show a panel */

    private void showPanel(WizardPanel panel) {
        if (curPanel != null) {
            content.remove(curPanel);
            curPanel.syncData();
        }
        curPanel = panel;

        content.add(curPanel);
        content.validate();
        content.repaint();
        curPanel.syncUI();
        refresh();
        curPanel.beginFocus();
    }

    /**
     * Reset the <code>WizardView</code>. Resets the component so that the first
     * panel is displayed. This method also calls the
     * <code>WizardPanelSequence</code>'s <code>reset()</code> method.
     *
     * @see com.hyperrealm.kiwi.ui.WizardPanelSequence#reset
     */

    public void reset() {
        sequence.reset();
        bNext.setText(sNext);
        bNext.setIcon(iNext);
        showPanel(sequence.getNextPanel());
    }

    /* refresh the buttons based on what the sequence tells us */

    private void refresh() {
        finish = sequence.isLastPanel();
        bNext.setEnabled(sequence.canMoveForward());
        bPrev.setEnabled(sequence.canMoveBackward());

        if (!finish) {
            bNext.setText(sNext);
            bNext.setIcon(iNext);
        } else {
            bNext.setText(sFinish);
            bNext.setIcon(null);
        }
    }

    /**
     * Add an <code>ActionListener</code> to this component's list of listeners.
     *
     * @param listener The listener to add.
     */

    public void addActionListener(java.awt.event.ActionListener listener) {
        support.addActionListener(listener);
    }

    /**
     * Add an <code>ActionListener</code> to this component's list of listeners.
     *
     * @param listener The listener to add.
     */

    public void removeActionListener(java.awt.event.ActionListener listener) {
        support.removeActionListener(listener);
    }

    /* Handle events. */
    @SuppressWarnings("all")
    private class ActionListener implements java.awt.event.ActionListener {
        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            if (o == bNext) {
                if (curPanel.accept()) {
                    if (finish) {
                        curPanel.syncData();
                        support.fireActionEvent("finish");
                    } else {
                        showPanel(sequence.getNextPanel());
                    }
                } else {
                    curPanel.beginFocus();
                }
            } else if (o == bPrev) {
                showPanel(sequence.getPreviousPanel());
            } else if (o == bCancel) {
                support.fireActionEvent("cancel");
            }
        }
    }

}
