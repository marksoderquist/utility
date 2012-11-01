package com.parallelsymmetry.escape.utility.ui;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class FontUtil {

	public static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext( new AffineTransform(), true, false );

	public static Rectangle2D getTextBounds( Font font, String text ) {
		return font.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();
	}

	public static Font findFontForAscent( Font font, String text, double ascent ) {
		return font.deriveFont( findFontSizeForAscent( font, text, ascent ) );
	}

	public static final Font findFontForBounds( Font font, String text, double width, double height ) {
		float w = findFontSizeForWidth( font, text, width );
		float h = findFontSizeForHeight( font, text, height );
		if( w < h ) {
			return findFontForWidth( font, text, width );
		} else {
			return findFontForHeight( font, text, height );
		}
	}

	/**
	 * Find the font that fits the specified height.
	 */
	public static Font findFontForHeight( Font font, String text, double height ) {
		return font.deriveFont( findFontSizeForHeight( font, text, height ) );
	}

	/**
	 * Find the font that fits the specified width.
	 */
	public static Font findFontForWidth( Font font, String text, double width ) {
		return font.deriveFont( findFontSizeForWidth( font, text, width ) );
	}

	private static final float findFontSizeForAscent( Font font, String text, double height ) {
		Font fontA = font.deriveFont( 200f );
		GlyphVector glyphsA = fontA.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsA = glyphsA.getVisualBounds();
		double a = -boundsA.getY();

		Font fontB = font.deriveFont( 100f );
		GlyphVector glyphsB = fontB.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsB = glyphsB.getVisualBounds();
		double b = -boundsB.getY();

		double m = 100 / ( a - b );

		return (float)( height * m );
	}

	private static final float findFontSizeForHeight( Font font, String text, double height ) {
		Font fontA = font.deriveFont( 200f );
		GlyphVector glyphsA = fontA.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsA = glyphsA.getVisualBounds();
		double a = boundsA.getHeight();

		Font fontB = font.deriveFont( 100f );
		GlyphVector glyphsB = fontB.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsB = glyphsB.getVisualBounds();
		double b = boundsB.getHeight();

		double m = 100 / ( a - b );

		return (float)( height * m );
	}

	private static final float findFontSizeForWidth( Font font, String text, double width ) {
		Font fontA = font.deriveFont( 200f );
		GlyphVector glyphsA = fontA.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsA = glyphsA.getVisualBounds();
		double a = boundsA.getWidth();

		Font fontB = font.deriveFont( 100f );
		GlyphVector glyphsB = fontB.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsB = glyphsB.getVisualBounds();
		double b = boundsB.getWidth();

		double m = 100 / ( a - b );

		return (float)( width * m );
	}

	@SuppressWarnings( "unused" )
	private static float findFontSizeForHeightNewtonMethod( Font font, String text, double height ) {
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

		return currentFont.getSize2D();
	}

	@SuppressWarnings( "unused" )
	private static final float findFontSizeForWidthNewtonMethod( Font font, String text, double width ) {
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

		return currentFont.getSize2D();
	}

}
