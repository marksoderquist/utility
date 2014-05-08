package com.parallelsymmetry.utility;

public class Primitives {

	public static final boolean parseBoolean( Object object ) {
		if( object == null ) return false;
		return Boolean.parseBoolean( object.toString() );
	}

	public static final int parseInt( Object object ) {
		return parseInt( object, 0 );
	}

	public static final int parseInt( Object object, int defaultValue ) {
		if( object == null ) return defaultValue;
		int result = defaultValue;
		try {
			result = Integer.parseInt( object.toString() );
		} catch( NumberFormatException exception ) {
			// Intentionally ignore exception.
		}
		return result;
	}

}
