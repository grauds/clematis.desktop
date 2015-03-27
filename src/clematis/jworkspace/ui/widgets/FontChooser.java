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

   tysinsh@comail.ru
  ----------------------------------------------------------------------------
*/

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Vector;

import kiwi.ui.dialog.ComponentDialog;
import kiwi.ui.KPanel;
import jworkspace.LangResource;

public class FontChooser extends ComponentDialog
        implements ActionListener, ListSelectionListener
{
	private Font font;
	private JList fontNames, fontSizes, fontStyles;
	private JTextField currentSize;
	private Font[] availableFonts;
	private FontPreviewPanel preview;

	/**
	 * Constructs a new JFontChooser component initialized
     * to the supplied font object.
	 */
	public FontChooser(Frame parent, Font font)
    {
		super( parent, LangResource.getString("FontChooserDialog.title"), true);
		this.font = font;
        if ( font != null )
        {
		    currentSize.setText((new Integer(font.getSize())).toString());
            fontSizes.setSelectedValue((new Integer(font.getSize())).toString(), true);
            fontNames.setSelectedValue(font.getFamily(), true);
            if (font.getStyle() == Font.PLAIN)
            {
                fontStyles.setSelectedValue("Regular", false);
            }
            else if (font.getStyle() == Font.ITALIC)
            {
                fontStyles.setSelectedValue("Italic", false);
            }
            else if (font.getStyle() == Font.BOLD)
            {
                fontStyles.setSelectedValue("Bold", false);
            }
            else if (font.getStyle() == (Font.BOLD | Font.ITALIC))
            {
                fontStyles.setSelectedValue("BoldItalic", false);
            }
        }
        setResizable( false );
        centerDialog();
	}

	private void updateFont(Font f)
    {
		this.font = f;
		preview.setFont(this.font);
	}

    public void dispose()
    {
        destroy();
        super.dispose();
    }

	private void updateFontSize(int size)
    {
		updateFont(font.deriveFont((new Integer(size)).floatValue()));
	}

	private void updateFontStyle(int style)
    {
		updateFont(font.deriveFont(style));
	}

	/**
	 * Returns the currently selected font. Typically called after receipt
	 * approveSelection method (using the component option).
	 * @return java.awt.Font A font class that represents the currently selected font.
	 */
	public Font getSelectedFont()
    {
		return font;
	}

	/**
	 * Processes action events from the okay and cancel buttons
	 * as well as the current size TextField.
	 */
	public void actionPerformed(ActionEvent e)
    {
		if (e.getSource() == currentSize)
        {
			fontSizes.setSelectedValue(currentSize.getText(), true);
		}
	}

	/**
	 * Processes events received from the various JList objects.
	 */
	public void valueChanged(ListSelectionEvent e)
    {
		if (e.getSource() == fontNames)
        {
			Font f = availableFonts[fontNames.getSelectedIndex()];
			f = new Font(f.getFontName(), font.getStyle(), font.getSize());
			updateFont(f);
		}
		if (e.getSource() == fontSizes)
        {
			currentSize.setText((String) fontSizes.getSelectedValue());
			updateFontSize((new Integer(currentSize.getText())).intValue());
		}
		if (e.getSource() == fontStyles)
        {
			int style = Font.PLAIN;
			String selection = (String) fontStyles.getSelectedValue();
			if (selection.equals("Regular"))
				style = Font.PLAIN;
			if (selection.equals("Bold"))
				style = Font.BOLD;
			if (selection.equals("Italic"))
				style = Font.ITALIC;
			if (selection.equals("BoldItalic"))
				style = (Font.BOLD | Font.ITALIC);
			updateFontStyle(style);
		}
	}

    protected boolean accept()
    {
        return true;
    }

    protected void cancel()
    {
        destroy();
    }

    protected JComponent buildDialogUI()
    {
        setComment(null);
        /**
         * Holder for all dialog controls
         */
        KPanel holder = new KPanel();
        holder.setLayout(new BorderLayout(5, 5));
        /**
         * Operation environment font list
         */
		Font[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		Vector fonts = new Vector(1, 1);
		Vector names = new Vector(1, 1);
		for (int i = 0; i < fontList.length; i++)
        {
			String fontName = fontList[i].getFamily();
			if (! names.contains(fontName))
            {
				names.addElement(fontName);
				fonts.addElement(fontList[i]);
			}
		}
		availableFonts = new Font[fonts.size()];
		for (int i = 0; i < fonts.size(); i++)
        {
			availableFonts[i] = (Font) fonts.elementAt(i);
        }
        /**
         * List names
         */
		fontNames = new JList(names);
		fontNames.addListSelectionListener(this);
        /**
         * Styles list
         */
		Object[] styles = {"Regular", "Bold", "Italic", "BoldItalic"};
		fontStyles = new JList(styles);
	    fontStyles.setSelectedIndex(0);
		fontStyles.addListSelectionListener(this);
        /**
         * Sizes
         */
		String[] sizes = new String[69];
		for (int i = 3; i < 72; i++)
        {
			sizes[i - 3] = (new Integer(i + 1)).toString();
        }
		fontSizes = new JList(sizes);
		fontSizes.addListSelectionListener(this);
        /**
         *  ********* Visual controls go here **********
         */
		JScrollPane fontNamesScroll = new JScrollPane(fontNames);
        JScrollPane fontStylesScroll = new JScrollPane(fontStyles);
		JScrollPane fontSizesScroll = new JScrollPane(fontSizes);

		currentSize = new JTextField(5);
		currentSize.addActionListener(this);

        /**
         * Size panel
         */
        KPanel sizePane = new KPanel();
        sizePane.setLayout( new BorderLayout( 3, 3) );
        KPanel top = new KPanel();
        top.setLayout( new BorderLayout( 3, 3) );
        JLabel label = new JLabel(LangResource.getString("FontChooserDialog.size") + ":");
        top.add(label, BorderLayout.WEST);
        sizePane.add(currentSize, BorderLayout.SOUTH);
        sizePane.add(top, BorderLayout.NORTH);
		sizePane.add(fontSizesScroll, BorderLayout.CENTER);
        /**
         * Style panel
         */
        KPanel stylePane = new KPanel();
        stylePane.setLayout( new BorderLayout( 3, 3) );
        top = new KPanel();
        top.setLayout( new BorderLayout( 3, 3) );
        label = new JLabel(LangResource.getString("FontChooserDialog.style") + ":");
        top.add(label, BorderLayout.CENTER);
        stylePane.add(top, BorderLayout.NORTH);
		stylePane.add(fontStylesScroll, BorderLayout.CENTER);
        /**
         * Names panel
         */
        KPanel namePane = new KPanel();
        namePane.setLayout( new BorderLayout( 3, 3) );
        top = new KPanel();
        top.setLayout( new BorderLayout( 3, 3) );
        label = new JLabel(LangResource.getString("FontChooserDialog.name") + ":");
        top.add(label, BorderLayout.CENTER);
        namePane.add(top, BorderLayout.NORTH);
		namePane.add(fontNamesScroll, BorderLayout.CENTER);
        /**
         * Top panel
         */
		GridBagLayout g = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		top = new KPanel(g);
		c.anchor = c.WEST;
		c.fill = c.VERTICAL;
		c.insets = new Insets(2, 5, 2, 5);
		c.gridx = 0;
		c.gridy = 0;
		top.add( namePane );
		g.setConstraints(namePane, c);
		c.gridx++;
		top.add( stylePane );
		g.setConstraints(stylePane, c);
		c.gridx++;
		top.add( sizePane );
		g.setConstraints(sizePane, c);

        /**
         * Preview panel
         */
		preview = new FontPreviewPanel(this.font);

		holder.add("North", top);
		holder.add("Center", preview);

        return holder;
    }

    public static void main(java.lang.String[] args)
    {
        new FontChooser(new Frame(), new Font("Arial", Font.BOLD, 12)).setVisible(true);
    }
}
