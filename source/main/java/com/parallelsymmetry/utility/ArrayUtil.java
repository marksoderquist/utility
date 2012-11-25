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

}
