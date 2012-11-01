package com.parallelsymmetry.escape.utility.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = -8100292323713526024L;

	private Image image;

	public ImagePanel( Image image ) {
		this.image = image;
		setOpaque( false );
		setBackground( Colors.CLEAR );
	}

	public ImagePanel( BaseImage image ) {
		this( image.getImage() );
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension( image.getWidth( this ), image.getHeight( this ) );
	}

	@Override
	public void paintComponent( Graphics graphics ) {
		Rectangle bounds = getBounds();

		int x = ( bounds.width - image.getWidth( this ) ) / 2;
		int y = ( bounds.height - image.getHeight( this ) ) / 2;

		graphics.setColor( getBackground() );
		graphics.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );

		graphics.setClip( bounds );
		graphics.drawImage( image, x, y, this );
	}

}
