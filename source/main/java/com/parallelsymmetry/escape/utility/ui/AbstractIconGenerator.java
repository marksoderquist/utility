package com.parallelsymmetry.escape.utility.ui;

import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;

public abstract class AbstractIconGenerator {

	private ColorScheme scheme;
	
	public abstract void generate();

	protected void forceColorScheme( ColorScheme scheme ) {
		this.scheme = scheme;
	}

	protected void save( File target, String name, Icon icon ) {
		save( target, name, icon, icon.getIconWidth(), icon.getIconHeight() );
	}

	protected void save( File target, String name, Icon icon, int size ) {
		save( target, name, icon, size, size, null );
	}

	protected void save( File target, String name, Icon icon, int width, int height ) {
		save( target, name, icon, width, height, null );
	}

	protected void save( File target, String name, Icon icon, ImageFilter filter ) {
		save( target, name, icon, icon.getIconWidth(), icon.getIconHeight(), filter );
	}

	protected void save( File target, String name, Icon icon, int width, int height, ImageFilter filter ) {
		if( !target.isDirectory() ) target = target.getParentFile();
		if( !target.exists() && target.mkdirs() ) {
			System.err.println( "Could not create target: " + target );
			return;
		}

		if( scheme != null && icon instanceof BaseIcon ) {
			( (BaseIcon)icon ).setColorScheme( scheme );
		}

		BufferedImage image = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
		icon.paintIcon( null, image.getGraphics(), 0, 0 );

		// Scale the icon.
		if( icon.getIconWidth() != width || icon.getIconHeight() != height ) image = Images.scale( image, width, height );

		// Filter the icon.
		image = Images.filter( image, filter );

		try {
			File file = new File( target, name + ".png" );
			ImageIO.write( image, "png", file );
			System.out.println( "Image created: " + file.toString() );
		} catch( IOException exception ) {
			exception.printStackTrace();
		}
	}

}
