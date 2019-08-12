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

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.hyperrealm.kiwi.ui.KPanel;
import com.hyperrealm.kiwi.ui.dialog.ComponentDialog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jworkspace.WorkspaceResourceAnchor;

/**
 * @author Anton Troshin
 */
@SuppressFBWarnings("UR_UNINIT_READ")
public class FontChooser extends ComponentDialog implements ActionListener, ListSelectionListener {

    private static final String REGULAR_FONT_FACE = "Regular";
    private static final String ITALIC_FONT_FACE = "Italic";
    private static final String BOLD_FONT_FACE = "Bold";
    private static final String BOLD_ITALIC_FONT_FACE = "BoldItalic";
    private Font font;

    private JList<String> fontNames, fontSizes, fontStyles;

    private JTextField currentSize;

    private Font[] availableFonts;

    private FontPreviewPanel preview;

    /**
     * Constructs a new JFontChooser component initialized to the supplied font object.
     */
    public FontChooser(Frame parent, Font font) {
        super(parent, WorkspaceResourceAnchor.getString("FontChooserDialog.title"), true);
        this.font = font;
        if (font != null) {
            String fontSize = Integer.toString(font.getSize());
            currentSize.setText(fontSize);
            fontSizes.setSelectedValue(fontSize, true);
            fontNames.setSelectedValue(font.getFamily(), true);
            if (font.getStyle() == Font.PLAIN) {
                fontStyles.setSelectedValue(REGULAR_FONT_FACE, false);
            } else if (font.getStyle() == Font.ITALIC) {
                fontStyles.setSelectedValue(ITALIC_FONT_FACE, false);
            } else if (font.getStyle() == Font.BOLD) {
                fontStyles.setSelectedValue(BOLD_FONT_FACE, false);
            } else if (font.getStyle() == (Font.BOLD | Font.ITALIC)) {
                fontStyles.setSelectedValue(BOLD_ITALIC_FONT_FACE, false);
            }
        }
        setResizable(false);
    }

    private void updateFont(Font f) {
        this.font = f;
        preview.setFont(this.font);
    }

    public void dispose() {
        destroy();
        super.dispose();
    }

    private void updateFontSize(int size) {
        updateFont(font.deriveFont((float) size));
    }

    private void updateFontStyle(int style) {
        updateFont(font.deriveFont(style));
    }

    /**
     * Returns the currently selected font. Typically called after receipt
     * approveSelection method (using the component option).
     *
     * @return java.awt.Font A font class that represents the currently selected font.
     */
    public Font getSelectedFont() {
        return font;
    }

    /**
     * Processes action events from the okay and cancel buttons
     * as well as the current size TextField.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == currentSize) {
            fontSizes.setSelectedValue(currentSize.getText(), true);
        }
    }

    /**
     * Processes events received from the various JList objects.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == fontNames) {
            Font f = availableFonts[fontNames.getSelectedIndex()];
            f = new Font(f.getFontName(), font.getStyle(), font.getSize());
            updateFont(f);
        }
        if (e.getSource() == fontSizes) {
            currentSize.setText(fontSizes.getSelectedValue());
            updateFontSize(Integer.parseInt(currentSize.getText()));
        }
        if (e.getSource() == fontStyles) {
            int style = Font.PLAIN;
            String selection = fontStyles.getSelectedValue();
            if (selection.equals(BOLD_FONT_FACE)) {
                style = Font.BOLD;
            }
            if (selection.equals(ITALIC_FONT_FACE)) {
                style = Font.ITALIC;
            }
            if (selection.equals(BOLD_ITALIC_FONT_FACE)) {
                style = (Font.BOLD | Font.ITALIC);
            }
            updateFontStyle(style);
        }
    }

    protected boolean accept() {
        return true;
    }

    protected void cancel() {
        destroy();
    }

    @SuppressWarnings("MagicNumber")
    protected JComponent buildDialogUI() {
        setComment(null);
        /*
         * Holder for all dialog controls
         */
        KPanel holder = new KPanel();
        holder.setLayout(new BorderLayout(5, 5));
        /*
         * Operation environment font list
         */
        Font[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        List<Font> fonts = new Vector<>(1, 1);
        Vector<String> names = new Vector<>(1, 1);
        for (Font value : fontList) {
            String fontName = value.getFamily();
            if (!names.contains(fontName)) {
                names.add(fontName);
                fonts.add(value);
            }
        }
        availableFonts = new Font[fonts.size()];
        for (int i = 0; i < fonts.size(); i++) {
            availableFonts[i] = fonts.get(i);
        }
        /*
         * List names (must be a vector)
         */
        fontNames = new JList<>(names);
        fontNames.addListSelectionListener(this);
        /*
         * Styles list
         */
        String[] styles = {REGULAR_FONT_FACE, BOLD_FONT_FACE, ITALIC_FONT_FACE, BOLD_ITALIC_FONT_FACE};
        fontStyles = new JList<>(styles);
        fontStyles.setSelectedIndex(0);
        fontStyles.addListSelectionListener(this);
        /*
         * Sizes
         */
        String[] sizes = new String[69];
        for (int i = 3; i < 72; i++) {
            sizes[i - 3] = Integer.toString(i + 1);
        }
        fontSizes = new JList<>(sizes);
        fontSizes.addListSelectionListener(this);
        /*
         *  ********* Visual controls go here **********
         */
        JScrollPane fontNamesScroll = new JScrollPane(fontNames);
        JScrollPane fontStylesScroll = new JScrollPane(fontStyles);
        JScrollPane fontSizesScroll = new JScrollPane(fontSizes);

        currentSize = new JTextField(5);
        currentSize.addActionListener(this);

        /*
         * Size panel
         */
        KPanel sizePane = new KPanel();
        sizePane.setLayout(new BorderLayout(3, 3));
        KPanel top = new KPanel();
        top.setLayout(new BorderLayout(3, 3));
        JLabel label = new JLabel(WorkspaceResourceAnchor.getString("FontChooserDialog.size"));
        top.add(label, BorderLayout.WEST);
        sizePane.add(currentSize, BorderLayout.SOUTH);
        sizePane.add(top, BorderLayout.NORTH);
        sizePane.add(fontSizesScroll, BorderLayout.CENTER);
        /*
         * Style panel
         */
        KPanel stylePane = new KPanel();
        stylePane.setLayout(new BorderLayout(3, 3));
        top = new KPanel();
        top.setLayout(new BorderLayout(3, 3));
        label = new JLabel(WorkspaceResourceAnchor.getString("FontChooserDialog.style"));
        top.add(label, BorderLayout.CENTER);
        stylePane.add(top, BorderLayout.NORTH);
        stylePane.add(fontStylesScroll, BorderLayout.CENTER);
        /*
         * Names panel
         */
        KPanel namePane = new KPanel();
        namePane.setLayout(new BorderLayout(3, 3));
        top = new KPanel();
        top.setLayout(new BorderLayout(3, 3));
        label = new JLabel(WorkspaceResourceAnchor.getString("FontChooserDialog.name"));
        top.add(label, BorderLayout.CENTER);
        namePane.add(top, BorderLayout.NORTH);
        namePane.add(fontNamesScroll, BorderLayout.CENTER);
        /*
         * Top panel
         */
        GridBagLayout g = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        top = new KPanel(g);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.VERTICAL;
        c.insets = new Insets(2, 5, 2, 5);
        c.gridx = 0;
        c.gridy = 0;
        top.add(namePane);
        g.setConstraints(namePane, c);
        c.gridx++;
        top.add(stylePane);
        g.setConstraints(stylePane, c);
        c.gridx++;
        top.add(sizePane);
        g.setConstraints(sizePane, c);

        /*
         * Preview panel
         */
        preview = new FontPreviewPanel(this.font);

        holder.add("North", top);
        holder.add("Center", preview);

        return holder;
    }
}
