package com.parallelsymmetry.swing;

import java.awt.image.BufferedImage;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;

public class AbstractIconGenerator {

	protected static final void save( File target, String name, Icon icon ) {
		save( target, name, icon, icon.getIconWidth(), icon.getIconHeight() );
	}

	protected static final void save( File target, String name, Icon icon, int size ) {
		save( target, name, icon, size, size, null );
	}

	protected static final void save( File target, String name, Icon icon, int width, int height ) {
		save( target, name, icon, width, height, null );
	}

	protected static final void save( File target, String name, Icon icon, int width, int height, RGBImageFilter filter ) {
		if( !target.isDirectory() ) target = target.getParentFile();
		if( !target.exists() || target.mkdirs() ) {
			System.err.println( "Could not create target: " + target );
			return;
		}

		BufferedImage image = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
		icon.paintIcon( null, image.getGraphics(), 0, 0 );

		// Scale the icon.
		if( icon.getIconWidth() != width || icon.getIconHeight() != height ) image = Images.scale( image, width, height );

		// Filter the icon.
		filter( image, filter );

		try {
			File file = new File( target, name + ".png" );
			ImageIO.write( image, "png", file );
			System.out.println( "Image created: " + file.toString() );
		} catch( IOException exception ) {
			exception.printStackTrace();
		}
	}

	private static final void filter( BufferedImage image, RGBImageFilter filter ) {
		if( filter == null ) return;

		int w = image.getWidth();
		int h = image.getHeight();
		for( int x = 0; x < w; x++ ) {
			for( int y = 0; y < h; y++ ) {
				image.setRGB( x, y, filter.filterRGB( x, y, image.getRGB( x, y ) ) );
			}
		}
	}

}
