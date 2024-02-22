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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import static com.hyperrealm.kiwi.ui.dialog.ComponentDialog.DEFAULT_PADDING;
import com.hyperrealm.kiwi.util.KiwiUtils;
import com.hyperrealm.kiwi.util.ResourceManager;

/**
 * An implementation of <code>UIElementViewer</code> for previewing
 * audio clips. The viewer consists of <i>Play</i> and <i>Stop</i> buttons,
 * for playing and stopping the audio clip, respectively.
 *
 * @author Mark Lindner
 */

public class AudioClipViewer extends KPanel implements UIElementViewer, ActionListener {

    private KButton bPlay, bStop;

    private KiwiAudioClip clip = null;

    /**
     * Construct a new <code>AudioClipViewer</code>.
     */

    public AudioClipViewer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, DEFAULT_PADDING, DEFAULT_PADDING));

        ResourceManager rm = KiwiUtils.getResourceManager();

        bPlay = new KButton(rm.getIcon("play_blue.png"));
        bPlay.addActionListener(this);
        add(bPlay);

        bStop = new KButton(rm.getIcon("stop_blue.png"));
        bStop.addActionListener(this);
        add(bStop);
    }

    /**
     * Get a reference to the viewer component.
     *
     * @return The viewer component.
     */

    public JComponent getViewerComponent() {
        return (this);
    }

    /**
     * Show the specified element.
     *
     * @param element An object, assumed to be an instance of
     *                <code>AudioClip</code>, to display.
     */

    public void showElement(UIElement element) {
        Object obj = element.getObject();

        if (obj instanceof KiwiAudioClip) {
            if (clip != null) {
                clip.stop();
            }

            clip = (KiwiAudioClip) obj;
        }
    }

    /**
     * This method is public as an implementation side-effect.
     */

    public void actionPerformed(ActionEvent evt) {
        if (clip == null) {
            return;
        }

        Object o = evt.getSource();

        if (o == bPlay) {
            clip.play();
        } else if (o == bStop) {
            clip.stop();
        }
    }

}
