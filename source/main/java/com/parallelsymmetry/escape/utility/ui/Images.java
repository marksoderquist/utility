package com.parallelsymmetry.escape.utility.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RGBImageFilter;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Images {

	public static void show( Image image ) {
		show( image, Color.WHITE );
	}

	public static void show( Image image, int size ) {
		show( image, Color.WHITE, size );
	}

	public static void show( Image image, Color color ) {
		show( image, color, 5 );
	}

	public static void show( Image image, Color color, int size ) {
		JPanel border = new JPanel();
		border.setBackground( color );
		border.setBorder( new LineBorder( color, size ) );
		border.setLayout( new BorderLayout() );

		ImagePanel panel = new ImagePanel( image );
		border.add( panel, BorderLayout.CENTER );
		JOptionPane.showMessageDialog( null, border, null, JOptionPane.PLAIN_MESSAGE );
	}

	public static final BufferedImage filter( BufferedImage image, RGBImageFilter filter ) {
		if( filter == null ) return null;

		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage result = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
		for( int x = 0; x < w; x++ ) {
			for( int y = 0; y < h; y++ ) {
				result.setRGB( x, y, filter.filterRGB( x, y, image.getRGB( x, y ) ) );
			}
		}

		return result;
	}

	public static final Image scale( Image image, int width, int height ) {
		Image result = image;

		int w = image.getWidth( null );
		int h = image.getHeight( null );

		do {
			if( w > width ) {
				w /= 2;
				if( w < width ) w = width;
			} else {
				w *= 2;
				if( w > width ) w = width;
			}

			if( h > height ) {
				h /= 2;
				if( h < height ) h = height;
			} else {
				h *= 2;
				if( h > height ) h = height;
			}

			BufferedImage buffer = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
			Graphics2D graphics = buffer.createGraphics();
			graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
			graphics.drawImage( result, 0, 0, w, h, null );
			graphics.dispose();

			result = buffer;
		} while( w != width || h != height );

		return result;
	}

	public static final BufferedImage scale( BufferedImage image, int width, int height ) {
		return (BufferedImage)scale( (Image)image, width, height );
	}

}
