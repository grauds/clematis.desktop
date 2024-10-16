package jworkspace.ui.desktop;

/* ----------------------------------------------------------------------------
   The Kiwi Toolkit
   Copyright (C) 1998-99 Mark A. Lindner

   This file is part of Kiwi.

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this library; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   frenzy@ix.netcom.com
   ----------------------------------------------------------------------------

   $Log: $
   ----------------------------------------------------------------------------
*/

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.UIManager;

import lombok.Getter;
import lombok.Setter;

/**
 * A multi-line label. This class renders a string as one or more lines,
 * breaking text on whitespace and producing a left-justified paragraph.
 * The preferred width of the column may be specified in terms of columns.
 * The resulting minimum and preferred size of the component will be equal
 * to this preferred width and the minimum height required to fully display
 * all of the text. As with all <code>JComponent</code>s, borders may be
 * applied using <code>setBorder()</code>.
 *
 * @author Mark Lindner
 * @author PING Software Group
 */
@Getter
@Setter
@SuppressWarnings("checkstyle:MagicNumber")
class DesktopIconLabel extends JLabel {
    static final int CENTER_ALIGNMENT = 0;
    private static final int LEFT_ALIGNMENT = 1;
    private static final int RIGHT_ALIGNMENT = 2;

    private final int cols;
    private int pw = -1;
    private int h;
    private int h0;
    private FontMetrics fm = null;
    private String text;
    private List<String> lines = new ArrayList<>();
    private Dimension dim = new Dimension(10, 10);
    private int alignment = DesktopIconLabel.LEFT_ALIGNMENT;
    private boolean selected = false;

    /**
     * Construct a new <code>DesktopIconLabel</code> for the given text and column
     * width.
     *
     * @param text The text to display.
     * @param cols The number of columns wide to make the label. The width of
     *             one "column" is the pixel width of the letter 'm' in the current font.
     */

    DesktopIconLabel(String text, int cols) {
        super();
        this.cols = cols;
        this.text = text;
    }

    DesktopIconLabel(String text) {
        super();
        this.cols = 1;
        this.text = text;
    }

    /* format the text */

    @SuppressWarnings("checkstyle:MagicNumber")
    private void format() {
        Font font = getFont();
        if (font == null) {
            font = new Font("Dialog", Font.PLAIN, 12);
        }
        fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        /*
         * If supplied text is narrower than maximum
         * columns - cols, shrink label horizontally.
         */
        if (fm.stringWidth(text) < fm.charWidth('m') * cols) {
            pw = fm.stringWidth(text) + 2 * fm.charWidth('m');
        } else {
            pw = fm.charWidth('m') * cols;
        }

        StringTokenizer st = new StringTokenizer(text, "\t \f\n");
        StringBuilder line = new StringBuilder();
        List<String> v = new ArrayList<>();
        int w = 0;
        int sw = fm.charWidth(' ');

        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            int ww = fm.stringWidth(word);
            int tw = ((w == 0) ? ww : ww + sw);

            if ((w + tw) > pw) {
                v.add(line.toString());
                line = new StringBuilder();
                line.append(word);
                w = ww;
            } else {
                if (w > 0) {
                    line.append(' ');
                }
                line.append(word);
                w += tw;
            }
        }

        // flush

        if (w > 0) {
            v.add(line.toString());
        }
        lines = v;

        // compute height information

        h = fm.getHeight() + fm.getLeading();
        h0 = fm.getAscent() + fm.getLeading();
        int ph = (lines.size() * h);

        Insets ins = getInsets();

        dim = new Dimension(pw + ins.left + ins.right, ph + ins.top + ins.bottom);
    }

    /**
     * Get the minimum size of the component. This will always be equal to the
     * preferred size of the component.
     */

    public Dimension getMinimumSize() {
        if (pw < 0) {
            format();
        }

        return (dim);
    }

    /**
     * Get the preferred size of the component.
     */

    public Dimension getPreferredSize() {
        return (getMinimumSize());
    }

    /**
     * Paint the component.
     */

    public void paintComponent(Graphics gc) {
        if (!isSelected()) {
            gc.setColor(getBackground());
        } else {
            gc.setColor(UIManager.getColor("textHighlight"));
            setForeground(UIManager.getColor("textHighlightText"));
        }
        gc.fillRect(0, 0, getWidth(), getHeight());

        format();
        Insets ins = getInsets();

        gc.setFont(getFont());
        gc.setColor(getForeground());

        int lineLength = 0;

        if (alignment == DesktopIconLabel.CENTER_ALIGNMENT) {
            for (int i = 0; i < lines.size(); i++) {
                for (int j = 0; j < lines.get(i).length(); j++) {
                    lineLength += fm.charWidth(lines.get(i).charAt(j));
                }

                gc.drawString(lines.get(i),
                    ins.left + (getWidth() - lineLength) / 2,
                    ins.top + h0 + (i * h));

                lineLength = 0;
            }
        } else if (alignment == DesktopIconLabel.LEFT_ALIGNMENT) {
            for (int i = 0; i < lines.size(); i++) {
                for (int j = 0; j < lines.get(i).length(); j++) {
                    lineLength += fm.charWidth(lines.get(i).charAt(j));
                }

                gc.drawString(lines.get(i),
                    getWidth() - lineLength - ins.right, ins.top + h0 + (i * h));

                lineLength = 0;
            }
        } else {
            for (int i = 0; i < lines.size(); i++) {
                for (int j = 0; j < lines.get(i).length(); j++) {
                    lineLength += fm.charWidth(lines.get(i).charAt(j));
                }

                gc.drawString(
                    lines.get(i),
                    ins.right, ins.top + h0 + (i * h)
                );

                lineLength = 0;
            }
        }
        paintBorder(gc);
    }

    /**
     * Set the text to be displayed by the label.
     *
     * @param text The new text.
     */

    public void setText(String text) {
        this.text = text;
        repaint();
    }

    /**
     * Set alignment
     *
     * @param alignment The new alignment
     */
    public void setAlignment(int alignment) {
        if (alignment != DesktopIconLabel.CENTER_ALIGNMENT && alignment != DesktopIconLabel.LEFT_ALIGNMENT
            && alignment != DesktopIconLabel.RIGHT_ALIGNMENT) {
            throw new IllegalArgumentException("Invalid alignment");
        }
        this.alignment = alignment;
    }
}