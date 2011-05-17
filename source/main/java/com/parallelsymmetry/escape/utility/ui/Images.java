package com.parallelsymmetry.escape.utility.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class Images {

	public static final RGBImageFilter NO_ALPHA_FILTER = new NoAlphaFilter();

	public static final RGBImageFilter STANDARD_FILTER = new GrayscaleFilter( 0, 0 );

	public static final RGBImageFilter DISABLED_FILTER = new GrayscaleFilter( 0.5 );

	public static final RGBImageFilter PRESSED_FILTER = new GrayscaleFilter( -0.125 );

	public static final RGBImageFilter ROLLOVER_FILTER = new MaskscaleFilter( 0x80808080 );

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

	public static final Image filter( Image image, ImageFilter filter ) {
		if( filter == null ) return image;
		return Toolkit.getDefaultToolkit().createImage( new FilteredImageSource( image.getSource(), filter ) );
	}

	public static final BufferedImage filter( BufferedImage image, ImageFilter filter ) {
		if( image == null ) return null;
		if( filter == null ) return image;

		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage result = new BufferedImage( w, h, image.getType() );
		Graphics2D graphics = result.createGraphics();
		graphics.drawImage( filter( (Image)image, filter ), 0, 0, null );
		graphics.dispose();

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
