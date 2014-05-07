package com.parallelsymmetry.utility.ui;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class FontUtil {

	public static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext( new AffineTransform(), true, true );

	public static final String DEFAULT_SANSSERIF_FONT_CODE = "SansSerif-PLAIN-10";

	public static final String DEFAULT_MONOSPACED_FONT_CODE = "Monospaced-PLAIN-8";

	public static final String DEFAULT_FONT_CODE = DEFAULT_SANSSERIF_FONT_CODE;

	private static final String SEPARATOR = "-";

	private static final float LARGE = 20f;

	private static final float SMALL = 10f;

	public static final Font decode( String string ) {
		return Font.decode( string );
	}

	public static final String encode( Font font ) {
		StringBuilder builder = new StringBuilder( encodeNameAndStyle( font ) );
		builder.append( SEPARATOR );
		builder.append( font.getSize() );
		return builder.toString();
	}

	public static final String encodeStyle( Font font ) {
		switch( font.getStyle() ) {
			case Font.ITALIC: {
				return "ITALIC";
			}
			case Font.BOLD: {
				return "BOLD";
			}
			case Font.BOLD | Font.ITALIC: {
				return "BOLDITALIC";
			}
		}
		return "PLAIN";
	}

	public static final String encodeNameAndStyle( Font font ) {
		StringBuilder builder = new StringBuilder( font.getFamily() );
		builder.append( SEPARATOR );
		builder.append( encodeStyle( font ) );
		return builder.toString();
	}

	public static final Rectangle2D getTextBounds( Font font, String text ) {
		return font.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();
	}

	public static final Font findFontByMaxHeight( Font font, double height ) {
		return font.deriveFont( findFontSizeByMaxHeight( font, height ) );
	}

	/**
	 * Find the font that fits the specified width.
	 */
	public static final Font findFontByTextWidth( Font font, String text, double width ) {
		return font.deriveFont( findFontSizeByTextWidth( font, text, width ) );
	}

	/**
	 * Find the font that fits the specified height.
	 */
	public static final Font findFontByTextHeight( Font font, String text, double height ) {
		return font.deriveFont( findFontSizeByTextHeight( font, text, height ) );
	}

	public static final Font findFontByTextBounds( Font font, String text, double width, double height ) {
		float w = findFontSizeByTextWidth( font, text, width );
		float h = findFontSizeByTextHeight( font, text, height );
		if( w < h ) {
			return findFontByTextWidth( font, text, width );
		} else {
			return findFontByTextHeight( font, text, height );
		}
	}

	private static final float findFontSizeByMaxHeight( Font font, double height ) {
		Font fontA = font.deriveFont( 200f );
		double a = fontA.getMaxCharBounds( FONT_RENDER_CONTEXT ).getHeight();

		Font fontB = font.deriveFont( 100f );
		double b = fontB.getMaxCharBounds( FONT_RENDER_CONTEXT ).getHeight();

		double m = 100 / ( a - b );

		return (float)( height * m );
	}

	private static final float findFontSizeByTextWidth( Font font, String text, double width ) {
		Font fontA = font.deriveFont( LARGE );
		GlyphVector glyphsA = fontA.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsA = glyphsA.getVisualBounds();
		double a = boundsA.getWidth();

		Font fontB = font.deriveFont( SMALL );
		GlyphVector glyphsB = fontB.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsB = glyphsB.getVisualBounds();
		double b = boundsB.getWidth();

		double m = ( LARGE - SMALL ) / ( a - b );

		return (float)( width * m );
	}

	private static final float findFontSizeByTextHeight( Font font, String text, double height ) {
		Font fontA = font.deriveFont( LARGE );
		GlyphVector glyphsA = fontA.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsA = glyphsA.getVisualBounds();
		double a = boundsA.getHeight();

		Font fontB = font.deriveFont( SMALL );
		GlyphVector glyphsB = fontB.createGlyphVector( FONT_RENDER_CONTEXT, text );
		Rectangle2D boundsB = glyphsB.getVisualBounds();
		double b = boundsB.getHeight();

		double m = ( LARGE - SMALL ) / ( a - b );

		return (float)( height * m );
	}

	@SuppressWarnings( "unused" )
	private static final float findFontSizeByTextWidthNewtonMethod( Font font, String text, double width ) {
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

	@SuppressWarnings( "unused" )
	private static final float findFontSizeByTextHeightNewtonMethod( Font font, String text, double height ) {
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

}
