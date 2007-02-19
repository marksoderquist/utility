package org.novaworx.util;

import java.util.Arrays;

public class TextFormat {

	public static final int LEFT = 1;

	public static final int CENTER = 2;

	public static final int RIGHT = 3;

	private static final char SPACE = ' ';

	public static final String justify( int alignment, String text, int width ) {
		return justify( alignment, text, width, SPACE );
	}

	public static final String justify( int alignment, String text, int width, char chr ) {
		return justify( alignment, text, width, chr, 0 );
	}

	public static final String justify( int alignment, String text, int width, char chr, int pad ) {
		switch( alignment ) {
			case LEFT:
				return leftJustify( text, width, chr, pad );
			case CENTER:
				return centerJustify( text, width, chr, pad );
			case RIGHT:
				return rightJustify( text, width, chr, pad );
			default:
				return leftJustify( text, width, chr, pad );
		}
	}

	public static final String pad( int width ) {
		return pad( width, SPACE );
	}

	public static final String pad( int width, char chr ) {
		if( width <= 0 ) return "";
		char[] pad = new char[ width ];
		Arrays.fill( pad, chr );
		return new String( pad );
	}

	public static final String leftJustify( String text, int width ) {
		return leftJustify( text, width, SPACE );
	}

	public static final String leftJustify( String text, int width, char chr ) {
		return leftJustify( text, width, chr, 0 );
	}

	public static final String leftJustify( String text, int width, char chr, int pad ) {
		if( text == null ) return pad( width );
		if( text.length() > width ) return text.substring( 0, width );

		int right = width - text.length();
		StringBuffer buffer = new StringBuffer( width );
		buffer.append( text );
		if( right <= pad ) {
			buffer.append( pad( right ) );
		} else {
			buffer.append( pad( pad ) );
			buffer.append( pad( right - pad, chr ) );
		}
		return buffer.toString();
	}

	public static final String centerJustify( String text, int width ) {
		return centerJustify( text, width, SPACE );
	}

	public static final String centerJustify( String text, int width, char chr ) {
		return centerJustify( text, width, chr, 0 );
	}

	public static final String centerJustify( String text, int width, char chr, int pad ) {
		if( text == null ) return pad( width );
		if( text.length() > width ) return text.substring( 0, width );

		int left = ( width - text.length() ) / 2;
		int right = ( width - text.length() ) - left;

		StringBuffer buffer = new StringBuffer( width );
		if( left <= pad ) {
			buffer.append( pad( left ) );
		} else {
			buffer.append( pad( left - pad, chr ) );
			buffer.append( pad( pad ) );
		}
		buffer.append( text );
		if( right <= pad ) {
			buffer.append( pad( right ) );
		} else {
			buffer.append( pad( pad ) );
			buffer.append( pad( right - pad, chr ) );
		}
		return buffer.toString();
	}

	public static final String rightJustify( String text, int width ) {
		return rightJustify( text, width, SPACE );
	}

	public static final String rightJustify( String text, int width, char chr ) {
		return rightJustify( text, width, chr, 0 );
	}

	public static final String rightJustify( String text, int width, char chr, int pad ) {
		if( text == null ) return pad( width );
		if( text.length() > width ) return text.substring( 0, width );

		int left = width - text.length();
		StringBuffer buffer = new StringBuffer( width );
		if( left <= pad ) {
			buffer.append( pad( left ) );
		} else {
			buffer.append( pad( left - pad, chr ) );
			buffer.append( pad( pad ) );
		}
		buffer.append( text );
		return buffer.toString();
	}

}
