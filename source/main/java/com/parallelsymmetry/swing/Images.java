package com.parallelsymmetry.swing;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Images {

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
