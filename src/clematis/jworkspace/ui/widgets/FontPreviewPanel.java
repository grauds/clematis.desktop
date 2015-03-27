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
import kiwi.ui.KPanel;

import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
/**
 * A simple panel that renders a font preview for {@link com.lamatek.swingextras.JFontChooser JFontChooser} component.
 */
public class FontPreviewPanel extends KPanel
{
	private Font font;
	/**
	 * Constructs a font preview panel initialized to the specified font.
	 *
	 * @param f The font used to render the preview
	 */
	public FontPreviewPanel(Font f)
    {
		super();
        setOpaque( false );
		setFont(f);
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Preview"));
	}
	/**
	 * Sets the font used to render the preview text.
	 *
	 * @param f The font used to render the preview
	 */
	public void setFont(Font f)
    {
		this.font = f;
		repaint();
	}
	public void update(Graphics g)
    {
		paintComponent(g);
		paintBorder(g);
	}
	public void paintComponent(Graphics g)
    {
        super.paintComponent( g );
		Image osi = createImage(getSize().width, getSize().height);
		Graphics osg = osi.getGraphics();
		osg.setFont(this.font);
		Rectangle2D bounds = font.getStringBounds(font.getFontName(), 0, font.getFontName().length(), new FontRenderContext(null, true, false));
		int height = (new Double(bounds.getHeight())).intValue();
		osg.drawString(font.getFontName(), 5, (((getSize().height - height) / 2) + height));
		g.drawImage(osi, 0, 0, this);
	}

	public Dimension getPreferredSize()
    {
		return new Dimension(getSize().width, 75);
	}

	public Dimension getMinimumSize()
    {
		return getPreferredSize();
	}
}