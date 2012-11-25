package com.parallelsymmetry.utility.ui;

import java.awt.image.ImageFilter;
import java.io.File;

import javax.swing.Icon;

public abstract class AbstractIconGenerator {

	private ColorScheme scheme;

	public abstract void generate();

	protected void forceColorScheme( ColorScheme scheme ) {
		this.scheme = scheme;
	}

	protected void save( Icon icon, File target, String name ) {
		save( icon, target, name, icon.getIconWidth(), icon.getIconHeight() );
	}

	protected void save( Icon icon, File target, String name, int size ) {
		save( icon, target, name, size, size, null );
	}

	protected void save( Icon icon, File target, String name, int width, int height ) {
		save( icon, target, name, width, height, null );
	}

	protected void save( Icon icon, File target, String name, ImageFilter filter ) {
		save( icon, target, name, icon.getIconWidth(), icon.getIconHeight(), filter );
	}

	protected void save( Icon icon, File target, String name, int width, int height, ImageFilter filter ) {
		if( scheme != null && icon instanceof BaseIcon ) {
			( (BaseIcon)icon ).setColorScheme( scheme );
		}

		Icons.save( icon, target, name, width, height, filter );
	}

}
