package jworkspace.ui.logging;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 1999-2003 Anton Troshin

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

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.swing.JTextArea;

import com.hyperrealm.kiwi.ui.KFrame;

/**
 * Stream window
 * This is taken from book of Gregory M. Travis "JDK 1.4 tutorial"
 *
 * @author <a href='mailto:anton.troshin@gmail.com'>Anton Troshin</a>
 * @version 1.0
 */
public class StreamWindow extends KFrame {
    // The text area in which we display incoming text
    private JTextArea textArea;
    // Data written to this stream is appended to the
    // text area
    private StreamWindowStream out;

    /**
     * Create a new StreamWindow -- set up the interface
     * and install listeners. Make the window visible
     * after everything else is done
     */
    StreamWindow(String name) {
        super(name);
        out = new StreamWindowStream();
        setupGUI();
    }

    /**
     * Add the text area to the window, and set the window size
     */
    @SuppressWarnings("MagicNumber")
    private void setupGUI() {
        Container cp = getContentPane();
        textArea = new JTextArea();
        textArea.setEditable(false);
        cp.setLayout(new BorderLayout());
        cp.add(textArea, BorderLayout.CENTER);
        setLocation(100, 100);
        setSize(100, 100);
    }

    /**
     * Return the output stream that is connected
     * to this window
     */
    public OutputStream getOutputStream() {
        return out;
    }

    /**
     * Close the window, and dispose of it
     */
    public void dispose() {
        setVisible(false);
        super.dispose();
    }

    /**
     * Add text to the end of the text showing in the
     * text area
     */
    private void appendText(String string) {
        textArea.append(string);
    }

    /**
     * Inner class: an output stream. Writing to
     * this stream sends the data to the window
     */
    class StreamWindowStream extends OutputStream {
        // This is used to write a single byte. We
        // pre-allocate it to save time
        private byte[] tinyBuffer = new byte[1];

        /**
         * Closing the stream closes the window
         */
        public void close() {
            dispose();
        }

        /**
         * Write a single byte
         */
        public void write(int b) {
            // Store the single byte in the array and
            // write the array
            tinyBuffer[0] = (byte) b;
            write(tinyBuffer);
        }

        /**
         * Write an array of bytes
         */
        public void write(byte[] b) {
            // Convert the bytes to a string and append
            String s = new String(b, StandardCharsets.UTF_8);
            appendText(s);
        }

        /**
         * Write a sub-array of bytes
         */
        public void write(byte[] b, int off, int len) {
            // Convert the bytes to a string and append
            String s = new String(b, off, len, StandardCharsets.UTF_8);
            appendText(s);
        }
    }
}