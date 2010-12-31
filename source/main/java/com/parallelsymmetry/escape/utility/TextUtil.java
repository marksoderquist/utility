package com.parallelsymmetry.escape.utility;

import java.util.Arrays;

public class TextUtil {

	public static final int LEFT = 1;

	public static final int CENTER = 2;

	public static final int RIGHT = 3;

	private static final char SPACE = ' ';

	public static final boolean isEmpty( String string ) {
		if( string == null ) return true;
		if( string.trim().length() == 0 ) return true;
		return false;
	}

	public static final boolean areEqual( String string1, String string2 ) {
		if( string1 == null && string2 == null ) return true;
		if( string1 == null && string2 != null ) return false;
		if( string1 != null && string2 == null ) return false;
		return string1.equals( string2 );
	}

	public static final boolean areSame( String string1, String string2 ) {
		if( isEmpty( string1 ) && isEmpty( string2 ) ) return true;
		if( string1 == null && string2 != null ) return false;
		if( string1 != null && string2 == null ) return false;
		return string1.equals( string2 );
	}

	public static final int compare( String string1, String string2 ) {
		if( string1 == null && string2 == null ) return 0;
		if( string1 == null && string2 != null ) return -1;
		if( string1 != null && string2 == null ) return 1;
		return string1.compareTo( string2 );
	}

	/**
	 * Concatenate multiple objects together using a fast string building object.
	 * 
	 * @param objects
	 * @return
	 */
	public static String concatenate( Object... objects ) {
		StringBuilder builder = new StringBuilder();

		for( Object object : objects ) {
			builder.append( object == null ? "null" : object.toString() );
		}

		return builder.toString();
	}

	/**
	 * Returns a printable string representation of a character by converting char
	 * values less than or equal to 32 or greater than or equal to 126 to the
	 * integer value surrounded by brackets.
	 * <p>
	 * Example: An escape char (27) would be returned as: [27]
	 * <p>
	 * Example: The letter A would be returned as: A
	 * 
	 * @param data The character to convert.
	 * @return A printable string representation of the character.
	 */
	public static String toPrintableString( char data ) {
		if( data >= 32 && data <= 126 ) {
			return String.valueOf( data );
		} else {
			return "[" + String.valueOf( (int)data ) + "]";
		}
	}

	public static String toPrintableString( byte[] data ) {
		if( data == null ) return null;
		StringBuilder builder = new StringBuilder();
		int count = data.length;
		for( int index = 0; index < count; index++ ) {
			builder.append( toPrintableString( (char)data[index] ) );
		}
		return builder.toString();
	}

	public static String toPrintableString( char[] data ) {
		if( data == null ) return null;
		StringBuilder builder = new StringBuilder();
		int count = data.length;
		for( int index = 0; index < count; index++ ) {
			builder.append( toPrintableString( data[index] ) );
		}
		return builder.toString();
	}

	public static String toPrintableString( String data ) {
		return toPrintableString( data.toCharArray() );
	}

	/**
	 * Convert an array of bytes to a HEX encoded string.
	 * 
	 * @param bytes The bytes to convert to hex.
	 * @return A hex encoded string of the byte array.
	 */
	public static final String toHexEncodedString( byte[] bytes ) {
		int value = 0;
		int count = bytes.length;
		String string = null;
		StringBuilder builder = new StringBuilder();
		for( int index = 0; index < count; index++ ) {
			value = bytes[index];
			string = Integer.toHexString( value < 0 ? value + 256 : value );
			if( string.length() == 1 ) builder.append( "0" );
			builder.append( string );
		}
		return builder.toString();
	}

	/**
	 * @param string
	 * @return
	 */
	public static final String toHexEncodedString( String string ) {
		int count = string.length();
		StringBuilder builder = new StringBuilder();
		for( int index = 0; index < count; index++ ) {
			builder.append( Integer.toHexString( string.charAt( index ) ) );
		}
		return builder.toString();
	}

	public static final boolean isInteger( String text ) {
		if( text == null ) return false;

		try {
			Integer.parseInt( text );
		} catch( NumberFormatException exception ) {
			return false;
		}
		return true;
	}

	public static final boolean isLong( String text ) {
		if( text == null ) return false;

		try {
			Long.parseLong( text );
		} catch( NumberFormatException exception ) {
			return false;
		}
		return true;
	}

	public static final boolean isFloat( String text ) {
		if( text == null ) return false;

		try {
			Float.parseFloat( text );
		} catch( NumberFormatException exception ) {
			return false;
		}
		return true;
	}

	public static final boolean isDouble( String text ) {
		if( text == null ) return false;

		try {
			Double.parseDouble( text );
		} catch( NumberFormatException exception ) {
			return false;
		}
		return true;
	}

	public static final String justify( int alignment, String text, int width ) {
		return justify( alignment, text, width, SPACE );
	}

	public static final String justify( int alignment, String text, int width, char chr ) {
		return justify( alignment, text, width, chr, 0 );
	}

	public static final String justify( int alignment, String text, int width, char chr, int pad ) {
		switch( alignment ) {
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
		char[] pad = new char[width];
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
		StringBuilder builder = new StringBuilder( width );
		builder.append( text );
		if( right <= pad ) {
			builder.append( pad( right ) );
		} else {
			builder.append( pad( pad ) );
			builder.append( pad( right - pad, chr ) );
		}
		return builder.toString();
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

		StringBuilder builder = new StringBuilder( width );
		if( left <= pad ) {
			builder.append( pad( left ) );
		} else {
			builder.append( pad( left - pad, chr ) );
			builder.append( pad( pad ) );
		}
		builder.append( text );
		if( right <= pad ) {
			builder.append( pad( right ) );
		} else {
			builder.append( pad( pad ) );
			builder.append( pad( right - pad, chr ) );
		}
		return builder.toString();
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
		StringBuilder builder = new StringBuilder( width );
		if( left <= pad ) {
			builder.append( pad( left ) );
		} else {
			builder.append( pad( left - pad, chr ) );
			builder.append( pad( pad ) );
		}
		builder.append( text );
		return builder.toString();
	}

	public static final int getLineCount( String text ) {
		if( text == null ) return 0;

		int count = 0;
		LineParser parser = new LineParser( text );
		while( parser.next() != null ) {
			count++;
		}

		return count;
	}

}
