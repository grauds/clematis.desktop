package jworkspace.ui.widgets;

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
  ----------------------------------------------------------------------------
*/

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import com.hyperrealm.kiwi.util.ResourceLoader;

import jworkspace.WorkspaceResourceAnchor;
import jworkspace.kernel.Workspace;

/**
 * jEdit's HTML viewer. It uses a Swing JEditorPane to display the HTML,
 * and implements a URL history.
 *
 * @author Slava Pestov
 * @version $Id: HelpViewer.java,v 1.1.1.1 2001/12/20 10:01:53 tysinsh Exp $
 */
@SuppressWarnings("MagicNumber")
public class HelpViewer extends JPanel {

    private static final String FILE_PROTOCOL = "file:";

    private static final String CANNOT_OPEN_LOCATION_MESSAGE = "Cannot open location: ";

    private static final String HELP_BROWSER_TITLE = "Help Browser";

    // private members
    private JButton back;

    private JButton forward;

    private JEditorPane viewer;

    private JTextField urlField;

    private URL[] history;

    private int historyPos;

    /**
     * Creates a new viewer for the specified URL.
     *
     * @param url The URL
     */
    private HelpViewer(URL url) {
        super();
        setLayout(new BorderLayout());
        history = new URL[25];

        ActionHandler actionListener = new ActionHandler();

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);

        JLabel label = new JLabel("Resource");
        label.setBorder(new EmptyBorder(0, 0, 0, 12));
        toolBar.add(label);
        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(Box.createGlue());
        urlField = new JTextField();
        urlField.addKeyListener(new KeyHandler());
        Dimension dim = urlField.getPreferredSize();
        dim.width = Integer.MAX_VALUE;
        urlField.setMaximumSize(dim);
        box.add(urlField);
        box.add(Box.createGlue());
        toolBar.add(box);

        toolBar.add(Box.createHorizontalStrut(6));

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setBorder(new EmptyBorder(0, 12, 0, 0));

        back = new JButton(new ImageIcon(new ResourceLoader(WorkspaceResourceAnchor.class)
            .getResourceAsImage("images/cpanel/normal/back.gif")));
        back.setToolTipText("Back");
        back.addActionListener(actionListener);
        back.setRequestFocusEnabled(false);
        toolBar.add(back);

        forward = new JButton(new ImageIcon(new ResourceLoader(WorkspaceResourceAnchor.class)
            .getResourceAsImage("images/cpanel/normal/forward.gif")));
        forward.addActionListener(actionListener);
        forward.setToolTipText("Forward");
        forward.setRequestFocusEnabled(false);
        toolBar.add(forward);

        back.setPreferredSize(forward.getPreferredSize());

        add(BorderLayout.NORTH, toolBar);

        viewer = new JEditorPane();
        viewer.setEditable(false);
        viewer.setFont(new Font("Monospaced", Font.PLAIN, 12));
        viewer.addHyperlinkListener(new LinkHandler());
        add(BorderLayout.CENTER, new JScrollPane(viewer));

        gotoURL(url, true);
    }

    /**
     * Goes to the specified file, relative to the jEdit documentation
     * directory.
     *
     * @param file The file
     * @since jEdit 2.7pre2
     */
    public static void gotoFile(String file) {
        try {
            gotoURL(new URL(FILE_PROTOCOL + file));
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(Workspace.getUi().getFrame(),
                CANNOT_OPEN_LOCATION_MESSAGE + FILE_PROTOCOL + file,
                HELP_BROWSER_TITLE,
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Goes to the specified URL, creating a new viewer or
     * reusing an existing one as necessary.
     *
     * @param url The URL
     * @since jEdit 2.2final
     */
    public static void gotoURL(URL url) {
        JFrame frame = new JFrame(HELP_BROWSER_TITLE);
        frame.getContentPane().add(new HelpViewer(url));
        frame.setBounds(50, 50, 400, 300);
        frame.setVisible(true);
    }

    /**
     * Displays the specified URL in the HTML component.
     *
     * @param url          The URL
     * @param addToHistory Should the URL be added to the back/forward
     *                     history?
     */
    public void gotoURL(URL url, boolean addToHistory) {
        // reset default cursor so that the hand cursor doesn't
        // stick around
        viewer.setCursor(Cursor.getDefaultCursor());

        try {
            urlField.setText(url.toString());
            viewer.setPage(url);
            if (addToHistory) {
                history[historyPos] = url;
                if (historyPos + 1 == history.length) {
                    System.arraycopy(history, 1, history,
                        0, history.length - 1);
                    history[historyPos] = null;
                } else {
                    historyPos++;
                }
            }
        } catch (IOException io) {
            JOptionPane.showMessageDialog(this, CANNOT_OPEN_LOCATION_MESSAGE
                + url, HELP_BROWSER_TITLE, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class ActionHandler implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
            if (source == back) {
                if (historyPos <= 1) {
                    getToolkit().beep();
                } else {
                    URL url = history[--historyPos - 1];
                    gotoURL(url, false);
                }
            } else if (source == forward) {
                if (history.length - historyPos <= 1) {
                    getToolkit().beep();
                    return;
                }

                URL url = history[historyPos];
                if (url == null) {
                    getToolkit().beep();
                } else {
                    historyPos++;
                    gotoURL(url, false);
                }
            }
        }
    }

    class LinkHandler implements HyperlinkListener {

        public void hyperlinkUpdate(HyperlinkEvent evt) {

            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (evt instanceof HTMLFrameHyperlinkEvent) {
                    ((HTMLDocument) viewer.getDocument())
                        .processHTMLFrameHyperlinkEvent(
                            (HTMLFrameHyperlinkEvent) evt);
                } else if (evt.getURL() != null) {
                    gotoURL(evt.getURL(), true);
                }
            } else if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                viewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
                viewer.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    class KeyHandler extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                try {
                    gotoURL(new URL(urlField.getText()), true);
                } catch (MalformedURLException mu) {
                    // show error page to user?
                }
            }
        }
    }
}
