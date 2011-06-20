package com.parallelsymmetry.escape.utility.ui;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class FontUtil {

	private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext( new AffineTransform(), true, false );

	public static Rectangle2D getTextBounds( Font font, String text ) {
		return font.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();
	}

	/**
	 * Find the font that fits the specified width.
	 */
	public static Font findFontForWidth( Font font, String text, double width ) {
		Font currentFont = font.deriveFont( 1f );
		Rectangle2D textBounds = currentFont.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();

		double error = ( textBounds.getWidth() - width ) / width;
		double precision = textBounds.getWidth() / width;

		while( Math.abs( error ) > precision ) {
			double rescale = Math.pow( 2, -error );
			currentFont = currentFont.deriveFont( (float)( currentFont.getSize2D() * rescale ) );
			textBounds = currentFont.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();
			error = ( textBounds.getWidth() - width ) / width;
		}

		return currentFont;
	}

	/**
	 * Find the font that fits the specified height.
	 */
	public static Font findFontForHeight( Font font, String text, double height ) {
		Font currentFont = font.deriveFont( 1f );
		Rectangle2D textBounds = currentFont.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();

		double error = ( textBounds.getHeight() - height ) / height;
		double precision = textBounds.getHeight() / height;

		while( Math.abs( error ) > precision ) {
			double rescale = Math.pow( 2, -error );
			currentFont = currentFont.deriveFont( (float)( currentFont.getSize2D() * rescale ) );
			textBounds = currentFont.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();
			error = ( textBounds.getHeight() - height ) / height;
		}

		return currentFont;
	}

}
