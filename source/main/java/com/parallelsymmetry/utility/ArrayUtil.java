package com.parallelsymmetry.utility;

import java.lang.reflect.Array;

public final class ArrayUtil {

	@SuppressWarnings( "unchecked" )
	public static <T> T[] combine( T[] a, T[] b ) {
		if( a == null && b == null ) return null;
		if( a == null ) return b;
		if( b == null ) return a;

		int size = a.length + b.length;
		T[] array = (T[])Array.newInstance( a.getClass().getComponentType(), size );
		System.arraycopy( a, 0, array, 0, a.length );
		System.arraycopy( b, 0, array, a.length, b.length );
		return array;
	}

	public static final <T> boolean containsEquivalent( T[] array, T element ) {
		for( T test : array ) {
			if( element == null && test == null ) return true;
			if( test != null && test.equals( element ) ) return true;
		}
		return false;
	}

	public static final String encodeIntArray( int[] array ) {
		if( array == null ) return null;

		StringBuilder builder = new StringBuilder();

		for( int index = 0; index < array.length; index++ ) {
			if( index > 0 ) builder.append( "," );
			builder.append( Integer.toString( array[index] ) );
		}

		return builder.toString();
	}

	public static final int[] decodeIntArray( String string ) {
		if( string == null ) return null;
		if( TextUtil.isEmpty( string ) ) return new int[0];

		String[] tokens = string.split( "," );

		int[] array = new int[tokens.length];
		for( int index = 0; index < array.length; index++ ) {
			array[index] = Integer.parseInt( tokens[index] );
		}

		return array;
	}

}
