package com.parallelsymmetry.utility.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.RGBImageFilter;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public final class Images {

	public static final double DEFUALT_CUMULATIVE_TOLERANCE = 1d / 256d;

	public static final double DEFAULT_COMPONENT_TOLERANCE = 16d / 256d;

	/**
	 * Forces pixels to opaque or transparent.
	 */
	public static final RGBImageFilter BITMASK_FILTER = new BitmaskFilter();

	/**
	 * Standard grayscale filter.
	 */
	public static final RGBImageFilter GRAYSCALE_FILTER = new GrayscaleFilter( 0 );

	/**
	 * Brighter grayscale filter.
	 */
	public static final RGBImageFilter DISABLED_FILTER = new GrayscaleFilter( 0.5 );

	/**
	 * Slightly darker grayscale filter.
	 */
	public static final RGBImageFilter PRESSED_FILTER = new GrayscaleFilter( -0.125 );

	public static final RGBImageFilter ROLLOVER_FILTER = new RescaleFilter( 0x80808080 );

	public static void show( Image image ) {
		show( image, Color.WHITE );
	}

	public static void show( Image image, int borderSize ) {
		show( image, Color.WHITE, borderSize );
	}

	public static void show( Image image, Color borderColor ) {
		show( image, borderColor, 5 );
	}

	public static void show( Image image, Color borderColor, int borderSize ) {
		JPanel border = new JPanel();
		border.setBackground( borderColor );
		border.setBorder( new LineBorder( borderColor, borderSize ) );
		border.setLayout( new BorderLayout() );

		ImagePanel panel = new ImagePanel( image );
		border.add( panel, BorderLayout.CENTER );
		JOptionPane.showMessageDialog( null, border, null, JOptionPane.PLAIN_MESSAGE );
	}

	public static Image filter( Image image, ImageFilter filter ) {
		if( filter == null ) return image;
		if( image == null ) return null;
		return Toolkit.getDefaultToolkit().createImage( new FilteredImageSource( image.getSource(), filter ) );
	}

	public static BufferedImage filter( BufferedImage image, ImageFilter filter ) {
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

	public static BufferedImage scale( Image image, int width, int height ) {
		return scale( image, width, height, BufferedImage.TYPE_INT_ARGB );
	}

	public static BufferedImage scale( Image image, int width, int height, int type ) {
		Image result = image;
		BufferedImage buffer = null;

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

			buffer = new BufferedImage( w, h, type );
			Graphics2D graphics = buffer.createGraphics();
			graphics.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
			graphics.drawImage( result, 0, 0, w, h, null );
			graphics.dispose();

			result = buffer;
		} while( w != width || h != height );

		return buffer;
	}

	public static boolean areSimilar( Image a, Image b ) {
		return areSimilar( a, b, DEFUALT_CUMULATIVE_TOLERANCE, DEFAULT_COMPONENT_TOLERANCE );
	}

	public static boolean areSimilar( Image a, Image b, double cumulativeTolerance ) {
		return areSimilar( a, b, cumulativeTolerance, DEFAULT_COMPONENT_TOLERANCE );
	}

	public static boolean areSimilar( Image a, Image b, double cumulativeTolerance, double componentTolerance ) {
		byte[] s = Images.getArrayFromImage( a );
		byte[] t = Images.getArrayFromImage( b );

		if( s.length != t.length ) return false;

		int cumulative = 0;
		for( int index = 0; index < s.length; index++ ) {
			int error = Math.abs( s[index] - t[index] );
			if( error / 256f > componentTolerance ) return false;
			cumulative += error;
		}

		double variance = (double)cumulative / (double)( s.length << 8 );

		return variance <= cumulativeTolerance;
	}

	public static byte[] getArrayFromImage( Image image ) {
		int width = image.getWidth( null );
		int height = image.getHeight( null );

		int[] pixels = new int[width * height];
		PixelGrabber grabber = new PixelGrabber( image, 0, 0, width, height, pixels, 0, width );

		// Grab the pixel data.
		try {
			grabber.grabPixels();
		} catch( InterruptedException exception ) {
			return null;
		}

		return convertIntsToBytes( pixels );
	}

	public static Image getImageFromArray( int[] pixels, int width, int height ) {
		MemoryImageSource source = new MemoryImageSource( width, height, pixels, 0, width );
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		return toolkit.createImage( source );
	}

	private static byte[] convertIntsToBytes( int[] ints ) {
		int length = ints.length;
		byte[] bytes = new byte[length * 4];
		for( int index = 0; index < length; index++ ) {
			int value = ints[index];
			bytes[index * 4 + 0] = (byte)( value >> 0 );
			bytes[index * 4 + 1] = (byte)( value >> 8 );
			bytes[index * 4 + 2] = (byte)( value >> 16 );
			bytes[index * 4 + 3] = (byte)( value >> 24 );
		}
		return bytes;
	}

}
