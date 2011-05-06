package com.parallelsymmetry.escape.utility.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;

public class IconPanel extends JComponent {

	private static final long serialVersionUID = 8886637669164274796L;

	private Icon icon;

	public IconPanel( Icon icon ) {
		this.icon = icon;
		setOpaque( false );
		setBackground( Colors.CLEAR );
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension( icon.getIconWidth(), icon.getIconHeight() );
	}

	@Override
	public void paintComponent( Graphics graphics ) {
		Rectangle bounds = getBounds();

		int x = ( bounds.width - icon.getIconWidth() ) / 2;
		int y = ( bounds.height - icon.getIconHeight() ) / 2;


		graphics.setColor( getBackground() );
		graphics.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );

		graphics.setClip( bounds );
		icon.paintIcon( this, graphics, x, y );
	}

}
